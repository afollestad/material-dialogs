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
package com.afollestad.materialdialogs.color.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import com.afollestad.materialdialogs.color.R
import com.afollestad.materialdialogs.color.R.drawable
import com.afollestad.materialdialogs.color.R.layout
import com.afollestad.materialdialogs.color.utils.hexValue
import com.afollestad.materialdialogs.color.utils.toColor
import com.afollestad.materialdialogs.utils.MDUtil.isColorDark

internal typealias HexColorChanged = (Int) -> Boolean

/** @author Aidan Follestad (afollestad) */
internal class PreviewFrameView(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  companion object {
    const val HEX_VALUE_ALPHA_THRESHOLD = 50
  }

  private lateinit var argbView: View
  private lateinit var hexPrefixView: TextView
  private lateinit var hexValueView: ObservableEditText

  var supportCustomAlpha: Boolean = true
  var onHexChanged: HexColorChanged = { true }
  var color: Int? = null
    private set

  init {
    setBackgroundResource(drawable.transparent_rect_repeat)
    LayoutInflater.from(context)
        .inflate(layout.md_color_chooser_preview_frame, this)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    argbView = findViewById(R.id.argbView)
    hexPrefixView = findViewById(R.id.hexPrefixView)
    hexValueView = findViewById(R.id.hexValueView)

    hexValueView.observe {
      if (it.length < 4) {
        return@observe
      }
      val newColor = it.toColor() ?: return@observe
      if (onHexChanged(newColor)) {
        setColor(newColor)
      }
    }
  }

  fun setColor(@ColorInt color: Int) {
    if (this.color == color) {
      // Not changed
      return
    }
    this.color = color

    argbView.background = ColorDrawable(color)
    hexValueView.updateText(color.hexValue(supportCustomAlpha))
    hexValueView.post { hexValueView.setSelection(hexValueView.textLength) }

    val tintColor = tintColor(color)
    hexPrefixView.setTextColor(tintColor)
    hexValueView.setTextColor(tintColor)
    ViewCompat.setBackgroundTintList(hexValueView, ColorStateList.valueOf(tintColor))
  }

  private fun tintColor(color: Int) =
    if (color.isColorDark() && Color.alpha(color) >= HEX_VALUE_ALPHA_THRESHOLD) {
      WHITE
    } else {
      BLACK
    }
}
