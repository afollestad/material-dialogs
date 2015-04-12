package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.R;

/**
 * @author Kevin Barry (teslacoil) 4/02/2015
 *         <p/>
 *         This is the top level view for all MaterialDialogs
 *         It handles the layout of:
 *         titleFrame (md_stub_titleframe)
 *         content (text, custom view, listview, etc)
 *         buttonDefault... (either stacked or horizontal)
 */
public class MDRootLayout extends ViewGroup {

    private View mTitleBar;
    private View mContent;

    private static final int INDEX_NEUTRAL = 0;
    private static final int INDEX_NEGATIVE = 1;
    private static final int INDEX_POSITIVE = 2;
    private boolean mDrawTopDivider = false;
    private boolean mDrawBottomDivider = false;
    private MDButton[] mButtons = new MDButton[3];
    private boolean mForceStack = false;
    private boolean mIsStacked = false;
    private boolean mUseFullPadding = true;
    private boolean mReducePaddingNoTitleNoButtons;

    private int mNoTitlePaddingFull;
    private int mButtonPaddingFull;
    private int mButtonBarHeight;

    private GravityEnum mButtonGravity = GravityEnum.START;

    /* Margin from dialog frame to first button */
    private int mButtonHorizontalEdgeMargin;

    private Paint mDividerPaint;

    private ViewTreeObserver.OnScrollChangedListener mTopOnScrollChangedListener;
    private ViewTreeObserver.OnScrollChangedListener mBottomOnScrollChangedListener;

