/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color

import android.annotation.SuppressLint
import android.graphics.Color.BLUE
import android.graphics.Color.DKGRAY
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.graphics.Color.alpha
import android.graphics.Color.argb
import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.color.utils.onPageSelected
import com.afollestad.materialdialogs.color.utils.progressChanged
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.tabs.TabLayout
import java.lang.Integer.toHexString

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
 * @param tabPresetTextRes Provides a custom tab label as resource integer for the preset grid page.
 * @param tabPresetText Provides a custom tab label as a literal string for the preset grid page.
 * @param tabArgbTextRes Provides a custom tab label as resource integer for the (A)RGB page.
 * @param tabArgbText Provides a custom tab label as a literal string for the (A)RGB page.
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
  @StringRes tabPresetTextRes: Int? = null,
  tabPresetText: String? = null,
  @StringRes tabArgbTextRes: Int? = null,
  tabArgbText: String? = null,
  selection: ColorCallback = null
): MaterialDialog {

  if (!allowCustomArgb) {
    customView(R.layout.md_color_chooser_base_grid)
    updateGridLayout(colors, subColors, initialSelection, waitForPositiveButton, selection)
  } else {
    customView(R.layout.md_color_chooser_base_pager, noVerticalPadding = true)

    val viewPager = getPager()
    viewPager.adapter =
        ColorPagerAdapter(this, tabPresetTextRes, tabPresetText, tabArgbTextRes, tabArgbText)
    viewPager.onPageSelected {
      setActionButtonEnabled(POSITIVE, selectedColor(allowCustomArgb) != null)
    }

    val tabLayout = getTabLayout()
    tabLayout.setupWithViewPager(viewPager)
    updateGridLayout(colors, subColors, initialSelection, waitForPositiveButton, selection)
    updateCustomPage(showAlphaSelector, initialSelection, waitForPositiveButton, selection)
  }

  if (waitForPositiveButton && selection != null) {
    setActionButtonEnabled(POSITIVE, false)
    positiveButton {
      val color = selectedColor(allowCustomArgb)
      if (color != null) {
        selection.invoke(this, color)
      }
    }
  }

  return this
}

private fun MaterialDialog.updateGridLayout(
  colors: IntArray,
  subColors: Array<IntArray>?,
  @ColorInt initialSelection: Int?,
  waitForPositiveButton: Boolean,
  selection: ColorCallback
) {
  if (subColors != null && colors.size != subColors.size) {
    throw IllegalArgumentException("Sub-colors array size should match the colors array size.")
  }

  val gridRecyclerView = getCustomView()!!.findViewById<RecyclerView>(R.id.colorPresetGrid)
  val gridColumnCount = windowContext.resources.getInteger(R.integer.color_grid_column_count)
  gridRecyclerView.layoutManager = GridLayoutManager(windowContext, gridColumnCount)

  val adapter = ColorGridAdapter(
      dialog = this,
      colors = colors,
      subColors = subColors,
      initialSelection = initialSelection,
      waitForPositiveButton = waitForPositiveButton,
      callback = selection
  )
  gridRecyclerView.adapter = adapter
}

private fun MaterialDialog.updateCustomPage(
  supportCustomAlpha: Boolean,
  @ColorInt initialSelection: Int?,
  waitForPositiveButton: Boolean,
  selection: ColorCallback
) {
  val customPage = getPageCustomView()
  val argbPreviewView = customPage.findViewById<View>(R.id.argbPreviewView)
  val alphaSeeker = customPage.findViewById<SeekBar>(R.id.alpha_seeker)
  val redSeeker = customPage.findViewById<SeekBar>(R.id.red_seeker)
  val greenSeeker = customPage.findViewById<SeekBar>(R.id.green_seeker)
  val blueSeeker = customPage.findViewById<SeekBar>(R.id.blue_seeker)
  val alphaValue = customPage.findViewById<TextView>(R.id.alpha_value)
  val redValue = customPage.findViewById<TextView>(R.id.red_value)
  val greenValue = customPage.findViewById<TextView>(R.id.green_value)
  val blueValue = customPage.findViewById<TextView>(R.id.blue_value)
  val hexValue = customPage.findViewById<TextView>(R.id.hex_value)

  alphaSeeker.tint(DKGRAY)
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

//  alphaFrame.visibility = if (supportCustomAlpha) VISIBLE else GONE
  arrayOf(alphaSeeker, redSeeker, greenSeeker, blueSeeker).progressChanged {
    onCustomValueChanged(
        supportCustomAlpha = supportCustomAlpha,
        waitForPositiveButton = waitForPositiveButton,
        valueChanged = true,
        customView = customPage,
        argbPreviewView = argbPreviewView,
        alphaSeeker = alphaSeeker,
        redSeeker = redSeeker,
        greenSeeker = greenSeeker,
        blueSeeker = blueSeeker,
        alphaValue = alphaValue,
        redValue = redValue,
        greenValue = greenValue,
        blueValue = blueValue,
        hexValue = hexValue,
        selection = selection
    )
  }

  onCustomValueChanged(
      supportCustomAlpha = supportCustomAlpha,
      waitForPositiveButton = waitForPositiveButton,
      valueChanged = initialSelection != null,
      customView = customPage,
      argbPreviewView = argbPreviewView,
      alphaSeeker = alphaSeeker,
      redSeeker = redSeeker,
      greenSeeker = greenSeeker,
      blueSeeker = blueSeeker,
      alphaValue = alphaValue,
      redValue = redValue,
      greenValue = greenValue,
      blueValue = blueValue,
      hexValue = hexValue,
      selection = selection
  )
}

private fun MaterialDialog.onCustomValueChanged(
  supportCustomAlpha: Boolean,
  waitForPositiveButton: Boolean,
  valueChanged: Boolean,
  customView: View,
  argbPreviewView: View,
  alphaSeeker: SeekBar,
  redSeeker: SeekBar,
  greenSeeker: SeekBar,
  blueSeeker: SeekBar,
  alphaValue: TextView,
  redValue: TextView,
  greenValue: TextView,
  blueValue: TextView,
  hexValue: TextView,
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
  hexValue.text = toHexString(color)

  val previewDrawable = GradientDrawable()
  previewDrawable.setColor(color)
  previewDrawable.cornerRadius =
      windowContext.resources.getDimension(R.dimen.color_argb_preview_border_radius)
  argbPreviewView.background = previewDrawable

  // We save the ARGB color as view the tag
  if (valueChanged) {
    customView.tag = color
    setActionButtonEnabled(POSITIVE, true)
  }

  if (!waitForPositiveButton && valueChanged) {
    selection?.invoke(this, color)
  }
}

private fun MaterialDialog.selectedColor(allowCustomColor: Boolean): Int? {
  if (allowCustomColor) {
    val viewPager = getPager()
    if (viewPager.currentItem == 1) {
      return getPageCustomView().tag as? Int
    }
  }
  return (getPageGridView().adapter as ColorGridAdapter).selectedColor()
}

private fun MaterialDialog.getPageGridView() = findViewById<RecyclerView>(R.id.colorPresetGrid)

private fun MaterialDialog.getPageCustomView() = findViewById<View>(R.id.colorArgbPage)

private fun MaterialDialog.getPager() = findViewById<ViewPager>(R.id.colorChooserPager)

private fun MaterialDialog.getTabLayout() = findViewById<TabLayout>(R.id.colorChooserTabs)

private fun SeekBar.tint(color: Int) {
  progressDrawable.setColorFilter(color, SRC_IN)
  thumb.setColorFilter(color, SRC_IN)
}
