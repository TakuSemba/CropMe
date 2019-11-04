package com.takusemba.cropme.internal

import android.animation.ObjectAnimator
import android.view.View
import android.view.View.SCALE_X
import android.view.View.SCALE_Y
import android.view.animation.DecelerateInterpolator

/**
 * ScaleAnimatorImpl is responsible for scaling [target].
 */
internal class ScaleAnimatorImpl(
    private val target: View,
    private val maxScale: Int
) : ScaleAnimator {

  private val animatorX: ObjectAnimator = ObjectAnimator()
  private val animatorY: ObjectAnimator = ObjectAnimator()

  init {
    animatorX.setProperty(SCALE_X)
    animatorX.target = target

    animatorY.setProperty(SCALE_Y)
    animatorY.target = target
  }

  override fun scale(scale: Float) {
    animatorX.cancel()
    animatorX.duration = 0
    animatorX.interpolator = null
    animatorX.setFloatValues(target.scaleX * scale)
    animatorX.start()

    animatorY.cancel()
    animatorY.duration = 0
    animatorY.interpolator = null
    animatorY.setFloatValues(target.scaleY * scale)
    animatorY.start()
  }

  override fun reScaleIfNeeded() {
    if (target.scaleX < 1) {
      animatorX.cancel()
      animatorX.duration = DURATION.toLong()
      animatorX.setFloatValues(1f)
      animatorX.interpolator = DecelerateInterpolator(FACTOR.toFloat())
      animatorX.start()
    } else if (maxScale < target.scaleX) {
      animatorX.cancel()
      animatorX.duration = DURATION.toLong()
      animatorX.setFloatValues(maxScale.toFloat())
      animatorX.interpolator = DecelerateInterpolator(FACTOR.toFloat())
      animatorX.start()
    }

    if (target.scaleY < 1) {
      animatorY.cancel()
      animatorY.duration = DURATION.toLong()
      animatorY.setFloatValues(1f)
      animatorY.interpolator = DecelerateInterpolator(FACTOR.toFloat())
      animatorY.start()
    } else if (maxScale < target.scaleY) {
      animatorY.cancel()
      animatorY.duration = DURATION.toLong()
      animatorY.setFloatValues(maxScale.toFloat())
      animatorY.interpolator = DecelerateInterpolator(FACTOR.toFloat())
      animatorY.start()
    }
  }

  companion object {

    private const val DURATION = 600
    private const val FACTOR = 2
  }
}
