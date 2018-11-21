/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color.utils

import android.graphics.Color

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
