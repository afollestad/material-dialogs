/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.LinearLayout
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
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.tabs.TabLayout

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
 * @param allowCustomColor allows to select a color with an (A)RGB slider view
 * @param supportCustomAlpha allows to select alpha values in the custom values view or not
 * @param tabGridTextRes provide a custom tab label as resource integer for the grid page
 * @param tabGridText provide a custom tab label as string for the grid page
 * @param tabCustomTextRes provide a custom tab label as resource integer for the custom page
 * @param tabCustomText provide a custom tab label as string  for the custom page
 * @param selection An optional callback invoked when the user selects a color.
 */
@SuppressLint("CheckResult")
@CheckResult
fun MaterialDialog.colorChooser(
  colors: IntArray,
  subColors: Array<IntArray>? = null,
  @ColorInt initialSelection: Int? = null,
  waitForPositiveButton: Boolean = true,
  allowCustomColor: Boolean = false,
  supportCustomAlpha: Boolean = false,
  @StringRes tabGridTextRes: Int? = null,
  tabGridText: String? = null,
  @StringRes tabCustomTextRes: Int? = null,
  tabCustomText: String? = null,
  selection: ColorCallback = null
): MaterialDialog {

  if (!allowCustomColor) {
    customView(R.layout.md_color_chooser_grid)
    updateGridLayout(this, colors, subColors, initialSelection, waitForPositiveButton, selection)
  } else {
    customView(R.layout.md_color_chooser_pager)
    val viewPager = getPager()
    viewPager.adapter =
        ColorPagerAdapter(this, tabGridTextRes, tabGridText, tabCustomTextRes, tabCustomText)
    viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
      override fun onPageScrollStateChanged(state: Int) {}

      override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
      ) {
      }

      override fun onPageSelected(position: Int) {
        setActionButtonEnabled(POSITIVE, selectedColor(this@colorChooser, allowCustomColor) != null)
      }
    })
    val tabLayout = getTabLayout()
    tabLayout.setupWithViewPager(viewPager)
    updateGridLayout(this, colors, subColors, initialSelection, waitForPositiveButton, selection)
    updateCustomPage(this, supportCustomAlpha, initialSelection, waitForPositiveButton, selection)
  }

  if (waitForPositiveButton && selection != null) {
    setActionButtonEnabled(POSITIVE, false)
    positiveButton {
      val color = selectedColor(this, allowCustomColor)
      if (color != null) {
        selection.invoke(this, color)
      }
    }
  }

  return this
}

private fun updateGridLayout(
  dialog: MaterialDialog,
  colors: IntArray,
  subColors: Array<IntArray>?,
  @ColorInt initialSelection: Int?,
  waitForPositiveButton: Boolean,
  selection: ColorCallback
) {
  if (subColors != null && colors.size != subColors.size) {
    throw IllegalStateException("Sub-colors array size should match the colors array size.")
  }
  val gridRecyclerView: RecyclerView = dialog.getCustomView()!!.findViewById(R.id.rvGrid)
  val gridColumnCount = dialog.windowContext.resources
      .getInteger(R.integer.color_grid_column_count)
  gridRecyclerView.layoutManager = GridLayoutManager(
      dialog.windowContext, gridColumnCount
  )

  val adapter = ColorGridAdapter(
      dialog = dialog,
      colors = colors,
      subColors = subColors,
      initialSelection = initialSelection,
      waitForPositiveButton = waitForPositiveButton,
      callback = selection
  )
  gridRecyclerView.adapter = adapter
}

