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

import android.widget.DatePicker
import android.widget.TimePicker
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.utils.hour
import com.afollestad.materialdialogs.datetime.utils.minute
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor
import com.afollestad.viewpagerdots.DotsIndicator
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

typealias DateCallback = ((dialog: MaterialDialog, date: LocalDate) -> Unit)?
typealias TimeCallback = ((dialog: MaterialDialog, time: LocalTime) -> Unit)?
typealias DateTimeCallback = ((dialog: MaterialDialog, datetime: LocalDateTime) -> Unit)?

fun MaterialDialog.datePicker(
  minDate: LocalDate? = null,
  currentDate: LocalDate? = null,
  dateCallback: DateCallback = null
): MaterialDialog {
  customView(R.layout.md_datetime_picker_date)

  minDate?.let { getDatePicker().minDate = it.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() }
  currentDate?.let { getDatePicker().init(it.year, it.monthValue.inc(), it.dayOfMonth, null) }

  positiveButton(android.R.string.ok) {
    dateCallback?.invoke(it, extractLocalDate(getDatePicker()))
  }
  negativeButton(android.R.string.cancel)

  return this
}

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
    timeCallback?.invoke(it, extractLocalTime(getTimePicker()))
  }
  negativeButton(android.R.string.cancel)

  return this
}

fun MaterialDialog.dateTimePicker(
  minDateTime: LocalDateTime? = null,
  currentDateTime: LocalDateTime? = null,
  requireFutureDateTime: Boolean = false,
  show24HoursView: Boolean = true,
  dateTimeCallback: DateTimeCallback = null
): MaterialDialog {
  customView(R.layout.md_datetime_picker_pager, noVerticalPadding = true)

  val viewPager = getPager()
  viewPager.adapter = DateTimePickerAdapter()

  getPageIndicator()?.run {
    attachViewPager(viewPager)
    setDotTint(resolveColor(windowContext, attr = android.R.attr.textColorPrimary))
  }

  minDateTime?.let { getDatePicker().minDate = it.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() }
  currentDateTime.let {
    getDatePicker().apply {
      init(it?.year ?: year, it?.monthValue?.inc() ?: month, it?.dayOfMonth ?: dayOfMonth) { _, _, _, _ ->
        setActionButtonEnabled(POSITIVE, !requireFutureDateTime || isFutureTime(this, getTimePicker()))
      }
    }
    getTimePicker().apply {
      if (it != null) {
        hour(it.hour)
        minute(it.minute)
      }

      setIs24HourView(show24HoursView)

      setOnTimeChangedListener { _, _, _ ->
        setActionButtonEnabled(POSITIVE, !requireFutureDateTime || isFutureTime(getDatePicker(), this))
      }
    }
  }

  setActionButtonEnabled(POSITIVE, !requireFutureDateTime || isFutureTime(getDatePicker(), getTimePicker()))
  positiveButton(android.R.string.ok) {
    dateTimeCallback?.invoke(it, extractLocalDateTime(getDatePicker(), getTimePicker()))
  }
  negativeButton(android.R.string.cancel)

  return this
}

private fun isFutureTime(datePicker: DatePicker, timePicker: TimePicker): Boolean {
  val dateTime = LocalDateTime.now()

  val date = extractLocalDateTime(datePicker, timePicker).toLocalDate()
  val time = extractLocalDateTime(datePicker, timePicker).toLocalTime()

  return date == dateTime.toLocalDate() && time.isAfter(dateTime.toLocalTime()) || date.isAfter(dateTime.toLocalDate())
}

private fun extractLocalDate(datePicker: DatePicker): LocalDate {
  return LocalDate.of(datePicker.year, datePicker.month.inc(), datePicker.dayOfMonth)
}

private fun extractLocalTime(timePicker: TimePicker): LocalTime {
  return LocalTime.of(timePicker.hour(), timePicker.minute())
}

private fun extractLocalDateTime(datePicker: DatePicker, timePicker: TimePicker): LocalDateTime {
  return LocalDateTime.of(datePicker.year, datePicker.month.inc(), datePicker.dayOfMonth, timePicker.hour(), timePicker.minute())
}

private fun MaterialDialog.getDatePicker() = findViewById<DatePicker>(R.id.datetimeDatePicker)

private fun MaterialDialog.getTimePicker() = findViewById<TimePicker>(R.id.datetimeTimePicker)

private fun MaterialDialog.getPager() = findViewById<ViewPager>(R.id.dateTimePickerPager)

private fun MaterialDialog.getPageIndicator() = findViewById<DotsIndicator?>(R.id.datetimePickerPagerDots)
