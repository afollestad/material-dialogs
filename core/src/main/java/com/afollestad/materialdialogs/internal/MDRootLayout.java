package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ScrollView;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.R;
import com.afollestad.materialdialogs.StackingBehavior;
import com.afollestad.materialdialogs.util.DialogUtils;

/**
 * @author Kevin Barry (teslacoil) 4/02/2015 This is the top level view for all MaterialDialogs It
 *     handles the layout of: titleFrame (md_stub_titleframe) content (text, custom view, listview,
 *     etc) buttonDefault... (either stacked or horizontal)
 */
public class MDRootLayout extends ViewGroup {

  private static final int INDEX_NEUTRAL = 0;
  private static final int INDEX_NEGATIVE = 1;
  private static final int INDEX_POSITIVE = 2;
  private final MDButton[] buttons = new MDButton[3];
  private int maxHeight;
  private View titleBar;
  private View content;
  private CheckBox checkPrompt;
  private boolean drawTopDivider = false;
  private boolean drawBottomDivider = false;
  private StackingBehavior stackBehavior = StackingBehavior.ADAPTIVE;
  private boolean isStacked = false;
  private boolean useFullPadding = true;
  private boolean reducePaddingNoTitleNoButtons;
  private boolean noTitleNoPadding;

  private int noTitlePaddingFull;
  private int buttonPaddingFull;
  private int buttonBarHeight;

  private GravityEnum buttonGravity = GravityEnum.START;

  /* Margin from dialog frame to first button */
  private int buttonHorizontalEdgeMargin;

  private Paint dividerPaint;

  private ViewTreeObserver.OnScrollChangedListener topOnScrollChangedListener;
  private ViewTreeObserver.OnScrollChangedListener bottomOnScrollChangedListener;
  private int dividerWidth;

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

  private static boolean isVisible(@Nullable View v) {
    boolean visible = v != null && v.getVisibility() != View.GONE;
    if (visible && v instanceof MDButton) {
      visible = ((MDButton) v).getText().toString().trim().length() > 0;
    }
    return visible;
  }

  public static boolean canRecyclerViewScroll(@Nullable RecyclerView view) {
    return view != null
        && view.getLayoutManager() != null
        && view.getLayoutManager().canScrollVertically();
  }

  private static boolean canScrollViewScroll(ScrollView sv) {
    if (sv.getChildCount() == 0) {
      return false;
    }
    final int childHeight = sv.getChildAt(0).getMeasuredHeight();
    return sv.getMeasuredHeight() - sv.getPaddingTop() - sv.getPaddingBottom() < childHeight;
  }

  private static boolean canWebViewScroll(WebView view) {
    //noinspection deprecation
    return view.getMeasuredHeight() < view.getContentHeight() * view.getScale();
  }

  private static boolean canAdapterViewScroll(AdapterView lv) {
    /* Force it to layout it's children */
    if (lv.getLastVisiblePosition() == -1) {
      return false;
    }

    /* We can scroll if the first or last item is not visible */
    boolean firstItemVisible = lv.getFirstVisiblePosition() == 0;
    boolean lastItemVisible = lv.getLastVisiblePosition() == lv.getCount() - 1;

    if (firstItemVisible && lastItemVisible && lv.getChildCount() > 0) {
      /* Or the first item's top is above or own top */
      if (lv.getChildAt(0).getTop() < lv.getPaddingTop()) {
        return true;
      }
      /* or the last item's bottom is beyond our own bottom */
      return lv.getChildAt(lv.getChildCount() - 1).getBottom()
          > lv.getHeight() - lv.getPaddingBottom();
    }

    return true;
  }

