package com.takusemba.cropme

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropImageAttrs: AttributeSet? = attrs
) : AppCompatImageView(context, attrs, defStyleAttr) {

  private var frame: RectF? = null

  private val percentWidth: Float
  private val percentHeight: Float

  init {
    if (cropImageAttrs != null) {
      val a = context.obtainStyledAttributes(cropImageAttrs, R.styleable.CropImageView, 0, 0)
      try {
        percentWidth = a.getFraction(
            R.styleable.CropImageView_cropme_percent_width,
            DEFAULT_BASE,
            DEFAULT_PBASE,
            DEFAULT_PERCENT_WIDTH
        )
        percentHeight = a.getFraction(
            R.styleable.CropImageView_cropme_percent_height,
            DEFAULT_BASE,
            DEFAULT_PBASE,
            DEFAULT_PERCENT_HEIGHT
        )
      } finally {
        a.recycle()
      }
    } else {
      percentWidth = DEFAULT_PERCENT_WIDTH
      percentHeight = DEFAULT_PERCENT_HEIGHT
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val height = measuredHeight.toFloat()
    val width = measuredWidth.toFloat()
    val widthScale = if (frame != null) frame!!.width() / width else 1f
    val heightScale = if (frame != null) frame!!.height() / height else 1f
    val scale = maxOf(widthScale, heightScale)
    setMeasuredDimension((width * scale).toInt(), (height * scale).toInt())
  }

  fun setFrame(frame: RectF) {
    this.frame = frame
  }

  companion object {
    private const val DEFAULT_BASE = 1
    private const val DEFAULT_PBASE = 1

    private const val DEFAULT_PERCENT_WIDTH = 0.8f
    private const val DEFAULT_PERCENT_HEIGHT = 0.8f
  }
}
