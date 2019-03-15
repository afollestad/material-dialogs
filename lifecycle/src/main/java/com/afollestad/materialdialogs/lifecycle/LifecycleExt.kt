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
package com.afollestad.materialdialogs.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog

/**
 * Implement lifecycle in dialog to avoid leaks when the activity is destroyed
 * when the dialog is showing.
 *
 * @param owner Optional lifecycle owner, if its null use windowContext.
 */
fun MaterialDialog.lifecycleOwner(owner: LifecycleOwner? = null): MaterialDialog {
    val observer = LCObserver {
        dismiss()
    }
    if (owner != null)
        owner.lifecycle.addObserver(observer)
    else
        (windowContext as? LifecycleOwner)?.lifecycle?.addObserver(observer)
    return this
}
