package com.takusemba.cropme;

import android.support.animation.SpringForce;

/**
 * MoveAnimator
 *
 * @author takusemba
 * @since 05/09/2017
 **/
interface MoveAnimator {
    
    float STIFFNESS = SpringForce.STIFFNESS_VERY_LOW;
    float DAMPING_RATIO = SpringForce.DAMPING_RATIO_NO_BOUNCY;
    float FRICTION = 3f;

    void move(float delta);

    void reMoveIfNeeded(float velocity);

    void fling(float velocity);

    boolean isNotFlinging();

}
