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
package com.afollestad.materialdialogs.internal.main

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
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx
import com.afollestad.materialdialogs.utils.isNotVisible
import com.afollestad.materialdialogs.utils.isRtl
import com.afollestad.materialdialogs.utils.isVisible
import java.lang.Math.max

/**
 * Manages the header frame of the dialog, including the optional icon and title.
 *
 * @author Aidan Follestad (afollestad)
 */
@RestrictTo(LIBRARY_GROUP)
class DialogTitleLayout(
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
  internal var iconPosition: IconPosition = IconPosition.LEFT_OF_TITLE

  private val iconTitleMarginBottom: Int
    get() = if (iconPosition == IconPosition.ABOVE_TITLE) dimenPx(R.dimen.md_dialog_icon_title_layout_margin_bottom) else 0

  override fun onFinishInflate() {
    super.onFinishInflate()
    iconView = findViewById(R.id.md_icon_title)
    titleView = findViewById(R.id.md_text_title)
  }

  fun shouldNotBeVisible() = iconView.isNotVisible() && titleView.isNotVisible()

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
      if (iconPosition == IconPosition.LEFT_OF_TITLE) {
        titleMaxWidth -= (iconView.measuredWidth + iconMargin)
      }
    }

    titleView.measure(
        makeMeasureSpec(titleMaxWidth, AT_MOST),
        makeMeasureSpec(0, UNSPECIFIED)
    )

    val iconViewHeight =
      if (iconView.isVisible()) iconView.measuredHeight else 0

    val requiredHeight = when (iconPosition) {
      IconPosition.LEFT_OF_TITLE -> {
        max(iconViewHeight, titleView.measuredHeight)
      }
      IconPosition.ABOVE_TITLE -> {
        iconViewHeight + titleView.measuredHeight
      }
    }

    val actualHeight = requiredHeight + frameMarginVertical + titleMarginBottom + iconTitleMarginBottom

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

    val contentTop = frameMarginVertical
    val contentBottom = measuredHeight - titleMarginBottom + iconTitleMarginBottom
    val contentHeight = contentBottom - contentTop
    val contentMidPoint = contentBottom - (contentHeight / 2)

    val titleHalfHeight = titleView.measuredHeight / 2
    var titleTop = contentMidPoint - titleHalfHeight
    var titleBottom = contentMidPoint + titleHalfHeight
    var titleLeft: Int
    var titleRight: Int

    if (isRtl()) {
      titleRight = measuredWidth - frameMarginHorizontal
      titleLeft = frameMarginHorizontal
    } else {
      titleLeft = frameMarginHorizontal
      titleRight = measuredWidth - frameMarginHorizontal
    }

    if (iconView.isVisible()) {
      val iconHalfHeight = iconView.measuredHeight / 2

      val iconTop: Int
      val iconBottom: Int
      val iconLeft: Int
      val iconRight: Int

      when (iconPosition) {
        IconPosition.LEFT_OF_TITLE -> {
          iconTop = contentMidPoint - iconHalfHeight
          iconBottom = contentMidPoint + iconHalfHeight

          if (isRtl()) {
            iconRight = titleRight
            iconLeft = iconRight - iconView.measuredWidth
            titleRight = iconLeft - iconMargin
            titleLeft = titleRight - titleView.measuredWidth
          } else {
            iconLeft = titleLeft
            iconRight = iconLeft + iconView.measuredWidth
            titleLeft = iconRight + iconMargin
            titleRight = titleLeft + titleView.measuredWidth
          }
        }

        IconPosition.ABOVE_TITLE -> {
          titleTop = contentMidPoint - titleHalfHeight + iconHalfHeight
          titleBottom = contentMidPoint + titleHalfHeight + iconHalfHeight

          iconTop = contentTop
          iconBottom = titleTop - iconTitleMarginBottom
          iconLeft = left + iconView.measuredWidth
          iconRight = right - iconView.measuredWidth
        }
      }

      iconView.layout(iconLeft, iconTop, iconRight, iconBottom)
    }

    titleView.layout(titleLeft, titleTop, titleRight, titleBottom)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

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
