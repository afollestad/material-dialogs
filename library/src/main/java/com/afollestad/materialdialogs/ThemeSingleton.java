package com.afollestad.materialdialogs;

import android.graphics.drawable.Drawable;

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
    public int accentColor = 0;
    public int itemColor = 0;
    public Drawable icon = null;
    public int backgroundColor = 0;
    public int dividerColor = 0;
    public Drawable selector;
    public Drawable btnSelector;
}
