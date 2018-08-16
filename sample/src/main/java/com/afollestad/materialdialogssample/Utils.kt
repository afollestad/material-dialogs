package com.afollestad.materialdialogssample

import android.app.Activity
import android.content.SharedPreferences
import android.widget.Toast

var toast: Toast? = null

internal fun Activity.toast(message: CharSequence) {
  toast?.cancel()
  toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
  toast!!.show()
}

typealias PrefEditor = SharedPreferences.Editor

internal fun SharedPreferences.boolean(
  key: String,
  defaultValue: Boolean = false
): Boolean {
  return getBoolean(key, defaultValue)
}

internal inline fun SharedPreferences.apply(crossinline exec: PrefEditor.() -> Unit) {
  val editor = this.edit()
  editor.exec()
  editor.apply()
}

internal fun Int.toHex() = String.format("#%06X", 0xFFFFFF and this)