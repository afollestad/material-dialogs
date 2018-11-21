/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.utils

import android.content.Context
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.utils.MDUtil.getColor

@ColorInt internal fun MaterialDialog.getColor(
  @ColorRes res: Int? = null,
  @AttrRes attr: Int? = null
): Int = getColor(windowContext, res, attr)

internal fun TextView?.maybeSetTextColor(
  context: Context,
  @AttrRes attrRes: Int?
) {
  if (attrRes == null) return
  val color = getColor(context, attr = attrRes)
  if (color != 0) {
    this?.setTextColor(color)
  }
}
