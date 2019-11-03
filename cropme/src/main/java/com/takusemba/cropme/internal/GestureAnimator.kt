package com.takusemba.cropme.internal

import android.graphics.RectF
import android.view.View

internal class GestureAnimator(
    private val horizontalAnimator: MoveAnimator,
    private val verticalAnimator: MoveAnimator,
    private val scaleAnimator: ScaleAnimator
) : ActionListener {

  override fun onScaled(scale: Float) {
    scaleAnimator.scale(scale)
  }

  override fun onScaleEnded() {
    scaleAnimator.reScaleIfNeeded()
  }

  override fun onMoved(dx: Float, dy: Float) {
    horizontalAnimator.move(dx)
    verticalAnimator.move(dy)
  }

  override fun onFlinged(velocityX: Float, velocityY: Float) {
    horizontalAnimator.fling(velocityX)
    verticalAnimator.fling(velocityY)
  }

  override fun onMoveEnded() {
    if (horizontalAnimator.isNotFlinging()) {
      horizontalAnimator.reMoveIfNeeded(0f)
    }
    if (verticalAnimator.isNotFlinging()) {
      verticalAnimator.reMoveIfNeeded(0f)
    }
  }

  companion object {

    fun of(target: View, frame: RectF, scale: Int): GestureAnimator {
      val horizontalAnimator = HorizontalAnimatorImpl(
          target = target,
          leftBound = frame.left.toInt(),
          rightBound = frame.right.toInt(),
          maxScale = scale
      )
      val verticalAnimator = VerticalAnimatorImpl(
          target = target,
          topBound = frame.top.toInt(),
          bottomBound = frame.bottom.toInt(),
          maxScale = scale
      )
      val scaleAnimator = ScaleAnimatorImpl(
          target = target,
          maxScale = scale
      )
      return GestureAnimator(horizontalAnimator, verticalAnimator, scaleAnimator)
    }
  }
}