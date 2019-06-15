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
package com.afollestad.materialdialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleRes
import com.afollestad.materialdialogs.WhichButton.NEGATIVE
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.internal.main.DialogLayout
import com.afollestad.materialdialogs.utils.MDUtil.getWidthAndHeight
import com.afollestad.materialdialogs.utils.isVisible
import kotlin.math.min

/** @author Aidan Follestad (@afollestad) */
interface DialogBehavior {
  /** Gets the styles.xml theme for the dialog Window. */
  @StyleRes fun getThemeRes(isDark: Boolean): Int

  /** Creates the root layout of the dialog. */
  fun createView(
    creatingContext: Context,
    dialogWindow: Window,
    layoutInflater: LayoutInflater,
    dialog: MaterialDialog
  ): ViewGroup

  /** Retrieves the [DialogLayout] from the view inflated in [createView]. */
  fun getDialogLayout(root: ViewGroup): DialogLayout

  /** Sets window constraints, width and height. */
  fun setWindowConstraints(
    context: Context,
    window: Window,
    view: DialogLayout,
    @Px maxWidth: Int?
  )

  /** Sets the root dialog background. */
  fun setBackgroundColor(
    view: DialogLayout,
    @ColorInt color: Int,
    cornerRounding: Float
  )

  /** Called when the dialog is about to be shown. */
  fun onPreShow(dialog: MaterialDialog)

  /** Called when the dialog is has been shown. */
  fun onPostShow(dialog: MaterialDialog)

  /**
   * Called when the dialog is being dismissed. Return true if you've handled
   * it, and if super.dismiss() should NOT be called on the dialog. This is an
   * opportunity to cleanup resources, as well.
   */
  fun onDismiss(): Boolean
}

/** @author Aidan Follestad (@afollestad) */
object ModalDialog : DialogBehavior {
  override fun getThemeRes(isDark: Boolean): Int {
    return if (isDark) {
      R.style.MD_Dark
    } else {
      R.style.MD_Light
    }
  }

  @SuppressLint("InflateParams")
  override fun createView(
    creatingContext: Context,
    dialogWindow: Window,
    layoutInflater: LayoutInflater,
    dialog: MaterialDialog
  ): ViewGroup {
    return layoutInflater.inflate(
        R.layout.md_dialog_base,
        null,
        false
    ) as ViewGroup
  }

  override fun getDialogLayout(root: ViewGroup): DialogLayout {
    return root as DialogLayout
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
    val wm = window.windowManager ?: return
    val res = context.resources
    val (windowWidth, windowHeight) = wm.getWidthAndHeight()

    val windowVerticalPadding =
      res.getDimensionPixelSize(R.dimen.md_dialog_vertical_margin)
    view.maxHeight = windowHeight - windowVerticalPadding * 2

    val lp = LayoutParams().apply {
      copyFrom(window.attributes)

      val windowHorizontalPadding =
        res.getDimensionPixelSize(R.dimen.md_dialog_horizontal_margin)
      val calculatedWidth = windowWidth - windowHorizontalPadding * 2
      val actualMaxWidth =
        maxWidth ?: res.getDimensionPixelSize(R.dimen.md_dialog_max_width)
      width = min(actualMaxWidth, calculatedWidth)
    }
    window.attributes = lp
  }

  override fun setBackgroundColor(
    view: DialogLayout,
    @ColorInt color: Int,
    cornerRounding: Float
  ) {
    view.background = GradientDrawable().apply {
      cornerRadius = cornerRounding
      setColor(color)
    }
  }

  override fun onPreShow(dialog: MaterialDialog) = Unit

  override fun onPostShow(dialog: MaterialDialog) {
    val negativeBtn = dialog.getActionButton(NEGATIVE)
    if (negativeBtn.isVisible()) {
      negativeBtn.post { negativeBtn.requestFocus() }
      return
    }
    val positiveBtn = dialog.getActionButton(POSITIVE)
    if (positiveBtn.isVisible()) {
      positiveBtn.post { positiveBtn.requestFocus() }
    }
  }

  override fun onDismiss(): Boolean = false
}
