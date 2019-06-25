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

import android.R.attr
import androidx.annotation.CheckResult
import com.afollestad.date.dayOfMonth
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.internal.DateTimePickerAdapter
import com.afollestad.materialdialogs.datetime.internal.TimeChangeListener
import com.afollestad.materialdialogs.datetime.utils.getDatePicker
import com.afollestad.materialdialogs.datetime.utils.getPageIndicator
import com.afollestad.materialdialogs.datetime.utils.getPager
import com.afollestad.materialdialogs.datetime.utils.getTimePicker
import com.afollestad.materialdialogs.datetime.utils.hour
import com.afollestad.materialdialogs.datetime.utils.isFutureTime
import com.afollestad.materialdialogs.datetime.utils.minute
import com.afollestad.materialdialogs.datetime.utils.toCalendar
import com.afollestad.materialdialogs.utils.MDUtil.isLandscape
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor
import java.util.Calendar

typealias DateTimeCallback = ((dialog: MaterialDialog, datetime: Calendar) -> Unit)?

/**
 * Makes the dialog a date and time picker.
 */
fun MaterialDialog.dateTimePicker(
  minDateTime: Calendar? = null,
  currentDateTime: Calendar? = null,
  requireFutureDateTime: Boolean = false,
  show24HoursView: Boolean = false,
  autoFlipToTime: Boolean = true,
  dateTimeCallback: DateTimeCallback = null
): MaterialDialog {
  customView(
      R.layout.md_datetime_picker_pager,
      noVerticalPadding = true,
      dialogWrapContent = windowContext.isLandscape()
  )

  val viewPager = getPager().apply {
    adapter = DateTimePickerAdapter()
  }
  getPageIndicator()?.run {
    attachViewPager(viewPager)
    setDotTint(resolveColor(windowContext, attr = attr.textColorPrimary))
  }

  getDatePicker().apply {
    minDateTime?.let { setMinDate(it) }
    currentDateTime?.let { setDate(it) }
    addOnDateChanged { previous, date ->
      val futureTime = isFutureTime(getDatePicker(), getTimePicker())
      setActionButtonEnabled(
          POSITIVE, !requireFutureDateTime || futureTime
      )
      if (autoFlipToTime && didDateChange(previous, date)) {
        getPager().currentItem = 1
      }
    }
  }

  getTimePicker().apply {
    setIs24HourView(show24HoursView)
    hour(currentDateTime?.get(Calendar.HOUR_OF_DAY) ?: 12)
    minute(currentDateTime?.get(Calendar.MINUTE) ?: 0)

    setOnTimeChangedListener { _, _, _ ->
      val isFutureTime = isFutureTime(getDatePicker(), this)
      setActionButtonEnabled(
          POSITIVE,
          !requireFutureDateTime || isFutureTime
      )
    }
  }

  positiveButton(android.R.string.ok) {
    val selectedTime = toCalendar(getDatePicker(), getTimePicker())
    dateTimeCallback?.invoke(it, selectedTime)
  }
  negativeButton(android.R.string.cancel)

  if (requireFutureDateTime) {
    val changeListener = TimeChangeListener(windowContext, getTimePicker()) {
      val isFutureTime = isFutureTime(getDatePicker(), it)
      setActionButtonEnabled(
          POSITIVE,
          !requireFutureDateTime || isFutureTime
      )
    }
    onDismiss { changeListener.dispose() }
  }

  return this
}

private fun didDateChange(
  from: Calendar?,
  to: Calendar
): Boolean {
  if (from == null) return false
  return from.dayOfMonth != to.dayOfMonth
}

/**
 * Gets the currently selected date and time from a date/time picker dialog.
 */
@CheckResult fun MaterialDialog.selectedDateTime(): Calendar {
  return toCalendar(getDatePicker(), getTimePicker())
}
