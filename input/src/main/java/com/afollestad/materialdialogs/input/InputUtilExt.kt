package com.afollestad.materialdialogs.input

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.shared.postApply

internal fun MaterialDialog.invalidateInputMaxLength() {
  val maxLength = getInputLayout()!!.counterMaxLength
  val currentLength = getInputField()!!.text.length
  if (maxLength > 0) {
    setActionButtonEnabled(POSITIVE, currentLength <= maxLength)
  }
}

internal fun MaterialDialog.showKeyboardIfApplicable() {
  val editText = getInputField() ?: return
  editText.postApply {
    requestFocus()
    val imm =
      windowContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
  }
}
