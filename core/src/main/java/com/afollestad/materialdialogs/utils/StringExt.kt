/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import androidx.annotation.ArrayRes
import com.afollestad.materialdialogs.MaterialDialog

internal fun MaterialDialog.getStringArray(@ArrayRes res: Int?): Array<String>? {
  if (res == null) return emptyArray()
  return windowContext.resources.getStringArray(res)
}
