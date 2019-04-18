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
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.internal.TimeChangeListener
import com.afollestad.materialdialogs.datetime.utils.getTimePicker
import com.afollestad.materialdialogs.datetime.utils.hour
import com.afollestad.materialdialogs.datetime.utils.isFutureTime
import com.afollestad.materialdialogs.datetime.utils.minute
import com.afollestad.materialdialogs.datetime.utils.toCalendar
import com.afollestad.materialdialogs.utils.MDUtil.isLandscape
import java.util.Calendar

/**
 * Makes the dialog a time picker.
 */
fun MaterialDialog.timePicker(
  currentTime: Calendar? = null,
  requireFutureTime: Boolean = false,
  show24HoursView: Boolean = true,
  timeCallback: DateTimeCallback = null
): MaterialDialog {
  customView(
      R.layout.md_datetime_picker_time,
      noVerticalPadding = true,
      dialogWrapContent = windowContext.isLandscape()
  )

  with(getTimePicker()) {
    setIs24HourView(show24HoursView)
    if (currentTime != null) {
      hour(currentTime.get(Calendar.HOUR_OF_DAY))
      minute(currentTime.get(Calendar.MINUTE))
    }
    setOnTimeChangedListener { _, _, _ ->
      val isFutureTime = isFutureTime()
      setActionButtonEnabled(
          POSITIVE,
          !requireFutureTime || isFutureTime
      )
    }
  }

  positiveButton(android.R.string.ok) {
    timeCallback?.invoke(it, getTimePicker().toCalendar())
  }
  negativeButton(android.R.string.cancel)

  if (requireFutureTime) {
    val changeListener = TimeChangeListener(windowContext, getTimePicker()) {
      val isFutureTime = it.isFutureTime()
      setActionButtonEnabled(
          POSITIVE,
          !requireFutureTime || isFutureTime
      )
    }
    onDismiss { changeListener.dispose() }
  }

  return this
}

/**
 * Gets the currently selected time from a time picker dialog.
 */
@CheckResult fun MaterialDialog.selectedTime(): Calendar {
  return getTimePicker().toCalendar()
}
