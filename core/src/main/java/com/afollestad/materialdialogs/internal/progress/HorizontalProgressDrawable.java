/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.afollestad.materialdialogs.internal.progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;

import com.afollestad.materialdialogs.util.DialogUtils;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class HorizontalProgressDrawable extends LayerDrawable {

    private int mSecondaryAlpha;
    private SingleHorizontalProgressDrawable mTrackDrawable;
    private SingleHorizontalProgressDrawable mSecondaryProgressDrawable;
    private SingleHorizontalProgressDrawable mProgressDrawable;

    public HorizontalProgressDrawable(Context context) {
        super(new Drawable[]{
                new SingleHorizontalProgressDrawable(context),
                new SingleHorizontalProgressDrawable(context),
                new SingleHorizontalProgressDrawable(context)
        });

        setId(0, android.R.id.background);
        mTrackDrawable = (SingleHorizontalProgressDrawable) getDrawable(0);

        setId(1, android.R.id.secondaryProgress);
        mSecondaryProgressDrawable = (SingleHorizontalProgressDrawable) getDrawable(1);
        float disabledAlpha = DialogUtils.resolveFloat(context, android.R.attr.disabledAlpha);
        mSecondaryAlpha = Math.round(disabledAlpha * 0xFF);
        mSecondaryProgressDrawable.setAlpha(mSecondaryAlpha);
        mSecondaryProgressDrawable.setShowTrack(false);

        setId(2, android.R.id.progress);
        mProgressDrawable = (SingleHorizontalProgressDrawable) getDrawable(2);
        mProgressDrawable.setShowTrack(false);
    }

    public boolean getShowTrack() {
        return mTrackDrawable.getShowTrack();
    }

    public void setShowTrack(boolean showTrack) {
        if (mTrackDrawable.getShowTrack() != showTrack) {
            mTrackDrawable.setShowTrack(showTrack);
            // HACK: Make alpha act as if composited.
            mSecondaryProgressDrawable.setAlpha(showTrack ? mSecondaryAlpha : 2 * mSecondaryAlpha);
        }
    }

    public boolean getUseIntrinsicPadding() {
        return mTrackDrawable.getUseIntrinsicPadding();
    }

    public void setUseIntrinsicPadding(boolean useIntrinsicPadding) {

        mTrackDrawable.setUseIntrinsicPadding(useIntrinsicPadding);
        mSecondaryProgressDrawable.setUseIntrinsicPadding(useIntrinsicPadding);
        mProgressDrawable.setUseIntrinsicPadding(useIntrinsicPadding);
    }

    // Rewrite for compatibility and workaround lint.
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTint(int tint) {
        mTrackDrawable.setTint(tint);
        mSecondaryProgressDrawable.setTint(tint);
        mProgressDrawable.setTint(tint);
    }

    // Rewrite for compatibility and workaround lint.
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTintList(ColorStateList tint) {
        mTrackDrawable.setTintList(tint);
        mSecondaryProgressDrawable.setTintList(tint);
        mProgressDrawable.setTintList(tint);
    }

    // Rewrite for compatibility and workaround lint.
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTintMode(PorterDuff.Mode tintMode) {
        mTrackDrawable.setTintMode(tintMode);
        mSecondaryProgressDrawable.setTintMode(tintMode);
        mProgressDrawable.setTintMode(tintMode);
    }
}