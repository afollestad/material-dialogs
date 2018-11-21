/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog

@RestrictTo(LIBRARY_GROUP)
object MDUtil {

  @RestrictTo(LIBRARY_GROUP) fun resolveString(
    materialDialog: MaterialDialog,
    @StringRes res: Int? = null,
    @StringRes fallback: Int? = null,
    html: Boolean = false
  ): CharSequence? {
    return resolveString(
        context = materialDialog.windowContext,
        res = res,
        fallback = fallback,
        html = html
    )
  }

  @RestrictTo(LIBRARY_GROUP) fun resolveString(
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

  @RestrictTo(LIBRARY_GROUP) fun resolveDrawable(
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

  @RestrictTo(LIBRARY_GROUP)
  @ColorInt
  fun resolveColor(
    context: Context,
    @ColorRes res: Int? = null,
    @AttrRes attr: Int? = null
  ): Int {
    if (attr != null) {
      val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
      try {
        return a.getColor(0, 0)
      } finally {
        a.recycle()
      }
    }
    return ContextCompat.getColor(context, res ?: 0)
  }

  @RestrictTo(LIBRARY_GROUP) fun Int.isColorDark(): Boolean {
    if (this == Color.TRANSPARENT) {
      return false
    }
    val darkness =
      1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    return darkness >= 0.5
  }

  @RestrictTo(LIBRARY_GROUP) fun <T : View> T.dimenPx(@DimenRes res: Int): Int {
    return context.resources.getDimensionPixelSize(res)
  }

  @RestrictTo(LIBRARY_GROUP) fun isLandscape(context: Context) =
    context.resources.configuration.orientation == ORIENTATION_LANDSCAPE

  @RestrictTo(LIBRARY_GROUP) fun EditText.textChanged(callback: (CharSequence) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable) = Unit

      override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
      ) = Unit

      override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
      ) = callback.invoke(s)
    })
  }
}
