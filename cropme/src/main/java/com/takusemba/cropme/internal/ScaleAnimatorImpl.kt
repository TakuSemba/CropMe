package com.takusemba.cropme.internal

import android.animation.ObjectAnimator
import android.view.View
import android.view.View.SCALE_X
import android.view.View.SCALE_Y
import android.view.animation.DecelerateInterpolator
import androidx.annotation.VisibleForTesting
import com.takusemba.cropme.internal.ScaleAnimator.Companion.ADJUSTING_DURATION
import com.takusemba.cropme.internal.ScaleAnimator.Companion.ADJUSTING_FACTOR
import com.takusemba.cropme.internal.ScaleAnimator.Companion.ORIGINAL_SCALE

/**
 * ScaleAnimatorImpl is responsible for scaling [targetView].
 */
internal class ScaleAnimatorImpl @VisibleForTesting constructor(
    private val targetView: View,
    private val maxScale: Float,
    private val animatorX: ObjectAnimator,
    private val animatorY: ObjectAnimator
) : ScaleAnimator {

  constructor(
      targetView: View,
      maxScale: Float
  ) : this(
      targetView = targetView,
      maxScale = maxScale,
      animatorX = ANIMATOR_X,
      animatorY = ANIMATOR_Y
  )

  init {
    animatorX.target = targetView
    animatorY.target = targetView
  }

  override fun scale(scale: Float) {
    animatorX.cancel()
    animatorX.clearProperties()
    animatorX.setFloatValues(targetView.scaleX * scale)
    animatorX.start()

    animatorY.cancel()
    animatorY.clearProperties()
    animatorY.setFloatValues(targetView.scaleY * scale)
    animatorY.start()
  }

  override fun adjust() {
    if (targetView.scaleX < ORIGINAL_SCALE) {
      animatorX.cancel()
      animatorX.setupProperties()
      animatorX.setFloatValues(ORIGINAL_SCALE)
      animatorX.start()
    } else if (maxScale < targetView.scaleX) {
      animatorX.cancel()
      animatorX.setupProperties()
      animatorX.setFloatValues(maxScale)
      animatorX.start()
    }

    if (targetView.scaleY < ORIGINAL_SCALE) {
      animatorY.cancel()
      animatorY.setupProperties()
      animatorY.setFloatValues(ORIGINAL_SCALE)
      animatorY.start()
    } else if (maxScale < targetView.scaleY) {
      animatorY.cancel()
      animatorY.setupProperties()
      animatorY.setFloatValues(maxScale)
      animatorY.start()
    }
  }

  private fun ObjectAnimator.clearProperties() {
    duration = 0
    interpolator = null
  }

  private fun ObjectAnimator.setupProperties() {
    duration = ADJUSTING_DURATION
    interpolator = DecelerateInterpolator(ADJUSTING_FACTOR)
  }

  companion object {

    private val ANIMATOR_X = ObjectAnimator().apply {
      setProperty(SCALE_X)
    }

    private val ANIMATOR_Y = ObjectAnimator().apply {
      setProperty(SCALE_Y)
    }
  }
}
