package com.takusemba.cropme

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class CropImageView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropImageAttrs: AttributeSet? = attrs
) : FrameLayout(context, attrs, defStyleAttr) {

  private val percentWidth: Float
  private val percentHeight: Float

  init {
    if (cropImageAttrs != null) {
      val a = context.theme.obtainStyledAttributes(cropImageAttrs, R.styleable.CropImageView2, 0, 0)
      try {
        percentWidth = a.getFraction(
            R.styleable.CropImageView2_cropme_percent_width,
            DEFAULT_BASE,
            DEFAULT_PBASE,
            DEFAULT_PERCENT_WIDTH
        )
        percentHeight = a.getFraction(
            R.styleable.CropImageView2_cropme_percent_height,
            DEFAULT_BASE,
            DEFAULT_PBASE,
            DEFAULT_PERCENT_WIDTH
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
    setMeasuredDimension((width * percentWidth).toInt(), (height * percentHeight).toInt())
  }

  companion object {
    private const val DEFAULT_BASE = 1
    private const val DEFAULT_PBASE = 1

    private const val DEFAULT_PERCENT_WIDTH = 0.8f
    private const val DEFAULT_PERCENT_HEIGHT = 0.8f
  }
}
