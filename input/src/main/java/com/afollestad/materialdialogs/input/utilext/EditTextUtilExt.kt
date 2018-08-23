/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.input.utilext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

typealias TextChangeCallback = (CharSequence) -> Unit

internal fun EditText.textChanged(callback: TextChangeCallback) {
  this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable) = Unit

    override fun beforeTextChanged(
      s: CharSequence,
      start: Int,
      count: Int,
      after: Int
    ) = Unit

    override fun onTextChanged(
      s: CharSequence,
      start: Int,
      before: Int,
      count: Int
    ) = callback.invoke(s)
  })
}
