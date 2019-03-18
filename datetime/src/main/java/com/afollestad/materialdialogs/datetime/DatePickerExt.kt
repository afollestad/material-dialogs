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
import com.afollestad.materialdialogs.datetime.utils.extractLocalDate
import com.afollestad.materialdialogs.datetime.utils.getDatePicker
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

typealias DateCallback = ((dialog: MaterialDialog, date: LocalDate) -> Unit)?

/**
 * Makes the dialog a date picker.
 */
fun MaterialDialog.datePicker(
  minDate: LocalDate? = null,
  currentDate: LocalDate? = null,
  dateCallback: DateCallback = null
): MaterialDialog {
  customView(R.layout.md_datetime_picker_date)

  minDate?.let {
    getDatePicker().minDate = it.atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
  }
  currentDate?.let {
    getDatePicker().init(
        it.year,
        it.monthValue.inc(),
        it.dayOfMonth,
        null
    )
  }

  positiveButton(android.R.string.ok) {
    dateCallback?.invoke(it, getDatePicker().extractLocalDate())
  }
  negativeButton(android.R.string.cancel)

  return this
}
