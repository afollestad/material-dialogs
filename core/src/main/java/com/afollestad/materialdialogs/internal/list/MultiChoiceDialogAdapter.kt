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
package com.afollestad.materialdialogs.internal.list

import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.hasActionButtons
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.list.MultiChoiceListener
import com.afollestad.materialdialogs.list.getItemSelector
import com.afollestad.materialdialogs.utils.appendAll
import com.afollestad.materialdialogs.utils.inflate
import com.afollestad.materialdialogs.utils.pullIndices
import com.afollestad.materialdialogs.utils.removeAll
import com.afollestad.materialdialogs.utils.MDUtil.maybeSetTextColor

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
  internal var items: List<String>,
  disabledItems: IntArray?,
  initialSelection: IntArray,
  private val waitForActionButton: Boolean,
  private val allowEmptySelection: Boolean,
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
  private var disabledIndices: IntArray = disabledItems ?: IntArray(0)

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
      dialog.setActionButtonEnabled(POSITIVE, allowEmptySelection || currentSelection.isNotEmpty())
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
    val viewHolder = MultiChoiceViewHolder(
        itemView = listItemView,
        adapter = this
    )
    viewHolder.titleView.maybeSetTextColor(dialog.windowContext, R.attr.md_color_content)
    return viewHolder
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

    if (dialog.bodyFont != null) {
      holder.titleView.typeface = dialog.bodyFont
    }
  }

  override fun positiveButtonClicked() {
    if (allowEmptySelection || currentSelection.isNotEmpty()) {
      val selectedItems = items.pullIndices(currentSelection)
      selection?.invoke(dialog, currentSelection, selectedItems)
    }
  }

  override fun replaceItems(
    items: List<String>,
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

  override fun checkItems(indices: IntArray) {
    val existingSelection = this.currentSelection
    val indicesToAdd = indices.filter { !existingSelection.contains(it) }
    this.currentSelection = this.currentSelection.appendAll(indicesToAdd)
  }

  override fun uncheckItems(indices: IntArray) {
    val existingSelection = this.currentSelection
    val indicesToAdd = indices.filter { existingSelection.contains(it) }
    this.currentSelection = this.currentSelection.removeAll(indicesToAdd)
  }

  override fun toggleItems(indices: IntArray) {
    val newSelection = this.currentSelection.toMutableList()
    for (target in indices) {
      if (this.disabledIndices.contains(target)) continue
      if (newSelection.contains(target)) {
        newSelection.remove(target)
      } else {
        newSelection.add(target)
      }
    }
    this.currentSelection = newSelection.toIntArray()
  }

  override fun checkAllItems() {
    val existingSelection = this.currentSelection
    val wholeRange = IntArray(itemCount) { it }
    val indicesToAdd = wholeRange.filter { !existingSelection.contains(it) }
    this.currentSelection = this.currentSelection.appendAll(indicesToAdd)
  }

  override fun uncheckAllItems() {
    this.currentSelection = intArrayOf()
  }

  override fun toggleAllChecked() {
    if (this.currentSelection.isEmpty()) {
      checkAllItems()
    } else {
      uncheckAllItems()
    }
  }

  override fun isItemChecked(index: Int) = this.currentSelection.contains(index)
}
