/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.internal.list

interface DialogAdapter<IT, SL> {

  fun replaceItems(
    items: List<IT>,
    listener: SL
  )

  fun disableItems(indices: IntArray)

  fun checkItems(indices: IntArray)

  fun uncheckItems(indices: IntArray)

  fun toggleItems(indices: IntArray)

  fun checkAllItems()

  fun uncheckAllItems()

  fun toggleAllChecked()

  fun isItemChecked(index: Int): Boolean

  fun getItemCount(): Int

  fun positiveButtonClicked()
}
