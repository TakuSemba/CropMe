package com.takusemba.cropme.internal

import android.animation.ObjectAnimator
import android.view.View
import android.view.View.SCALE_X
import android.view.View.SCALE_Y
import android.view.animation.DecelerateInterpolator

/**
 * ScaleAnimatorImpl is responsible for scaling [CropImageView].
 */
internal class ScaleAnimatorImpl(target: View, private val maxScale: Float) : ScaleAnimator {

  private val animatorX: ObjectAnimator
  private val animatorY: ObjectAnimator

  init {

    this.animatorX = ObjectAnimator()
    animatorX.setProperty(SCALE_X)
    animatorX.target = target

    this.animatorY = ObjectAnimator()
    animatorY.setProperty(SCALE_Y)
    animatorY.target = target
  }

  override fun scale(scale: Float) {
    val targetX = animatorX.target as View
    if (targetX != null) {
      animatorX.cancel()
      animatorX.duration = 0
      animatorX.interpolator = null
      animatorX.setFloatValues(targetX.scaleX * scale)
      animatorX.start()
    }

    val targetY = animatorY.target as View
    if (targetY != null) {
      animatorY.cancel()
      animatorY.duration = 0
      animatorY.interpolator = null
      animatorY.setFloatValues(targetY.scaleY * scale)
      animatorY.start()
    }
  }

  override fun reScaleIfNeeded() {
    val targetX = animatorX.target as View
    if (targetX != null) {
      if (targetX.scaleX < 1) {
        animatorX.cancel()
        animatorX.duration = DURATION.toLong()
        animatorX.setFloatValues(1f)
        animatorX.interpolator = DecelerateInterpolator(
            FACTOR.toFloat())
        animatorX.start()
      } else if (maxScale < targetX.scaleX) {
        animatorX.cancel()
        animatorX.duration = DURATION.toLong()
        animatorX.setFloatValues(maxScale)
        animatorX.interpolator = DecelerateInterpolator(
            FACTOR.toFloat())
        animatorX.start()
      }
    }

    val targetY = animatorY.target as View
    if (targetY != null) {
      if (targetY.scaleY < 1) {
        animatorY.cancel()
        animatorY.duration = DURATION.toLong()
        animatorY.setFloatValues(1f)
        animatorY.interpolator = DecelerateInterpolator(
            FACTOR.toFloat())
        animatorY.start()
      } else if (maxScale < targetY.scaleY) {
        animatorY.cancel()
        animatorY.duration = DURATION.toLong()
        animatorY.setFloatValues(maxScale)
        animatorY.interpolator = DecelerateInterpolator(
            FACTOR.toFloat())
        animatorY.start()
      }
    }
  }

  companion object {

    private val DURATION = 600
    private val FACTOR = 2
  }
}
