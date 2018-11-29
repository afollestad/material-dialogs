/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.color

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx
import kotlin.math.abs

/** @author Aidan Follestad (afollestad) */
class SeekBarGroupLayout(
  context: Context,
  attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

  private val tolerance = dimenPx(R.dimen.seekbar_grouplayout_tolerance)
  private var seekBars = listOf<SeekBar>()
  private var grabbedBar: SeekBar? = null

  override fun onFinishInflate() {
    super.onFinishInflate()

    val mySeekBars = mutableListOf<SeekBar>()
    for (i in 0 until childCount) {
      val child = getChildAt(i) as? SeekBar
      child?.let(mySeekBars::add)
    }
    this.seekBars = mySeekBars
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val action = event.actionMasked

    when (action) {
      ACTION_DOWN -> {
        val target = closestSeekBar(event)
        if (target != null) {
          log("Grabbed: ${target.idName()}")
          grabbedBar = target
          target.dispatchTouchEvent(event)
          return true
        }
      }
      ACTION_MOVE -> {
        if (grabbedBar != null) {
          grabbedBar!!.dispatchTouchEvent(event)
          return true
        }
      }
      ACTION_UP -> {
        if (grabbedBar != null) {
          log("Released: ${grabbedBar.idName()}")
          grabbedBar!!.dispatchTouchEvent(event)
          grabbedBar = null
          return true
        }
      }
    }

    return super.onTouchEvent(event)
  }

  private fun closestSeekBar(event: MotionEvent): SeekBar? {
    val y = event.y
    var closest: SeekBar? = null
    var smallestDiff = -1

    for (bar in seekBars) {
      val diff = abs(y - bar.middleY()).toInt()
      log("Diff from ${bar.idName()} = $diff, tolerance = $tolerance")

      if (diff <= tolerance && (smallestDiff == -1 || diff < smallestDiff)) {
        log("New closest: ${bar.idName()}")
        closest = bar
        smallestDiff = diff
      }
    }

    log("Final closest: ${closest?.idName()}")
    return closest
  }

  private fun View.middleY() = this.y + (this.measuredHeight / 2f)

  private fun SeekBar?.idName(): String {
    return if (this == null) "" else this.resources.getResourceEntryName(this.id)
  }

  private fun log(message: String) {
    if (BuildConfig.DEBUG) {
      Log.d("SeekBarGroupLayout", message)
    }
  }
}
