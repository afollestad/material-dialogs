package com.afollestad.materialdialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * @author Aidan Follestad (afollestad)
 */
class DialogUtils {

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static int resolveColor(Context context, int attr) {
        return resolveColor(context, attr, 0);
    }

    public static int resolveColor(Context context, int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static Drawable resolveDrawable(Context context, int attr) {
        return resolveDrawable(context, attr, null);
    }

    private static Drawable resolveDrawable(Context context, int attr, Drawable fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            Drawable d = a.getDrawable(0);
            if (d == null && fallback != null)
                d = fallback;
            return d;
        } finally {
            a.recycle();
        }
    }

    public static void setBackground(View view, Drawable d) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            view.setBackgroundDrawable(d);
        else
            view.setBackground(d);
    }
}
