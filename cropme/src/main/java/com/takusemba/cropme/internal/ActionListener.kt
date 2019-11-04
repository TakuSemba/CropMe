package com.takusemba.cropme.internal

/**
 * ActionListener to notify action events that is needed for moving/scaling a view.
 */
internal interface ActionListener {

  /**
   * Called when scaling action is detected
   *
   * @param scale scaling out when it's greater than 1
   * scaling in when it's less than 1
   */
  fun onScaled(scale: Float)

  /**
   * Called when scaling action ends
   */
  fun onScaleEnded()

  /**
   * Called when moving action is detected
   *
   * @param dx horizontal moved distance
   * @param dy vertical moved distance
   */
  fun onMoved(dx: Float, dy: Float)

  /**
   * Called when fling action is detected
   *
   * @param velocityX horizontal velocity when flinged
   * @param velocityY vertical velocity when flinged
   */
  fun onFlinged(velocityX: Float, velocityY: Float)

  /**
   * Called when moving action ends
   */
  fun onMoveEnded()
}