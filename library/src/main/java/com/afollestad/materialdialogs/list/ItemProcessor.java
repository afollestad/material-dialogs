package com.afollestad.materialdialogs.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class ItemProcessor {

    private LayoutInflater li;

    public ItemProcessor(Context context) {
        li = LayoutInflater.from(context);
    }

    protected abstract int getLayout();

    protected abstract void onViewInflated(View view);

    public final View inflateItem() {
        View view = li.inflate(getLayout(), null);
        onViewInflated(view);
        return view;
    }
}
