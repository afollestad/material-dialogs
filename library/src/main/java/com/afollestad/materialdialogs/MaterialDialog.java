package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.base.DialogBase;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.afollestad.materialdialogs.util.RecyclerUtil;
import com.afollestad.materialdialogs.util.TypefaceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialDialog extends DialogBase implements View.OnClickListener {

    protected final View view;
    protected final Builder mBuilder;
    protected ListView listView;
    protected ImageView icon;
    protected TextView title;
    protected View titleFrame;
    protected FrameLayout customViewFrame;
    protected ProgressBar mProgress;
    protected TextView mProgressLabel;
    protected TextView content;

    protected View positiveButton;
    protected View neutralButton;
    protected View negativeButton;
    protected boolean isStacked;
    protected final int defaultItemColor;
    protected ListType listType;
    protected List<Integer> selectedIndicesList;

    private static ContextThemeWrapper getTheme(Builder builder) {
        TypedArray a = builder.context.getTheme().obtainStyledAttributes(new int[]{R.attr.md_dark_theme});
        boolean darkTheme = builder.theme == Theme.DARK;
        if (!darkTheme) {
            try {
                darkTheme = a.getBoolean(0, false);
                builder.theme = darkTheme ? Theme.DARK : Theme.LIGHT;
            } finally {
                a.recycle();
            }
        }
        return new ContextThemeWrapper(builder.context, darkTheme ? R.style.MD_Dark : R.style.MD_Light);
    }

    @SuppressLint("InflateParams")
    protected MaterialDialog(Builder builder) {
        super(getTheme(builder));
        mBuilder = builder;

        if (!mBuilder.useCustomFonts) {
            if (mBuilder.mediumFont == null)
                mBuilder.mediumFont = TypefaceHelper.get(getContext(), "Roboto-Medium");
            if (mBuilder.regularFont == null)
                mBuilder.regularFont = TypefaceHelper.get(getContext(), "Roboto-Regular");
        }

        final LayoutInflater inflater = LayoutInflater.from(mBuilder.context);
        this.view = inflater.inflate(R.layout.md_dialog, null);
        this.setCancelable(builder.cancelable);

        if (mBuilder.backgroundColor == 0)
            mBuilder.backgroundColor = DialogUtils.resolveColor(mBuilder.context, R.attr.md_background_color);
        if (mBuilder.backgroundColor != 0)
            this.view.setBackgroundColor(mBuilder.backgroundColor);

        mBuilder.positiveColor = DialogUtils.resolveColor(mBuilder.context, R.attr.md_positive_color, mBuilder.positiveColor);
        mBuilder.neutralColor = DialogUtils.resolveColor(mBuilder.context, R.attr.md_neutral_color, mBuilder.neutralColor);
        mBuilder.negativeColor = DialogUtils.resolveColor(mBuilder.context, R.attr.md_negative_color, mBuilder.negativeColor);

        title = (TextView) view.findViewById(R.id.title);
        icon = (ImageView) view.findViewById(R.id.icon);
        titleFrame = view.findViewById(R.id.titleFrame);
        content = (TextView) view.findViewById(R.id.content);

        if (mBuilder.mIndeterminateProgress || mBuilder.mProgress > -2) {
            mBuilder.customView = inflater.inflate(mBuilder.mIndeterminateProgress ? R.layout.md_progress_dialog_indeterminate
                    : R.layout.md_progress_dialog, (ViewGroup) this.view, false);
            mProgress = (ProgressBar) mBuilder.customView.findViewById(android.R.id.progress);
            content = (TextView) mBuilder.customView.findViewById(android.R.id.message);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable indDraw = mProgress.getIndeterminateDrawable();
                if (indDraw != null) {
                    indDraw.setColorFilter(mBuilder.accentColor, PorterDuff.Mode.SRC_ATOP);
                    mProgress.setIndeterminateDrawable(indDraw);
                }
                Drawable regDraw = mProgress.getProgressDrawable();
                if (regDraw != null) {
                    regDraw.setColorFilter(mBuilder.accentColor, PorterDuff.Mode.SRC_ATOP);
                    mProgress.setProgressDrawable(regDraw);
                }
            }

            if (!mBuilder.mIndeterminateProgress) {
                mProgress.setProgress(0);
                mProgress.setMax(mBuilder.mProgressMax);
                mProgressLabel = (TextView) mBuilder.customView.findViewById(R.id.label);
                mProgressLabel.setText("0%");
            }
            int bottomPadding = (int) getContext().getResources().getDimension(R.dimen.md_dialog_frame_margin);
            int topPadding = builder.title == null ? bottomPadding
                    : (int) getContext().getResources().getDimension(R.dimen.md_progressdialog_paddingwithtitle);
            mBuilder.customView.setPadding(mBuilder.customView.getPaddingLeft(),
                    topPadding,
                    mBuilder.customView.getPaddingRight(),
                    bottomPadding);
        }

        content.setText(builder.content);
        content.setMovementMethod(new LinkMovementMethod());
        setTypeface(content, mBuilder.regularFont);
        content.setLineSpacing(0f, builder.contentLineSpacingMultiplier);
        if (mBuilder.positiveColor == 0) {
            content.setLinkTextColor(DialogUtils.resolveColor(getContext(), android.R.attr.textColorPrimary));
        } else {
            content.setLinkTextColor(mBuilder.positiveColor);
        }

        title.setGravity(gravityIntToGravity(builder.titleGravity));

        if (builder.contentColorSet) {
            content.setTextColor(builder.contentColor);
        } else {
            final int fallback = DialogUtils.resolveColor(getContext(), android.R.attr.textColorSecondary);
            final int contentColor = DialogUtils.resolveColor(getContext(), R.attr.md_content_color, fallback);
            content.setTextColor(contentColor);
        }

        if (builder.itemColorSet) {
            defaultItemColor = builder.itemColor;
        } else if (builder.theme == Theme.LIGHT) {
            defaultItemColor = Color.BLACK;
        } else {
            defaultItemColor = Color.WHITE;
        }

        if (mBuilder.customView != null) {
            invalidateCustomViewAssociations();
            FrameLayout frame = (FrameLayout) view.findViewById(R.id.customViewFrame);
            customViewFrame = frame;
            View innerView = mBuilder.customView;

            if (mBuilder.wrapCustomViewInScroll) {
                /* Apply the frame padding to the content, this allows the ScrollView to draw it's
                   overscroll glow without clipping */
                final Resources r = getContext().getResources();
                final int framePadding = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);

                final ScrollView sv = new ScrollView(getContext());
                int paddingTop;
                int paddingBottom;
                if (titleFrame.getVisibility() != View.GONE)
                    paddingTop = r.getDimensionPixelSize(R.dimen.md_content_vertical_padding);
                else
                    paddingTop = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);

                if (hasActionButtons())
                    paddingBottom = r.getDimensionPixelSize(R.dimen.md_content_vertical_padding);
                else
                    paddingBottom = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);

                sv.setClipToPadding(false);

                if (innerView instanceof EditText) {
                    // Setting padding to an EditText causes visual errors, set it to the parent instead
                    sv.setPadding(framePadding, paddingTop, framePadding, paddingBottom);
                } else {
                    // Setting padding to scroll view pushes the scroll bars out, don't do it if not necessary (like above)
                    sv.setPadding(0, paddingTop, 0, paddingBottom);
                    innerView.setPadding(framePadding, 0, framePadding, 0);
                }

                sv.addView(innerView, new ScrollView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                innerView = sv;
            }

            frame.addView(innerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            invalidateCustomViewAssociations();
        }

        if (mBuilder.listCallbackMulti != null)
            selectedIndicesList = new ArrayList<>();

        boolean adapterProvided = mBuilder.adapter != null;
        if (mBuilder.items != null && mBuilder.items.length > 0 || adapterProvided) {
            listView = (ListView) view.findViewById(R.id.contentListView);
            listView.setSelector(getListSelector());

            if (!adapterProvided) {
                // Determine list type
                if (mBuilder.listCallbackSingle != null) {
                    listType = ListType.SINGLE;
                } else if (mBuilder.listCallbackMulti != null) {
                    listType = ListType.MULTI;
                    if (mBuilder.selectedIndices != null) {
                        selectedIndicesList = new ArrayList<>(Arrays.asList(mBuilder.selectedIndices));
                    }
                } else {
                    listType = ListType.REGULAR;
                }
                mBuilder.adapter = new MaterialDialogAdapter(mBuilder.context,
                        ListType.getLayoutForType(listType), R.id.title, mBuilder.items);
            }
        }

        if (builder.icon != null) {
            icon.setVisibility(View.VISIBLE);
            icon.setImageDrawable(builder.icon);
        } else {
            Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_icon);
            if (d != null) {
                icon.setVisibility(View.VISIBLE);
                icon.setImageDrawable(d);
            } else {
                icon.setVisibility(View.GONE);
            }
        }

        int maxIconSize = builder.maxIconSize;
        if (maxIconSize == -1) {
            maxIconSize = DialogUtils.resolveDimension(mBuilder.context, R.attr.md_icon_max_size);
        }

        if (builder.limitIconToDefaultSize ||
                DialogUtils.resolveBoolean(mBuilder.context, R.attr.md_icon_limit_icon_to_default_size)) {
            maxIconSize = mBuilder.context.getResources().getDimensionPixelSize(R.dimen.md_icon_max_size);
        }

        if (maxIconSize > -1) {
            icon.setAdjustViewBounds(true);
            icon.setMaxHeight(maxIconSize);
            icon.setMaxWidth(maxIconSize);
            icon.requestLayout();
        }

        if (builder.title == null) {
            titleFrame.setVisibility(View.GONE);
        } else {
            title.setText(builder.title);
            setTypeface(title, mBuilder.mediumFont);
            if (builder.titleColorSet) {
                title.setTextColor(builder.titleColor);
            } else {
                final int fallback = DialogUtils.resolveColor(getContext(), android.R.attr.textColorPrimary);
                title.setTextColor(DialogUtils.resolveColor(getContext(), R.attr.md_title_color, fallback));
            }
            content.setGravity(gravityIntToGravity(builder.contentGravity));
        }

        if (builder.showListener != null) {
            setOnShowListener(builder.showListener);
        }
        if (builder.cancelListener != null) {
            setOnCancelListener(builder.cancelListener);
        }
        if (builder.dismissListener != null) {
            setOnDismissListener(builder.dismissListener);
        }
        if (builder.keyListener != null) {
            setOnKeyListener(builder.keyListener);
        }

        updateFramePadding();
        invalidateActions();
        setOnShowListenerInternal();
        setViewInternal(view);

        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        invalidateCustomViewAssociations();
                    }
                });

        if (builder.theme == Theme.LIGHT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            setInverseBackgroundForced(true);
            if (!builder.titleColorSet)
                title.setTextColor(Color.BLACK);
            if (!builder.contentColorSet)
                content.setTextColor(Color.BLACK);
        }
    }

    private static int gravityIntToGravity(GravityEnum gravity) {
        switch (gravity) {
            case CENTER:
                return Gravity.CENTER_HORIZONTAL;
            case END:
                return Gravity.END;
            default:
                return Gravity.START;
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        super.onShow(dialog); // calls any external show listeners
        checkIfStackingNeeded();
        invalidateCustomViewAssociations();
    }

    /**
     * To account for scrolling content and overscroll glows, the frame padding/margins sometimes
     * must be set on inner views. This is dependent on the visibility of the title bar and action
     * buttons. This method determines where the padding or margins are needed and applies them.
     */
    private void updateFramePadding() {
        Resources r = getContext().getResources();
        int framePadding = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);

        View contentScrollView = view.findViewById(R.id.contentScrollView);
        int paddingTop = contentScrollView.getPaddingTop();
        int paddingBottom = contentScrollView.getPaddingBottom();

        if (!hasActionButtons())
            paddingBottom = framePadding;
        if (titleFrame.getVisibility() == View.GONE)
            paddingTop = framePadding;

        contentScrollView.setPadding(contentScrollView.getPaddingLeft(), paddingTop,
                contentScrollView.getPaddingRight(), paddingBottom);

        if (listView != null) {
            // Padding below title is reduced for divider.
            final int titlePaddingBottom = (int) mBuilder.context.getResources().getDimension(R.dimen.md_title_frame_margin_bottom_list);
            titleFrame.setPadding(titleFrame.getPaddingLeft(),
                    titleFrame.getPaddingTop(),
                    titleFrame.getPaddingRight(),
                    titlePaddingBottom);
        }
    }

    /**
     * Invalidates visibility of views for the presence of a custom view or list content
     */
    private void invalidateCustomViewAssociations() {
        if (view.getMeasuredWidth() == 0) {
            return;
        }
        View contentScrollView = view.findViewById(R.id.contentScrollView);
        final int contentHorizontalPadding = (int) mBuilder.context.getResources()
                .getDimension(R.dimen.md_dialog_frame_margin);
        content.setPadding(contentHorizontalPadding, 0, contentHorizontalPadding, 0);

        if (mBuilder.customView != null) {
            contentScrollView.setVisibility(View.GONE);
            customViewFrame.setVisibility(View.VISIBLE);
            boolean topScroll = canViewOrChildScroll(customViewFrame.getChildAt(0), false);
            boolean bottomScroll = canViewOrChildScroll(customViewFrame.getChildAt(0), true);
            setDividerVisibility(topScroll, bottomScroll);
        } else if ((mBuilder.items != null && mBuilder.items.length > 0) || mBuilder.adapter != null) {
            contentScrollView.setVisibility(mBuilder.content != null
                    && mBuilder.content.toString().trim().length() > 0 ? View.VISIBLE : View.GONE);
            boolean canScroll = titleFrame.getVisibility() == View.VISIBLE &&
                    (canListViewScroll() || canContentScroll());
            setDividerVisibility(canScroll, canScroll);
        } else {
            contentScrollView.setVisibility(View.VISIBLE);
            boolean canScroll = canContentScroll();
            if (canScroll) {
                final int contentVerticalPadding = (int) mBuilder.context.getResources()
                        .getDimension(R.dimen.md_title_frame_margin_bottom);
                content.setPadding(contentHorizontalPadding, contentVerticalPadding,
                        contentHorizontalPadding, contentVerticalPadding);

                // Same effect as when there's a ListView. Padding below title is reduced for divider.
                final int titlePaddingBottom = (int) mBuilder.context.getResources().getDimension(R.dimen.md_title_frame_margin_bottom_list);
                titleFrame.setPadding(titleFrame.getPaddingLeft(),
                        titleFrame.getPaddingTop(),
                        titleFrame.getPaddingRight(),
                        titlePaddingBottom);
            }
            setDividerVisibility(canScroll, canScroll);
        }
    }

    /**
     * Set the visibility of the bottom divider and adjusts the layout margin,
     * when the divider is visible the button bar bottom margin (8dp from
     * http://www.google.com/design/spec/components/dialogs.html#dialogs-specs )
     * is removed as it makes the button bar look off balanced with different amounts of padding
     * above and below the divider.
     */
    private void setDividerVisibility(boolean topVisible, boolean bottomVisible) {
        topVisible = topVisible && titleFrame.getVisibility() == View.VISIBLE;
        bottomVisible = bottomVisible && hasActionButtons();

        if (mBuilder.dividerColor == 0)
            mBuilder.dividerColor = DialogUtils.resolveColor(mBuilder.context, R.attr.md_divider_color);
        if (mBuilder.dividerColor == 0)
            mBuilder.dividerColor = DialogUtils.resolveColor(getContext(), R.attr.md_divider);

        View titleBarDivider = view.findViewById(R.id.titleBarDivider);
        if (topVisible) {
            titleBarDivider.setVisibility(View.VISIBLE);
            titleBarDivider.setBackgroundColor(mBuilder.dividerColor);
        } else {
            titleBarDivider.setVisibility(View.INVISIBLE);
        }

        View buttonBarDivider = view.findViewById(R.id.buttonBarDivider);
        if (bottomVisible) {
            buttonBarDivider.setVisibility(View.VISIBLE);
            buttonBarDivider.setBackgroundColor(mBuilder.dividerColor);

            setVerticalMargins(view.findViewById(R.id.buttonStackedFrame), -1, 0);
            setVerticalMargins(view.findViewById(R.id.buttonDefaultFrame), -1, 0);
        } else {
            buttonBarDivider.setVisibility(View.INVISIBLE);

            Resources r = getContext().getResources();
            final int bottomMargin = r.getDimensionPixelSize(R.dimen.md_button_frame_vertical_padding);
            setVerticalMargins(view.findViewById(R.id.buttonStackedFrame), -1, bottomMargin);
            setVerticalMargins(view.findViewById(R.id.buttonDefaultFrame), -1, bottomMargin);
        }
    }

    /**
     * Constructs the dialog's list content and sets up click listeners.
     */
    private void invalidateList() {
        if ((mBuilder.items == null || mBuilder.items.length == 0) && mBuilder.adapter == null)
            return;

        // Hide content
        view.findViewById(R.id.contentScrollView).setVisibility(mBuilder.content != null
                && mBuilder.content.toString().trim().length() > 0 ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.customViewFrame).setVisibility(View.GONE);

        // Set up list with adapter
        FrameLayout listViewContainer = (FrameLayout) view.findViewById(R.id.contentListViewFrame);
        listViewContainer.setVisibility(View.VISIBLE);
        listView.setAdapter(mBuilder.adapter);

        if (listType != null) {
            // Only set listener for 1st-party adapter, leave custom adapter implementation to user with getListView()
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (listType == ListType.MULTI) {
                        // Keep our selected items up to date
                        boolean isChecked = !((CheckBox) view.findViewById(R.id.control)).isChecked();  // Inverted because the view's click listener is called before the check is toggled
                        boolean previouslySelected = selectedIndicesList.contains(position);
                        if (isChecked) {
                            if (!previouslySelected) {
                                selectedIndicesList.add(position);
                            }
                        } else if (previouslySelected) {
                            selectedIndicesList.remove(Integer.valueOf(position));
                        }
                    } else if (listType == ListType.SINGLE) {
                        // Keep our selected item up to date
                        if (mBuilder.selectedIndex != position) {
                            mBuilder.selectedIndex = position;
                            ((MaterialDialogAdapter) mBuilder.adapter).notifyDataSetChanged();
                        }
                    }

                    onClick(view);
                }
            });
        }

    }

    /**
     * Find the view touching the bottom of this ViewGroup. Non visible children are ignored,
     * however getChildDrawingOrder is not taking into account for simplicity and because it behaves
     * inconsistently across platform versions.
     *
     * @return View touching the bottom of this viewgroup or null
     */
    @Nullable
    private static View getBottomView(ViewGroup viewGroup) {
        View bottomView = null;
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View child = viewGroup.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE && child.getBottom() == viewGroup.getBottom()) {
                bottomView = child;
                break;
            }
        }
        return bottomView;
    }

    @Nullable
    private static View getTopView(ViewGroup viewGroup) {
        View topView = null;
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View child = viewGroup.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE && child.getTop() == viewGroup.getTop()) {
                topView = child;
                break;
            }
        }
        return topView;
    }

    private static boolean canViewOrChildScroll(View view, boolean atBottom) {
        if (view == null || !(view instanceof ViewGroup)) {
            return false;
        }
            /* Is the bottom view something that scrolls? */
        if (view instanceof ScrollView) {
            ScrollView sv = (ScrollView) view;
            if (sv.getChildCount() == 0)
                return false;
            final int childHeight = sv.getChildAt(0).getMeasuredHeight();
            return sv.getMeasuredHeight() < childHeight;
        } else if (view instanceof AdapterView) {
            return canAdapterViewScroll((AdapterView) view);
        } else if (view instanceof WebView) {
            return canWebViewScroll((WebView) view);
        } else if (isRecyclerView(view)) {
            return RecyclerUtil.canRecyclerViewScroll(view);
        } else {
            if (atBottom) {
                return canViewOrChildScroll(getBottomView((ViewGroup) view), true);
            } else {
                return canViewOrChildScroll(getTopView((ViewGroup) view), false);
            }
        }
    }

    private static boolean isRecyclerView(View view) {
        boolean isRecyclerView = false;
        try {
            Class.forName("android.support.v7.widget.RecyclerView");

            // We got here, so now we can safely check
            isRecyclerView = RecyclerUtil.isRecyclerView(view);
        } catch (ClassNotFoundException ignored) {
        }

        return isRecyclerView;
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

        if (firstItemVisible && lastItemVisible) {
            /* Or the first item's top is above or own top */
            if (lv.getChildAt(0).getTop() < lv.getPaddingTop())
                return true;

            /* or the last item's bottom is beyond our own bottom */
            return lv.getChildAt(lv.getChildCount() - 1).getBottom() >
                    lv.getHeight() - lv.getPaddingBottom();
        }

        return true;
    }

    private boolean canListViewScroll() {
        return canAdapterViewScroll(listView);
    }


    public static class NotImplementedException extends Error {
        public NotImplementedException(String message) {
            super(message);
        }
    }

    public static class DialogException extends WindowManager.BadTokenException {
        public DialogException(String message) {
            super(message);
        }
    }

    /**
     * Detects whether or not the content TextView can be scrolled.
     */
    private boolean canContentScroll() {
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.contentScrollView);
        final int childHeight = content.getMeasuredHeight();
        return scrollView.getMeasuredHeight() < childHeight;
    }

    /**
     * Measures the action button's and their text to decide whether or not the button should be stacked.
     */
    private void checkIfStackingNeeded() {
        if (numberOfActionButtons() <= 1) {
            return;
        } else if (mBuilder.forceStacking) {
            isStacked = true;
            invalidateActions();
            return;
        }
        isStacked = false;
        int buttonsWidth = 0;

        positiveButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        neutralButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        negativeButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        if (mBuilder.positiveText != null) buttonsWidth += positiveButton.getMeasuredWidth();
        if (mBuilder.neutralText != null) buttonsWidth += neutralButton.getMeasuredWidth();
        if (mBuilder.negativeText != null) buttonsWidth += negativeButton.getMeasuredWidth();

        final int buttonFrameWidth = view.findViewById(R.id.buttonDefaultFrame).getWidth();
        isStacked = buttonsWidth > buttonFrameWidth;
        invalidateActions();
    }

    private Drawable getListSelector() {
        if (mBuilder.listSelector != 0)
            return mBuilder.context.getResources().getDrawable(mBuilder.listSelector);
        final Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_list_selector);
        if (d != null) return d;
        return DialogUtils.resolveDrawable(getContext(), R.attr.md_list_selector);
    }

    private Drawable getButtonSelector(DialogAction which) {
        if (isStacked) {
            if (mBuilder.btnSelectorStacked != 0)
                return mBuilder.context.getResources().getDrawable(mBuilder.btnSelectorStacked);
            final Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_btn_stacked_selector);
            if (d != null) return d;
            return DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_stacked_selector);
        } else {
            switch (which) {
                default: {
                    if (mBuilder.btnSelectorPositive != 0)
                        return mBuilder.context.getResources().getDrawable(mBuilder.btnSelectorPositive);
                    final Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_btn_positive_selector);
                    if (d != null) return d;
                    return DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_positive_selector);
                }
                case NEUTRAL: {
                    if (mBuilder.btnSelectorNeutral != 0)
                        return mBuilder.context.getResources().getDrawable(mBuilder.btnSelectorNeutral);
                    final Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_btn_neutral_selector);
                    if (d != null) return d;
                    return DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_neutral_selector);
                }
                case NEGATIVE: {
                    if (mBuilder.btnSelectorNegative != 0)
                        return mBuilder.context.getResources().getDrawable(mBuilder.btnSelectorNegative);
                    final Drawable d = DialogUtils.resolveDrawable(mBuilder.context, R.attr.md_btn_negative_selector);
                    if (d != null) return d;
                    return DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_negative_selector);
                }
            }
        }
    }

    /**
     * Invalidates the positive/neutral/negative action buttons. Decides whether they should be visible
     * and sets their properties (such as height, text color, etc.).
     */
    private boolean invalidateActions() {
        if (!hasActionButtons()) {
            // If the dialog is a plain list dialog, no buttons are shown.
            view.findViewById(R.id.buttonDefaultFrame).setVisibility(View.GONE);
            view.findViewById(R.id.buttonStackedFrame).setVisibility(View.GONE);
            invalidateList();
            return false;
        }

        if (isStacked) {
            view.findViewById(R.id.buttonDefaultFrame).setVisibility(View.GONE);
            view.findViewById(R.id.buttonStackedFrame).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.buttonDefaultFrame).setVisibility(View.VISIBLE);
            view.findViewById(R.id.buttonStackedFrame).setVisibility(View.GONE);
        }

        positiveButton = view.findViewById(
                isStacked ? R.id.buttonStackedPositive : R.id.buttonDefaultPositive);
        if (mBuilder.positiveText != null) {
            TextView positiveTextView = (TextView) ((FrameLayout) positiveButton).getChildAt(0);
            setTypeface(positiveTextView, mBuilder.mediumFont);
            positiveTextView.setText(mBuilder.positiveText);
            positiveTextView.setTextColor(getActionTextStateList(mBuilder.positiveColor));
            setBackgroundCompat(positiveButton, getButtonSelector(DialogAction.POSITIVE));
            positiveButton.setTag(POSITIVE);
            positiveButton.setOnClickListener(this);
            if (isStacked)
                positiveTextView.setGravity(gravityIntToGravity(mBuilder.btnStackedGravity));
        } else {
            positiveButton.setVisibility(View.GONE);
        }

        neutralButton = view.findViewById(
                isStacked ? R.id.buttonStackedNeutral : R.id.buttonDefaultNeutral);
        if (mBuilder.neutralText != null) {
            TextView neutralTextView = (TextView) ((FrameLayout) neutralButton).getChildAt(0);
            setTypeface(neutralTextView, mBuilder.mediumFont);
            neutralButton.setVisibility(View.VISIBLE);
            neutralTextView.setTextColor(getActionTextStateList(mBuilder.neutralColor));
            setBackgroundCompat(neutralButton, getButtonSelector(DialogAction.NEUTRAL));
            neutralTextView.setText(mBuilder.neutralText);
            neutralButton.setTag(NEUTRAL);
            neutralButton.setOnClickListener(this);
            if (isStacked)
                neutralTextView.setGravity(gravityIntToGravity(mBuilder.btnStackedGravity));
        } else {
            neutralButton.setVisibility(View.GONE);
        }

        negativeButton = view.findViewById(
                isStacked ? R.id.buttonStackedNegative : R.id.buttonDefaultNegative);
        if (mBuilder.negativeText != null) {
            TextView negativeTextView = (TextView) ((FrameLayout) negativeButton).getChildAt(0);
            setTypeface(negativeTextView, mBuilder.mediumFont);
            negativeButton.setVisibility(View.VISIBLE);
            negativeTextView.setTextColor(getActionTextStateList(mBuilder.negativeColor));
            setBackgroundCompat(negativeButton, getButtonSelector(DialogAction.NEGATIVE));
            negativeTextView.setText(mBuilder.negativeText);
            negativeButton.setTag(NEGATIVE);
            negativeButton.setOnClickListener(this);

            if (!isStacked) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, (int) getContext().getResources().getDimension(R.dimen.md_button_height));
                if (mBuilder.positiveText != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.addRule(RelativeLayout.START_OF, R.id.buttonDefaultPositive);
                    } else {
                        params.addRule(RelativeLayout.LEFT_OF, R.id.buttonDefaultPositive);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    } else {
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    }
                }
                negativeButton.setLayoutParams(params);
            } else {
                negativeTextView.setGravity(gravityIntToGravity(mBuilder.btnStackedGravity));
            }
        } else {
            negativeButton.setVisibility(View.GONE);
        }

        invalidateList();
        return true;
    }

    private void sendSingleChoiceCallback(View v) {
        CharSequence text = null;
        if (mBuilder.selectedIndex >= 0) {
            text = mBuilder.items[mBuilder.selectedIndex];
        }
        mBuilder.listCallbackSingle.onSelection(this, v, mBuilder.selectedIndex, text);
    }

    private void sendMultichoiceCallback() {
        List<CharSequence> selectedTitles = new ArrayList<>();
        for (Integer i : selectedIndicesList) {
            selectedTitles.add(mBuilder.items[i]);
        }
        mBuilder.listCallbackMulti.onSelection(this,
                selectedIndicesList.toArray(new Integer[selectedIndicesList.size()]),
                selectedTitles.toArray(new CharSequence[selectedTitles.size()]));
    }

    @Override
    public final void onClick(View v) {
        String tag = (String) v.getTag();
        switch (tag) {
            case POSITIVE: {
                if (mBuilder.callback != null)
                    mBuilder.callback.onPositive(this);
                if (mBuilder.listCallbackSingle != null)
                    sendSingleChoiceCallback(v);
                if (mBuilder.listCallbackMulti != null)
                    sendMultichoiceCallback();
                if (mBuilder.autoDismiss) dismiss();
                break;
            }
            case NEGATIVE: {
                if (mBuilder.callback != null)
                    mBuilder.callback.onNegative(this);
                if (mBuilder.autoDismiss) dismiss();
                break;
            }
            case NEUTRAL: {
                if (mBuilder.callback != null)
                    mBuilder.callback.onNeutral(this);
                if (mBuilder.autoDismiss) dismiss();
                break;
            }
            default: {
                String[] split = tag.split(":");
                int index = Integer.parseInt(split[0]);
                if (mBuilder.listCallback != null) {
                    if (mBuilder.autoDismiss)
                        dismiss();
                    mBuilder.listCallback.onSelection(this, v, index, split[1]);
                } else if (mBuilder.listCallbackSingle != null) {
                    RadioButton cb = (RadioButton) ((LinearLayout) v).getChildAt(0);
                    if (!cb.isChecked())
                        cb.setChecked(true);
                    if (mBuilder.autoDismiss && mBuilder.positiveText == null) {
                        dismiss();
                        sendSingleChoiceCallback(v);
                    } else if (mBuilder.alwaysCallSingleChoiceCallback) {
                        sendSingleChoiceCallback(v);
                    }
                } else if (mBuilder.listCallbackMulti != null) {
                    CheckBox cb = (CheckBox) ((LinearLayout) v).getChildAt(0);
                    cb.setChecked(!cb.isChecked());
                    if (mBuilder.alwaysCallMultiChoiceCallback) {
                        sendMultichoiceCallback();
                    }
                } else if (mBuilder.autoDismiss) dismiss();
                break;
            }
        }
    }

    /**
     * The class used to construct a MaterialDialog.
     */
    public static class Builder {

        protected final Context context;
        protected CharSequence title;
        protected GravityEnum titleGravity = GravityEnum.START;
        protected GravityEnum contentGravity = GravityEnum.START;
        protected GravityEnum btnStackedGravity = GravityEnum.END;
        protected int titleColor = -1;
        protected int contentColor = -1;
        protected CharSequence content;
        protected CharSequence[] items;
        protected CharSequence positiveText;
        protected CharSequence neutralText;
        protected CharSequence negativeText;
        protected View customView;
        protected int accentColor;
        protected int positiveColor;
        protected int negativeColor;
        protected int neutralColor;
        protected ButtonCallback callback;
        protected ListCallback listCallback;
        protected ListCallback listCallbackSingle;
        protected ListCallbackMulti listCallbackMulti;
        protected boolean alwaysCallMultiChoiceCallback = false;
        protected boolean alwaysCallSingleChoiceCallback = false;
        protected Theme theme = Theme.LIGHT;
        protected boolean cancelable = true;
        protected float contentLineSpacingMultiplier = 1.3f;
        protected int selectedIndex = -1;
        protected Integer[] selectedIndices = null;
        protected boolean autoDismiss = true;
        protected Typeface regularFont;
        protected Typeface mediumFont;
        protected boolean useCustomFonts;
        protected Drawable icon;
        protected boolean limitIconToDefaultSize;
        protected int maxIconSize = -1;
        protected ListAdapter adapter;
        protected OnDismissListener dismissListener;
        protected OnCancelListener cancelListener;
        protected OnKeyListener keyListener;
        protected OnShowListener showListener;
        protected boolean forceStacking;
        protected boolean wrapCustomViewInScroll;
        protected int dividerColor;
        protected int backgroundColor;
        protected int itemColor;
        protected boolean mIndeterminateProgress;
        protected int mProgress = -2;
        protected int mProgressMax = 0;

        // Since 0 is black and -1 is white, no default value is good for indicating if a color was set.
        // So this is a decent solution to this problem.
        protected boolean titleColorSet;
        protected boolean contentColorSet;
        protected boolean itemColorSet;

        @DrawableRes
        protected int listSelector;
        @DrawableRes
        protected int btnSelectorStacked;
        @DrawableRes
        protected int btnSelectorPositive;
        @DrawableRes
        protected int btnSelectorNeutral;
        @DrawableRes
        protected int btnSelectorNegative;

        public Builder(@NonNull Context context) {
            this.context = context;
            final int materialBlue = context.getResources().getColor(R.color.md_material_blue_600);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorAccent});
                try {
                    this.accentColor = a.getColor(0, materialBlue);
                    this.positiveColor = this.accentColor;
                    this.negativeColor = this.accentColor;
                    this.neutralColor = this.accentColor;
                } catch (Exception e) {
                    this.accentColor = materialBlue;
                    this.positiveColor = materialBlue;
                    this.negativeColor = materialBlue;
                    this.neutralColor = materialBlue;
                } finally {
                    a.recycle();
                }
            } else {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent});
                try {
                    this.accentColor = a.getColor(0, materialBlue);
                    this.positiveColor = this.accentColor;
                    this.negativeColor = this.accentColor;
                    this.neutralColor = this.accentColor;
                } catch (Exception e) {
                    this.accentColor = materialBlue;
                    this.positiveColor = materialBlue;
                    this.negativeColor = materialBlue;
                    this.neutralColor = materialBlue;
                } finally {
                    a.recycle();
                }
            }
            checkSingleton();
        }

        private void checkSingleton() {
            if (ThemeSingleton.get(false) == null) return;
            ThemeSingleton s = ThemeSingleton.get();
            theme(s.darkTheme ? Theme.DARK : Theme.LIGHT);
            if (s.titleColor != 0)
                this.titleColor = s.titleColor;
            if (s.contentColor != 0)
                this.contentColor = s.contentColor;
            if (s.positiveColor != 0)
                this.positiveColor = s.positiveColor;
            if (s.neutralColor != 0)
                this.neutralColor = s.neutralColor;
            if (s.negativeColor != 0)
                this.negativeColor = s.negativeColor;
            if (s.itemColor != 0)
                this.itemColor = s.itemColor;
            if (s.icon != null)
                this.icon = s.icon;
            if (s.backgroundColor != 0)
                this.backgroundColor = s.backgroundColor;
            if (s.dividerColor != 0)
                this.dividerColor = s.dividerColor;
            if (s.btnSelectorStacked != 0)
                this.btnSelectorStacked = s.btnSelectorStacked;
            if (s.listSelector != 0)
                this.listSelector = s.listSelector;
            if (s.btnSelectorPositive != 0)
                this.btnSelectorPositive = s.btnSelectorPositive;
            if (s.btnSelectorNeutral != 0)
                this.btnSelectorNeutral = s.btnSelectorNeutral;
            if (s.btnSelectorNegative != 0)
                this.btnSelectorNegative = s.btnSelectorNegative;
        }

        public Builder title(@StringRes int titleRes) {
            title(this.context.getString(titleRes));
            return this;
        }

        public Builder title(@NonNull CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder titleGravity(@NonNull GravityEnum gravity) {
            this.titleGravity = gravity;
            return this;
        }

        public Builder titleColor(int color) {
            this.titleColor = color;
            this.titleColorSet = true;
            return this;
        }

        public Builder titleColorRes(@ColorRes int colorRes) {
            titleColor(this.context.getResources().getColor(colorRes));
            return this;
        }

        public Builder titleColorAttr(@AttrRes int colorAttr) {
            titleColor(DialogUtils.resolveColor(this.context, colorAttr));
            return this;
        }

        /**
         * Disable usage of the default fonts. This is automatically set by
         * {@link #typeface(String, String)} and {@link #typeface(Typeface, Typeface)}.
         *
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder disableDefaultFonts() {
            this.useCustomFonts = true;
            return this;
        }

        /**
         * Sets the fonts used in the dialog. It's recommended that you use {@link #typeface(String, String)} instead,
         * to avoid duplicate Typeface allocations and high memory usage.
         *
         * @param medium  The font used on titles and action buttons. Null uses device default.
         * @param regular The font used everywhere else, like on the content and list items. Null uses device default.
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder typeface(Typeface medium, Typeface regular) {
            this.mediumFont = medium;
            this.regularFont = regular;
            this.useCustomFonts = true;
            return this;
        }

        /**
         * Sets the fonts used in the dialog, by file names. This also uses TypefaceHelper in order
         * to avoid any un-needed allocations (it recycles typefaces for you).
         *
         * @param medium  The name of font in assets/fonts, minus the extension (null uses device default). E.g. [your-project]/app/main/assets/fonts/[medium].ttf
         * @param regular The name of font in assets/fonts, minus the extension (null uses device default). E.g. [your-project]/app/main/assets/fonts/[regular].ttf
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder typeface(String medium, String regular) {
            if (medium != null)
                this.mediumFont = TypefaceHelper.get(this.context, medium);
            if (regular != null)
                this.regularFont = TypefaceHelper.get(this.context, regular);
            this.useCustomFonts = true;
            return this;
        }

        public Builder icon(@NonNull Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder iconRes(@DrawableRes int icon) {
            this.icon = context.getResources().getDrawable(icon);
            return this;
        }

        public Builder iconAttr(@AttrRes int iconAttr) {
            this.icon = DialogUtils.resolveDrawable(context, iconAttr);
            return this;
        }

        public Builder contentColor(int color) {
            this.contentColor = color;
            this.contentColorSet = true;
            return this;
        }

        public Builder contentColorRes(@ColorRes int colorRes) {
            contentColor(this.context.getResources().getColor(colorRes));
            return this;
        }

        public Builder contentColorAttr(@AttrRes int colorAttr) {
            contentColor(DialogUtils.resolveColor(this.context, colorAttr));
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            content(this.context.getString(contentRes));
            return this;
        }

        public Builder content(CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder content(@StringRes int contentRes, Object... formatArgs) {
            content(this.context.getString(contentRes, formatArgs));
            return this;
        }

        public Builder contentGravity(@NonNull GravityEnum gravity) {
            this.contentGravity = gravity;
            return this;
        }

        public Builder contentLineSpacing(float multiplier) {
            this.contentLineSpacingMultiplier = multiplier;
            return this;
        }

        public Builder items(@ArrayRes int itemsRes) {
            items(this.context.getResources().getTextArray(itemsRes));
            return this;
        }

        public Builder items(@NonNull CharSequence[] items) {
            this.items = items;
            return this;
        }

        public Builder itemsCallback(@NonNull ListCallback callback) {
            this.listCallback = callback;
            this.listCallbackSingle = null;
            this.listCallbackMulti = null;
            return this;
        }

        /**
         * Pass anything below 0 (such as -1) for the selected index to leave all options unselected initially.
         * Otherwise pass the index of an item that will be selected initially.
         *
         * @param selectedIndex The checkbox index that will be selected initially.
         * @param callback      The callback that will be called when the presses the positive button.
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder itemsCallbackSingleChoice(int selectedIndex, @NonNull ListCallback callback) {
            this.selectedIndex = selectedIndex;
            this.listCallback = null;
            this.listCallbackSingle = callback;
            this.listCallbackMulti = null;
            return this;
        }

        /**
         * By default, the single choice callback is only called when the user clicks the positive button
         * or if there are no buttons. Call this to force it to always call on item clicks even if the
         * positive button exists.
         *
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder alwaysCallSingleChoiceCallback() {
            this.alwaysCallSingleChoiceCallback = true;
            return this;
        }

        /**
         * Pass null for the selected indices to leave all options unselected initially. Otherwise pass
         * an array of indices that will be selected initially.
         *
         * @param selectedIndices The radio button indices that will be selected initially.
         * @param callback        The callback that will be called when the presses the positive button.
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder itemsCallbackMultiChoice(Integer[] selectedIndices, @NonNull ListCallbackMulti callback) {
            this.selectedIndices = selectedIndices;
            this.listCallback = null;
            this.listCallbackSingle = null;
            this.listCallbackMulti = callback;
            return this;
        }

        /**
         * By default, the multi choice callback is only called when the user clicks the positive button
         * or if there are no buttons. Call this to force it to always call on item clicks even if the
         * positive button exists.
         *
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder alwaysCallMultiChoiceCallback() {
            this.alwaysCallMultiChoiceCallback = true;
            return this;
        }

        public Builder positiveText(@StringRes int postiveRes) {
            positiveText(this.context.getString(postiveRes));
            return this;
        }

        public Builder positiveText(@NonNull CharSequence message) {
            this.positiveText = message;
            return this;
        }

        public Builder neutralText(@StringRes int neutralRes) {
            return neutralText(this.context.getString(neutralRes));
        }

        public Builder neutralText(@NonNull CharSequence message) {
            this.neutralText = message;
            return this;
        }

        public Builder negativeText(@StringRes int negativeRes) {
            return negativeText(this.context.getString(negativeRes));
        }

        public Builder negativeText(@NonNull CharSequence message) {
            this.negativeText = message;
            return this;
        }

        public Builder listSelector(@DrawableRes int selectorRes) {
            this.listSelector = selectorRes;
            return this;
        }

        public Builder btnSelectorStacked(@DrawableRes int selectorRes) {
            this.btnSelectorStacked = selectorRes;
            return this;
        }

        public Builder btnSelector(@DrawableRes int selectorRes) {
            this.btnSelectorPositive = selectorRes;
            this.btnSelectorNeutral = selectorRes;
            this.btnSelectorNegative = selectorRes;
            return this;
        }

        public Builder btnSelector(@DrawableRes int selectorRes, @NonNull DialogAction which) {
            switch (which) {
                default:
                    this.btnSelectorPositive = selectorRes;
                    break;
                case NEUTRAL:
                    this.btnSelectorNeutral = selectorRes;
                    break;
                case NEGATIVE:
                    this.btnSelectorNegative = selectorRes;
                    break;
            }
            return this;
        }

        /**
         * Sets the gravity used for the text in stacked action buttons. By default, it's #{@link GravityEnum#END}.
         *
         * @param gravity The gravity to use.
         * @return The Builder instance so calls can be chained.
         */
        public Builder btnStackedGravity(@NonNull GravityEnum gravity) {
            this.btnStackedGravity = gravity;
            return this;
        }

        /**
         * Use {@link #customView(int, boolean)} instead.
         */
        @Deprecated
        public Builder customView(@LayoutRes int layoutRes) {
            return customView(layoutRes, true);
        }

        public Builder customView(@LayoutRes int layoutRes, boolean wrapInScrollView) {
            LayoutInflater li = LayoutInflater.from(this.context);
            return customView(li.inflate(layoutRes, null), wrapInScrollView);
        }

        /**
         * Use {@link #customView(android.view.View, boolean)} instead.
         */
        @Deprecated
        public Builder customView(@NonNull View view) {
            return customView(view, true);
        }

        public Builder customView(@NonNull View view, boolean wrapInScrollView) {
            this.customView = view;
            this.wrapCustomViewInScroll = wrapInScrollView;
            return this;
        }

        /**
         * Makes this dialog a progress dialog.
         *
         * @param indeterminate If true, an infinite circular spinner is shown. If false, a horizontal progress bar is shown that is incremented or set via the built MaterialDialog instance.
         * @param max           When indeterminate is false, the max value the horizontal progress bar can get to.
         * @return An instance of the Builder so calls can be chained.
         */
        public Builder progress(boolean indeterminate, int max) {
            if (indeterminate) {
                this.mIndeterminateProgress = true;
                this.mProgress = -2;
            } else {
                this.mIndeterminateProgress = false;
                this.mProgress = -1;
                this.mProgressMax = max;
            }
            return this;
        }

        public Builder positiveColor(int color) {
            this.positiveColor = color;
            return this;
        }

        public Builder positiveColorRes(@ColorRes int colorRes) {
            positiveColor(this.context.getResources().getColor(colorRes));
            return this;
        }

        public Builder positiveColorAttr(@AttrRes int colorAttr) {
            positiveColor(DialogUtils.resolveColor(this.context, colorAttr));
            return this;
        }

        public Builder negativeColor(int color) {
            this.negativeColor = color;
            return this;
        }

        public Builder negativeColorRes(@ColorRes int colorRes) {
            negativeColor(this.context.getResources().getColor(colorRes));
            return this;
        }

        public Builder negativeColorAttr(@AttrRes int colorAttr) {
            negativeColor(DialogUtils.resolveColor(this.context, colorAttr));
            return this;
        }

        public Builder neutralColor(int color) {
            this.neutralColor = color;
            return this;
        }

        public Builder neutralColorRes(@ColorRes int colorRes) {
            return neutralColor(this.context.getResources().getColor(colorRes));
        }

        public Builder neutralColorAttr(@AttrRes int colorAttr) {
            return neutralColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder dividerColor(int color) {
            this.dividerColor = color;
            return this;
        }

        public Builder dividerColorRes(@ColorRes int colorRes) {
            return dividerColor(this.context.getResources().getColor(colorRes));
        }

        public Builder dividerColorAttr(@AttrRes int colorAttr) {
            return dividerColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder backgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder backgroundColorRes(@ColorRes int colorRes) {
            return backgroundColor(this.context.getResources().getColor(colorRes));
        }

        public Builder backgroundColorAttr(@AttrRes int colorAttr) {
            return backgroundColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder itemColor(int color) {
            this.itemColor = color;
            this.itemColorSet = true;
            return this;
        }

        public Builder itemColorRes(@ColorRes int colorRes) {
            return itemColor(this.context.getResources().getColor(colorRes));
        }

        public Builder itemColorAttr(@AttrRes int colorAttr) {
            return itemColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder callback(@NonNull ButtonCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder theme(@NonNull Theme theme) {
            this.theme = theme;
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * This defaults to true. If set to false, the dialog will not automatically be dismissed
         * when an action button is pressed, and not automatically dismissed when the user selects
         * a list item.
         *
         * @param dismiss Whether or not to dismiss the dialog automatically.
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder autoDismiss(boolean dismiss) {
            this.autoDismiss = dismiss;
            return this;
        }

        /**
         * Sets a custom {@link android.widget.ListAdapter} for the dialog's list
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder adapter(@NonNull ListAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder limitIconToDefaultSize() {
            this.limitIconToDefaultSize = true;
            return this;
        }

        public Builder maxIconSize(int maxIconSize) {
            this.maxIconSize = maxIconSize;
            return this;
        }

        public Builder maxIconSizeRes(@DimenRes int maxIconSizeRes) {
            return maxIconSize((int) this.context.getResources().getDimension(maxIconSizeRes));
        }

        public Builder showListener(@NonNull OnShowListener listener) {
            this.showListener = listener;
            return this;
        }

        public Builder dismissListener(@NonNull OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder cancelListener(@NonNull OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        public Builder keyListener(@NonNull OnKeyListener listener) {
            this.keyListener = listener;
            return this;
        }

        public Builder forceStacking(boolean stacked) {
            this.forceStacking = stacked;
            return this;
        }

        public MaterialDialog build() {
            if ((content == null || content.toString().trim().length() == 0) &&
                    title != null && (items == null || items.length == 0) &&
                    customView == null && adapter == null) {
                this.content = this.title;
                this.title = null;
            }
            return new MaterialDialog(this);
        }

        public MaterialDialog show() {
            MaterialDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }

    @Override
    public void show() {
        if (Looper.myLooper() != Looper.getMainLooper())
            throw new IllegalStateException("Dialogs can only be shown from the UI thread.");
        try {
            super.show();
        } catch (WindowManager.BadTokenException e) {
            throw new DialogException("Bad window token, you cannot show a dialog before an Activity is created or after it's hidden.");
        }
    }

    private ColorStateList getActionTextStateList(int newPrimaryColor) {
        final int fallBackButtonColor = DialogUtils.resolveColor(getContext(), android.R.attr.textColorPrimary);
        if (newPrimaryColor == 0) newPrimaryColor = fallBackButtonColor;
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{} // enabled
        };
        int[] colors = new int[]{
                DialogUtils.adjustAlpha(newPrimaryColor, 0.4f),
                newPrimaryColor
        };
        return new ColorStateList(states, colors);
    }

    /**
     * Retrieves the view of an action button, allowing you to modify properties such as whether or not it's enabled.
     * Use {@link #setActionButton(DialogAction, int)} to change text, since the view returned here is not
     * the view that displays text.
     *
     * @param which The action button of which to get the view for.
     * @return The view from the dialog's layout representing this action button.
     */
    public final View getActionButton(@NonNull DialogAction which) {
        if (isStacked) {
            switch (which) {
                default:
                    return view.findViewById(R.id.buttonStackedPositive);
                case NEUTRAL:
                    return view.findViewById(R.id.buttonStackedNeutral);
                case NEGATIVE:
                    return view.findViewById(R.id.buttonStackedNegative);
            }
        } else {
            switch (which) {
                default:
                    return view.findViewById(R.id.buttonDefaultPositive);
                case NEUTRAL:
                    return view.findViewById(R.id.buttonDefaultNeutral);
                case NEGATIVE:
                    return view.findViewById(R.id.buttonDefaultNegative);
            }
        }
    }

    /**
     * This will not return buttons that are actually in the layout itself, since the layout doesn't
     * contain buttons. This is only implemented to avoid crashing issues on Huawei devices. Huawei's
     * stock OS requires this method in order to detect visible buttons.
     *
     * @deprecated Use getActionButton(com.afollestad.materialdialogs.DialogAction)} instead.
     */
    @Deprecated
    @Override
    public Button getButton(int whichButton) {
        Log.w("MaterialDialog", "Warning: getButton() is a deprecated method that does not return valid references to action buttons.");
        if (whichButton == AlertDialog.BUTTON_POSITIVE) {
            return mBuilder.positiveText != null ? new Button(getContext()) : null;
        } else if (whichButton == AlertDialog.BUTTON_NEUTRAL) {
            return mBuilder.neutralText != null ? new Button(getContext()) : null;
        } else {
            return mBuilder.negativeText != null ? new Button(getContext()) : null;
        }
    }

    /**
     * Retrieves the frame view containing the title and icon. You can manually change visibility and retrieve children.
     */
    public final View getTitleFrame() {
        return titleFrame;
    }

    /**
     * Retrieves the custom view that was inflated or set to the MaterialDialog during building.
     *
     * @return The custom view that was passed into the Builder.
     */
    public final View getCustomView() {
        return mBuilder.customView;
    }

    /**
     * Updates an action button's title, causing invalidation to check if the action buttons should be stacked.
     *
     * @param which The action button to update.
     * @param title The new title of the action button.
     */
    public final void setActionButton(@NonNull DialogAction which, CharSequence title) {
        switch (which) {
            default:
                mBuilder.positiveText = title;
                break;
            case NEUTRAL:
                mBuilder.neutralText = title;
                break;
            case NEGATIVE:
                mBuilder.negativeText = title;
                break;
        }
        invalidateActions();
    }

    /**
     * Updates an action button's title, causing invalidation to check if the action buttons should be stacked.
     *
     * @param which    The action button to update.
     * @param titleRes The string resource of the new title of the action button.
     */
    public final void setActionButton(DialogAction which, @StringRes int titleRes) {
        setActionButton(which, getContext().getString(titleRes));
    }

    /**
     * Gets whether or not the positive, neutral, or negative action button is visible.
     *
     * @return Whether or not 1 or more action buttons is visible.
     */
    public final boolean hasActionButtons() {
        return numberOfActionButtons() > 0;
    }

    /**
     * Gets the number of visible action buttons.
     *
     * @return 0 through 3, depending on how many should be or are visible.
     */
    public final int numberOfActionButtons() {
        int number = 0;
        if (mBuilder.positiveText != null) number++;
        if (mBuilder.neutralText != null) number++;
        if (mBuilder.negativeText != null) number++;
        return number;
    }

    /**
     * Updates the dialog's title.
     */
    public final void setTitle(@NonNull CharSequence title) {
        this.title.setText(title);
    }

    @Override
    public void setIcon(@DrawableRes int resId) {
        icon.setImageResource(resId);
        icon.setVisibility(resId != 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setIcon(Drawable d) {
        icon.setImageDrawable(d);
        icon.setVisibility(d != null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setIconAttribute(@AttrRes int attrId) {
        Drawable d = DialogUtils.resolveDrawable(mBuilder.context, attrId);
        icon.setImageDrawable(d);
        icon.setVisibility(d != null ? View.VISIBLE : View.GONE);
    }

    public final void setContent(CharSequence content) {
        this.content.setText(content);
        invalidateCustomViewAssociations(); // invalidates padding in content area scroll (if needed)
    }

    public final void setItems(CharSequence[] items) {
        if (mBuilder.adapter == null)
            throw new IllegalStateException("This MaterialDialog instance does not yet have an adapter set to it. You cannot use setItems().");
        if (mBuilder.adapter instanceof MaterialDialogAdapter) {
            mBuilder.adapter = new MaterialDialogAdapter(mBuilder.context,
                    ListType.getLayoutForType(listType), R.id.title, items);
        } else {
            throw new IllegalStateException("When using a custom adapter, setItems() cannot be used. Set items through the adapter instead.");
        }
        mBuilder.items = items;
        listView.setAdapter(mBuilder.adapter);
        invalidateCustomViewAssociations();
    }

    public final int getCurrentProgress() {
        if (mProgress == null) return -1;
        return mProgress.getProgress();
    }

    public final void incrementProgress(int by) {
        if (mBuilder.mProgress <= -2)
            throw new IllegalStateException("Cannot use incrementProgress() on this dialog.");
        setProgress(getCurrentProgress() + by);
    }

    public final void setProgress(int progress) {
        if (Looper.myLooper() != Looper.getMainLooper())
            throw new IllegalStateException("You can only set the dialog's progress from the UI thread.");
        else if (mBuilder.mProgress <= -2)
            throw new IllegalStateException("Cannot use setProgress() on this dialog.");
        mProgress.setProgress(progress);
        int percentage = (int) (((float) getCurrentProgress() / (float) getMaxProgress()) * 100f);
        mProgressLabel.setText(percentage + "%");
    }

    public final void setMaxProgress(int max) {
        if (Looper.myLooper() != Looper.getMainLooper())
            throw new IllegalStateException("You can only set the dialog's progress from the UI thread.");
        else if (mBuilder.mProgress <= -2)
            throw new IllegalStateException("Cannot use setMaxProgress() on this dialog.");
        mProgress.setMax(max);
    }

    public final boolean isIndeterminateProgress() {
        return mBuilder.mIndeterminateProgress;
    }

    public final int getMaxProgress() {
        if (mProgress == null) return -1;
        return mProgress.getMax();
    }

    public final boolean isCancelled() {
        return !isShowing();
    }

    /**
     * Use this to customize any list-specific logic for this dialog (OnItemClickListener, OnLongItemClickListener, etc.)
     *
     * @return The ListView instance used by this dialog, or null if not using a list.
     */
    @Nullable
    public ListView getListView() {
        return listView;
    }

    /**
     * Convenience method for getting the currently selected index of a single choice list.
     *
     * @return Currently selected index of a single choice list, or -1 if not showing a single choice list
     */
    public int getSelectedIndex() {
        if (mBuilder.listCallbackSingle != null) {
            return mBuilder.selectedIndex;
        } else {
            return -1;
        }
    }

    /**
     * Convenience method for getting the currently selected indices of a multi choice list
     *
     * @return Currently selected index of a multi choice list, or null if not showing a multi choice list
     */
    @Nullable
    public Integer[] getSelectedIndices() {
        if (mBuilder.listCallbackMulti != null) {
            return selectedIndicesList.toArray(new Integer[selectedIndicesList.size()]);
        } else {
            return null;
        }
    }

    /**
     * Convenience method for setting the currently selected index of a single choice list.
     * This only works if you are not using a custom adapter; if you're using a custom adapter,
     * an IllegalStateException is thrown. Note that this does not call the respective single choice callback.
     *
     * @param index The index of the list item to check.
     */
    public void setSelectedIndex(int index) {
        mBuilder.selectedIndex = index;
        if (mBuilder.adapter != null && mBuilder.adapter instanceof MaterialDialogAdapter) {
            ((MaterialDialogAdapter) mBuilder.adapter).notifyDataSetChanged();
        } else {
            throw new IllegalStateException("You can only use setSelectedIndex() with the default adapter implementation.");
        }
    }

    /**
     * Convenience method for setting the currently selected indices of a multi choice list.
     * This only works if you are not using a custom adapter; if you're using a custom adapter,
     * an IllegalStateException is thrown. Note that this does not call the respective multi choice callback.
     *
     * @param indices The indices of the list items to check.
     */
    public void setSelectedIndices(@NonNull Integer[] indices) {
        mBuilder.selectedIndices = indices;
        selectedIndicesList = new ArrayList<>(Arrays.asList(indices));
        if (mBuilder.adapter != null && mBuilder.adapter instanceof MaterialDialogAdapter) {
            ((MaterialDialogAdapter) mBuilder.adapter).notifyDataSetChanged();
        } else {
            throw new IllegalStateException("You can only use setSelectedIndices() with the default adapter implementation.");
        }
    }

    private class MaterialDialogAdapter extends ArrayAdapter<CharSequence> {

        final int itemColor;

        public MaterialDialogAdapter(Context context, int resource, int textViewResourceId, CharSequence[] objects) {
            super(context, resource, textViewResourceId, objects);
            itemColor = DialogUtils.resolveColor(getContext(), R.attr.md_item_color, defaultItemColor);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("WrongViewCast")
        @Override
        public View getView(final int index, View convertView, ViewGroup parent) {
            final View view = super.getView(index, convertView, parent);
            TextView tv = (TextView) view.findViewById(R.id.title);
            switch (listType) {
                case SINGLE: {
                    @SuppressLint("CutPasteId")
                    RadioButton radio = (RadioButton) view.findViewById(R.id.control);
                    radio.setChecked(mBuilder.selectedIndex == index);
                    break;
                }
                case MULTI: {
                    @SuppressLint("CutPasteId")
                    CheckBox checkbox = (CheckBox) view.findViewById(R.id.control);
                    checkbox.setChecked(selectedIndicesList.contains(index));
                    break;
                }
            }
            tv.setText(mBuilder.items[index]);
            tv.setTextColor(itemColor);
            setTypeface(tv, mBuilder.regularFont);
            view.setTag(index + ":" + mBuilder.items[index]);
            return view;
        }
    }

    private static enum ListType {
        REGULAR, SINGLE, MULTI;

        public static int getLayoutForType(ListType type) {
            switch (type) {
                case REGULAR:
                    return R.layout.md_listitem;
                case SINGLE:
                    return R.layout.md_listitem_singlechoice;
                case MULTI:
                    return R.layout.md_listitem_multichoice;
                default:
                    throw new IllegalArgumentException("Not a valid list type");
            }
        }
    }

    public static interface ListCallback {
        void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text);
    }

    public static interface ListCallbackMulti {
        void onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text);
    }

    /**
     * Override these as needed, so no needing to sub empty methods from an interface
     */
    public static abstract class ButtonCallback {

        public void onPositive(MaterialDialog dialog) {
        }

        public void onNegative(MaterialDialog dialog) {
        }

        public void onNeutral(MaterialDialog dialog) {
        }

        public ButtonCallback() {
            super();
        }

        @Override
        protected final Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public final boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        protected final void finalize() throws Throwable {
            super.finalize();
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public final String toString() {
            return super.toString();
        }
    }
}