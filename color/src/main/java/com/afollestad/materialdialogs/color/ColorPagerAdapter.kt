package com.afollestad.materialdialogs.color

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

internal class ColorPagerAdapter : PagerAdapter() {
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
}