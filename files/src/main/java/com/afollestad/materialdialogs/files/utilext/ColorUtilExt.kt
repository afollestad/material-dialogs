/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.files.utilext

import android.content.Context
import android.widget.TextView
import androidx.annotation.AttrRes
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor

internal fun TextView?.maybeSetTextColor(
  context: Context,
  @AttrRes attrRes: Int?
) {
  if (attrRes == null) return
  val color = resolveColor(context, attr = attrRes)
  if (color != 0) {
    this?.setTextColor(color)
  }
}
