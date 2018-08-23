/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.internal.rtl

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.afollestad.materialdialogs.utils.setGravityStartCompat

/**
 * With our custom layout-ing, using START/END gravity doesn't work so we manually
 * set text alignment for RTL/LTR.
 *
 * @author Aidan Follestad (afollestad)
 */
class RtlTextView(
  context: Context,
  attrs: AttributeSet?
) : TextView(context, attrs) {

  init {
    setGravityStartCompat()
  }
}