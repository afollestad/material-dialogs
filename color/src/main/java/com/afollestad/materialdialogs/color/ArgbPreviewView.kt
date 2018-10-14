/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt

/** @author Aidan Follestad (afollestad) */
internal class ArgbPreviewView(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {

  private var path: Path? = null
  private val borderRadius = resources.getDimension(R.dimen.color_argb_preview_border_radius)

  fun setColor(@ColorInt color: Int) {
    background = ColorDrawable(color)
  }

  override fun onSizeChanged(
    w: Int,
    h: Int,
    oldw: Int,
    oldh: Int
  ) {
    super.onSizeChanged(w, h, oldw, oldh)
    this.path = Path()
    this.path?.addRoundRect(RectF(0f, 0f, w.toFloat(), h.toFloat()), borderRadius, borderRadius, CW)
  }

  override fun dispatchDraw(canvas: Canvas) {
    this.path?.let {
      canvas.clipPath(it)
    }
    super.dispatchDraw(canvas)
  }
}
