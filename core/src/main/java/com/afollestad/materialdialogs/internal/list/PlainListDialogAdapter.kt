/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.internal.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.actions.hasActionButtons
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.getItemSelector
import com.afollestad.materialdialogs.shared.inflate

private const val KEY_ACTIVATED_INDEX = "activated_index"

/** @author Aidan Follestad (afollestad) */
internal class PlainListViewHolder(
  itemView: View,
  private val adapter: PlainListDialogAdapter
) : RecyclerView.ViewHolder(itemView), OnClickListener {
  init {
    itemView.setOnClickListener(this)
  }

  val titleView = (itemView as ViewGroup).getChildAt(0) as TextView

  override fun onClick(view: View) = adapter.itemClicked(adapterPosition)
}

/**
 * The default list adapter for list dialogs, containing plain textual list items.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class PlainListDialogAdapter(
  private var dialog: MaterialDialog,
  internal var items: Array<String>,
  disabledItems: IntArray?,
  private var waitForActionButton: Boolean,
  internal var selection: ItemListener
) : RecyclerView.Adapter<PlainListViewHolder>(), DialogAdapter<String, ItemListener> {

  private var disabledIndices: IntArray = disabledItems ?: IntArray(0)

  fun itemClicked(index: Int) {
    if (waitForActionButton && dialog.hasActionButtons()) {
      // Wait for action button, mark clicked item as activated so that we can call the selection
      // listener when the positive action button is pressed.
      val lastActivated = dialog.config[KEY_ACTIVATED_INDEX] as? Int
      dialog.config[KEY_ACTIVATED_INDEX] = index
      if (lastActivated != null) {
        notifyItemChanged(lastActivated)
      }
      notifyItemChanged(index)
    } else {
      // Don't wait for action button, call listener and dismiss if auto dismiss is applicable
      this.selection?.invoke(dialog, index, this.items[index])
      if (dialog.autoDismissEnabled && !dialog.hasActionButtons()) {
        dialog.dismiss()
      }
    }
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): PlainListViewHolder {
    val listItemView: View = parent.inflate(dialog.windowContext, R.layout.md_listitem)
    return PlainListViewHolder(
        itemView = listItemView,
        adapter = this
    )
  }

  override fun getItemCount() = items.size

  override fun onBindViewHolder(
    holder: PlainListViewHolder,
    position: Int
  ) {
    holder.itemView.isEnabled = !disabledIndices.contains(position)

    val titleValue = items[position]
    holder.titleView.text = titleValue
    holder.itemView.background = dialog.getItemSelector()

    val activatedIndex = dialog.config[KEY_ACTIVATED_INDEX] as? Int
    holder.itemView.isActivated = activatedIndex != null && activatedIndex == position
  }

  override fun positiveButtonClicked() {
    val activatedIndex = dialog.config[KEY_ACTIVATED_INDEX] as? Int
    if (activatedIndex != null) {
      selection?.invoke(dialog, activatedIndex, items[activatedIndex])
      dialog.config.remove(KEY_ACTIVATED_INDEX)
    }
  }

  override fun replaceItems(
    items: Array<String>,
    listener: ItemListener
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