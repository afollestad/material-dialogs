/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.afollestad.materialdialogs.internal.progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Build;

import com.afollestad.materialdialogs.util.DialogUtils;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class SingleHorizontalProgressDrawable extends ProgressDrawableBase {

    private static final float PROGRESS_INTRINSIC_HEIGHT_DP = 3.2f;
    private static final float PADDED_INTRINSIC_HEIGHT_DP = 16;
    private static final RectF RECT_BOUND = new RectF(-180, -1, 180, 1);
    private static final RectF RECT_PADDED_BOUND = new RectF(-180, -5, 180, 5);
    private static final int LEVEL_MAX = 10000;

    private int mProgressIntrinsicHeight;
    private int mPaddedIntrinsicHeight;
    private boolean mShowTrack = true;
    private float mTrackAlpha;

    public SingleHorizontalProgressDrawable(Context context) {
        super(context);

        float density = context.getResources().getDisplayMetrics().density;
        mProgressIntrinsicHeight = Math.round(PROGRESS_INTRINSIC_HEIGHT_DP * density);
        mPaddedIntrinsicHeight = Math.round(PADDED_INTRINSIC_HEIGHT_DP * density);

        mTrackAlpha = DialogUtils.resolveFloat(context, android.R.attr.disabledAlpha);
    }

    public boolean getShowTrack() {
        return mShowTrack;
    }

    public void setShowTrack(boolean showTrack) {
        if (mShowTrack != showTrack) {
            mShowTrack = showTrack;
            invalidateSelf();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIntrinsicHeight() {
        return mUseIntrinsicPadding ? mPaddedIntrinsicHeight : mProgressIntrinsicHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOpacity() {
        if (mAlpha == 0) {
            return PixelFormat.TRANSPARENT;
        } else if (mAlpha == 0xFF && (!mShowTrack || mTrackAlpha == 1)) {
            return PixelFormat.OPAQUE;
        } else {
            return PixelFormat.TRANSLUCENT;
        }
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    protected void onPreparePaint(Paint paint) {
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas, int width, int height, Paint paint) {

        if (mUseIntrinsicPadding) {
            canvas.scale(width / RECT_PADDED_BOUND.width(), height / RECT_PADDED_BOUND.height());
            canvas.translate(RECT_PADDED_BOUND.width() / 2, RECT_PADDED_BOUND.height() / 2);
        } else {
            canvas.scale(width / RECT_BOUND.width(), height / RECT_BOUND.height());
            canvas.translate(RECT_BOUND.width() / 2, RECT_BOUND.height() / 2);
        }

        if (mShowTrack) {
            paint.setAlpha(Math.round(mAlpha * mTrackAlpha));
            drawTrackRect(canvas, paint);
            paint.setAlpha(mAlpha);
        }
        drawProgressRect(canvas, paint);
    }

    private static void drawTrackRect(Canvas canvas, Paint paint) {
        canvas.drawRect(RECT_BOUND, paint);
    }

    private void drawProgressRect(Canvas canvas, Paint paint) {

        int level = getLevel();
        if (level == 0) {
            return;
        }

        int saveCount = canvas.save();
        canvas.scale((float) level / LEVEL_MAX, 1, RECT_BOUND.left, 0);

        canvas.drawRect(RECT_BOUND, paint);

        canvas.restoreToCount(saveCount);
    }
}
