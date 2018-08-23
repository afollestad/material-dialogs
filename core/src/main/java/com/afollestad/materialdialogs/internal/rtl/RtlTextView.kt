/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.internal.rtl

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
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
) : AppCompatTextView(context, attrs) {

  init {
    setGravityStartCompat()
  }
}
