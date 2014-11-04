package com.afollestad.materialdialogs.base;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author Aidan Follestad (afollestad)
 */
public class DialogBase extends AlertDialog {

    protected final static String POSITIVE = "POSITIVE";
    protected final static String NEGATIVE = "NEGATIVE";
    protected final static String NEUTRAL = "NEUTRAL";

    protected DialogBase(Context context) {
        super(context);
    }

    /**
     * @deprecated Use getActionButton(com.afollestad.materialdialogs.DialogAction)} instead.
     */
    @Override
    public Button getButton(int whichButton) {
        throw new RuntimeException("Use getActionButton(MaterialDialog.Button) instead.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setView(View view) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    protected void setViewInternal(View view) {
        super.setView(view);
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setMessage(CharSequence message) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setTitle(CharSequence title) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setCustomTitle(View customTitleView) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setIcon(Drawable icon) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setIcon(int resId) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setIconAttribute(int attrId) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setButton(int whichButton, CharSequence text, Message msg) {
        throw new RuntimeException("Use setActionButton(MaterialDialog.Button, CharSequence) instead.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        throw new RuntimeException("Use setActionButton(MaterialDialog.Button, CharSequence) instead.");
    }

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Override
    public ListView getListView() {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }
}
