/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.materialdialogs.utils

import android.R.attr
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
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
import com.afollestad.materialdialogs.R

@RestrictTo(LIBRARY_GROUP)
object MDUtil {

  @RestrictTo(LIBRARY_GROUP) fun resolveString(
    materialDialog: MaterialDialog,
    @StringRes res: Int? = null,
    @StringRes fallback: Int? = null,
    html: Boolean = false
  ): CharSequence? = resolveString(
      context = materialDialog.windowContext,
      res = res,
      fallback = fallback,
      html = html
  )

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

  @RestrictTo(LIBRARY_GROUP) @ColorInt
  fun resolveColor(
    context: Context,
    @ColorRes res: Int? = null,
    @AttrRes attr: Int? = null,
    fallback: (() -> Int)? = null
  ): Int {
    if (attr != null) {
      val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
      try {
        val result = a.getColor(0, 0)
        if (result == 0 && fallback != null) {
          return fallback()
        }
        return result
      } finally {
        a.recycle()
      }
    }
    return ContextCompat.getColor(context, res ?: 0)
  }

  @RestrictTo(LIBRARY_GROUP)
  fun resolveColors(
    context: Context,
    attrs: IntArray,
    fallback: ((forAttr: Int) -> Int)? = null
  ): IntArray {
    val a = context.theme.obtainStyledAttributes(attrs)
    try {
      return (0 until attrs.size).map { index ->
        val color = a.getColor(index, 0)
        return@map if (color != 0) {
          color
        } else {
          fallback?.invoke(attrs[index]) ?: 0
        }
      }
          .toIntArray()
    } finally {
      a.recycle()
    }
  }

  @RestrictTo(LIBRARY_GROUP) fun resolveInt(
    context: Context,
    @AttrRes attr: Int,
    defaultValue: Int
  ): Int {
    val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
    try {
      return a.getInt(0, defaultValue)
    } finally {
      a.recycle()
    }
  }

  @RestrictTo(LIBRARY_GROUP) fun Int.isColorDark(threshold: Double = 0.5): Boolean {
    if (this == Color.TRANSPARENT) {
      return false
    }
    val darkness =
      1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    return darkness >= threshold
  }

  @RestrictTo(LIBRARY_GROUP) fun <T : View> T.dimenPx(@DimenRes res: Int): Int {
    return context.resources.getDimensionPixelSize(res)
  }

  @RestrictTo(LIBRARY_GROUP) fun Context.isLandscape() =
    resources.configuration.orientation == ORIENTATION_LANDSCAPE

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

  @RestrictTo(LIBRARY_GROUP) fun TextView?.maybeSetTextColor(
    context: Context,
    @AttrRes attrRes: Int?,
    @AttrRes hintAttrRes: Int? = null
  ) {
    if (this == null || (attrRes == null && hintAttrRes == null)) return
    if (attrRes != null) {
      resolveColor(context, attr = attrRes)
          .ifNotZero(this::setTextColor)
    }
    if (hintAttrRes != null) {
      resolveColor(context, attr = hintAttrRes)
          .ifNotZero(this::setHintTextColor)
    }
  }

  @RestrictTo(LIBRARY_GROUP) inline fun Int?.ifNotZero(block: (value: Int) -> Unit) {
    if (this != null && this != 0) {
      block(this)
    }
  }

  @RestrictTo(LIBRARY_GROUP) fun createColorSelector(
    context: Context,
    @ColorInt unchecked: Int = 0,
    @ColorInt checked: Int = 0
  ): ColorStateList {
    val checkedColor = if (checked == 0) resolveColor(
        context, attr = R.attr.colorControlActivated
    ) else checked
    return ColorStateList(
        arrayOf(
            intArrayOf(-attr.state_checked, -attr.state_focused),
            intArrayOf(attr.state_checked),
            intArrayOf(attr.state_focused)
        ),
        intArrayOf(
            if (unchecked == 0) resolveColor(
                context, attr = R.attr.colorControlNormal
            ) else unchecked,
            checkedColor,
            checkedColor
        )
    )
  }
}
