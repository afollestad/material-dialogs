package com.afollestad.materialdialogs.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MeasureCallbackListView extends ListView {

    public MeasureCallbackListView(Context context) {
        super(context);
    }

    public MeasureCallbackListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureCallbackListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static interface Callback {
        void onMeasureList(ListView view);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCallback != null)
            mCallback.onMeasureList(this);
    }
}