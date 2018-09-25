/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.files

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.hasActionButtons
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.files.utilext.betterParent
import com.afollestad.materialdialogs.files.utilext.friendlyName
import com.afollestad.materialdialogs.files.utilext.getColor
import com.afollestad.materialdialogs.files.utilext.getDrawable
import com.afollestad.materialdialogs.files.utilext.hasParent
import com.afollestad.materialdialogs.files.utilext.isColorDark
import com.afollestad.materialdialogs.files.utilext.jumpOverEmulated
import com.afollestad.materialdialogs.files.utilext.maybeSetTextColor
import com.afollestad.materialdialogs.files.utilext.setVisible
import java.io.File

internal class FileChooserViewHolder(
  itemView: View,
  private val adapter: FileChooserAdapter
) : RecyclerView.ViewHolder(itemView), OnClickListener {

  init {
    itemView.setOnClickListener(this)
  }

  val iconView: ImageView = itemView.findViewById(R.id.icon)
  val nameView: TextView = itemView.findViewById(R.id.name)

  override fun onClick(view: View) = adapter.itemClicked(adapterPosition)
}

/** @author Aidan Follestad (afollestad */
internal class FileChooserAdapter(
  private val dialog: MaterialDialog,
  initialFolder: File,
  private val waitForPositiveButton: Boolean,
  private val emptyView: TextView,
  private val onlyFolders: Boolean,
  private val filter: FileFilter,
  private val allowFolderCreation: Boolean,
  @StringRes private val folderCreationLabel: Int?,
  private val callback: FileCallback
) : RecyclerView.Adapter<FileChooserViewHolder>() {

  var selectedFile: File? = null

  private var currentFolder = initialFolder
  private var listingJob: Job<List<File>>? = null
  private var contents: List<File>? = null

  private val isLightTheme =
    getColor(dialog.windowContext, attr = android.R.attr.textColorPrimary).isColorDark()

  init {
    dialog.onDismiss { listingJob?.abort() }
    loadContents(initialFolder)
  }

  fun itemClicked(index: Int) {
    if (currentFolder.hasParent() && index == goUpIndex()) {
      // go up
      loadContents(currentFolder.betterParent()!!)
      return
    } else if (currentFolder.canWrite() && allowFolderCreation && index == newFolderIndex()) {
      // New folder
      dialog.showNewFolderCreator(
          parent = currentFolder,
          folderCreationLabel = folderCreationLabel
      ) {
        // Refresh view
        loadContents(currentFolder)
      }
      return
    }

    val actualIndex = actualIndex(index)
    val selected = contents!![actualIndex].jumpOverEmulated()

    if (selected.isDirectory) {
      loadContents(selected)
    } else {
      val previousSelectedIndex = getSelectedIndex()
      this.selectedFile = selected
      val actualWaitForPositive = waitForPositiveButton && dialog.hasActionButtons()

      if (actualWaitForPositive) {
        dialog.setActionButtonEnabled(POSITIVE, true)
        notifyItemChanged(index)
        notifyItemChanged(previousSelectedIndex)
      } else {
        callback?.invoke(dialog, selected)
        dialog.dismiss()
      }
    }
  }

  private fun loadContents(directory: File) {
    if (onlyFolders) {
      this.selectedFile = directory
      dialog.setActionButtonEnabled(POSITIVE, true)
    }

    this.currentFolder = directory
    dialog.title(text = directory.friendlyName())

    listingJob?.abort()
    listingJob = job<List<File>> { _ ->
      val rawContents = directory.listFiles() ?: emptyArray()
      if (onlyFolders) {
        rawContents
            .filter { it.isDirectory && filter?.invoke(it) ?: true }
            .sortedBy { it.name.toLowerCase() }
      } else {
        rawContents
            .filter { filter?.invoke(it) ?: true }
            .sortedWith(compareBy({ !it.isDirectory }, { it.nameWithoutExtension.toLowerCase() }))
      }
    }.after {
      this.contents = it
      this.emptyView.setVisible(it.isEmpty())
      notifyDataSetChanged()
    }
  }

  override fun getItemCount(): Int {
    var count = contents?.size ?: 0
    if (currentFolder.hasParent()) {
      count += 1
    }
    if (allowFolderCreation && currentFolder.canWrite()) {
      count += 1
    }
    return count
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): FileChooserViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.md_file_chooser_item, parent, false)
    view.background = getDrawable(dialog.context, attr = R.attr.md_item_selector)

    val viewHolder = FileChooserViewHolder(view, this)
    viewHolder.nameView.maybeSetTextColor(dialog.windowContext, R.attr.md_color_content)
    return viewHolder
  }

  override fun onBindViewHolder(
    holder: FileChooserViewHolder,
    position: Int
  ) {
    if (currentFolder.hasParent() && position == goUpIndex()) {
      // Go up
      holder.iconView.setImageResource(
          if (isLightTheme) R.drawable.icon_return_dark
          else R.drawable.icon_return_light
      )
      holder.nameView.text = "..."
      holder.itemView.isActivated = false
      return
    }

    if (allowFolderCreation && currentFolder.canWrite() && position == newFolderIndex()) {
      // New folder
      holder.iconView.setImageResource(
          if (isLightTheme) R.drawable.icon_new_folder_dark
          else R.drawable.icon_new_folder_light
      )
      holder.nameView.text = dialog.windowContext.getString(
          folderCreationLabel ?: R.string.files_new_folder
      )
      holder.itemView.isActivated = false
      return
    }

    val actualIndex = actualIndex(position)
    val item = contents!![actualIndex]
    holder.iconView.setImageResource(item.iconRes())
    holder.nameView.text = item.name
    holder.itemView.isActivated = selectedFile?.absolutePath == item.absolutePath ?: false
  }

  private fun goUpIndex() = if (currentFolder.hasParent()) 0 else -1

  private fun newFolderIndex() = if (currentFolder.hasParent()) 1 else 0

  private fun actualIndex(position: Int): Int {
    var actualIndex = position
    if (currentFolder.hasParent()) {
      actualIndex -= 1
    }
    if (currentFolder.canWrite() && allowFolderCreation) {
      actualIndex -= 1
    }
    return actualIndex
  }

  private fun File.iconRes(): Int {
    return if (isLightTheme) {
      if (this.isDirectory) R.drawable.icon_folder_dark
      else R.drawable.icon_file_dark
    } else {
      if (this.isDirectory) R.drawable.icon_folder_light
      else R.drawable.icon_file_light
    }
  }

  private fun getSelectedIndex(): Int {
    if (selectedFile == null) return -1
    else if (contents?.isEmpty() == true) return -1
    val index = contents?.indexOfFirst { it.absolutePath == selectedFile!!.absolutePath } ?: -1
    return if (index > -1 && currentFolder.hasParent()) index + 1 else index
  }
}
