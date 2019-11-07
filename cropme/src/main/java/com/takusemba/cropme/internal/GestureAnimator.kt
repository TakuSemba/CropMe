package com.takusemba.cropme.internal

import android.graphics.RectF
import android.view.View

/**
 * Animator to move a view horizontally and vertically, and scale a view.
 */
internal class GestureAnimator(
    private val horizontalAnimator: MoveAnimator,
    private val verticalAnimator: MoveAnimator,
    private val scaleAnimator: ScaleAnimator
) : ActionListener {

  override fun onScaled(scale: Float) {
    scaleAnimator.scale(scale)
  }

  override fun onScaleEnded() {
    scaleAnimator.adjust()
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
    horizontalAnimator.adjust()
    verticalAnimator.adjust()
  }

  companion object {

    fun of(target: View, frame: RectF, scale: Float): GestureAnimator {
      val horizontalAnimator = HorizontalAnimatorImpl(
          targetView = target,
          leftBound = frame.left,
          rightBound = frame.right,
          maxScale = scale
      )
      val verticalAnimator = VerticalAnimatorImpl(
          targetView = target,
          topBound = frame.top,
          bottomBound = frame.bottom,
          maxScale = scale
      )
      val scaleAnimator = ScaleAnimatorImpl(
          targetView = target,
          maxScale = scale
      )
      return GestureAnimator(horizontalAnimator, verticalAnimator, scaleAnimator)
    }
  }
}