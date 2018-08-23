/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
@file:Suppress("unused")

package com.afollestad.materialdialogs.list

import android.support.annotation.ArrayRes
import android.support.annotation.CheckResult
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.assertOneSet
import com.afollestad.materialdialogs.internal.list.MultiChoiceDialogAdapter
import com.afollestad.materialdialogs.utils.getStringArray

/**
 * @param res The string array resource to populate the list with.
 * @param items The literal string array to populate the list with.
 * @param initialSelection The initially selected item indices.
 * @param waitForPositiveButton When true, the [selection] listener won't be called until
 *    the positive action button is pressed.
 * @param selection A listener invoked when an item in the list is selected.
 */
@CheckResult
fun MaterialDialog.listItemsMultiChoice(
  @ArrayRes res: Int? = null,
  items: List<String>? = null,
  disabledIndices: IntArray? = null,
  initialSelection: IntArray = IntArray(0),
  waitForPositiveButton: Boolean = true,
  selection: MultiChoiceListener = null
): MaterialDialog {
  val array = items ?: getStringArray(res)?.toList()
  val adapter = getListAdapter()

  if (adapter is MultiChoiceDialogAdapter) {
    if (array != null) {
      adapter.replaceItems(array, selection)
    }
    if (disabledIndices != null) {
      adapter.disableItems(disabledIndices)
    }
    return this
  }

  assertOneSet("listItemsMultiChoice", items, res)
  setActionButtonEnabled(POSITIVE, initialSelection.isNotEmpty())
  return customListAdapter(
      MultiChoiceDialogAdapter(
          dialog = this,
          items = array!!,
          disabledItems = disabledIndices,
          initialSelection = initialSelection,
          waitForActionButton = waitForPositiveButton,
          selection = selection
      )
  )
}
