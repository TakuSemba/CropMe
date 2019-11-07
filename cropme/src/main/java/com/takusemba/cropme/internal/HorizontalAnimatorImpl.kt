package com.takusemba.cropme.internal

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import android.view.View.TRANSLATION_X
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
 * HorizontalAnimatorImpl is responsible for animating [target] horizontally.
 */
internal class HorizontalAnimatorImpl(
    private val target: View,
    private val leftBound: Float,
    private val rightBound: Float,
    private val maxScale: Float
) : MoveAnimator {

  private val spring = SpringAnimation(target, VERTICAL_FLOAT_PROPERTY).setSpring(SPRING_FORCE)

  private val fling = FlingAnimation(target, DynamicAnimation.X).setFriction(FRICTION)

  private val animator: ObjectAnimator

  private val updateListener = OnAnimationUpdateListener { dynamicAnimation, value, velocity ->
    adjust(velocity)
  }

  init {
    animator = ObjectAnimator()
    animator.setProperty(TRANSLATION_X)
    animator.target = target
  }

  override fun move(delta: Float) {
    cancel()
    animator.interpolator = null
    animator.duration = 0
    animator.setFloatValues(target.translationX + delta)
    animator.start()
  }

  override fun adjust(velocity: Float) {
    val targetRect = Rect()
    target.getHitRect(targetRect)

    val scale: Float
    val afterRect: Rect
    when {
      maxScale < target.scaleX -> {
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
      target.scaleX < 1 -> {
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
        scale = target.scaleX
        afterRect = targetRect
      }
    }
    val horizontalDiff = (target.width * scale - target.width) / 2

    if (leftBound < afterRect.left) {
      cancel()
      spring.setStartVelocity(velocity)
          .animateToFinalPosition(leftBound + horizontalDiff)
    } else if (afterRect.right < rightBound) {
      cancel()
      spring.setStartVelocity(velocity)
          .animateToFinalPosition(rightBound - target.width.toFloat() - horizontalDiff)
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

    private val VERTICAL_FLOAT_PROPERTY = object : FloatPropertyCompat<View>("X") {
      override fun getValue(view: View): Float {
        return view.x
      }

      override fun setValue(view: View, value: Float) {
        view.x = value
      }
    }

    private val SPRING_FORCE = SpringForce().setStiffness(STIFFNESS).setDampingRatio(DAMPING_RATIO)
  }
}
