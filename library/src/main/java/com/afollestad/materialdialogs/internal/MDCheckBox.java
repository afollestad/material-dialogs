package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.util.DialogUtils;

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

    public void setColorFilter(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            ColorStateList sl = new ColorStateList(new int[][]{
                    new int[]{-android.R.attr.state_checked},
                    new int[]{android.R.attr.state_checked}
            }, new int[]{
                    DialogUtils.resolveColor(getContext(), android.R.attr.textColorSecondary),
                    color
            });
            setButtonTintList(sl);
        }
    }
}