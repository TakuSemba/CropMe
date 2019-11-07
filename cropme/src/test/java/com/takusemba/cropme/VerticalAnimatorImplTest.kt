package com.takusemba.cropme

import android.animation.ObjectAnimator
import android.view.View
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import com.takusemba.cropme.internal.VerticalAnimatorImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class VerticalAnimatorImplTest {

  @Test
  fun move() {
    val targetView = mockk<View>(relaxed = true, relaxUnitFun = true)
    val spring = mockk<SpringAnimation>(relaxed = true, relaxUnitFun = true)
    val fling = mockk<FlingAnimation>(relaxed = true, relaxUnitFun = true)
    val animator = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)

    every { targetView.translationY } returns 20f

    val scaleAnimator = VerticalAnimatorImpl(targetView, 10f, 50f, 2f, spring, fling, animator)

    scaleAnimator.move(10f)

    verifyOrder {
      animator.cancel()
      animator.setFloatValues(eq(10f + 20f))
      animator.start()
    }
  }

  // TODO write tests for move()

  @Test
  fun fling() {
    val targetView = mockk<View>(relaxed = true, relaxUnitFun = true)
    val spring = mockk<SpringAnimation>(relaxed = true, relaxUnitFun = true)
    val fling = mockk<FlingAnimation>(relaxed = true, relaxUnitFun = true)
    val animator = mockk<ObjectAnimator>(relaxed = true, relaxUnitFun = true)

    val scaleAnimator = VerticalAnimatorImpl(targetView, 10f, 50f, 2f, spring, fling, animator)

    scaleAnimator.fling(100f)

    verifyOrder {
      fling.cancel()
      fling.setStartVelocity(eq(100f))
    }
  }
}