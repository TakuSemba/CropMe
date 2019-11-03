package com.takusemba.cropme.internal

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import android.view.View.TRANSLATION_X
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

/**
 * HorizontalAnimatorImpl is responsible for animating [CropImageView] horizontally.
 */
internal class HorizontalAnimatorImpl(
    target: View,
    private val leftBound: Int,
    private val rightBound: Int,
    private val maxScale: Int
) : MoveAnimator {

  private val spring: SpringAnimation
  private val fling: FlingAnimation
  private val animator: ObjectAnimator

  private val updateListener = DynamicAnimation.OnAnimationUpdateListener { dynamicAnimation, value, velocity ->
    reMoveIfNeeded(velocity)
  }
  private val endListener = DynamicAnimation.OnAnimationEndListener { dynamicAnimation, b, v, v1 -> isFlinging = false }

  private var isFlinging = false

  init {

    spring = SpringAnimation(target,
        object : FloatPropertyCompat<View>("X") {
          override fun getValue(view: View): Float {
            return view.x
          }

          override fun setValue(view: View, value: Float) {
            view.x = value
          }
        })
        .setSpring(SpringForce()
            .setStiffness(MoveAnimator.STIFFNESS)
            .setDampingRatio(MoveAnimator.DAMPING_RATIO)
        )

    fling = FlingAnimation(target, DynamicAnimation.X).setFriction(MoveAnimator.FRICTION)

    animator = ObjectAnimator()
    animator.setProperty(TRANSLATION_X)
    animator.target = target
  }

  override fun move(delta: Float) {
    val target = animator.target as View
    if (target != null) {
      cancel()
      animator.interpolator = null
      animator.duration = 0
      animator.setFloatValues(target.translationX + delta)
      animator.start()
    }
  }

  override fun reMoveIfNeeded(velocity: Float) {
    val target = animator.target as View
    if (target != null) {
      val targetRect = Rect()
      target.getHitRect(targetRect)

      val scale: Float
      val afterRect: Rect
      if (maxScale < target.scaleX) {
        scale = maxScale.toFloat()
        val heightDiff = ((targetRect.height() - targetRect.height() * (maxScale / target.scaleY)) / 2).toInt()
        val widthDiff = ((targetRect.width() - targetRect.width() * (maxScale / target.scaleY)) / 2).toInt()
        afterRect = Rect(targetRect.left + widthDiff, targetRect.top + heightDiff,
            targetRect.right - widthDiff, targetRect.bottom - heightDiff)
      } else if (target.scaleX < 1) {
        scale = 1f
        val heightDiff = (target.height - targetRect.height()) / 2
        val widthDiff = (target.width - targetRect.width()) / 2
        afterRect = Rect(targetRect.left + widthDiff, targetRect.top + heightDiff,
            targetRect.right - widthDiff, targetRect.bottom - heightDiff)
      } else {
        scale = target.scaleX
        afterRect = targetRect
      }
      val horizontalDiff = (target.width * scale - target.width) / 2

      if (leftBound < afterRect.left) {
        cancel()
        spring.setStartVelocity(velocity).animateToFinalPosition(
            leftBound + horizontalDiff)
      } else if (afterRect.right < rightBound) {
        cancel()
        spring.setStartVelocity(velocity).animateToFinalPosition(
            rightBound - target.width.toFloat() - horizontalDiff)
      }
    }
  }

  override fun fling(velocity: Float) {
    isFlinging = true
    cancel()
    fling.addUpdateListener(updateListener)
    fling.addEndListener(endListener)
    fling.setStartVelocity(velocity).start()
  }

  override fun isNotFlinging(): Boolean {
    return !isFlinging
  }

  private fun cancel() {
    animator.cancel()
    spring.cancel()
    fling.cancel()
    fling.removeUpdateListener(updateListener)
    fling.removeEndListener(endListener)
    isFlinging = false
  }
}
