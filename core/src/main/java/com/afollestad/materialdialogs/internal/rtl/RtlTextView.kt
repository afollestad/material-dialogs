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
package com.afollestad.materialdialogs.internal.rtl

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.afollestad.materialdialogs.utils.setGravityStartCompat

/**
 * With our custom layout-ing, using START/END gravity doesn't work so we manually
 * set text alignment for RTL/LTR.
 *
 * @author Aidan Follestad (afollestad)
 */
class RtlTextView(
  context: Context,
  attrs: AttributeSet?
) : AppCompatTextView(context, attrs) {
  init {
    setGravityStartCompat()
  }
}
