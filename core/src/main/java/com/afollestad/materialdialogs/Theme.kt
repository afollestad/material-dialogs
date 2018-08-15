package com.afollestad.materialdialogs

import android.content.Context
import android.support.annotation.StyleRes
import com.afollestad.materialdialogs.shared.getColor
import com.afollestad.materialdialogs.shared.isColorDark

internal enum class Theme(
  @StyleRes val styleRes: Int
) {
  LIGHT(R.style.MD_Light),
  DARK(R.style.MD_Dark);

  companion object {
    fun inferTheme(context: Context): Theme {
      val isPrimaryDark =
        getColor(context = context, attr = android.R.attr.textColorPrimary).isColorDark()
      return if (isPrimaryDark) LIGHT else DARK
    }
  }
}