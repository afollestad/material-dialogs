/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup.MarginLayoutParams
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
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

internal fun TextView.textChanged(changed: (String) -> Unit) {
  addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable?) = Unit

    override fun beforeTextChanged(
      s: CharSequence?,
      start: Int,
      count: Int,
      after: Int
    ) = Unit

    override fun onTextChanged(
      s: CharSequence?,
      start: Int,
      before: Int,
      count: Int
    ) = changed(s.toString())
  })
}

internal fun View.changeHeight(height: Int) {
  if (height == 0) {
    visibility = INVISIBLE
  }
  val lp = layoutParams as MarginLayoutParams
  lp.height = height
  layoutParams = lp
}

internal fun View.below(@IdRes id: Int) {
  val lp = layoutParams as RelativeLayout.LayoutParams
  lp.addRule(RelativeLayout.BELOW, id)
  layoutParams = lp
}
