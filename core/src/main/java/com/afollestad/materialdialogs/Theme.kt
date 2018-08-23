/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs

import android.R.attr
import android.content.Context
import android.support.annotation.StyleRes
import com.afollestad.materialdialogs.utils.getColor
import com.afollestad.materialdialogs.utils.isColorDark

internal enum class Theme(
  @StyleRes val styleRes: Int
) {
  LIGHT(R.style.MD_Light),
  DARK(R.style.MD_Dark);

  companion object {
    fun inferTheme(context: Context): Theme {
      val isPrimaryDark =
        getColor(context = context, attr = attr.textColorPrimary).isColorDark()
      return if (isPrimaryDark) LIGHT else DARK
    }
  }
}
