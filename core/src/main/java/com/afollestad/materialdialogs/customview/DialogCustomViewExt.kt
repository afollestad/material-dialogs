/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.customview

import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.LayoutRes
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.assertOneSet

internal const val CUSTOM_VIEW_NO_PADDING = "md.custom_view_no_padding"

/** Gets a custom view set by [customView]. */
@CheckResult fun MaterialDialog.getCustomView(): View? {
  return this.view.contentLayout.customView
}

/**
 * Sets a custom view to display in the dialog, below the title and above the action buttons
 * (and checkbox prompt).
 *
 * @param viewRes The layout resource to inflate as the custom view.
 * @param view The view to insert as the custom view.
 * @param scrollable Whether or not the custom view is automatically wrapped in a ScrollView.
 * @param noVerticalPadding When set to true, vertical padding is not added around your content.
 */
@CheckResult fun MaterialDialog.customView(
  @LayoutRes viewRes: Int? = null,
  view: View? = null,
  scrollable: Boolean = false,
  noVerticalPadding: Boolean = false
): MaterialDialog {
  assertOneSet("customView", view, viewRes)
  config[CUSTOM_VIEW_NO_PADDING] = noVerticalPadding
  this.view.contentLayout.addCustomView(
      res = viewRes,
      view = view,
      scrollable = scrollable
  )
  return this
}
