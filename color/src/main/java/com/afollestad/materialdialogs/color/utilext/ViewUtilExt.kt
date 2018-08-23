/* Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color.utilext

import android.support.annotation.DimenRes
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

internal fun <T : View> T.dimenPx(@DimenRes res: Int): Int {
  return context.resources.getDimensionPixelSize(res)
}

internal fun <T : View> T.setVisibleOrGone(visible: Boolean) {
  visibility = if (visible) VISIBLE else GONE
}
