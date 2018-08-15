/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.color

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import com.afollestad.materialdialogs.shared.dimenPx

/** @author Aidan Follestad (afollestad) */
internal class ColorCircleView(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {

  private val strokePaint = Paint()
  private val fillPaint = Paint()

  private val borderWidth = dimenPx(R.dimen.color_circle_view_border)

  init {
    setWillNotDraw(false)
    strokePaint.style = STROKE
    strokePaint.isAntiAlias = true
    strokePaint.color = Color.BLACK
    strokePaint.strokeWidth = borderWidth.toFloat()
    fillPaint.style = FILL
    fillPaint.isAntiAlias = true
    fillPaint.color = Color.DKGRAY
  }

  @ColorInt
  var color: Int = Color.BLACK
    set(value) {
      field = value
      fillPaint.color = value
      invalidate()
    }
  @ColorInt
  var border: Int = Color.DKGRAY
    set(value) {
      field = value
      strokePaint.color = value
      invalidate()
    }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) = super.onMeasure(widthMeasureSpec, widthMeasureSpec)

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawCircle(
        measuredWidth / 2f,
        measuredHeight / 2f,
        (measuredWidth / 2f) - borderWidth,
        fillPaint
    )
    canvas.drawCircle(
        measuredWidth / 2f,
        measuredHeight / 2f,
        (measuredWidth / 2f) - borderWidth,
        strokePaint
    )
  }
}