package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.base.DialogBase;
import com.afollestad.materialdialogs.views.MeasureCallbackListView;
import com.afollestad.materialdialogs.views.MeasureCallbackScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialDialog extends DialogBase implements View.OnClickListener, MeasureCallbackScrollView.Callback, MeasureCallbackListView.Callback {

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
    private View view;
    private ListView listView;
    private int positiveColor;
    private int negativeColor;
    private int neutralColor;
    private SimpleCallback callback;
    private ListCallback listCallback;
    private ListCallback listCallbackSingle;
    private ListCallbackMulti listCallbackMulti;
    private View customView;
    private CharSequence[] items;
    private boolean isStacked;
    private int selectedIndex;
    private Integer[] selectedIndices;
    private boolean mMeasuredScrollView;
    private Typeface mediumFont;
    private Typeface regularFont;
    private boolean autoDismiss;
    private ListAdapter adapter;
    private ListType listType;
    private List<Integer> selectedIndicesList;

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
        this.view = LayoutInflater.from(getContext()).inflate(R.layout.md_dialog, null);
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
            title = (TextView) view.findViewById(R.id.titleCustomView);
            icon = (ImageView) view.findViewById(R.id.iconCustomView);
            titleFrame = view.findViewById(R.id.titleFrameCustomView);
            invalidateCustomViewAssociations();
            ((LinearLayout) view.findViewById(R.id.customViewFrame)).addView(customView);
        } else {
            invalidateCustomViewAssociations();
        }

        boolean adapterProvided = adapter != null;
        if (items != null && items.length > 0 || adapterProvided) {
            title = (TextView) view.findViewById(R.id.titleCustomView);
            icon = (ImageView) view.findViewById(R.id.iconCustomView);
            titleFrame = view.findViewById(R.id.titleFrameCustomView);
            listView = (ListView) view.findViewById(R.id.contentListView);
            listView.setSelector(DialogUtils.resolveDrawable(getContext(), R.attr.md_selector));
            ((MeasureCallbackListView) listView).setCallback(this);

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

            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            invalidateCustomViewAssociations();
                        }
                    });
                }
            });
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

        // Title is set after it's determined whether to use first title or custom view title
        if (builder.title == null || builder.title.toString().trim().length() == 0) {
            titleFrame.setVisibility(View.GONE);
            if (customView == null)
                view.findViewById(R.id.titleFrameCustomView).setVisibility(View.GONE);
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

        invalidateActions();
        setOnShowListenerInternal();
        setViewInternal(view);

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
        invalidateCustomViewAssociations();
    }

    /**
     * Invalidates visibility of views for the presence of a custom view or list content
     */
    private void invalidateCustomViewAssociations() {
        if (customView != null || (items != null && items.length > 0) || adapter != null) {
            view.findViewById(R.id.mainFrame).setVisibility(View.GONE);
            view.findViewById(R.id.customViewScrollParent).setVisibility(View.VISIBLE);
            if (!mMeasuredScrollView && listView == null) {
                // Wait until it's measured
                ((MeasureCallbackScrollView) view.findViewById(R.id.customViewScroll)).setCallback(this);
                return;
            }
            if (canCustomViewScroll()) {
                view.findViewById(R.id.customViewDivider).setVisibility(View.VISIBLE);
                view.findViewById(R.id.customViewDivider).setBackgroundColor(DialogUtils.resolveColor(getContext(), R.attr.md_divider));
                setMargin(view.findViewById(R.id.buttonStackedFrame), -1, 0, -1, -1);
                setMargin(view.findViewById(R.id.buttonDefaultFrame), -1, 0, -1, -1);
                if (items != null && items.length > 0) {
                    View customFrame = view.findViewById(R.id.customViewFrame);
                    Resources r = getContext().getResources();
                    int bottomPadding = view.findViewById(R.id.titleCustomView).getVisibility() == View.VISIBLE ?
                            (int) r.getDimension(R.dimen.md_main_frame_margin) : (int) r.getDimension(R.dimen.md_dialog_frame_margin);
                    customFrame.setPadding(customFrame.getPaddingLeft(), customFrame.getPaddingTop(),
                            customFrame.getPaddingRight(), bottomPadding);
                }
            } else {
                view.findViewById(R.id.customViewDivider).setVisibility(View.GONE);
                final int bottomMargin = (int) getContext().getResources().getDimension(R.dimen.md_button_padding_frame_bottom);
                setMargin(view.findViewById(R.id.buttonStackedFrame), -1, bottomMargin, -1, -1);
                setMargin(view.findViewById(R.id.buttonDefaultFrame), -1, bottomMargin, -1, -1);
            }
        } else {
            view.findViewById(R.id.mainFrame).setVisibility(View.VISIBLE);
            view.findViewById(R.id.customViewScrollParent).setVisibility(View.GONE);
            view.findViewById(R.id.customViewDivider).setVisibility(View.GONE);
            if (!mMeasuredScrollView) {
                // Wait until it's measured
                ((MeasureCallbackScrollView) view.findViewById(R.id.contentScrollView)).setCallback(this);
                return;
            }
            if (canContentScroll()) {
                view.findViewById(R.id.customViewDivider).setVisibility(View.VISIBLE);
                view.findViewById(R.id.customViewDivider).setBackgroundColor(DialogUtils.resolveColor(getContext(), R.attr.md_divider));
                setMargin(view.findViewById(R.id.mainFrame), -1, 0, -1, -1);
                setMargin(view.findViewById(R.id.buttonStackedFrame), -1, 0, -1, -1);
                setMargin(view.findViewById(R.id.buttonDefaultFrame), -1, 0, -1, -1);
                final int conPadding = (int) getContext().getResources().getDimension(R.dimen.md_main_frame_margin);
                View con = view.findViewById(R.id.content);
                con.setPadding(con.getPaddingLeft(), 0, con.getPaddingRight(), conPadding);
            } else {
                View con = view.findViewById(R.id.content);
                con.setPadding(con.getPaddingLeft(), 0, con.getPaddingRight(), 0);
            }
        }
    }

    /**
     * Invalidates the radio buttons in the single choice mode list so that only the radio button that
     * was previous selected is checked.
     */
    private void invalidateSingleChoice(int newSelection) {
        newSelection++;
        final LinearLayout list = (LinearLayout) view.findViewById(R.id.customViewFrame);
        for (int i = 1; i < list.getChildCount(); i++) {
            View v = list.getChildAt(i);
            @SuppressLint("WrongViewCast")
            RadioButton rb = (RadioButton) v.findViewById(R.id.control);
            if (newSelection != i) {
                rb.setChecked(false);
                rb.clearFocus();
            }
        }
    }

    /**
     * Constructs the dialog's list content and sets up click listeners.
     */
    private void invalidateList() {
        if ((items == null || items.length == 0) && adapter == null) return;

        // Hide content
        view.findViewById(R.id.contentScrollView).setVisibility(View.GONE);

        // Show custom frame container but hide the scrollview
        view.findViewById(R.id.customViewScrollParent).setVisibility(View.VISIBLE);
        view.findViewById(R.id.customViewScroll).setVisibility(View.GONE);

        // Set up list with adapter
        LinearLayout listViewContainer = (LinearLayout) view.findViewById(R.id.list_view_container);
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

        final int dialogFramePadding = (int) mContext.getResources().getDimension(R.dimen.md_dialog_frame_margin);
        final int mainFramePadding = (int) mContext.getResources().getDimension(R.dimen.md_main_frame_margin);
        if (titleFrame.getVisibility() == View.VISIBLE || icon.getVisibility() == View.VISIBLE) {
            int bottomPadding = mainFramePadding;
            if (icon.getVisibility() == View.VISIBLE)
                bottomPadding = (int) getContext().getResources().getDimension(R.dimen.md_title_margin_plainlist);
            setMargin(titleFrame, dialogFramePadding, bottomPadding, dialogFramePadding, dialogFramePadding);
            ((ViewGroup) titleFrame.getParent()).removeView(titleFrame);
            listViewContainer.addView(titleFrame, 0);
        } else {
            listView.setPadding(listView.getPaddingLeft(), mainFramePadding,
                    listView.getPaddingRight(), listView.getPaddingBottom());
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
     * Detects whether or not the custom view or list content can be scrolled.
     */
    private boolean canCustomViewScroll() {
        if (listView != null) {
            return listView.getLastVisiblePosition() != -1 && listView.getLastVisiblePosition() < (listView.getCount() - 1);
        }
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.customViewScroll);
        final int childHeight = view.findViewById(R.id.customViewFrame).getMeasuredHeight();
        return scrollView.getMeasuredHeight() < childHeight;
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
        if (tag.equals(POSITIVE)) {
            if (listCallbackSingle != null) {
                if (autoDismiss) dismiss();
                sendSingleChoiceCallback(v);
            } else if (listCallbackMulti != null) {
                if (autoDismiss) dismiss();
                sendMultichoiceCallback();
            } else if (callback != null) {
                if (autoDismiss) dismiss();
                callback.onPositive(this);
            } else if (autoDismiss) dismiss();
        } else if (tag.equals(NEGATIVE)) {
            if (callback != null && callback instanceof Callback) {
                if (autoDismiss) dismiss();
                ((Callback) callback).onNegative(this);
            } else if (autoDismiss) dismiss();
        } else if (tag.equals(NEUTRAL)) {
            if (callback != null && callback instanceof FullCallback) {
                if (autoDismiss) dismiss();
                ((FullCallback) callback).onNeutral(this);
            } else if (autoDismiss) dismiss();
        } else {
            String[] split = tag.split(":");
            int index = Integer.parseInt(split[0]);
            if (listCallback != null) {
                if (autoDismiss) dismiss();
                listCallback.onSelection(this, v, index, split[1]);
            } else if (listCallbackSingle != null) {
                RadioButton cb = (RadioButton) ((LinearLayout) v).getChildAt(0);
                if (!cb.isChecked())
                    cb.setChecked(true);
                invalidateSingleChoice(index);
                if (positiveText == null) {
                    // Immediately send the selection callback if no positive button is shown
                    if (autoDismiss) dismiss();
                    sendSingleChoiceCallback(v);
                }
            } else if (listCallbackMulti != null) {
                CheckBox cb = (CheckBox) ((LinearLayout) v).getChildAt(0);
                cb.setChecked(!cb.isChecked());
                if (positiveText == null) {
                    // Immediately send the selection callback if no positive button is shown
                    if (autoDismiss) dismiss();
                    sendMultichoiceCallback();
                }
            } else if (autoDismiss) dismiss();
        }
    }

    @Override
    public void onMeasureScroll(ScrollView view) {
        if (view.getMeasuredWidth() > 0) {
            mMeasuredScrollView = true;
            invalidateCustomViewAssociations();
        }
    }

    @Override
    public void onMeasureList(ListView view) {
        invalidateCustomViewAssociations();
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

        public Builder customView(@LayoutRes int layoutRes) {
            LayoutInflater li = LayoutInflater.from(this.context);
            customView(li.inflate(layoutRes, null));
            return this;
        }

        public Builder customView(View view) {
            this.customView = view;
            return this;
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
        invalidateCustomViewAssociations();
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
}
