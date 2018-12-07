package com.takusemba.cropme;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import static android.view.View.SCALE_X;
import static android.view.View.SCALE_Y;

/**
 * ScaleAnimatorImpl is responsible for scaling {@link CropImageView}.
 **/
class ScaleAnimatorImpl implements ScaleAnimator {

    private static final int DURATION = 600;
    private static final int FACTOR = 2;

    private ObjectAnimator animatorX;
    private ObjectAnimator animatorY;
    private int maxScale;

    ScaleAnimatorImpl(View target, int maxScale) {
        this.maxScale = maxScale;

        this.animatorX = new ObjectAnimator();
        animatorX.setProperty(SCALE_X);
        animatorX.setTarget(target);

        this.animatorY = new ObjectAnimator();
        animatorY.setProperty(SCALE_Y);
        animatorY.setTarget(target);
    }

    @Override
    public void scale(final float scale) {
        View targetX = (View) animatorX.getTarget();
        if (targetX != null) {
            animatorX.cancel();
            animatorX.setDuration(0);
            animatorX.setInterpolator(null);
            animatorX.setFloatValues(targetX.getScaleX() * scale);
            animatorX.start();
        }

        View targetY = (View) animatorY.getTarget();
        if (targetY != null) {
            animatorY.cancel();
            animatorY.setDuration(0);
            animatorY.setInterpolator(null);
            animatorY.setFloatValues(targetY.getScaleY() * scale);
            animatorY.start();
        }
    }

    @Override
    public void reScaleIfNeeded() {
        View targetX = (View) animatorX.getTarget();
        if (targetX != null) {
            if (targetX.getScaleX() < 1) {
                animatorX.cancel();
                animatorX.setDuration(DURATION);
                animatorX.setFloatValues(1);
                animatorX.setInterpolator(new DecelerateInterpolator(FACTOR));
                animatorX.start();
            } else if (maxScale < targetX.getScaleX()) {
                animatorX.cancel();
                animatorX.setDuration(DURATION);
                animatorX.setFloatValues(maxScale);
                animatorX.setInterpolator(new DecelerateInterpolator(FACTOR));
                animatorX.start();
            }
        }

        View targetY = (View) animatorY.getTarget();
        if (targetY != null) {
            if (targetY.getScaleY() < 1) {
                animatorY.cancel();
                animatorY.setDuration(DURATION);
                animatorY.setFloatValues(1);
                animatorY.setInterpolator(new DecelerateInterpolator(FACTOR));
                animatorY.start();
            } else if (maxScale < targetY.getScaleY()) {
                animatorY.cancel();
                animatorY.setDuration(DURATION);
                animatorY.setFloatValues(maxScale);
                animatorY.setInterpolator(new DecelerateInterpolator(FACTOR));
                animatorY.start();
            }
        }
    }
}
