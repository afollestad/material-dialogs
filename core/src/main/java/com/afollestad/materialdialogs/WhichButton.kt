/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs

import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout.Companion.INDEX_NEGATIVE
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout.Companion.INDEX_NEUTRAL
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout.Companion.INDEX_POSITIVE

enum class WhichButton(val index: Int) {
  POSITIVE(INDEX_POSITIVE),
  NEGATIVE(INDEX_NEGATIVE),
  NEUTRAL(INDEX_NEUTRAL);

  companion object {
    fun fromIndex(index: Int) = when (index) {
      INDEX_POSITIVE -> POSITIVE
      INDEX_NEGATIVE -> NEGATIVE
      INDEX_NEUTRAL -> NEUTRAL
      else -> throw IndexOutOfBoundsException("$index is not an action button index.")
    }
  }
}
