/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.internal.title

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.getSize
import android.view.View.MeasureSpec.makeMeasureSpec
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.internal.main.BaseSubLayout
import com.afollestad.materialdialogs.internal.main.DEBUG_COLOR_DARK_PINK
import com.afollestad.materialdialogs.internal.main.DEBUG_COLOR_PINK
import com.afollestad.materialdialogs.utils.dimenPx
import com.afollestad.materialdialogs.utils.isNotVisible
import com.afollestad.materialdialogs.utils.isVisible
import java.lang.Math.max

/**
 * Manages the header frame of the dialog, including the optional icon and title.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class DialogTitleLayout(
  context: Context,
  attrs: AttributeSet? = null
) : BaseSubLayout(context, attrs) {

  private val frameMarginVertical = dimenPx(R.dimen.md_dialog_frame_margin_vertical)
  private val titleMarginBottom = dimenPx(R.dimen.md_dialog_title_layout_margin_bottom)
  private val frameMarginHorizontal = dimenPx(R.dimen.md_dialog_frame_margin_horizontal)

  private val iconMargin = dimenPx(R.dimen.md_icon_margin)
  private val iconSize = dimenPx(R.dimen.md_icon_size)

  internal lateinit var iconView: ImageView
  internal lateinit var titleView: TextView

  override fun onFinishInflate() {
    super.onFinishInflate()
    iconView = findViewById(R.id.md_icon_title)
    titleView = findViewById(R.id.md_text_title)
  }

  fun shouldNotBeVisible() =
    iconView.isNotVisible() && titleView.isNotVisible()

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    if (shouldNotBeVisible()) {
      setMeasuredDimension(0, 0)
      return
    }

    val parentWidth = getSize(widthMeasureSpec)
    var titleMaxWidth =
      parentWidth - (frameMarginHorizontal * 2)

    if (iconView.isVisible()) {
      iconView.measure(
          makeMeasureSpec(iconSize, EXACTLY),
          makeMeasureSpec(iconSize, EXACTLY)
      )
      titleMaxWidth -= iconView.measuredWidth
    }

    titleView.measure(
        makeMeasureSpec(titleMaxWidth, AT_MOST),
        makeMeasureSpec(0, UNSPECIFIED)
    )

    val iconViewHeight =
      if (iconView.isVisible()) iconView.measuredHeight else 0
    val requiredHeight = max(
        iconViewHeight, titleView.measuredHeight
    )
    val actualHeight = requiredHeight + frameMarginVertical + titleMarginBottom

    setMeasuredDimension(
        parentWidth,
        actualHeight
    )
  }

  override fun onLayout(
    changed: Boolean,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
  ) {
    if (shouldNotBeVisible()) return

    var titleLeft = frameMarginHorizontal
    val titleBottom = measuredHeight - titleMarginBottom
    val titleTop = titleBottom - titleView.measuredHeight
    val titleRight = titleLeft + titleView.measuredWidth

    if (iconView.isVisible()) {
      val titleHalfHeight = (titleBottom - titleTop) / 2
      val titleMidPoint = titleBottom - titleHalfHeight
      val iconHalfHeight = iconView.measuredHeight / 2
      val iconLeft = titleLeft
      val iconTop = titleMidPoint - iconHalfHeight
      val iconRight = iconLeft + iconView.measuredWidth
      val iconBottom = iconTop + iconView.measuredHeight
      iconView.layout(iconLeft, iconTop, iconRight, iconBottom)
      titleLeft = iconRight + iconMargin
    }

    titleView.layout(titleLeft, titleTop, titleRight, titleBottom)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    if (dialogParent().debugMode) {
      // Fill above the title
      canvas.drawRect(
          0f,
          0f,
          measuredWidth.toFloat(),
          frameMarginVertical.toFloat(),
          debugPaint(DEBUG_COLOR_PINK)
      )
      // Fill below the title
      canvas.drawRect(
          0f,
          measuredHeight.toFloat() - titleMarginBottom,
          measuredWidth.toFloat(),
          measuredHeight.toFloat(),
          debugPaint(DEBUG_COLOR_PINK)
      )
      // Fill to the left of the title
      canvas.drawRect(
          0f,
          0f,
          frameMarginHorizontal.toFloat(),
          measuredHeight.toFloat(),
          debugPaint(DEBUG_COLOR_DARK_PINK)
      )
      // Fill to the right of the title
      canvas.drawRect(
          measuredWidth.toFloat() - frameMarginHorizontal,
          0f,
          measuredWidth.toFloat(),
          measuredHeight.toFloat(),
          debugPaint(DEBUG_COLOR_DARK_PINK)
      )
    }

    if (drawDivider) {
      canvas.drawLine(
          0f,
          measuredHeight.toFloat() - dividerHeight.toFloat(),
          measuredWidth.toFloat(),
          measuredHeight.toFloat(),
          dividerPaint()
      )
    }
  }
}