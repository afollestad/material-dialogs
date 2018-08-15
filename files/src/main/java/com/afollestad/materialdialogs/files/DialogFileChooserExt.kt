/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.files

import android.annotation.SuppressLint
import android.os.Environment.getExternalStorageDirectory
import android.support.annotation.CheckResult
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.internal.list.DialogRecyclerView
import java.io.File

typealias FileFilter = ((File) -> Boolean)?
typealias FileCallback = ((dialog: MaterialDialog, file: File) -> Unit)?

/**
 * Shows a dialog that lets the user select a local file.
 *
 * @param initialDirectory The directory that is listed initially, defaults to external storage.
 * @param filter A filter to apply when listing files, defaults to only show non-hidden files.
 * @param waitForPositiveButton When true, the callback isn't invoked until the user selects a
 *    file and taps on the positive action button. Defaults to true if the dialog has buttons.
 * @param emptyTextRes A string resource displayed on the empty view shown when a directory is
 *    empty. Defaults to "This folder's empty!".
 * @param selection A callback invoked when a file is selected.
 */
@SuppressLint("CheckResult")
@CheckResult
fun MaterialDialog.fileChooser(
  initialDirectory: File = getExternalStorageDirectory(),
  filter: FileFilter = { !it.isHidden },
  waitForPositiveButton: Boolean = true,
  emptyTextRes: Int = R.string.files_default_empty_text,
  selection: FileCallback = null
): MaterialDialog {
  if (!hasReadStoragePermission()) {
    throw IllegalStateException("You must have the READ_EXTERNAL_STORAGE permission first.")
  }
  customView(R.layout.md_file_chooser_base)
  setActionButtonEnabled(POSITIVE, false)

  val customView = getCustomView()!!
  val list: DialogRecyclerView = customView.findViewById(R.id.list)
  val emptyText: TextView = customView.findViewById(R.id.empty_text)
  emptyText.setText(emptyTextRes)

  list.attach(this)
  list.layoutManager = LinearLayoutManager(windowContext)
  val adapter = FileChooserAdapter(
      dialog = this,
      initialFolder = initialDirectory,
      waitForPositiveButton = waitForPositiveButton,
      emptyView = emptyText,
      onlyFolders = false,
      filter = filter,
      callback = selection
  )
  list.adapter = adapter

  if (waitForPositiveButton && selection != null) {
    setActionButtonEnabled(POSITIVE, false)
    positiveButton {
      val selectedFile = adapter.selectedFile
      if (selectedFile != null) {
        selection.invoke(this, selectedFile)
      }
    }
  }

  return this
}