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
package com.afollestad.materialdialogs.internal.button

import android.content.Context
import android.content.res.ColorStateList.valueOf
import android.graphics.drawable.RippleDrawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.AttributeSet
import android.view.Gravity.CENTER
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.inferThemeIsLight
import com.afollestad.materialdialogs.utils.MDUtil.ifNotZero
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor
import com.afollestad.materialdialogs.utils.MDUtil.resolveDrawable
import com.afollestad.materialdialogs.utils.MDUtil.resolveInt
import com.afollestad.materialdialogs.utils.adjustAlpha
import com.afollestad.materialdialogs.utils.setGravityEndCompat

/**
 * Represents an action button in a dialog, positive, negative, or neutral. Handles switching
 * out its selector, padding, and text alignment based on whether buttons are in stacked mode or not.
 *
 * @author Aidan Follestad (afollestad)
 */
class DialogActionButton(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {
  companion object {
    private const val CASING_UPPER = 1
  }

  private var enabledColor: Int = 0
  private var disabledColor: Int = 0

  init {
    isClickable = true
    isFocusable = true
  }

  internal fun update(
    baseContext: Context,
    appContext: Context,
    stacked: Boolean
  ) {
    // Casing
    val casing = resolveInt(
        context = appContext,
        attr = R.attr.md_button_casing,
        defaultValue = CASING_UPPER
    )
    setSupportAllCaps(casing == CASING_UPPER)

    // Text color
    val isLightTheme = inferThemeIsLight(appContext)
    enabledColor = resolveColor(appContext, attr = R.attr.md_color_button_text) {
      resolveColor(appContext, attr = R.attr.colorPrimary)
    }
    val disabledColorRes =
      if (isLightTheme) R.color.md_disabled_text_light_theme
      else R.color.md_disabled_text_dark_theme
    disabledColor = resolveColor(baseContext, res = disabledColorRes)
    setTextColor(enabledColor)

    // Selector
    val bgDrawable = resolveDrawable(baseContext, attr = R.attr.md_button_selector)
    if (SDK_INT >= LOLLIPOP && bgDrawable is RippleDrawable) {
      resolveColor(context = baseContext, attr = R.attr.md_ripple_color) {
        resolveColor(appContext, attr = R.attr.colorPrimary).adjustAlpha(.12f)
      }.ifNotZero {
        bgDrawable.setColor(valueOf(it))
      }
    }
    background = bgDrawable

    // Text alignment
    if (stacked) setGravityEndCompat()
    else gravity = CENTER

    // Invalidate in case enabled state was changed before this method executed
    isEnabled = isEnabled
  }

  fun updateTextColor(@ColorInt color: Int) {
    enabledColor = color
    isEnabled = isEnabled
  }

  override fun setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    setTextColor(if (enabled) enabledColor else disabledColor)
  }
}
