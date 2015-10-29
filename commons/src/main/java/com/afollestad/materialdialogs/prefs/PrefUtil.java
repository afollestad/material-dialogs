package com.afollestad.materialdialogs.prefs;

import android.content.res.XmlResourceParser;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.commons.R;

import java.lang.reflect.Method;

/**
 * @author Aidan Follestad (afollestad)
 */
class PrefUtil {

    private PrefUtil() {
    }

    public static void setLayoutResource(Preference preference, AttributeSet attrs) {
        boolean foundLayout = false;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            final String namespace = ((XmlResourceParser) attrs).getAttributeNamespace(0);
            if (namespace.equals("http://schemas.android.com/apk/res/android") &&
                    attrs.getAttributeName(i).equals("layout")) {
                foundLayout = true;
                break;
            }
        }
        if (!foundLayout)
            preference.setLayoutResource(R.layout.md_preference_custom);
    }

    public static void registerOnActivityDestroyListener(Preference preference, PreferenceManager.OnActivityDestroyListener listener) {
        try {
            PreferenceManager pm = preference.getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod(
                    "registerOnActivityDestroyListener",
                    PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, listener);
        } catch (Exception ignored) {
        }
    }

    public static void unregisterOnActivityDestroyListener(Preference preference, PreferenceManager.OnActivityDestroyListener listener) {
        try {
            PreferenceManager pm = preference.getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod(
                    "unregisterOnActivityDestroyListener",
                    PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, listener);
        } catch (Exception ignored) {
        }
    }
}
