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
package com.afollestad.materialdialogs.datetime.internal

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.utils.MDUtil.ifNotZero

internal class WrapContentViewPager(
  context: Context,
  attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    var newHeightSpec = heightMeasureSpec

    var maxChildHeight = 0
    forEachChild { child ->
      child.measure(
          widthMeasureSpec,
          MeasureSpec.makeMeasureSpec(0, UNSPECIFIED)
      )

      val h = child.measuredHeight
      if (h > maxChildHeight) {
        maxChildHeight = h
      }
    }

    val maxAllowedHeightFromParent = MeasureSpec.getSize(heightMeasureSpec)
    if (maxChildHeight > maxAllowedHeightFromParent) {
      maxChildHeight = maxAllowedHeightFromParent
    }
    maxChildHeight.ifNotZero {
      newHeightSpec = MeasureSpec.makeMeasureSpec(it, EXACTLY)
    }

    super.onMeasure(widthMeasureSpec, newHeightSpec)
  }

  private fun forEachChild(each: (View) -> Unit) {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      each(child)
    }
  }
}
