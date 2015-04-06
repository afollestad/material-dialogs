package com.afollestad.materialdialogs.base;

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
public class DialogBase extends AlertDialog implements DialogInterface.OnShowListener {

    private OnShowListener mShowListener;
    protected ContextThemeWrapper mThemedContext;

    protected DialogBase(Context context) {
        super(context);
    }

    protected void setVerticalMargins(View view, int topMargin, int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        boolean changed = false;
        if (topMargin > -1 && params.topMargin != topMargin) {
            params.topMargin = topMargin;
            changed = true;
        }
        if (bottomMargin > -1 && params.bottomMargin != bottomMargin) {
            params.bottomMargin = bottomMargin;
            changed = true;
        }
        if (changed)
            view.setLayoutParams(params);
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

    public final void _setViewInternal(View view) {
        super.setView(view);
    }

    public final void _setOnShowListenerInternal() {
        super.setOnShowListener(this);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (mShowListener != null)
            mShowListener.onShow(dialog);
    }
}