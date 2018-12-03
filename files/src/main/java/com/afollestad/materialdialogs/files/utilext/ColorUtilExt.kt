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
package com.afollestad.materialdialogs.files.utilext

import android.content.Context
import android.widget.TextView
import androidx.annotation.AttrRes
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor

internal fun TextView?.maybeSetTextColor(
  context: Context,
  @AttrRes attrRes: Int?
) {
  if (attrRes == null) return
  val color = resolveColor(context, attr = attrRes)
  if (color != 0) {
    this?.setTextColor(color)
  }
}
