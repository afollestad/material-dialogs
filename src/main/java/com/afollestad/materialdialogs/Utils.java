package com.afollestad.materialdialogs;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Utils {

    public static float convertToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
