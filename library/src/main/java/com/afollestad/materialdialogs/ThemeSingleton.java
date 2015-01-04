package com.afollestad.materialdialogs;

import android.support.annotation.ColorRes;
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

//    <attr name="md_dark_theme" format="boolean" />
//    <attr name="md_title_color" format="color" />
//    <attr name="md_content_color" format="color" />
//    <attr name="md_accent_color" format="color" />
//    <attr name="md_item_color" format="color" />
//    <attr name="md_icon" format="reference" />
//    <attr name="md_background_color" format="color" />
//    <attr name="md_divider_color" format="color" />

    public boolean darkTheme = false;
    @ColorRes
    public int titleColor = -1;
    @ColorRes
    public int contentColor = -1;
    @ColorRes
    public int accentColor = -1;
    @ColorRes
    public int itemColor = -1;
    @DrawableRes
    public int icon = -1;
    @ColorRes
    public int backgroundColor = -1;
    @ColorRes
    public int dividerColor = -1;
}
