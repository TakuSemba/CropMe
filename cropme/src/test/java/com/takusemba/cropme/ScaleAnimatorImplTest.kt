package com.takusemba.cropme

import android.animation.ObjectAnimator
import android.view.View
import com.takusemba.cropme.internal.ScaleAnimatorImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ScaleAnimatorImplTest {

  @Test
  fun scale() {
    val targetView = mockk<View>(relaxed = true, relaxUnitFun = true)
    val animatorX = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)
    val animatorY = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)

    every { targetView.scaleX } returns 1.2f
    every { targetView.scaleY } returns 1.2f

    val scaleAnimator = ScaleAnimatorImpl(targetView, 2f, animatorX, animatorY)

    scaleAnimator.scale(1.2f)

    verifyOrder {
      animatorX.cancel()
      animatorX.setFloatValues(eq(1.2f * 1.2f))
      animatorX.start()
    }
    verifyOrder {
      animatorY.cancel()
      animatorY.setFloatValues(eq(1.2f * 1.2f))
      animatorY.start()
    }
  }

  @Test
  fun adjust_whenTooLarge() {
    val targetView = mockk<View>(relaxed = true, relaxUnitFun = true)
    val animatorX = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)
    val animatorY = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)

    every { targetView.scaleX } returns 3.0f
    every { targetView.scaleY } returns 3.0f

    val scaleAnimator = ScaleAnimatorImpl(targetView, 2f, animatorX, animatorY)

    scaleAnimator.adjust()

    verifyOrder {
      animatorX.cancel()
      animatorX.setFloatValues(eq(2f))
      animatorX.start()
    }
    verifyOrder {
      animatorY.cancel()
      animatorY.setFloatValues(eq(2f))
      animatorY.start()
    }
  }

  @Test
  fun adjust_whenTooSmall() {
    val targetView = mockk<View>(relaxed = true, relaxUnitFun = true)
    val animatorX = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)
    val animatorY = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)

    every { targetView.scaleX } returns 0.7f
    every { targetView.scaleY } returns 0.7f

    val scaleAnimator = ScaleAnimatorImpl(targetView, 2f, animatorX, animatorY)

    scaleAnimator.adjust()

    verifyOrder {
      animatorX.cancel()
      animatorX.setFloatValues(eq(1f))
      animatorX.start()
    }
    verifyOrder {
      animatorY.cancel()
      animatorY.setFloatValues(eq(1f))
      animatorY.start()
    }
  }

  @Test
  fun adjust_whenWithinRestrict() {
    val targetView = mockk<View>(relaxed = true, relaxUnitFun = true)
    val animatorX = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)
    val animatorY = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)

    every { targetView.scaleX } returns 1.7f
    every { targetView.scaleY } returns 1.7f

    val scaleAnimator = ScaleAnimatorImpl(targetView, 2f, animatorX, animatorY)

    scaleAnimator.adjust()

    verifyOrder(inverse = true) {
      animatorX.cancel()
      animatorX.setFloatValues(any())
      animatorX.start()
    }
    verifyOrder(inverse = true) {
      animatorY.cancel()
      animatorY.setFloatValues(any())
      animatorY.start()
    }
  }
}