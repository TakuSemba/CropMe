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
 * HorizontalMoveAnimatorImpl is responsible for animating {@link CropImageView} horizontally.
 **/
class HorizontalMoveAnimatorImpl implements MoveAnimator {

    private SpringAnimation spring;
    private FlingAnimation fling;
    private ObjectAnimator animator;

    private RectF restrictionRect;
    private int maxScale;

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

    HorizontalMoveAnimatorImpl(View target, RectF restrictionRect, int maxScale) {
        this.maxScale = maxScale;
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

            float scale;
            Rect afterRect;
            if (maxScale < target.getScaleX()) {
                scale = maxScale;
                int heightDiff = (int) ((targetRect.height() - targetRect.height() * (maxScale / target.getScaleY())) / 2);
                int widthDiff = (int) ((targetRect.width() - targetRect.width() * (maxScale / target.getScaleY())) / 2);
                afterRect = new Rect(targetRect.left + widthDiff, targetRect.top + heightDiff, targetRect.right - widthDiff, targetRect.bottom - heightDiff);
            } else if (target.getScaleX() < 1) {
                scale = 1;
                int heightDiff = (target.getHeight() - targetRect.height()) / 2;
                int widthDiff = (target.getWidth() - targetRect.width()) / 2;
                afterRect = new Rect(targetRect.left + widthDiff, targetRect.top + heightDiff, targetRect.right - widthDiff, targetRect.bottom - heightDiff);
            } else {
                scale = target.getScaleX();
                afterRect = targetRect;
            }
            float horizontalDiff = (target.getWidth() * scale - target.getWidth()) / 2;

            if (restrictionRect.left < afterRect.left) {
                cancel();
                spring.setStartVelocity(velocity).animateToFinalPosition(restrictionRect.left + horizontalDiff);
            } else if (afterRect.right < restrictionRect.right) {
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
