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
package com.afollestad.materialdialogs.input

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled

internal fun MaterialDialog.invalidateInputMaxLength(allowEmpty: Boolean) {
  val currentLength = getInputField().text?.length ?: 0
  if (!allowEmpty && currentLength == 0) {
    return
  }
  val maxLength = getInputLayout().counterMaxLength
  if (maxLength > 0) {
    setActionButtonEnabled(POSITIVE, currentLength <= maxLength)
  }
}

internal fun MaterialDialog.showKeyboardIfApplicable() {
  getInputField().postRun {
    requestFocus()
    val imm =
      windowContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
  }
}

internal inline fun <T : View> T.postRun(crossinline exec: T.() -> Unit) = this.post {
  this.exec()
}
