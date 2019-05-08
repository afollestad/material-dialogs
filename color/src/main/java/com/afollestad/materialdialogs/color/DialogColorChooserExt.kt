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
package com.afollestad.materialdialogs.color

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.Color.argb
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SeekBar
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.NEGATIVE
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.color.utils.below
import com.afollestad.materialdialogs.color.utils.changeHeight
import com.afollestad.materialdialogs.color.utils.clearTopMargin
import com.afollestad.materialdialogs.color.utils.onPageSelected
import com.afollestad.materialdialogs.color.view.PreviewFrameView
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.internal.list.DialogRecyclerView
import com.afollestad.materialdialogs.utils.MDUtil.isColorDark
import com.afollestad.materialdialogs.utils.MDUtil.isLandscape
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor
import com.afollestad.materialdialogs.utils.invalidateDividers
import com.afollestad.viewpagerdots.DotsIndicator

typealias ColorCallback = ((dialog: MaterialDialog, color: Int) -> Unit)?

private const val ALPHA_SOLID = 255

private const val KEY_CUSTOM_PAGE_VIEW_SET = "color_custom_page_view_set"
private const val KEY_CUSTOM_ARGB = "color_custom_argb"
private const val KEY_SHOW_ALPHA = "color_show_alpha"
private const val KEY_WAIT_FOR_POSITIVE = "color_wait_for_positive"
private const val KEY_CHANGE_ACTION_BUTTON_COLOR = "color_change_action_button_color"

/**
 * Shows a dialog with a grid of colors that the user can select from.
 *
 * @param colors The top-level array of colors integers to display in the grid.
 * @param subColors Optional sub-level colors which exist under each top-level color.
 * @param initialSelection The optionally initially selected color literal integer.
 * @param waitForPositiveButton When true, the selection isn't invoked until the user selects
 *    a color and taps on the positive action button. Defaults to true if the dialog has buttons.
 * @param allowCustomArgb Allows selection of a color with an (A)RGB slider view
 * @param showAlphaSelector Allows selection alpha (transparency) values in (A)RGB mode.
 * @param changeActionButtonsColor When true, action button colors will match the selected color.
 * @param selection An optional callback invoked when the user selects a color.
 */
@SuppressLint("CheckResult")
@CheckResult
fun MaterialDialog.colorChooser(
  colors: IntArray,
  subColors: Array<IntArray>? = null,
  @ColorInt initialSelection: Int? = null,
  waitForPositiveButton: Boolean = true,
  allowCustomArgb: Boolean = false,
  showAlphaSelector: Boolean = false,
  changeActionButtonsColor: Boolean = false,
  selection: ColorCallback = null
): MaterialDialog {
  config.run {
    set(KEY_WAIT_FOR_POSITIVE, waitForPositiveButton)
    set(KEY_CUSTOM_ARGB, allowCustomArgb)
    set(KEY_SHOW_ALPHA, showAlphaSelector)
    set(KEY_CHANGE_ACTION_BUTTON_COLOR, changeActionButtonsColor)
  }

  if (!allowCustomArgb) {
    customView(R.layout.md_color_chooser_base_grid)
    setupGridLayout(
        colors = colors,
        subColors = subColors,
        initialSelection = initialSelection,
        waitForPositiveButton = waitForPositiveButton,
        selection = selection,
        allowCustomArgb = allowCustomArgb
    )
  } else {
    customView(R.layout.md_color_chooser_base_pager, noVerticalPadding = true)

    val viewPager = getPager()
    viewPager.adapter = ColorPagerAdapter()
    viewPager.onPageSelected { pageIndex ->
      setActionButtonEnabled(POSITIVE, selectedColor(allowCustomArgb) != null)
      val pageView = getPageCustomView() ?: return@onPageSelected
      val hexValueView = pageView.findViewById<EditText>(R.id.hexValueView)

      if (pageIndex == 0) {
        getCustomView()
            .findViewById<DialogRecyclerView>(R.id.colorPresetGrid)
            .invalidateDividers()
        val imm =
          context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(hexValueView.windowToken, 0)
      } else {
        invalidateDividers(showTop = false, showBottom = false)
      }
    }

    getPageIndicator()?.run {
      attachViewPager(viewPager)
      setDotTint(resolveColor(windowContext, attr = android.R.attr.textColorPrimary))
    }
    setupGridLayout(
        colors = colors,
        subColors = subColors,
        initialSelection = initialSelection,
        waitForPositiveButton = waitForPositiveButton,
        selection = selection,
        allowCustomArgb = allowCustomArgb
    )
    setupCustomPage(
        supportCustomAlpha = showAlphaSelector,
        initialSelection = initialSelection,
        selection = selection
    )
  }

  if (waitForPositiveButton && selection != null) {
    setActionButtonEnabled(POSITIVE, false)
    positiveButton {
      selectedColor(allowCustomArgb)?.let { selected ->
        selection.invoke(this, selected)
      }
    }
  }

  return this
}

