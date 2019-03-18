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
package com.afollestad.materialdialogs.datetime

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.utils.extractLocalTime
import com.afollestad.materialdialogs.datetime.utils.getTimePicker
import com.afollestad.materialdialogs.datetime.utils.hour
import com.afollestad.materialdialogs.datetime.utils.minute
import org.threeten.bp.LocalTime

typealias TimeCallback = ((dialog: MaterialDialog, time: LocalTime) -> Unit)?

/**
 * Makes the dialog a time picker.
 */
fun MaterialDialog.timePicker(
  currentTime: LocalTime? = null,
  show24HoursView: Boolean = true,
  timeCallback: TimeCallback = null
): MaterialDialog {
  customView(R.layout.md_datetime_picker_time)

  currentTime?.let {
    getTimePicker().apply {
      hour(it.hour)
      minute(it.minute)
      setIs24HourView(show24HoursView)
    }
  }

  positiveButton(android.R.string.ok) {
    timeCallback?.invoke(it, getTimePicker().extractLocalTime())
  }
  negativeButton(android.R.string.cancel)

  return this
}
