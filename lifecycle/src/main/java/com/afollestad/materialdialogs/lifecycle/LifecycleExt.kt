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

package com.afollestad.materialdialogs.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog

/**
 * Attach the dialog to a lifecycle and dismiss it when the lifecycle is destroyed.
 * Uses the given [owner] lifecycle if provided, else falls back to the Context of the dialog
 * window if it can.
 *
 * @param owner Optional lifecycle owner, if its null use windowContext.
 */
fun MaterialDialog.lifecycleOwner(owner: LifecycleOwner? = null): MaterialDialog {
  val observer = DialogLifecycleObserver(::dismiss)
  val lifecycleOwner = owner ?: (windowContext as? LifecycleOwner
      ?: throw IllegalStateException(
          "$windowContext is not a LifecycleOwner."
      ))
  lifecycleOwner.lifecycle.addObserver(observer)
  return this
}
