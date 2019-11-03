package com.takusemba.cropme

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.IntDef

class CropLayout2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private val cropImageView: CropImageView2
  private val cropOverlay: CropOverlay2

  init {
    val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CropLayout2, 0, 0)
    try {
      // CropImageView
      val customCropImageView = findViewById<CropImageView2>(R.id.cropme_image_view)
      if (customCropImageView != null) {
        cropImageView = customCropImageView
      } else {
        // Propagate attrs as cropImageAttrs so that CropImageView's custom attributes are transferred,
        // but standard attributes (e.g. background) are not.
        // Inspired from https://github.com/google/ExoPlayer/blob/r2.10.6/library/ui/src/main/java/com/google/android/exoplayer2/ui/PlayerView.java#L464
        val defaultCropImageView = CropImageView2(context, null, 0, attrs)
        defaultCropImageView.id = R.id.cropme_image_view
        defaultCropImageView.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER)
        // TODO fix index of child
        addView(defaultCropImageView, 0)
        cropImageView = defaultCropImageView
      }

      // CropOverlayView
      val customCropOverlay = findViewById<CropOverlay2>(R.id.cropme_overlay)
      if (customCropOverlay != null) {
        cropOverlay = customCropOverlay
      } else {
        @OverlayShape val overlayShape = a.getInt(
            R.styleable.CropLayout2_cropme_overlay_shape,
            DEFAULT_OVERLAY_SHAPE
        )
        // Propagate attrs as cropOverlayAttrs so that CropOverlay's custom attributes are transferred,
        // but standard attributes (e.g. background) are not.
        // Inspired from https://github.com/google/ExoPlayer/blob/r2.10.6/library/ui/src/main/java/com/google/android/exoplayer2/ui/PlayerView.java#L464
        val defaultCropOverlay: CropOverlay2 = when (overlayShape) {
          OVERLAY_SHAPE_NONE -> NoneCropOverlay(context, null, 0, attrs)
          OVERLAY_SHAPE_RECTANGLE -> RectangleCropOverlay2(context, null, 0, attrs)
          OVERLAY_SHAPE_CIRCLE -> CircleCropOverlay(context, null, 0, attrs)
          else -> RectangleCropOverlay2(context, null, 0, attrs)
        }
        defaultCropOverlay.id = R.id.cropme_overlay
        defaultCropOverlay.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER)
        // TODO fix index of child
        addView(defaultCropOverlay, 1)
        cropOverlay = defaultCropOverlay
      }
    } finally {
      a.recycle()
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
  }
}
