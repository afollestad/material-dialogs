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
@file:Suppress("DEPRECATION")

package com.afollestad.materialdialogs.datetime.utils

import android.os.Build
import android.widget.TimePicker
import androidx.viewpager.widget.ViewPager
import com.afollestad.date.DatePicker
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.R
import com.afollestad.viewpagerdots.DotsIndicator

internal fun TimePicker.hour(): Int = if (isNougat()) hour else currentHour

internal fun TimePicker.minute(): Int = if (isNougat()) minute else currentMinute

internal fun TimePicker.hour(value: Int) {
  if (isNougat()) hour = value else currentHour = value
}

internal fun TimePicker.minute(value: Int) {
  if (isNougat()) minute = value else currentMinute = value
}

internal fun MaterialDialog.getDatePicker() = findViewById<DatePicker>(R.id.datetimeDatePicker)

internal fun MaterialDialog.getTimePicker() = findViewById<TimePicker>(R.id.datetimeTimePicker)

internal fun MaterialDialog.getPager() = findViewById<ViewPager>(R.id.dateTimePickerPager)

internal fun MaterialDialog.getPageIndicator() =
  findViewById<DotsIndicator?>(R.id.datetimePickerPagerDots)

private fun isNougat() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