    public MDRootLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MDRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MDRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MDRootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        Resources r = context.getResources();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MDRootLayout, defStyleAttr, 0);
        mReducePaddingNoTitleNoButtons = a.getBoolean(R.styleable.MDRootLayout_md_reduce_padding_no_title_no_buttons, true);
        a.recycle();

        mNoTitlePaddingFull = r.getDimensionPixelSize(R.dimen.md_notitle_vertical_padding);
        mButtonPaddingFull = r.getDimensionPixelSize(R.dimen.md_button_frame_vertical_padding);

        mButtonHorizontalEdgeMargin = r.getDimensionPixelSize(R.dimen.md_button_padding_frame_side);
        mButtonBarHeight = r.getDimensionPixelSize(R.dimen.md_button_height);

        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth(r.getDimensionPixelSize(R.dimen.md_divider_height));
        mDividerPaint.setStyle(Paint.Style.STROKE);
        setWillNotDraw(false);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getId() == R.id.titleFrame) {
                mTitleBar = v;
            } else if (v.getId() == R.id.buttonDefaultNeutral) {
                mButtons[INDEX_NEUTRAL] = (MDButton) v;
            } else if (v.getId() == R.id.buttonDefaultNegative) {
                mButtons[INDEX_NEGATIVE] = (MDButton) v;
            } else if (v.getId() == R.id.buttonDefaultPositive) {
                mButtons[INDEX_POSITIVE] = (MDButton) v;
            } else {
                mContent = v;
            }
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        mUseFullPadding = true;
        boolean hasButtons = false;

        final boolean stacked;
        if (!mForceStack) {
            int buttonsWidth = 0;
            for (MDButton button : mButtons) {
                if (button != null && isVisible(button)) {
                    button.setStacked(false, false);
                    measureChild(button, widthMeasureSpec, heightMeasureSpec);
                    buttonsWidth += button.getMeasuredWidth();
                    hasButtons = true;
                }
            }

            int buttonBarPadding = getContext().getResources()
                    .getDimensionPixelSize(R.dimen.md_neutral_button_margin);
            final int buttonFrameWidth = width - 2 * buttonBarPadding;
            stacked = buttonsWidth > buttonFrameWidth;
        } else {
            stacked = true;
        }

        int stackedHeight = 0;
        mIsStacked = stacked;
        if (stacked) {
            for (MDButton button : mButtons) {
                if (button != null && isVisible(button)) {
                    button.setStacked(true, false);
                    measureChild(button, widthMeasureSpec, heightMeasureSpec);
                    stackedHeight += button.getMeasuredHeight();
                    hasButtons = true;
                }
            }
        }

        int availableHeight = height;
        int fullPadding = 0;
        int minPadding = 0;
        if (hasButtons) {
            if (mIsStacked) {
                availableHeight -= stackedHeight;
                fullPadding += 2 * mButtonPaddingFull;
                minPadding += 2 * mButtonPaddingFull;
            } else {
                availableHeight -= mButtonBarHeight;
                fullPadding += 2 * mButtonPaddingFull;
                /* No minPadding */
            }
        } else {
            /* Content has 8dp, we add 16dp and get 24dp, the frame margin */
            fullPadding += 2 * mButtonPaddingFull;
        }

        if (isVisible(mTitleBar)) {
            mTitleBar.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.UNSPECIFIED);
            availableHeight -= mTitleBar.getMeasuredHeight();
        } else {
            fullPadding += mNoTitlePaddingFull;
        }

        if (isVisible(mContent)) {
            mContent.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(availableHeight - minPadding, MeasureSpec.AT_MOST));

            if (mContent.getMeasuredHeight() <= availableHeight - fullPadding) {
                if (!mReducePaddingNoTitleNoButtons || isVisible(mTitleBar) || hasButtons) {
                    mUseFullPadding = true;
                    availableHeight -= mContent.getMeasuredHeight() + fullPadding;
                } else {
                    mUseFullPadding = false;
                    availableHeight -= mContent.getMeasuredHeight() + minPadding;
                }
            } else {
                mUseFullPadding = false;
                availableHeight = 0;
            }

        }

        setMeasuredDimension(width, height - availableHeight);
    }

    private static boolean isVisible(View v) {
        boolean visible = v != null && v.getVisibility() != View.GONE;
        if (visible && v instanceof MDButton)
            visible = ((MDButton) v).getText().toString().trim().length() > 0;
        return visible;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mContent != null) {
            if (mDrawTopDivider) {
                int y = mContent.getTop();
                canvas.drawLine(0, y, getMeasuredWidth(), y, mDividerPaint);
            }

            if (mDrawBottomDivider) {
                int y = mContent.getBottom();
                canvas.drawLine(0, y, getMeasuredWidth(), y, mDividerPaint);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, final int l, int t, final int r, int b) {
        if (isVisible(mTitleBar)) {
            int height = mTitleBar.getMeasuredHeight();
            mTitleBar.layout(l, t, r, t + height);
            t += height;
        } else if (mUseFullPadding) {
            t += mNoTitlePaddingFull;
        }

        if (isVisible(mContent))
            mContent.layout(l, t, r, t + mContent.getMeasuredHeight());

        if (mIsStacked) {
            b -= mButtonPaddingFull;
            for (MDButton mButton : mButtons) {
                if (isVisible(mButton)) {
                    mButton.layout(l, b - mButton.getMeasuredHeight(), r, b);
                    b -= mButton.getMeasuredHeight();
                }
            }
        } else {
            int barTop;
            int barBottom = b;
            if (mUseFullPadding)
                barBottom -= mButtonPaddingFull;
            barTop = barBottom - mButtonBarHeight;
            /* START:
               Neutral   Negative  Positive

               CENTER:
               Negative  Neutral   Positive

               END:
               Positive  Negative  Neutral

               (With no Positive, Negative takes it's place except for CENTER)
             */
            int offset = mButtonHorizontalEdgeMargin;

            /* Used with CENTER gravity */
            int neutralLeft = -1;
            int neutralRight = -1;

            if (isVisible(mButtons[INDEX_POSITIVE])) {
                int bl, br;
                if (mButtonGravity == GravityEnum.END) {
                    bl = l + offset;
                    br = bl + mButtons[INDEX_POSITIVE].getMeasuredWidth();
                } else { /* START || CENTER */
                    br = r - offset;
                    bl = br - mButtons[INDEX_POSITIVE].getMeasuredWidth();
                    neutralRight = bl;
                }
                mButtons[INDEX_POSITIVE].layout(bl, barTop, br, barBottom);
                offset += mButtons[INDEX_POSITIVE].getMeasuredWidth();
            }

            if (isVisible(mButtons[INDEX_NEGATIVE])) {
                int bl, br;
                if (mButtonGravity == GravityEnum.END) {
                    bl = l + offset;
                    br = bl + mButtons[INDEX_NEGATIVE].getMeasuredWidth();
                } else if (mButtonGravity == GravityEnum.START) {
                    br = r - offset;
                    bl = br - mButtons[INDEX_NEGATIVE].getMeasuredWidth();
                } else { /* CENTER */
                    bl = l + mButtonHorizontalEdgeMargin;
                    br = bl + mButtons[INDEX_NEGATIVE].getMeasuredWidth();
                    neutralLeft = br;
                }
                mButtons[INDEX_NEGATIVE].layout(bl, barTop, br, barBottom);
            }

            if (isVisible(mButtons[INDEX_NEUTRAL])) {
                int bl, br;
                if (mButtonGravity == GravityEnum.END) {
                    br = r - mButtonHorizontalEdgeMargin;
                    bl = br - mButtons[INDEX_NEUTRAL].getMeasuredWidth();
                } else if (mButtonGravity == GravityEnum.START) {
                    bl = l + mButtonHorizontalEdgeMargin;
                    br = bl + mButtons[INDEX_NEUTRAL].getMeasuredWidth();
                } else { /* CENTER */
                    if (neutralLeft == -1 && neutralRight != -1) {
                        neutralLeft = neutralRight - mButtons[INDEX_NEUTRAL].getMeasuredWidth();
                    } else if (neutralRight == -1 && neutralLeft != -1) {
                        neutralRight = neutralLeft + mButtons[INDEX_NEUTRAL].getMeasuredWidth();
                    } else if (neutralRight == -1) {
                        neutralLeft = (r - l) / 2 - mButtons[INDEX_NEUTRAL].getMeasuredWidth() / 2;
                        neutralRight = neutralLeft + mButtons[INDEX_NEUTRAL].getMeasuredWidth();
                    }
                    bl = neutralLeft;
                    br = neutralRight;
                }

                mButtons[INDEX_NEUTRAL].layout(bl, barTop, br, barBottom);
            }
        }

        setUpDividersVisibility(mContent, true, false);
    }

    public void setForceStack(boolean forceStack) {
        mForceStack = forceStack;
        invalidate();
    }

    public void setDividerColor(int color) {
        mDividerPaint.setColor(color);
        invalidate();
    }

    public void setButtonGravity(GravityEnum gravity) {
        mButtonGravity = gravity;
    }

    public void setButtonStackedGravity(GravityEnum gravity) {
        for (MDButton mButton : mButtons) {
            if (mButton != null)
                mButton.setStackedGravity(gravity);
        }
    }

    private void setUpDividersVisibility(View view, boolean topAndBottom, boolean bottom) {
        if (view == null)
            return;
        if (view instanceof ScrollView) {
            final ScrollView sv = (ScrollView) view;
            if (canScrollViewScroll(sv)) {
                addScrollListener(sv, topAndBottom, bottom);
            } else {
                if (!bottom || topAndBottom)
                    mDrawTopDivider = false;
                if (bottom || topAndBottom)
                    mDrawBottomDivider = false;
            }
        } else if (view instanceof AdapterView) {
            final AdapterView sv = (AdapterView) view;
            if (canAdapterViewScroll(sv)) {
                addScrollListener(sv, topAndBottom, bottom);
            } else {
                if (!bottom || topAndBottom)
                    mDrawTopDivider = false;
                if (bottom || topAndBottom)
                    mDrawBottomDivider = false;
            }
        } else if (view instanceof WebView) {
            boolean canScroll = canWebViewScroll((WebView) view);
            if (!bottom || topAndBottom)
                mDrawTopDivider = canScroll;
            if (bottom || topAndBottom)
                mDrawBottomDivider = canScroll;
        } else if (view instanceof RecyclerView) {
            /* Scroll offset detection for RecyclerView isn't reliable b/c the OnScrollChangedListener
            isn't always called on scroll. We can't set a OnScrollListener either because that will
            override the user's OnScrollListener if they set one.*/
            boolean canScroll = canRecyclerViewScroll((RecyclerView) view);
            if (!bottom || topAndBottom)
                mDrawTopDivider = canScroll;
            if (bottom || topAndBottom)
                mDrawBottomDivider = canScroll;
        } else if (view instanceof ViewGroup) {
            View topView = getTopView((ViewGroup) view);
            setUpDividersVisibility(topView, topAndBottom, bottom);
            View bottomView = getBottomView((ViewGroup) view);
            if (bottomView != topView) {
                setUpDividersVisibility(bottomView, false, true);
            }
        }
    }

    private void addScrollListener(final ViewGroup vg, final boolean topAndBottom, final boolean bottom) {
        if ((!bottom && mTopOnScrollChangedListener == null
                || (bottom && mBottomOnScrollChangedListener == null))) {
            ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    invalidateDividersForScrollingView(vg, topAndBottom, bottom);
                }
            };
            if (!bottom) {
                mTopOnScrollChangedListener = onScrollChangedListener;
                vg.getViewTreeObserver().addOnScrollChangedListener(mTopOnScrollChangedListener);
            } else {
                mBottomOnScrollChangedListener = onScrollChangedListener;
                vg.getViewTreeObserver().addOnScrollChangedListener(mBottomOnScrollChangedListener);
            }
        }
    }

    private void invalidateDividersForScrollingView(ViewGroup view, final boolean topAndBottom, boolean bottom) {
        boolean hasButtons = false;
        for (MDButton button : mButtons) {
            if (button != null && button.getVisibility() != View.GONE) {
                hasButtons = true;
            }
        }
        if ((!bottom || topAndBottom) && view.getChildCount() > 0) {
            mDrawTopDivider = mTitleBar != null &&
                    mTitleBar.getVisibility() != View.GONE &&
                    //Not scrolled to the top.
                    view.getScrollY() + view.getPaddingTop() > view.getChildAt(0).getTop();

        }
        if ((bottom || topAndBottom) && view.getChildCount() > 0) {
            mDrawBottomDivider = hasButtons &&
                    view.getScrollY() + view.getHeight() - view.getPaddingBottom() < view.getChildAt(view.getChildCount() - 1).getBottom();
        }
        invalidate();
    }

    public static boolean canRecyclerViewScroll(RecyclerView view) {
        final RecyclerView.LayoutManager lm = view.getLayoutManager();
        final int count = view.getAdapter().getItemCount();
        int lastVisible;

        if (lm instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) lm;
            lastVisible = llm.findLastVisibleItemPosition();
        } else {
            throw new MaterialDialog.NotImplementedException("Material Dialogs currently only supports LinearLayoutManager. Please report any new layout managers.");
        }

        if (lastVisible == -1)
            return false;
        /* We scroll if the last item is not visible */
        final boolean lastItemVisible = lastVisible == count - 1;
        return !lastItemVisible ||
                (view.getChildCount() > 0 && view.getChildAt(view.getChildCount() - 1).getBottom() > view.getHeight() - view.getPaddingBottom());
    }

    private static boolean canScrollViewScroll(ScrollView sv) {
        if (sv.getChildCount() == 0)
            return false;
        final int childHeight = sv.getChildAt(0).getMeasuredHeight();
        return sv.getMeasuredHeight() - sv.getPaddingTop() - sv.getPaddingBottom() < childHeight;
    }

    private static boolean canWebViewScroll(WebView view) {
        return view.getMeasuredHeight() > view.getContentHeight();
    }

    private static boolean canAdapterViewScroll(AdapterView lv) {
        /* Force it to layout it's children */
        if (lv.getLastVisiblePosition() == -1)
            return false;

        /* We can scroll if the first or last item is not visible */
        boolean firstItemVisible = lv.getFirstVisiblePosition() == 0;
        boolean lastItemVisible = lv.getLastVisiblePosition() == lv.getCount() - 1;

        if (firstItemVisible && lastItemVisible && lv.getChildCount() > 0) {
            /* Or the first item's top is above or own top */
            if (lv.getChildAt(0).getTop() < lv.getPaddingTop())
                return true;
            /* or the last item's bottom is beyond our own bottom */
            return lv.getChildAt(lv.getChildCount() - 1).getBottom() >
                    lv.getHeight() - lv.getPaddingBottom();
        }

        return true;
    }

    /**
     * Find the view touching the bottom of this ViewGroup. Non visible children are ignored,
     * however getChildDrawingOrder is not taking into account for simplicity and because it behaves
     * inconsistently across platform versions.
     *
     * @return View touching the bottom of this ViewGroup or null
     */
    @Nullable
    private static View getBottomView(ViewGroup viewGroup) {
        if (viewGroup == null || viewGroup.getChildCount() == 0)
            return null;
        View bottomView = null;
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View child = viewGroup.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE && child.getBottom() == viewGroup.getMeasuredHeight()) {
                bottomView = child;
                break;
            }
        }
        return bottomView;
    }

    @Nullable
    private static View getTopView(ViewGroup viewGroup) {
        if (viewGroup == null || viewGroup.getChildCount() == 0)
            return null;
        View topView = null;
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View child = viewGroup.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE && child.getTop() == 0) {
                topView = child;
                break;
            }
        }
        return topView;
    }
}