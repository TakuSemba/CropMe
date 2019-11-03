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
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.MainThread
import com.takusemba.cropme.internal.ActionDetector
import com.takusemba.cropme.internal.ActionListener
import com.takusemba.cropme.internal.HorizontalMoveAnimatorImpl
import com.takusemba.cropme.internal.MoveAnimator
import com.takusemba.cropme.internal.ScaleAnimator
import com.takusemba.cropme.internal.ScaleAnimatorImpl
import com.takusemba.cropme.internal.VerticalMoveAnimatorImpl

/**
 * CropLayout is a parent layout that has [CropOverlay].
 * Image can be set thorough this class. see [Croppable]
 */
class CropLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Croppable {

  private var horizontalAnimator: MoveAnimator? = null
  private var verticalAnimator: MoveAnimator? = null
  private var scaleAnimator: ScaleAnimator? = null

  private val maxScale: Int
  private var frame: RectF? = null

  init {
    val a = getContext().obtainStyledAttributes(attrs, R.styleable.CropLayout)

    maxScale = a.getInt(R.styleable.CropLayout_cropme_max_scale, DEFAULT_MAX_SCALE)
    if (maxScale < MIN_SCALE || MAX_SCALE < maxScale) {
      throw IllegalArgumentException("cropme_max_scale must be set from 1 to 5")
    }

    a.recycle()

    init()
  }

  private fun init() {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
      override fun onPreDraw(): Boolean {
        val overlayView = findViewById<CropOverlay>(R.id.cropme_overlay)
        frame = overlayView.frame
        val cropImageView = CropImageView(context)
        cropImageView.setFrame(frame!!)
        val layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER)
        cropImageView.scaleType = ImageView.ScaleType.FIT_XY
        cropImageView.adjustViewBounds = true
        cropImageView.layoutParams = layoutParams
        cropImageView.id = R.id.cropme_image_view
        addView(cropImageView, 0)

        horizontalAnimator = HorizontalMoveAnimatorImpl(cropImageView, frame!!, maxScale)
        verticalAnimator = VerticalMoveAnimatorImpl(cropImageView, frame!!, maxScale)
        scaleAnimator = ScaleAnimatorImpl(cropImageView, maxScale.toFloat())

        startActionDetector()

        viewTreeObserver.removeOnPreDrawListener(this)
        return true
      }
    })
  }

  private fun startActionDetector() {
    val actionDetector = ActionDetector(this, object : ActionListener {

      override fun onScaled(scale: Float) {
        scaleAnimator!!.scale(scale)
      }

      override fun onScaleEnded() {
        scaleAnimator!!.reScaleIfNeeded()
      }

      override fun onMoved(dx: Float, dy: Float) {
        horizontalAnimator!!.move(dx)
        verticalAnimator!!.move(dy)
      }

      override fun onFlinged(velocityX: Float, velocityY: Float) {
        horizontalAnimator!!.fling(velocityX)
        verticalAnimator!!.fling(velocityY)
      }

      override fun onMoveEnded() {
        if (horizontalAnimator!!.isNotFlinging()) {
          horizontalAnimator!!.reMoveIfNeeded(0f)
        }

        if (verticalAnimator!!.isNotFlinging()) {
          verticalAnimator!!.reMoveIfNeeded(0f)
        }
      }
    })
    actionDetector.start()
  }

  override fun setUri(uri: Uri) {
    val image = findViewById<ImageView>(R.id.cropme_image_view)
    image.setImageURI(uri)
    image.requestLayout()
  }

  override fun setBitmap(bitmap: Bitmap) {
    val image = findViewById<ImageView>(R.id.cropme_image_view)
    image.setImageBitmap(bitmap)
    image.requestLayout()
  }

  override fun isOffOfFrame(): Boolean {
    val imageView = findViewById<ImageView>(R.id.cropme_image_view)
    val targetRect = Rect()
    imageView.getHitRect(targetRect)
    return !targetRect.contains(frame!!.left.toInt(), frame!!.top.toInt(), frame!!.right.toInt(),
        frame!!.bottom.toInt())
  }

  @MainThread
  override fun crop(listener: OnCropListener) {
    val imageView = findViewById<ImageView>(R.id.cropme_image_view)
    val targetRect = Rect()
    imageView.getHitRect(targetRect)
    val sourceBitmap = (imageView.drawable as BitmapDrawable).bitmap
    Thread(Runnable {
      val bitmap = Bitmap.createScaledBitmap(sourceBitmap, targetRect.width(), targetRect.height(),
          false)
      var leftOffset = frame!!.left.toInt() - targetRect.left
      var topOffset = frame!!.top.toInt() - targetRect.top
      val rightOffset = (targetRect.right - frame!!.right).toInt()
      val bottomOffset = (targetRect.bottom - frame!!.bottom).toInt()
      var width = frame!!.width().toInt()
      var height = frame!!.height().toInt()

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
    }).start()
  }

  companion object {

    private const val DEFAULT_MAX_SCALE = 2
    private const val MIN_SCALE = 1
    private const val MAX_SCALE = 5
  }
}
