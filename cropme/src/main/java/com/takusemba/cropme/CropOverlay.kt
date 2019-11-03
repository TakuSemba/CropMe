package com.takusemba.cropme

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat

abstract class CropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropOverlayAttrs: AttributeSet? = attrs
) : FrameLayout(context, attrs, defStyleAttr) {

  private val backgroundPaint: Paint
  private val cropPaint: Paint
  private val borderPaint: Paint?

  protected var frame: RectF? = null
    private set

  init {
    val backgroundAlpha: Float
    val withBorder: Boolean

    if (cropOverlayAttrs != null) {
      val a = context.obtainStyledAttributes(cropOverlayAttrs, R.styleable.CropOverlay, 0, 0)
      try {
        backgroundAlpha = a.getFraction(
            R.styleable.CropLayout_cropme_background_alpha,
            DEFAULT_BASE,
            DEFAULT_PBASE,
            DEFAULT_BACKGROUND_ALPHA
        ) * COLOR_DENSITY
        withBorder = a.getBoolean(
            R.styleable.CropOverlay_cropme_with_border,
            DEFAULT_WITH_BORDER
        )
      } finally {
        a.recycle()
      }
    } else {
      backgroundAlpha = DEFAULT_BACKGROUND_ALPHA
      withBorder = DEFAULT_WITH_BORDER
    }
    setWillNotDraw(false)
    setLayerType(View.LAYER_TYPE_HARDWARE, null)

    backgroundPaint = Paint().apply {
      color = ContextCompat.getColor(context, android.R.color.black)
      alpha = (backgroundAlpha * COLOR_DENSITY).toInt()
    }

    cropPaint = Paint().apply {
      xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    borderPaint = if (withBorder) Paint().apply {
      strokeWidth = BORDER_WIDTH.toFloat()
      color = ContextCompat.getColor(context, R.color.light_white)
    } else null
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawBackground(canvas, backgroundPaint)
    drawCrop(canvas, cropPaint)
    if (borderPaint != null) {
      drawBorder(canvas, borderPaint)
    }
  }

  open fun drawBackground(canvas: Canvas, paint: Paint) {
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
  }

  abstract fun drawCrop(canvas: Canvas, paint: Paint)

  open fun drawBorder(canvas: Canvas, paint: Paint) {
    val frameRect = frame ?: return
    val frameWidth = frameRect.width()
    val frameHeight = frameRect.height()
    val borderHeight = frameHeight / 3

    val left = (width - frameWidth) / 2f
    val top = (height - frameHeight) / 2f
    val right = (width + frameWidth) / 2f
    val bottom = (height + frameHeight) / 2f

    canvas.drawLine(left, top, right, top, paint)
    canvas.drawLine(left, top + borderHeight, right, top + borderHeight, paint)
    canvas.drawLine(left, top + borderHeight * 2, right, top + borderHeight * 2, paint)
    canvas.drawLine(left, bottom, right, bottom, paint)

    val borderWidth = frameWidth / 3
    canvas.drawLine(left, top, left, bottom, paint)
    canvas.drawLine(left + borderWidth, top, left + borderWidth, bottom, paint)
    canvas.drawLine(left + borderWidth * 2, top, left + borderWidth * 2, bottom, paint)
    canvas.drawLine(right, top, right, bottom, paint)
  }

  fun setFrame(frame: RectF) {
    this.frame = frame
  }

  final override fun setWillNotDraw(willNotDraw: Boolean) {
    super.setWillNotDraw(willNotDraw)
  }

  final override fun setLayerType(layerType: Int, paint: Paint?) {
    super.setLayerType(layerType, paint)
  }

  companion object {

    private const val BORDER_WIDTH = 5

    private const val DEFAULT_BASE = 1
    private const val DEFAULT_PBASE = 1

    private const val DEFAULT_BACKGROUND_ALPHA = 0.8f
    private const val COLOR_DENSITY = 255f

    private const val DEFAULT_WITH_BORDER = true
  }
}