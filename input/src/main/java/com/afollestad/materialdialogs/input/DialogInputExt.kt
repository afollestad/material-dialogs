/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
@file:Suppress("unused")

package com.afollestad.materialdialogs.input

import android.annotation.SuppressLint
import android.support.annotation.CheckResult
import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.hasActionButtons
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.utilext.textChanged

typealias InputCallback = ((MaterialDialog, CharSequence) -> Unit)?

@CheckResult
fun MaterialDialog.getInputLayout(): TextInputLayout? {
  return this.getCustomView() as? TextInputLayout
}

@CheckResult
fun MaterialDialog.getInputField(): EditText? {
  return getInputLayout()?.editText
}

/**
 * Shows an input field as the content of the dialog. Can be used with a message and checkbox
 * prompt, but cannot be used with a list.
 *
 * @param hint The literal string to display as the input field hint.
 * @param hintRes The string resource to display as the input field hint.
 * @param prefill The literal string to pre-fill the input field with.
 * @param prefillRes The string resource to pre-fill the input field with.
 * @param inputType The input type for the input field, e.g. phone or email. Defaults to plain text.
 * @param maxLength The max length for the input field, shows a counter and disables the positive
 *    action button if the input length surpasses it.
 * @param waitForPositiveButton When true, the [callback] isn't invoked until the positive button
 *    is clicked. Otherwise, it's invoked every time the input text changes. Defaults to true if
 *    the dialog has buttons.
 * @param callback A listener to invoke for input text notifications.
 */
@SuppressLint("CheckResult")
@CheckResult
fun MaterialDialog.input(
  hint: String? = null,
  @StringRes hintRes: Int? = null,
  prefill: CharSequence? = null,
  @StringRes prefillRes: Int? = null,
  inputType: Int = InputType.TYPE_CLASS_TEXT,
  maxLength: Int? = null,
  waitForPositiveButton: Boolean = true,
  callback: InputCallback = null
): MaterialDialog {
  customView(R.layout.md_dialog_stub_input)
  onPreShow { showKeyboardIfApplicable() }
  if (!hasActionButtons()) {
    positiveButton(android.R.string.ok)
  }

  if (callback != null && waitForPositiveButton) {
    // Add an additional callback to invoke the input listener after the positive AB is pressed
    positiveButton { callback.invoke(this@input, getInputField()!!.text) }
  }

  val resources = windowContext.resources
  val editText = getInputField()!!
  editText.setText(
      prefill ?: if (prefillRes != null) resources.getString(prefillRes) else null
  )
  editText.hint = hint ?: if (hintRes != null) resources.getString(hintRes) else null
  editText.inputType = inputType

  if (maxLength != null) {
    getInputLayout()!!.apply {
      isCounterEnabled = true
      counterMaxLength = maxLength
    }
  }

  if (maxLength != null) {
    // Add text change listener to invalidate max length enabled state
    editText.textChanged { invalidateInputMaxLength() }
    invalidateInputMaxLength()
  }

  editText.textChanged {
    setActionButtonEnabled(POSITIVE, it.isNotEmpty())
    if (!waitForPositiveButton && callback != null) {
      // We aren't waiting for positive, so invoke every time the text changes
      callback.invoke(this, it)
    }
  }

  return this
}
