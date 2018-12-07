package com.takusemba.cropme;

/**
 * ActionListener passes events to {@link ScaleAnimator} and {@link MoveAnimator} to animate Image.
 **/
interface ActionListener {

    /**
     * Called when scaling action is detected
     *
     * @param scale scaling out when it's greater than 1
     *              scaling in when it's less than 1
     */
    void onScaled(float scale);

    /**
     * Called when scaling action ends
     */
    void onScaleEnded();

    /**
     * Called when moving action is detected
     *
     * @param dx horizontal moved distance
     * @param dy vertical moved distance
     */
    void onMoved(float dx, float dy);

    /**
     * Called when fling action is detected
     *
     * @param velocityX horizontal velocity when flinged
     * @param velocityY vertical velocity when flinged
     */
    void onFlinged(float velocityX, float velocityY);

    /**
     * Called when moving action ends
     */
    void onMoveEnded();
}