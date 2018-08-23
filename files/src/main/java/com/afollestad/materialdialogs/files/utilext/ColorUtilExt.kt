/* Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.files.utilext

import android.content.Context
import android.graphics.Color
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat

@ColorInt
internal fun getColor(
  context: Context,
  @ColorRes res: Int? = null,
  @AttrRes attr: Int? = null
): Int {
  if (attr != null) {
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
      return a.getColor(0, Color.BLACK)
    } finally {
      a.recycle()
    }
  }
  return ContextCompat.getColor(context, res ?: 0)
}

internal fun Int.isColorDark(): Boolean {
  val darkness =
    1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
  return darkness >= 0.5
}
