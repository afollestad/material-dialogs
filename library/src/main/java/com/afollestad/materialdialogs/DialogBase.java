package com.afollestad.materialdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;

import java.lang.reflect.Field;

/**
 * @author Aidan Follestad (afollestad)
 */
class DialogBase extends Dialog implements DialogInterface.OnShowListener {

    private OnShowListener mShowListener;

    protected DialogBase(Context context, int theme) {
        super(context, theme);
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

    @Override
    protected void onStop() {
        super.onStop();
        // Prevent framework leak
        try {
            Class superCls = getClass().getSuperclass().getSuperclass();
            Field cancelMsg = superCls.getDeclaredField("mCancelMessage");
            scrubMessageField(cancelMsg);
            Field dismissMsg = superCls.getDeclaredField("mDismissMessage");
            scrubMessageField(dismissMsg);
            Field showMsg = superCls.getDeclaredField("mShowMessage");
            scrubMessageField(showMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scrubMessageField(Field field) throws Exception {
        field.setAccessible(true);
        Object val = field.get(this);
        if (val != null) {
            Message msg = (Message) val;
            msg.recycle();
            field.set(this, null);
        }
    }
}