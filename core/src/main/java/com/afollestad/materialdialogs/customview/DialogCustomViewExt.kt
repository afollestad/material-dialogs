/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.materialdialogs.customview

import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.LayoutRes
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.assertOneSet

internal const val CUSTOM_VIEW_NO_HORIZONTAL_PADDING = "md.custom_view_no_horizontal_padding"

/**
 * Gets the custom view for the dialog, set by [customView].
 *
 * @throws IllegalStateException if there is no custom view set.
 */
@CheckResult fun MaterialDialog.getCustomView(): View {
  return this.view.contentLayout.customView ?: throw IllegalStateException(
      "You have not setup this dialog as a customView dialog."
  )
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
fun MaterialDialog.customView(
  @LayoutRes viewRes: Int? = null,
  view: View? = null,
  scrollable: Boolean = false,
  noVerticalPadding: Boolean = false
): MaterialDialog {
  assertOneSet("customView", view, viewRes)
  config[CUSTOM_VIEW_NO_HORIZONTAL_PADDING] = noVerticalPadding
  this.view.contentLayout.addCustomView(
      res = viewRes,
      view = view,
      scrollable = scrollable
  )
  return this
}
