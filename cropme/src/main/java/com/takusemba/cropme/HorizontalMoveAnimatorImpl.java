package com.takusemba.cropme;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.view.View;

import static android.view.View.TRANSLATION_X;

/**
 * HorizontalMoveAnimatorImpl
 *
 * @author takusemba
 * @since 05/09/2017
 **/
class HorizontalMoveAnimatorImpl implements MoveAnimator {

    private SpringAnimation spring;
    private FlingAnimation fling;
    private ObjectAnimator animator;

    private RectF restrictionRect;

    private DynamicAnimation.OnAnimationUpdateListener updateListener = new DynamicAnimation.OnAnimationUpdateListener() {
        @Override
        public void onAnimationUpdate(DynamicAnimation dynamicAnimation, float value, float velocity) {
            reMoveIfNeeded(velocity);
        }
    };
    private DynamicAnimation.OnAnimationEndListener endListener = new DynamicAnimation.OnAnimationEndListener() {
        @Override
        public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean b, float v, float v1) {
            isFlinging = false;
        }
    };

    private boolean isFlinging = false;

    HorizontalMoveAnimatorImpl(View target, RectF restrictionRect) {
        this.restrictionRect = restrictionRect;

        spring = new SpringAnimation(target,
                new FloatPropertyCompat<View>("X") {
                    @Override
                    public float getValue(View view) {
                        return view.getX();
                    }

                    @Override
                    public void setValue(View view, float value) {
                        view.setX(value);
                    }
                })
                .setSpring(new SpringForce()
                        .setStiffness(STIFFNESS)
                        .setDampingRatio(DAMPING_RATIO)
                );

        fling = new FlingAnimation(target, DynamicAnimation.X).setFriction(FRICTION);

        animator = new ObjectAnimator();
        animator.setProperty(TRANSLATION_X);
        animator.setTarget(target);
    }

    @Override
    public void move(float delta) {
        View target = (View) animator.getTarget();
        if (target != null) {
            cancel();
            animator.setInterpolator(null);
            animator.setDuration(0);
            animator.setFloatValues(target.getTranslationX() + delta);
            animator.start();
        }
    }

    @Override
    public void reMoveIfNeeded(float velocity) {
        View target = (View) animator.getTarget();
        if (target != null) {
            Rect targetRect = new Rect();
            target.getHitRect(targetRect);

            float scale = 1 < target.getScaleY() ? target.getScaleY() : 1;
            float horizontalDiff = (target.getWidth() * scale - target.getWidth()) / 2;

            if (restrictionRect.left < targetRect.left) {
                cancel();
                spring.setStartVelocity(velocity).animateToFinalPosition(restrictionRect.left + horizontalDiff);
            } else if (targetRect.right < restrictionRect.right) {
                cancel();
                spring.setStartVelocity(velocity).animateToFinalPosition(restrictionRect.right - target.getWidth() - horizontalDiff);
            }
        }
    }

    @Override
    public void fling(float velocity) {
        isFlinging = true;
        cancel();
        fling.addUpdateListener(updateListener);
        fling.addEndListener(endListener);
        fling.setStartVelocity(velocity).start();
    }

    @Override
    public boolean isNotFlinging() {
        return !isFlinging;
    }

    private void cancel() {
        animator.cancel();
        spring.cancel();
        fling.cancel();
        fling.removeUpdateListener(updateListener);
        fling.removeEndListener(endListener);
        isFlinging = false;
    }
}
