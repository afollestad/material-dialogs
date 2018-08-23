/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.internal.main

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.afollestad.materialdialogs.utils.waitForLayout

/**
 * A [ScrollView] which reports whether or not it's scrollable based on whether the content
 * is shorter than the ScrollView itself. Also reports back to an [DialogLayout] to invalidate
 * dividers.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class DialogScrollView(
  context: Context?,
  attrs: AttributeSet? = null
) : ScrollView(context, attrs) {

  var rootView: DialogLayout? = null

  private var isScrollable: Boolean = false
    get() = getChildAt(0).measuredHeight > height

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    waitForLayout { invalidateDividers() }
  }

  override fun onScrollChanged(
    left: Int,
    top: Int,
    oldl: Int,
    oldt: Int
  ) {
    super.onScrollChanged(left, top, oldl, oldt)
    invalidateDividers()
  }

  private fun invalidateDividers() {
    if (childCount == 0 || measuredHeight == 0 || !isScrollable) {
      rootView?.invalidateDividers(false, false)
      return
    }
    val view = getChildAt(childCount - 1)
    val diff = view.bottom - (measuredHeight + scrollY)
    rootView?.invalidateDividers(
        scrollY > 0,
        diff > 0
    )
  }
}
