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

  private val backgroundPaint = Paint()
  private val cropPaint = Paint()
  private val borderPaint = Paint()

  protected val percentWidth: Float
  protected val percentHeight: Float
  protected val backgroundAlpha: Int
  protected val withBorder: Boolean

  private var frameCache: RectF? = null

  init {
    if (cropOverlayAttrs != null) {
      val a = context.obtainStyledAttributes(cropOverlayAttrs, R.styleable.CropOverlay, 0, 0)
      try {
        percentWidth = a.getFraction(
            R.styleable.CropOverlay_cropme_percent_width,
            DEFAULT_BASE,
            DEFAULT_PBASE,
            DEFAULT_PERCENT_WIDTH
        )
        percentHeight = a.getFraction(
            R.styleable.CropOverlay_cropme_percent_height,
            DEFAULT_BASE,
            DEFAULT_PBASE,
            DEFAULT_PERCENT_WIDTH
        )
        val backgroundAlphaFraction = a.getFraction(
            R.styleable.CropLayout_cropme_background_alpha,
            DEFAULT_BASE,
            DEFAULT_PBASE,
            DEFAULT_BACKGROUND_ALPHA
        ) * COLOR_DENSITY
        backgroundAlpha = (backgroundAlphaFraction * COLOR_DENSITY).toInt()
        withBorder = a.getBoolean(
            R.styleable.CropOverlay_cropme_with_border,
            DEFAULT_WITH_BORDER
        )
      } finally {
        a.recycle()
      }
    } else {
      percentWidth = DEFAULT_PERCENT_WIDTH
      percentHeight = DEFAULT_PERCENT_HEIGHT
      backgroundAlpha = (DEFAULT_BACKGROUND_ALPHA * COLOR_DENSITY).toInt()
      withBorder = DEFAULT_WITH_BORDER
    }
    setWillNotDraw(false)
    setLayerType(View.LAYER_TYPE_HARDWARE, null)

    backgroundPaint.color = ContextCompat.getColor(context, android.R.color.black)
    backgroundPaint.alpha = backgroundAlpha

    cropPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    borderPaint.strokeWidth = BORDER_WIDTH.toFloat()
    borderPaint.color = ContextCompat.getColor(context, R.color.light_white)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawBackground(canvas, backgroundPaint)
    drawCrop(canvas, cropPaint)
    if (withBorder) {
      drawBorder(canvas, borderPaint)
    }
  }

  open fun drawBackground(canvas: Canvas, paint: Paint) {
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
  }

  abstract fun drawCrop(canvas: Canvas, paint: Paint)

  open fun drawBorder(canvas: Canvas, paint: Paint) {
    val frameWidth = measuredWidth * percentWidth
    val frameHeight = measuredHeight * percentHeight
    val borderHeight = frameHeight / 3

    val left = (width - frameWidth) / 2f
    val top = (height - frameHeight) / 2f
    val right = (width + frameWidth) / 2f
    val bottom = (height + frameHeight) / 2f

    canvas.drawLine(left, top, right, top, borderPaint)
    canvas.drawLine(left, top + borderHeight, right, top + borderHeight, borderPaint)
    canvas.drawLine(left, top + borderHeight * 2, right, top + borderHeight * 2, borderPaint)
    canvas.drawLine(left, bottom, right, bottom, borderPaint)

    val borderWidth = frameWidth / 3
    canvas.drawLine(left, top, left, bottom, borderPaint)
    canvas.drawLine(left + borderWidth, top, left + borderWidth, bottom, borderPaint)
    canvas.drawLine(left + borderWidth * 2, top, left + borderWidth * 2, bottom, borderPaint)
    canvas.drawLine(right, top, right, bottom, borderPaint)
  }

  fun getFrame(): RectF {
    return if (frameCache != null) {
      checkNotNull(frameCache)
    } else {
      if (measuredWidth != 0 && measuredHeight != 0) {
        val totalWidth = measuredWidth.toFloat()
        val totalHeight = measuredHeight.toFloat()
        val frameWidth = measuredWidth * percentWidth
        val frameHeight = measuredHeight * percentHeight
        RectF(
            (totalWidth - frameWidth) / 2f,
            (totalHeight - frameHeight) / 2f,
            (totalWidth + frameWidth) / 2f,
            (totalHeight + frameHeight) / 2f
        ).also { calculatedRect ->
          frameCache = calculatedRect
        }
      } else {
        EMPTY_FRAME
      }
    }
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

    private const val DEFAULT_PERCENT_WIDTH = 0.8f
    private const val DEFAULT_PERCENT_HEIGHT = 0.8f

    private const val DEFAULT_BACKGROUND_ALPHA = 0.8f
    private const val COLOR_DENSITY = 255f

    private const val DEFAULT_WITH_BORDER = true

    private val EMPTY_FRAME = RectF(0f, 0f, 0f, 0f)
  }
}