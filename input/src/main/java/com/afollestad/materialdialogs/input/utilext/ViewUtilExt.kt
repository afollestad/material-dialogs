/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.input.utilext

import android.view.View
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope

@RestrictTo(Scope.LIBRARY_GROUP)
inline fun <T : View> T.postApply(crossinline exec: T.() -> Unit) = this.post {
  this.exec()
}
