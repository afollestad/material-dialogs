/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.internal.list

import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.hasActionButtons
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.list.MultiChoiceListener
import com.afollestad.materialdialogs.list.getItemSelector
import com.afollestad.materialdialogs.shared.inflate
import com.afollestad.materialdialogs.utilext.pullIndices

/** @author Aidan Follestad (afollestad) */
internal class MultiChoiceViewHolder(
  itemView: View,
  private val adapter: MultiChoiceDialogAdapter
) : RecyclerView.ViewHolder(itemView), OnClickListener {
  init {
    itemView.setOnClickListener(this)
  }

  val controlView: AppCompatCheckBox = itemView.findViewById(R.id.md_control)
  val titleView: TextView = itemView.findViewById(R.id.md_title)

  var isEnabled: Boolean
    get() = itemView.isEnabled
    set(value) {
      itemView.isEnabled = value
      controlView.isEnabled = value
      titleView.isEnabled = value
    }

  override fun onClick(view: View) = adapter.itemClicked(adapterPosition)
}

/**
 * The default list adapter for multiple choice (checkbox) list dialogs.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class MultiChoiceDialogAdapter(
  private var dialog: MaterialDialog,
  internal var items: Array<String>,
  disabledItems: IntArray?,
  initialSelection: IntArray,
  private val waitForActionButton: Boolean,
  internal var selection: MultiChoiceListener
) : RecyclerView.Adapter<MultiChoiceViewHolder>(), DialogAdapter<String, MultiChoiceListener> {

  private var currentSelection: IntArray = initialSelection
    set(value) {
      val previousSelection = field
      field = value
      for (previous in previousSelection) {
        if (!value.contains(previous)) {
          // This value was unselected
          notifyItemChanged(previous)
        }
      }
      for (current in value) {
        if (!previousSelection.contains(current)) {
          // This value was selected
          notifyItemChanged(current)
        }
      }
    }
  var disabledIndices: IntArray = disabledItems ?: IntArray(0)

  internal fun itemClicked(index: Int) {
    val newSelection = this.currentSelection.toMutableList()
    if (newSelection.contains(index)) {
      newSelection.remove(index)
    } else {
      newSelection.add(index)
    }
    this.currentSelection = newSelection.toIntArray()

    if (waitForActionButton && dialog.hasActionButtons()) {
      // Wait for action button, don't call listener
      // so that positive action button press can do so later.
      dialog.setActionButtonEnabled(POSITIVE, true)
    } else {
      // Don't wait for action button, call listener and dismiss if auto dismiss is applicable
      val selectedItems = this.items.pullIndices(this.currentSelection)
      this.selection?.invoke(dialog, this.currentSelection, selectedItems)
      if (dialog.autoDismissEnabled && !dialog.hasActionButtons()) {
        dialog.dismiss()
      }
    }
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): MultiChoiceViewHolder {
    val listItemView: View = parent.inflate(dialog.windowContext, R.layout.md_listitem_multichoice)
    return MultiChoiceViewHolder(
        itemView = listItemView,
        adapter = this
    )
  }

  override fun getItemCount() = items.size

  override fun onBindViewHolder(
    holder: MultiChoiceViewHolder,
    position: Int
  ) {
    holder.isEnabled = !disabledIndices.contains(position)

    holder.controlView.isChecked = currentSelection.contains(position)
    holder.titleView.text = items[position]
    holder.itemView.background = dialog.getItemSelector()
  }

  override fun positiveButtonClicked() {
    if (currentSelection.isNotEmpty()) {
      val selectedItems = items.pullIndices(currentSelection)
      selection?.invoke(dialog, currentSelection, selectedItems)
    }
  }

  override fun replaceItems(
    items: Array<String>,
    listener: MultiChoiceListener
  ) {
    this.items = items
    this.selection = listener
    this.notifyDataSetChanged()
  }

  override fun disableItems(indices: IntArray) {
    this.disabledIndices = indices
    notifyDataSetChanged()
  }
}