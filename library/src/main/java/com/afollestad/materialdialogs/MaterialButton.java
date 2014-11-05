package com.afollestad.materialdialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialButton extends TextView {

    public MaterialButton(Context context) {
        super(context);
        init();
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        try {
            setBackgroundCompat(a.getDrawable(0));
        } finally {
            a.recycle();
        }
    }

    private void setBackgroundCompat(Drawable d) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(d);
        } else {
            setBackground(d);
        }
    }
}