/**
 * Updates the color displayed in the ARGB page of a color chooser dialog.
 */
fun MaterialDialog.setArgbColor(@ColorInt color: Int) {
  val customPage = getPageCustomView() ?: return
  val previewFrame =
    customPage.findViewById<PreviewFrameView>(R.id.preview_frame)
  previewFrame.setColor(color)

  customPage.findViewById<SeekBar>(R.id.alpha_seeker)
      .progress = Color.alpha(color)
  customPage.findViewById<SeekBar>(R.id.red_seeker)
      .progress = Color.red(color)
  customPage.findViewById<SeekBar>(R.id.green_seeker)
      .progress = Color.green(color)
  customPage.findViewById<SeekBar>(R.id.blue_seeker)
      .progress = Color.blue(color)
}

private fun MaterialDialog.setupGridLayout(
  colors: IntArray,
  subColors: Array<IntArray>?,
  @ColorInt initialSelection: Int?,
  waitForPositiveButton: Boolean,
  selection: ColorCallback,
  allowCustomArgb: Boolean
) {
  require(subColors == null || colors.size == subColors.size) {
    "Sub-colors array size should match the colors array size."
  }

  val gridRecyclerView = getCustomView()
      .findViewById<DialogRecyclerView>(R.id.colorPresetGrid)
  val gridColumnCount = windowContext.resources.getInteger(R.integer.color_grid_column_count)
  gridRecyclerView.layoutManager = GridLayoutManager(windowContext, gridColumnCount)
  gridRecyclerView.attach(this)

  val adapter = ColorGridAdapter(
      dialog = this,
      colors = colors,
      subColors = subColors,
      initialSelection = initialSelection,
      waitForPositiveButton = waitForPositiveButton,
      callback = selection,
      enableARGBButton = allowCustomArgb && context.isLandscape()
  )
  gridRecyclerView.adapter = adapter
}

private fun MaterialDialog.setupCustomPage(
  supportCustomAlpha: Boolean,
  @ColorInt initialSelection: Int?,
  selection: ColorCallback
) {
  val viewSet = CustomPageViewSet(this).tint()
  config[KEY_CUSTOM_PAGE_VIEW_SET] = viewSet

  initialSelection?.let { color ->
    viewSet.setColorArgb(color)
  } ?: run {
    viewSet.setColorAlpha(ALPHA_SOLID)
  }

  val landscape = context.isLandscape()
  if (!supportCustomAlpha) {
    viewSet.alphaLabel.changeHeight(0)
    viewSet.alphaSeeker.changeHeight(0)
    viewSet.alphaValue.changeHeight(0)
    if (!landscape) {
      viewSet.redLabel.below(R.id.preview_frame)
    }
  }
  if (landscape) {
    if (!supportCustomAlpha) {
      viewSet.redLabel.clearTopMargin()
    } else {
      viewSet.alphaLabel.clearTopMargin()
    }
  }

  viewSet.previewFrame.onHexChanged = { color ->
    if (color != selectedColor(true)) {
      viewSet.setColorArgb(color)
      invalidateFromColorChanged(valueChanged = initialSelection != null, selection = selection)
      true
    } else {
      false
    }
  }

  viewSet.alphaSeeker.observe {
    invalidateFromColorChanged(valueChanged = initialSelection != null, selection = selection)
  }
  viewSet.redSeeker.observe {
    invalidateFromColorChanged(valueChanged = initialSelection != null, selection = selection)
  }
  viewSet.greenSeeker.observe {
    invalidateFromColorChanged(valueChanged = initialSelection != null, selection = selection)
  }
  viewSet.blueSeeker.observe {
    invalidateFromColorChanged(valueChanged = initialSelection != null, selection = selection)
  }
  invalidateFromColorChanged(valueChanged = initialSelection != null, selection = selection)
}

