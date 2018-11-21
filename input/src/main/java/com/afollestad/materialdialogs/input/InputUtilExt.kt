/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.input

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled

internal fun MaterialDialog.invalidateInputMaxLength() {
  val maxLength = getInputLayout()?.counterMaxLength ?: return
  val currentLength = getInputField()?.text?.length ?: 0
  if (maxLength > 0) {
    setActionButtonEnabled(POSITIVE, currentLength <= maxLength)
  }
}

internal fun MaterialDialog.showKeyboardIfApplicable() {
  val editText = getInputField() ?: return
  editText.postRun {
    requestFocus()
    val imm =
      windowContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
  }
}

internal inline fun <T : View> T.postRun(crossinline exec: T.() -> Unit) = this.post {
  this.exec()
}
