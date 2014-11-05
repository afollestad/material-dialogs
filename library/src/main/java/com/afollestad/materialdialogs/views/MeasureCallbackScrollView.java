package com.afollestad.materialdialogs.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MeasureCallbackScrollView extends ScrollView {

    public MeasureCallbackScrollView(Context context) {
        super(context);
    }

    public MeasureCallbackScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureCallbackScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static interface Callback {
        void onMeasureScroll(ScrollView view);
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCallback != null)
            mCallback.onMeasureScroll(this);
    }
}
