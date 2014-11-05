package com.afollestad.materialdialogs.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MeasureCallbackLinearLayout extends LinearLayout {

    public MeasureCallbackLinearLayout(Context context) {
        super(context);
    }

    public MeasureCallbackLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureCallbackLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static interface Callback {
        void onMeasureLinear(LinearLayout view);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCallback != null)
            mCallback.onMeasureLinear(this);
    }
}
