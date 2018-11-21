/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import com.afollestad.materialdialogs.color.utils.hexValue
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.afollestad.materialdialogs.utils.MDUtil.isColorDark
import com.afollestad.materialdialogs.color.utils.toColor

internal typealias HexColorChanged = (Int) -> Boolean

/** @author Aidan Follestad (afollestad) */
internal class PreviewFrameView(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  companion object {
    const val HEX_VALUE_ALPHA_THRESHOLD = 50
  }

  lateinit var argbView: View
  lateinit var hexPrefixView: TextView
  lateinit var hexValueView: EditText

  var supportCustomAlpha: Boolean = true
  var onHexChanged: HexColorChanged = { true }

  init {
    setBackgroundResource(R.drawable.transparent_rect_repeat)
    LayoutInflater.from(context)
        .inflate(R.layout.md_color_chooser_preview_frame, this)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    argbView = findViewById(R.id.argbView)
    hexPrefixView = findViewById(R.id.hexPrefixView)
    hexValueView = findViewById(R.id.hexValueView)

    hexValueView.textChanged {
      if (it.length < 4) {
        return@textChanged
      }
      val newColor = it.toString().toColor() ?: return@textChanged
      if (onHexChanged(newColor)) {
        hexValueView.post { hexValueView.setSelection(hexValueView.text.length) }
      }
    }
  }

  fun setColor(@ColorInt color: Int) {
    argbView.background = ColorDrawable(color)
    hexValueView.setText(color.hexValue(supportCustomAlpha))
    hexValueView.post { hexValueView.setSelection(hexValueView.text.length) }

    val tintColor = if (color.isColorDark() &&
        Color.alpha(color) >= HEX_VALUE_ALPHA_THRESHOLD
    ) {
      WHITE
    } else {
      BLACK
    }
    hexPrefixView.setTextColor(tintColor)
    hexValueView.setTextColor(tintColor)
    ViewCompat.setBackgroundTintList(hexValueView, ColorStateList.valueOf(tintColor))
  }
}
