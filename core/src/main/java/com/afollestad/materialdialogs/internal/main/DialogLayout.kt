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
import android.content.Context.WINDOW_SERVICE
import android.graphics.Canvas
import android.graphics.Color.BLUE
import android.graphics.Color.CYAN
import android.graphics.Color.MAGENTA
import android.graphics.Color.RED
import android.graphics.Color.YELLOW
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.util.AttributeSet
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.getSize
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.LayoutMode.WRAP_CONTENT
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout
import com.afollestad.materialdialogs.internal.button.shouldBeVisible
import com.afollestad.materialdialogs.internal.message.DialogContentLayout
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx
import com.afollestad.materialdialogs.utils.MDUtil.getWidthAndHeight
import com.afollestad.materialdialogs.utils.dp
import com.afollestad.materialdialogs.utils.isRtl
import com.afollestad.materialdialogs.utils.isVisible

/**
 * The root layout of a dialog. Contains a [DialogTitleLayout], [DialogContentLayout],
 * and [DialogActionButtonLayout].
 *
 * @author Aidan Follestad (afollestad)
 */
class DialogLayout(
  context: Context,
  attrs: AttributeSet?
) : FrameLayout(context, attrs) {

  var maxHeight: Int = 0
  var debugMode: Boolean = false
    set(value) {
      field = value
      setWillNotDraw(!value)
    }
  private var debugPaint: Paint? = null

  internal val frameMarginVertical = dimenPx(R.dimen.md_dialog_frame_margin_vertical)
  internal val frameMarginVerticalLess = dimenPx(R.dimen.md_dialog_frame_margin_vertical_less)

  lateinit var dialog: MaterialDialog
  lateinit var titleLayout: DialogTitleLayout
  lateinit var contentLayout: DialogContentLayout
  var buttonsLayout: DialogActionButtonLayout? = null
  var layoutMode: LayoutMode = WRAP_CONTENT

  private var isButtonsLayoutAChild: Boolean = true
  private var windowHeight: Int = -1

  override fun onFinishInflate() {
    super.onFinishInflate()
    titleLayout = findViewById(R.id.md_title_layout)
    contentLayout = findViewById(R.id.md_content_layout)
    buttonsLayout = findViewById(R.id.md_button_layout)
  }

  fun attachDialog(dialog: MaterialDialog) {
    titleLayout.dialog = dialog
    buttonsLayout?.dialog = dialog
  }

  fun attachButtonsLayout(buttonsLayout: DialogActionButtonLayout) {
    this.buttonsLayout = buttonsLayout
    this.isButtonsLayoutAChild = false
  }

  /**
   * Shows or hides the top and bottom dividers, which separate the title, content, and buttons.
   */
  fun invalidateDividers(
    showTop: Boolean,
    showBottom: Boolean
  ) {
    titleLayout.drawDivider = showTop
    buttonsLayout?.drawDivider = showBottom
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    val (_, windowHeight) = windowManager.getWidthAndHeight()
    this.windowHeight = windowHeight
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    val specWidth = getSize(widthMeasureSpec)
    var specHeight = getSize(heightMeasureSpec)
    if (maxHeight in 1 until specHeight) {
      specHeight = maxHeight
    }

    titleLayout.measure(
        makeMeasureSpec(specWidth, EXACTLY),
        makeMeasureSpec(0, UNSPECIFIED)
    )
    if (buttonsLayout.shouldBeVisible()) {
      buttonsLayout!!.measure(
          makeMeasureSpec(specWidth, EXACTLY),
          makeMeasureSpec(0, UNSPECIFIED)
      )
    }

    val titleAndButtonsHeight =
      titleLayout.measuredHeight + (buttonsLayout?.measuredHeight ?: 0)
    val remainingHeight = specHeight - titleAndButtonsHeight
    contentLayout.measure(
        makeMeasureSpec(specWidth, EXACTLY),
        makeMeasureSpec(remainingHeight, AT_MOST)
    )

    if (layoutMode == WRAP_CONTENT) {
      val totalHeight = titleLayout.measuredHeight +
          contentLayout.measuredHeight +
          (buttonsLayout?.measuredHeight ?: 0)
      setMeasuredDimension(specWidth, totalHeight)
    } else {
      setMeasuredDimension(specWidth, windowHeight)
    }
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

    val buttonsTop: Int
    if (isButtonsLayoutAChild) {
      buttonsTop =
        measuredHeight - (buttonsLayout?.measuredHeight ?: 0)
      if (buttonsLayout.shouldBeVisible()) {
        val buttonsLeft = 0
        val buttonsRight = measuredWidth
        val buttonsBottom = measuredHeight
        buttonsLayout!!.layout(
            buttonsLeft,
            buttonsTop,
            buttonsRight,
            buttonsBottom
        )
      }
    } else {
      buttonsTop = measuredHeight
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
    if (!debugMode) return

    // Blue line on dialog left boundary
    canvas.verticalLine(BLUE, start = dp(24))
    // Blue line on dialog top boundary
    canvas.horizontalLine(BLUE, start = dp(24))
    // Blue line on dialog right boundary
    canvas.verticalLine(BLUE, start = measuredWidth - dp(24))

    // Red line under title layout
    if (titleLayout.isVisible()) {
      canvas.horizontalLine(RED, start = titleLayout.bottom.toFloat())
    }
    // Yellow line above content layout
    if (contentLayout.isVisible()) {
      canvas.horizontalLine(YELLOW, start = contentLayout.top.toFloat())
    }

    // Fill background of actual buttons
    if (!buttonsLayout.shouldBeVisible()) {
      return
    }

    // Cyan line on the end edge of the buttons
    val buttonsRight = if (isRtl()) {
      dp(8)
    } else {
      measuredWidth.toFloat() - dp(8)
    }
    canvas.verticalLine(CYAN, start = buttonsRight)

    if (buttonsLayout?.stackButtons == true) {
      // Fill visible parts of buttons
      var currentTop = buttonsLayout!!.top + dp(8)
      for (button in buttonsLayout!!.visibleButtons) {
        val currentBottom = currentTop + dp(36)
        canvas.box(
            CYAN,
            alpha = .4f,
            left = button.left.toFloat(),
            right = measuredWidth.toFloat() - dp(8),
            top = currentTop,
            bottom = currentBottom
        )
        currentTop = currentBottom + dp(16)
      }

      // Blue line over the top of the buttons layout
      canvas.horizontalLine(BLUE, start = buttonsLayout!!.top.toFloat())

      // Red line over the top edge of the buttons
      val buttonsTop = buttonsLayout!!.top.toFloat() + dp(8)
      val buttonsBottom = measuredHeight.toFloat() - dp(8)
      canvas.horizontalLine(RED, start = buttonsTop)
      canvas.horizontalLine(RED, start = buttonsBottom)
    } else if (buttonsLayout != null) {
      // Fill visible parts of buttons
      for (button in buttonsLayout!!.visibleButtons) {
        val top = buttonsLayout!!.top + button.top.toFloat() + dp(8)
        val bottom = buttonsLayout!!.bottom.toFloat() - dp(8)
        val left = button.left.toFloat() + dp(4)
        val right = button.right.toFloat() - dp(4)
        canvas.box(
            CYAN,
            alpha = .4f,
            left = left,
            right = right,
            top = top,
            bottom = bottom
        )
      }

      // Magenta line over the top of the buttons layout
      canvas.horizontalLine(MAGENTA, start = buttonsLayout!!.top.toFloat())
      // Red line over the top and bottom edge of the buttons
      val buttonsTop = measuredHeight.toFloat() - (dp(52) - dp(8))
      val buttonsBottom = measuredHeight.toFloat() - dp(8)
      canvas.horizontalLine(RED, start = buttonsTop)
      canvas.horizontalLine(RED, start = buttonsBottom)
      // Blue line over the top of the buttons with inset
      canvas.horizontalLine(BLUE, start = buttonsTop - dp(8))
    }
  }

  private fun paint(
    color: Int,
    alpha: Float = 1f
  ): Paint {
    if (debugPaint == null) {
      debugPaint = Paint().apply {
        strokeWidth = dp(1)
        style = FILL
        isAntiAlias = true
      }
    }
    return debugPaint!!.also {
      it.color = color
      setAlpha(alpha)
    }
  }

  private fun Canvas.box(
    @ColorInt color: Int,
    alpha: Float = 1f,
    left: Float,
    right: Float,
    top: Float,
    bottom: Float
  ) = drawRect(left, top, right, bottom, paint(color, alpha))

  private fun Canvas.line(
    @ColorInt color: Int,
    left: Float = 0f,
    right: Float = left,
    top: Float = 0f,
    bottom: Float = top
  ) = drawLine(left, top, right, bottom, paint(color))

  private fun Canvas.verticalLine(
    @ColorInt color: Int,
    start: Float,
    width: Float = start
  ) = line(color, left = start, right = width, top = 0f, bottom = measuredHeight.toFloat())

  private fun Canvas.horizontalLine(
    @ColorInt color: Int,
    start: Float = measuredHeight.toFloat(),
    height: Float = start
  ) = line(color, left = 0f, right = measuredWidth.toFloat(), top = start, bottom = height)
}
