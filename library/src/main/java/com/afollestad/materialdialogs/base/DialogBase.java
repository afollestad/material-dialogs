package com.afollestad.materialdialogs.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Aidan Follestad (afollestad)
 */
public class DialogBase extends AlertDialog implements DialogInterface.OnShowListener {

    protected final static String POSITIVE = "POSITIVE";
    protected final static String NEGATIVE = "NEGATIVE";
    protected final static String NEUTRAL = "NEUTRAL";
    private OnShowListener mShowListener;

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

    /**
     * @deprecated Not supported by the Material dialog.
     */
    @Deprecated
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
    @Deprecated
    @Override
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        throw new RuntimeException("This method is not supported by the MaterialDialog.");
    }

    /**
     * @deprecated Use setContent() instead.
     */
    @Deprecated
    @Override
    public void setMessage(CharSequence message) {
        throw new RuntimeException("This method is not supported by the MaterialDialog, use setContent() instead.");
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
    public void onShow(DialogInterface dialog) {
        if (mShowListener != null)
            mShowListener.onShow(dialog);
    }

    protected void setBackgroundCompat(View view, Drawable d) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            view.setBackgroundDrawable(d);
        } else {
            view.setBackground(d);
        }
    }

    protected void setTypeface(TextView text, Typeface t) {
        if (t == null) return;
        int flags = text.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG;
        text.setPaintFlags(flags);
        text.setTypeface(t);
    }
}