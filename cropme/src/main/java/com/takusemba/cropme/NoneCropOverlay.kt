package com.takusemba.cropme

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class NoneCropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropOverlayAttrs: AttributeSet? = attrs
) : CropOverlay(context, attrs, defStyleAttr, cropOverlayAttrs) {

  override fun drawBackground(canvas: Canvas) = Unit

  override fun drawCrop(canvas: Canvas) = Unit

  override fun drawBorder(canvas: Canvas) = Unit
}