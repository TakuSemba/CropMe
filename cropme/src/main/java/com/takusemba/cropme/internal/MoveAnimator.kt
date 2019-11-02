package com.takusemba.cropme.internal

import androidx.dynamicanimation.animation.SpringForce

/**
 * interface to move Image.
 */
internal interface MoveAnimator {

  /**
   * true if image is flinging, false otherwise
   */
  fun isNotFlinging(): Boolean

  /**
   * move image
   *
   * @param delta distance of how much image moves
   */
  fun move(delta: Float)

  /**
   * bounce image when image is off of [CropOverlay.frame]
   *
   * @param velocity velocity when starting to move
   */
  fun reMoveIfNeeded(velocity: Float)

  /**
   * fling image
   *
   * @param velocity velocity when starting to fling
   */
  fun fling(velocity: Float)

  companion object {

    /**
     * stiffness when flinging or bouncing
     */
    const val STIFFNESS = SpringForce.STIFFNESS_VERY_LOW

    /**
     * dumping ratio when flinging or bouncing
     */
    const val DAMPING_RATIO = SpringForce.DAMPING_RATIO_NO_BOUNCY

    /**
     * friction when flinging
     */
    const val FRICTION = 3f
  }
}
