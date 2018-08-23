/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.internal.button

import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import android.view.Gravity.CENTER
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.R.attr
import com.afollestad.materialdialogs.Theme.Companion.inferTheme
import com.afollestad.materialdialogs.Theme.LIGHT
import com.afollestad.materialdialogs.utils.dimenPx
import com.afollestad.materialdialogs.utils.getColor
import com.afollestad.materialdialogs.utils.getDrawable
import com.afollestad.materialdialogs.utils.setGravityEndCompat
import com.afollestad.materialdialogs.utils.updatePadding

/**
 * Represents an action button in a dialog, positive, negative, or neutral. Handles switching
 * out its selector, padding, and text alignment based on whether buttons are in stacked mode or not.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class DialogActionButton(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

  private val paddingDefault = dimenPx(R.dimen.md_action_button_padding_horizontal)
  private val paddingStacked = dimenPx(R.dimen.md_stacked_action_button_padding_horizontal)

  private var enabledColor: Int = 0
  private var disabledColor: Int = 0

  init {
    isClickable = true
    isFocusable = true
  }

  fun update(
    baseContext: Context,
    appContext: Context,
    stacked: Boolean
  ) {
    // Text color
    val theme = inferTheme(appContext)
    enabledColor = getColor(appContext, attr = attr.colorAccent)
    val disabledColorRes =
      if (theme == LIGHT) R.color.md_disabled_text_light_theme
      else R.color.md_disabled_text_dark_theme
    disabledColor = getColor(baseContext, res = disabledColorRes)
    setTextColor(enabledColor)

    // Selector
    val selectorAttr = if (stacked) R.attr.md_item_selector else R.attr.md_button_selector
    background = getDrawable(baseContext, attr = selectorAttr)

    // Padding
    val sidePadding = if (stacked) paddingStacked else paddingDefault
    updatePadding(left = sidePadding, right = sidePadding)

    // Text alignment
    if (stacked) setGravityEndCompat()
    else gravity = CENTER

    // Invalidate in case enabled state was changed before this method executed
    isEnabled = isEnabled
  }

  override fun setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    setTextColor(if (enabled) enabledColor else disabledColor)
  }
}
