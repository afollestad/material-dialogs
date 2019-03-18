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
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.internal.DateTimePickerAdapter
import com.afollestad.materialdialogs.datetime.utils.extractLocalDateTime
import com.afollestad.materialdialogs.datetime.utils.getDatePicker
import com.afollestad.materialdialogs.datetime.utils.getPageIndicator
import com.afollestad.materialdialogs.datetime.utils.getPager
import com.afollestad.materialdialogs.datetime.utils.getTimePicker
import com.afollestad.materialdialogs.datetime.utils.hour
import com.afollestad.materialdialogs.datetime.utils.isFutureTime
import com.afollestad.materialdialogs.datetime.utils.minute
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

typealias DateTimeCallback = ((dialog: MaterialDialog, datetime: LocalDateTime) -> Unit)?

/**
 * Makes the dialog a date and time picker.
 */
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
    setDotTint(resolveColor(windowContext, attr = attr.textColorPrimary))
  }

  minDateTime?.let {
    getDatePicker().minDate = it.atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
  }
  currentDateTime.let {
    getDatePicker().apply {
      init(
          it?.year ?: year,
          it?.monthValue?.inc() ?: month,
          it?.dayOfMonth ?: dayOfMonth
      ) { _, _, _, _ ->
        val futureTime = isFutureTime(this, getTimePicker())
        setActionButtonEnabled(
            POSITIVE, !requireFutureDateTime || futureTime
        )
      }
    }
    getTimePicker().apply {
      if (it != null) {
        hour(it.hour)
        minute(it.minute)
      }

      setIs24HourView(show24HoursView)

      setOnTimeChangedListener { _, _, _ ->
        val isFutureTime = isFutureTime(getDatePicker(), this)
        setActionButtonEnabled(
            POSITIVE,
            !requireFutureDateTime || isFutureTime
        )
      }
    }
  }

  val futureTime = isFutureTime(getDatePicker(), getTimePicker())
  setActionButtonEnabled(
      POSITIVE,
      !requireFutureDateTime || futureTime
  )
  positiveButton(android.R.string.ok) {
    val selectedTime = extractLocalDateTime(getDatePicker(), getTimePicker())
    dateTimeCallback?.invoke(it, selectedTime)
  }
  negativeButton(android.R.string.cancel)

  return this
}

/**
 * Gets the currently selected date and time from a date/time picker dialog.
 */
@CheckResult fun MaterialDialog.selectedDateTime(): LocalDateTime {
  return extractLocalDateTime(getDatePicker(), getTimePicker())
}
