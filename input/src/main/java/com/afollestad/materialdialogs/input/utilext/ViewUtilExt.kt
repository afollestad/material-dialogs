/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.input.utilext

import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope
import android.view.View

@RestrictTo(Scope.LIBRARY_GROUP)
inline fun <T : View> T.postApply(crossinline exec: T.() -> Unit) = this.post {
  this.exec()
}
