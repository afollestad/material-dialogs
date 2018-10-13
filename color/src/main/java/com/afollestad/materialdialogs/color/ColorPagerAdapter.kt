/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.viewpager.widget.PagerAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.utils.Util

internal class ColorPagerAdapter(
  dialog: MaterialDialog,
  @StringRes tabGridTextRes: Int? = null,
  tabGridText: String? = null,
  @StringRes tabCustomTextRes: Int? = null,
  tabCustomText: String? = null
) : PagerAdapter() {

  private val actualTabGridTitle: CharSequence
  private val actualTabCustomTitle: CharSequence

  init {
    actualTabGridTitle = tabGridText
        ?: Util.getString(dialog, tabGridTextRes, R.string.md_dialog_color_presets)!!
    actualTabCustomTitle = tabCustomText
        ?: Util.getString(dialog, tabCustomTextRes, R.string.md_dialog_color_custom)!!
  }

  override fun instantiateItem(
    collection: ViewGroup,
    position: Int
  ): Any {
    var resId = 0
    when (position) {
      0 -> resId = R.id.colorPresetGrid
      1 -> resId = R.id.colorArgbPage
    }
    return collection.findViewById(resId)
  }

  override fun getCount(): Int {
    return 2
  }

  override fun isViewFromObject(
    arg0: View,
    arg1: Any
  ): Boolean {
    return arg0 === arg1 as View
  }

  override fun getPageTitle(position: Int): CharSequence? = when (position) {
    0 -> actualTabGridTitle
    1 -> actualTabCustomTitle
    else -> null
  }
}