private fun updateCustomPage(
  dialog: MaterialDialog,
  supportCustomAlpha: Boolean, @ColorInt initialSelection: Int?,
  waitForPositiveButton: Boolean,
  selection: ColorCallback
) {
  val customPage: View = dialog.getPageCustomView()
  val vColor: View = customPage.findViewById(R.id.v_color)
  val llAlpha: LinearLayout = customPage.findViewById(R.id.llAlpha)
  val sbAlpha: SeekBar = customPage.findViewById(R.id.sb_alpha)
  val sbRed: SeekBar = customPage.findViewById(R.id.sb_red)
  val sbGreen: SeekBar = customPage.findViewById(R.id.sb_green)
  val sbBlue: SeekBar = customPage.findViewById(R.id.sb_blue)
  val tvAlphaValue: TextView = customPage.findViewById(R.id.tv_alpha_value)
  val tvRedValue: TextView = customPage.findViewById(R.id.tv_red_value)
  val tvGreenValue: TextView = customPage.findViewById(R.id.tv_green_value)
  val tvBlueValue: TextView = customPage.findViewById(R.id.tv_blue_value)

  sbAlpha.tint(Color.BLACK)
  sbRed.tint(Color.RED)
  sbGreen.tint(Color.GREEN)
  sbBlue.tint(Color.BLUE)

  initialSelection?.let {
    if (supportCustomAlpha) {
      sbAlpha.progress = Color.alpha(it)
    }
    sbRed.progress = Color.red(it)
    sbGreen.progress = Color.green(it)
    sbBlue.progress = Color.blue(it)
  } ?: run {
    sbAlpha.progress = ALPHA_SOLID
  }

  llAlpha.visibility = if (supportCustomAlpha) View.VISIBLE else View.GONE

  val listener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(
      p0: SeekBar?,
      p1: Int,
      p2: Boolean
    ) {
      onCustomValueChanged(
          dialog, supportCustomAlpha, waitForPositiveButton, true, customPage, vColor, sbAlpha,
          sbRed, sbGreen, sbBlue, tvAlphaValue, tvRedValue, tvGreenValue, tvBlueValue, selection
      )
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}
    override fun onStopTrackingTouch(p0: SeekBar?) {}
  }

  sbAlpha.setOnSeekBarChangeListener(listener)
  sbRed.setOnSeekBarChangeListener(listener)
  sbGreen.setOnSeekBarChangeListener(listener)
  sbBlue.setOnSeekBarChangeListener(listener)

  onCustomValueChanged(
      dialog = dialog,
      supportCustomAlpha = supportCustomAlpha,
      waitForPositiveButton = waitForPositiveButton,
      valueChanged = initialSelection != null, customView = customPage,
      vColor = vColor,
      sbAlpha = sbAlpha,
      sbRed = sbRed,
      sbGreen = sbGreen,
      sbBlue = sbBlue,
      tvAlphaValue = tvAlphaValue,
      tvRedValue = tvRedValue,
      tvGreenValue = tvGreenValue,
      tvBlueValue = tvBlueValue,
      selection = selection
  )
}

private fun onCustomValueChanged(
  dialog: MaterialDialog,
  supportCustomAlpha: Boolean,
  waitForPositiveButton: Boolean,
  valueChanged: Boolean,
  customView: View,
  vColor: View,
  sbAlpha: SeekBar,
  sbRed: SeekBar,
  sbGreen: SeekBar,
  sbBlue: SeekBar,
  tvAlphaValue: TextView,
  tvRedValue: TextView,
  tvGreenValue: TextView,
  tvBlueValue: TextView,
  selection: ColorCallback
) {
  if (supportCustomAlpha) {
    tvAlphaValue.text = sbAlpha.progress.toString()
  }
  tvRedValue.text = sbRed.progress.toString()
  tvGreenValue.text = sbGreen.progress.toString()
  tvBlueValue.text = sbBlue.progress.toString()

  val color = Color.argb(
      if (supportCustomAlpha) sbAlpha.progress else ALPHA_SOLID, sbRed.progress, sbGreen.progress,
      sbBlue.progress
  )
  vColor.setBackgroundColor(color)
  // simple solution - we save the color as view tag
  if (valueChanged) {
    customView.setTag(color)
    dialog.setActionButtonEnabled(POSITIVE, true)
  }

  if (!waitForPositiveButton && valueChanged) {
    selection?.invoke(dialog, color)
  }
}

// ----------------
// Helper functions
// ----------------

private fun selectedColor(
  dialog: MaterialDialog,
  allowCustomColor: Boolean
): Int? {
  if (allowCustomColor) {
    val viewPager = dialog.getPager()
    if (viewPager.currentItem == 1) {
      return dialog.getPageCustomView().getTag() as? Int
    }
  }
  return (dialog.getPageGridView().adapter as ColorGridAdapter).selectedColor()
}

private fun MaterialDialog.getPageGridView() = findViewById(R.id.rvGrid) as RecyclerView

private fun MaterialDialog.getPageCustomView() = findViewById(R.id.llCustomColor) as View

private fun MaterialDialog.getPager() = findViewById(R.id.vpPager) as ViewPager

private fun MaterialDialog.getTabLayout() = findViewById(R.id.tlTabls) as TabLayout

private fun SeekBar.tint(color: Int) {
  getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN)
  getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN)
}
