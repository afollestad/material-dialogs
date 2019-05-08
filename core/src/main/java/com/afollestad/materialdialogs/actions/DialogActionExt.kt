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
package com.afollestad.materialdialogs.actions

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.internal.button.DialogActionButton
import com.afollestad.materialdialogs.utils.isVisible

/** Returns true if the dialog has visible action buttons. */
fun MaterialDialog.hasActionButtons(): Boolean {
  return view.buttonsLayout?.visibleButtons?.isNotEmpty() ?: false
}

/** Returns true if the given button is visible in the dialog. */
fun MaterialDialog.hasActionButton(which: WhichButton) = getActionButton(which).isVisible()

/** Returns the underlying view for an action button in the dialog. */
fun MaterialDialog.getActionButton(which: WhichButton): DialogActionButton {
  return view.buttonsLayout?.actionButtons?.get(which.index) ?: throw IllegalStateException(
      "The dialog does not have an attached buttons layout."
  )
}

/** Enables or disables an action button. */
fun MaterialDialog.setActionButtonEnabled(
  which: WhichButton,
  enabled: Boolean
) {
  getActionButton(which).isEnabled = enabled
}
