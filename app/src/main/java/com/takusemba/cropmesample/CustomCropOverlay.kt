package com.takusemba.cropmesample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import com.takusemba.cropme.CropOverlay

/**
 * Custom overlay which has a rounded rectangle frame.
 *
 * To create a custom overlay, you need to extend [CropOverlay] class and override [CropOverlay.drawCrop].
 * You can optionally override [CropOverlay.drawBackground], [CropOverlay.drawBorder] too.
 */
class CustomCropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropOverlayAttrs: AttributeSet? = attrs
) : CropOverlay(context, attrs, defStyleAttr, cropOverlayAttrs) {

  override fun drawCrop(canvas: Canvas, paint: Paint) {
    val frameRect = frame ?: return
    val frameWidth = frameRect.width()
    val frameHeight = frameRect.height()

    val left = (width - frameWidth) / 2f
    val top = (height - frameHeight) / 2f
    val right = (width + frameWidth) / 2f
    val bottom = (height + frameHeight) / 2f

    if (VERSION_CODES.LOLLIPOP < SDK_INT) {
      canvas.drawRoundRect(left, top, right, bottom, ROUND, ROUND, paint)
    } else {
      canvas.drawRect(left, top, right, bottom, paint)
    }
  }

  override fun drawBorder(canvas: Canvas, paint: Paint) {
    val frameRect = frame ?: return
    val frameWidth = frameRect.width()
    val frameHeight = frameRect.height()

    val left = (width - frameWidth) / 2f
    val top = (height - frameHeight) / 2f
    val right = (width + frameWidth) / 2f
    val bottom = (height + frameHeight) / 2f

    if (VERSION_CODES.LOLLIPOP < SDK_INT) {
      val borderHeight = frameHeight / 3
      canvas.drawLine(left, top + borderHeight, right, top + borderHeight, paint)
      canvas.drawLine(left, top + borderHeight * 2, right, top + borderHeight * 2, paint)

      val borderWidth = frameWidth / 3
      canvas.drawLine(left + borderWidth, top, left + borderWidth, bottom, paint)
      canvas.drawLine(left + borderWidth * 2, top, left + borderWidth * 2, bottom, paint)

      canvas.drawRoundRect(left, top, right, bottom, ROUND, ROUND, paint)
    } else {
      val borderHeight = frameHeight / 3
      canvas.drawLine(left, top + borderHeight, right, top + borderHeight, paint)
      canvas.drawLine(left, top + borderHeight * 2, right, top + borderHeight * 2, paint)

      val borderWidth = frameWidth / 3
      canvas.drawLine(left + borderWidth, top, left + borderWidth, bottom, paint)
      canvas.drawLine(left + borderWidth * 2, top, left + borderWidth * 2, bottom, paint)

      canvas.drawRect(left, top, right, bottom, paint)
    }
  }

  companion object {

    private const val ROUND: Float = 25f
  }
}