/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.internal.rtl

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.LEFT
import android.view.Gravity.RIGHT
import android.widget.TextView
import com.afollestad.materialdialogs.utils.isRtl

/**
 * With our custom layout-ing, using START/END gravity doesn't work so we manually
 * set text alignment for RTL/LTR.
 *
 * @author Aidan Follestad (afollestad)
 */
@SuppressLint("RtlHardcoded")
class RtlTextView(
  context: Context,
  attrs: AttributeSet?
) : TextView(context, attrs) {

  init {
    if (isRtl()) {
      this.gravity = RIGHT
    } else {
      this.gravity = LEFT
    }
  }
}