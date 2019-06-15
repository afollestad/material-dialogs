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
package com.afollestad.materialdialogs.bottomsheets

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build.VERSION.SDK_INT
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.LayoutMode.MATCH_PARENT
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout
import com.afollestad.materialdialogs.internal.button.shouldBeVisible
import com.afollestad.materialdialogs.internal.main.DialogLayout
import com.afollestad.materialdialogs.utils.MDUtil.getWidthAndHeight
import com.afollestad.materialdialogs.utils.MDUtil.waitForHeight
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import kotlin.math.min
import kotlin.properties.Delegates.notNull

/** @author Aidan Follestad (@afollestad) */
class BottomSheet(
  private val layoutMode: LayoutMode = MATCH_PARENT
) : DialogBehavior {
  internal var bottomSheetBehavior: BottomSheetBehavior<*>? = null
  private var bottomSheetView: ViewGroup? = null

  private var rootView: CoordinatorLayout? = null
  private var buttonsLayout: DialogActionButtonLayout? = null
  private var dialog: MaterialDialog? = null

  internal var defaultPeekHeight: Int by notNull()
  internal var maxPeekHeight: Int = -1
  private var actualPeekHeight: Int by notNull()

  override fun getThemeRes(isDark: Boolean): Int {
    return if (isDark) {
      R.style.MD_Dark_BottomSheet
    } else {
      R.style.MD_Light_BottomSheet
    }
  }

  @SuppressLint("InflateParams")
  override fun createView(
    creatingContext: Context,
    dialogWindow: Window,
    layoutInflater: LayoutInflater,
    dialog: MaterialDialog
  ): ViewGroup {
    rootView = layoutInflater.inflate(
        R.layout.md_dialog_base_bottomsheet,
        null,
        false
    ) as CoordinatorLayout

    this.dialog = dialog
    this.bottomSheetView = rootView!!.findViewById(R.id.md_root_bottom_sheet)
    this.buttonsLayout = rootView!!.findViewById(R.id.md_button_layout)

    val (_, windowHeight) = dialogWindow.windowManager.getWidthAndHeight()
    defaultPeekHeight = (windowHeight * DEFAULT_PEEK_HEIGHT_RATIO).toInt()
    actualPeekHeight = defaultPeekHeight
    maxPeekHeight = windowHeight

    setupBottomSheetBehavior()
    if (creatingContext is Activity) {
      carryOverWindowFlags(
          dialogWindow = dialogWindow,
          creatingActivity = creatingContext
      )
    }

    return rootView!!
  }

  private fun carryOverWindowFlags(
    dialogWindow: Window,
    creatingActivity: Activity
  ) {
    val activityWindow = creatingActivity.window!!
    if (SDK_INT >= 21) {
      dialogWindow.navigationBarColor = activityWindow.navigationBarColor
    }
  }

  private fun setupBottomSheetBehavior() {
    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        .apply {
          isHideable = true
          // start at 0 so we can animate it up when the dialog lays out the view
          peekHeight = 0
        }

    bottomSheetBehavior!!.setCallbacks(
        onSlide = { currentHeight ->
          // Slide the buttons layout down as the bottom sheet is hiding itself
          val buttonsLayoutHeight = buttonsLayout?.measuredHeight ?: currentHeight + 1
          if (currentHeight in 1..buttonsLayoutHeight) {
            val diff = buttonsLayoutHeight - currentHeight
            buttonsLayout?.translationY = diff.toFloat()
          } else if (currentHeight > 0) {
            buttonsLayout?.translationY = 0f
          }
          // Show divider over buttons layout if sheet is sliding down
          invalidateDividers(currentHeight)
        },
        onHide = {
          buttonsLayout?.visibility = GONE
          dialog?.dismiss()
        }
    )

    bottomSheetView!!.waitForHeight {
      actualPeekHeight = min(defaultPeekHeight, min(this.measuredHeight, defaultPeekHeight))
    }
  }

  private fun invalidateDividers(currentHeight: Int) {
    val contentLayout = dialog?.view?.contentLayout ?: return
    val mainViewHeight = dialog?.view?.measuredHeight ?: return
    val scrollView = contentLayout.scrollView
    val recyclerView = contentLayout.recyclerView
    when {
      currentHeight < mainViewHeight -> buttonsLayout?.drawDivider = true
      scrollView != null -> scrollView.invalidateDividers()
      recyclerView != null -> recyclerView.invalidateDividers()
      else -> buttonsLayout?.drawDivider = false
    }
  }

  override fun getDialogLayout(root: ViewGroup): DialogLayout {
    return (root.findViewById(R.id.md_root) as DialogLayout).also { dialogLayout ->
      dialogLayout.layoutMode = layoutMode
      dialogLayout.attachButtonsLayout(buttonsLayout!!)
    }
  }

  override fun setWindowConstraints(
    context: Context,
    window: Window,
    view: DialogLayout,
    maxWidth: Int?
  ) {
    if (maxWidth == 0) {
      // Postpone
      return
    }
    window.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    val lp = LayoutParams()
        .apply {
          copyFrom(window.attributes)
          width = LayoutParams.MATCH_PARENT
          height = LayoutParams.MATCH_PARENT
        }
    window.attributes = lp
  }

  override fun setBackgroundColor(
    view: DialogLayout,
    color: Int,
    cornerRounding: Float
  ) {
    bottomSheetView?.background = GradientDrawable().apply {
      cornerRadii = floatArrayOf(
          cornerRounding, cornerRounding, // top left
          cornerRounding, cornerRounding, // top right
          0f, 0f, // bottom left
          0f, 0f // bottom right
      )
      setColor(color)
    }
    buttonsLayout?.setBackgroundColor(color)
  }

  override fun onPreShow(dialog: MaterialDialog) {
    if (dialog.cancelOnTouchOutside) {
      // Clicking outside the bottom sheet dismisses the dialog
      rootView?.setOnClickListener { this.dialog?.dismiss() }
    }

    bottomSheetView!!.waitForHeight {
      bottomSheetBehavior?.peekHeight = 0
      bottomSheetBehavior?.state = STATE_COLLAPSED
      bottomSheetBehavior?.animatePeekHeight(
          view = bottomSheetView!!,
          start = 0,
          dest = actualPeekHeight,
          duration = LAYOUT_PEEK_CHANGE_DURATION_MS,
          onEnd = {
            invalidateDividers(actualPeekHeight)
          }
      )
      showButtons()
    }
  }

  override fun onPostShow(dialog: MaterialDialog) = Unit

  override fun onDismiss(): Boolean {
    if (dialog != null &&
        bottomSheetBehavior != null &&
        bottomSheetBehavior!!.state != STATE_HIDDEN
    ) {
      bottomSheetBehavior!!.state = STATE_HIDDEN
      hideButtons()
      return true
    }
    return false
  }

  private fun showButtons() {
    if (!buttonsLayout.shouldBeVisible()) {
      return
    }
    val start = buttonsLayout!!.measuredHeight
    buttonsLayout?.translationY = start.toFloat()
    buttonsLayout?.visibility = VISIBLE
    val animator = animateValues(
        from = start,
        to = 0,
        duration = BUTTONS_SHOW_DURATION_MS,
        onUpdate = { buttonsLayout?.translationY = it.toFloat() }
    )
    buttonsLayout?.onDetach { animator.cancel() }
    animator.apply {
      startDelay = BUTTONS_SHOW_START_DELAY_MS
      start()
    }
  }

  private fun hideButtons() {
    if (!buttonsLayout.shouldBeVisible()) return
    val animator = animateValues(
        from = 0,
        to = buttonsLayout!!.measuredHeight,
        duration = LAYOUT_PEEK_CHANGE_DURATION_MS,
        onUpdate = { buttonsLayout?.translationY = it.toFloat() }
    )
    buttonsLayout?.onDetach { animator.cancel() }
    animator.start()
  }

  companion object {
    internal const val LAYOUT_PEEK_CHANGE_DURATION_MS = 250L
    private const val DEFAULT_PEEK_HEIGHT_RATIO = 0.6f

    private const val BUTTONS_SHOW_START_DELAY_MS = 100L
    private const val BUTTONS_SHOW_DURATION_MS = 180L
  }
}
