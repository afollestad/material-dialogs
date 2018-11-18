/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

@ColorInt internal fun getColor(
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
  if (this == TRANSPARENT) {
    return false
  }
  val darkness =
    1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
  return darkness >= 0.5
}

internal fun Int.hexValue(includeAlpha: Boolean) = if (this == 0) {
  if (includeAlpha) "00000000" else "000000"
} else {
  if (includeAlpha) Integer.toHexString(this) else String.format("%06X", 0xFFFFFF and this)
}

internal fun String.toColor(): Int? {
  return try {
    Color.parseColor("#$this")
  } catch (_: Exception) {
    null
  }
}
