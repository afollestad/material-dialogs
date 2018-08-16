/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.utilext

internal inline fun <reified T> Array<T>.pullIndices(indices: IntArray): Array<T> {
  val result = mutableListOf<T>()
  for (index in indices) {
    result.add(this[index])
  }
  return result.toTypedArray()
}