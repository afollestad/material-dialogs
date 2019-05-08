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
package com.afollestad.materialdialogs.color.utils

import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup.MarginLayoutParams
import android.widget.RelativeLayout
import androidx.annotation.IdRes
import androidx.viewpager.widget.ViewPager

internal fun <T : View> T.setVisibleOrGone(visible: Boolean) {
  visibility = if (visible) VISIBLE else GONE
}

internal fun ViewPager.onPageSelected(selection: (Int) -> Unit) {
  addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
    override fun onPageSelected(position: Int) = selection(position)

    override fun onPageScrollStateChanged(state: Int) = Unit

    override fun onPageScrolled(
      position: Int,
      positionOffset: Float,
      positionOffsetPixels: Int
    ) = Unit
  })
}

internal fun View.changeHeight(height: Int) {
  if (height == 0) {
    visibility = INVISIBLE
  }
  val lp = layoutParams as MarginLayoutParams
  lp.height = height
  layoutParams = lp
}

internal fun View.below(@IdRes id: Int) {
  val lp = layoutParams as RelativeLayout.LayoutParams
  lp.addRule(RelativeLayout.BELOW, id)
  layoutParams = lp
}

internal fun View.clearTopMargin() {
  val lp = this.layoutParams as MarginLayoutParams
  lp.topMargin = 0
  layoutParams = lp
  parent.requestLayout()
}
