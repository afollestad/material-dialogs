/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.customview

import android.support.annotation.CheckResult
import android.support.annotation.LayoutRes
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.assertOneSet
import com.afollestad.materialdialogs.utils.addContentScrollView
import com.afollestad.materialdialogs.utils.dimenPx
import com.afollestad.materialdialogs.utils.inflate
import com.afollestad.materialdialogs.utils.topMargin
import com.afollestad.materialdialogs.utils.updateMargin
import com.afollestad.materialdialogs.utils.updatePadding

@CheckResult
fun MaterialDialog.getCustomView(): View? {
  return contentCustomView
}

/**
 * Sets a custom view to display in the dialog, below the title and above the action buttons
 * (and checkbox prompt).
 *
 * @param viewRes The layout resource to inflate as the custom view.
 * @param view The view to insert as the custom view.
 * @param scrollable Whether or not the custom view is automatically wrapped in a ScrollView.
 */
@CheckResult
fun MaterialDialog.customView(
  @LayoutRes viewRes: Int? = null,
  view: View? = null,
  scrollable: Boolean = false
): MaterialDialog {
  if (this.contentRecyclerView != null) {
    throw IllegalStateException(
        "This dialog has already been setup with another type " +
            "(e.g. list, message, input, etc.)"
    )
  }
  assertOneSet("customView", view, viewRes)
  if (scrollable || this.contentScrollViewFrame != null) {
    addContentScrollView()
    this.contentCustomView = view ?: inflate(viewRes!!, this.contentScrollViewFrame!!)
    if (!scrollable) {
      // We didn't explicitly want this view to be scrollable but we already had existing
      // scroll content. So, add top margin to separate a bit.
      this.contentCustomView!!.apply {
        updateMargin(top = topMargin() + dimenPx(R.dimen.md_dialog_frame_margin_vertical_less))
        updatePadding(bottom = 0)
      }
    }
    this.contentScrollViewFrame!!.addView(this.contentCustomView)
  } else {
    this.contentCustomView = view ?: inflate(viewRes!!, this.view)
    this.view.addView(this.contentCustomView, 1)
  }
  return this
}