private fun MaterialDialog.invalidateFromColorChanged(
  valueChanged: Boolean,
  selection: ColorCallback
) {
  val viewSet: CustomPageViewSet = config(KEY_CUSTOM_PAGE_VIEW_SET)
  val supportCustomAlpha: Boolean = config(KEY_SHOW_ALPHA)
  val waitForPositiveButton: Boolean = config(KEY_WAIT_FOR_POSITIVE)

  val color = argb(
      if (supportCustomAlpha) viewSet.alphaSeeker.progress
      else ALPHA_SOLID,
      viewSet.redSeeker.progress,
      viewSet.greenSeeker.progress,
      viewSet.blueSeeker.progress
  )
  viewSet.previewFrame.supportCustomAlpha = supportCustomAlpha
  viewSet.previewFrame.setColor(color)
  viewSet.setColorArgb(color) // invalidate labels

  if (valueChanged) {
    setActionButtonEnabled(POSITIVE, true)
    if (!waitForPositiveButton) {
      selection?.invoke(this, color)
    }
  }

  updateActionButtonsColor(color)
  getCustomView()
      .findViewById<DialogRecyclerView>(R.id.colorPresetGrid)
      ?.let {
        (it.adapter as ColorGridAdapter).updateSelection(color)
      }
}

internal fun MaterialDialog.setPage(@IntRange(from = 0, to = 1) index: Int) {
  getPager().setCurrentItem(index, true)
}

internal fun MaterialDialog.updateActionButtonsColor(@ColorInt color: Int) {
  val changeButtonColor: Boolean = config(KEY_CHANGE_ACTION_BUTTON_COLOR)
  if (!changeButtonColor) return

  val adjustedColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color))
  val isAdjustedDark = adjustedColor.isColorDark(0.25)
  val isPrimaryDark =
    resolveColor(context = context, attr = android.R.attr.textColorPrimary).isColorDark()

  val finalColor = if (isPrimaryDark && !isAdjustedDark) {
    resolveColor(context = context, attr = android.R.attr.textColorPrimary)
  } else if (!isPrimaryDark && isAdjustedDark) {
    resolveColor(context = context, attr = android.R.attr.textColorPrimaryInverse)
  } else {
    adjustedColor
  }
  getActionButton(POSITIVE).updateTextColor(finalColor)
  getActionButton(NEGATIVE).updateTextColor(finalColor)
}

private fun MaterialDialog.selectedColor(allowCustomColor: Boolean): Int? {
  if (allowCustomColor) {
    val viewPager = getPager()
    if (viewPager.currentItem == 1) {
      val viewSet: CustomPageViewSet = config(KEY_CUSTOM_PAGE_VIEW_SET)
      return viewSet.previewFrame.color
    }
  }
  return (getPageGridView().adapter as ColorGridAdapter).selectedColor()
}

private fun MaterialDialog.getPageGridView() = findViewById<RecyclerView>(R.id.colorPresetGrid)

private fun MaterialDialog.getPageCustomView() = findViewById<View?>(R.id.colorArgbPage)

private fun MaterialDialog.getPager() = findViewById<ViewPager>(R.id.colorChooserPager)

private fun MaterialDialog.getPageIndicator() =
  findViewById<DotsIndicator?>(R.id.colorChooserPagerDots)
