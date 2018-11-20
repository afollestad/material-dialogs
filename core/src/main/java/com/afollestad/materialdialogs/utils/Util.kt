/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.text.Html
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog

@RestrictTo(LIBRARY_GROUP)
object Util {

  @RestrictTo(LIBRARY_GROUP) fun getString(
    materialDialog: MaterialDialog,
    @StringRes res: Int? = null,
    @StringRes fallback: Int? = null,
    html: Boolean = false
  ): CharSequence? {
    return getString(
        context = materialDialog.windowContext,
        res = res,
        fallback = fallback,
        html = html
    )
  }

  @RestrictTo(LIBRARY_GROUP) fun getString(
    context: Context,
    @StringRes res: Int? = null,
    @StringRes fallback: Int? = null,
    html: Boolean = false
  ): CharSequence? {
    val resourceId = res ?: (fallback ?: 0)
    if (resourceId == 0) return null
    val text = context.resources.getText(resourceId)
    if (html) {
      @Suppress("DEPRECATION")
      return Html.fromHtml(text.toString())
    }
    return text
  }

  @RestrictTo(LIBRARY_GROUP) fun isLandscape(context: Context) =
    context.resources.configuration.orientation == ORIENTATION_LANDSCAPE
}
