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
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.afollestad.materialdialogs.color.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatEditText

typealias TextListener = ((String) -> Unit)?

/** @author Aidan Follestad (@afollestad) */
class ObservableEditText(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

  private var listener: TextListener = null
  private var paused: Boolean = false

  val textOrEmpty: String
    get() = text?.toString()?.trim() ?: ""
  val textLength: Int get() = textOrEmpty.length

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    addTextChangedListener(watcher)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    removeTextChangedListener(watcher)
  }

  fun observe(listener: TextListener) {
    this.listener = listener
  }

  fun updateText(text: CharSequence) {
    paused = true
    setText(text)
  }

  fun updateText(@StringRes res: Int) {
    paused = true
    setText(res)
  }

  private val watcher = object : TextWatcher {
    override fun beforeTextChanged(
      s: CharSequence?,
      start: Int,
      count: Int,
      after: Int
    ) = Unit

    override fun onTextChanged(
      s: CharSequence,
      start: Int,
      before: Int,
      count: Int
    ) {
      if (!paused) {
        listener?.invoke(s.toString())
      }
    }

    override fun afterTextChanged(s: Editable?) {
      paused = false
    }
  }
}
