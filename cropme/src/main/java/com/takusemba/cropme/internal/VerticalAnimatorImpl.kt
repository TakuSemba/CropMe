package com.takusemba.cropme.internal

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import android.view.View.TRANSLATION_Y
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

/**
 * VerticalAnimatorImpl is responsible for animating [CropImageView] vertically.
 */
internal class VerticalAnimatorImpl(
    target: View,
    private val topBound: Int,
    private val bottomBound: Int,
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

  override fun isNotFlinging(): Boolean {
    return !isFlinging
  }

  init {

    spring = SpringAnimation(target,
        object : FloatPropertyCompat<View>("Y") {
          override fun getValue(view: View): Float {
            return view.y
          }

          override fun setValue(view: View, value: Float) {
            view.y = value
          }
        })
        .setSpring(SpringForce()
            .setStiffness(MoveAnimator.STIFFNESS)
            .setDampingRatio(MoveAnimator.DAMPING_RATIO)
        )

    fling = FlingAnimation(target, DynamicAnimation.Y).setFriction(
        MoveAnimator.FRICTION)

    animator = ObjectAnimator()
    animator.setProperty(TRANSLATION_Y)
    animator.target = target
  }

  override fun move(delta: Float) {
    val target = animator.target as View
    if (target != null) {
      cancel()
      animator.interpolator = null
      animator.duration = 0
      animator.setFloatValues(target.translationY + delta)
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
      if (maxScale < target.scaleY) {
        scale = maxScale.toFloat()
        val heightDiff = ((targetRect.height() - targetRect.height() * (maxScale / target.scaleY)) / 2).toInt()
        val widthDiff = ((targetRect.width() - targetRect.width() * (maxScale / target.scaleY)) / 2).toInt()
        afterRect = Rect(targetRect.left + widthDiff, targetRect.top + heightDiff,
            targetRect.right - widthDiff, targetRect.bottom - heightDiff)
      } else if (target.scaleY < 1) {
        scale = 1f
        val heightDiff = (target.height - targetRect.height()) / 2
        val widthDiff = (target.width - targetRect.width()) / 2
        afterRect = Rect(targetRect.left + widthDiff, targetRect.top + heightDiff,
            targetRect.right - widthDiff, targetRect.bottom - heightDiff)
      } else {
        scale = target.scaleY
        afterRect = targetRect
      }
      val verticalDiff = (target.height * scale - target.height) / 2

      if (topBound < afterRect.top) {
        cancel()
        spring.setStartVelocity(velocity).animateToFinalPosition(topBound + verticalDiff)
      } else if (afterRect.bottom < bottomBound) {
        cancel()
        spring.setStartVelocity(velocity).animateToFinalPosition(
            bottomBound - target.height.toFloat() - verticalDiff)
      }
    }
  }

  override fun fling(velocity: Float) {
    cancel()
    isFlinging = true
    fling.addUpdateListener(updateListener)
    fling.addEndListener(endListener)
    fling.setStartVelocity(velocity).start()
  }

  private fun cancel() {
    isFlinging = false
    animator.cancel()
    spring.cancel()
    fling.cancel()
    fling.removeUpdateListener(updateListener)
    fling.removeEndListener(endListener)
  }
}
