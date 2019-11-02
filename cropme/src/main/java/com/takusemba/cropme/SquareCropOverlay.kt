package com.takusemba.cropme

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class SquareCropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CropOverlay(context, attrs, defStyleAttr) {

  private val background = Paint()
  private val border = Paint()
  private val cropPaint = Paint()

  private val backgroundAlpha: Int
  private val withBorder: Boolean
  private val percentWidth: Float
  private val percentHeight: Float

  override val frame: RectF
    get() {
      val totalWidth = measuredWidth.toFloat()
      val totalHeight = measuredHeight.toFloat()
      val frameWidth = measuredWidth * percentWidth
      val frameHeight = measuredHeight * percentHeight
      return RectF((totalWidth - frameWidth) / 2f, (totalHeight - frameHeight) / 2f,
          (totalWidth + frameWidth) / 2f, (totalHeight + frameHeight) / 2f)
    }

  init {

    val a = getContext().obtainStyledAttributes(attrs, R.styleable.SquareCropOverlay)

    percentWidth = a.getFraction(R.styleable.SquareCropOverlay_cropme_result_width, DEFAULT_BASE,
        DEFAULT_PBASE, DEFAULT_PERCENT_WIDTH)
    if (percentWidth < MIN_PERCENT || MAX_PERCENT < percentWidth) {
      throw IllegalArgumentException("cropme_result_width must be set from 0% to 100%")
    }

    percentHeight = a.getFraction(R.styleable.SquareCropOverlay_cropme_result_height, DEFAULT_BASE,
        DEFAULT_PBASE, DEFAULT_PERCENT_HEIGHT)
    if (percentHeight < MIN_PERCENT || MAX_PERCENT < percentHeight) {
      throw IllegalArgumentException("cropme_result_width must be set from 0% to 100%")
    }

    backgroundAlpha = (a.getFraction(R.styleable.SquareCropOverlay_cropme_background_alpha,
        DEFAULT_BASE, DEFAULT_PBASE, DEFAULT_BACKGROUND_ALPHA) * COLOR_DENSITY).toInt()
    if (percentWidth < MIN_PERCENT || MAX_PERCENT < percentWidth) {
      throw IllegalArgumentException("cropme_background_alpha must be set between 0% to 100%")
    }

    withBorder = a.getBoolean(R.styleable.SquareCropOverlay_cropme_with_border, DEFAULT_WITH_BORDER)

    a.recycle()

    init()
  }

  private fun init() {
    setWillNotDraw(false)
    setLayerType(LAYER_TYPE_HARDWARE, null)
    cropPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    border.strokeWidth = BORDER_WIDTH.toFloat()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    background.color = ContextCompat.getColor(context, android.R.color.black)
    background.alpha = backgroundAlpha
    border.color = ContextCompat.getColor(context, R.color.light_white)

    val frameWidth = measuredWidth * percentWidth
    val frameHeight = measuredHeight * percentHeight
    val left = (width - frameWidth) / 2f
    val top = (height - frameHeight) / 2f
    val right = (width + frameWidth) / 2f
    val bottom = (height + frameHeight) / 2f

    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), background)
    canvas.drawRect(left, top, right, bottom, cropPaint)

    if (withBorder) {
      val borderHeight = frameHeight / 3
      canvas.drawLine(left, top, right, top, border)
      canvas.drawLine(left, top + borderHeight, right, top + borderHeight, border)
      canvas.drawLine(left, top + borderHeight * 2, right, top + borderHeight * 2, border)
      canvas.drawLine(left, bottom, right, bottom, border)

      val borderWidth = frameWidth / 3
      canvas.drawLine(left, top, left, bottom, border)
      canvas.drawLine(left + borderWidth, top, left + borderWidth, bottom, border)
      canvas.drawLine(left + borderWidth * 2, top, left + borderWidth * 2, bottom, border)
      canvas.drawLine(right, top, right, bottom, border)
    }
  }

  companion object {

    private const val BORDER_WIDTH = 5

    private const val DEFAULT_BASE = 1
    private const val DEFAULT_PBASE = 1

    private const val MIN_PERCENT = 0
    private const val MAX_PERCENT = 1

    private const val DEFAULT_PERCENT_WIDTH = 0.8f
    private const val DEFAULT_PERCENT_HEIGHT = 0.8f

    private const val DEFAULT_BACKGROUND_ALPHA = 0.8f
    private const val COLOR_DENSITY = 255f

    private const val DEFAULT_WITH_BORDER = true
  }
}
