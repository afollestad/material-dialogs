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
package com.afollestad.materialdialogs.internal.button

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.getSize
import android.view.View.MeasureSpec.makeMeasureSpec
import androidx.appcompat.widget.AppCompatCheckBox
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.internal.main.BaseSubLayout
import com.afollestad.materialdialogs.internal.main.DEBUG_COLOR_BLUE
import com.afollestad.materialdialogs.internal.main.DEBUG_COLOR_DARK_PINK
import com.afollestad.materialdialogs.internal.main.DEBUG_COLOR_PINK
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx
import com.afollestad.materialdialogs.utils.isRtl
import com.afollestad.materialdialogs.utils.isVisible

/**
 * Manages a set of three [DialogActionButton]'s (measuring, layout, etc.).
 * Handles switching between stacked and unstacked configuration.
 *
 * Also handles an optional checkbox prompt.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class DialogActionButtonLayout(
  context: Context,
  attrs: AttributeSet? = null
) : BaseSubLayout(context, attrs) {

  companion object {
    const val INDEX_POSITIVE = 0
    const val INDEX_NEGATIVE = 1
    const val INDEX_NEUTRAL = 2
  }

  private val buttonHeightDefault = dimenPx(R.dimen.md_action_button_height)
  private val buttonHeightStacked = dimenPx(R.dimen.md_stacked_action_button_height)
  private val buttonFramePadding = dimenPx(R.dimen.md_action_button_frame_padding)
  private val buttonFramePaddingNeutral = dimenPx(R.dimen.md_action_button_frame_padding_neutral)
  private val buttonSpacing = 0

  private val checkBoxPromptMarginVertical = dimenPx(R.dimen.md_checkbox_prompt_margin_vertical)
  private val checkBoxPromptMarginHorizontal = dimenPx(R.dimen.md_checkbox_prompt_margin_horizontal)

  private var stackButtons: Boolean = false

  lateinit var actionButtons: Array<DialogActionButton>
  lateinit var checkBoxPrompt: AppCompatCheckBox

  val visibleButtons: Array<DialogActionButton>
    get() = actionButtons.filter { it.isVisible() }
        .toTypedArray()

  fun shouldBeVisible() = visibleButtons.isNotEmpty() || checkBoxPrompt.isVisible()

  override fun onFinishInflate() {
    super.onFinishInflate()
    actionButtons = arrayOf(
        findViewById(R.id.md_button_positive),
        findViewById(R.id.md_button_negative),
        findViewById(R.id.md_button_neutral)
    )
    checkBoxPrompt = findViewById(R.id.md_checkbox_prompt)

    for ((i, btn) in actionButtons.withIndex()) {
      val which = WhichButton.fromIndex(i)
      btn.setOnClickListener { dialogParent().dialog.onActionButtonClicked(which) }
    }
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    if (!shouldBeVisible()) {
      setMeasuredDimension(0, 0)
      return
    }

    val parentWidth = getSize(widthMeasureSpec)

    if (checkBoxPrompt.isVisible()) {
      val checkboxPromptMaxWidth = parentWidth - (checkBoxPromptMarginHorizontal * 2)
      checkBoxPrompt.measure(
          makeMeasureSpec(checkboxPromptMaxWidth, AT_MOST),
          makeMeasureSpec(0, UNSPECIFIED)
      )
    }

    // Buttons plus any spacing around that makes up the "frame"
    val baseContext = dialogParent().dialog.context
    val appContext = dialogParent().dialog.windowContext
    for (button in visibleButtons) {
      button.update(
          baseContext = baseContext,
          appContext = appContext,
          stacked = stackButtons
      )
      if (stackButtons) {
        button.measure(
            makeMeasureSpec(parentWidth, EXACTLY),
            makeMeasureSpec(buttonHeightStacked, EXACTLY)
        )
      } else {
        button.measure(
            makeMeasureSpec(0, UNSPECIFIED),
            makeMeasureSpec(buttonHeightDefault, EXACTLY)
        )
      }
    }

    if (visibleButtons.isNotEmpty() && !stackButtons) {
      var totalWidth = 0
      for (button in visibleButtons) {
        totalWidth += button.measuredWidth + buttonSpacing
      }
      if (totalWidth >= parentWidth && !stackButtons) {
        stackButtons = true
        for (button in visibleButtons) {
          button.update(
              baseContext = baseContext,
              appContext = appContext,
              stacked = true
          )
          button.measure(
              makeMeasureSpec(parentWidth, EXACTLY),
              makeMeasureSpec(buttonHeightStacked, EXACTLY)
          )
        }
      }
    }

    var totalHeight = requiredHeightForButtons()
    if (checkBoxPrompt.isVisible()) {
      totalHeight += checkBoxPrompt.measuredHeight + (checkBoxPromptMarginVertical * 2)
    }

    setMeasuredDimension(parentWidth, totalHeight)
  }

  override fun onLayout(
    changed: Boolean,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
  ) {
    if (!shouldBeVisible()) {
      return
    }

    if (checkBoxPrompt.isVisible()) {
      val promptLeft: Int
      val promptTop: Int
      val promptRight: Int
      val promptBottom: Int
      if (isRtl()) {
        promptRight = measuredWidth - checkBoxPromptMarginHorizontal
        promptTop = checkBoxPromptMarginVertical
        promptLeft = promptRight - checkBoxPrompt.measuredWidth
        promptBottom = promptTop + checkBoxPrompt.measuredHeight
      } else {
        promptLeft = checkBoxPromptMarginHorizontal
        promptTop = checkBoxPromptMarginVertical
        promptRight = promptLeft + checkBoxPrompt.measuredWidth
        promptBottom = promptTop + checkBoxPrompt.measuredHeight
      }
      checkBoxPrompt.layout(
          promptLeft,
          promptTop,
          promptRight,
          promptBottom
      )
    }

    if (stackButtons) {
      var topY = measuredHeight - requiredHeightForButtons()
      for (button in visibleButtons) {
        val bottomY = topY + buttonHeightStacked
        button.layout(0, topY, measuredWidth, bottomY)
        topY = bottomY
      }
    } else if (isRtl()) {
      val topY = measuredHeight - (requiredHeightForButtons() - buttonFramePadding)
      val bottomY = measuredHeight - buttonFramePadding

      if (actionButtons[INDEX_NEUTRAL].isVisible()) {
        val btn = actionButtons[INDEX_NEUTRAL]
        val rightX = measuredWidth - buttonFramePaddingNeutral
        btn.layout(
            rightX - btn.measuredWidth,
            topY,
            rightX,
            bottomY
        )
      }

      var leftX = buttonFramePadding
      if (actionButtons[INDEX_POSITIVE].isVisible()) {
        val btn = actionButtons[INDEX_POSITIVE]
        val rightX = leftX + btn.measuredWidth
        btn.layout(leftX, topY, rightX, bottomY)
        leftX = rightX + buttonSpacing
      }
      if (actionButtons[INDEX_NEGATIVE].isVisible()) {
        val btn = actionButtons[INDEX_NEGATIVE]
        val rightX = leftX + btn.measuredWidth
        btn.layout(leftX, topY, rightX, bottomY)
      }
    } else {
      val topY = measuredHeight - (requiredHeightForButtons() - buttonFramePadding)
      val bottomY = measuredHeight - buttonFramePadding

      if (actionButtons[INDEX_NEUTRAL].isVisible()) {
        val btn = actionButtons[INDEX_NEUTRAL]
        val leftX = buttonFramePaddingNeutral
        btn.layout(
            leftX,
            topY,
            leftX + btn.measuredWidth,
            bottomY
        )
      }

      var rightX = measuredWidth - buttonFramePadding
      if (actionButtons[INDEX_POSITIVE].isVisible()) {
        val btn = actionButtons[INDEX_POSITIVE]
        val leftX = rightX - btn.measuredWidth
        btn.layout(leftX, topY, rightX, bottomY)
        rightX = leftX - buttonSpacing
      }
      if (actionButtons[INDEX_NEGATIVE].isVisible()) {
        val btn = actionButtons[INDEX_NEGATIVE]
        val leftX = rightX - btn.measuredWidth
        btn.layout(leftX, topY, rightX, bottomY)
      }
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    if (dialogParent().debugMode) {
      if (visibleButtons.isNotEmpty()) {
        if (stackButtons) {
          // Fill below buttons
          canvas.drawRect(
              0f,
              measuredHeight.toFloat() - buttonFramePadding,
              measuredWidth.toFloat(),
              measuredHeight.toFloat(),
              debugPaint(DEBUG_COLOR_PINK)
          )
          // Outline on buttons
          var bottom = measuredHeight - buttonFramePadding
          for (i in visibleButtons.size - 1 downTo 0) {
            val top = bottom - buttonHeightStacked
            canvas.drawRect(
                0f,
                top.toFloat(),
                measuredWidth.toFloat(),
                bottom.toFloat(),
                debugPaint(DEBUG_COLOR_DARK_PINK, stroke = true)
            )
            bottom = top
          }
        } else {
          // Fill below buttons
          val bottomFillTop = measuredHeight.toFloat() - buttonFramePadding
          canvas.drawRect(
              0f,
              bottomFillTop,
              measuredWidth.toFloat(),
              measuredHeight.toFloat(),
              debugPaint(DEBUG_COLOR_PINK)
          )
          // Fill above buttons
          val topFillTop = bottomFillTop - buttonHeightDefault - buttonFramePadding
          val topFillBottom = bottomFillTop - buttonHeightDefault
          canvas.drawRect(
              0f,
              topFillTop,
              measuredWidth.toFloat(),
              topFillBottom,
              debugPaint(DEBUG_COLOR_PINK)
          )

          // Fill over and between buttons
          var right = measuredWidth
          for (i in 0 until visibleButtons.size) {
            var left = right - buttonSpacing
            canvas.drawRect(
                left.toFloat(),
                topFillBottom - buttonFramePadding,
                right.toFloat(),
                measuredHeight.toFloat(),
                debugPaint(DEBUG_COLOR_DARK_PINK)
            )
            right -= buttonSpacing

            val currentButton = visibleButtons[i]
            left = right - currentButton.measuredWidth
            canvas.drawRect(
                left.toFloat(),
                topFillBottom,
                right.toFloat(),
                measuredHeight.toFloat() - buttonFramePadding,
                debugPaint(DEBUG_COLOR_BLUE)
            )
            right = left
          }
        }
      }
    }

    if (drawDivider) {
      canvas.drawLine(
          0f,
          0f,
          measuredWidth.toFloat(),
          dividerHeight.toFloat(),
          dividerPaint()
      )
    }
  }

  private fun requiredHeightForButtons() = when {
    visibleButtons.isEmpty() -> 0
    stackButtons -> (visibleButtons.size * buttonHeightStacked) + buttonFramePadding
    else -> buttonHeightDefault + (buttonFramePadding * 2)
  }
}
