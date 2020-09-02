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

/**
 * Overlay to show a frame with some decorations like borders.
 *
 * You have to extend [CropOverlay] to create your own custom overlay.
 * You have to override [drawCrop], and optionally [drawBackground], [drawBorder]
 */
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
        )
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
      style = Paint.Style.STROKE
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

  abstract fun drawBorder(canvas: Canvas, paint: Paint)

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