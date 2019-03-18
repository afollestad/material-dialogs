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
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

internal fun isFutureTime(
  datePicker: DatePicker,
  timePicker: TimePicker
): Boolean {
  val now = LocalDateTime.now()
  val date = extractLocalDateTime(datePicker, timePicker).toLocalDate()
  val time = extractLocalDateTime(datePicker, timePicker).toLocalTime()

  return date == now.toLocalDate() &&
      time.isAfter(now.toLocalTime()) ||
      date.isAfter(now.toLocalDate())
}

internal fun DatePicker.extractLocalDate(): LocalDate {
  return LocalDate.of(year, month.inc(), dayOfMonth)
}

internal fun TimePicker.extractLocalTime(): LocalTime {
  return LocalTime.of(hour(), minute())
}

internal fun extractLocalDateTime(
  datePicker: DatePicker,
  timePicker: TimePicker
): LocalDateTime {
  return LocalDateTime.of(
      datePicker.year,
      datePicker.month.inc(),
      datePicker.dayOfMonth,
      timePicker.hour(),
      timePicker.minute()
  )
}
