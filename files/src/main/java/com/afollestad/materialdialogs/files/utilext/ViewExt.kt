/* Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.files.utilext

import android.view.View

internal fun <T : View> T.setVisible(visible: Boolean) {
  visibility = if (visible) View.VISIBLE else View.INVISIBLE
}
