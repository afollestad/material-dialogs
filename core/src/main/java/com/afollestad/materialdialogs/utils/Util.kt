/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import android.text.Html
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog

object Util {
  fun getString(
    materialDialog: MaterialDialog,
    @StringRes res: Int? = null,
    @StringRes fallback: Int? = null,
    html: Boolean = false
  ): CharSequence? {
    val resourceId = res ?: (fallback ?: 0)
    if (resourceId == 0) return null
    val text = materialDialog.windowContext.resources.getText(resourceId)
    if (html) {
      @Suppress("DEPRECATION")
      return Html.fromHtml(text.toString())
    }
    return text
  }
}
