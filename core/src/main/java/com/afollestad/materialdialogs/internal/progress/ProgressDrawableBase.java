/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.afollestad.materialdialogs.internal.progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;

import com.afollestad.materialdialogs.R;
import com.afollestad.materialdialogs.util.DialogUtils;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
abstract class ProgressDrawableBase extends Drawable {

    protected boolean mUseIntrinsicPadding = true;
    protected int mAlpha = 0xFF;
    protected ColorFilter mColorFilter;
    protected ColorStateList mTint;
    protected PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;
    protected PorterDuffColorFilter mTintFilter;
    protected boolean mAutoMirrored = true;

    private Paint mPaint;

    public ProgressDrawableBase(Context context) {
        int colorControlActivated = DialogUtils.resolveColor(context, R.attr.colorControlActivated);
        // setTint() has been overridden for compatibility; DrawableCompat won't work because
        // wrapped Drawable won't be Animatable.
        setTint(colorControlActivated);
    }

    /**
     * Get whether this {@code Drawable} is showing a track. The default is true.
     *
     * @return Whether this {@code Drawable} is showing a track.
     */
    public boolean getUseIntrinsicPadding() {
        return mUseIntrinsicPadding;
    }

    /**
     * Set whether this {@code Drawable} should show a track. The default is true.
     */
    public void setUseIntrinsicPadding(boolean useIntrinsicPadding) {
        if (mUseIntrinsicPadding != useIntrinsicPadding) {
            mUseIntrinsicPadding = useIntrinsicPadding;
            invalidateSelf();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlpha(int alpha) {
        if (mAlpha != alpha) {
            mAlpha = alpha;
            invalidateSelf();
        }
    }

    /**
     * {@inheritDoc}
     */
    // Rewrite for compatibility.
    @Override
    public void setTint(int tint) {
        setTintList(ColorStateList.valueOf(tint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mColorFilter = colorFilter;
        invalidateSelf();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTintList(ColorStateList tint) {
        mTint = tint;
        mTintFilter = makeTintFilter(tint, mTintMode);
        invalidateSelf();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        mTintMode = tintMode;
        mTintFilter = makeTintFilter(mTint, tintMode);
        invalidateSelf();
    }

    private PorterDuffColorFilter makeTintFilter(ColorStateList tint, PorterDuff.Mode tintMode) {

        if (tint == null || tintMode == null) {
            return null;
        }

        int color = tint.getColorForState(getState(), Color.TRANSPARENT);
        // They made PorterDuffColorFilter.setColor() and setMode() @hide.
        return new PorterDuffColorFilter(color, tintMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAutoMirrored() {
        return mAutoMirrored;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoMirrored(boolean autoMirrored) {
        if (mAutoMirrored != autoMirrored) {
            mAutoMirrored = autoMirrored;
            invalidateSelf();
        }
    }

    private boolean needMirroring() {
        return isAutoMirrored() && getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(Canvas canvas) {

        Rect bounds = getBounds();
        if (bounds.width() == 0 || bounds.height() == 0) {
            return;
        }

        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLACK);
            onPreparePaint(mPaint);
        }
        mPaint.setAlpha(mAlpha);
        ColorFilter colorFilter = mColorFilter != null ? mColorFilter : mTintFilter;
        mPaint.setColorFilter(colorFilter);

        int saveCount = canvas.save();

        canvas.translate(bounds.left, bounds.top);
        if (needMirroring()) {
            canvas.translate(bounds.width(), 0);
            canvas.scale(-1, 1);
        }

        onDraw(canvas, bounds.width(), bounds.height(), mPaint);

        canvas.restoreToCount(saveCount);
    }

    abstract protected void onPreparePaint(Paint paint);

    abstract protected void onDraw(Canvas canvas, int width, int height, Paint paint);
}
