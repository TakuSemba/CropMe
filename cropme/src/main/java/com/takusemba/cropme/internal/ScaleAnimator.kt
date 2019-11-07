package com.takusemba.cropme.internal

/**
 * Interface to scale Image.
 */
internal interface ScaleAnimator {

  /**
   * Scale image
   *
   * @param scale how much image scales
   */
  fun scale(scale: Float)

  /**
   * Adjust scaling when image is too much big or small
   */
  fun adjust()

  companion object {

    /**
     * Original scale
     */
    const val ORIGINAL_SCALE = 1f

    /**
     * The length of animation while adjusting
     */
    const val ADJUSTING_DURATION = 600L

    /**
     * The Interpolate factor while adjusting
     */
    const val ADJUSTING_FACTOR = 2f
  }
}

