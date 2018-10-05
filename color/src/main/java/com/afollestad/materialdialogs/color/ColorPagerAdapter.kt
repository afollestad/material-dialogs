package com.afollestad.materialdialogs.color

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.viewpager.widget.PagerAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.utils.getString

internal class ColorPagerAdapter(
        dialog: MaterialDialog,
        @StringRes resTabGrid: Int? = null,
        textTabGrid: String? = null,
        @StringRes resTabCustom: Int? = null,
        textTabCustom: String? = null) : PagerAdapter() {

    private var actualTabGridTitle: CharSequence
    private var actualTabCustomTitle: CharSequence

    init {
        actualTabGridTitle = textTabGrid ?: dialog.getString(resTabGrid, R.string.md_dialog_color_presets)!!
        actualTabCustomTitle = textTabCustom ?: dialog.getString(resTabCustom, R.string.md_dialog_color_custom)!!
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        var resId = 0
        when (position) {
            0 -> resId = R.id.rvGrid
            1 -> resId = R.id.llCustomColor
        }
        return collection.findViewById(resId)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1 as View
    }

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> actualTabGridTitle
        1 -> actualTabCustomTitle
        else -> null
    }
}