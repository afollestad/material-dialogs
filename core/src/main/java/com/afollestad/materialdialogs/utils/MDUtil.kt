/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.drawable.Drawable
import android.text.Html
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog

@RestrictTo(LIBRARY_GROUP)
object MDUtil {

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

  @RestrictTo(LIBRARY_GROUP) fun getDrawable(
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

  @RestrictTo(LIBRARY_GROUP) fun isLandscape(context: Context) =
    context.resources.configuration.orientation == ORIENTATION_LANDSCAPE
}
