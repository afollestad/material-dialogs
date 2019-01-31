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
package com.afollestad.materialdialogs.callbacks

import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog

/**
 * Adds a listener that's invoked right before the dialog is [MaterialDialog.show]'n. If this is called
 * multiple times, it appends additional callbacks, rather than overwriting.
 */
fun MaterialDialog.onPreShow(callback: DialogCallback): MaterialDialog {
  this.preShowListeners.add(callback)
  return this
}

/**
 * Adds a listener that's invoked when the dialog is [MaterialDialog.show]'n. If this is called
 * multiple times, it appends additional callbacks, rather than overwriting.
 *
 * If the dialog is already showing, the callback be will be invoked immediately.
 */
fun MaterialDialog.onShow(callback: DialogCallback): MaterialDialog {
  this.showListeners.add(callback)
  if (this.isShowing) {
    // Already showing, invoke now
    this.showListeners.invokeAll(this)
  }
  setOnShowListener { this.showListeners.invokeAll(this) }
  return this
}

/**
 * Adds a listener that's invoked when the dialog is [MaterialDialog.dismiss]'d. If this is called
 * multiple times, it appends additional callbacks, rather than overwriting.
 */
fun MaterialDialog.onDismiss(callback: DialogCallback): MaterialDialog {
  this.dismissListeners.add(callback)
  setOnDismissListener { dismissListeners.invokeAll(this) }
  return this
}

/**
 * Adds a listener that's invoked when the dialog is [MaterialDialog.cancel]'d. If this is called
 * multiple times, it appends additional callbacks, rather than overwriting.
 */
fun MaterialDialog.onCancel(callback: DialogCallback): MaterialDialog {
  this.cancelListeners.add(callback)
  setOnCancelListener { cancelListeners.invokeAll(this) }
  return this
}

internal fun MutableList<DialogCallback>.invokeAll(dialog: MaterialDialog) {
  for (callback in this) {
    callback.invoke(dialog)
  }
}
