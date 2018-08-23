/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import android.support.annotation.AttrRes
import android.support.annotation.DimenRes
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.assertOneSet

internal fun MaterialDialog.dimen(
  @DimenRes res: Int? = null,
  @AttrRes attr: Int? = null,
  fallback: Float = windowContext.resources.getDimension(R.dimen.md_dialog_default_corner_radius)
): Float {
  assertOneSet("dimen", attr, res)
  if (res != null) {
    return windowContext.resources.getDimension(res)
  }
  val a = windowContext.theme.obtainStyledAttributes(intArrayOf(attr!!))
  try {
    return a.getDimension(0, fallback)
  } finally {
    a.recycle()
  }
}

internal fun <T : View> T.dimenPx(@DimenRes res: Int): Int {
  return context.resources.getDimensionPixelSize(res)
}
