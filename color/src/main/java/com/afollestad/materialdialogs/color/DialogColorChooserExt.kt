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
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 * @param allowCustomColor allows to select a color with an (A)RGB slider view -
 *     ATTENTION: the neutral button will be set to switch between presets and color selector + autoDismiss is disabled!
 * @param supportCustomAlpha allows to select alpha values in the custom values view or not
 *  @param selection An optional callback invoked when the user selects a color.
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
  selection: ColorCallback = null
): MaterialDialog {
  setLayoutColorPresets(this, colors, subColors, initialSelection, waitForPositiveButton, selection)

  if (waitForPositiveButton && selection != null) {
    setActionButtonEnabled(POSITIVE, false)
    positiveButton {
      val color = selectedColor(this)
      if (color != null) {
        selection.invoke(this, color)
      }
      if (allowCustomColor) {
        dismiss()
      }
    }
  }

  var showPresets = true
  if (allowCustomColor) {
    noAutoDismiss()
    negativeButton { dismiss() }
    @Suppress("DEPRECATION")
    neutralButton(R.string.md_dialog_color_custom) {
      val color = selectedColor(this)
      if (showPresets) {
        neutralButton(R.string.md_dialog_color_presets)
        removeCurrentCustomView(this)
        setLayoutCustom(this,  supportCustomAlpha, color, waitForPositiveButton, selection)
      } else {
        neutralButton(R.string.md_dialog_color_custom)
        removeCurrentCustomView(this)
        setLayoutColorPresets(this, colors, subColors, color, waitForPositiveButton, selection)
      }
      showPresets = showPresets.not()
    }
  }

  return this
}

private fun selectedColor(dialog: MaterialDialog) : Int? {
  val customView = dialog.getCustomView()
  if (customView is RecyclerView) {
    return (customView.adapter as ColorGridAdapter).selectedColor()
  } else if (customView != null) {
    return customView.getTag() as Int?
  }
  return null
}

private fun removeCurrentCustomView(dialog: MaterialDialog) {
  (dialog.getCustomView()?.parent as ViewGroup)?.removeView(dialog.getCustomView())
}

private fun setLayoutColorPresets(dialog: MaterialDialog,
      colors: IntArray,
      subColors: Array<IntArray>?,
      @ColorInt initialSelection: Int?,
      waitForPositiveButton: Boolean,
      selection: ColorCallback) {
  dialog.customView(R.layout.md_color_chooser_grid)
  val customView = dialog.getCustomView() as RecyclerView

  if (subColors != null && colors.size != subColors.size) {
    throw IllegalStateException("Sub-colors array size should match the colors array size.")
  }

  val gridColumnCount = dialog.windowContext.resources
          .getInteger(R.integer.color_grid_column_count)
  customView.layoutManager = GridLayoutManager(
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
  customView.adapter = adapter
}

private fun setLayoutCustom(dialog: MaterialDialog, supportCustomAlpha: Boolean, @ColorInt initialSelection: Int?, waitForPositiveButton: Boolean, selection: ColorCallback) {
  dialog.customView(R.layout.md_color_custom_color_chooser)
  val vColor: View = dialog.getCustomView()!!.findViewById(R.id.v_color)
  val sbAlpha: SeekBar = dialog.getCustomView()!!.findViewById(R.id.sb_alpha)
  val sbRed: SeekBar = dialog.getCustomView()!!.findViewById(R.id.sb_red)
  val sbGreen: SeekBar = dialog.getCustomView()!!.findViewById(R.id.sb_green)
  val sbBlue: SeekBar = dialog.getCustomView()!!.findViewById(R.id.sb_blue)
  val tvAlphaValue: TextView = dialog.getCustomView()!!.findViewById(R.id.tv_alpha_value)
  val tvRedValue: TextView = dialog.getCustomView()!!.findViewById(R.id.tv_red_value)
  val tvGreenValue: TextView = dialog.getCustomView()!!.findViewById(R.id.tv_green_value)
  val tvBlueValue: TextView = dialog.getCustomView()!!.findViewById(R.id.tv_blue_value)

  tintSeekbar(sbAlpha, Color.BLACK)
  tintSeekbar(sbRed, Color.RED)
  tintSeekbar(sbGreen, Color.GREEN)
  tintSeekbar(sbBlue, Color.BLUE)

  initialSelection?.let {
    if (supportCustomAlpha) {
      sbAlpha.progress = Color.alpha(it)
    }
    sbRed.progress = Color.red(it)
    sbGreen.progress = Color.green(it)
    sbBlue.progress = Color.blue(it)
  } ?: run {
    sbAlpha.progress = 255
  }

  sbAlpha.visibility = if (supportCustomAlpha) View.VISIBLE else View.GONE

  val listener = object: SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
      onCustomValueChanged(dialog, supportCustomAlpha, waitForPositiveButton, true, dialog.getCustomView()!!, vColor, sbAlpha, sbRed, sbGreen, sbBlue, tvAlphaValue, tvRedValue, tvGreenValue, tvBlueValue, selection)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}
    override fun onStopTrackingTouch(p0: SeekBar?) { }
  }

  sbAlpha.setOnSeekBarChangeListener(listener)
  sbRed.setOnSeekBarChangeListener(listener)
  sbGreen.setOnSeekBarChangeListener(listener)
  sbBlue.setOnSeekBarChangeListener(listener)

  onCustomValueChanged(dialog, supportCustomAlpha, waitForPositiveButton, initialSelection != null, dialog.getCustomView()!!, vColor, sbAlpha, sbRed, sbGreen, sbBlue, tvAlphaValue, tvRedValue, tvGreenValue, tvBlueValue, selection)
}

private fun tintSeekbar(seekBar: SeekBar, color: Int) {
  seekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
  seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
}

private fun onCustomValueChanged(
        dialog: MaterialDialog,
        supportCustomAlpha: Boolean,
        waitForPositiveButton: Boolean,
        valueChanged: Boolean,
        customView: View,
        vColor: View,
        sbAlpha: SeekBar, sbRed: SeekBar, sbGreen: SeekBar, sbBlue: SeekBar,
        tvAlphaValue: TextView, tvRedValue: TextView, tvGreenValue: TextView, tvBlueValue: TextView,
        selection: ColorCallback) {
  if (supportCustomAlpha) {
    tvAlphaValue.text = sbAlpha.progress.toString()
  }
  tvRedValue.text = sbRed.progress.toString()
  tvGreenValue.text = sbGreen.progress.toString()
  tvBlueValue.text = sbBlue.progress.toString()

  val color = Color.argb(if (supportCustomAlpha) sbAlpha.progress else 255, sbRed.progress, sbGreen.progress, sbBlue.progress)
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
