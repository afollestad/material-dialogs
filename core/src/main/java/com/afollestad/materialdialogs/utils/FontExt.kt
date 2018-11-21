/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import android.graphics.Typeface
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.assertOneSet

@CheckResult internal fun MaterialDialog.font(
  @FontRes res: Int? = null,
  @AttrRes attr: Int? = null
): Typeface? {
  assertOneSet("font", attr, res)
  if (res != null) {
    return ResourcesCompat.getFont(windowContext, res)
  }
  requireNotNull(attr)
  val a = windowContext.theme.obtainStyledAttributes(intArrayOf(attr))
  try {
    val resId = a.getResourceId(0, 0)
    if (resId == 0) return null
    return ResourcesCompat.getFont(windowContext, resId)
  } finally {
    a.recycle()
  }
}
