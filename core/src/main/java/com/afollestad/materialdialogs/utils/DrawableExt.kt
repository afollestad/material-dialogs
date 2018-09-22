/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

internal fun getDrawable(
  context: Context,
  @DrawableRes res: Int? = null,
  @AttrRes attr: Int? = null,
  fallback: Drawable? = null
): Drawable? {
  if (attr != null) {
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
      var d = a.getDrawable(0)
      if (d == null && fallback != null) {
        d = fallback
      }
      return d
    } finally {
      a.recycle()
    }
  }
  if (res == null) return fallback
  return ContextCompat.getDrawable(context, res)
}
