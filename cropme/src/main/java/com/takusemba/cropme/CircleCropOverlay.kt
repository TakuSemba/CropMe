package com.takusemba.cropme

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet

class CircleCropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropOverlayAttrs: AttributeSet? = attrs
) : CropOverlay(context, attrs, defStyleAttr, cropOverlayAttrs) {

  override fun drawCrop(canvas: Canvas, paint: Paint) {
    val frameRect = frame ?: return
    val x = measuredWidth / 2f
    val y = measuredHeight / 2f

    val frameWidth = frameRect.width()
    val frameHeight = frameRect.height()
    val radius = maxOf(frameWidth, frameHeight) / 2f

    canvas.drawCircle(x, y, radius, paint)
  }
}