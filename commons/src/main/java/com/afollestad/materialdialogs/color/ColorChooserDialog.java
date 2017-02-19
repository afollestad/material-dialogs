package com.afollestad.materialdialogs.color;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.commons.R;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings({"FieldCanBeLocal", "ConstantConditions"})
public class ColorChooserDialog extends DialogFragment implements View.OnClickListener, View.OnLongClickListener {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            TAG_PRIMARY,
            TAG_ACCENT,
            TAG_CUSTOM
    })
    public @interface ColorChooserTag {
    }

    public final static String TAG_PRIMARY = "[MD_COLOR_CHOOSER]";
    public final static String TAG_ACCENT = "[MD_COLOR_CHOOSER]";
    public final static String TAG_CUSTOM = "[MD_COLOR_CHOOSER]";

    @NonNull
    private int[] mColorsTop;
    @Nullable
    private int[][] mColorsSub;

    private void generateColors() {
        Builder builder = getBuilder();
        if (builder.mColorsTop != null) {
            mColorsTop = builder.mColorsTop;
            mColorsSub = builder.mColorsSub;
            return;
        }

        if (builder.mAccentMode) {
            mColorsTop = ColorPalette.ACCENT_COLORS;
            mColorsSub = ColorPalette.ACCENT_COLORS_SUB;
        } else {
            mColorsTop = ColorPalette.PRIMARY_COLORS;
            mColorsSub = ColorPalette.PRIMARY_COLORS_SUB;
        }
    }

    public ColorChooserDialog() {
    }

    private int circleSize;
    private ColorCallback callback;
    private GridView grid;

    private View colorChooserCustomFrame;
    private EditText customColorHex;
    private View customColorIndicator;
    private TextWatcher customColorTextWatcher;
    private SeekBar customSeekA;
    private TextView customSeekAValue;
    private SeekBar customSeekR;
    private TextView customSeekRValue;
    private SeekBar customSeekG;
    private TextView customSeekGValue;
    private SeekBar customSeekB;
    private TextView customSeekBValue;
    private SeekBar.OnSeekBarChangeListener customColorRgbListener;
    private int selectedCustomColor;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("top_index", topIndex());
        outState.putBoolean("in_sub", isInSub());
        outState.putInt("sub_index", subIndex());
        outState.putBoolean("in_custom", colorChooserCustomFrame != null &&
                colorChooserCustomFrame.getVisibility() == View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ColorCallback))
            throw new IllegalStateException("ColorChooserDialog needs to be shown from an Activity implementing ColorCallback.");
        callback = (ColorCallback) activity;
    }

    private boolean isInSub() {
        return getArguments().getBoolean("in_sub", false);
    }

    private void isInSub(boolean value) {
        getArguments().putBoolean("in_sub", value);
    }

    private int topIndex() {
        return getArguments().getInt("top_index", -1);
    }

    private void topIndex(int value) {
        if (value > -1)
            findSubIndexForColor(value, mColorsTop[value]);
        getArguments().putInt("top_index", value);
    }

    private int subIndex() {
        if (mColorsSub == null) return -1;
        return getArguments().getInt("sub_index", -1);
    }

    private void subIndex(int value) {
        if (mColorsSub == null) return;
        getArguments().putInt("sub_index", value);
    }

    @StringRes public int getTitle() {
        Builder builder = getBuilder();
        int title;
        if (isInSub()) title = builder.mTitleSub;
        else title = builder.mTitle;
        if (title == 0) title = builder.mTitle;
        return title;
    }

    public String tag() {
        Builder builder = getBuilder();
        if (builder.mTag != null)
            return builder.mTag;
        else return super.getTag();
    }

    public boolean isAccentMode() {
        return getBuilder().mAccentMode;
    }

    @Override public void onClick(View v) {
        if (v.getTag() != null) {
            final String[] tag = ((String) v.getTag()).split(":");
            final int index = Integer.parseInt(tag[0]);
            final MaterialDialog dialog = (MaterialDialog) getDialog();
            final Builder builder = getBuilder();

            if (isInSub()) {
                subIndex(index);
            } else {
                topIndex(index);
                if (mColorsSub != null && index < mColorsSub.length) {
                    dialog.setActionButton(DialogAction.NEGATIVE, builder.mBackBtn);
                    isInSub(true);
                }
            }

            if (builder.mAllowUserCustom)
                selectedCustomColor = getSelectedColor();
            invalidateDynamicButtonColors();
            invalidate();
        }
    }

    @Override public boolean onLongClick(View v) {
        if (v.getTag() != null) {
            final String[] tag = ((String) v.getTag()).split(":");
            final int color = Integer.parseInt(tag[1]);
            ((CircleView) v).showHint(color);
            return true;
        }
        return false;
    }

    private void invalidateDynamicButtonColors() {
        final MaterialDialog dialog = (MaterialDialog) getDialog();
        if (dialog == null) return;
        final Builder builder = getBuilder();
        if (builder.mDynamicButtonColor) {
            int selectedColor = getSelectedColor();
            if (Color.alpha(selectedColor) < 64 ||
                    (Color.red(selectedColor) > 247 &&
                            Color.green(selectedColor) > 247 &&
                            Color.blue(selectedColor) > 247)) {
                // Once we get close to white or transparent, the action buttons and seekbars will be a very light gray
                selectedColor = Color.parseColor("#DEDEDE");
            }

            if (getBuilder().mDynamicButtonColor) {
                dialog.getActionButton(DialogAction.POSITIVE).setTextColor(selectedColor);
                dialog.getActionButton(DialogAction.NEGATIVE).setTextColor(selectedColor);
                dialog.getActionButton(DialogAction.NEUTRAL).setTextColor(selectedColor);
            }

            if (customSeekR != null) {
                if (customSeekA.getVisibility() == View.VISIBLE)
                    MDTintHelper.setTint(customSeekA, selectedColor);
                MDTintHelper.setTint(customSeekR, selectedColor);
                MDTintHelper.setTint(customSeekG, selectedColor);
                MDTintHelper.setTint(customSeekB, selectedColor);
            }
        }
    }

    @ColorInt private int getSelectedColor() {
        if (colorChooserCustomFrame != null &&
                colorChooserCustomFrame.getVisibility() == View.VISIBLE) {
            return selectedCustomColor;
        }

        int color = 0;
        if (subIndex() > -1)
            color = mColorsSub[topIndex()][subIndex()];
        else if (topIndex() > -1)
            color = mColorsTop[topIndex()];
        if (color == 0) {
            int fallback = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                fallback = DialogUtils.resolveColor(getActivity(), android.R.attr.colorAccent);
            color = DialogUtils.resolveColor(getActivity(), R.attr.colorAccent, fallback);
        }
        return color;
    }

    public interface ColorCallback {

        void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor);

        void onColorChooserDismissed(@NonNull ColorChooserDialog dialog);
    }

    private void findSubIndexForColor(int topIndex, int color) {
        if (mColorsSub == null || mColorsSub.length - 1 < topIndex)
            return;
        int[] subColors = mColorsSub[topIndex];
        for (int subIndex = 0; subIndex < subColors.length; subIndex++) {
            if (subColors[subIndex] == color) {
                subIndex(subIndex);
                break;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() == null || !getArguments().containsKey("builder"))
            throw new IllegalStateException("ColorChooserDialog should be created using its Builder interface.");
        generateColors();

        int preselectColor;
        boolean foundPreselectColor = false;

        if (savedInstanceState != null) {
            foundPreselectColor = !savedInstanceState.getBoolean("in_custom", false);
            preselectColor = getSelectedColor();
        } else {
            if (getBuilder().mSetPreselectionColor) {
                preselectColor = getBuilder().mPreselect;
                if (preselectColor != 0) {
                    for (int topIndex = 0; topIndex < mColorsTop.length; topIndex++) {
                        if (mColorsTop[topIndex] == preselectColor) {
                            foundPreselectColor = true;
                            topIndex(topIndex);
                            if (getBuilder().mAccentMode) {
                                subIndex(2);
                            } else if (mColorsSub != null) {
                                findSubIndexForColor(topIndex, preselectColor);
                            } else {
                                subIndex(5);
                            }
                            break;
                        }

                        if (mColorsSub != null) {
                            for (int subIndex = 0; subIndex < mColorsSub[topIndex].length; subIndex++) {
                                if (mColorsSub[topIndex][subIndex] == preselectColor) {
                                    foundPreselectColor = true;
                                    topIndex(topIndex);
                                    subIndex(subIndex);
                                    break;
                                }
                            }
                            if (foundPreselectColor) break;
                        }
                    }
                }
            } else {
                preselectColor = Color.BLACK;
                foundPreselectColor = true;
            }
        }

        circleSize = getResources().getDimensionPixelSize(R.dimen.md_colorchooser_circlesize);
        final Builder builder = getBuilder();

        MaterialDialog.Builder bd = new MaterialDialog.Builder(getActivity())
                .title(getTitle())
                .autoDismiss(false)
                .customView(R.layout.md_dialog_colorchooser, false)
                .negativeText(builder.mCancelBtn)
                .positiveText(builder.mDoneBtn)
                .neutralText(builder.mAllowUserCustom ? builder.mCustomBtn : 0)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        callback.onColorSelection(ColorChooserDialog.this, getSelectedColor());
                        dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (isInSub()) {
                            dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mCancelBtn);
                            isInSub(false);
                            subIndex(-1); // Do this to avoid ArrayIndexOutOfBoundsException
                            invalidate();
                        } else {
                            dialog.cancel();
                        }
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        toggleCustom(dialog);
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        invalidateDynamicButtonColors();
                    }
                });

        if (builder.mTheme != null)
            bd.theme(builder.mTheme);

        final MaterialDialog dialog = bd.build();
        final View v = dialog.getCustomView();
        grid = (GridView) v.findViewById(R.id.md_grid);

        if (builder.mAllowUserCustom) {
            selectedCustomColor = preselectColor;
            colorChooserCustomFrame = v.findViewById(R.id.md_colorChooserCustomFrame);
            customColorHex = (EditText) v.findViewById(R.id.md_hexInput);
            customColorIndicator = v.findViewById(R.id.md_colorIndicator);
            customSeekA = (SeekBar) v.findViewById(R.id.md_colorA);
            customSeekAValue = (TextView) v.findViewById(R.id.md_colorAValue);
            customSeekR = (SeekBar) v.findViewById(R.id.md_colorR);
            customSeekRValue = (TextView) v.findViewById(R.id.md_colorRValue);
            customSeekG = (SeekBar) v.findViewById(R.id.md_colorG);
            customSeekGValue = (TextView) v.findViewById(R.id.md_colorGValue);
            customSeekB = (SeekBar) v.findViewById(R.id.md_colorB);
            customSeekBValue = (TextView) v.findViewById(R.id.md_colorBValue);

            if (!builder.mAllowUserCustomAlpha) {
                v.findViewById(R.id.md_colorALabel).setVisibility(View.GONE);
                customSeekA.setVisibility(View.GONE);
                customSeekAValue.setVisibility(View.GONE);
                customColorHex.setHint("2196F3");
                customColorHex.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            } else {
                customColorHex.setHint("FF2196F3");
                customColorHex.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            }

            if (!foundPreselectColor) {
                // If color wasn't found in the preset colors, it must be custom
                toggleCustom(dialog);
            }
        }

        invalidate();
        return dialog;
    }

    @Override public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (callback != null) {
            callback.onColorChooserDismissed(this);
        }
    }

    private void toggleCustom(MaterialDialog dialog) {
        if (dialog == null)
            dialog = (MaterialDialog) getDialog();
        if (grid.getVisibility() == View.VISIBLE) {
            dialog.setTitle(getBuilder().mCustomBtn);
            dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().mPresetsBtn);
            dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mCancelBtn);
            grid.setVisibility(View.INVISIBLE);
            colorChooserCustomFrame.setVisibility(View.VISIBLE);

            customColorTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        selectedCustomColor = Color.parseColor("#" + s.toString());
                    } catch (IllegalArgumentException e) {
                        selectedCustomColor = Color.BLACK;
                    }
                    customColorIndicator.setBackgroundColor(selectedCustomColor);
                    if (customSeekA.getVisibility() == View.VISIBLE) {
                        int alpha = Color.alpha(selectedCustomColor);
                        customSeekA.setProgress(alpha);
                        customSeekAValue.setText(String.format(Locale.US, "%d", alpha));
                    }
                    if (customSeekA.getVisibility() == View.VISIBLE) {
                        int alpha = Color.alpha(selectedCustomColor);
                        customSeekA.setProgress(alpha);
                    }
                    int red = Color.red(selectedCustomColor);
                    customSeekR.setProgress(red);
                    int green = Color.green(selectedCustomColor);
                    customSeekG.setProgress(green);
                    int blue = Color.blue(selectedCustomColor);
                    customSeekB.setProgress(blue);
                    isInSub(false);
                    topIndex(-1);
                    subIndex(-1);
                    invalidateDynamicButtonColors();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            customColorHex.addTextChangedListener(customColorTextWatcher);
            customColorRgbListener = new SeekBar.OnSeekBarChangeListener() {

                @SuppressLint("DefaultLocale")
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        if (getBuilder().mAllowUserCustomAlpha) {
                            int color = Color.argb(customSeekA.getProgress(),
                                    customSeekR.getProgress(),
                                    customSeekG.getProgress(),
                                    customSeekB.getProgress());
                            customColorHex.setText(String.format("%08X", color));
                        } else {
                            int color = Color.rgb(customSeekR.getProgress(),
                                    customSeekG.getProgress(),
                                    customSeekB.getProgress());
                            customColorHex.setText(String.format("%06X", 0xFFFFFF & color));
                        }
                    }
                    customSeekAValue.setText(String.format("%d", customSeekA.getProgress()));
                    customSeekRValue.setText(String.format("%d", customSeekR.getProgress()));
                    customSeekGValue.setText(String.format("%d", customSeekG.getProgress()));
                    customSeekBValue.setText(String.format("%d", customSeekB.getProgress()));
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

            customSeekR.setOnSeekBarChangeListener(customColorRgbListener);
            customSeekG.setOnSeekBarChangeListener(customColorRgbListener);
            customSeekB.setOnSeekBarChangeListener(customColorRgbListener);
            if (customSeekA.getVisibility() == View.VISIBLE) {
                customSeekA.setOnSeekBarChangeListener(customColorRgbListener);
                customColorHex.setText(String.format("%08X", selectedCustomColor));
            } else {
                customColorHex.setText(String.format("%06X", 0xFFFFFF & selectedCustomColor));
            }
        } else {
            dialog.setTitle(getBuilder().mTitle);
            dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().mCustomBtn);
            if (isInSub())
                dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mBackBtn);
            else dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mCancelBtn);
            grid.setVisibility(View.VISIBLE);
            colorChooserCustomFrame.setVisibility(View.GONE);
            customColorHex.removeTextChangedListener(customColorTextWatcher);
            customColorTextWatcher = null;
            customSeekR.setOnSeekBarChangeListener(null);
            customSeekG.setOnSeekBarChangeListener(null);
            customSeekB.setOnSeekBarChangeListener(null);
            customColorRgbListener = null;
        }
    }

    private void invalidate() {
        if (grid.getAdapter() == null) {
            grid.setAdapter(new ColorGridAdapter());
            grid.setSelector(ResourcesCompat.getDrawable(getResources(), R.drawable.md_transparent, null));
        } else ((BaseAdapter) grid.getAdapter()).notifyDataSetChanged();
        if (getDialog() != null)
            getDialog().setTitle(getTitle());
    }

    private class ColorGridAdapter extends BaseAdapter {

        ColorGridAdapter() {
        }

        @Override public int getCount() {
            if (isInSub()) return mColorsSub[topIndex()].length;
            else return mColorsTop.length;
        }

        @Override public Object getItem(int position) {
            if (isInSub()) return mColorsSub[topIndex()][position];
            else return mColorsTop[position];
        }

        @Override public long getItemId(int position) {
            return position;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new CircleView(getContext());
                convertView.setLayoutParams(new GridView.LayoutParams(circleSize, circleSize));
            }
            CircleView child = (CircleView) convertView;
            @ColorInt
            final int color = isInSub() ? mColorsSub[topIndex()][position] : mColorsTop[position];
            child.setBackgroundColor(color);
            if (isInSub())
                child.setSelected(subIndex() == position);
            else child.setSelected(topIndex() == position);
            child.setTag(String.format("%d:%d", position, color));
            child.setOnClickListener(ColorChooserDialog.this);
            child.setOnLongClickListener(ColorChooserDialog.this);
            return convertView;
        }
    }

    public static class Builder implements Serializable {

        @NonNull final transient AppCompatActivity mContext;
        @StringRes final int mTitle;
        @StringRes int mTitleSub;
        @ColorInt int mPreselect;
        @StringRes int mDoneBtn = R.string.md_done_label;
        @StringRes int mBackBtn = R.string.md_back_label;
        @StringRes int mCancelBtn = R.string.md_cancel_label;
        @StringRes int mCustomBtn = R.string.md_custom_label;
        @StringRes int mPresetsBtn = R.string.md_presets_label;
        @Nullable int[] mColorsTop;
        @Nullable int[][] mColorsSub;
        @Nullable String mTag;
        @Nullable Theme mTheme;

        boolean mAccentMode = false;
        boolean mDynamicButtonColor = true;
        boolean mAllowUserCustom = true;
        boolean mAllowUserCustomAlpha = true;
        boolean mSetPreselectionColor = false;

        public <ActivityType extends AppCompatActivity & ColorCallback> Builder(
                @NonNull ActivityType context, @StringRes int title) {
            mContext = context;
            mTitle = title;
        }

        @NonNull public Builder titleSub(@StringRes int titleSub) {
            mTitleSub = titleSub;
            return this;
        }

        @NonNull public Builder tag(@Nullable String tag) {
            mTag = tag;
            return this;
        }

        @NonNull public Builder theme(@NonNull Theme theme) {
            mTheme = theme;
            return this;
        }

        @NonNull public Builder preselect(@ColorInt int preselect) {
            mPreselect = preselect;
            mSetPreselectionColor = true;
            return this;
        }

        @NonNull public Builder accentMode(boolean accentMode) {
            mAccentMode = accentMode;
            return this;
        }

        @NonNull public Builder doneButton(@StringRes int text) {
            mDoneBtn = text;
            return this;
        }

        @NonNull public Builder backButton(@StringRes int text) {
            mBackBtn = text;
            return this;
        }

        @NonNull public Builder cancelButton(@StringRes int text) {
            mCancelBtn = text;
            return this;
        }

        @NonNull public Builder customButton(@StringRes int text) {
            mCustomBtn = text;
            return this;
        }

        @NonNull public Builder presetsButton(@StringRes int text) {
            mPresetsBtn = text;
            return this;
        }

        @NonNull public Builder dynamicButtonColor(boolean enabled) {
            mDynamicButtonColor = enabled;
            return this;
        }

        @NonNull public Builder customColors(@NonNull int[] topLevel, @Nullable int[][] subLevel) {
            mColorsTop = topLevel;
            mColorsSub = subLevel;
            return this;
        }

        @NonNull public Builder customColors(@ArrayRes int topLevel, @Nullable int[][] subLevel) {
            mColorsTop = DialogUtils.getColorArray(mContext, topLevel);
            mColorsSub = subLevel;
            return this;
        }

        @NonNull public Builder allowUserColorInput(boolean allow) {
            mAllowUserCustom = allow;
            return this;
        }

        @NonNull public Builder allowUserColorInputAlpha(boolean allow) {
            mAllowUserCustomAlpha = allow;
            return this;
        }

        @NonNull public ColorChooserDialog build() {
            ColorChooserDialog dialog = new ColorChooserDialog();
            Bundle args = new Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull public ColorChooserDialog show() {
            ColorChooserDialog dialog = build();
            dialog.show(mContext);
            return dialog;
        }
    }

    private Builder getBuilder() {
        if (getArguments() == null || !getArguments().containsKey("builder")) return null;
        return (Builder) getArguments().getSerializable("builder");
    }

    private void dismissIfNecessary(AppCompatActivity context, String tag) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null) {
            ((DialogFragment) frag).dismiss();
            context.getSupportFragmentManager().beginTransaction()
                    .remove(frag).commit();
        }
    }

    @Nullable public static ColorChooserDialog findVisible(
            @NonNull AppCompatActivity context, @ColorChooserTag String tag) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null && frag instanceof ColorChooserDialog)
            return (ColorChooserDialog) frag;
        return null;
    }

    @NonNull public ColorChooserDialog show(AppCompatActivity context) {
        String tag;
        Builder builder = getBuilder();
        if (builder.mColorsTop != null)
            tag = TAG_CUSTOM;
        else if (builder.mAccentMode)
            tag = TAG_ACCENT;
        else tag = TAG_PRIMARY;
        dismissIfNecessary(context, tag);
        show(context.getSupportFragmentManager(), tag);
        return this;
    }
}
