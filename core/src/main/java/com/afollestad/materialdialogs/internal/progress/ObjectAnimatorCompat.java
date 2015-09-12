/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.afollestad.materialdialogs.internal.progress;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Path;
import android.os.Build;
import android.util.Property;

/**
 * Helper for accessing features in {@link ObjectAnimator} introduced after API level 11 (for
 * {@link android.animation.PropertyValuesHolder}) in a backward compatible fashion.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ObjectAnimatorCompat {

    private ObjectAnimatorCompat() {}

    /**
     * Constructs and returns an ObjectAnimator that animates between color values. A single
     * value implies that that value is the one being animated to. Two values imply starting
     * and ending values. More than two values imply a starting value, values to animate through
     * along the way, and an ending value (these values will be distributed evenly across
     * the duration of the animation).
     *
     * @param target The object whose property is to be animated. This object should
     * have a public method on it called <code>setName()</code>, where <code>name</code> is
     * the value of the <code>propertyName</code> parameter.
     * @param propertyName The name of the property being animated.
     * @param values A set of values that the animation will animate between over time.
     * @return An ObjectAnimator object that is set up to animate between the given values.
     */
    public static ObjectAnimator ofArgb(Object target, String propertyName, int... values) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ObjectAnimatorCompatLollipop.ofArgb(target, propertyName, values);
        }
        return ObjectAnimatorCompatBase.ofArgb(target, propertyName, values);
    }

    /**
     * Constructs and returns an ObjectAnimator that animates between color values. A single
     * value implies that that value is the one being animated to. Two values imply starting
     * and ending values. More than two values imply a starting value, values to animate through
     * along the way, and an ending value (these values will be distributed evenly across
     * the duration of the animation).
     *
     * @param target The object whose property is to be animated.
     * @param property The property being animated.
     * @param values A set of values that the animation will animate between over time.
     * @return An ObjectAnimator object that is set up to animate between the given values.
     */
    public static <T> ObjectAnimator ofArgb(T target, Property<T, Integer> property,
                                            int... values) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ObjectAnimatorCompatLollipop.ofArgb(target, property, values);
        }
        return ObjectAnimatorCompatBase.ofArgb(target, property, values);
    }

    /**
     * Constructs and returns an ObjectAnimator that animates coordinates along a <code>Path</code>
     * using two properties. A <code>Path</code></> animation moves in two dimensions, animating
     * coordinates <code>(x, y)</code> together to follow the line. In this variation, the
     * coordinates are floats that are set to separate properties designated by
     * <code>xPropertyName</code> and <code>yPropertyName</code>.
     *
     * @param target The object whose properties are to be animated. This object should
     *               have public methods on it called <code>setNameX()</code> and
     *               <code>setNameY</code>, where <code>nameX</code> and <code>nameY</code>
     *               are the value of the <code>xPropertyName</code> and <code>yPropertyName</code>
     *               parameters, respectively.
     * @param xPropertyName The name of the property for the x coordinate being animated.
     * @param yPropertyName The name of the property for the y coordinate being animated.
     * @param path The <code>Path</code> to animate values along.
     * @return An ObjectAnimator object that is set up to animate along <code>path</code>.
     */
    public static ObjectAnimator ofFloat(Object target, String xPropertyName, String yPropertyName,
                                         Path path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ObjectAnimatorCompatLollipop.ofFloat(target, xPropertyName, yPropertyName, path);
        }
        return ObjectAnimatorCompatBase.ofFloat(target, xPropertyName, yPropertyName, path);
    }

    /**
     * Constructs and returns an ObjectAnimator that animates coordinates along a <code>Path</code>
     * using two properties. A <code>Path</code></> animation moves in two dimensions, animating
     * coordinates <code>(x, y)</code> together to follow the line. In this variation, the
     * coordinates are floats that are set to separate properties, <code>xProperty</code> and
     * <code>yProperty</code>.
     *
     * @param target The object whose properties are to be animated.
     * @param xProperty The property for the x coordinate being animated.
     * @param yProperty The property for the y coordinate being animated.
     * @param path The <code>Path</code> to animate values along.
     * @return An ObjectAnimator object that is set up to animate along <code>path</code>.
     */
    public static <T> ObjectAnimator ofFloat(T target, Property<T, Float> xProperty,
                                             Property<T, Float> yProperty, Path path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ObjectAnimatorCompatLollipop.ofFloat(target, xProperty, yProperty, path);
        }
        return ObjectAnimatorCompatBase.ofFloat(target, xProperty, yProperty, path);
    }

    /**
     * Constructs and returns an ObjectAnimator that animates coordinates along a <code>Path</code>
     * using two properties. A <code>Path</code></> animation moves in two dimensions, animating
     * coordinates <code>(x, y)</code> together to follow the line. In this variation, the
     * coordinates are integers that are set to separate properties designated by
     * <code>xPropertyName</code> and <code>yPropertyName</code>.
     *
     * @param target The object whose properties are to be animated. This object should
     *               have public methods on it called <code>setNameX()</code> and
     *               <code>setNameY</code>, where <code>nameX</code> and <code>nameY</code>
     *               are the value of <code>xPropertyName</code> and <code>yPropertyName</code>
     *               parameters, respectively.
     * @param xPropertyName The name of the property for the x coordinate being animated.
     * @param yPropertyName The name of the property for the y coordinate being animated.
     * @param path The <code>Path</code> to animate values along.
     * @return An ObjectAnimator object that is set up to animate along <code>path</code>.
     */
    public static ObjectAnimator ofInt(Object target, String xPropertyName, String yPropertyName,
                                       Path path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ObjectAnimatorCompatLollipop.ofInt(target, xPropertyName, yPropertyName, path);
        }
        return ObjectAnimatorCompatBase.ofInt(target, xPropertyName, yPropertyName, path);
    }

    /**
     * Constructs and returns an ObjectAnimator that animates coordinates along a <code>Path</code>
     * using two properties.  A <code>Path</code></> animation moves in two dimensions, animating
     * coordinates <code>(x, y)</code> together to follow the line. In this variation, the
     * coordinates are integers that are set to separate properties, <code>xProperty</code> and
     * <code>yProperty</code>.
     *
     * @param target The object whose properties are to be animated.
     * @param xProperty The property for the x coordinate being animated.
     * @param yProperty The property for the y coordinate being animated.
     * @param path The <code>Path</code> to animate values along.
     * @return An ObjectAnimator object that is set up to animate along <code>path</code>.
     */
    public static <T> ObjectAnimator ofInt(T target, Property<T, Integer> xProperty,
                                           Property<T, Integer> yProperty, Path path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ObjectAnimatorCompatLollipop.ofInt(target, xProperty, yProperty, path);
        }
        return ObjectAnimatorCompatBase.ofInt(target, xProperty, yProperty, path);
    }
}
