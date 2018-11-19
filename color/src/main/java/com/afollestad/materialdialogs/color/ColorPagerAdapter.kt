/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

internal class ColorPagerAdapter : PagerAdapter() {

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

  override fun getCount() = 2

  override fun isViewFromObject(
    arg0: View,
    arg1: Any
  ) = arg0 === arg1 as View

  override fun destroyItem(
    container: ViewGroup,
    position: Int,
    arg1: Any
  ) = Unit
}
