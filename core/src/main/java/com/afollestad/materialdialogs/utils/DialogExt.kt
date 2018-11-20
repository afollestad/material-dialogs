/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
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
import com.afollestad.materialdialogs.callbacks.invokeAll
import com.afollestad.materialdialogs.checkbox.getCheckBoxPrompt
import com.afollestad.materialdialogs.customview.CUSTOM_VIEW_NO_PADDING

internal fun MaterialDialog.setWindowConstraints() {
  window!!.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)

  val wm = this.window!!.windowManager
  val display = wm.defaultDisplay
  val size = Point()
  display.getSize(size)
  val windowWidth = size.x
  val windowHeight = size.y

  context.resources.apply {
    val windowVerticalPadding = getDimensionPixelSize(
        R.dimen.md_dialog_vertical_margin
    )
    val windowHorizontalPadding = getDimensionPixelSize(
        R.dimen.md_dialog_horizontal_margin
    )
    val maxWidth = getDimensionPixelSize(R.dimen.md_dialog_max_width)
    val calculatedWidth = windowWidth - windowHorizontalPadding * 2

    this@setWindowConstraints.view.maxHeight = windowHeight - windowVerticalPadding * 2
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(this@setWindowConstraints.window!!.attributes)
    lp.width = Math.min(maxWidth, calculatedWidth)
    this@setWindowConstraints.window!!.attributes = lp
  }
}

internal fun MaterialDialog.setDefaults() {
  // Background color and corner radius
  var backgroundColor = getColor(attr = R.attr.md_background_color)
  if (backgroundColor == 0) {
    backgroundColor = getColor(attr = R.attr.colorBackgroundFloating)
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
  val customViewNoPadding = config[CUSTOM_VIEW_NO_PADDING] as? Boolean == true
  this.preShowListeners.invokeAll(this)

  this.view.apply {
    if (titleLayout.shouldNotBeVisible() && !customViewNoPadding) {
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

internal fun MaterialDialog.populateIcon(
  imageView: ImageView,
  @DrawableRes iconRes: Int?,
  icon: Drawable?
) {
  val drawable = getDrawable(windowContext, res = iconRes, fallback = icon)
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
  val value = text ?: Util.getString(this, textRes, fallback)
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
  window!!.setBackgroundDrawable(drawable)
  return this
}
