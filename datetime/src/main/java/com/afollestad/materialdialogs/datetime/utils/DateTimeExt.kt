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
package com.afollestad.materialdialogs.datetime.utils

import android.widget.DatePicker
import android.widget.TimePicker
import java.util.Calendar
import java.util.GregorianCalendar

internal fun isFutureTime(
  datePicker: DatePicker,
  timePicker: TimePicker
): Boolean {
  val now = Calendar.getInstance()
  val dateTime = toCalendar(datePicker, timePicker)
  return dateTime.timeInMillis >= now.timeInMillis
}

internal fun DatePicker.toCalendar(): Calendar {
  return GregorianCalendar(year, month.inc(), dayOfMonth, 0, 0, 1)
}

internal fun TimePicker.toCalendar(): Calendar {
  return Calendar.getInstance()
      .apply {
        set(Calendar.HOUR, hour())
        set(Calendar.MINUTE, minute())
      }
}

internal fun toCalendar(
  datePicker: DatePicker,
  timePicker: TimePicker
): Calendar {
  return GregorianCalendar(
      datePicker.year,
      datePicker.month.inc(),
      datePicker.dayOfMonth,
      timePicker.hour(),
      timePicker.minute()
  )
}
