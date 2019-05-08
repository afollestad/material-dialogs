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
package com.afollestad.materialdialogs.internal.main

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import com.afollestad.materialdialogs.utils.MDUtil.waitForWidth

/**
 * A [ScrollView] which reports whether or not it's scrollable based on whether the content
 * is shorter than the ScrollView itself. Also reports back to an [DialogLayout] to invalidate
 * dividers.
 *
 * @author Aidan Follestad (afollestad)
 */
@RestrictTo(LIBRARY_GROUP)
class DialogScrollView(
  context: Context?,
  attrs: AttributeSet? = null
) : ScrollView(context, attrs) {

  var rootView: DialogLayout? = null

  private val isScrollable: Boolean
    get() = getChildAt(0).measuredHeight > height

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    waitForWidth {
      invalidateDividers()
      invalidateOverScroll()
    }
  }

  override fun onScrollChanged(
    left: Int,
    top: Int,
    oldl: Int,
    oldt: Int
  ) {
    super.onScrollChanged(left, top, oldl, oldt)
    invalidateDividers()
  }

  fun invalidateDividers() {
    if (childCount == 0 || measuredHeight == 0 || !isScrollable) {
      rootView?.invalidateDividers(showTop = false, showBottom = false)
      return
    }
    val view = getChildAt(childCount - 1)
    val diff = view.bottom - (measuredHeight + scrollY)
    rootView?.invalidateDividers(
        scrollY > 0,
        diff > 0
    )
  }

  private fun invalidateOverScroll() {
    overScrollMode = if (childCount == 0 || measuredHeight == 0 || !isScrollable) {
      OVER_SCROLL_NEVER
    } else {
      OVER_SCROLL_IF_CONTENT_SCROLLS
    }
  }
}
