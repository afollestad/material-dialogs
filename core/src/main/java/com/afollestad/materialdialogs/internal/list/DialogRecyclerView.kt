/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.internal.list

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.internal.main.DialogLayout
import com.afollestad.materialdialogs.utils.invalidateDividers
import com.afollestad.materialdialogs.utils.waitForLayout

typealias InvalidateDividersDelegate = (scrolledDown: Boolean, atBottom: Boolean) -> Unit

/**
 * A [RecyclerView] which reports whether or not it's scrollable, along with reporting back to a
 * [DialogLayout] to invalidate dividers.
 *
 * @author Aidan Follestad (afollestad)
 */
class DialogRecyclerView(
  context: Context,
  attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

  private var invalidateDividersDelegate: InvalidateDividersDelegate? = null

  fun attach(dialog: MaterialDialog) {
    this.invalidateDividersDelegate = dialog::invalidateDividers
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    waitForLayout { invalidateDividers() }
    addOnScrollListener(scrollListeners)
  }

  override fun onDetachedFromWindow() {
    removeOnScrollListener(scrollListeners)
    super.onDetachedFromWindow()
  }

  override fun onScrollChanged(
    left: Int,
    top: Int,
    oldl: Int,
    oldt: Int
  ) {
    super.onScrollChanged(left, top, oldl, oldt)
    invalidateDividers()
  }

  private fun isAtTop(): Boolean {
    if (!isScrollable()) {
      return false
    }
    val lm = layoutManager
    return when (lm) {
      is LinearLayoutManager -> lm.findFirstCompletelyVisibleItemPosition() == 0
      is GridLayoutManager -> lm.findFirstCompletelyVisibleItemPosition() == 0
      else -> false
    }
  }

  private fun isAtBottom(): Boolean {
    if (!isScrollable()) {
      return false
    }
    val lastIndex = adapter!!.itemCount - 1
    val lm = layoutManager
    return when (lm) {
      is LinearLayoutManager -> lm.findLastVisibleItemPosition() == lastIndex
      is GridLayoutManager -> lm.findLastVisibleItemPosition() == lastIndex
      else -> false
    }
  }

  private val scrollListeners = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(
      recyclerView: RecyclerView,
      dx: Int,
      dy: Int
    ) {
      super.onScrolled(recyclerView, dx, dy)
      invalidateDividers()
    }
  }

  private fun invalidateDividers() {
    if (childCount == 0 || measuredHeight == 0) {
      return
    }
    invalidateDividersDelegate?.invoke(!isAtTop(), !isAtBottom())
  }

  private fun isScrollable(): Boolean {
    if (adapter == null) return false
    val lm = layoutManager
    val itemCount = adapter!!.itemCount
    @Suppress("UNREACHABLE_CODE")
    return when (lm) {
      is LinearLayoutManager -> {
        val diff = lm.findLastVisibleItemPosition() - lm.findFirstVisibleItemPosition()
        return itemCount > diff
      }
      is GridLayoutManager -> {
        val diff = lm.findLastVisibleItemPosition() - lm.findFirstVisibleItemPosition()
        return itemCount > diff
      }
      else -> {
        Log.w(
            "MaterialDialogs",
            "LayoutManager of type ${lm!!.javaClass.name} is currently unsupported."
        )
        return false
      }
    }
  }
}