  /**
   * Find the view touching the bottom of this ViewGroup. Non visible children are ignored, however
   * getChildDrawingOrder is not taking into account for simplicity and because it behaves
   * inconsistently across platform versions.
   *
   * @return View touching the bottom of this ViewGroup or null
   */
  @Nullable
  private static View getBottomView(@Nullable ViewGroup viewGroup) {
    if (viewGroup == null || viewGroup.getChildCount() == 0) {
      return null;
    }
    View bottomView = null;
    for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
      View child = viewGroup.getChildAt(i);
      if (child.getVisibility() == View.VISIBLE
          && child.getBottom() == viewGroup.getMeasuredHeight()) {
        bottomView = child;
        break;
      }
    }
    return bottomView;
  }

  @Nullable
  private static View getTopView(@Nullable ViewGroup viewGroup) {
    if (viewGroup == null || viewGroup.getChildCount() == 0) {
      return null;
    }
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

  private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    Resources r = context.getResources();

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MDRootLayout, defStyleAttr, 0);
    reducePaddingNoTitleNoButtons =
        a.getBoolean(R.styleable.MDRootLayout_md_reduce_padding_no_title_no_buttons, true);
    a.recycle();

    noTitlePaddingFull = r.getDimensionPixelSize(R.dimen.md_notitle_vertical_padding);
    buttonPaddingFull = r.getDimensionPixelSize(R.dimen.md_button_frame_vertical_padding);

    buttonHorizontalEdgeMargin = r.getDimensionPixelSize(R.dimen.md_button_padding_frame_side);
    buttonBarHeight = r.getDimensionPixelSize(R.dimen.md_button_height);

    dividerPaint = new Paint();
    dividerWidth = r.getDimensionPixelSize(R.dimen.md_divider_height);
    dividerPaint.setColor(DialogUtils.resolveColor(context, R.attr.md_divider_color));
    setWillNotDraw(false);
  }

  public void setMaxHeight(int maxHeight) {
    this.maxHeight = maxHeight;
  }

  public void noTitleNoPadding() {
    noTitleNoPadding = true;
  }

  @Override
  public void onFinishInflate() {
    super.onFinishInflate();
    for (int i = 0; i < getChildCount(); i++) {
      View v = getChildAt(i);
      if (v.getId() == R.id.md_titleFrame) {
        titleBar = v;
      } else if (v.getId() == R.id.md_buttonDefaultNeutral) {
        buttons[INDEX_NEUTRAL] = (MDButton) v;
      } else if (v.getId() == R.id.md_buttonDefaultNegative) {
        buttons[INDEX_NEGATIVE] = (MDButton) v;
      } else if (v.getId() == R.id.md_buttonDefaultPositive) {
        buttons[INDEX_POSITIVE] = (MDButton) v;
      } else if (v.getId() == R.id.md_promptCheckbox) {
        checkPrompt = (CheckBox) v;
      } else {
        content = v;
      }
    }
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);

    if (height > maxHeight) {
      height = maxHeight;
    }

    useFullPadding = true;
    boolean hasButtons = false;

    final boolean stacked;
    if (stackBehavior == StackingBehavior.ALWAYS) {
      stacked = true;
    } else if (stackBehavior == StackingBehavior.NEVER) {
      stacked = false;
    } else {
      int buttonsWidth = 0;
      for (MDButton button : buttons) {
        if (button != null && isVisible(button)) {
          button.setStacked(false, false);
          measureChild(button, widthMeasureSpec, heightMeasureSpec);
          buttonsWidth += button.getMeasuredWidth();
          hasButtons = true;
        }
      }

      int buttonBarPadding =
          getContext().getResources().getDimensionPixelSize(R.dimen.md_neutral_button_margin);
      final int buttonFrameWidth = width - 2 * buttonBarPadding;
      stacked = buttonsWidth > buttonFrameWidth;
    }

    int stackedHeight = 0;
    isStacked = stacked;
    if (stacked) {
      for (MDButton button : buttons) {
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
      if (isStacked) {
        availableHeight -= stackedHeight;
        fullPadding += 2 * buttonPaddingFull;
        minPadding += 2 * buttonPaddingFull;
      } else {
        availableHeight -= buttonBarHeight;
        fullPadding += 2 * buttonPaddingFull;
        /* No minPadding */
      }
    } else {
      /* Content has 8dp, we add 16dp and get 24dp, the frame margin */
      fullPadding += 2 * buttonPaddingFull;
    }

    if (isVisible(titleBar)) {
      titleBar.measure(
          MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.UNSPECIFIED);
      availableHeight -= titleBar.getMeasuredHeight();
    } else if (!noTitleNoPadding) {
      fullPadding += noTitlePaddingFull;
    }

    if (isVisible(content)) {
      content.measure(
          MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(availableHeight - minPadding, MeasureSpec.AT_MOST));

      if (content.getMeasuredHeight() <= availableHeight - fullPadding) {
        if (!reducePaddingNoTitleNoButtons || isVisible(titleBar) || hasButtons) {
          useFullPadding = true;
          availableHeight -= content.getMeasuredHeight() + fullPadding;
        } else {
          useFullPadding = false;
          availableHeight -= content.getMeasuredHeight() + minPadding;
        }
      } else {
        useFullPadding = false;
        availableHeight = 0;
      }
    }

    setMeasuredDimension(width, height - availableHeight);
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (content != null) {
      if (drawTopDivider) {
        int y = content.getTop();
        canvas.drawRect(0, y - dividerWidth, getMeasuredWidth(), y, dividerPaint);
      }

      if (drawBottomDivider) {
        int y = content.getBottom();
        if (checkPrompt != null && checkPrompt.getVisibility() == View.GONE) {
          y = checkPrompt.getTop();
        }
        canvas.drawRect(0, y, getMeasuredWidth(), y + dividerWidth, dividerPaint);
      }
    }
  }

  @Override
  protected void onLayout(boolean changed, final int l, int t, final int r, int b) {
    if (isVisible(titleBar)) {
      int height = titleBar.getMeasuredHeight();
      titleBar.layout(l, t, r, t + height);
      t += height;
    } else if (!noTitleNoPadding && useFullPadding) {
      t += noTitlePaddingFull;
    }

    if (isVisible(content)) {
      content.layout(l, t, r, t + content.getMeasuredHeight());
    }

    if (isStacked) {
      b -= buttonPaddingFull;
      for (MDButton mButton : buttons) {
        if (isVisible(mButton)) {
          mButton.layout(l, b - mButton.getMeasuredHeight(), r, b);
          b -= mButton.getMeasuredHeight();
        }
      }
    } else {
      int barTop;
      int barBottom = b;
      if (useFullPadding) {
        barBottom -= buttonPaddingFull;
      }
      barTop = barBottom - buttonBarHeight;
      /* START:
        Neutral   Negative  Positive

        CENTER:
        Negative  Neutral   Positive

        END:
        Positive  Negative  Neutral

        (With no Positive, Negative takes it's place except for CENTER)
      */
      int offset = buttonHorizontalEdgeMargin;

      /* Used with CENTER gravity */
      int neutralLeft = -1;
      int neutralRight = -1;

      if (isVisible(buttons[INDEX_POSITIVE])) {
        int bl, br;
        if (buttonGravity == GravityEnum.END) {
          bl = l + offset;
          br = bl + buttons[INDEX_POSITIVE].getMeasuredWidth();
        } else {
          /* START || CENTER */
          br = r - offset;
          bl = br - buttons[INDEX_POSITIVE].getMeasuredWidth();
          neutralRight = bl;
        }
        buttons[INDEX_POSITIVE].layout(bl, barTop, br, barBottom);
        offset += buttons[INDEX_POSITIVE].getMeasuredWidth();
      }

      if (isVisible(buttons[INDEX_NEGATIVE])) {
        int bl, br;
        if (buttonGravity == GravityEnum.END) {
          bl = l + offset;
          br = bl + buttons[INDEX_NEGATIVE].getMeasuredWidth();
        } else if (buttonGravity == GravityEnum.START) {
          br = r - offset;
          bl = br - buttons[INDEX_NEGATIVE].getMeasuredWidth();
        } else {
          /* CENTER */
          bl = l + buttonHorizontalEdgeMargin;
          br = bl + buttons[INDEX_NEGATIVE].getMeasuredWidth();
          neutralLeft = br;
        }
        buttons[INDEX_NEGATIVE].layout(bl, barTop, br, barBottom);
      }

      if (isVisible(buttons[INDEX_NEUTRAL])) {
        int bl, br;
        if (buttonGravity == GravityEnum.END) {
          br = r - buttonHorizontalEdgeMargin;
          bl = br - buttons[INDEX_NEUTRAL].getMeasuredWidth();
        } else if (buttonGravity == GravityEnum.START) {
          bl = l + buttonHorizontalEdgeMargin;
          br = bl + buttons[INDEX_NEUTRAL].getMeasuredWidth();
        } else {
          /* CENTER */
          if (neutralLeft == -1 && neutralRight != -1) {
            neutralLeft = neutralRight - buttons[INDEX_NEUTRAL].getMeasuredWidth();
          } else if (neutralRight == -1 && neutralLeft != -1) {
            neutralRight = neutralLeft + buttons[INDEX_NEUTRAL].getMeasuredWidth();
          } else if (neutralRight == -1) {
            neutralLeft = (r - l) / 2 - buttons[INDEX_NEUTRAL].getMeasuredWidth() / 2;
            neutralRight = neutralLeft + buttons[INDEX_NEUTRAL].getMeasuredWidth();
          }
          bl = neutralLeft;
          br = neutralRight;
        }

        buttons[INDEX_NEUTRAL].layout(bl, barTop, br, barBottom);
      }
    }

    setUpDividersVisibility(content, true, true);
  }

  public void setStackingBehavior(StackingBehavior behavior) {
    stackBehavior = behavior;
    invalidate();
  }

  public void setDividerColor(int color) {
    dividerPaint.setColor(color);
    invalidate();
  }

  public void setButtonGravity(GravityEnum gravity) {
    buttonGravity = gravity;
    invertGravityIfNecessary();
  }

  private void invertGravityIfNecessary() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      return;
    }
    Configuration config = getResources().getConfiguration();
    if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
      switch (buttonGravity) {
        case START:
          buttonGravity = GravityEnum.END;
          break;
        case END:
          buttonGravity = GravityEnum.START;
          break;
      }
    }
  }

  public void setButtonStackedGravity(GravityEnum gravity) {
    for (MDButton mButton : buttons) {
      if (mButton != null) {
        mButton.setStackedGravity(gravity);
      }
    }
  }

  private void setUpDividersVisibility(
      @Nullable final View view, final boolean setForTop, final boolean setForBottom) {
    if (view == null) {
      return;
    }
    if (view instanceof ScrollView) {
      final ScrollView sv = (ScrollView) view;
      if (canScrollViewScroll(sv)) {
        addScrollListener(sv, setForTop, setForBottom);
      } else {
        if (setForTop) {
          drawTopDivider = false;
        }
        if (setForBottom) {
          drawBottomDivider = false;
        }
      }
    } else if (view instanceof AdapterView) {
      final AdapterView sv = (AdapterView) view;
      if (canAdapterViewScroll(sv)) {
        addScrollListener(sv, setForTop, setForBottom);
      } else {
        if (setForTop) {
          drawTopDivider = false;
        }
        if (setForBottom) {
          drawBottomDivider = false;
        }
      }
    } else if (view instanceof WebView) {
      view.getViewTreeObserver()
          .addOnPreDrawListener(
              new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                  if (view.getMeasuredHeight() != 0) {
                    if (!canWebViewScroll((WebView) view)) {
                      if (setForTop) {
                        drawTopDivider = false;
                      }
                      if (setForBottom) {
                        drawBottomDivider = false;
                      }
                    } else {
                      addScrollListener((ViewGroup) view, setForTop, setForBottom);
                    }
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                  }
                  return true;
                }
              });
    } else if (view instanceof RecyclerView) {
      boolean canScroll = canRecyclerViewScroll((RecyclerView) view);
      if (setForTop) {
        drawTopDivider = canScroll;
      }
      if (setForBottom) {
        drawBottomDivider = canScroll;
      }
      if (canScroll) {
        addScrollListener((ViewGroup) view, setForTop, setForBottom);
      }
    } else if (view instanceof ViewGroup) {
      View topView = getTopView((ViewGroup) view);
      setUpDividersVisibility(topView, setForTop, setForBottom);
      View bottomView = getBottomView((ViewGroup) view);
      if (bottomView != topView) {
        setUpDividersVisibility(bottomView, false, true);
      }
    }
  }

  private void addScrollListener(
      final ViewGroup vg, final boolean setForTop, final boolean setForBottom) {
    if ((!setForBottom && topOnScrollChangedListener == null
        || (setForBottom && bottomOnScrollChangedListener == null))) {
      if (vg instanceof RecyclerView) {
        RecyclerView.OnScrollListener scrollListener =
            new RecyclerView.OnScrollListener() {
              @Override
              public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean hasButtons = false;
                for (MDButton button : buttons) {
                  if (button != null && button.getVisibility() != View.GONE) {
                    hasButtons = true;
                    break;
                  }
                }
                invalidateDividersForScrollingView(vg, setForTop, setForBottom, hasButtons);
                invalidate();
              }
            };
        ((RecyclerView) vg).addOnScrollListener(scrollListener);
        scrollListener.onScrolled((RecyclerView) vg, 0, 0);
      } else {
        ViewTreeObserver.OnScrollChangedListener onScrollChangedListener =
            new ViewTreeObserver.OnScrollChangedListener() {
              @Override
              public void onScrollChanged() {
                boolean hasButtons = false;
                for (MDButton button : buttons) {
                  if (button != null && button.getVisibility() != View.GONE) {
                    hasButtons = true;
                    break;
                  }
                }
                if (vg instanceof WebView) {
                  invalidateDividersForWebView((WebView) vg, setForTop, setForBottom, hasButtons);
                } else {
                  invalidateDividersForScrollingView(vg, setForTop, setForBottom, hasButtons);
                }
                invalidate();
              }
            };
        if (!setForBottom) {
          topOnScrollChangedListener = onScrollChangedListener;
          vg.getViewTreeObserver().addOnScrollChangedListener(topOnScrollChangedListener);
        } else {
          bottomOnScrollChangedListener = onScrollChangedListener;
          vg.getViewTreeObserver().addOnScrollChangedListener(bottomOnScrollChangedListener);
        }
        onScrollChangedListener.onScrollChanged();
      }
    }
  }

  private void invalidateDividersForScrollingView(
      ViewGroup view, final boolean setForTop, boolean setForBottom, boolean hasButtons) {
    if (setForTop && view.getChildCount() > 0) {
      drawTopDivider =
          titleBar != null
              && titleBar.getVisibility() != View.GONE
              &&
              // Not scrolled to the top.
              view.getScrollY() + view.getPaddingTop() > view.getChildAt(0).getTop();
    }
    if (setForBottom && view.getChildCount() > 0) {
      drawBottomDivider =
          hasButtons
              && view.getScrollY() + view.getHeight() - view.getPaddingBottom()
                  < view.getChildAt(view.getChildCount() - 1).getBottom();
    }
  }

  private void invalidateDividersForWebView(
      WebView view, final boolean setForTop, boolean setForBottom, boolean hasButtons) {
    if (setForTop) {
      drawTopDivider =
          titleBar != null
              && titleBar.getVisibility() != View.GONE
              &&
              // Not scrolled to the top.
              view.getScrollY() + view.getPaddingTop() > 0;
    }
    if (setForBottom) {
      //noinspection deprecation
      drawBottomDivider =
          hasButtons
              && view.getScrollY() + view.getMeasuredHeight() - view.getPaddingBottom()
                  < view.getContentHeight() * view.getScale();
    }
  }
}
