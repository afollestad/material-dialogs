/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.shared

import android.content.Context
import android.graphics.Color
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope
import android.support.v4.content.ContextCompat

@ColorInt
@RestrictTo(Scope.LIBRARY_GROUP)
fun getColor(
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

@RestrictTo(Scope.LIBRARY_GROUP)
fun Int.isColorDark(): Boolean {
  val darkness =
    1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
  return darkness >= 0.5
}

//@ColorInt
//internal fun Int.adjustAlpha(factor: Float): Int {
//  val alpha = Math.round(Color.alpha(this) * factor)
//  val red = Color.red(this)
//  val green = Color.green(this)
//  val blue = Color.blue(this)
//  return Color.argb(alpha, red, green, blue)
//}
