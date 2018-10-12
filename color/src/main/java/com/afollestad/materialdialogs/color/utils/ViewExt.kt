/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SeekBar
import androidx.annotation.DimenRes
import androidx.viewpager.widget.ViewPager

internal fun <T : View> T.dimenPx(@DimenRes res: Int): Int {
  return context.resources.getDimensionPixelSize(res)
}

internal fun <T : View> T.setVisibleOrGone(visible: Boolean) {
  visibility = if (visible) VISIBLE else GONE
}

internal fun ViewPager.onPageSelected(selection: (Int) -> Unit) {
  addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
    override fun onPageSelected(position: Int) = selection(position)

    override fun onPageScrollStateChanged(state: Int) = Unit

    override fun onPageScrolled(
      position: Int,
      positionOffset: Float,
      positionOffsetPixels: Int
    ) = Unit
  })
}

internal fun Array<SeekBar>.progressChanged(selection: (Int) -> Unit) {
  val listener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(
      p0: SeekBar?,
      p1: Int,
      p2: Boolean
    ) = selection(p1)

    override fun onStartTrackingTouch(p0: SeekBar?) = Unit
    override fun onStopTrackingTouch(p0: SeekBar?) = Unit
  }
  for (bar in this) {
    bar.setOnSeekBarChangeListener(listener)
  }
}
