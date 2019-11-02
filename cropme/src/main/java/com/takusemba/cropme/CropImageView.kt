package com.takusemba.cropme

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatImageView

/**
 * ImageView to hold an Image to be animated.
 */
internal class CropImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

  private var frame: RectF? = null

  /**
   * set image size along with [CropImageView.frame]
   */
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
}
