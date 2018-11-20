/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.internal.main

import android.content.Context
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout
import com.afollestad.materialdialogs.utils.Util
import com.afollestad.materialdialogs.utils.inflate
import com.afollestad.materialdialogs.utils.maybeSetTextColor
import com.afollestad.materialdialogs.utils.updatePadding

/**
 * The middle section of the dialog, between [DialogTitleLayout] and
 * [DialogActionButtonLayout], which contains content such as messages,
 * lists, etc.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class DialogContentLayout(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  private var rootLayout: DialogLayout? = null
    get() = parent as DialogLayout
  private var contentScrollView: DialogScrollView? = null
  private var contentScrollFrame: ViewGroup? = null
  private var messageTextView: TextView? = null

  fun setMessage(
    @StringRes res: Int?,
    text: CharSequence?,
    html: Boolean,
    lineHeightMultiplier: Float,
    typeface: Typeface?
  ) {
    addContentScrollView()
    if (messageTextView == null) {
      messageTextView = inflate(R.layout.md_dialog_stub_message, contentScrollFrame!!)
      contentScrollFrame!!.addView(messageTextView)
    }
    typeface.let { messageTextView?.typeface = it }
    messageTextView!!.apply {
      maybeSetTextColor(context, R.attr.md_color_content)
      setText(text ?: Util.getString(context, res, html = html))
      setLineSpacing(0f, lineHeightMultiplier)
      if (html) {
        movementMethod = LinkMovementMethod.getInstance()
      }
    }
  }

  fun modifyPadding(
    top: Int,
    bottom: Int
  ) = contentScrollView.updatePadding(top = top, bottom = bottom)

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    val specWidth = MeasureSpec.getSize(widthMeasureSpec)
    val specHeight = MeasureSpec.getSize(heightMeasureSpec)

    contentScrollView?.measure(
        MeasureSpec.makeMeasureSpec(specWidth, EXACTLY),
        MeasureSpec.makeMeasureSpec(specHeight, AT_MOST)
    )

    val scrollWidth = contentScrollView?.measuredWidth ?: 0
    val scrollHeight = contentScrollView?.measuredHeight ?: 0

    setMeasuredDimension(scrollWidth, scrollHeight)
  }

  override fun onLayout(
    changed: Boolean,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
  ) {
    val scrollTop = paddingTop
    val scrollBottom = measuredHeight - paddingBottom

    contentScrollView?.layout(
        0,
        scrollTop,
        measuredWidth,
        scrollBottom
    )
  }

  private fun addContentScrollView() {
    if (contentScrollView == null) {
      contentScrollView = inflate(R.layout.md_dialog_stub_scrollview)
      contentScrollView!!.rootView = rootLayout
      contentScrollFrame = contentScrollView!!.getChildAt(0) as ViewGroup
      addView(contentScrollView)
    }
  }
}
