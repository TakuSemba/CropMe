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
 * VerticalAnimatorImpl is responsible for animating [target] vertically.
 */
internal class VerticalAnimatorImpl(
    private val target: View,
    private val topBound: Float,
    private val bottomBound: Float,
    private val maxScale: Float
) : MoveAnimator {

  private val spring = SpringAnimation(target, HORIZONTAL_FLOAT_PROPERTY).setSpring(SPRING_FORCE)

  private val fling = FlingAnimation(target, DynamicAnimation.Y).setFriction(FRICTION)

  private val animator: ObjectAnimator

  private val updateListener = OnAnimationUpdateListener { _, _, velocity -> adjust(velocity) }

  init {
    animator = ObjectAnimator()
    animator.setProperty(TRANSLATION_Y)
    animator.target = target
  }

  override fun move(delta: Float) {
    cancel()
    animator.interpolator = null
    animator.duration = 0
    animator.setFloatValues(target.translationY + delta)
    animator.start()
  }

  override fun adjust(velocity: Float) {
    val targetRect = Rect()
    target.getHitRect(targetRect)

    val scale: Float
    val afterRect: Rect
    when {
      maxScale < target.scaleY -> {
        scale = maxScale
        val heightDiff = ((targetRect.height() - targetRect.height() * (maxScale / target.scaleY)) / 2).toInt()
        val widthDiff = ((targetRect.width() - targetRect.width() * (maxScale / target.scaleY)) / 2).toInt()
        afterRect = Rect(
            targetRect.left + widthDiff,
            targetRect.top + heightDiff,
            targetRect.right - widthDiff,
            targetRect.bottom - heightDiff
        )
      }
      target.scaleY < 1 -> {
        scale = 1f
        val heightDiff = (target.height - targetRect.height()) / 2
        val widthDiff = (target.width - targetRect.width()) / 2
        afterRect = Rect(
            targetRect.left + widthDiff,
            targetRect.top + heightDiff,
            targetRect.right - widthDiff,
            targetRect.bottom - heightDiff
        )
      }
      else -> {
        scale = target.scaleY
        afterRect = targetRect
      }
    }
    val verticalDiff = (target.height * scale - target.height) / 2

    if (topBound < afterRect.top) {
      cancel()
      spring.setStartVelocity(velocity)
          .animateToFinalPosition(topBound + verticalDiff)
    } else if (afterRect.bottom < bottomBound) {
      cancel()
      spring.setStartVelocity(velocity)
          .animateToFinalPosition(bottomBound - target.height.toFloat() - verticalDiff)
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

    private val HORIZONTAL_FLOAT_PROPERTY = object : FloatPropertyCompat<View>("Y") {
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
