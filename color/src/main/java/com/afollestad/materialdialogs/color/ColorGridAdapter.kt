/*
 * Licensed under Apache-2.0
 *
 * Designed an developed by Aidan Follestad (afollestad)
 */

package com.afollestad.materialdialogs.color

import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton.POSITIVE
import com.afollestad.materialdialogs.actions.hasActionButtons
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.shared.getColor
import com.afollestad.materialdialogs.shared.isColorDark
import com.afollestad.materialdialogs.shared.setVisibleOrGone

internal class ColorGridViewHolder(
  itemView: View,
  private val adapter: ColorGridAdapter
) : RecyclerView.ViewHolder(itemView), OnClickListener {

  init {
    itemView.setOnClickListener(this)
  }

  val colorCircle: ColorCircleView? = itemView.findViewById(R.id.color_view)
  val iconView: ImageView = itemView.findViewById(R.id.icon)

  override fun onClick(view: View) = adapter.itemSelected(adapterPosition)
}

/** @author Aidan Follestad (afollestad */
internal class ColorGridAdapter(
  private val dialog: MaterialDialog,
  private val colors: IntArray,
  private val subColors: Array<IntArray>?,
  @ColorInt private val initialSelection: Int?,
  private val waitForPositiveButton: Boolean,
  private val callback: ColorCallback
) : RecyclerView.Adapter<ColorGridViewHolder>() {

  private val upIcon =
    if (getColor(dialog.windowContext, attr = android.R.attr.textColorPrimary).isColorDark())
      R.drawable.icon_back_black
    else R.drawable.icon_back_white

  private var selectedTopIndex: Int = -1
  private var selectedSubIndex: Int = -1
  private var inSub: Boolean = false

  internal fun itemSelected(index: Int) {
    if (inSub && index == 0) {
      inSub = false
      notifyDataSetChanged()
      return
    }

    dialog.setActionButtonEnabled(POSITIVE, true)

    if (inSub) {
      val previousSelection = selectedSubIndex
      selectedSubIndex = index
      notifyItemChanged(previousSelection)
      notifyItemChanged(selectedSubIndex)
      invokeCallback()
      return
    }

    if (index != selectedTopIndex) {
      // Different than previous selected top, reset sub index
      selectedSubIndex = -1
    }

    selectedTopIndex = index
    if (subColors != null) {
      inSub = true
      // Preselect top color in sub-colors if it exists
      selectedSubIndex = subColors[selectedTopIndex].indexOfFirst { it == colors[selectedTopIndex] }
    }

    invokeCallback()
    notifyDataSetChanged()

  }

  fun selectedColor(): Int? {
    if (selectedTopIndex > -1) {
      if (selectedSubIndex > -1 && subColors != null) {
        return subColors[selectedTopIndex][selectedSubIndex - 1]
      }
      return colors[selectedTopIndex]
    }
    return null
  }

  init {
    if (initialSelection != null) {
      selectedTopIndex = colors.indexOfFirst { it == initialSelection }
      if (selectedTopIndex == -1 && subColors != null) {
        for (section in subColors) {
          selectedSubIndex = section.indexOfFirst { it == initialSelection }
          if (selectedSubIndex != -1) {
            inSub = true
            break
          }
        }
      }
    }
  }

  override fun getItemViewType(position: Int): Int {
    if (inSub && position == 0) {
      return 1
    }
    return 0
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ColorGridViewHolder {
    val layoutRes =
      if (viewType == 1) R.layout.md_color_grid_item_goup
      else R.layout.md_color_grid_item
    val view = LayoutInflater.from(parent.context)
        .inflate(layoutRes, parent, false)
    return ColorGridViewHolder(view, this)
  }

  override fun getItemCount() = if (inSub) subColors!![selectedTopIndex].size + 1 else colors.size

  override fun onBindViewHolder(
    holder: ColorGridViewHolder,
    position: Int
  ) {
    if (inSub && position == 0) {
      holder.iconView.setImageResource(upIcon)
      return
    }

    val color =
      if (inSub) subColors!![selectedTopIndex][position - 1]
      else colors[position]

    holder.colorCircle!!.color = color
    holder.colorCircle.border =
        getColor(holder.itemView.context, attr = android.R.attr.textColorPrimary)

    holder.iconView.setImageResource(
        if (color.isColorDark()) R.drawable.icon_checkmark_white
        else R.drawable.icon_checkmark_black
    )
    holder.iconView.setVisibleOrGone(
        if (inSub) position == selectedSubIndex
        else position == selectedTopIndex
    )
  }

  private fun invokeCallback() {
    val actualWaitForPositive = waitForPositiveButton && dialog.hasActionButtons()
    if (!actualWaitForPositive) {
      callback?.invoke(dialog, selectedColor()!!)
    }
  }
}