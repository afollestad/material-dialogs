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
package com.afollestad.materialdialogs.utils

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR1
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.afollestad.materialdialogs.MaterialDialog

@Suppress("UNCHECKED_CAST")
internal fun <R : View> ViewGroup.inflate(
  ctxt: Context = context,
  @LayoutRes res: Int
) = LayoutInflater.from(ctxt).inflate(res, this, false) as R

@Suppress("UNCHECKED_CAST")
internal fun <T> MaterialDialog.inflate(
  @LayoutRes res: Int,
  root: ViewGroup? = null
) = LayoutInflater.from(windowContext).inflate(res, root, false) as T

@Suppress("UNCHECKED_CAST")
internal fun <T> ViewGroup.inflate(
  @LayoutRes res: Int,
  root: ViewGroup? = this
) = LayoutInflater.from(context).inflate(res, root, false) as T

internal fun <T : View> T?.updatePadding(
  left: Int = this?.paddingLeft ?: 0,
  top: Int = this?.paddingTop ?: 0,
  right: Int = this?.paddingRight ?: 0,
  bottom: Int = this?.paddingBottom ?: 0
) {
  if (this != null &&
      left == this.paddingLeft &&
      top == this.paddingTop &&
      right == this.paddingRight &&
      bottom == this.paddingBottom
  ) {
    // no change needed, don't want to invalidate layout
    return
  }
  this?.setPadding(left, top, right, bottom)
}

internal inline fun <T : View> T.waitForLayout(crossinline f: T.() -> Unit) =
  viewTreeObserver.apply {
    addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
      override fun onGlobalLayout() {
        removeOnGlobalLayoutListener(this)
        this@waitForLayout.f()
      }
    })
  }!!

internal fun <T : View> T.isVisible(): Boolean {
  return if (this is Button) {
    this.visibility == View.VISIBLE && this.text.trim().isNotBlank()
  } else {
    this.visibility == View.VISIBLE
  }
}

internal fun <T : View> T.isNotVisible(): Boolean {
  return !isVisible()
}

internal fun <T : View> T.isRtl(): Boolean {
  if (SDK_INT < JELLY_BEAN_MR1) return false
  return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

internal fun TextView.setGravityStartCompat() {
  if (SDK_INT >= JELLY_BEAN_MR1) {
    this.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
  } else {
    this.gravity = Gravity.START
  }
}

internal fun TextView.setGravityEndCompat() {
  if (SDK_INT >= JELLY_BEAN_MR1) {
    this.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
  } else {
    this.gravity = Gravity.END
  }
}
