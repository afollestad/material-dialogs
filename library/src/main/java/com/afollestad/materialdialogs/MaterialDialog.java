package com.afollestad.materialdialogs;

import android.app.Activity;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.base.DialogBase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialDialog extends DialogBase implements View.OnClickListener {

    private Context mContext;
    private CharSequence positiveText;
    private TextView positiveButton;
    private CharSequence neutralText;
    private TextView neutralButton;
    private CharSequence negativeText;
    private TextView negativeButton;
    private View view;
    private int positiveColor;
    private SimpleCallback callback;
    private ListCallback listCallback;
    private ListCallback listCallbackSingle;
    private ListCallbackMulti listCallbackMulti;
    private View customView;
    private float buttonHeight;
    private String[] items;
    private boolean isStacked;

    private Typeface regularFont;
    private Typeface mediumFont;

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
        this.items = builder.items;
        this.setCancelable(builder.cancelable);
        this.regularFont = Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Regular.ttf");
        this.mediumFont = Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Medium.ttf");

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView content = (TextView) view.findViewById(R.id.content);

        content.setText(builder.content);
        content.setMovementMethod(new LinkMovementMethod());
        content.setVisibility(View.VISIBLE);
        content.setTypeface(regularFont);
        content.setTextColor(Utils.resolveColor(getContext(), R.attr.content_color));
        content.setLineSpacing(0f, builder.contentLineSpacingMultiplier);
        if (this.positiveColor == 0) {
            content.setLinkTextColor(Utils.resolveColor(getContext(), R.attr.button_color));
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
            buttonHeight = mContext.getResources().getDimension(R.dimen.button_height_customview);
            view.findViewById(R.id.mainFrame).setVisibility(View.GONE);
            view.findViewById(R.id.customViewScroll).setVisibility(View.VISIBLE);
            view.findViewById(R.id.customViewDivider).setVisibility(View.VISIBLE);
            view.findViewById(R.id.customViewDivider).setBackgroundColor(Utils.resolveColor(getContext(), R.attr.divider_color));
            ((LinearLayout) view.findViewById(R.id.customViewFrame)).addView(customView);
        } else {
            buttonHeight = mContext.getResources().getDimension(R.dimen.button_height);
            view.findViewById(R.id.mainFrame).setVisibility(View.VISIBLE);
            view.findViewById(R.id.customViewScroll).setVisibility(View.GONE);
            view.findViewById(R.id.customViewDivider).setVisibility(View.GONE);
        }

        // Title is set after it's determined whether to use first title or custom view title
        title.setText(builder.title);
        title.setTypeface(mediumFont);
        if (builder.titleColor != -1) {
            title.setTextColor(builder.titleColor);
        } else {
            title.setTextColor(Utils.resolveColor(getContext(), R.attr.title_color));
        }
        if (builder.titleAlignment == Alignment.CENTER) {
            title.setGravity(Gravity.CENTER_HORIZONTAL);
        } else if (builder.titleAlignment == Alignment.RIGHT) {
            title.setGravity(Gravity.RIGHT);
        }

        invalidateList();
        invalidateActions();
        checkIfStackingNeeded();
        setViewInternal(view);
    }

    /**
     * Invalidates the radio buttons in the single choice mode list so that only the radio button that
     * was previous selected is checked.
     */
    private void invalidateSingleChoice(int newSelection) {
        LinearLayout list = (LinearLayout) view.findViewById(R.id.listFrame);
        for (int i = 0; i < list.getChildCount(); i++) {
            View v = list.getChildAt(i);
            RadioButton rb = (RadioButton) ((LinearLayout) v).getChildAt(0);
            rb.setChecked(newSelection == i);
        }
    }

    /**
     * Constructs the dialog's list content and sets up click listeners.
     */
    private void invalidateList() {
        if (items == null || items.length == 0) return;
        view.findViewById(R.id.content).setVisibility(View.GONE);

        // When showing list items, less padding is used on the left/right and bottom of the title area
        View mainFrame = view.findViewById(R.id.mainFrame);
        View title = view.findViewById(R.id.title);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) title.getLayoutParams();
        params.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.title_margin_customview);
        title.setLayoutParams(params);
        int dpPadding = (int) mContext.getResources().getDimension(R.dimen.button_frame_margin);
        mainFrame.setPadding(dpPadding, mainFrame.getPaddingTop(), dpPadding, 0);

        LinearLayout list = (LinearLayout) view.findViewById(R.id.listFrame);
        list.setVisibility(View.VISIBLE);
        LayoutInflater li = LayoutInflater.from(mContext);

        final int itemColor = Utils.resolveColor(getContext(), R.attr.item_color);
        for (int index = 0; index < items.length; index++) {
            View il;
            if (listCallbackSingle != null) {
                il = li.inflate(R.layout.dialog_listitem_singlechoice, null);
                RadioButton rb = (RadioButton) ((LinearLayout) il).getChildAt(0);
                rb.setText(items[index]);
                rb.setTextColor(itemColor);
            } else if (listCallbackMulti != null) {
                il = li.inflate(R.layout.dialog_listitem_multichoice, null);
                CheckBox cb = (CheckBox) ((LinearLayout) il).getChildAt(0);
                cb.setText(items[index]);
                cb.setTextColor(itemColor);
            } else {
                il = li.inflate(R.layout.dialog_listitem, null);
                TextView tv = (TextView) ((LinearLayout) il).getChildAt(0);
                tv.setText(items[index]);
                tv.setTextColor(itemColor);
            }
            il.setTag(index + ":" + items[index]);
            il.setOnClickListener(this);
            list.addView(il);
        }
    }

    /**
     * Measures the action button's and their text to decide whether or not the button should be stacked.
     */
    private void checkIfStackingNeeded() {
        if (((negativeButton == null || negativeButton.getVisibility() == View.GONE) &&
                (neutralButton == null || neutralButton.getVisibility() == View.GONE))) {
            // Stacking isn't necessary if you only have one button
            return;
        }

        Paint paint = positiveButton.getPaint();
        float buttonMinWidth = mContext.getResources().getDimension(R.dimen.button_min_width);
        float totalWidth = paint.measureText(positiveButton.getText().toString());

        Log.v("MaterialDialogStack", "Positive width: " + totalWidth);
        if (this.neutralText != null) {
            totalWidth += paint.measureText(neutralButton.getText().toString());
            Log.v("MaterialDialogStack", "With neutral width: " + totalWidth);
        }
        if (this.negativeText != null) {
            totalWidth += paint.measureText(negativeButton.getText().toString());
            Log.v("MaterialDialogStack", "With negative width: " + totalWidth);
        }
        Log.v("MaterialDialogStack", "Max width: " + buttonMinWidth * 3);
        isStacked = totalWidth > (buttonMinWidth * 3);
        invalidateActions();
    }

    /**
     * Invalidates the height of action buttons based on whether there's a custom view or not, also
     * adds bottom margin to the buttons (only to the bottom one if they're stacked).
     */
    private void invalidateHeightAndMargin(View button, boolean bottom) {
        if (button.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
            params.height = (int) buttonHeight;
            if (customView != null) {
                params.bottomMargin = 0;
            } else {
                if (isStacked && !bottom) return;
                params.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.button_frame_margin);
            }
            button.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button.getLayoutParams();
            params.height = (int) buttonHeight;
            if (customView != null) {
                params.bottomMargin = 0;
            } else {
                if (isStacked && !bottom) return;
                params.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.button_frame_margin);
            }
            button.setLayoutParams(params);
        }
    }

    /**
     * Invalidates the positive/neutral/negative action buttons. Decides whether they should be visible
     * and sets their properties (such as height, text color, etc.).
     */
    private void invalidateActions() {
        if (items != null && listCallbackSingle == null && listCallbackMulti == null) {
            // If the dialog is a plain list dialog, no buttons are shown.
            view.findViewById(R.id.buttonDefaultFrame).setVisibility(View.GONE);
            view.findViewById(R.id.buttonStackedFrame).setVisibility(View.GONE);
            return;
        }

        view.findViewById(R.id.buttonDefaultFrame).setVisibility(isStacked ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.buttonStackedFrame).setVisibility(isStacked ? View.VISIBLE : View.GONE);

        positiveButton = (TextView) view.findViewById(
                isStacked ? R.id.buttonStackedPositive : R.id.buttonDefaultPositive);
        positiveButton.setTypeface(mediumFont);
        if (this.positiveText == null)
            this.positiveText = mContext.getString(R.string.accept);
        positiveButton.setText(this.positiveText);
        positiveButton.setTextColor(getActionTextStateList(this.positiveColor));

        invalidateHeightAndMargin(positiveButton, negativeText == null && neutralText == null);
        positiveButton.setTag(POSITIVE);
        positiveButton.setOnClickListener(this);

        neutralButton = (TextView) view.findViewById(
                isStacked ? R.id.buttonStackedNeutral : R.id.buttonDefaultNeutral);
        neutralButton.setTypeface(mediumFont);
        if (this.neutralText != null) {
            neutralButton.setVisibility(View.VISIBLE);
            neutralButton.setTextColor(getActionTextStateList(0));
            neutralButton.setText(this.neutralText);
            invalidateHeightAndMargin(neutralButton, true);
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
            negativeButton.setTextColor(getActionTextStateList(0));
            negativeButton.setText(this.negativeText);
            invalidateHeightAndMargin(negativeButton, neutralText == null);
            negativeButton.setTag(NEGATIVE);
            negativeButton.setOnClickListener(this);
        } else {
            negativeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public final void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(POSITIVE)) {
            if (listCallbackSingle != null) {
                dismiss();
                LinearLayout list = (LinearLayout) view.findViewById(R.id.listFrame);
                for (int i = 0; i < list.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) ((LinearLayout) list.getChildAt(i)).getChildAt(0);
                    if (rb.isChecked()) {
                        listCallbackSingle.onSelection(this, i, rb.getText().toString());
                        break;
                    }
                }
            } else if (listCallbackMulti != null) {
                dismiss();
                List<Integer> selectedIndices = new ArrayList<Integer>();
                List<String> selectedTitles = new ArrayList<String>();
                LinearLayout list = (LinearLayout) view.findViewById(R.id.listFrame);
                for (int i = 0; i < list.getChildCount(); i++) {
                    CheckBox rb = (CheckBox) ((LinearLayout) list.getChildAt(i)).getChildAt(0);
                    if (rb.isChecked()) {
                        selectedIndices.add(i);
                        selectedTitles.add(rb.getText().toString());
                    }
                }
                listCallbackMulti.onSelection(this,
                        selectedIndices.toArray(new Integer[selectedIndices.size()]),
                        selectedTitles.toArray(new String[selectedTitles.size()]));
            } else if (callback != null) {
                dismiss();
                callback.onPositive(this);
            }
        } else if (tag.equals(NEGATIVE)) {
            if (callback != null && callback instanceof Callback) {
                dismiss();
                ((Callback) callback).onNegative(this);
            }
        } else if (tag.equals(NEUTRAL)) {
            if (callback != null && callback instanceof FullCallback) {
                dismiss();
                ((FullCallback) callback).onNeutral(this);
            }
        } else {
            String[] split = tag.split(":");
            int index = Integer.parseInt(split[0]);
            if (listCallback != null) {
                dismiss();
                listCallback.onSelection(this, index, split[1]);
            } else if (listCallbackSingle != null) {
                RadioButton cb = (RadioButton) ((LinearLayout) v).getChildAt(0);
                cb.performClick();
                invalidateSingleChoice(index);
            } else if (listCallbackMulti != null) {
                CheckBox cb = (CheckBox) ((LinearLayout) v).getChildAt(0);
                cb.performClick();
            }
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
        protected SimpleCallback callback;
        protected ListCallback listCallback;
        protected ListCallback listCallbackSingle;
        private ListCallbackMulti listCallbackMulti;
        protected Theme theme = Theme.LIGHT;
        protected boolean cancelable = true;
        protected float contentLineSpacingMultiplier = 1.0f;

        public Builder(@NonNull Activity context) {
            this.context = context;
            this.positiveText = context.getString(R.string.accept);
            final int materialBlue = context.getResources().getColor(R.color.material_blue_500);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorAccent});
                try {
                    this.positiveColor = a.getColor(0, materialBlue);
                } finally {
                    a.recycle();
                }
            } else {
                this.positiveColor = materialBlue;
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

        public Builder itemsCallbackSingleChoice(ListCallback callback) {
            this.listCallback = null;
            this.listCallbackSingle = callback;
            this.listCallbackMulti = null;
            return this;
        }

        public Builder itemsCallbackMultiChoice(ListCallbackMulti callback) {
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

        public MaterialDialog build() {
            return new MaterialDialog(this);
        }
    }


    private ColorStateList getActionTextStateList(int newPrimaryColor) {
        final int buttonColor = Utils.resolveColor(getContext(), R.attr.button_color);
        if (newPrimaryColor == 0) newPrimaryColor = buttonColor;
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{} // enabled
        };
        int[] colors = new int[]{
                Utils.adjustAlpha(buttonColor, 0.6f),
                newPrimaryColor
        };
        return new ColorStateList(states, colors);
    }

    /**
     * Retrieves the view of an action button, allowing you to modify properties such as whether or not it's enabled.
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
     * Retrieves the custom view that was inflated or set to the MaterialDialog during building.
     */
    public final View getCustomView() {
        return customView;
    }


    public static interface ListCallback {
        void onSelection(MaterialDialog dialog, int which, String text);
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
