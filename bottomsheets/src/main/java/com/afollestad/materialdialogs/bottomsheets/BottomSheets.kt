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

package com.afollestad.materialdialogs.bottomsheets

import androidx.annotation.CheckResult
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet.Companion.LAYOUT_PEEK_CHANGE_DURATION_MS
import com.afollestad.materialdialogs.internal.list.DialogAdapter
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getListAdapter
import com.afollestad.materialdialogs.utils.MDUtil.assertOneSet
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import kotlin.math.min

/** Expands the bottom sheet, so that it's at its maximum height. */
fun MaterialDialog.expandBottomSheet(): MaterialDialog {
  check(dialogBehavior is BottomSheet) {
    "This dialog is not a bottom sheet dialog."
  }
  (dialogBehavior as BottomSheet).bottomSheetBehavior?.let {
    it.state = STATE_EXPANDED
  }
  return this
}

/** Collapses the bottom sheet, so that it's at its peek height. */
fun MaterialDialog.collapseBottomSheet(): MaterialDialog {
  check(dialogBehavior is BottomSheet) {
    "This dialog is not a bottom sheet dialog."
  }
  (dialogBehavior as BottomSheet).bottomSheetBehavior?.let {
    it.state = STATE_COLLAPSED
  }
  return this
}

/**
 * Changes the bottom sheet's peek height, animating it from the previous value if the dialog
 * is currently shown.
 */
fun MaterialDialog.setPeekHeight(
  @Px literal: Int? = null,
  @DimenRes res: Int? = null
): MaterialDialog {
  check(dialogBehavior is BottomSheet) {
    "This dialog is not a bottom sheet dialog."
  }
  assertOneSet("setPeekHeight", literal, res)

  val bottomSheet = (dialogBehavior as BottomSheet)
  val literalOrRes = literal ?: context.resources.getDimensionPixelSize(res!!)
  val destinationPeekHeight = if (bottomSheet.maxPeekHeight > 0) {
    min(bottomSheet.maxPeekHeight, literalOrRes)
  } else {
    literalOrRes
  }
  require(destinationPeekHeight > 0) { "Peek height must be > 0." }

  bottomSheet.defaultPeekHeight = destinationPeekHeight
  val bottomSheetBehavior = bottomSheet.bottomSheetBehavior
  if (isShowing) {
    bottomSheetBehavior?.animatePeekHeight(
        view = this.view,
        dest = destinationPeekHeight,
        duration = LAYOUT_PEEK_CHANGE_DURATION_MS
    )
  } else {
    bottomSheetBehavior!!.peekHeight = destinationPeekHeight
  }
  return this
}

typealias GridItemListener<IT> =
    ((dialog: MaterialDialog, index: Int, item: IT) -> Unit)?

/**
 * Populates the bottom sheet with a grid of items that have icon and text.
 */
@CheckResult fun <IT : GridItem> MaterialDialog.gridItems(
  items: List<IT>,
  @IntegerRes customGridWidth: Int? = null,
  disabledIndices: IntArray? = null,
  waitForPositiveButton: Boolean = true,
  selection: GridItemListener<IT> = null
): MaterialDialog {
  if (getListAdapter() != null) {
    return updateGridItems(
        items = items,
        disabledIndices = disabledIndices
    )
  }

  val gridWidth = windowContext.resources.getInteger(customGridWidth ?: R.integer.md_grid_width)
  val layoutManager = GridLayoutManager(windowContext, gridWidth)
  return customListAdapter(
      adapter = GridIconDialogAdapter(
          dialog = this,
          items = items,
          disabledItems = disabledIndices,
          waitForPositiveButton = waitForPositiveButton,
          selection = selection
      ),
      layoutManager = layoutManager
  )
}

/**
 * Updates the grid items, and optionally the disabled indices, for a bottom sheet.
 *
 * @author Aidan Follestad (@afollestad)
 */
fun MaterialDialog.updateGridItems(
  items: List<GridItem>,
  disabledIndices: IntArray? = null
): MaterialDialog {
  val adapter = getListAdapter()
  check(adapter != null) {
    "updateGridItems(...) can't be used before you've created a bottom sheet grid dialog."
  }
  if (adapter is DialogAdapter<*, *>) {
    @Suppress("UNCHECKED_CAST")
    (adapter as DialogAdapter<GridItem, *>).replaceItems(items)

    if (disabledIndices != null) {
      adapter.disableItems(disabledIndices)
    }
  }
  return this
}
