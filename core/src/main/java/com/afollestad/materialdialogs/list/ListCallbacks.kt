/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.list

import com.afollestad.materialdialogs.MaterialDialog

typealias ItemListener =
    ((dialog: MaterialDialog, index: Int, text: String) -> Unit)?

typealias SingleChoiceListener =
    ((dialog: MaterialDialog, index: Int, text: String) -> Unit)?

typealias MultiChoiceListener =
    ((dialog: MaterialDialog, indices: IntArray, items: List<String>) -> Unit)?
