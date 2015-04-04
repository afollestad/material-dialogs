package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MDCheckBox extends CheckBox {

    public MDCheckBox(Context context) {
        super(context);
    }

    public MDCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MDCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MDCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int color;

    @Override
    protected boolean verifyDrawable(Drawable who) {
        boolean val = super.verifyDrawable(who);
        if (who != null)
            who.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return val;
    }

    public void setColorFilter(int color) {
        this.color = color;
//        invalidate();
    }
}
