package com.afollestad.materialdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.afollestad.materialdialogs.internal.CircleView;
import com.afollestad.materialdialogs.internal.ColorPalette;

import java.io.Serializable;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings({"FieldCanBeLocal", "ConstantConditions"})
public class ColorChooserDialog extends DialogFragment implements View.OnClickListener {

    private int[] mColorsTop;
    private int[][] mColorsSub;

    private void generateColors() {
        if (getBuilder().mAccentMode) {
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
        if (topIndex() != value)
            subIndex(getBuilder().mAccentMode ? 2 : 5);
        getArguments().putInt("top_index", value);
    }

    private int subIndex() {
        return getArguments().getInt("sub_index", -1);
    }

    private void subIndex(int value) {
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

    public boolean isAccentMode() {
        return getBuilder().mAccentMode;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            final int index = (Integer) v.getTag();
            if (isInSub()) {
                subIndex(index);
            } else {
                ((MaterialDialog) getDialog()).setActionButton(DialogAction.NEUTRAL, R.string.back);
                topIndex(index);
                isInSub(true);
            }
            invalidate();
        }
    }

    public interface ColorCallback {
        void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getBuilder() == null)
            throw new IllegalStateException("ColorChooserDialog should be created using its Builder interface.");
        generateColors();

        final int preselectColor = getBuilder().mPreselect;
        if (preselectColor != 0) {
            for (int topIndex = 0; topIndex < mColorsTop.length; topIndex++) {
                if (mColorsTop[topIndex] == preselectColor) {
                    topIndex(topIndex);
                    if (getBuilder().mAccentMode) {
                        subIndex(2);
                    } else subIndex(5);
                    break;
                }

                boolean found = false;
                for (int subIndex = 0; subIndex < mColorsSub[topIndex].length; subIndex++) {
                    if (mColorsSub[topIndex][subIndex] == preselectColor) {
                        topIndex(topIndex);
                        subIndex(subIndex);
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
        }

        final DisplayMetrics dm = getResources().getDisplayMetrics();
        mCircleSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, dm);
        mGrid = new GridView(getContext());
        mGrid.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mGrid.setColumnWidth(mCircleSize);
        mGrid.setNumColumns(GridView.AUTO_FIT);
        final int eightDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, dm);
        mGrid.setVerticalSpacing(eightDp);
        mGrid.setHorizontalSpacing(eightDp);
        final int sixteenDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, dm);
        mGrid.setPadding(sixteenDp, sixteenDp, sixteenDp, sixteenDp);
        mGrid.setClipToPadding(false);
        mGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mGrid.setGravity(Gravity.CENTER);

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(getTitle())
                .autoDismiss(false)
                .customView(mGrid, false)
                .positiveText(R.string.done)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        int color = 0;
                        if (subIndex() > -1)
                            color = mColorsSub[topIndex()][subIndex()];
                        else if (topIndex() > -1)
                            color = mColorsTop[topIndex()];
                        mCallback.onColorSelection(ColorChooserDialog.this, color);
                        dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                        dialog.setActionButton(DialogAction.NEUTRAL, null);
                        isInSub(false);
                        invalidate();
                    }
                }).build();
        invalidate();
        return dialog;
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new CircleView(getContext());
                convertView.setLayoutParams(new GridView.LayoutParams(mCircleSize, mCircleSize));
            }
            CircleView child = (CircleView) convertView;
            final int color = isInSub() ? mColorsSub[topIndex()][position] : mColorsTop[position];
            child.setBackgroundColor(color);
            if (isInSub())
                child.setSelected(subIndex() == position);
            else child.setSelected(topIndex() == position);
            child.setTag(position);
            child.setOnClickListener(ColorChooserDialog.this);
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
        protected boolean mAccentMode;

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
        public Builder preselect(@ColorInt int preselect) {
            mPreselect = preselect;
            return this;
        }

        @NonNull
        public Builder accentMode(boolean accentMode) {
            mAccentMode = accentMode;
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

    @NonNull
    public ColorChooserDialog show(AppCompatActivity context) {
        show(context.getSupportFragmentManager(), "[MD_COLOR_CHOOSER]");
        return this;
    }
}
