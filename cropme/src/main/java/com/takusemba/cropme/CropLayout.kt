package com.takusemba.cropme

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.IntDef
import androidx.annotation.MainThread
import com.takusemba.cropme.internal.GestureAnimation
import com.takusemba.cropme.internal.GestureAnimator
import kotlin.concurrent.thread

class CropLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private val cropImageView: CropImageView
  private val cropOverlay: CropOverlay

  private val percentWidth: Float
  private val percentHeight: Float

  private var frameCache: RectF? = null

  init {
    val a = context.obtainStyledAttributes(attrs, R.styleable.CropLayout, 0, 0)

    val scale: Int

    try {
      // Propagate attrs as cropImageAttrs so that CropImageView's custom attributes are transferred,
      // but standard attributes (e.g. background) are not.
      // Inspired from https://github.com/google/ExoPlayer/blob/r2.10.6/library/ui/src/main/java/com/google/android/exoplayer2/ui/PlayerView.java#L464
      val defaultCropImageView = CropImageView(context, null, 0)
      defaultCropImageView.id = R.id.cropme_image_view
      defaultCropImageView.scaleType = ImageView.ScaleType.FIT_XY
      defaultCropImageView.adjustViewBounds = true
      defaultCropImageView.layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER)
      addView(defaultCropImageView, 0)
      cropImageView = defaultCropImageView

      val customCropOverlay = findViewById<CropOverlay>(R.id.cropme_overlay)
      if (customCropOverlay != null) {
        cropOverlay = customCropOverlay
      } else {
        @OverlayShape val overlayShape = a.getInt(
            R.styleable.CropLayout_cropme_overlay_shape,
            DEFAULT_OVERLAY_SHAPE
        )
        // Propagate attrs as cropOverlayAttrs so that CropOverlay's custom attributes are transferred,
        // but standard attributes (e.g. background) are not.
        // Inspired from https://github.com/google/ExoPlayer/blob/r2.10.6/library/ui/src/main/java/com/google/android/exoplayer2/ui/PlayerView.java#L464
        val defaultCropOverlay: CropOverlay = when (overlayShape) {
          OVERLAY_SHAPE_NONE -> NoneCropOverlay(context, null, 0, attrs)
          OVERLAY_SHAPE_RECTANGLE -> RectangleCropOverlay(context, null, 0, attrs)
          OVERLAY_SHAPE_CIRCLE -> CircleCropOverlay(context, null, 0, attrs)
          else -> RectangleCropOverlay(context, null, 0, attrs)
        }
        defaultCropOverlay.id = R.id.cropme_overlay
        defaultCropOverlay.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER)
        addView(defaultCropOverlay, 1)
        cropOverlay = defaultCropOverlay
      }

      scale = a.getInt(R.styleable.CropLayout_cropme_max_scale, DEFAULT_MAX_SCALE)

      percentWidth = a.getFraction(
          R.styleable.CropLayout_cropme_percent_width,
          DEFAULT_BASE,
          DEFAULT_PBASE,
          DEFAULT_PERCENT_WIDTH
      )
      percentHeight = a.getFraction(
          R.styleable.CropLayout_cropme_percent_height,
          DEFAULT_BASE,
          DEFAULT_PBASE,
          DEFAULT_PERCENT_HEIGHT
      )
    } finally {
      a.recycle()
    }

    val vto = viewTreeObserver
    vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {

      override fun onPreDraw(): Boolean {

        val totalWidth = measuredWidth.toFloat()
        val totalHeight = measuredHeight.toFloat()
        val frameWidth = measuredWidth * percentWidth
        val frameHeight = measuredHeight * percentHeight
        val frame = RectF(
            (totalWidth - frameWidth) / 2f,
            (totalHeight - frameHeight) / 2f,
            (totalWidth + frameWidth) / 2f,
            (totalHeight + frameHeight) / 2f
        )

        cropImageView.setFrame(frame)
        cropOverlay.setFrame(frame)
        frameCache = frame

        val animator = GestureAnimator.of(cropImageView, frame, scale)
        val animation = GestureAnimation(cropOverlay, animator)
        animation.start()
        when {
          vto.isAlive -> vto.removeOnPreDrawListener(this)
          else -> cropOverlay.viewTreeObserver.removeOnPreDrawListener(this)
        }
        return true
      }
    })
  }

  fun setUri(uri: Uri) {
    cropImageView.setImageURI(uri)
    cropImageView.requestLayout()
  }

  fun setBitmap(bitmap: Bitmap) {
    cropImageView.setImageBitmap(bitmap)
    cropImageView.requestLayout()
  }

  fun isOffFrame(): Boolean {
    val frameRect = frameCache ?: return false
    val targetRect = Rect()
    cropImageView.getHitRect(targetRect)
    return !frameRect.contains(
        targetRect.left.toFloat(),
        targetRect.top.toFloat(),
        targetRect.right.toFloat(),
        targetRect.bottom.toFloat()
    )
  }

  @MainThread
  fun crop(listener: OnCropListener) {
    val frame = frameCache ?: return
    val targetRect = Rect()
    cropImageView.getHitRect(targetRect)

    val sourceBitmap = (cropImageView.drawable as BitmapDrawable).bitmap
    thread {
      val bitmap = Bitmap.createScaledBitmap(sourceBitmap, targetRect.width(), targetRect.height(),
          false)
      var leftOffset = (frame.left - targetRect.left).toInt()
      var topOffset = (frame.top - targetRect.top).toInt()
      val rightOffset = (targetRect.right - frame.right).toInt()
      val bottomOffset = (targetRect.bottom - frame.bottom).toInt()
      var width = frame.width().toInt()
      var height = frame.height().toInt()

      if (leftOffset < 0) {
        width += leftOffset
        leftOffset = 0
      }
      if (topOffset < 0) {
        height += topOffset
        topOffset = 0
      }
      if (rightOffset < 0) {
        width += rightOffset
      }
      if (bottomOffset < 0) {
        height += bottomOffset
      }
      if (width < 0 || height < 0) {
        throw IllegalStateException("width or heigt is less than 0")
      }

      val result = Bitmap.createBitmap(bitmap, leftOffset, topOffset, width, height)

      (context as Activity).runOnUiThread {
        if (result != null) {
          listener.onSuccess(result)
        } else {
          listener.onFailure()
        }
      }
    }
  }

  companion object {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(OVERLAY_SHAPE_NONE, OVERLAY_SHAPE_RECTANGLE, OVERLAY_SHAPE_CIRCLE)
    annotation class OverlayShape

    private const val OVERLAY_SHAPE_NONE = 0
    private const val OVERLAY_SHAPE_RECTANGLE = 1
    private const val OVERLAY_SHAPE_CIRCLE = 2

    private const val DEFAULT_OVERLAY_SHAPE = OVERLAY_SHAPE_RECTANGLE
    private const val DEFAULT_MAX_SCALE = 2

    private const val DEFAULT_BASE = 1
    private const val DEFAULT_PBASE = 1

    private const val DEFAULT_PERCENT_WIDTH = 0.8f
    private const val DEFAULT_PERCENT_HEIGHT = 0.8f
  }
}
