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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.afollestad.materialdialogs.color

import android.R.attr
import android.graphics.Color
import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.graphics.PorterDuff.Mode.SRC_IN
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.view.ObservableSeekBar
import com.afollestad.materialdialogs.color.view.PreviewFrameView
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor

internal class CustomPageViewSet(private val dialog: MaterialDialog) {

  val previewFrame: PreviewFrameView

  val alphaLabel: TextView
  val alphaSeeker: ObservableSeekBar
  val alphaValue: TextView

  val redLabel: TextView
  val redSeeker: ObservableSeekBar
  val redValue: TextView

  val greenLabel: TextView
  val greenSeeker: ObservableSeekBar
  val greenValue: TextView

  val blueLabel: TextView
  val blueSeeker: ObservableSeekBar
  val blueValue: TextView

  init {
    val customPage =
      dialog.getPageCustomView() ?: throw IllegalArgumentException("Page custom view is null")
    previewFrame = customPage.findViewById(R.id.preview_frame)

    alphaLabel = customPage.findViewById(R.id.alpha_label)
    alphaSeeker = customPage.findViewById(R.id.alpha_seeker)
    alphaValue = customPage.findViewById(R.id.alpha_value)

    redLabel = customPage.findViewById(R.id.red_label)
    redSeeker = customPage.findViewById(R.id.red_seeker)
    redValue = customPage.findViewById(R.id.red_value)

    greenLabel = customPage.findViewById(R.id.green_label)
    greenSeeker = customPage.findViewById(R.id.green_seeker)
    greenValue = customPage.findViewById(R.id.green_value)

    blueLabel = customPage.findViewById(R.id.blue_label)
    blueSeeker = customPage.findViewById(R.id.blue_seeker)
    blueValue = customPage.findViewById(R.id.blue_value)
  }

  fun tint(): CustomPageViewSet {
    alphaSeeker.tint(resolveColor(dialog.windowContext, attr = attr.textColorSecondary))
    redSeeker.tint(RED)
    greenSeeker.tint(GREEN)
    blueSeeker.tint(BLUE)
    return this
  }

  fun setColorArgb(color: Int) {
    setColorAlpha(Color.alpha(color))
    setColorRed(Color.red(color))
    setColorBlue(Color.blue(color))
    setColorGreen(Color.green(color))
    previewFrame.setColor(color)
  }

  fun setColorAlpha(alpha: Int) {
    alphaSeeker.updateProgress(alpha)
    alphaValue.text = alpha.toString()
  }

  private fun setColorRed(red: Int) {
    redSeeker.updateProgress(red)
    redValue.text = red.toString()
  }

  private fun setColorGreen(green: Int) {
    greenSeeker.updateProgress(green)
    greenValue.text = green.toString()
  }

  private fun setColorBlue(blue: Int) {
    blueSeeker.updateProgress(blue)
    blueValue.text = blue.toString()
  }
}

private fun MaterialDialog.getPageCustomView() = findViewById<View?>(R.id.colorArgbPage)

private fun SeekBar.tint(color: Int) {
  progressDrawable.setColorFilter(color, SRC_IN)
  thumb.setColorFilter(color, SRC_IN)
}
