package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.base.DialogBase;
import com.afollestad.materialdialogs.list.ItemProcessor;
import com.afollestad.materialdialogs.views.MeasureCallbackScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialDialog extends DialogBase implements View.OnClickListener, MeasureCallbackScrollView.Callback {

    private Context mContext;
    private CharSequence positiveText;
    private TextView positiveButton;
    private CharSequence neutralText;
    private TextView neutralButton;
    private CharSequence negativeText;
    private TextView negativeButton;
    private View view;
    private int positiveColor;
    private int negativeColor;
    private int neutralColor;
    private SimpleCallback callback;
    private ListCallback listCallback;
    private ListCallback listCallbackSingle;
    private ListCallbackMulti listCallbackMulti;
    private View customView;
    private String[] items;
    private boolean isStacked;
    private int selectedIndex;
    private Integer[] selectedIndices;
    private boolean mMeasuredScrollView;
    private Typeface mediumFont;
    private ItemProcessor mItemProcessor;
    private boolean hideActions;
    private boolean dismissOnActionPress;

    MaterialDialog(Builder builder) {
        super(new ContextThemeWrapper(builder.context, builder.theme == Theme.LIGHT ? R.style.Light : R.style.Dark));

        this.mContext = builder.context;
        this.view = LayoutInflater.from(builder.context).inflate(R.layout.material_dialog, null);
        this.customView = builder.customView;
        this.callback = builder.callback;
        this.listCallback = builder.listCallback;
        this.listCallbackSingle = builder.listCallbackSingle;
        this.listCallbackMulti = builder.listCallbackMulti;
        this.positiveText = builder.positiveText;
        this.neutralText = builder.neutralText;
        this.negativeText = builder.negativeText;
        this.positiveColor = builder.positiveColor;
        this.negativeColor = builder.negativeColor;
        this.neutralColor = builder.neutralColor;
        this.items = builder.items;
        this.setCancelable(builder.cancelable);
        final Typeface regularFont = Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Regular.ttf");
        this.mediumFont = Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Medium.ttf");
        this.selectedIndex = builder.selectedIndex;
        this.selectedIndices = builder.selectedIndicies;
        this.mItemProcessor = builder.itemProcessor;
        this.hideActions = builder.hideActions;

        TextView title = (TextView) view.findViewById(R.id.title);
        final TextView content = (TextView) view.findViewById(R.id.content);

        content.setText(builder.content);
        content.setMovementMethod(new LinkMovementMethod());
        content.setVisibility(View.VISIBLE);
        content.setTypeface(regularFont);
        content.setTextColor(DialogUtils.resolveColor(getContext(), android.R.attr.textColorSecondary));
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

        if (customView != null) {
            title = (TextView) view.findViewById(R.id.titleCustomView);
            invalidateCustomViewAssociations();
            ((LinearLayout) view.findViewById(R.id.customViewFrame)).addView(customView);
        } else {
            invalidateCustomViewAssociations();
        }

        if (items != null && items.length > 0)
            title = (TextView) view.findViewById(R.id.titleCustomView);

        // Title is set after it's determined whether to use first title or custom view title
        if (builder.title == null || builder.title.toString().trim().isEmpty()) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(builder.title);
            title.setTypeface(mediumFont);
            if (builder.titleColor != -1) {
                title.setTextColor(builder.titleColor);
            } else {
                title.setTextColor(DialogUtils.resolveColor(getContext(), android.R.attr.textColorPrimary));
            }
            if (builder.titleAlignment == Alignment.CENTER) {
                title.setGravity(Gravity.CENTER_HORIZONTAL);
            } else if (builder.titleAlignment == Alignment.RIGHT) {
                title.setGravity(Gravity.RIGHT);
            }
        }

        invalidateList();
        invalidateActions();
        setOnShowListenerInternal();
        setViewInternal(view);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        super.onShow(dialog); // calls any external show listeners
        checkIfStackingNeeded();
    }

    /**
     * Invalidates visibility of views for the presence of a custom view or list content
     */
    private void invalidateCustomViewAssociations() {
        if (customView != null || (items != null && items.length > 0)) {
            view.findViewById(R.id.mainFrame).setVisibility(View.GONE);
            view.findViewById(R.id.customViewScrollParent).setVisibility(View.VISIBLE);
            if (!mMeasuredScrollView) {
                // Wait until it's measured
                ((MeasureCallbackScrollView) view.findViewById(R.id.customViewScroll)).setCallback(this);
                return;
            }
            if (canCustomViewScroll()) {
                view.findViewById(R.id.customViewDivider).setVisibility(View.VISIBLE);
                view.findViewById(R.id.customViewDivider).setBackgroundColor(DialogUtils.resolveColor(getContext(), R.attr.divider_color));
                setMargin(view.findViewById(R.id.buttonStackedFrame), -1, 0, -1, -1);
                setMargin(view.findViewById(R.id.buttonDefaultFrame), -1, 0, -1, -1);
            } else {
                final int bottomMargin = (int) mContext.getResources().getDimension(R.dimen.button_padding_frame_bottom);
                setMargin(view.findViewById(R.id.buttonStackedFrame), -1, bottomMargin, -1, -1);
                setMargin(view.findViewById(R.id.buttonDefaultFrame), -1, bottomMargin, -1, -1);
            }
        } else {
            view.findViewById(R.id.mainFrame).setVisibility(View.VISIBLE);
            view.findViewById(R.id.customViewScrollParent).setVisibility(View.GONE);
            view.findViewById(R.id.customViewDivider).setVisibility(View.GONE);
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
    @SuppressLint("WrongViewCast")
    private void invalidateList() {
        if (items == null || items.length == 0) return;
        view.findViewById(R.id.content).setVisibility(View.GONE);

        final int dialogFrameMargin = (int) mContext.getResources().getDimension(R.dimen.dialog_frame_margin);
        view.findViewById(R.id.customViewScrollParent).setVisibility(View.VISIBLE);
        LinearLayout customFrame = (LinearLayout) view.findViewById(R.id.customViewFrame);
        setMargin(view.findViewById(R.id.titleCustomView), -1, -1, dialogFrameMargin, dialogFrameMargin);
        setMargin(customFrame, -1, -1, 0, 0);
        LayoutInflater li = LayoutInflater.from(mContext);

        if (listCallbackSingle != null || listCallbackMulti != null) {
            final int mainFrameMargin = (int) mContext.getResources().getDimension(R.dimen.main_frame_margin);
            customFrame.setPadding(customFrame.getPaddingLeft(), customFrame.getPaddingTop(),
                    customFrame.getPaddingRight(), mainFrameMargin);
        }

        final int itemColor = DialogUtils.resolveColor(getContext(), android.R.attr.textColorSecondary);
        for (int index = 0; index < items.length; index++) {
            View il;
            if (listCallbackSingle != null) {
                il = li.inflate(R.layout.dialog_listitem_singlechoice, null);
                if (selectedIndex > -1) {
                    RadioButton control = (RadioButton) il.findViewById(R.id.control);
                    if (selectedIndex == index) control.setChecked(true);
                }
                TextView tv = (TextView) il.findViewById(R.id.title);
                tv.setText(items[index]);
                tv.setTextColor(itemColor);
            } else if (listCallbackMulti != null) {
                il = li.inflate(R.layout.dialog_listitem_multichoice, null);
                if (selectedIndices != null) {
                    if (Arrays.asList(selectedIndices).contains(index)) {
                        CheckBox control = (CheckBox) il.findViewById(R.id.control);
                        control.setChecked(true);
                    }
                }
                TextView tv = (TextView) il.findViewById(R.id.title);
                tv.setText(items[index]);
                tv.setTextColor(itemColor);
            } else {
                if (mItemProcessor != null) {
                    il = mItemProcessor.inflateItem(index, items[index]);
                } else {
                    il = li.inflate(R.layout.dialog_listitem, null);
                    TextView tv = (TextView) il.findViewById(R.id.title);
                    tv.setText(items[index]);
                    tv.setTextColor(itemColor);
                }
            }
            il.setTag(index + ":" + items[index]);
            il.setOnClickListener(this);
            il.setBackgroundResource(DialogUtils.resolveDrawable(getContext(), R.attr.list_selector));
            customFrame.addView(il);
        }
    }

    private int calculateMaxButtonWidth() {
        /**
         * Max button width = (DialogWidth - 16dp - 16dp - 8dp) / 2
         * From: http://www.google.com/design/spec/components/dialogs.html#dialogs-specs
         */
        final int dialogWidth = getWindow().getDecorView().getMeasuredWidth();
        final int eightDp = (int) mContext.getResources().getDimension(R.dimen.button_padding_horizontal_external);
        final int sixteenDp = (int) mContext.getResources().getDimension(R.dimen.button_padding_frame_side);
        return (dialogWidth - sixteenDp - sixteenDp - eightDp) / 2;
    }

    private boolean canCustomViewScroll() {
        /**
         * Detects whether or not the custom view or list content can be scrolled.
         */
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.customViewScroll);
        final int childHeight = view.findViewById(R.id.customViewFrame).getMeasuredHeight();
        return scrollView.getMeasuredHeight() < childHeight;
    }

    private void checkIfStackingNeeded() {
        /**
         * Measures the action button's and their text to decide whether or not the button should be stacked.
         */
        if (((negativeButton == null || negativeButton.getVisibility() == View.GONE) &&
                (neutralButton == null || neutralButton.getVisibility() == View.GONE))) {
            // Stacking isn't necessary if you only have one button
            return;
        }
        final int maxWidth = calculateMaxButtonWidth();
        Log.v("StackingAlgorithm", "Max button width: " + maxWidth);
        final Paint paint = positiveButton.getPaint();
        final int eightDp = (int) mContext.getResources().getDimension(R.dimen.button_padding_horizontal_external);
        final int positiveWidth = (int) paint.measureText(positiveButton.getText().toString()) + (eightDp * 2);
        Log.v("StackingAlgorithm", "Positive button width: " + positiveWidth);
        isStacked = positiveWidth > maxWidth;
        if (!isStacked && this.neutralText != null) {
            final int neutralWidth = (int) paint.measureText(neutralButton.getText().toString()) + (eightDp * 2);
            Log.v("StackingAlgorithm", "Neutral button width: " + neutralWidth);
            isStacked = neutralWidth > maxWidth;
        }
        if (!isStacked && this.negativeText != null) {
            final int negativeWidth = (int) paint.measureText(negativeButton.getText().toString()) + (eightDp * 2);
            Log.v("StackingAlgorithm", "Negative button width: " + negativeWidth);
            isStacked = negativeWidth > maxWidth;
        }
        invalidateActions();
    }

    private boolean invalidateActions() {
        /**
         * Invalidates the positive/neutral/negative action buttons. Decides whether they should be visible
         * and sets their properties (such as height, text color, etc.).
         */
        if (items != null && listCallbackSingle == null && listCallbackMulti == null || hideActions) {
            // If the dialog is a plain list dialog, no buttons are shown.
            view.findViewById(R.id.buttonDefaultFrame).setVisibility(View.GONE);
            view.findViewById(R.id.buttonStackedFrame).setVisibility(View.GONE);
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
        positiveButton.setTypeface(mediumFont);
        if (this.positiveText == null)
            this.positiveText = mContext.getString(android.R.string.ok);
        positiveButton.setText(this.positiveText);
        positiveButton.setTextColor(getActionTextStateList(this.positiveColor));
        positiveButton.setBackgroundResource(DialogUtils.resolveDrawable(getContext(), R.attr.list_selector));
        positiveButton.setTag(POSITIVE);
        positiveButton.setOnClickListener(this);

        neutralButton = (TextView) view.findViewById(
                isStacked ? R.id.buttonStackedNeutral : R.id.buttonDefaultNeutral);
        neutralButton.setTypeface(mediumFont);
        if (this.neutralText != null) {
            neutralButton.setVisibility(View.VISIBLE);
            neutralButton.setTextColor(getActionTextStateList(this.neutralColor));
            neutralButton.setBackgroundResource(DialogUtils.resolveDrawable(getContext(), R.attr.list_selector));
            neutralButton.setText(this.neutralText);
            neutralButton.setTag(NEUTRAL);
            neutralButton.setOnClickListener(this);
        } else {
            neutralButton.setVisibility(View.GONE);
        }

        negativeButton = (TextView) view.findViewById(
                isStacked ? R.id.buttonStackedNegative : R.id.buttonDefaultNegative);
        negativeButton.setTypeface(mediumFont);
        if (this.negativeText != null) {
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setTextColor(getActionTextStateList(this.negativeColor));
            negativeButton.setBackgroundResource(DialogUtils.resolveDrawable(getContext(), R.attr.list_selector));
            negativeButton.setText(this.negativeText);
            negativeButton.setTag(NEGATIVE);
            negativeButton.setOnClickListener(this);
        } else {
            negativeButton.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public final void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(POSITIVE)) {
            if (listCallbackSingle != null) {
                if (dismissOnActionPress) dismiss();
                LinearLayout list = (LinearLayout) view.findViewById(R.id.customViewFrame);
                for (int i = 1; i < list.getChildCount(); i++) {
                    View itemView = list.getChildAt(i);
                    @SuppressLint("WrongViewCast")
                    RadioButton rb = (RadioButton) itemView.findViewById(R.id.control);
                    if (rb.isChecked()) {
                        listCallbackSingle.onSelection(this, v, i - 1, ((TextView) itemView.findViewById(R.id.title)).getText().toString());
                        break;
                    }
                }
            } else if (listCallbackMulti != null) {
                if (dismissOnActionPress) dismiss();
                List<Integer> selectedIndices = new ArrayList<Integer>();
                List<String> selectedTitles = new ArrayList<String>();
                LinearLayout list = (LinearLayout) view.findViewById(R.id.customViewFrame);
                for (int i = 1; i < list.getChildCount(); i++) {
                    View itemView = list.getChildAt(i);
                    @SuppressLint("WrongViewCast")
                    CheckBox rb = (CheckBox) itemView.findViewById(R.id.control);
                    if (rb.isChecked()) {
                        selectedIndices.add(i - 1);
                        selectedTitles.add(((TextView) itemView.findViewById(R.id.title)).getText().toString());
                    }
                }
                listCallbackMulti.onSelection(this,
                        selectedIndices.toArray(new Integer[selectedIndices.size()]),
                        selectedTitles.toArray(new String[selectedTitles.size()]));
            } else if (callback != null) {
                if (dismissOnActionPress) dismiss();
                callback.onPositive(this);
            }
        } else if (tag.equals(NEGATIVE)) {
            if (callback != null && callback instanceof Callback) {
                if (dismissOnActionPress) dismiss();
                ((Callback) callback).onNegative(this);
            }
        } else if (tag.equals(NEUTRAL)) {
            if (callback != null && callback instanceof FullCallback) {
                if (dismissOnActionPress) dismiss();
                ((FullCallback) callback).onNeutral(this);
            }
        } else {
            String[] split = tag.split(":");
            int index = Integer.parseInt(split[0]);
            if (listCallback != null) {
                if (dismissOnActionPress) dismiss();
                listCallback.onSelection(this, v, index, split[1]);
            } else if (listCallbackSingle != null) {
                RadioButton cb = (RadioButton) ((LinearLayout) v).getChildAt(0);
                if (!cb.isChecked())
                    cb.setChecked(true);
                invalidateSingleChoice(index);
            } else if (listCallbackMulti != null) {
                CheckBox cb = (CheckBox) ((LinearLayout) v).getChildAt(0);
                cb.setChecked(!cb.isChecked());
            }
        }
    }

    @Override
    public void onMeasureScroll(ScrollView view) {
        if (view.getMeasuredWidth() > 0) {
            mMeasuredScrollView = true;
            invalidateCustomViewAssociations();
        }
    }

    /**
     * The class used to construct a MaterialDialog.
     */
    public static class Builder {

        protected Activity context;
        protected CharSequence title;
        protected Alignment titleAlignment = Alignment.LEFT;
        protected Alignment contentAlignment = Alignment.LEFT;
        protected int titleColor = -1;
        protected CharSequence content;
        protected String[] items;
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
        private ListCallbackMulti listCallbackMulti;
        protected Theme theme = Theme.LIGHT;
        protected boolean cancelable = true;
        protected float contentLineSpacingMultiplier = 1.3f;
        protected int selectedIndex = -1;
        protected Integer[] selectedIndicies = null;
        protected ItemProcessor itemProcessor;
        protected boolean hideActions;
        protected boolean dismissOnActionPress = true;

        public Builder(@NonNull Activity context) {
            this.context = context;
            this.positiveText = context.getString(android.R.string.ok);
            final int materialBlue = context.getResources().getColor(R.color.material_blue_500);
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

        public Builder titleColor(int color) {
            this.titleColor = color;
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

        public Builder items(String[] items) {
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
         * Sets an item processor used to inflate and customize list items (NOT including single and
         * multi choice list items).
         *
         * @param processor The processor to apply to all non single/multi choice list items.
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder itemProessor(ItemProcessor processor) {
            this.itemProcessor = processor;
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
            this.selectedIndicies = selectedIndices;
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

        public Builder hideActions() {
            this.hideActions = true;
            return this;
        }

        /**
         * This defaults to true. If set to false, the dialog will not automatically be dismissed
         * when an action button is pressed.
         *
         * @param dismiss Whether or not to dismiss the dialog.
         * @return The Builder instance so you can chain calls to it.
         */
        public Builder dismissOnActionPress(boolean dismiss) {
            this.dismissOnActionPress = dismiss;
            return this;
        }

        public MaterialDialog build() {
            return new MaterialDialog(this);
        }
    }


    private ColorStateList getActionTextStateList(int newPrimaryColor) {
        final int buttonColor = DialogUtils.resolveColor(getContext(), android.R.attr.textColorPrimary);
        if (newPrimaryColor == 0) newPrimaryColor = buttonColor;
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{} // enabled
        };
        int[] colors = new int[]{
                DialogUtils.adjustAlpha(buttonColor, 0.6f),
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
    public final View getActionButton(DialogAction which) {
        if (view == null) return null;
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
        setActionButton(which, mContext.getString(titleRes));
    }

    /**
     * Hides the positive/neutral/negative action buttons.
     */
    public final void hideActions() {
        hideActions = true;
        invalidateActions();
    }

    /**
     * Shows the positive/neutral/negative action buttons that were previously hidden.
     */
    public final void showActions() {
        hideActions = false;
        if (invalidateActions())
            checkIfStackingNeeded();
    }

    /**
     * Retrieves the custom view that was inflated or set to the MaterialDialog during building.
     *
     * @return The custom view that was passed into the Builder.
     */
    public final View getCustomView() {
        return customView;
    }


    public static interface ListCallback {
        void onSelection(MaterialDialog dialog, View itemView, int which, String text);
    }

    public static interface ListCallbackMulti {
        void onSelection(MaterialDialog dialog, Integer[] which, String[] text);
    }

    public static interface SimpleCallback {
        void onPositive(MaterialDialog dialog);
    }

    public static interface Callback extends SimpleCallback {
        void onPositive(MaterialDialog dialog);

        void onNegative(MaterialDialog dialog);
    }

    public static interface FullCallback extends Callback {
        void onNeutral(MaterialDialog dialog);
    }
}