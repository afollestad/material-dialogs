package com.afollestad.materialdialogs.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.afollestad.materialdialogs.R;

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
        setFocusable(true);
        setFocusableInTouchMode(true);
//        TypedArray a = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.list_selector});
//        try {
//            setBackgroundCompat(a.getDrawable(0));
//        } finally {
//            a.recycle();
//        }
        setBackgroundCompat(getResources().getDrawable(R.drawable.list_selector));
    }

    private void setBackgroundCompat(Drawable d) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(d);
        } else {
            setBackground(d);
        }
    }
}
