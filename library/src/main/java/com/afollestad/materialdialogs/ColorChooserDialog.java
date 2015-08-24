package com.afollestad.materialdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
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

import java.io.Serializable;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings("FieldCanBeLocal")
public class ColorChooserDialog extends DialogFragment implements View.OnClickListener {

    private final int[] TOP_LEVEL_COLORS = new int[]{
            Color.parseColor("#F44336"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#673AB7"),
            Color.parseColor("#3F51B5"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#03A9F4"),
            Color.parseColor("#00BCD4"),
            Color.parseColor("#009688"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#8BC34A"),
            Color.parseColor("#CDDC39"),
            Color.parseColor("#FFEB3B"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#FF5722"),
            Color.parseColor("#795548"),
            Color.parseColor("#9E9E9E"),
            Color.parseColor("#607D8B")
    };
    private final int[][] SUB_LEVEL_COLORS = new int[][]{
            new int[]{
                    Color.parseColor("#FFEBEE"),
                    Color.parseColor("#FFCDD2"),
                    Color.parseColor("#EF9A9A"),
                    Color.parseColor("#E57373"),
                    Color.parseColor("#EF5350"),
                    Color.parseColor("#F44336"),
                    Color.parseColor("#E53935"),
                    Color.parseColor("#D32F2F"),
                    Color.parseColor("#C62828"),
                    Color.parseColor("#B71C1C")
            },
            new int[]{
                    Color.parseColor("#FCE4EC"),
                    Color.parseColor("#F8BBD0"),
                    Color.parseColor("#F48FB1"),
                    Color.parseColor("#F06292"),
                    Color.parseColor("#EC407A"),
                    Color.parseColor("#E91E63"),
                    Color.parseColor("#D81B60"),
                    Color.parseColor("#C2185B"),
                    Color.parseColor("#AD1457"),
                    Color.parseColor("#880E4F")
            },
            new int[]{
                    Color.parseColor("#F3E5F5"),
                    Color.parseColor("#E1BEE7"),
                    Color.parseColor("#CE93D8"),
                    Color.parseColor("#BA68C8"),
                    Color.parseColor("#AB47BC"),
                    Color.parseColor("#9C27B0"),
                    Color.parseColor("#8E24AA"),
                    Color.parseColor("#7B1FA2"),
                    Color.parseColor("#6A1B9A"),
                    Color.parseColor("#4A148C")
            },
            new int[]{
                    Color.parseColor("#EDE7F6"),
                    Color.parseColor("#D1C4E9"),
                    Color.parseColor("#B39DDB"),
                    Color.parseColor("#9575CD"),
                    Color.parseColor("#7E57C2"),
                    Color.parseColor("#673AB7"),
                    Color.parseColor("#5E35B1"),
                    Color.parseColor("#512DA8"),
                    Color.parseColor("#4527A0"),
                    Color.parseColor("#311B92")
            },
            new int[]{
                    Color.parseColor("#E8EAF6"),
                    Color.parseColor("#C5CAE9"),
                    Color.parseColor("#9FA8DA"),
                    Color.parseColor("#7986CB"),
                    Color.parseColor("#5C6BC0"),
                    Color.parseColor("#3F51B5"),
                    Color.parseColor("#3949AB"),
                    Color.parseColor("#303F9F"),
                    Color.parseColor("#283593"),
                    Color.parseColor("#1A237E")
            },
            new int[]{
                    Color.parseColor("#E3F2FD"),
                    Color.parseColor("#BBDEFB"),
                    Color.parseColor("#90CAF9"),
                    Color.parseColor("#64B5F6"),
                    Color.parseColor("#42A5F5"),
                    Color.parseColor("#2196F3"),
                    Color.parseColor("#1E88E5"),
                    Color.parseColor("#1976D2"),
                    Color.parseColor("#1565C0"),
                    Color.parseColor("#0D47A1")
            },
            new int[]{
                    Color.parseColor("#E1F5FE"),
                    Color.parseColor("#B3E5FC"),
                    Color.parseColor("#81D4FA"),
                    Color.parseColor("#4FC3F7"),
                    Color.parseColor("#29B6F6"),
                    Color.parseColor("#03A9F4"),
                    Color.parseColor("#039BE5"),
                    Color.parseColor("#0288D1"),
                    Color.parseColor("#0277BD"),
                    Color.parseColor("#01579B")
            },
            new int[]{
                    Color.parseColor("#E0F7FA"),
                    Color.parseColor("#B2EBF2"),
                    Color.parseColor("#80DEEA"),
                    Color.parseColor("#4DD0E1"),
                    Color.parseColor("#26C6DA"),
                    Color.parseColor("#00BCD4"),
                    Color.parseColor("#00ACC1"),
                    Color.parseColor("#0097A7"),
                    Color.parseColor("#00838F"),
                    Color.parseColor("#006064")
            },
            new int[]{
                    Color.parseColor("#E0F2F1"),
                    Color.parseColor("#B2DFDB"),
                    Color.parseColor("#80CBC4"),
                    Color.parseColor("#4DB6AC"),
                    Color.parseColor("#26A69A"),
                    Color.parseColor("#009688"),
                    Color.parseColor("#00897B"),
                    Color.parseColor("#00796B"),
                    Color.parseColor("#00695C"),
                    Color.parseColor("#004D40")
            },
            new int[]{
                    Color.parseColor("#E8F5E9"),
                    Color.parseColor("#C8E6C9"),
                    Color.parseColor("#A5D6A7"),
                    Color.parseColor("#81C784"),
                    Color.parseColor("#66BB6A"),
                    Color.parseColor("#4CAF50"),
                    Color.parseColor("#43A047"),
                    Color.parseColor("#388E3C"),
                    Color.parseColor("#2E7D32"),
                    Color.parseColor("#1B5E20")
            },
            new int[]{
                    Color.parseColor("#F1F8E9"),
                    Color.parseColor("#DCEDC8"),
                    Color.parseColor("#C5E1A5"),
                    Color.parseColor("#AED581"),
                    Color.parseColor("#9CCC65"),
                    Color.parseColor("#8BC34A"),
                    Color.parseColor("#7CB342"),
                    Color.parseColor("#689F38"),
                    Color.parseColor("#558B2F"),
                    Color.parseColor("#33691E")
            },
            new int[]{
                    Color.parseColor("#F9FBE7"),
                    Color.parseColor("#F0F4C3"),
                    Color.parseColor("#E6EE9C"),
                    Color.parseColor("#DCE775"),
                    Color.parseColor("#D4E157"),
                    Color.parseColor("#CDDC39"),
                    Color.parseColor("#C0CA33"),
                    Color.parseColor("#AFB42B"),
                    Color.parseColor("#9E9D24"),
                    Color.parseColor("#827717")
            },
            new int[]{
                    Color.parseColor("#FFFDE7"),
                    Color.parseColor("#FFF9C4"),
                    Color.parseColor("#FFF59D"),
                    Color.parseColor("#FFF176"),
                    Color.parseColor("#FFEE58"),
                    Color.parseColor("#FFEB3B"),
                    Color.parseColor("#FDD835"),
                    Color.parseColor("#FBC02D"),
                    Color.parseColor("#F9A825"),
                    Color.parseColor("#F57F17")
            },
            new int[]{
                    Color.parseColor("#FFF8E1"),
                    Color.parseColor("#FFECB3"),
                    Color.parseColor("#FFE082"),
                    Color.parseColor("#FFD54F"),
                    Color.parseColor("#FFCA28"),
                    Color.parseColor("#FFC107"),
                    Color.parseColor("#FFB300"),
                    Color.parseColor("#FFA000"),
                    Color.parseColor("#FF8F00"),
                    Color.parseColor("#FF6F00")
            },
            new int[]{
                    Color.parseColor("#FFF3E0"),
                    Color.parseColor("#FFE0B2"),
                    Color.parseColor("#FFCC80"),
                    Color.parseColor("#FFB74D"),
                    Color.parseColor("#FFA726"),
                    Color.parseColor("#FF9800"),
                    Color.parseColor("#FB8C00"),
                    Color.parseColor("#F57C00"),
                    Color.parseColor("#EF6C00"),
                    Color.parseColor("#E65100")
            },
            new int[]{
                    Color.parseColor("#FBE9E7"),
                    Color.parseColor("#FFCCBC"),
                    Color.parseColor("#FFAB91"),
                    Color.parseColor("#FF8A65"),
                    Color.parseColor("#FF7043"),
                    Color.parseColor("#FF5722"),
                    Color.parseColor("#F4511E"),
                    Color.parseColor("#E64A19"),
                    Color.parseColor("#D84315"),
                    Color.parseColor("#BF360C")
            },
            new int[]{
                    Color.parseColor("#EFEBE9"),
                    Color.parseColor("#D7CCC8"),
                    Color.parseColor("#BCAAA4"),
                    Color.parseColor("#A1887F"),
                    Color.parseColor("#8D6E63"),
                    Color.parseColor("#795548"),
                    Color.parseColor("#6D4C41"),
                    Color.parseColor("#5D4037"),
                    Color.parseColor("#4E342E"),
                    Color.parseColor("#3E2723")
            },
            new int[]{
                    Color.parseColor("#FAFAFA"),
                    Color.parseColor("#F5F5F5"),
                    Color.parseColor("#EEEEEE"),
                    Color.parseColor("#E0E0E0"),
                    Color.parseColor("#BDBDBD"),
                    Color.parseColor("#9E9E9E"),
                    Color.parseColor("#757575"),
                    Color.parseColor("#616161"),
                    Color.parseColor("#424242"),
                    Color.parseColor("#212121")
            },
            new int[]{
                    Color.parseColor("#ECEFF1"),
                    Color.parseColor("#CFD8DC"),
                    Color.parseColor("#B0BEC5"),
                    Color.parseColor("#90A4AE"),
                    Color.parseColor("#78909C"),
                    Color.parseColor("#607D8B"),
                    Color.parseColor("#546E7A"),
                    Color.parseColor("#455A64"),
                    Color.parseColor("#37474F"),
                    Color.parseColor("#263238")
            }
    };

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
            subIndex(5);
        getArguments().putInt("top_index", value);
    }

    private int subIndex() {
        return getArguments().getInt("sub_index", -1);
    }

    private void subIndex(int value) {
        getArguments().putInt("sub_index", value);
    }

    private int preselectTop() {
        return getBuilder().mPreselectTopLevel;
    }

    private void preselectTop(int value) {
        setBuilder(getBuilder().preselect(value, getBuilder().mPreselectTopLevel));
    }

    private int preselectSub() {
        return getBuilder().mPreselectSubLevel;
    }

    private void preselectSub(int value) {
        setBuilder(getBuilder().preselect(getBuilder().mPreselectTopLevel, value));
    }

    @StringRes
    private int title() {
        Builder builder = getBuilder();
        int title;
        if (isInSub()) title = builder.mTitleSub;
        else title = builder.mTitle;
        if (title == 0) title = builder.mTitle;
        return title;
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
        void onColorSelection(@StringRes int dialogTitle, @ColorInt int topLevelColor, @ColorInt int subLevelColor);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getBuilder() == null)
            throw new IllegalStateException("ColorChooserDialog should be created using its Builder interface.");
        final int preselectTop = preselectTop();
        if (preselectTop != 0) {
            for (int i = 0; i < TOP_LEVEL_COLORS.length; i++) {
                if (TOP_LEVEL_COLORS[i] == preselectTop) {
                    topIndex(i);
                    final int preselectSub = preselectSub();
                    if (preselectSub != 0) {
                        for (int b = 0; b < SUB_LEVEL_COLORS[i].length; b++) {
                            if (preselectSub == SUB_LEVEL_COLORS[i][b]) {
                                subIndex(b);
                                break;
                            }
                        }
                        preselectSub(0);
                    }
                    break;
                }
            }
            preselectTop(0);
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
                .title(getArguments().getInt("title", 0))
                .autoDismiss(false)
                .customView(mGrid, false)
                .positiveText(R.string.done)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        int topLevelColor = 0;
                        int subLevelColor = 0;
                        if (topIndex() > -1)
                            topLevelColor = TOP_LEVEL_COLORS[topIndex()];
                        if (subIndex() > -1)
                            subLevelColor = SUB_LEVEL_COLORS[topIndex()][subIndex()];
                        mCallback.onColorSelection(getArguments().getInt("title", 0), topLevelColor, subLevelColor);
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
        getDialog().setTitle(title());
    }

    private class ColorGridAdapter extends BaseAdapter {

        public ColorGridAdapter() {
        }

        @Override
        public int getCount() {
            if (isInSub()) {
                return SUB_LEVEL_COLORS[topIndex()].length;
            } else {
                return TOP_LEVEL_COLORS.length;
            }
        }

        @Override
        public Object getItem(int position) {
            if (isInSub()) {
                return SUB_LEVEL_COLORS[topIndex()][position];
            } else {
                return TOP_LEVEL_COLORS[position];
            }
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
            if (isInSub()) {
                child.setBackgroundColor(SUB_LEVEL_COLORS[topIndex()][position]);
                child.setSelected(subIndex() == position);
            } else {
                child.setBackgroundColor(TOP_LEVEL_COLORS[position]);
                child.setSelected(topIndex() == position);
            }
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
        protected int mPreselectTopLevel;
        @ColorInt
        protected int mPreselectSubLevel;

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
        public Builder preselect(@ColorInt int topLevelColor, @ColorInt int subLevelColor) {
            mPreselectTopLevel = topLevelColor;
            mPreselectSubLevel = subLevelColor;
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

    public Builder getBuilder() {
        if (getArguments() == null || !getArguments().containsKey("builder")) return null;
        return (Builder) getArguments().getSerializable("builder");
    }

    public void setBuilder(Builder builder) {
        getArguments().putSerializable("builder", builder);
    }

    @NonNull
    public ColorChooserDialog show(AppCompatActivity context) {
        show(context.getSupportFragmentManager(), "[MD_COLOR_CHOOSER]");
        return this;
    }
}
