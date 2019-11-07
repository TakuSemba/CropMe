package com.takusemba.cropme.internal

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import android.view.View.TRANSLATION_Y
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.takusemba.cropme.internal.MoveAnimator.Companion.DAMPING_RATIO
import com.takusemba.cropme.internal.MoveAnimator.Companion.FRICTION
import com.takusemba.cropme.internal.MoveAnimator.Companion.STIFFNESS

/**
 * VerticalAnimatorImpl is responsible for animating [targetView] vertically.
 */
internal class VerticalAnimatorImpl(
    private val targetView: View,
    private val topBound: Float,
    private val bottomBound: Float,
    private val maxScale: Float
) : MoveAnimator {

  private val spring = SpringAnimation(targetView, HORIZONTAL_PROPERTY).setSpring(SPRING_FORCE)

  private val fling = FlingAnimation(targetView, DynamicAnimation.Y).setFriction(FRICTION)

  private val animator: ObjectAnimator = ObjectAnimator().apply {
    setProperty(TRANSLATION_Y)
    target = targetView
    interpolator = null
    duration = 0
  }

  private val updateListener = OnAnimationUpdateListener { _, _, velocity -> adjust(velocity) }

  override fun move(delta: Float) {
    cancel()
    animator.setFloatValues(targetView.translationY + delta)
    animator.start()
  }

  override fun adjust(velocity: Float) {
    val targetRect = Rect()
    targetView.getHitRect(targetRect)

    val scale: Float
    val afterRect: Rect
    when {
      maxScale < targetView.scaleY -> {
        scale = maxScale
        val heightDiff = ((targetRect.height() - targetRect.height() * (maxScale / targetView.scaleY)) / 2).toInt()
        val widthDiff = ((targetRect.width() - targetRect.width() * (maxScale / targetView.scaleY)) / 2).toInt()
        afterRect = Rect(
            targetRect.left + widthDiff,
            targetRect.top + heightDiff,
            targetRect.right - widthDiff,
            targetRect.bottom - heightDiff
        )
      }
      targetView.scaleY < 1 -> {
        scale = 1f
        val heightDiff = (targetView.height - targetRect.height()) / 2
        val widthDiff = (targetView.width - targetRect.width()) / 2
        afterRect = Rect(
            targetRect.left + widthDiff,
            targetRect.top + heightDiff,
            targetRect.right - widthDiff,
            targetRect.bottom - heightDiff
        )
      }
      else -> {
        scale = targetView.scaleY
        afterRect = targetRect
      }
    }
    val verticalDiff = (targetView.height * scale - targetView.height) / 2

    if (topBound < afterRect.top) {
      cancel()
      spring.setStartVelocity(velocity)
          .animateToFinalPosition(topBound + verticalDiff)
    } else if (afterRect.bottom < bottomBound) {
      cancel()
      spring.setStartVelocity(velocity)
          .animateToFinalPosition(bottomBound - targetView.height.toFloat() - verticalDiff)
    }
  }

  override fun fling(velocity: Float) {
    cancel()
    fling.addUpdateListener(updateListener)
    fling.setStartVelocity(velocity).start()
  }

  private fun cancel() {
    animator.cancel()
    spring.cancel()
    fling.cancel()
    fling.removeUpdateListener(updateListener)
  }

  companion object {

    private val HORIZONTAL_PROPERTY = object : FloatPropertyCompat<View>("Y") {
      override fun getValue(view: View): Float {
        return view.y
      }

      override fun setValue(view: View, value: Float) {
        view.y = value
      }
    }

    private val SPRING_FORCE = SpringForce().setStiffness(STIFFNESS).setDampingRatio(DAMPING_RATIO)
  }
}
