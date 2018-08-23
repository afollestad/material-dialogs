/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.actions

import android.support.v7.widget.AppCompatButton
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton

/** Returns true if the dialog has visible action buttons. */
fun MaterialDialog.hasActionButtons() = view.buttonsLayout.visibleButtons.isNotEmpty()

/** Returns the underlying view for an action button in the dialog. */
fun MaterialDialog.getActionButton(which: WhichButton) =
  view.buttonsLayout.actionButtons[which.index] as AppCompatButton

/** Enables or disables an action button. */
fun MaterialDialog.setActionButtonEnabled(
  which: WhichButton,
  enabled: Boolean
) {
  getActionButton(which).isEnabled = enabled
}
