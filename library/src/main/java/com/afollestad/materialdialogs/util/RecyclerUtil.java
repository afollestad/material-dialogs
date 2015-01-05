package com.afollestad.materialdialogs.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

public class RecyclerUtil {

    public static boolean canRecyclerViewScroll(View view) {

        RecyclerView rv = (RecyclerView) view;

        final RecyclerView.LayoutManager lm = rv.getLayoutManager();
        final int count = rv.getAdapter().getItemCount();
        int lastVisible;

        if (lm instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) lm;
            lastVisible = llm.findLastVisibleItemPosition();
        } else if (lm instanceof GridLayoutManager) {
            GridLayoutManager glm = (GridLayoutManager) lm;
            lastVisible = glm.findLastVisibleItemPosition();
        } else {
            throw new MaterialDialog.NotImplementedException("Material Dialogs currently only supports LinearLayoutManager and GridLayoutManager. Please report any new layout managers.");
        }

        if (lastVisible == -1)
            return false;
        /* We scroll if the last item is not visible */
        final boolean lastItemVisible = lastVisible == count - 1;
        return !lastItemVisible || rv.getChildAt(rv.getChildCount() - 1).getBottom() > rv.getHeight() - rv.getPaddingBottom();
    }

    public static boolean isRecyclerView(View view) {
        return view instanceof RecyclerView;
    }
}
