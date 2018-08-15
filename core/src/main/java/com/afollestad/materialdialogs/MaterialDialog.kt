/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

@file:Suppress("unused")

package com.afollestad.materialdialogs

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope
import android.support.annotation.StringRes
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.Theme.Companion.inferTheme
import com.afollestad.materialdialogs.WhichButton.NEGATIVE
import com.afollestad.materialdialogs.WhichButton.NEUTRAL
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.callbacks.invokeAll
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout.Companion.INDEX_NEGATIVE
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout.Companion.INDEX_NEUTRAL
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout.Companion.INDEX_POSITIVE
import com.afollestad.materialdialogs.internal.list.DialogAdapter
import com.afollestad.materialdialogs.internal.list.DialogRecyclerView
import com.afollestad.materialdialogs.internal.main.DialogLayout
import com.afollestad.materialdialogs.internal.main.DialogScrollView
import com.afollestad.materialdialogs.list.getListAdapter
import com.afollestad.materialdialogs.shared.isVisible
import com.afollestad.materialdialogs.utilext.assertOneSet
import com.afollestad.materialdialogs.utilext.getString
import com.afollestad.materialdialogs.utilext.hideKeyboard
import com.afollestad.materialdialogs.utilext.inflate
import com.afollestad.materialdialogs.utilext.preShow
import com.afollestad.materialdialogs.utilext.setDefaults
import com.afollestad.materialdialogs.utilext.setIcon
import com.afollestad.materialdialogs.utilext.setText
import com.afollestad.materialdialogs.utilext.setWindowConstraints

typealias DialogCallback = (MaterialDialog) -> Unit

