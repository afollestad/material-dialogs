/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.afollestad.materialdialogs.internal.progress;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.annotation.Size;
import android.util.Property;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class ObjectAnimatorCompatBase {

    private static final int NUM_POINTS = 500;

    private ObjectAnimatorCompatBase() {}

    public static ObjectAnimator ofArgb(Object target, String propertyName, int... values) {
        ObjectAnimator animator = ObjectAnimator.ofInt(target, propertyName, values);
        animator.setEvaluator(new ArgbEvaluator());
        return animator;
    }

    public static <T> ObjectAnimator ofArgb(T target, Property<T, Integer> property,
                                            int... values) {
        ObjectAnimator animator = ObjectAnimator.ofInt(target, property, values);
        animator.setEvaluator(new ArgbEvaluator());
        return animator;
    }

    public static ObjectAnimator ofFloat(Object target, String xPropertyName, String yPropertyName,
                                         Path path) {

        float[] xValues = new float[NUM_POINTS];
        float[] yValues = new float[NUM_POINTS];
        calculateXYValues(path, xValues, yValues);

        PropertyValuesHolder xPvh = PropertyValuesHolder.ofFloat(xPropertyName, xValues);
        PropertyValuesHolder yPvh = PropertyValuesHolder.ofFloat(yPropertyName, yValues);

        return ObjectAnimator.ofPropertyValuesHolder(target, xPvh, yPvh);
    }

    public static <T> ObjectAnimator ofFloat(T target, Property<T, Float> xProperty,
                                             Property<T, Float> yProperty, Path path) {

        float[] xValues = new float[NUM_POINTS];
        float[] yValues = new float[NUM_POINTS];
        calculateXYValues(path, xValues, yValues);

        PropertyValuesHolder xPvh = PropertyValuesHolder.ofFloat(xProperty, xValues);
        PropertyValuesHolder yPvh = PropertyValuesHolder.ofFloat(yProperty, yValues);

        return ObjectAnimator.ofPropertyValuesHolder(target, xPvh, yPvh);
    }

    public static ObjectAnimator ofInt(Object target, String xPropertyName, String yPropertyName,
                                       Path path) {

        int[] xValues = new int[NUM_POINTS];
        int[] yValues = new int[NUM_POINTS];
        calculateXYValues(path, xValues, yValues);

        PropertyValuesHolder xPvh = PropertyValuesHolder.ofInt(xPropertyName, xValues);
        PropertyValuesHolder yPvh = PropertyValuesHolder.ofInt(yPropertyName, yValues);

        return ObjectAnimator.ofPropertyValuesHolder(target, xPvh, yPvh);
    }

    public static <T> ObjectAnimator ofInt(T target, Property<T, Integer> xProperty,
                                           Property<T, Integer> yProperty, Path path) {

        int[] xValues = new int[NUM_POINTS];
        int[] yValues = new int[NUM_POINTS];
        calculateXYValues(path, xValues, yValues);

        PropertyValuesHolder xPvh = PropertyValuesHolder.ofInt(xProperty, xValues);
        PropertyValuesHolder yPvh = PropertyValuesHolder.ofInt(yProperty, yValues);

        return ObjectAnimator.ofPropertyValuesHolder(target, xPvh, yPvh);
    }

    private static void calculateXYValues(Path path, @Size(NUM_POINTS) float[] xValues,
                                          @Size(NUM_POINTS) float[] yValues) {

        PathMeasure pathMeasure = new PathMeasure(path, false /* forceClosed */);
        float pathLength = pathMeasure.getLength();

        float[] position = new float[2];
        for (int i = 0; i < NUM_POINTS; ++i) {
            float distance = (i * pathLength) / (NUM_POINTS - 1);
            pathMeasure.getPosTan(distance, position, null /* tangent */);
            xValues[i] = position[0];
            yValues[i] = position[1];
        }
    }

    private static void calculateXYValues(Path path, @Size(NUM_POINTS) int[] xValues,
                                          @Size(NUM_POINTS) int[] yValues) {

        PathMeasure pathMeasure = new PathMeasure(path, false /* forceClosed */);
        float pathLength = pathMeasure.getLength();

        float[] position = new float[2];
        for (int i = 0; i < NUM_POINTS; ++i) {
            float distance = (i * pathLength) / (NUM_POINTS - 1);
            pathMeasure.getPosTan(distance, position, null /* tangent */);
            xValues[i] = Math.round(position[0]);
            yValues[i] = Math.round(position[1]);
        }
    }
}
