package com.takusemba.cropme

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet

class RectangleCropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropOverlayAttrs: AttributeSet? = attrs
) : CropOverlay(context, attrs, defStyleAttr, cropOverlayAttrs) {

  override fun drawCrop(canvas: Canvas, paint: Paint) {
    val frameWidth = measuredWidth * percentWidth
    val frameHeight = measuredHeight * percentHeight

    val left = (width - frameWidth) / 2f
    val top = (height - frameHeight) / 2f
    val right = (width + frameWidth) / 2f
    val bottom = (height + frameHeight) / 2f

    canvas.drawRect(left, top, right, bottom, paint)
  }
}