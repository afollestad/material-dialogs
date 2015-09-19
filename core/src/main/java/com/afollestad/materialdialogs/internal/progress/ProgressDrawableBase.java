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
import android.support.annotation.NonNull;
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

    public boolean getUseIntrinsicPadding() {
        return mUseIntrinsicPadding;
    }

    public void setUseIntrinsicPadding(boolean useIntrinsicPadding) {
        if (mUseIntrinsicPadding != useIntrinsicPadding) {
            mUseIntrinsicPadding = useIntrinsicPadding;
            invalidateSelf();
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mAlpha != alpha) {
            mAlpha = alpha;
            invalidateSelf();
        }
    }

    @Override
    public void setTint(int tint) {
        setTintList(ColorStateList.valueOf(tint));
    }

    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mColorFilter = colorFilter;
        invalidateSelf();
    }

    @Override
    public void setTintList(ColorStateList tint) {
        mTint = tint;
        mTintFilter = makeTintFilter(tint, mTintMode);
        invalidateSelf();
    }

    @Override
    public void setTintMode(@NonNull PorterDuff.Mode tintMode) {
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

    @Override
    public boolean isAutoMirrored() {
        return mAutoMirrored;
    }

    @Override
    public void setAutoMirrored(boolean autoMirrored) {
        if (mAutoMirrored != autoMirrored) {
            mAutoMirrored = autoMirrored;
            invalidateSelf();
        }
    }

    private boolean needMirroring() {
        return Build.VERSION.SDK_INT >= 23 && isAutoMirrored() && getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

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