/** @author Aidan Follestad (afollestad) */
class MaterialDialog(
  val windowContext: Context
) : Dialog(windowContext, inferTheme(windowContext).styleRes) {

  /**
   * A named config map, used like tags for extensions.
   *
   * Developers extending functionality of Material Dialogs should not use things
   * like static variables to store things. They instead should be stored at a dialog
   * instance level, which is what this provides.
   */
  val config: MutableMap<String, Any> = mutableMapOf()

  /** Returns true if auto dismiss is enabled. */
  var autoDismissEnabled: Boolean = true
    internal set

  internal val view: DialogLayout = inflate(R.layout.md_dialog_base)
  private var textViewMessage: TextView? = null
  internal var contentScrollView: DialogScrollView? = null
  internal var contentScrollViewFrame: LinearLayout? = null
  internal var contentRecyclerView: DialogRecyclerView? = null
  internal var contentCustomView: View? = null

  internal val preShowListeners = mutableListOf<DialogCallback>()
  internal val showListeners = mutableListOf<DialogCallback>()
  internal val dismissListeners = mutableListOf<DialogCallback>()
  internal val cancelListeners = mutableListOf<DialogCallback>()

  private val positiveListeners = mutableListOf<DialogCallback>()
  private val negativeListeners = mutableListOf<DialogCallback>()
  private val neutralListeners = mutableListOf<DialogCallback>()

  init {
    setContentView(view)
    this.view.dialog = this
    setWindowConstraints()
    setDefaults()
  }

  /**
   * Shows an drawable to the left of the dialog title.
   *
   * @param res The drawable resource to display as the drawable.
   * @param drawable The drawable to display as the drawable.
   */
  @CheckResult
  fun icon(
    @DrawableRes res: Int? = null,
    drawable: Drawable? = null
  ): MaterialDialog {
    assertOneSet(res, drawable)
    setIcon(
        view.titleLayout.iconView,
        iconRes = res,
        icon = drawable
    )
    return this
  }

  /**
   * Shows a title, or header, at the top of the dialog.
   *
   * @param res The string resource to display as the title.
   * @param text The literal string to display as the title.
   */
  @CheckResult
  fun title(
    @StringRes res: Int? = null,
    text: String? = null
  ): MaterialDialog {
    assertOneSet(res, text)
    setText(
        view.titleLayout.titleView,
        textRes = res,
        text = text
    )
    return this
  }

  /**
   * Shows a message, below the title, and above the action buttons (and checkbox prompt).
   *
   * @param res The string resource to display as the message.
   * @param text The literal string to display as the message.
   */
  @CheckResult
  fun message(
    @StringRes res: Int? = null,
    text: CharSequence? = null
  ): MaterialDialog {
    if (this.contentCustomView != null) {
      throw IllegalStateException("message() should be used BEFORE customView().")
    }
    addContentScrollView()
    addContentMessageView(res, text)
    return this
  }

  /**
   * Shows a positive action button, in the far right at the bottom of the dialog.
   *
   * @param res The string resource to display on the title.
   * @param text The literal string to display on the button.
   * @param click A listener to invoke when the button is pressed.
   */
  @CheckResult
  fun positiveButton(
    @StringRes res: Int? = null,
    text: CharSequence? = null,
    click: DialogCallback? = null
  ): MaterialDialog {
    if (click != null) {
      positiveListeners.add(click)
    }

    val btn = view.buttonsLayout.actionButtons[INDEX_POSITIVE]
    if (res == null && text == null && btn.isVisible()) {
      // Didn't receive text and the button is already setup,
      // so just stop with the added listener.
      return this
    }

    setText(
        btn,
        textRes = res,
        text = text,
        fallback = android.R.string.ok
    )
    return this
  }

  /**
   * Shows a negative action button, to the left of the positive action button (or at the far
   * right if there is no positive action button).
   *
   * @param res The string resource to display on the title.
   * @param text The literal string to display on the button.
   * @param click A listener to invoke when the button is pressed.
   */
  @CheckResult
  fun negativeButton(
    @StringRes res: Int? = null,
    text: CharSequence? = null,
    click: DialogCallback? = null
  ): MaterialDialog {
    if (click != null) {
      negativeListeners.add(click)
    }

    val btn = view.buttonsLayout.actionButtons[INDEX_NEGATIVE]
    if (res == null && text == null && btn.isVisible()) {
      // Didn't receive text and the button is already setup,
      // so just stop with the added listener.
      return this
    }

    setText(
        btn,
        textRes = res,
        text = text,
        fallback = android.R.string.cancel
    )
    return this
  }

  @CheckResult
  @Deprecated(
      "Use of neutral buttons is discouraged, see " +
          "https://material.io/design/components/dialogs.html#actions."
  )
  fun neutralButton(
    @StringRes res: Int? = null,
    text: CharSequence? = null,
    click: DialogCallback? = null
  ): MaterialDialog {
    if (click != null) {
      neutralListeners.add(click)
    }

    val btn = view.buttonsLayout.actionButtons[INDEX_NEUTRAL]
    if (res == null && text == null && btn.isVisible()) {
      // Didn't receive text and the button is already setup,
      // so just stop with the added listener.
      return this
    }

    setText(
        btn,
        textRes = res,
        text = text
    )
    return this
  }

  /**
   * Turns off auto dismiss. Action button and list item clicks won't dismiss the dialog on their
   * own. You have to handle dismissing the dialog manually with the [dismiss] method.
   */
  @CheckResult
  fun noAutoDismiss(): MaterialDialog {
    this.autoDismissEnabled = false
    return this
  }

  /** Turns debug mode on or off. Draws spec guides over dialog views. */
  @CheckResult
  fun debugMode(debugMode: Boolean = true): MaterialDialog {
    this.view.debugMode = debugMode
    return this
  }

  /** Opens the dialog. */
  override fun show() {
    preShow()
    super.show()
  }

  /** Applies multiple properties to the dialog and opens it. */
  inline fun show(func: MaterialDialog.() -> Unit): MaterialDialog {
    this.func()
    this.show()
    return this
  }

  override fun dismiss() {
    hideKeyboard()
    super.dismiss()
  }

  @RestrictTo(Scope.LIBRARY_GROUP)
  fun invalidateDividers(
    scrolledDown: Boolean,
    atBottom: Boolean
  ) = view.invalidateDividers(scrolledDown, atBottom)

  @RestrictTo(Scope.LIBRARY_GROUP)
  fun isContentScrollViewAdded() = this.contentScrollView != null

  internal fun onActionButtonClicked(which: WhichButton) {
    when (which) {
      POSITIVE -> {
        positiveListeners.invokeAll(this)
        val adapter = getListAdapter() as? DialogAdapter<*, *>
        adapter?.positiveButtonClicked()
      }
      NEGATIVE -> negativeListeners.invokeAll(this)
      NEUTRAL -> neutralListeners.invokeAll(this)
    }
    if (autoDismissEnabled) {
      dismiss()
    }
  }

  internal fun addContentScrollView() {
    if (this.contentScrollView == null) {
      this.contentScrollView = inflate(R.layout.md_dialog_stub_scrollview, this.view)
      this.contentScrollView!!.rootView = this.view
      this.contentScrollViewFrame = this.contentScrollView!!.getChildAt(0) as LinearLayout
      this.view.addView(this.contentScrollView, 1)
    }
  }

  private fun addContentMessageView(@StringRes res: Int?, text: CharSequence?) {
    if (this.textViewMessage == null) {
      this.textViewMessage = inflate(
          R.layout.md_dialog_stub_message,
          this.contentScrollViewFrame!!
      )
      this.contentScrollViewFrame!!.addView(this.textViewMessage)
    }
    assertOneSet(res, text)
    this.textViewMessage!!.text = text ?: getString(res)
  }
}