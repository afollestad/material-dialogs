package com.afollestad.materialdialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Aidan Follestad (afollestad)
 */
class DialogBase extends AlertDialog implements DialogInterface.OnShowListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {

    private OnShowListener mShowListener;
    private OnDismissListener mDismissListener;
    private OnCancelListener mCancelListener;
    protected ContextThemeWrapper mThemedContext;

    protected DialogBase(ContextThemeWrapper context) {
        super(context);
        mThemedContext = context;
    }

    public final Context getThemedContext() {
        return mThemedContext;
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Deprecated
    @Override
    public void setView(View view) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Deprecated
    @Override
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Deprecated
    @Override
    public void setCustomTitle(View customTitleView) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Deprecated
    @Override
    public void setButton(int whichButton, CharSequence text, Message msg) {
        throw new RuntimeException("Use setActionButton(MaterialDialog.Button, CharSequence) instead.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Deprecated
    @Override
    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        throw new RuntimeException("Use setActionButton(MaterialDialog.Button, CharSequence) instead.");
    }

    @Override
    public final void setOnShowListener(OnShowListener listener) {
        mShowListener = listener;
    }

    protected final void setOnShowListenerInternal() {
        super.setOnShowListener(this);
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        mDismissListener = listener;
    }

    public final void setOnDismissListenerInternal() {
        super.setOnDismissListener(this);
    }

    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        mCancelListener = listener;
    }

    public final void setOnCancelListenerInternal() {
        super.setOnCancelListener(this);
    }

    protected final void setViewInternal(View view) {
        super.setView(view);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (mShowListener != null)
            mShowListener.onShow(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDismissListener != null)
            mDismissListener.onDismiss(dialog);
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        if (mCancelListener != null)
            mCancelListener.onCancel(dialog);
    }
}