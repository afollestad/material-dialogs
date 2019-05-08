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
package com.afollestad.materialdialogs.utils

import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.utils.MDUtil.assertOneSet

internal fun MaterialDialog.dimen(
  @DimenRes res: Int? = null,
  @AttrRes attr: Int? = null,
  fallback: Float = windowContext.resources.getDimension(R.dimen.md_dialog_default_corner_radius)
): Float {
  assertOneSet("dimen", attr, res)
  if (res != null) {
    return windowContext.resources.getDimension(res)
  }
  requireNotNull(attr)
  val a = windowContext.theme.obtainStyledAttributes(intArrayOf(attr))
  try {
    return a.getDimension(0, fallback)
  } finally {
    a.recycle()
  }
}

internal fun View.dp(value: Int): Float {
  return TypedValue.applyDimension(COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics)
}
