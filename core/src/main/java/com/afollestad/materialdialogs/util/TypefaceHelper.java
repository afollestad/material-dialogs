package com.afollestad.materialdialogs.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.SimpleArrayMap;

/*
    Each call to Typeface.createFromAsset will load a new instance of the typeface into memory,
    and this memory is not consistently get garbage collected
    http://code.google.com/p/android/issues/detail?id=9904
    (It states released but even on Lollipop you can see the typefaces accumulate even after
    multiple GC passes)

    You can detect this by running:
    adb shell dumpsys meminfo com.your.packagenage

    You will see output like:

     Asset Allocations
        zip:/data/app/com.your.packagenage-1.apk:/assets/Roboto-Medium.ttf: 125K
        zip:/data/app/com.your.packagenage-1.apk:/assets/Roboto-Medium.ttf: 125K
        zip:/data/app/com.your.packagenage-1.apk:/assets/Roboto-Medium.ttf: 125K
        zip:/data/app/com.your.packagenage-1.apk:/assets/Roboto-Regular.ttf: 123K
        zip:/data/app/com.your.packagenage-1.apk:/assets/Roboto-Medium.ttf: 125K

*/
public class TypefaceHelper {

    private static final SimpleArrayMap<String, Typeface> cache = new SimpleArrayMap<>();

    public static Typeface get(Context c, String name) {
        synchronized (cache) {
            if (!cache.containsKey(name)) {
                try {
                    Typeface t = Typeface.createFromAsset(
                            c.getAssets(), String.format("fonts/%s", name));
                    cache.put(name, t);
                    return t;
                } catch (RuntimeException e) {
                    return null;
                }
            }
            return cache.get(name);
        }
    }
}

