package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.base.DialogBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialDialog extends DialogBase implements View.OnClickListener
{

    private ImageView icon;
    private TextView title;
    private View titleFrame;

    private int contentColor;
    private Context mContext;
    private CharSequence positiveText;
    private TextView positiveButton;
    private CharSequence neutralText;
    private TextView neutralButton;
    private CharSequence negativeText;
    private TextView negativeButton;
    private LinearLayout view;
    private ListView listView;
    private int positiveColor;
    private int negativeColor;
    private int neutralColor;
    private SimpleCallback callback;
    private ListCallback listCallback;
    private ListCallback listCallbackSingle;
    private ListCallbackMulti listCallbackMulti;
    private View customView;
    private FrameLayout customViewFrame;
    private CharSequence[] items;
    private boolean isStacked;
    private int selectedIndex;
    private Integer[] selectedIndices;
    private Typeface mediumFont;
    private Typeface regularFont;
    private boolean autoDismiss;
    private ListAdapter adapter;
    private ListType listType;
    private List<Integer> selectedIndicesList;
    private boolean forceStacking;

    protected static ContextThemeWrapper getTheme(Builder builder) {
        TypedArray a = builder.context.getTheme().obtainStyledAttributes(new int[]{R.attr.md_dark_theme});
        boolean darkTheme = builder.theme == Theme.DARK;
        if (!darkTheme) {
            try {
                darkTheme = a.getBoolean(0, false);
            } finally {
                a.recycle();
            }
        }
        return new ContextThemeWrapper(builder.context, darkTheme ? R.style.MD_Dark : R.style.MD_Light);
    }

    protected MaterialDialog(Builder builder) {
        super(getTheme(builder));

        this.regularFont = builder.regularFont;
        if (this.regularFont == null)
            this.regularFont = TypefaceHelper.get(getContext(), "Roboto-Regular");
        this.mediumFont = builder.mediumFont;
        if (this.mediumFont == null)
            this.mediumFont = TypefaceHelper.get(getContext(), "Roboto-Medium");

        mContext = builder.context;
        this.view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.md_dialog, null);
        this.customView = builder.customView;
        this.callback = builder.callback;
        this.listCallback = builder.listCallback;
        this.listCallbackSingle = builder.listCallbackSingle;
        this.listCallbackMulti = builder.listCallbackMulti;
        this.positiveText = builder.positiveText;
        this.neutralText = builder.neutralText;
        this.negativeText = builder.negativeText;
        this.items = builder.items;
        this.setCancelable(builder.cancelable);
        this.selectedIndex = builder.selectedIndex;
        this.selectedIndices = builder.selectedIndices;
        this.autoDismiss = builder.autoDismiss;
        this.adapter = builder.adapter;

        this.positiveColor = builder.positiveColor;
        this.negativeColor = builder.negativeColor;
        this.neutralColor = builder.neutralColor;

        final int mdAccentColor = DialogUtils.resolveColor(mContext, R.attr.md_accent_color);
        if (mdAccentColor != 0) {
            this.positiveColor = mdAccentColor;
            this.negativeColor = mdAccentColor;
            this.neutralColor = mdAccentColor;
        }

        title = (TextView) view.findViewById(R.id.title);
        icon = (ImageView) view.findViewById(R.id.icon);
        titleFrame = view.findViewById(R.id.titleFrame);

        if (builder.title == null || builder.title.toString().trim().length() == 0) {
            titleFrame.setVisibility(View.GONE);
        } else {
            title.setText(builder.title);
            setTypeface(title, mediumFont);
            if (builder.titleColor != -1) {
                title.setTextColor(builder.titleColor);
            } else {
                final int fallback = DialogUtils.resolveColor(getContext(), android.R.attr.textColorPrimary);
                title.setTextColor(DialogUtils.resolveColor(getContext(), R.attr.md_title_color, fallback));
            }
            if (builder.titleAlignment == Alignment.CENTER) {
                title.setGravity(Gravity.CENTER_HORIZONTAL);
            } else if (builder.titleAlignment == Alignment.RIGHT) {
                title.setGravity(Gravity.RIGHT);
            }
        }

        final TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(builder.content);
        content.setMovementMethod(new LinkMovementMethod());
        setTypeface(content, regularFont);
        content.setLineSpacing(0f, builder.contentLineSpacingMultiplier);
        if (this.positiveColor == 0) {
            content.setLinkTextColor(DialogUtils.resolveColor(getContext(), android.R.attr.textColorPrimary));
        } else {
            content.setLinkTextColor(this.positiveColor);
        }
        if (builder.contentAlignment == Alignment.CENTER) {
            content.setGravity(Gravity.CENTER_HORIZONTAL);
        } else if (builder.contentAlignment == Alignment.RIGHT) {
            content.setGravity(Gravity.RIGHT);
        }

        if (builder.contentColor != -1) {
            this.contentColor = builder.contentColor;
            content.setTextColor(this.contentColor);
        } else {
            final int fallback = DialogUtils.resolveColor(getContext(), android.R.attr.textColorSecondary);
            this.contentColor = DialogUtils.resolveColor(getContext(), R.attr.md_content_color, fallback);
            content.setTextColor(contentColor);
        }

        if (customView != null) {
            invalidateCustomViewAssociations();
            FrameLayout frame = (FrameLayout) view.findViewById(R.id.customViewFrame);
            customViewFrame = frame;
            View innerView = customView;

            if (builder.customViewWrapInScrollView) {
                /* Apply the frame padding to the content, this allows the ScrollView to draw it's
                   overscroll glow without clipping */
                Resources r = getContext().getResources();
                int frameMargin = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
                innerView.setPadding(frameMargin, 0, frameMargin, 0);

                ScrollView sv = new ScrollView(getContext());
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

                sv.setPadding(0, paddingTop, 0, paddingBottom);
                sv.setClipToPadding(false);
                sv.addView(innerView,
                        new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

                innerView = sv;
            }

            frame.addView(innerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            invalidateCustomViewAssociations();
        }

        boolean adapterProvided = adapter != null;
        if (items != null && items.length > 0 || adapterProvided) {
            listView = (ListView) view.findViewById(R.id.contentListView);
            listView.setSelector(DialogUtils.resolveDrawable(getContext(), R.attr.md_selector));

            if (!adapterProvided) {
                // Determine list type
                if (listCallbackSingle != null) {
                    listType = ListType.SINGLE;
                } else if (listCallbackMulti != null) {
                    listType = ListType.MULTI;
                    if (selectedIndices != null) {
                        selectedIndicesList = new ArrayList<>(Arrays.asList(selectedIndices));
                    } else {
                        selectedIndicesList = new ArrayList<>();
                    }
                } else {
                    listType = ListType.REGULAR;
                }
                adapter = new MaterialDialogAdapter(mContext, ListType.getLayoutForType(listType), R.id.title, items);
            }
        }

        if (builder.icon != null) {
            icon.setVisibility(View.VISIBLE);
            icon.setImageDrawable(builder.icon);
        } else {
            Drawable d = DialogUtils.resolveDrawable(mContext, R.attr.md_icon);
            if (d != null) {
                icon.setVisibility(View.VISIBLE);
                icon.setImageDrawable(d);
            } else {
                icon.setVisibility(View.GONE);
            }
        }

        updateFramePadding();
        invalidateActions();
        setOnShowListenerInternal();
        setViewInternal(view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (view.getMeasuredWidth() > 0) {
                        invalidateCustomViewAssociations();
                    }
                }
            });

        if (builder.theme == Theme.LIGHT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            setInverseBackgroundForced(true);
            title.setTextColor(Color.BLACK);
            content.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        super.onShow(dialog); // calls any external show listeners
        checkIfStackingNeeded();
    }

    /**
     * To account for scrolling content and overscroll glows, the frame padding/margins sometimes
     * must be set on inner views. This is dependent on the visibility of the title bar and action
     * buttons. This method determines where the padding or margins are needed and applies them.
     */
    private void updateFramePadding() {
        Resources r = getContext().getResources();
        int frameMargin = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);

        View contentScrollView = view.findViewById(R.id.contentScrollView);
        int paddingTop = contentScrollView.getPaddingTop();
        int paddingBottom = contentScrollView.getPaddingBottom();

        if (!hasActionButtons())
            paddingBottom = frameMargin;
        if (titleFrame.getVisibility() == View.GONE)
            paddingTop = frameMargin;

        contentScrollView.setPadding(contentScrollView.getPaddingLeft(), paddingTop,
                contentScrollView.getPaddingRight(), paddingBottom);

        if (listView != null) {
            final int dialogFramePadding = (int) mContext.getResources().getDimension(R.dimen.md_dialog_frame_margin);
            paddingTop = titleFrame.getVisibility() != View.GONE ? listView.getPaddingTop() :
                    dialogFramePadding;
            paddingBottom = hasActionButtons() ? listView.getPaddingBottom() :
                    dialogFramePadding;
            listView.setPadding(listView.getPaddingLeft(), paddingTop,
                    listView.getPaddingRight(), paddingBottom);
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
        if (customView != null) {
            contentScrollView.setVisibility(View.GONE);
            customViewFrame.setVisibility(View.VISIBLE);
            boolean topScroll = canViewOrChildScroll(customViewFrame.getChildAt(0), false);
            boolean bottomScroll = canViewOrChildScroll(customViewFrame.getChildAt(0), true);
            setDividerVisibility(topScroll, bottomScroll);
        } else if ((items != null && items.length > 0) || adapter != null) {
            contentScrollView.setVisibility(View.GONE);
            boolean canScroll = canListViewScroll();
            setDividerVisibility(canScroll, canScroll);
        } else {
            contentScrollView.setVisibility(View.VISIBLE);
            boolean canScroll = canContentScroll();
            setDividerVisibility(canScroll, canScroll);
        }
    }


    /**
     * Set the visibility of the bottom divider and adjusts the layout margin,
     * when the divider is visible the button bar bottom margin (8dp from
     *  http://www.google.com/design/spec/components/dialogs.html#dialogs-specs )
     * is removed as it makes the button bar look off balanced with different amounts of padding
     * above and below the divider.
     * @param visible if the divider should be visible
     */
    private void setDividerVisibility(boolean topVisible, boolean bottomVisible) {
        View titleBarDivider = view.findViewById(R.id.titleBarDivider);
        if (topVisible) {
            titleBarDivider.setVisibility(View.VISIBLE);
            titleBarDivider.setBackgroundColor(DialogUtils.resolveColor(getContext(), R.attr.md_divider));
        } else {
            titleBarDivider.setVisibility(View.GONE);
        }

        View buttonBarDivider = view.findViewById(R.id.buttonBarDivider);
        if (bottomVisible) {
            buttonBarDivider.setVisibility(View.VISIBLE);
            buttonBarDivider.setBackgroundColor(DialogUtils.resolveColor(getContext(), R.attr.md_divider));
            setVerticalMargins(view.findViewById(R.id.buttonStackedFrame), 0, 0);
            setVerticalMargins(view.findViewById(R.id.buttonDefaultFrame), 0, 0);
        } else {
            Resources r = getContext().getResources();
            buttonBarDivider.setVisibility(View.GONE);
            final int bottomMargin = r.getDimensionPixelSize(R.dimen.md_button_frame_vertical_padding);
            setVerticalMargins(view.findViewById(R.id.buttonStackedFrame), bottomMargin, bottomMargin);
            setVerticalMargins(view.findViewById(R.id.buttonDefaultFrame), bottomMargin, bottomMargin);
        }
    }

    /**
     * Constructs the dialog's list content and sets up click listeners.
     */
    private void invalidateList() {
        if ((items == null || items.length == 0) && adapter == null) return;

        // Hide content
        view.findViewById(R.id.contentScrollView).setVisibility(View.GONE);
        view.findViewById(R.id.customViewFrame).setVisibility(View.GONE);

        // Set up list with adapter
        FrameLayout listViewContainer = (FrameLayout) view.findViewById(R.id.contentListViewFrame);
        listViewContainer.setVisibility(View.VISIBLE);
        listView.setAdapter(adapter);

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
                        if (selectedIndex != position) {
                            selectedIndex = position;
                            ((MaterialDialogAdapter) adapter).notifyDataSetChanged();
                        }
                    }

                    onClick(view);
                }
            });
        }

    }

    private int calculateMaxButtonWidth() {
        /**
         * Max button width = (DialogWidth - Side margins) / [Number of buttons]
         * From: http://www.google.com/design/spec/components/dialogs.html#dialogs-specs
         */
        final int dialogWidth = getWindow().getDecorView().getMeasuredWidth();
        final int margins = (int) getContext().getResources().getDimension(R.dimen.md_button_padding_frame_side);
        return (dialogWidth - 2 * margins) / numberOfActionButtons();
    }

    /**
     * Find the view touching the bottom of this ViewGroup. Non visible children are ignored,
     * however getChildDrawingOrder is not taking into account for simplicity and because it behaves
     * inconsistently across platform versions.
     * @param viewGroup
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
        if (!(view instanceof ViewGroup)) {
            return false;
        }
        if (view != null) {
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
//          } TODO else if RecyclerView {
            } else if (view instanceof ViewGroup) {
                if (atBottom) {
                    return canViewOrChildScroll(getBottomView((ViewGroup) view), true);
                } else {
                    return canViewOrChildScroll(getTopView((ViewGroup) view), false);
                }
            }
        }
        return false;
    }


    private static boolean canWebViewScroll(WebView view) {
        return view.getMeasuredHeight() > view.getContentHeight();
    }

    private static boolean canAdapterViewScroll(AdapterView lv) {
        /* Force it to layout it's children */
        if (lv.getLastVisiblePosition() == -1)
            return false;
        /* We scroll if the last item is not visible */
        boolean lastItemVisible = lv.getLastVisiblePosition() == lv.getCount() - 1;

        if (lastItemVisible) {
            /* or the last item's bottom is beyond our own bottom */
            return lv.getChildAt(lv.getChildCount() - 1).getBottom() >
                    lv.getHeight() - lv.getPaddingBottom();
        }
        return true;
    }

    private boolean canListViewScroll() {
        return canAdapterViewScroll(listView);
    }


    /**
     * Detects whether or not the content TextView can be scrolled.
     */
    private boolean canContentScroll() {
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.contentScrollView);
        final int childHeight = view.findViewById(R.id.content).getMeasuredHeight();
        return scrollView.getMeasuredHeight() < childHeight;
    }

    /**
     * Measures the action button's and their text to decide whether or not the button should be stacked.
     */
    private void checkIfStackingNeeded() {
        if (numberOfActionButtons() <= 1) {
            return;
        } else if (forceStacking) {
            isStacked = true;
            invalidateActions();
            return;
        }

        final int maxWidth = calculateMaxButtonWidth();
        isStacked = false;

        if (this.positiveText != null) {
            final int positiveWidth = positiveButton.getWidth();
            isStacked = positiveWidth > maxWidth;
        }

        if (!isStacked && this.neutralText != null) {
            final int neutralWidth = neutralButton.getWidth();
            isStacked = neutralWidth > maxWidth;
        }

        if (!isStacked && this.negativeText != null) {
            final int negativeWidth = negativeButton.getWidth();
            isStacked = negativeWidth > maxWidth;
        }

        invalidateActions();
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

        positiveButton = (TextView) view.findViewById(
                isStacked ? R.id.buttonStackedPositive : R.id.buttonDefaultPositive);
        if (this.positiveText != null) {
            setTypeface(positiveButton, mediumFont);
            positiveButton.setText(this.positiveText);
            positiveButton.setTextColor(getActionTextStateList(this.positiveColor));
            setBackgroundCompat(positiveButton, DialogUtils.resolveDrawable(getContext(), isStacked ? R.attr.md_selector : R.attr.md_btn_selector));
            positiveButton.setTag(POSITIVE);
            positiveButton.setOnClickListener(this);
        } else {
            positiveButton.setVisibility(View.GONE);
        }

        neutralButton = (TextView) view.findViewById(
                isStacked ? R.id.buttonStackedNeutral : R.id.buttonDefaultNeutral);
        if (this.neutralText != null) {
            setTypeface(neutralButton, mediumFont);
            neutralButton.setVisibility(View.VISIBLE);
            neutralButton.setTextColor(getActionTextStateList(this.neutralColor));
            setBackgroundCompat(neutralButton, DialogUtils.resolveDrawable(getContext(), isStacked ? R.attr.md_selector : R.attr.md_btn_selector));
            neutralButton.setText(this.neutralText);
            neutralButton.setTag(NEUTRAL);
            neutralButton.setOnClickListener(this);
        } else {
            neutralButton.setVisibility(View.GONE);
        }

        negativeButton = (TextView) view.findViewById(
                isStacked ? R.id.buttonStackedNegative : R.id.buttonDefaultNegative);
        if (this.negativeText != null) {
            setTypeface(negativeButton, mediumFont);
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setTextColor(getActionTextStateList(this.negativeColor));
            setBackgroundCompat(negativeButton, DialogUtils.resolveDrawable(getContext(), isStacked ? R.attr.md_selector : R.attr.md_btn_selector));
            negativeButton.setText(this.negativeText);
            negativeButton.setTag(NEGATIVE);
            negativeButton.setOnClickListener(this);

            if (!isStacked) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        (int) getContext().getResources().getDimension(R.dimen.md_button_height));
                if (this.positiveText != null) {
                    params.addRule(RelativeLayout.LEFT_OF, R.id.buttonDefaultPositive);
                } else {
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                }
                negativeButton.setLayoutParams(params);
            }
        } else {
            negativeButton.setVisibility(View.GONE);
        }

        invalidateList();
        return true;
    }

    private void sendSingleChoiceCallback(View v) {
        CharSequence text = null;
        if (selectedIndex >= 0) {
            text = items[selectedIndex];
        }
        listCallbackSingle.onSelection(this, v, selectedIndex, text);
    }

    private void sendMultichoiceCallback() {
        List<CharSequence> selectedTitles = new ArrayList<CharSequence>();
        for (Integer i : selectedIndicesList) {
            selectedTitles.add(items[i]);
        }
        listCallbackMulti.onSelection(this,
                selectedIndicesList.toArray(new Integer[selectedIndicesList.size()]),
                selectedTitles.toArray(new CharSequence[selectedTitles.size()]));
    }

    @Override
    public final void onClick(View v) {
        String tag = (String) v.getTag();
        switch (tag) {
            case POSITIVE:
                if (callback != null) {
                    callback.onPositive(this);
                }
                if (autoDismiss) dismiss();
                break;
            case NEGATIVE:
                if (callback != null && callback instanceof Callback) {
                    ((Callback) callback).onNegative(this);
                }
                if (autoDismiss) dismiss();
                break;
            case NEUTRAL:
                if (callback != null && callback instanceof FullCallback) {
                    ((FullCallback) callback).onNeutral(this);
                }
                if (autoDismiss) dismiss();
                break;
            default:
                String[] split = tag.split(":");
                int index = Integer.parseInt(split[0]);
                if (listCallback != null) {
                    if (autoDismiss) dismiss();
                    listCallback.onSelection(this, v, index, split[1]);
                } else if (listCallbackSingle != null) {
                    RadioButton cb = (RadioButton) ((LinearLayout) v).getChildAt(0);
                    if (!cb.isChecked())
                        cb.setChecked(true);
                    if (autoDismiss) dismiss();
                    sendSingleChoiceCallback(v);
                } else if (listCallbackMulti != null) {
                    CheckBox cb = (CheckBox) ((LinearLayout) v).getChildAt(0);
                    cb.setChecked(!cb.isChecked());
                    sendMultichoiceCallback();
                } else if (autoDismiss) dismiss();
                break;
        }
    }

    /**
     * The class used to construct a MaterialDialog.
     */
    public static class Builder {

        protected Context context;
        protected CharSequence title;
        protected Alignment titleAlignment = Alignment.LEFT;
        protected Alignment contentAlignment = Alignment.LEFT;
        protected int titleColor = -1;
        protected int contentColor = -1;
        protected CharSequence content;
        protected CharSequence[] items;
        protected CharSequence positiveText;
        protected CharSequence neutralText;
        protected CharSequence negativeText;
        protected View customView;
        protected boolean customViewWrapInScrollView = true;
        protected int positiveColor;
        protected int negativeColor;
        protected int neutralColor;
        protected SimpleCallback callback;
        protected ListCallback listCallback;
        protected ListCallback listCallbackSingle;
        protected ListCallbackMulti listCallbackMulti;
        protected Theme theme = Theme.LIGHT;
        protected boolean cancelable = true;
        protected float contentLineSpacingMultiplier = 1.3f;
        protected int selectedIndex = -1;
        protected Integer[] selectedIndices = null;
        protected boolean autoDismiss = true;
        protected Typeface regularFont;
        protected Typeface mediumFont;
        protected Drawable icon;
        protected ListAdapter adapter;
        private OnDismissListener dismissListener;
        private OnCancelListener cancelListener;
        private OnShowListener showListener;
        protected boolean forceStacking;

        public Builder(@NonNull Context context) {
            this.context = context;
            final int materialBlue = context.getResources().getColor(R.color.md_material_blue_500);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorAccent});
                try {
                    this.positiveColor = a.getColor(0, materialBlue);
                    this.negativeColor = a.getColor(0, materialBlue);
                    this.neutralColor = a.getColor(0, materialBlue);
                } catch (Exception e) {
                    this.positiveColor = materialBlue;
                    this.negativeColor = materialBlue;
                    this.neutralColor = materialBlue;
                } finally {
                    a.recycle();
                }
            } else {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent});
                try {
                    this.positiveColor = a.getColor(0, materialBlue);
                    this.negativeColor = a.getColor(0, materialBlue);
                    this.neutralColor = a.getColor(0, materialBlue);
                } catch (Exception e) {
                    this.positiveColor = materialBlue;
                    this.negativeColor = materialBlue;
                    this.neutralColor = materialBlue;
                } finally {
                    a.recycle();
                }
            }
        }

        public Builder title(@StringRes int titleRes) {
            title(this.context.getString(titleRes));
            return this;
        }

        public Builder title(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder titleAlignment(Alignment align) {
            this.titleAlignment = align;
            return this;
        }

        public Builder titleColorRes(@ColorRes int colorRes) {
            titleColor(this.context.getResources().getColor(colorRes));
            return this;
        }

        /**
         * Sets the fonts used in the dialog.
         *
         * @param medium  The font used on titles and action buttons. Null uses the default.
         * @param regular The font used everywhere else, like on the content and list items. Null uses the default.
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder typeface(Typeface medium, Typeface regular) {
            this.mediumFont = medium;
            this.regularFont = regular;
            return this;
        }

        public Builder titleColor(int color) {
            this.titleColor = color;
            return this;
        }

        public Builder icon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder icon(@DrawableRes int icon) {
            this.icon = context.getResources().getDrawable(icon);
            return this;
        }

        public Builder iconAttr(int iconAttr) {
            this.icon = DialogUtils.resolveDrawable(context, iconAttr);
            return this;
        }

        public Builder contentColor(int color) {
            this.contentColor = color;
            return this;
        }

        public Builder contentColorRes(@ColorRes int colorRes) {
            contentColor(this.context.getResources().getColor(colorRes));
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

        public Builder contentAlignment(Alignment align) {
            this.contentAlignment = align;
            return this;
        }

        public Builder contentLineSpacing(float multiplier) {
            this.contentLineSpacingMultiplier = multiplier;
            return this;
        }

        public Builder items(@ArrayRes int itemsRes) {
            items(this.context.getResources().getStringArray(itemsRes));
            return this;
        }

        public Builder items(CharSequence[] items) {
            this.items = items;
            return this;
        }

        public Builder itemsCallback(ListCallback callback) {
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
        public Builder itemsCallbackSingleChoice(int selectedIndex, ListCallback callback) {
            this.selectedIndex = selectedIndex;
            this.listCallback = null;
            this.listCallbackSingle = callback;
            this.listCallbackMulti = null;
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
        public Builder itemsCallbackMultiChoice(Integer[] selectedIndices, ListCallbackMulti callback) {
            this.selectedIndices = selectedIndices;
            this.listCallback = null;
            this.listCallbackSingle = null;
            this.listCallbackMulti = callback;
            return this;
        }

        public Builder positiveText(@StringRes int postiveRes) {
            positiveText(this.context.getString(postiveRes));
            return this;
        }

        public Builder positiveText(CharSequence message) {
            this.positiveText = message;
            return this;
        }

        public Builder neutralText(@StringRes int neutralRes) {
            neutralText(this.context.getString(neutralRes));
            return this;
        }

        public Builder neutralText(CharSequence message) {
            this.neutralText = message;
            return this;
        }

        public Builder negativeText(@StringRes int negativeRes) {
            negativeText(this.context.getString(negativeRes));
            return this;
        }

        public Builder negativeText(CharSequence message) {
            this.negativeText = message;
            return this;
        }

        public Builder customView(View view) {
            this.customView = view;
            return this;
        }

        /**
         * Like customView(View) however this view will NOT be wrapped by a scrollview or have the
         * default padding applied to it. Instead the view itself must handle any scrolling and
         * padding itself.
         *
         * Recommend padding is:
         * Top
         *  @dimen/md_content_vertical_padding if the title bar is shown otherwise
         *  @dimen/md_dialog_frame_margin
         *
         * Bottom
         *  @dimen/md_content_vertical_padding if the button bar is shown otherwise
         *  @dimen/md_dialog_frame_margin
         *
         *  Left and right should always be @dimen/md_dialog_frame_margin
         *
         *  If using scrolling content, the ScrollView or equivalent should go all the way to the
         *  edges so the overscroll glow touches the left and right sides, and the content neatly
         *  disappears behind the dividers from the title/button bars or the window edge
         *
         *  @see #customView(View)
         */
        public Builder customViewRaw(View view) {
            this.customView = view;
            this.customViewWrapInScrollView = false;
            return this;
        }

        public Builder customView(@LayoutRes int layoutRes) {
            return customView(LayoutInflater.from(this.context).inflate(layoutRes, null));
        }

        /**
         * @see #customViewRaw(View)
         */
        public Builder customViewRaw(@LayoutRes int layoutRes) {
            return customViewRaw(LayoutInflater.from(this.context).inflate(layoutRes, null));
        }



        public Builder positiveColorRes(@ColorRes int colorRes) {
            positiveColor(this.context.getResources().getColor(colorRes));
            return this;
        }

        public Builder positiveColor(int color) {
            this.positiveColor = color;
            return this;
        }

        public Builder negativeColorRes(@ColorRes int colorRes) {
            negativeColor(this.context.getResources().getColor(colorRes));
            return this;
        }

        public Builder negativeColor(int color) {
            this.negativeColor = color;
            return this;
        }

        public Builder neutralColorRes(@ColorRes int colorRes) {
            neutralColor(this.context.getResources().getColor(colorRes));
            return this;
        }

        public Builder neutralColor(int color) {
            this.neutralColor = color;
            return this;
        }

        public Builder callback(SimpleCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder theme(Theme theme) {
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
        public Builder adapter(ListAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder showListener(OnShowListener listener) {
            this.showListener = listener;
            return this;
        }

        public Builder dismissListener(OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder cancelListener(OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        public Builder forceStacking(boolean stacked) {
            this.forceStacking = stacked;
            return this;
        }

        public MaterialDialog build() {
            MaterialDialog dialog = new MaterialDialog(this);
            if (this.showListener != null) {
                dialog.setOnShowListener(this.showListener);
            }
            if (this.cancelListener != null) {
                dialog.setOnCancelListener(this.cancelListener);
            }
            if (this.dismissListener != null) {
                dialog.setOnDismissListener(this.dismissListener);
            }
            return dialog;
        }

        public MaterialDialog show() {
            MaterialDialog dialog = build();
            dialog.show();
            return dialog;
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
     *
     * @param which The action button of which to get the view for.
     * @return The view from the dialog's layout representing this action button.
     */
    public final Button getActionButton(DialogAction which) {
        if (view == null) return null;
        if (isStacked) {
            switch (which) {
                default:
                    return (Button) view.findViewById(R.id.buttonStackedPositive);
                case NEUTRAL:
                    return (Button) view.findViewById(R.id.buttonStackedNeutral);
                case NEGATIVE:
                    return (Button) view.findViewById(R.id.buttonStackedNegative);
            }
        } else {
            switch (which) {
                default:
                    return (Button) view.findViewById(R.id.buttonDefaultPositive);
                case NEUTRAL:
                    return (Button) view.findViewById(R.id.buttonDefaultNeutral);
                case NEGATIVE:
                    return (Button) view.findViewById(R.id.buttonDefaultNegative);
            }
        }
    }

    /**
     * @deprecated Use getActionButton(com.afollestad.materialdialogs.DialogAction)} instead.
     */
    @Override
    public Button getButton(int whichButton) {
        switch (whichButton) {
            case BUTTON_POSITIVE:
                return getActionButton(DialogAction.POSITIVE);
            case BUTTON_NEUTRAL:
                return getActionButton(DialogAction.NEUTRAL);
            case BUTTON_NEGATIVE:
                return getActionButton(DialogAction.NEGATIVE);
            default:
                return null;
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
        return customView;
    }

    /**
     * Updates an action button's title, causing invalidation to check if the action buttons should be stacked.
     *
     * @param which The action button to update.
     * @param title The new title of the action button.
     */
    public final void setActionButton(DialogAction which, CharSequence title) {
        switch (which) {
            default:
                this.positiveText = title;
                break;
            case NEUTRAL:
                this.neutralText = title;
                break;
            case NEGATIVE:
                this.negativeText = title;
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
        if (positiveText != null) number++;
        if (neutralText != null) number++;
        if (negativeText != null) number++;
        return number;
    }

    /**
     * Updates the dialog's title.
     */
    public final void setTitle(CharSequence title) {
        this.title.setText(title);
    }

    @Override
    public void setIcon(int resId) {
        icon.setImageResource(resId);
        icon.setVisibility(resId != 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setIcon(Drawable d) {
        icon.setImageDrawable(d);
        icon.setVisibility(d != null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setIconAttribute(int attrId) {
        Drawable d = DialogUtils.resolveDrawable(mContext, attrId);
        icon.setImageDrawable(d);
        icon.setVisibility(d != null ? View.VISIBLE : View.GONE);
    }

    public final void setContent(CharSequence content) {
        ((TextView) view.findViewById(R.id.content)).setText(content);
    }

    public final void setItems(CharSequence[] items) {
        if (adapter == null)
            throw new IllegalStateException("This MaterialDialog instance does not yet have an adapter set to it. You cannot use setItems().");
        if (adapter instanceof MaterialDialogAdapter) {
            adapter = new MaterialDialogAdapter(mContext, ListType.getLayoutForType(listType), R.id.title, items);
        } else {
            throw new IllegalStateException("When using a custom adapter, setItems() cannot be used. Set items through the adapter instead.");
        }
        this.items = items;

        listView.setAdapter(adapter);
        setDividerVisibility(true, true);
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

    private class MaterialDialogAdapter extends ArrayAdapter<CharSequence> {

        final int itemColor;

        public MaterialDialogAdapter(Context context, int resource, int textViewResourceId, CharSequence[] objects) {
            super(context, resource, textViewResourceId, objects);
            itemColor = DialogUtils.resolveColor(getContext(), R.attr.md_item_color, contentColor);
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
                case SINGLE:
                    RadioButton radio = (RadioButton) view.findViewById(R.id.control);
                    radio.setChecked(selectedIndex == index);
                    break;
                case MULTI:
                    if (selectedIndices != null) {
                        CheckBox checkbox = (CheckBox) view.findViewById(R.id.control);
                        checkbox.setChecked(selectedIndicesList.contains(index));
                    }
                    break;
            }

            tv.setText(items[index]);
            tv.setTextColor(itemColor);
            setTypeface(tv, regularFont);

            view.setTag(index + ":" + items[index]);

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
                    // Shouldn't be possible
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

    public static interface SimpleCallback {
        void onPositive(MaterialDialog dialog);
    }

    public static interface Callback extends SimpleCallback {
        void onNegative(MaterialDialog dialog);
    }

    public static interface FullCallback extends Callback {
        void onNeutral(MaterialDialog dialog);
    }

    private static void setVerticalMargins(View view, int topMargin, int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        boolean changed = false;
        if (topMargin > -1 && params.topMargin != topMargin) {
            params.topMargin = topMargin;
            changed = true;
        }
        if (bottomMargin > -1 && params.bottomMargin != bottomMargin) {
            params.bottomMargin = bottomMargin;
            changed = true;
        }
        if (changed)
            view.setLayoutParams(params);
    }

}
