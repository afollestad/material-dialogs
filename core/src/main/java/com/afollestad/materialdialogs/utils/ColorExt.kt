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
@file:Suppress("NOTHING_TO_INLINE")

package com.afollestad.materialdialogs.utils

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor
import com.afollestad.materialdialogs.utils.MDUtil.resolveColors

@ColorInt @CheckResult
internal inline fun MaterialDialog.resolveColor(
  @ColorRes res: Int? = null,
  @AttrRes attr: Int? = null
): Int = resolveColor(windowContext, res, attr)

@CheckResult
internal inline fun MaterialDialog.resolveColors(
  attrs: IntArray,
  noinline fallback: ((forAttr: Int) -> Int)? = null
): IntArray = resolveColors(windowContext, attrs, fallback)

@ColorInt @CheckResult
internal inline fun Int.adjustAlpha(alpha: Float): Int {
  return Color.argb((255 * alpha).toInt(), Color.red(this), Color.green(this), Color.blue(this))
}

@RestrictTo(LIBRARY_GROUP) internal fun MaterialDialog.createColorSelector(
  @ColorInt unchecked: Int = 0,
  @ColorInt checked: Int = 0
): ColorStateList {
  return ColorStateList(
      arrayOf(
          intArrayOf(-android.R.attr.state_checked),
          intArrayOf(android.R.attr.state_checked)
      ),
      intArrayOf(
          if (unchecked == 0) resolveColor(
              windowContext, attr = R.attr.colorControlNormal
          ) else unchecked,
          if (checked == 0) resolveColor(
              windowContext, attr = R.attr.colorControlActivated
          ) else checked
      )
  )
}
