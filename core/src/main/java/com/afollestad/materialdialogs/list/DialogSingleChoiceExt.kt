/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("unused")

package com.afollestad.materialdialogs.list

import android.util.Log
import androidx.annotation.ArrayRes
import androidx.annotation.CheckResult
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.utils.MDUtil.assertOneSet
import com.afollestad.materialdialogs.internal.list.DialogAdapter
import com.afollestad.materialdialogs.internal.list.SingleChoiceDialogAdapter
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray

/**
 * @param res The string array resource to populate the list with.
 * @param items The literal string array to populate the list with.
 * @param initialSelection The initially selected item's index.
 * @param waitForPositiveButton When true, the [selection] listener won't be called until
 *    the positive action button is pressed. Defaults to true if the dialog has buttons.
 * @param selection A listener invoked when an item in the list is selected.
 */
@CheckResult fun MaterialDialog.listItemsSingleChoice(
  @ArrayRes res: Int? = null,
  items: List<String>? = null,
  disabledIndices: IntArray? = null,
  initialSelection: Int = -1,
  waitForPositiveButton: Boolean = true,
  selection: SingleChoiceListener = null
): MaterialDialog {
  assertOneSet("listItemsSingleChoice", items, res)
  val array = items ?: windowContext.getStringArray(res).toList()
  require(initialSelection >= -1 || initialSelection < array.size) {
    "Initial selection $initialSelection must be between -1 and " +
        "the size of your items array ${array.size}"
  }

  if (getListAdapter() != null) {
    Log.w(
        "MaterialDialogs",
        "Prefer calling updateListItemsSingleChoice(...) over listItemsSingleChoice(...) again."
    )
    return updateListItemsSingleChoice(
        res = res,
        items = items,
        disabledIndices = disabledIndices,
        selection = selection
    )
  }

  setActionButtonEnabled(POSITIVE, initialSelection > -1)
  return customListAdapter(
      SingleChoiceDialogAdapter(
          dialog = this,
          items = array,
          disabledItems = disabledIndices,
          initialSelection = initialSelection,
          waitForActionButton = waitForPositiveButton,
          selection = selection
      )
  )
}

/**
 * Updates the items, and optionally the disabled indices, of a single choice list dialog.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun MaterialDialog.updateListItemsSingleChoice(
  @ArrayRes res: Int? = null,
  items: List<String>? = null,
  disabledIndices: IntArray? = null,
  selection: SingleChoiceListener = null
): MaterialDialog {
  assertOneSet("updateListItemsSingleChoice", items, res)
  val array = items ?: windowContext.getStringArray(res).toList()
  val adapter = getListAdapter()
  check(adapter is SingleChoiceDialogAdapter) {
    "updateListItemsSingleChoice(...) can't be used before you've created a single choice list dialog."
  }
  adapter.replaceItems(array, selection)
  disabledIndices?.let(adapter::disableItems)
  return this
}

/** Checks a single or multiple choice list item. */
fun MaterialDialog.checkItem(index: Int) {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    adapter.checkItems(intArrayOf(index))
    return
  }
  throw UnsupportedOperationException(
      "Can't check item on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}

/** Unchecks a single or multiple choice list item. */
fun MaterialDialog.uncheckItem(index: Int) {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    adapter.uncheckItems(intArrayOf(index))
    return
  }
  throw UnsupportedOperationException(
      "Can't uncheck item on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}

/** Checks or unchecks a single or multiple choice list item. */
fun MaterialDialog.toggleItemChecked(index: Int) {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    adapter.toggleItems(intArrayOf(index))
    return
  }
  throw UnsupportedOperationException(
      "Can't toggle checked item on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}

/** Returns true if a single or multiple list item is checked. */
fun MaterialDialog.isItemChecked(index: Int): Boolean {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    return adapter.isItemChecked(index)
  }
  throw UnsupportedOperationException(
      "Can't check if item is checked on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}
