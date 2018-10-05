/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog

fun MaterialDialog.getString(
  @StringRes res: Int? = null,
  @StringRes fallback: Int? = null
): CharSequence? {
  val resourceId = res ?: (fallback ?: 0)
  if (resourceId == 0) return null
  return windowContext.resources.getText(resourceId)
}

internal fun MaterialDialog.getStringArray(@ArrayRes res: Int?): Array<String>? {
  if (res == null) return emptyArray()
  return windowContext.resources.getStringArray(res)
}
