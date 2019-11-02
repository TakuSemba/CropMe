package com.takusemba.cropme

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * CropOverlay is laid on [CropImageView].
 * You can extend this view to show your custom shaped overlay.
 */
abstract class CropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  /**
   * This RectF is used to restrict smooth animations,
   * and cropped image also will be cropped in this shape.
   */
  internal abstract val frame: RectF
}