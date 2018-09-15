/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.files

import android.annotation.SuppressLint
import android.os.Environment.getExternalStorageDirectory
import android.support.annotation.CheckResult
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.files.utilext.hasReadStoragePermission
import com.afollestad.materialdialogs.files.utilext.hasWriteStoragePermission
import com.afollestad.materialdialogs.files.utilext.maybeSetTextColor
import com.afollestad.materialdialogs.files.utilext.updatePadding
import com.afollestad.materialdialogs.input.input
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
  allowFolderCreation: Boolean = false,
  @StringRes folderCreationLabel: Int? = null,
  selection: FileCallback = null
): MaterialDialog {
  if (allowFolderCreation && !hasWriteStoragePermission()) {
    throw IllegalStateException("You must have the WRITE_EXTERNAL_STORAGE permission first.")
  } else if (!hasReadStoragePermission()) {
    throw IllegalStateException("You must have the READ_EXTERNAL_STORAGE permission first.")
  }
  customView(R.layout.md_file_chooser_base)
  setActionButtonEnabled(POSITIVE, false)

  val customView = getCustomView()!!
  val list: DialogRecyclerView = customView.findViewById(R.id.list)
  val emptyText: TextView = customView.findViewById(R.id.empty_text)
  emptyText.setText(emptyTextRes)
  emptyText.maybeSetTextColor(windowContext, R.attr.md_color_content)

  list.attach(this)
  list.layoutManager = LinearLayoutManager(windowContext)
  val adapter = FileChooserAdapter(
      dialog = this,
      initialFolder = initialDirectory,
      waitForPositiveButton = waitForPositiveButton,
      emptyView = emptyText,
      onlyFolders = false,
      filter = filter,
      allowFolderCreation = allowFolderCreation,
      folderCreationLabel = folderCreationLabel,
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

  if (allowFolderCreation) {
    // Increase empty text top padding to make room for New Folder option
    emptyText.updatePadding(
        top = context.resources.getDimensionPixelSize(
            R.dimen.empty_text_padding_top_larger
        )
    )
  }

  return this
}

internal fun MaterialDialog.showNewFolderCreator(
  parent: File,
  @StringRes folderCreationLabel: Int?,
  onCreation: () -> Unit
) {
  MaterialDialog(windowContext).show {
    title(folderCreationLabel ?: R.string.files_new_folder)
    input(hintRes = R.string.files_new_folder_hint) { _, input ->
      File(parent, input.toString().trim()).mkdir()
      onCreation()
    }
  }
}
