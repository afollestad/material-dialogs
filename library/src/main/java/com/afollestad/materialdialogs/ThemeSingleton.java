package com.afollestad.materialdialogs;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

/**
 * Use of this is discouraged for now; for internal use only. See the Global Theming section of the README.
 */
public class ThemeSingleton {

    private static ThemeSingleton singleton;

    public static ThemeSingleton get(boolean createIfNull) {
        if (singleton == null && createIfNull)
            singleton = new ThemeSingleton();
        return singleton;
    }

    public static ThemeSingleton get() {
        return get(true);
    }

    public boolean darkTheme = false;
    public int titleColor = 0;
    public int contentColor = 0;
    public int positiveColor = 0;
    public int neutralColor = 0;
    public int negativeColor = 0;
    public int itemColor = 0;
    public Drawable icon = null;
    public int backgroundColor = 0;
    public int dividerColor = 0;

    @DrawableRes
    public int listSelector = 0;
    @DrawableRes
    public int btnSelectorStacked = 0;
    @DrawableRes
    public int btnSelectorPositive = 0;
    @DrawableRes
    public int btnSelectorNeutral = 0;
    @DrawableRes
    public int btnSelectorNegative = 0;
}
