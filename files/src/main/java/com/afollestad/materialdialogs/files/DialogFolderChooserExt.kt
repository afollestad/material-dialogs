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
@file:Suppress("unused")

package com.afollestad.materialdialogs.files

import android.annotation.SuppressLint
import android.os.Environment.getExternalStorageDirectory
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.files.util.hasReadStoragePermission
import com.afollestad.materialdialogs.files.util.hasWriteStoragePermission
import com.afollestad.materialdialogs.internal.list.DialogRecyclerView
import com.afollestad.materialdialogs.utils.MDUtil.maybeSetTextColor
import java.io.File

/** Gets the selected folder for the current folder chooser dialog. */
@CheckResult
fun MaterialDialog.selectedFolder(): File? {
  val list: DialogRecyclerView = getCustomView().findViewById(R.id.list)
  return (list.adapter as? FileChooserAdapter)?.selectedFile
}

/**
 * Shows a dialog that lets the user select a local folder.
 *
 * @param initialDirectory The directory that is listed initially, defaults to external storage.
 * @param filter A filter to apply when listing folders, defaults to only show non-hidden folders.
 * @param waitForPositiveButton When true, the callback isn't invoked until the user selects a
 *    folder and taps on the positive action button. Defaults to true if the dialog has buttons.
 * @param emptyTextRes A string resource displayed on the empty view shown when a directory is
 *    empty. Defaults to "This folder's empty!".
 * @param selection A callback invoked when a folder is selected.
 */
@SuppressLint("CheckResult")
fun MaterialDialog.folderChooser(
  initialDirectory: File = getExternalStorageDirectory(),
  filter: FileFilter = null,
  waitForPositiveButton: Boolean = true,
  emptyTextRes: Int = R.string.files_default_empty_text,
  allowFolderCreation: Boolean = false,
  @StringRes folderCreationLabel: Int? = null,
  selection: FileCallback = null
): MaterialDialog {
  var actualFilter: FileFilter = filter

  if (allowFolderCreation) {
    check(hasWriteStoragePermission()) {
      "You must have the WRITE_EXTERNAL_STORAGE permission first."
    }
    check(initialDirectory.canWrite()) {
      "${initialDirectory.absolutePath} is not writeable to your app."
    }
    if (filter == null) {
      actualFilter = { !it.isHidden && it.canWrite() }
    }
  } else {
    check(hasReadStoragePermission()) {
      "You must have the READ_EXTERNAL_STORAGE permission first."
    }
    check(initialDirectory.canRead()) {
      "${initialDirectory.absolutePath} is not readable to your app."
    }
    if (filter == null) {
      actualFilter = { !it.isHidden && it.canRead() }
    }
  }

  customView(R.layout.md_file_chooser_base, noVerticalPadding = true)
  setActionButtonEnabled(POSITIVE, false)

  val customView = getCustomView()
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
      onlyFolders = true,
      filter = actualFilter,
      allowFolderCreation = allowFolderCreation,
      folderCreationLabel = folderCreationLabel,
      callback = selection
  )
  list.adapter = adapter

  if (waitForPositiveButton && selection != null) {
    positiveButton {
      val selectedFile = adapter.selectedFile
      if (selectedFile != null) {
        selection.invoke(this, selectedFile)
      }
    }
  }

  return this
}
