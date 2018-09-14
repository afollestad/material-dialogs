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
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope
import android.support.annotation.StringRes
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.assertOneSet
import com.afollestad.materialdialogs.callbacks.invokeAll
import com.afollestad.materialdialogs.checkbox.getCheckBoxPrompt

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

internal fun MaterialDialog.addContentScrollView() {
  if (this.contentScrollView == null) {
    this.contentScrollView = inflate(R.layout.md_dialog_stub_scrollview, this.view)
    this.contentScrollView!!.rootView = this.view
    this.contentScrollViewFrame = this.contentScrollView!!.getChildAt(0) as LinearLayout
    this.view.addView(this.contentScrollView, 1)
  }
}

internal fun MaterialDialog.addContentMessageView(@StringRes res: Int?, text: CharSequence?) {
  if (this.textViewMessage == null) {
    this.textViewMessage = inflate(
        R.layout.md_dialog_stub_message,
        this.contentScrollViewFrame!!
    )
    this.textViewMessage.maybeSetTextColor(windowContext, R.attr.md_color_content)
    this.contentScrollViewFrame!!.addView(this.textViewMessage)
    if (this.bodyFont != null) {
      this.textViewMessage?.typeface = this.bodyFont
    }
  }
  assertOneSet("message", text, res)
  this.textViewMessage!!.text = text ?: getString(res)
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
  @StringRes fallback: Int = 0,
  typeface: Typeface?,
  textColor: Int? = null
) {
  val value = text ?: getString(textRes, fallback)
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
