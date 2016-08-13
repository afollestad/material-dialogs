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

    private int mCircleSize;
    private ColorCallback mCallback;
    private GridView mGrid;

    private View mColorChooserCustomFrame;
    private EditText mCustomColorHex;
    private View mCustomColorIndicator;
    private TextWatcher mCustomColorTextWatcher;
    private SeekBar mCustomSeekA;
    private TextView mCustomSeekAValue;
    private SeekBar mCustomSeekR;
    private TextView mCustomSeekRValue;
    private SeekBar mCustomSeekG;
    private TextView mCustomSeekGValue;
    private SeekBar mCustomSeekB;
    private TextView mCustomSeekBValue;
    private SeekBar.OnSeekBarChangeListener mCustomColorRgbListener;
    private int mSelectedCustomColor;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("top_index", topIndex());
        outState.putBoolean("in_sub", isInSub());
        outState.putInt("sub_index", subIndex());
        outState.putBoolean("in_custom", mColorChooserCustomFrame != null &&
                mColorChooserCustomFrame.getVisibility() == View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ColorCallback))
            throw new IllegalStateException("ColorChooserDialog needs to be shown from an Activity implementing ColorCallback.");
        mCallback = (ColorCallback) activity;
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

    @StringRes
    public int getTitle() {
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

    @Override
    public void onClick(View v) {
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
                mSelectedCustomColor = getSelectedColor();
            invalidateDynamicButtonColors();
            invalidate();
        }
    }

    @Override
    public boolean onLongClick(View v) {
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

            if (mCustomSeekR != null) {
                if (mCustomSeekA.getVisibility() == View.VISIBLE)
                    MDTintHelper.setTint(mCustomSeekA, selectedColor);
                MDTintHelper.setTint(mCustomSeekR, selectedColor);
                MDTintHelper.setTint(mCustomSeekG, selectedColor);
                MDTintHelper.setTint(mCustomSeekB, selectedColor);
            }
        }
    }

    @ColorInt
    private int getSelectedColor() {
        if (mColorChooserCustomFrame != null && mColorChooserCustomFrame.getVisibility() == View.VISIBLE)
            return mSelectedCustomColor;

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

        mCircleSize = getResources().getDimensionPixelSize(R.dimen.md_colorchooser_circlesize);
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
                        mCallback.onColorSelection(ColorChooserDialog.this, getSelectedColor());
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
        mGrid = (GridView) v.findViewById(R.id.md_grid);

        if (builder.mAllowUserCustom) {
            mSelectedCustomColor = preselectColor;
            mColorChooserCustomFrame = v.findViewById(R.id.md_colorChooserCustomFrame);
            mCustomColorHex = (EditText) v.findViewById(R.id.md_hexInput);
            mCustomColorIndicator = v.findViewById(R.id.md_colorIndicator);
            mCustomSeekA = (SeekBar) v.findViewById(R.id.md_colorA);
            mCustomSeekAValue = (TextView) v.findViewById(R.id.md_colorAValue);
            mCustomSeekR = (SeekBar) v.findViewById(R.id.md_colorR);
            mCustomSeekRValue = (TextView) v.findViewById(R.id.md_colorRValue);
            mCustomSeekG = (SeekBar) v.findViewById(R.id.md_colorG);
            mCustomSeekGValue = (TextView) v.findViewById(R.id.md_colorGValue);
            mCustomSeekB = (SeekBar) v.findViewById(R.id.md_colorB);
            mCustomSeekBValue = (TextView) v.findViewById(R.id.md_colorBValue);

            if (!builder.mAllowUserCustomAlpha) {
                v.findViewById(R.id.md_colorALabel).setVisibility(View.GONE);
                mCustomSeekA.setVisibility(View.GONE);
                mCustomSeekAValue.setVisibility(View.GONE);
                mCustomColorHex.setHint("2196F3");
                mCustomColorHex.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            } else {
                mCustomColorHex.setHint("FF2196F3");
                mCustomColorHex.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            }

            if (!foundPreselectColor) {
                // If color wasn't found in the preset colors, it must be custom
                toggleCustom(dialog);
            }
        }

        invalidate();
        return dialog;
    }

    private void toggleCustom(MaterialDialog dialog) {
        if (dialog == null)
            dialog = (MaterialDialog) getDialog();
        if (mGrid.getVisibility() == View.VISIBLE) {
            dialog.setTitle(getBuilder().mCustomBtn);
            dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().mPresetsBtn);
            dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mCancelBtn);
            mGrid.setVisibility(View.INVISIBLE);
            mColorChooserCustomFrame.setVisibility(View.VISIBLE);

            mCustomColorTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        mSelectedCustomColor = Color.parseColor("#" + s.toString());
                    } catch (IllegalArgumentException e) {
                        mSelectedCustomColor = Color.BLACK;
                    }
                    mCustomColorIndicator.setBackgroundColor(mSelectedCustomColor);
                    if (mCustomSeekA.getVisibility() == View.VISIBLE) {
                        int alpha = Color.alpha(mSelectedCustomColor);
                        mCustomSeekA.setProgress(alpha);
                        mCustomSeekAValue.setText(String.format("%d", alpha));
                    }
                    if (mCustomSeekA.getVisibility() == View.VISIBLE) {
                        int alpha = Color.alpha(mSelectedCustomColor);
                        mCustomSeekA.setProgress(alpha);
                    }
                    int red = Color.red(mSelectedCustomColor);
                    mCustomSeekR.setProgress(red);
                    int green = Color.green(mSelectedCustomColor);
                    mCustomSeekG.setProgress(green);
                    int blue = Color.blue(mSelectedCustomColor);
                    mCustomSeekB.setProgress(blue);
                    isInSub(false);
                    topIndex(-1);
                    subIndex(-1);
                    invalidateDynamicButtonColors();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

            mCustomColorHex.addTextChangedListener(mCustomColorTextWatcher);
            mCustomColorRgbListener = new SeekBar.OnSeekBarChangeListener() {

                @SuppressLint("DefaultLocale")
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        if (getBuilder().mAllowUserCustomAlpha) {
                            int color = Color.argb(mCustomSeekA.getProgress(),
                                    mCustomSeekR.getProgress(),
                                    mCustomSeekG.getProgress(),
                                    mCustomSeekB.getProgress());
                            mCustomColorHex.setText(String.format("%08X", color));
                        } else {
                            int color = Color.rgb(mCustomSeekR.getProgress(),
                                    mCustomSeekG.getProgress(),
                                    mCustomSeekB.getProgress());
                            mCustomColorHex.setText(String.format("%06X", 0xFFFFFF & color));
                        }
                    }
                    mCustomSeekAValue.setText(String.format("%d", mCustomSeekA.getProgress()));
                    mCustomSeekRValue.setText(String.format("%d", mCustomSeekR.getProgress()));
                    mCustomSeekGValue.setText(String.format("%d", mCustomSeekG.getProgress()));
                    mCustomSeekBValue.setText(String.format("%d", mCustomSeekB.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

            mCustomSeekR.setOnSeekBarChangeListener(mCustomColorRgbListener);
            mCustomSeekG.setOnSeekBarChangeListener(mCustomColorRgbListener);
            mCustomSeekB.setOnSeekBarChangeListener(mCustomColorRgbListener);
            if (mCustomSeekA.getVisibility() == View.VISIBLE) {
                mCustomSeekA.setOnSeekBarChangeListener(mCustomColorRgbListener);
                mCustomColorHex.setText(String.format("%08X", mSelectedCustomColor));
            } else {
                mCustomColorHex.setText(String.format("%06X", 0xFFFFFF & mSelectedCustomColor));
            }
        } else {
            dialog.setTitle(getBuilder().mTitle);
            dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().mCustomBtn);
            if (isInSub())
                dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mBackBtn);
            else dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mCancelBtn);
            mGrid.setVisibility(View.VISIBLE);
            mColorChooserCustomFrame.setVisibility(View.GONE);
            mCustomColorHex.removeTextChangedListener(mCustomColorTextWatcher);
            mCustomColorTextWatcher = null;
            mCustomSeekR.setOnSeekBarChangeListener(null);
            mCustomSeekG.setOnSeekBarChangeListener(null);
            mCustomSeekB.setOnSeekBarChangeListener(null);
            mCustomColorRgbListener = null;
        }
    }

    private void invalidate() {
        if (mGrid.getAdapter() == null) {
            mGrid.setAdapter(new ColorGridAdapter());
            mGrid.setSelector(ResourcesCompat.getDrawable(getResources(), R.drawable.md_transparent, null));
        } else ((BaseAdapter) mGrid.getAdapter()).notifyDataSetChanged();
        if (getDialog() != null)
            getDialog().setTitle(getTitle());
    }

    private class ColorGridAdapter extends BaseAdapter {

        public ColorGridAdapter() {
        }

        @Override
        public int getCount() {
            if (isInSub()) return mColorsSub[topIndex()].length;
            else return mColorsTop.length;
        }

        @Override
        public Object getItem(int position) {
            if (isInSub()) return mColorsSub[topIndex()][position];
            else return mColorsTop[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new CircleView(getContext());
                convertView.setLayoutParams(new GridView.LayoutParams(mCircleSize, mCircleSize));
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

        @NonNull
        protected final transient AppCompatActivity mContext;
        @StringRes
        protected final int mTitle;
        @StringRes
        protected int mTitleSub;
        @ColorInt
        protected int mPreselect;
        @StringRes
        protected int mDoneBtn = R.string.md_done_label;
        @StringRes
        protected int mBackBtn = R.string.md_back_label;
        @StringRes
        protected int mCancelBtn = R.string.md_cancel_label;
        @StringRes
        protected int mCustomBtn = R.string.md_custom_label;
        @StringRes
        protected int mPresetsBtn = R.string.md_presets_label;
        @Nullable
        protected int[] mColorsTop;
        @Nullable
        protected int[][] mColorsSub;
        @Nullable
        protected String mTag;
        @Nullable
        protected Theme mTheme;

        protected boolean mAccentMode = false;
        protected boolean mDynamicButtonColor = true;
        protected boolean mAllowUserCustom = true;
        protected boolean mAllowUserCustomAlpha = true;
        protected boolean mSetPreselectionColor = false;

        public <ActivityType extends AppCompatActivity & ColorCallback> Builder(@NonNull ActivityType context, @StringRes int title) {
            mContext = context;
            mTitle = title;
        }

        @NonNull
        public Builder titleSub(@StringRes int titleSub) {
            mTitleSub = titleSub;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            mTag = tag;
            return this;
        }

        @NonNull
        public Builder theme(@NonNull Theme theme) {
            mTheme = theme;
            return this;
        }

        @NonNull
        public Builder preselect(@ColorInt int preselect) {
            mPreselect = preselect;
            mSetPreselectionColor = true;
            return this;
        }

        @NonNull
        public Builder accentMode(boolean accentMode) {
            mAccentMode = accentMode;
            return this;
        }

        @NonNull
        public Builder doneButton(@StringRes int text) {
            mDoneBtn = text;
            return this;
        }

        @NonNull
        public Builder backButton(@StringRes int text) {
            mBackBtn = text;
            return this;
        }

        @NonNull
        public Builder cancelButton(@StringRes int text) {
            mCancelBtn = text;
            return this;
        }

        @NonNull
        public Builder customButton(@StringRes int text) {
            mCustomBtn = text;
            return this;
        }

        @NonNull
        public Builder presetsButton(@StringRes int text) {
            mPresetsBtn = text;
            return this;
        }

        @NonNull
        public Builder dynamicButtonColor(boolean enabled) {
            mDynamicButtonColor = enabled;
            return this;
        }

        @NonNull
        public Builder customColors(@NonNull int[] topLevel, @Nullable int[][] subLevel) {
            mColorsTop = topLevel;
            mColorsSub = subLevel;
            return this;
        }

        @NonNull
        public Builder customColors(@ArrayRes int topLevel, @Nullable int[][] subLevel) {
            mColorsTop = DialogUtils.getColorArray(mContext, topLevel);
            mColorsSub = subLevel;
            return this;
        }

        @NonNull
        public Builder allowUserColorInput(boolean allow) {
            mAllowUserCustom = allow;
            return this;
        }

        @NonNull
        public Builder allowUserColorInputAlpha(boolean allow) {
            mAllowUserCustomAlpha = allow;
            return this;
        }

        @NonNull
        public ColorChooserDialog build() {
            ColorChooserDialog dialog = new ColorChooserDialog();
            Bundle args = new Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        public ColorChooserDialog show() {
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

    @Nullable
    public static ColorChooserDialog findVisible(@NonNull AppCompatActivity context, @ColorChooserTag String tag) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null && frag instanceof ColorChooserDialog)
            return (ColorChooserDialog) frag;
        return null;
    }

    @NonNull
    public ColorChooserDialog show(AppCompatActivity context) {
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
