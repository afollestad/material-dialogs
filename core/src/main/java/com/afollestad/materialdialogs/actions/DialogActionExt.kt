package com.afollestad.materialdialogs.actions

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton

/** Enables or disables an action button. */
fun MaterialDialog.setActionButtonEnabled(
  which: WhichButton,
  enabled: Boolean
): MaterialDialog {
  view.buttonsLayout.actionButtons[which.index].isEnabled = enabled
  return this
}

/** Returns true if the dialog has visible action buttons. */
fun MaterialDialog.hasActionButtons() = view.buttonsLayout.visibleButtons.isNotEmpty()