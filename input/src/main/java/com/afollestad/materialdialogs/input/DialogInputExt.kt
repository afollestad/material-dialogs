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
@file:Suppress("unused")

package com.afollestad.materialdialogs.input

import android.annotation.SuppressLint
import android.text.InputType
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.hasActionButtons
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.google.android.material.textfield.TextInputLayout

typealias InputCallback = ((MaterialDialog, CharSequence) -> Unit)?

/**
 * Gets the input layout for the dialog if it's an input dialog.
 *
 * @throws IllegalStateException if the dialog is not an input dialog.
 */
@CheckResult fun MaterialDialog.getInputLayout(): TextInputLayout {
  return getCustomView().findViewById(R.id.md_input_layout) as? TextInputLayout
      ?: throw IllegalStateException(
          "You have not setup this dialog as an input dialog."
      )
}

/**
 * Gets the input EditText for the dialog if it's an input dialog.
 *
 * @throws IllegalStateException if the dialog is not an input dialog.
 */
@CheckResult fun MaterialDialog.getInputField(): EditText {
  return getInputLayout().editText ?: throw IllegalStateException(
      "You have not setup this dialog as an input dialog."
  )
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
 * @param allowEmpty Defaults to false. When false, the positive action button is disabled unless
 *    the input field is not empty.
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
  allowEmpty: Boolean = false,
  callback: InputCallback = null
): MaterialDialog {
  customView(R.layout.md_dialog_stub_input)
  onPreShow { showKeyboardIfApplicable() }
  if (!hasActionButtons()) {
    positiveButton(android.R.string.ok)
  }

  if (callback != null && waitForPositiveButton) {
    // Add an additional callback to invoke the input listener after the positive AB is pressed
    positiveButton { callback.invoke(this@input, getInputField().text ?: "") }
  }

  val resources = windowContext.resources
  val editText = getInputField()

  val prefillText = prefill ?: if (prefillRes != null) resources.getString(prefillRes) else ""
  if (prefillText.isNotEmpty()) {
    editText.setText(prefillText)
    onShow { editText.setSelection(prefillText.length) }
  }
  setActionButtonEnabled(
      POSITIVE,
      allowEmpty || prefillText.isNotEmpty()
  )

  editText.hint = hint ?: if (hintRes != null) resources.getString(hintRes) else null
  editText.inputType = inputType

  if (maxLength != null) {
    getInputLayout().run {
      isCounterEnabled = true
      counterMaxLength = maxLength
    }
    invalidateInputMaxLength(allowEmpty)
  }

  editText.textChanged {
    if (!allowEmpty) {
      setActionButtonEnabled(POSITIVE, it.isNotEmpty())
    }
    if (maxLength != null) {
      invalidateInputMaxLength(allowEmpty)
    }
    if (!waitForPositiveButton && callback != null) {
      // We aren't waiting for positive, so invoke every time the text changes
      callback.invoke(this, it)
    }
  }

  return this
}
