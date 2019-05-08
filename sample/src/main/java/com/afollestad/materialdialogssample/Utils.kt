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
package com.afollestad.materialdialogssample

import android.app.Activity
import android.content.SharedPreferences
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private var toast: Toast? = null

internal fun Activity.toast(message: CharSequence) {
  toast?.cancel()
  toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
      .apply { show() }
}

typealias PrefEditor = SharedPreferences.Editor

internal fun SharedPreferences.boolean(
  key: String,
  defaultValue: Boolean = false
): Boolean {
  return getBoolean(key, defaultValue)
}

internal inline fun SharedPreferences.commit(crossinline exec: PrefEditor.() -> Unit) {
  val editor = this.edit()
  editor.exec()
  editor.apply()
}

internal fun Int.toHex() = "#${Integer.toHexString(this)}"

internal fun Calendar.formatTime(): String {
  return SimpleDateFormat("kk:mm a", Locale.US).format(this.time)
}

internal fun Calendar.formatDate(): String {
  return SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(this.time)
}

internal fun Calendar.formatDateTime(): String {
  return SimpleDateFormat("kk:mm a, MMMM dd, yyyy", Locale.US).format(this.time)
}
