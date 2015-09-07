package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.Gravity;
import android.view.View;

public enum GravityEnum {
    START, CENTER, END;

    private static final boolean HAS_RTL = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

    @SuppressLint("RtlHardcoded")
    public int getGravityInt() {
        switch (this) {
            case START:
                return HAS_RTL ? Gravity.START : Gravity.LEFT;
            case CENTER:
                return Gravity.CENTER_HORIZONTAL;
            case END:
                return HAS_RTL ? Gravity.END : Gravity.RIGHT;
            default:
                throw new IllegalStateException("Invalid gravity constant");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int getTextAlignment() {
        switch (this) {
            case CENTER:
                return View.TEXT_ALIGNMENT_CENTER;
            case END:
                return View.TEXT_ALIGNMENT_VIEW_END;
            default:
                return View.TEXT_ALIGNMENT_VIEW_START;
        }
    }
}