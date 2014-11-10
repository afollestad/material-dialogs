package com.afollestad.materialdialogs.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class ItemProcessor {

    private Context context;
    private LayoutInflater li;
    private final int defaultLayout;

    public ItemProcessor(Context context) {
        this.context = context;
        li = LayoutInflater.from(context);
        defaultLayout = R.layout.md_listitem;
    }

    protected final Context getContext() {
        return context;
    }

    /**
     * Returning 0 will use the default layout.
     *
     * @param forIndex The index of the item being inflated.
     */
    protected abstract int getLayout(int forIndex);

    /**
     * Called when the view is inflated and will soon be added to the list. You can setup views in your
     * list item here.
     *
     * @param forIndex The index of the item being inflated.
     * @param itemText The text associated with the current item from the array passed into items() from the Builder.
     * @param view The inflated view for the current item.
     */
    protected abstract void onViewInflated(int forIndex, String itemText, View view);

    /**
     * Used by MaterialDialog to inflate a list item view that will be displayed in a list.
     *
     * @param forIndex The index of the item being inflated.
     * @param itemText The text associated with the current item from the array passed into items() from the Builder.
     */
    public final View inflateItem(int forIndex, String itemText) {
        int itemLayout = getLayout(forIndex);
        if (itemLayout == 0) itemLayout = defaultLayout;
        View view = li.inflate(itemLayout, null);
        onViewInflated(forIndex, itemText, view);
        return view;
    }
}
