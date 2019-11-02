package com.takusemba.cropme

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.core.view.GestureDetectorCompat

/**
 * Detect all actions related to Image transition.
 */
internal class ActionDetector(
    context: Context,
    private val listener: ActionListener
) {

  private val gestureDetectorCompat: GestureDetectorCompat?
  private val scaleGestureDetector: ScaleGestureDetector

  private val simpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
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

  private val simpleScaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

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

  init {
    this.gestureDetectorCompat = GestureDetectorCompat(context, simpleOnGestureListener)
    this.scaleGestureDetector = ScaleGestureDetector(context, simpleScaleListener)
  }

  fun detectAction(event: MotionEvent) {
    if (gestureDetectorCompat == null) {
      throw IllegalStateException("GestureDetectorCompat must not be null")
    }
    gestureDetectorCompat.onTouchEvent(event)
    scaleGestureDetector.onTouchEvent(event)
    when (event.action) {
      MotionEvent.ACTION_UP -> listener.onMoveEnded()
    }
  }
}

