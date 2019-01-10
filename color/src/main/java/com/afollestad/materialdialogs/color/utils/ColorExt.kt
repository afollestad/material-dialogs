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
package com.afollestad.materialdialogs.color.utils

import android.graphics.Color

internal fun Int.hexValue(includeAlpha: Boolean) = if (this == 0) {
  if (includeAlpha) "00000000" else "000000"
} else {
  if (includeAlpha) {
    val result = Integer.toHexString(this)
    if (result.length == 6) {
      "00$result"
    } else {
      result
    }
  } else {
    String.format("%06X", 0xFFFFFF and this)
  }
}

internal fun String.toColor(): Int? {
  return try {
    Color.parseColor("#$this")
  } catch (_: Exception) {
    null
  }
}
