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
import android.view.View
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.getSize
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.internal.button.DialogActionButtonLayout
import com.afollestad.materialdialogs.internal.list.DialogRecyclerView
import com.afollestad.materialdialogs.utils.MDUtil.getString
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
  private var scrollView: DialogScrollView? = null
  private var scrollFrame: ViewGroup? = null
  private var messageTextView: TextView? = null

  internal var recyclerView: DialogRecyclerView? = null
  internal var customView: View? = null

  fun setMessage(
    dialog: MaterialDialog,
    @StringRes res: Int?,
    text: CharSequence?,
    html: Boolean,
    lineHeightMultiplier: Float,
    typeface: Typeface?
  ) {
    addContentScrollView()
    if (messageTextView == null) {
      messageTextView = inflate(R.layout.md_dialog_stub_message, scrollFrame!!)
      scrollFrame!!.addView(messageTextView)
    }

    typeface.let { messageTextView?.typeface = it }
    messageTextView!!.apply {
      maybeSetTextColor(dialog.windowContext, R.attr.md_color_content)
      setText(text ?: getString(dialog, res, html = html))
      setLineSpacing(0f, lineHeightMultiplier)
      if (html) {
        movementMethod = LinkMovementMethod.getInstance()
      }
    }
  }

  fun addRecyclerView(
    dialog: MaterialDialog,
    adapter: RecyclerView.Adapter<*>
  ) {
    if (recyclerView == null) {
      recyclerView = inflate(R.layout.md_dialog_stub_recyclerview)
      recyclerView!!.attach(dialog)
      recyclerView!!.layoutManager = LinearLayoutManager(dialog.windowContext)
      addView(recyclerView)
    }
    recyclerView!!.adapter = adapter
  }

  fun addCustomView(
    @LayoutRes res: Int?,
    view: View?,
    scrollable: Boolean
  ) {
    check(customView == null) { "Custom view already set." }
    if (scrollable) {
      addContentScrollView()
      customView = view ?: inflate(res!!, scrollFrame)
      scrollFrame!!.addView(customView)
    } else {
      customView = view ?: inflate(res!!)
      addView(customView)
    }
  }

  fun haveMoreThanOneChild() = childCount > 1

  fun modifyFirstAndLastPadding(
    top: Int = -1,
    bottom: Int = -1
  ) {
    if (top != -1) {
      getChildAt(0).updatePadding(top = top)
    }
    if (bottom != -1) {
      getChildAt(childCount - 1).updatePadding(bottom = bottom)
    }
  }

  fun modifyScrollViewPadding(
    top: Int = -1,
    bottom: Int = -1
  ) {
    if (top != -1) {
      scrollView.updatePadding(top = top)
    }
    if (bottom != -1) {
      scrollView.updatePadding(bottom = bottom)
    }
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    val specWidth = getSize(widthMeasureSpec)
    val specHeight = getSize(heightMeasureSpec)

    // The ScrollView is the most important child view because it contains main content
    // like a message.
    scrollView?.measure(
        makeMeasureSpec(specWidth, EXACTLY),
        makeMeasureSpec(specHeight, AT_MOST)
    )
    val scrollViewHeight = scrollView?.measuredHeight ?: 0
    val remainingHeightAfterScrollView = specHeight - scrollViewHeight
    val childCountWithoutScrollView = if (scrollView != null) childCount - 1 else childCount

    if (childCountWithoutScrollView == 0) {
      // No more children to measure
      setMeasuredDimension(specWidth, scrollViewHeight)
      return
    }

    val heightPerRemainingChild = remainingHeightAfterScrollView / childCountWithoutScrollView

    var totalChildHeight = scrollViewHeight
    for (i in 0 until childCount) {
      val currentChild = getChildAt(i)
      if (currentChild.id == scrollView?.id) {
        continue
      }
      currentChild.measure(
          makeMeasureSpec(specWidth, EXACTLY),
          makeMeasureSpec(heightPerRemainingChild, AT_MOST)
      )
      totalChildHeight += currentChild.measuredHeight
    }

    setMeasuredDimension(specWidth, totalChildHeight)
  }

  override fun onLayout(
    changed: Boolean,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
  ) {
    var currentTop = 0
    for (i in 0 until childCount) {
      val currentChild = getChildAt(i)
      val currentBottom = currentTop + currentChild.measuredHeight
      currentChild.layout(
          0,
          currentTop,
          measuredWidth,
          currentBottom
      )
      currentTop = currentBottom
    }
  }

  private fun addContentScrollView() {
    if (scrollView == null) {
      scrollView = inflate(R.layout.md_dialog_stub_scrollview)
      scrollView!!.rootView = rootLayout
      scrollFrame = scrollView!!.getChildAt(0) as ViewGroup
      addView(scrollView)
    }
  }
}
