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
import com.afollestad.materialdialogs.utils.MDUtil.maybeSetTextColor
import com.afollestad.materialdialogs.utils.MDUtil.resolveString
import com.afollestad.materialdialogs.utils.inflate
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
      messageTextView = inflate<TextView>(R.layout.md_dialog_stub_message, scrollFrame!!).apply {
        scrollFrame!!.addView(this)
      }
    }

    typeface.let { messageTextView?.typeface = it }
    messageTextView?.run {
      maybeSetTextColor(dialog.windowContext, R.attr.md_color_content)
      setText(text ?: resolveString(dialog, res, html = html))
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
      recyclerView = inflate<DialogRecyclerView>(R.layout.md_dialog_stub_recyclerview).apply {
        this.attach(dialog)
        this.layoutManager = LinearLayoutManager(dialog.windowContext)
      }
      addView(recyclerView)
    }
    recyclerView?.adapter = adapter
  }

  fun addCustomView(
    @LayoutRes res: Int?,
    view: View?,
    scrollable: Boolean
  ) {
    check(customView == null) { "Custom view already set." }

    if (view != null && view.parent != null) {
      // Make sure the view is detached from any former parents.
      val parent = view.parent as? ViewGroup
      parent?.let { parent.removeView(view) }
    }

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
    val targetView = if (scrollView != null) scrollView else recyclerView
    if (top != -1) {
      targetView.updatePadding(top = top)
    }
    if (bottom != -1) {
      targetView.updatePadding(bottom = bottom)
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
      scrollView = inflate<DialogScrollView>(R.layout.md_dialog_stub_scrollview).apply {
        this.rootView = rootLayout
        scrollFrame = this.getChildAt(0) as ViewGroup
      }
      addView(scrollView)
    }
  }
}
