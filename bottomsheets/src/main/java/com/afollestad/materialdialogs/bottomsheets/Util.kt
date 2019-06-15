/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.materialdialogs.bottomsheets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.animation.DecelerateInterpolator
import androidx.annotation.CheckResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import java.lang.Float.isNaN
import kotlin.math.abs

internal fun BottomSheetBehavior<*>.setCallbacks(
  onSlide: (currentHeight: Int) -> Unit,
  onHide: () -> Unit
) {
  setBottomSheetCallback(object : BottomSheetCallback() {
    private var currentState: Int = STATE_COLLAPSED

    override fun onSlide(
      view: View,
      dY: Float
    ) {
      if (state == STATE_HIDDEN) return
      val percentage = if (isNaN(dY)) 0f else dY
      if (percentage > 0f) {
        val diff = peekHeight * abs(percentage)
        onSlide((peekHeight + diff).toInt())
      } else {
        val diff = peekHeight * abs(percentage)
        onSlide((peekHeight - diff).toInt())
      }
    }

    override fun onStateChanged(
      view: View,
      state: Int
    ) {
      currentState = state
      if (state == STATE_HIDDEN) onHide()
    }
  })
}

internal fun BottomSheetBehavior<*>.animatePeekHeight(
  view: View,
  start: Int = peekHeight,
  dest: Int,
  duration: Long,
  onEnd: () -> Unit = {}
) {
  if (dest == start) {
    return
  } else if (duration <= 0) {
    peekHeight = dest
    return
  }
  val animator = animateValues(
      from = start,
      to = dest,
      duration = duration,
      onUpdate = this::setPeekHeight,
      onEnd = onEnd
  )
  view.onDetach { animator.cancel() }
  animator.start()
}

@CheckResult internal fun animateValues(
  from: Int,
  to: Int,
  duration: Long,
  onUpdate: (currentValue: Int) -> Unit,
  onEnd: () -> Unit = {}
): Animator {
  return ValueAnimator.ofInt(from, to)
      .apply {
        this.interpolator = DecelerateInterpolator()
        this.duration = duration
        addUpdateListener {
          onUpdate(it.animatedValue as Int)
        }
        addListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) = onEnd()
        })
      }
}

internal fun <T : View> T.onDetach(onAttached: T.() -> Unit) {
  addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
    @Suppress("UNCHECKED_CAST")
    override fun onViewDetachedFromWindow(v: View) {
      removeOnAttachStateChangeListener(this)
      (v as T).onAttached()
    }

    override fun onViewAttachedToWindow(v: View) = Unit
  })
}
