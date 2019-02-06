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
package com.afollestad.materialdialogs.utils

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Point
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.WhichButton.NEGATIVE
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.callbacks.invokeAll
import com.afollestad.materialdialogs.checkbox.getCheckBoxPrompt
import com.afollestad.materialdialogs.customview.CUSTOM_VIEW_NO_HORIZONTAL_PADDING
import com.afollestad.materialdialogs.utils.MDUtil.maybeSetTextColor
import com.afollestad.materialdialogs.utils.MDUtil.resolveDrawable
import com.afollestad.materialdialogs.utils.MDUtil.resolveString
import kotlin.math.min

internal fun MaterialDialog.setWindowConstraints() {
  val win = window ?: return
  win.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)
  val wm = win.windowManager ?: return

  val display = wm.defaultDisplay
  val size = Point()
  display.getSize(size)

  val windowWidth = size.x
  val windowHeight = size.y

  context.resources.run {
    val windowVerticalPadding = getDimensionPixelSize(
        R.dimen.md_dialog_vertical_margin
    )
    val windowHorizontalPadding = getDimensionPixelSize(
        R.dimen.md_dialog_horizontal_margin
    )
    val maxWidth = getDimensionPixelSize(R.dimen.md_dialog_max_width)
    val calculatedWidth = windowWidth - windowHorizontalPadding * 2

    view.maxHeight = windowHeight - windowVerticalPadding * 2
    val lp = WindowManager.LayoutParams().apply {
      copyFrom(win.attributes)
      width = min(maxWidth, calculatedWidth)
    }
    win.attributes = lp
  }
}

internal fun MaterialDialog.setDefaults() {
  // Background color and corner radius
  var backgroundColor = resolveColor(attr = R.attr.md_background_color)
  if (backgroundColor == 0) {
    backgroundColor = resolveColor(attr = R.attr.colorBackgroundFloating)
  }
  colorBackground(color = backgroundColor)
  // Fonts
  this.titleFont = font(attr = R.attr.md_font_title)
  this.bodyFont = font(attr = R.attr.md_font_body)
  this.buttonFont = font(attr = R.attr.md_font_button)
}

@RestrictTo(Scope.LIBRARY_GROUP)
fun MaterialDialog.invalidateDividers(
  scrolledDown: Boolean,
  atBottom: Boolean
) = view.invalidateDividers(scrolledDown, atBottom)

internal fun MaterialDialog.preShow() {
  val customViewNoHorizontalPadding = config[CUSTOM_VIEW_NO_HORIZONTAL_PADDING] as? Boolean == true
  this.preShowListeners.invokeAll(this)

  this.view.run {
    if (titleLayout.shouldNotBeVisible() && !customViewNoHorizontalPadding) {
      // Reduce top and bottom padding if we have no title
      contentLayout.modifyFirstAndLastPadding(
          top = frameMarginVerticalLess,
          bottom = frameMarginVerticalLess
      )
    }
    if (getCheckBoxPrompt().isVisible()) {
      // Zero out bottom content padding if we have a checkbox prompt
      contentLayout.modifyFirstAndLastPadding(bottom = 0)
    } else if (contentLayout.haveMoreThanOneChild()) {
      contentLayout.modifyScrollViewPadding(bottom = frameMarginVerticalLess)
    }
  }
}

internal fun MaterialDialog.postShow() {
  val negativeBtn = getActionButton(NEGATIVE)
  if (negativeBtn.isVisible()) {
    negativeBtn.post { negativeBtn.requestFocus() }
    return
  }
  val positiveBtn = getActionButton(POSITIVE)
  if (positiveBtn.isVisible()) {
    positiveBtn.post { positiveBtn.requestFocus() }
    return
  }
}

internal fun MaterialDialog.populateIcon(
  imageView: ImageView,
  @DrawableRes iconRes: Int?,
  icon: Drawable?
) {
  val drawable = resolveDrawable(windowContext, res = iconRes, fallback = icon)
  if (drawable != null) {
    (imageView.parent as View).visibility = View.VISIBLE
    imageView.visibility = View.VISIBLE
    imageView.setImageDrawable(drawable)
  } else {
    imageView.visibility = View.GONE
  }
}

internal fun MaterialDialog.populateText(
  textView: TextView,
  @StringRes textRes: Int? = null,
  text: CharSequence? = null,
  @StringRes fallback: Int = 0,
  typeface: Typeface?,
  textColor: Int? = null
) {
  val value = text ?: resolveString(this, textRes, fallback)
  if (value != null) {
    (textView.parent as View).visibility = View.VISIBLE
    textView.visibility = View.VISIBLE
    textView.text = value
    if (typeface != null) {
      textView.typeface = typeface
    }
    textView.maybeSetTextColor(windowContext, textColor)
  } else {
    textView.visibility = View.GONE
  }
}

internal fun MaterialDialog.hideKeyboard() {
  val imm =
    windowContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
  val currentFocus = currentFocus
  val windowToken = if (currentFocus != null) {
    currentFocus.windowToken
  } else {
    view.windowToken
  }
  if (windowToken != null) {
    imm.hideSoftInputFromWindow(windowToken, 0)
  }
}

internal fun MaterialDialog.colorBackground(@ColorInt color: Int): MaterialDialog {
  val drawable = GradientDrawable()
  drawable.cornerRadius = dimen(attr = R.attr.md_corner_radius)
  drawable.setColor(color)
  window?.setBackgroundDrawable(drawable)
  return this
}
