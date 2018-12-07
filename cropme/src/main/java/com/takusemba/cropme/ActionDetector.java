package com.takusemba.cropme;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Detect all actions related to Image transition.
 **/
class ActionDetector {

    private ActionListener listener;
    private GestureDetectorCompat gestureDetectorCompat;
    private ScaleGestureDetector scaleGestureDetector;

    ActionDetector(Context context, ActionListener actionListener) {
        this.listener = actionListener;
        this.gestureDetectorCompat = new GestureDetectorCompat(context, simpleOnGestureListener);
        this.scaleGestureDetector = new ScaleGestureDetector(context, simpleScaleListener);
    }

    void detectAction(MotionEvent event) {
        if (gestureDetectorCompat == null) {
            throw new IllegalStateException("GestureDetectorCompat must not be null");
        }
        gestureDetectorCompat.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                listener.onMoveEnded();
                break;
        }
    }

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent initialEvent, MotionEvent currentEvent, float dx, float dy) {
            listener.onMoved(-dx, -dy);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            listener.onFlinged(velocityX, velocityY);
            return true;
        }
    };

    private ScaleGestureDetector.SimpleOnScaleGestureListener simpleScaleListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            listener.onScaleEnded();
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            listener.onScaled(detector.getScaleFactor());
            return true;
        }
    };
}

