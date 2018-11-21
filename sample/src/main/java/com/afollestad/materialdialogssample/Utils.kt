/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogssample

import android.app.Activity
import android.content.SharedPreferences
import android.widget.Toast

var toast: Toast? = null

internal fun Activity.toast(message: CharSequence) {
  toast?.cancel()
  toast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply { show() }
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
