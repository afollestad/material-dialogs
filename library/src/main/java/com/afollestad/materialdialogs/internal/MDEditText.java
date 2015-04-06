package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MDEditText extends EditText {

    public MDEditText(Context context) {
        super(context);
    }

    public MDEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MDEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MDEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setColorFilter(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackgroundTintList(ColorStateList.valueOf(color));
        } else {
            getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
}