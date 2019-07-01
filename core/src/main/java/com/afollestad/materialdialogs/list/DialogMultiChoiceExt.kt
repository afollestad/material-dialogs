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
import com.afollestad.materialdialogs.internal.list.MultiChoiceDialogAdapter
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray

/**
 * @param res The string array resource to populate the list with.
 * @param items The literal string array to populate the list with.
 * @param initialSelection The initially selected item indices.
 * @param waitForPositiveButton When true, the [selection] listener won't be called until
 *    the positive action button is pressed.
 * @param allowEmptySelection When true, the dialog allows to select 0 items as well
 *    otherwise at least one item must be selected
 * @param selection A listener invoked when an item in the list is selected.
 */
@CheckResult fun MaterialDialog.listItemsMultiChoice(
  @ArrayRes res: Int? = null,
  items: List<String>? = null,
  disabledIndices: IntArray? = null,
  initialSelection: IntArray = IntArray(0),
  waitForPositiveButton: Boolean = true,
  allowEmptySelection: Boolean = false,
  selection: MultiChoiceListener = null
): MaterialDialog {
  assertOneSet("listItemsMultiChoice", items, res)
  val array = items ?: windowContext.getStringArray(res).toList()

  if (getListAdapter() != null) {
    Log.w(
        "MaterialDialogs",
        "Prefer calling updateListItemsMultiChoice(...) over listItemsMultiChoice(...) again."
    )
    return updateListItemsMultiChoice(
        res = res,
        items = items,
        disabledIndices = disabledIndices,
        selection = selection
    )
  }

  setActionButtonEnabled(POSITIVE, allowEmptySelection || initialSelection.isNotEmpty())
  return customListAdapter(
      MultiChoiceDialogAdapter(
          dialog = this,
          items = array,
          disabledItems = disabledIndices,
          initialSelection = initialSelection,
          waitForActionButton = waitForPositiveButton,
          allowEmptySelection = allowEmptySelection,
          selection = selection
      )
  )
}

/**
 * Updates the items, and optionally the disabled indices, of a plain list dialog.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun MaterialDialog.updateListItemsMultiChoice(
  @ArrayRes res: Int? = null,
  items: List<String>? = null,
  disabledIndices: IntArray? = null,
  selection: MultiChoiceListener = null
): MaterialDialog {
  assertOneSet("updateListItemsMultiChoice", items, res)
  val array = items ?: windowContext.getStringArray(res).toList()
  val adapter = getListAdapter()
  check(adapter is MultiChoiceDialogAdapter) {
    "updateListItemsMultiChoice(...) can't be used before you've created a multiple choice list dialog."
  }
  adapter.replaceItems(array, selection)
  disabledIndices?.let(adapter::disableItems)
  return this
}

/** Checks a set of multiple choice list items. */
fun MaterialDialog.checkItems(indices: IntArray) {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    adapter.checkItems(indices)
    return
  }
  throw UnsupportedOperationException(
      "Can't check items on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}

/** Unchecks a set of multiple choice list items. */
fun MaterialDialog.uncheckItems(indices: IntArray) {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    adapter.uncheckItems(indices)
    return
  }
  throw UnsupportedOperationException(
      "Can't uncheck items on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}

/** Toggles the checked state of a set of multiple choice list items. */
fun MaterialDialog.toggleItemsChecked(indices: IntArray) {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    adapter.toggleItems(indices)
    return
  }
  throw UnsupportedOperationException(
      "Can't toggle checked items on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}

/** Checks all multiple choice list items. */
fun MaterialDialog.checkAllItems() {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    adapter.checkAllItems()
    return
  }
  throw UnsupportedOperationException(
      "Can't check all items on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}

/** Unchecks all single or multiple choice list items. */
fun MaterialDialog.uncheckAllItems() {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    adapter.uncheckAllItems()
    return
  }
  throw UnsupportedOperationException(
      "Can't uncheck all items on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}

/** Toggles the checked state of all multiple choice list items. */
fun MaterialDialog.toggleAllItemsChecked() {
  val adapter = getListAdapter()
  if (adapter is DialogAdapter<*, *>) {
    adapter.toggleAllChecked()
    return
  }
  throw UnsupportedOperationException(
      "Can't uncheck all items on adapter: ${adapter?.javaClass?.name ?: "null"}"
  )
}
