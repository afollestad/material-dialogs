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
package com.afollestad.materialdialogs.internal.message

import android.graphics.Rect
import android.text.Spannable
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.method.TransformationMethod
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView

/** https://medium.com/@nullthemall/make-textview-open-links-in-customtabs-12fdcf4bb684 */
internal class LinkTransformationMethod(
  private val onLinkClick: (link: String) -> Unit
) : TransformationMethod {
  override fun getTransformation(
    source: CharSequence,
    view: View
  ): CharSequence {
    if (view !is TextView) {
      return source
    } else if (view.text == null || view.text !is Spannable) {
      return source
    }
    val text = view.text as Spannable
    val spans = text.getSpans(0, view.length(), URLSpan::class.java)
    for (span in spans) {
      val start = text.getSpanStart(span)
      val end = text.getSpanEnd(span)
      val url = span.url

      text.removeSpan(span)
      text.setSpan(CustomUrlSpan(url, onLinkClick), start, end, SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return text
  }

  override fun onFocusChanged(
    view: View,
    sourceText: CharSequence,
    focused: Boolean,
    direction: Int,
    previouslyFocusedRect: Rect
  ) = Unit
}
