/* Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color

import android.annotation.SuppressLint
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

typealias ColorCallback = ((dialog: MaterialDialog, color: Int) -> Unit)?

/**
 * Shows a dialog with a grid of colors that the user can select from.
 *
 * @param colors The top-level array of colors integers to display in the grid.
 * @param subColors Optional sub-level colors which exist under each top-level color.
 * @param initialSelection The optionally initially selected color literal integer.
 * @param waitForPositiveButton When true, the selection isn't invoked until the user selects
 *    a color and taps on the positive action button. Defaults to true if the dialog has buttons.
 * @param selection An optional callback invoked when the user selects a color.
 */
@SuppressLint("CheckResult")
@CheckResult
fun MaterialDialog.colorChooser(
  colors: IntArray,
  subColors: Array<IntArray>? = null,
  @ColorInt initialSelection: Int? = null,
  waitForPositiveButton: Boolean = true,
  selection: ColorCallback = null
): MaterialDialog {
  customView(R.layout.md_color_chooser_grid)
  val customView = getCustomView() as RecyclerView

  if (subColors != null && colors.size != subColors.size) {
    throw IllegalStateException("Sub-colors array size should match the colors array size.")
  }

  val gridColumnCount = windowContext.resources
      .getInteger(R.integer.color_grid_column_count)
  customView.layoutManager = GridLayoutManager(
      windowContext, gridColumnCount
  )

  val adapter = ColorGridAdapter(
      dialog = this,
      colors = colors,
      subColors = subColors,
      initialSelection = initialSelection,
      waitForPositiveButton = waitForPositiveButton,
      callback = selection
  )
  customView.adapter = adapter

  if (waitForPositiveButton && selection != null) {
    setActionButtonEnabled(POSITIVE, false)
    positiveButton {
      val color = adapter.selectedColor()
      if (color != null) {
        selection.invoke(this, color)
      }
    }
  }

  return this
}
