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

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.utils.MDUtil.assertOneSet

@CheckResult internal fun MaterialDialog.font(
  @FontRes res: Int? = null,
  @AttrRes attr: Int? = null
): Typeface? {
  assertOneSet("font", attr, res)
  if (res != null) {
    return safeGetFont(windowContext, res)
  }
  requireNotNull(attr)
  val a = windowContext.theme.obtainStyledAttributes(intArrayOf(attr))
  try {
    return fontFromResIdOrString(windowContext, a)
  } finally {
    a.recycle()
  }
}

private fun safeGetFont(context: Context, @FontRes res: Int): Typeface? {
  return try {
    ResourcesCompat.getFont(context, res)
  } catch (e: Throwable) {
    e.printStackTrace()
    null
  }
}

private fun fontFromResIdOrString(context: Context, a: TypedArray): Typeface? {
  try {
    val resId = a.getResourceId(0, 0)
    if (resId != 0) {
      val typeface = safeGetFont(context, resId)
      if (typeface != null) return typeface
    }
    val string = a.getString(0)
    if (string != null) {
      return Typeface.create(string, Typeface.NORMAL)
    }
    return null
  } catch (e: Throwable) {
    e.printStackTrace()
    return null
  }
}
