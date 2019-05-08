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
package com.afollestad.materialdialogs.bottomsheets

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.appcompat.widget.AppCompatImageView

/**
 * @author Aidan Follestad (@afollestad)
 * @hide
 */
@RestrictTo(LIBRARY_GROUP)
class HeightMatchesWidthImageView(
  context: Context,
  attrs: AttributeSet?
) : AppCompatImageView(context, attrs) {

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) = super.onMeasure(widthMeasureSpec, widthMeasureSpec)
}
