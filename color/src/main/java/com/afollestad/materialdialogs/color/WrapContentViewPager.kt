/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import androidx.viewpager.widget.ViewPager

internal class WrapContentViewPager(
  context: Context,
  attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    var newHeightSpec = heightMeasureSpec

    var height = 0
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      child.measure(
          widthMeasureSpec, makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
      )
      val h = child.measuredHeight
      if (h > height) height = h
    }

    if (height != 0) {
      newHeightSpec = makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    }

    super.onMeasure(widthMeasureSpec, newHeightSpec)
  }
}
