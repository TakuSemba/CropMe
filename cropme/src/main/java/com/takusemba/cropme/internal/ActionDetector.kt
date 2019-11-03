package com.takusemba.cropme.internal

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat

/**
 * Detect all actions related to Image moving and scaling.
 */
internal class ActionDetector(
    private val target: View,
    private val listener: ActionListener
) {

  private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
      return true
    }

    override fun onShowPress(e: MotionEvent) = Unit

    override fun onSingleTapUp(e: MotionEvent): Boolean {
      return true
    }

    override fun onScroll(
        initialEvent: MotionEvent, currentEvent: MotionEvent, dx: Float, dy: Float
    ): Boolean {
      listener.onMoved(-dx, -dy)
      return true
    }

    override fun onLongPress(e: MotionEvent) = Unit

    override fun onFling(
        e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
    ): Boolean {
      listener.onFlinged(velocityX, velocityY)
      return true
    }
  }

  private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
      return super.onScaleBegin(detector)
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
      super.onScaleEnd(detector)
      listener.onScaleEnded()
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
      listener.onScaled(detector.scaleFactor)
      return true
    }
  }

  private val gestureDetector = GestureDetectorCompat(target.context, gestureListener)
  private val scaleDetector = ScaleGestureDetector(target.context, scaleListener)

  fun start() {
    target.setOnTouchListener { _, event ->
      gestureDetector.onTouchEvent(event)
      scaleDetector.onTouchEvent(event)
      when (event.action) {
        MotionEvent.ACTION_UP -> listener.onMoveEnded()
      }
      true
    }
  }
}

