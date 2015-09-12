/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.afollestad.materialdialogs.internal.progress;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Path;
import android.os.Build;

/**
 * Animators backported for Drawables in this library.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class Animators {

    private Animators() {
    }

    // M -522.59998,0
    // c 48.89972,0 166.02656,0 301.21729,0
    // c 197.58128,0 420.9827,0 420.9827,0
    private static final Path PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X;

    static {
        PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X = new Path();
        PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X.moveTo(-522.59998f, 0);
        PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X.rCubicTo(48.89972f, 0, 166.02656f,
                0, 301.21729f, 0);
        PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X.rCubicTo(197.58128f, 0, 420.9827f,
                0, 420.9827f, 0);
    }

    // M 0 0.1
    // L 1 0.826849212646
    // L 2 0.1
    private static final Path PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X;

    static {
        PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X = new Path();
        PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X.moveTo(0, 0.1f);
        PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X.lineTo(1, 0.826849212646f);
        PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X.lineTo(2, 0.1f);
    }

    // M -197.60001,0
    // c 14.28182,0 85.07782,0 135.54689,0
    // c 54.26191,0 90.42461,0 168.24331,0
    // c 144.72154,0 316.40982,0 316.40982,0
    private static final Path PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X;

    static {
        PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X = new Path();
        PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.moveTo(-197.60001f, 0);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.rCubicTo(14.28182f, 0, 85.07782f, 0,
                135.54689f, 0);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.rCubicTo(54.26191f, 0, 90.42461f, 0,
                168.24331f, 0);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.rCubicTo(144.72154f, 0, 316.40982f, 0,
                316.40982f, 0);
    }

    // M 0.0,0.1
    // L 1.0,0.571379510698
    // L 2.0,0.909950256348
    // L 3.0,0.1
    private static final Path PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X;

    static {
        PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X = new Path();
        PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.moveTo(0, 0.1f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.lineTo(1, 0.571379510698f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.lineTo(2, 0.909950256348f);
        PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.lineTo(3, 0.1f);
    }

    /**
     * Create a backported Animator for
     * {@code @android:anim/progress_indeterminate_horizontal_rect1}.
     *
     * @param target The object whose properties are to be animated.
     * @return An Animator object that is set up to behave the same as the its native counterpart.
     */
    public static Animator createIndeterminateHorizontalRect1(Object target) {

        ObjectAnimator translateXAnimator = ObjectAnimatorCompat.ofFloat(target, "translateX", null,
                PATH_INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X);
        translateXAnimator.setDuration(2000);
        translateXAnimator.setInterpolator(
                Interpolators.INDETERMINATE_HORIZONTAL_RECT1_TRANSLATE_X.INSTANCE);
        translateXAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator scaleXAnimator = ObjectAnimatorCompat.ofFloat(target, null, "scaleX",
                PATH_INDETERMINATE_HORIZONTAL_RECT1_SCALE_X);
        scaleXAnimator.setDuration(2000);
        scaleXAnimator.setInterpolator(
                Interpolators.INDETERMINATE_HORIZONTAL_RECT1_SCALE_X.INSTANCE);
        scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateXAnimator, scaleXAnimator);
        return animatorSet;
    }

    /**
     * Create a backported Animator for
     * {@code @android:anim/progress_indeterminate_horizontal_rect2}.
     *
     * @param target The object whose properties are to be animated.
     * @return An Animator object that is set up to behave the same as the its native counterpart.
     */
    public static Animator createIndeterminateHorizontalRect2(Object target) {

        ObjectAnimator translateXAnimator = ObjectAnimatorCompat.ofFloat(target, "translateX", null,
                PATH_INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X);
        translateXAnimator.setDuration(2000);
        translateXAnimator.setInterpolator(
                Interpolators.INDETERMINATE_HORIZONTAL_RECT2_TRANSLATE_X.INSTANCE);
        translateXAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator scaleXAnimator = ObjectAnimatorCompat.ofFloat(target, null, "scaleX",
                PATH_INDETERMINATE_HORIZONTAL_RECT2_SCALE_X);
        scaleXAnimator.setDuration(2000);
        scaleXAnimator.setInterpolator(
                Interpolators.INDETERMINATE_HORIZONTAL_RECT2_SCALE_X.INSTANCE);
        scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateXAnimator, scaleXAnimator);
        return animatorSet;
    }

    /**
     * Create a backported Animator for {@code @android:anim/progress_indeterminate_material}.
     *
     * @param target The object whose properties are to be animated.
     * @return An Animator object that is set up to behave the same as the its native counterpart.
     */
    public static Animator createIndeterminate(Object target) {

        ObjectAnimator trimPathStartAnimator = ObjectAnimator.ofFloat(target, "trimPathStart", 0,
                0.75f);
        trimPathStartAnimator.setDuration(1333);
        trimPathStartAnimator.setInterpolator(Interpolators.TRIM_PATH_START.INSTANCE);
        trimPathStartAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator trimPathEndAnimator = ObjectAnimator.ofFloat(target, "trimPathEnd", 0,
                0.75f);
        trimPathEndAnimator.setDuration(1333);
        trimPathEndAnimator.setInterpolator(Interpolators.TRIM_PATH_END.INSTANCE);
        trimPathEndAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator trimPathOffsetAnimator = ObjectAnimator.ofFloat(target, "trimPathOffset", 0,
                0.25f);
        trimPathOffsetAnimator.setDuration(1333);
        trimPathOffsetAnimator.setInterpolator(Interpolators.LINEAR.INSTANCE);
        trimPathOffsetAnimator.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(trimPathStartAnimator, trimPathEndAnimator,
                trimPathOffsetAnimator);
        return animatorSet;
    }

    /**
     * Create a backported Animator for
     * {@code @android:anim/progress_indeterminate_rotation_material}.
     *
     * @param target The object whose properties are to be animated.
     * @return An Animator object that is set up to behave the same as the its native counterpart.
     */
    public static Animator createIndeterminateRotation(Object target) {
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(target, "rotation", 0, 720);
        rotationAnimator.setDuration(6665);
        rotationAnimator.setInterpolator(Interpolators.LINEAR.INSTANCE);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        return rotationAnimator;
    }
}
