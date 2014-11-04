package com.afollestad.materialdialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Utils {

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static int resolveColor(Context context, int attr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }
}
