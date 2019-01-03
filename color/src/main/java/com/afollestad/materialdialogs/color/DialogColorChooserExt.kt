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

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.graphics.Color.alpha
import android.graphics.Color.argb
import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
import android.graphics.PorterDuff.Mode.SRC_IN
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
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
import com.afollestad.materialdialogs.color.utils.progressChanged
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
  selection: ColorCallback = null
): MaterialDialog {

  if (!allowCustomArgb) {
    customView(R.layout.md_color_chooser_base_grid)
    updateGridLayout(
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
            ?.findViewById<DialogRecyclerView>(R.id.colorPresetGrid)
            ?.invalidateDividers()
        val imm =
          context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(hexValueView.windowToken, 0)
      } else {
        invalidateDividers(false, false)
      }
    }

    val pageIndicator = getPageIndicator()
    pageIndicator?.attachViewPager(viewPager)
    pageIndicator?.setDotTint(resolveColor(windowContext, attr = android.R.attr.textColorPrimary))

    updateGridLayout(
        colors = colors,
        subColors = subColors,
        initialSelection = initialSelection,
        waitForPositiveButton = waitForPositiveButton,
        selection = selection,
        allowCustomArgb = allowCustomArgb
    )
    updateCustomPage(
        supportCustomAlpha = showAlphaSelector,
        initialSelection = initialSelection,
        waitForPositiveButton = waitForPositiveButton,
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

private fun MaterialDialog.updateGridLayout(
  colors: IntArray,
  subColors: Array<IntArray>?,
  @ColorInt initialSelection: Int?,
  waitForPositiveButton: Boolean,
  selection: ColorCallback,
  allowCustomArgb: Boolean
) {
  require(subColors != null && colors.size != subColors.size) {
    "Sub-colors array size should match the colors array size."
  }

  val gridRecyclerView =
    getCustomView()?.findViewById<DialogRecyclerView>(R.id.colorPresetGrid) ?: return
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
      enableARGBButton = allowCustomArgb && isLandscape(context)
  )
  gridRecyclerView.adapter = adapter
}

private fun MaterialDialog.updateCustomPage(
  supportCustomAlpha: Boolean,
  @ColorInt initialSelection: Int?,
  waitForPositiveButton: Boolean,
  selection: ColorCallback
) {
  val customPage = getPageCustomView() ?: return
  val previewFrame = customPage.findViewById<PreviewFrameView>(R.id.preview_frame)
  val alphaLabel = customPage.findViewById<TextView>(R.id.alpha_label)
  val alphaSeeker = customPage.findViewById<SeekBar>(R.id.alpha_seeker)
  val alphaValue = customPage.findViewById<TextView>(R.id.alpha_value)
  val redLabel = customPage.findViewById<TextView>(R.id.red_label)
  val redSeeker = customPage.findViewById<SeekBar>(R.id.red_seeker)
  val redValue = customPage.findViewById<TextView>(R.id.red_value)
  val greenSeeker = customPage.findViewById<SeekBar>(R.id.green_seeker)
  val greenValue = customPage.findViewById<TextView>(R.id.green_value)
  val blueSeeker = customPage.findViewById<SeekBar>(R.id.blue_seeker)
  val blueValue = customPage.findViewById<TextView>(R.id.blue_value)

  alphaSeeker.tint(resolveColor(windowContext, attr = android.R.attr.textColorSecondary))
  redSeeker.tint(RED)
  greenSeeker.tint(GREEN)
  blueSeeker.tint(BLUE)

  initialSelection?.let {
    if (supportCustomAlpha) {
      alphaSeeker.progress = alpha(it)
    }
    redSeeker.progress = red(it)
    greenSeeker.progress = green(it)
    blueSeeker.progress = blue(it)
  } ?: run {
    alphaSeeker.progress = ALPHA_SOLID
  }

  val landscape = isLandscape(context)

  if (!supportCustomAlpha) {
    alphaLabel.changeHeight(0)
    alphaSeeker.changeHeight(0)
    alphaValue.changeHeight(0)

    if (!landscape) {
      redLabel.below(R.id.preview_frame)
    }
  }

  if (landscape) {
    if (!supportCustomAlpha) {
      redLabel.clearTopMargin()
    } else {
      alphaLabel.clearTopMargin()
    }
  }

  previewFrame.onHexChanged = { color ->
    if (color != selectedColor(true)) {
      alphaSeeker.progress = Color.alpha(color)
      redSeeker.progress = Color.red(color)
      greenSeeker.progress = Color.green(color)
      blueSeeker.progress = Color.blue(color)
      true
    } else {
      false
    }
  }

  arrayOf(alphaSeeker, redSeeker, greenSeeker, blueSeeker).progressChanged {
    onCustomValueChanged(
        supportCustomAlpha = supportCustomAlpha,
        waitForPositiveButton = waitForPositiveButton,
        valueChanged = true,
        customView = customPage,
        previewFrame = previewFrame,
        alphaSeeker = alphaSeeker,
        redSeeker = redSeeker,
        greenSeeker = greenSeeker,
        blueSeeker = blueSeeker,
        alphaValue = alphaValue,
        redValue = redValue,
        greenValue = greenValue,
        blueValue = blueValue,
        selection = selection
    )
  }

  onCustomValueChanged(
      supportCustomAlpha = supportCustomAlpha,
      waitForPositiveButton = waitForPositiveButton,
      valueChanged = initialSelection != null,
      customView = customPage,
      previewFrame = previewFrame,
      alphaSeeker = alphaSeeker,
      redSeeker = redSeeker,
      greenSeeker = greenSeeker,
      blueSeeker = blueSeeker,
      alphaValue = alphaValue,
      redValue = redValue,
      greenValue = greenValue,
      blueValue = blueValue,
      selection = selection
  )
}

private fun MaterialDialog.onCustomValueChanged(
  supportCustomAlpha: Boolean,
  waitForPositiveButton: Boolean,
  valueChanged: Boolean,
  customView: View,
  previewFrame: PreviewFrameView,
  alphaSeeker: SeekBar,
  redSeeker: SeekBar,
  greenSeeker: SeekBar,
  blueSeeker: SeekBar,
  alphaValue: TextView,
  redValue: TextView,
  greenValue: TextView,
  blueValue: TextView,
  selection: ColorCallback
) {
  if (supportCustomAlpha) {
    alphaValue.text = alphaSeeker.progress.toString()
  }

  redValue.text = redSeeker.progress.toString()
  greenValue.text = greenSeeker.progress.toString()
  blueValue.text = blueSeeker.progress.toString()

  val color = argb(
      if (supportCustomAlpha) alphaSeeker.progress
      else ALPHA_SOLID,
      redSeeker.progress,
      greenSeeker.progress,
      blueSeeker.progress
  )

  previewFrame.supportCustomAlpha = supportCustomAlpha
  previewFrame.setColor(color)

  // We save the ARGB color as view tag
  if (valueChanged) {
    customView.tag = color
    setActionButtonEnabled(POSITIVE, true)
  }

  if (!waitForPositiveButton && valueChanged) {
    selection?.invoke(this, color)
  }

  updateActionButtonsColor(color)
  getCustomView()?.findViewById<DialogRecyclerView>(R.id.colorPresetGrid)
      ?.let {
        (it.adapter as ColorGridAdapter).updateSelection(color)
      }
}

private fun MaterialDialog.selectedColor(allowCustomColor: Boolean): Int? {
  if (allowCustomColor) {
    val viewPager = getPager()
    if (viewPager.currentItem == 1) {
      return getPageCustomView()?.tag as? Int
    }
  }
  return (getPageGridView().adapter as ColorGridAdapter).selectedColor()
}

private fun MaterialDialog.getPageGridView() = findViewById<RecyclerView>(R.id.colorPresetGrid)

private fun MaterialDialog.getPageCustomView() = findViewById<View?>(R.id.colorArgbPage)

private fun MaterialDialog.getPager() = findViewById<ViewPager>(R.id.colorChooserPager)

internal fun MaterialDialog.setPage(@IntRange(from = 0, to = 1) index: Int) {
  getPager().setCurrentItem(index, true)
}

internal fun MaterialDialog.updateActionButtonsColor(@ColorInt color: Int) {
  val adjustedColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color))
  val isAdjustedDark = adjustedColor.isColorDark(0.25)
  val isPrimaryDark =
    resolveColor(context = context, attr = attr.textColorPrimary).isColorDark()

  val finalColor = if (isPrimaryDark && !isAdjustedDark) {
    resolveColor(context = context, attr = attr.textColorPrimary)
  } else if (!isPrimaryDark && isAdjustedDark) {
    resolveColor(context = context, attr = attr.textColorPrimaryInverse)
  } else {
    adjustedColor
  }
  getActionButton(POSITIVE).updateTextColor(finalColor)
  getActionButton(NEGATIVE).updateTextColor(finalColor)
}

private fun MaterialDialog.getPageIndicator() =
  findViewById<DotsIndicator?>(R.id.colorChooserPagerDots)

private fun SeekBar.tint(color: Int) {
  progressDrawable.setColorFilter(color, SRC_IN)
  thumb.setColorFilter(color, SRC_IN)
}
