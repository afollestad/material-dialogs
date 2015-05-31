package com.afollestad.materialdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.afollestad.materialdialogs.internal.MDRootLayout;

/**
 * @author Aidan Follestad (afollestad)
 */
class DialogBase extends Dialog implements DialogInterface.OnShowListener {

    protected MDRootLayout view;
    private OnShowListener mShowListener;

    protected DialogBase(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public View findViewById(int id) {
        return view.findViewById(id);
    }

    @Override
    public final void setOnShowListener(OnShowListener listener) {
        mShowListener = listener;
    }

    protected final void setOnShowListenerInternal() {
        super.setOnShowListener(this);
    }

    protected final void setViewInternal(View view) {
        setContentView(view);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (mShowListener != null)
            mShowListener.onShow(dialog);
    }
}