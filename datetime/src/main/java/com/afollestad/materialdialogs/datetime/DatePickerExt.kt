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

package com.afollestad.materialdialogs.datetime

import androidx.annotation.CheckResult
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.utils.getDatePicker
import com.afollestad.materialdialogs.datetime.utils.toCalendar
import java.util.Calendar

/**
 * Makes the dialog a date picker.
 */
fun MaterialDialog.datePicker(
  minDate: Calendar? = null,
  currentDate: Calendar? = null,
  dateCallback: DateTimeCallback = null
): MaterialDialog {
  customView(R.layout.md_datetime_picker_date)

  minDate?.let {
    getDatePicker().minDate = minDate.timeInMillis
  }
  currentDate?.let {
    getDatePicker().init(
        it.get(Calendar.YEAR),
        it.get(Calendar.MONTH).inc(),
        it.get(Calendar.DAY_OF_MONTH),
        null
    )
  }

  positiveButton(android.R.string.ok) {
    dateCallback?.invoke(it, getDatePicker().toCalendar())
  }
  negativeButton(android.R.string.cancel)

  return this
}

/**
 * Gets the currently selected date from a date picker dialog.
 */
@CheckResult fun MaterialDialog.selectedDate(): Calendar {
  return getDatePicker().toCalendar()
}
