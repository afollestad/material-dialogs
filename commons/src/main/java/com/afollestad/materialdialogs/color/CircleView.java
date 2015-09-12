package com.afollestad.materialdialogs.color;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;

public class CircleView extends FrameLayout {

    private final int borderWidthSmall;
    private final int borderWidthLarge;

    private final Paint outerPaint;
    private final Paint whitePaint;
    private final Paint innerPaint;
    private boolean mSelected;

    public CircleView(Context context) {
        this(context, null, 0);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Resources r = getResources();
        borderWidthSmall = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics());
        borderWidthLarge = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());

        whitePaint = new Paint();
        whitePaint.setAntiAlias(true);
        whitePaint.setColor(Color.WHITE);
        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);
        outerPaint = new Paint();
        outerPaint.setAntiAlias(true);

        update(Color.DKGRAY);
        setWillNotDraw(false);
    }

    private void update(@ColorInt int color) {
        innerPaint.setColor(color);
        outerPaint.setColor(shiftColorDown(color));

        Drawable selector = createSelector(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_pressed}
            };
            int[] colors = new int[]{shiftColorUp(color)};
            ColorStateList rippleColors = new ColorStateList(states, colors);
            setForeground(new RippleDrawable(rippleColors, selector, null));
        } else {
            setForeground(selector);
        }
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        update(color);
        requestLayout();
        invalidate();
    }

    @Override
    public void setBackgroundResource(@ColorRes int color) {
        setBackgroundColor(ContextCompat.getColor(getContext(), color));
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void setBackground(Drawable background) {
        throw new IllegalStateException("Cannot use setBackground() on CircleView.");
    }

    /**
     * @deprecated
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void setBackgroundDrawable(Drawable background) {
        throw new IllegalStateException("Cannot use setBackgroundDrawable() on CircleView.");
    }

    /**
     * @deprecated
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void setActivated(boolean activated) {
        throw new IllegalStateException("Cannot use setActivated() on CircleView.");
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            //noinspection SuspiciousNameCombination
            int height = width;
            if (heightMode == MeasureSpec.AT_MOST)
                height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int outerRadius = getMeasuredWidth() / 2;
        if(mSelected) {
            final int whiteRadius = outerRadius - borderWidthLarge;
            final int innerRadius = whiteRadius - borderWidthSmall;
            canvas.drawCircle(getMeasuredWidth() / 2,
                    getMeasuredHeight() / 2,
                    outerRadius,
                    outerPaint);
            canvas.drawCircle(getMeasuredWidth() / 2,
                    getMeasuredHeight() / 2,
                    whiteRadius,
                    whitePaint);
            canvas.drawCircle(getMeasuredWidth() / 2,
                    getMeasuredHeight() / 2,
                    innerRadius,
                    innerPaint);
        } else {
            canvas.drawCircle(getMeasuredWidth() / 2,
                    getMeasuredHeight() / 2,
                    outerRadius,
                    innerPaint);
        }
    }

    @ColorInt
    private static int translucentColor(int color) {
        final float factor = 0.7f;
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private Drawable createSelector(int color) {
        ShapeDrawable darkerCircle = new ShapeDrawable(new OvalShape());
        darkerCircle.getPaint().setColor(translucentColor(shiftColorUp(color)));
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, darkerCircle);
        return stateListDrawable;
    }

    @ColorInt
    public static int shiftColor(@ColorInt int color, @FloatRange(from = 0.0f, to = 2.0f) float by) {
        if (by == 1f) return color;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= by; // value component
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    public static int shiftColorDown(@ColorInt int color) {
        return shiftColor(color, 0.9f);
    }

    @ColorInt
    public static int shiftColorUp(@ColorInt int color) {
        return shiftColor(color, 1.1f);
    }
}
