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

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

internal class DateTimePickerAdapter : PagerAdapter() {

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    var resId = 0
    when (position) {
      0 -> resId = R.id.datetimeDatePicker
      1 -> resId = R.id.datetimeTimePicker
    }
    return container.findViewById(resId)
  }

  override fun getCount(): Int = 2

  override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object` as View

  override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = Unit
}
