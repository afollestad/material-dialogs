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
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.util.AttributeSet
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.getSize
import android.view.View.MeasureSpec.makeMeasureSpec
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx

val DEBUG_COLOR_PINK = Color.parseColor("#EAA3CF")
val DEBUG_COLOR_DARK_PINK = Color.parseColor("#E066B1")
val DEBUG_COLOR_BLUE = Color.parseColor("#B5FAFB")

/**
 * The root layout of a dialog. Contains a [DialogTitleLayout], [DialogContentLayout],
 * and [DialogActionButtonLayout].
 *
 * @author Aidan Follestad (afollestad)
 */
internal class DialogLayout(
  context: Context,
  attrs: AttributeSet?
) : FrameLayout(context, attrs) {

  var maxHeight: Int = 0
  var debugMode: Boolean = false

  private val frameMarginVertical = dimenPx(R.dimen.md_dialog_frame_margin_vertical)
  private var debugPaint: Paint? = null

  internal val frameMarginVerticalLess = dimenPx(R.dimen.md_dialog_frame_margin_vertical_less)

  internal lateinit var dialog: MaterialDialog
  internal lateinit var titleLayout: DialogTitleLayout
  internal lateinit var contentLayout: DialogContentLayout
  internal lateinit var buttonsLayout: DialogActionButtonLayout

  init {
    setWillNotDraw(false)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    titleLayout = findViewById(R.id.md_title_layout)
    contentLayout = findViewById(R.id.md_content_layout)
    buttonsLayout = findViewById(R.id.md_button_layout)
  }

  internal fun invalidateDividers(
    scrolledDown: Boolean,
    atBottom: Boolean
  ) {
    titleLayout.drawDivider = scrolledDown
    buttonsLayout.drawDivider = atBottom
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    val specWidth = getSize(widthMeasureSpec)
    var specHeight = getSize(heightMeasureSpec)
    if (specHeight > maxHeight) {
      specHeight = maxHeight
    }

    titleLayout.measure(
        makeMeasureSpec(specWidth, EXACTLY),
        makeMeasureSpec(0, UNSPECIFIED)
    )
    if (buttonsLayout.shouldBeVisible()) {
      buttonsLayout.measure(
          makeMeasureSpec(specWidth, EXACTLY),
          makeMeasureSpec(0, UNSPECIFIED)
      )
    }

    val titleAndButtonsHeight =
      titleLayout.measuredHeight + buttonsLayout.measuredHeight
    val remainingHeight = specHeight - titleAndButtonsHeight
    contentLayout.measure(
        makeMeasureSpec(specWidth, EXACTLY),
        makeMeasureSpec(remainingHeight, AT_MOST)
    )

    val totalHeight = titleLayout.measuredHeight +
        contentLayout.measuredHeight +
        buttonsLayout.measuredHeight
    setMeasuredDimension(specWidth, totalHeight)
  }

  override fun onLayout(
    changed: Boolean,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
  ) {
    val titleLeft = 0
    val titleTop = 0
    val titleRight = measuredWidth
    val titleBottom = titleLayout.measuredHeight
    titleLayout.layout(
        titleLeft,
        titleTop,
        titleRight,
        titleBottom
    )

    val buttonsTop =
      measuredHeight - buttonsLayout.measuredHeight
    if (buttonsLayout.shouldBeVisible()) {
      val buttonsLeft = 0
      val buttonsRight = measuredWidth
      val buttonsBottom = measuredHeight
      buttonsLayout.layout(
          buttonsLeft,
          buttonsTop,
          buttonsRight,
          buttonsBottom
      )
    }

    val contentLeft = 0
    val contentRight = measuredWidth
    contentLayout.layout(
        contentLeft,
        titleBottom,
        contentRight,
        buttonsTop
    )
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    if (debugMode) {
      val contentPadding =
        if (titleLayout.shouldNotBeVisible()) frameMarginVerticalLess else frameMarginVertical
      if (titleLayout.shouldNotBeVisible()) {
        // Content top spacing
        canvas.drawRect(
            0f,
            titleLayout.bottom.toFloat(),
            measuredWidth.toFloat(),
            titleLayout.bottom.toFloat() + contentPadding,
            debugPaint(DEBUG_COLOR_BLUE)
        )
      }
      // Content bottom spacing
      canvas.drawRect(
          0f,
          buttonsLayout.top.toFloat() - contentPadding,
          measuredWidth.toFloat(),
          buttonsLayout.top.toFloat(),
          debugPaint(DEBUG_COLOR_BLUE)
      )
    }
  }

  fun debugPaint(
    @ColorInt color: Int,
    stroke: Boolean = false
  ): Paint {
    if (debugPaint == null) {
      debugPaint = Paint()
    }
    return debugPaint!!.apply {
      this.style = if (stroke) STROKE else FILL
      this.color = color
    }
  }
}
