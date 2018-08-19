/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.utils

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.callbacks.invokeAll
import com.afollestad.materialdialogs.checkbox.getCheckBoxPrompt

@CheckResult
internal fun MaterialDialog.colorBackground(@ColorInt color: Int): MaterialDialog {
  val drawable = GradientDrawable()
  drawable.cornerRadius = dimen(attr = R.attr.md_corner_radius)
  drawable.setColor(color)
  window!!.setBackgroundDrawable(drawable)
  return this
}

internal fun MaterialDialog.setWindowConstraints() {
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
  val backgroundColor = getColor(attr = R.attr.colorBackgroundFloating)
  colorBackground(color = backgroundColor)
}

internal fun MaterialDialog.preShow() {
  this.preShowListeners.invokeAll(this)
  this.view.apply {
    if (titleLayout.shouldNotBeVisible()) {
      // Reduce top and bottom padding if we have no title
      contentView.updatePadding(
          top = frameMarginVerticalLess,
          bottom = frameMarginVerticalLess
      )
    }
    if (getCheckBoxPrompt().isVisible()) {
      // Zero out bottom content padding if we have a checkbox prompt
      contentView.updatePadding(bottom = 0)
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
  @StringRes fallback: Int = 0
) {
  val value = text ?: getString(textRes, fallback)
  if (value != null) {
    (textView.parent as View).visibility = View.VISIBLE
    textView.visibility = View.VISIBLE
    textView.text = value
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