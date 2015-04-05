package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MDProgressBar extends ProgressBar {

    public MDProgressBar(Context context) {
        super(context);
    }

    public MDProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MDProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MDProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setColorFilter(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList stateList = ColorStateList.valueOf(color);
            setProgressTintList(stateList);
            setSecondaryProgressTintList(stateList);
            setIndeterminateTintList(stateList);
        } else {
            if (getIndeterminateDrawable() != null)
                getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            if (getProgressDrawable() != null)
                getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
}