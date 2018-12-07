package com.takusemba.cropme;

import android.support.animation.SpringForce;

/**
 * interface to move Image.
 **/
interface MoveAnimator {

    /**
     * stiffness when flinging or bouncing
     **/
    float STIFFNESS = SpringForce.STIFFNESS_VERY_LOW;

    /**
     * dumping ratio when flinging or bouncing
     **/
    float DAMPING_RATIO = SpringForce.DAMPING_RATIO_NO_BOUNCY;

    /**
     * friction when flinging
     **/
    float FRICTION = 3f;

    /**
     * move image
     *
     * @param delta distance of how much image moves
     **/
    void move(float delta);

    /**
     * bounce image when image is off of {@link CropOverlay#getFrame()}
     *
     * @param velocity velocity when starting to move
     **/
    void reMoveIfNeeded(float velocity);

    /**
     * fling image
     *
     * @param velocity velocity when starting to fling
     **/
    void fling(float velocity);

    /**
     * true if image is flinging, false otherwise
     **/
    boolean isNotFlinging();

}
