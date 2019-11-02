package com.takusemba.cropme

/**
 * interface to scale Image.
 */
internal interface ScaleAnimator {

  /**
   * scale image
   *
   * @param scale how much image scales
   */
  fun scale(scale: Float)

  /**
   * rescale image when image is too much big or small
   */
  fun reScaleIfNeeded()
}
