package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;

class DefaultAdapter extends BaseAdapter {

    private final MaterialDialog dialog;
    @LayoutRes
    private final int layout;

    private final GravityEnum itemGravity;

    public DefaultAdapter(MaterialDialog dialog, @LayoutRes int layout) {
        this.dialog = dialog;
        this.layout = layout;
        this.itemGravity = dialog.mBuilder.itemsGravity;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return dialog.mBuilder.items != null ? dialog.mBuilder.items.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return dialog.mBuilder.items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View getView(final int index, View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(dialog.getContext()).inflate(layout, parent, false);
        boolean disabled = DialogUtils.isIn(index, dialog.mBuilder.disabledIndices);

        TextView tv = (TextView) view.findViewById(R.id.title);
        switch (dialog.listType) {
            case SINGLE: {
                @SuppressLint("CutPasteId")
                RadioButton radio = (RadioButton) view.findViewById(R.id.control);
                boolean selected = dialog.mBuilder.selectedIndex == index;
                MDTintHelper.setTint(radio, dialog.mBuilder.widgetColor);
                radio.setChecked(selected);
                radio.setEnabled(!disabled);
                break;
            }
            case MULTI: {
                @SuppressLint("CutPasteId")
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.control);
                boolean selected = dialog.selectedIndicesList.contains(index);
                MDTintHelper.setTint(checkbox, dialog.mBuilder.widgetColor);
                checkbox.setChecked(selected);
                checkbox.setEnabled(!disabled);
                break;
            }
        }

        tv.setText(dialog.mBuilder.items[index]);
        tv.setTextColor(dialog.mBuilder.itemColor);
        dialog.setTypeface(tv, dialog.mBuilder.regularFont);

        view.setTag(index + ":" + dialog.mBuilder.items[index]);
        setupGravity((ViewGroup) view);

        if (dialog.mBuilder.itemIds != null) {
            if (index < dialog.mBuilder.itemIds.length)
                view.setId(dialog.mBuilder.itemIds[index]);
            else view.setId(-1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup group = (ViewGroup) view;
            if (group.getChildCount() == 2) {
                // Remove circular selector from check boxes and radio buttons on Lollipop
                if (group.getChildAt(0) instanceof CompoundButton)
                    group.getChildAt(0).setBackground(null);
                else if (group.getChildAt(1) instanceof CompoundButton)
                    group.getChildAt(1).setBackground(null);
            }
        }

        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setupGravity(ViewGroup view) {
        final LinearLayout itemRoot = (LinearLayout) view;
        final int gravityInt = itemGravity.getGravityInt();
        itemRoot.setGravity(gravityInt | Gravity.CENTER_VERTICAL);

        if (view.getChildCount() == 2) {
            if (itemGravity == GravityEnum.END && !isRTL() && view.getChildAt(0) instanceof CompoundButton) {
                CompoundButton first = (CompoundButton) view.getChildAt(0);
                view.removeView(first);

                TextView second = (TextView) view.getChildAt(0);
                view.removeView(second);
                second.setPadding(second.getPaddingRight(), second.getPaddingTop(),
                        second.getPaddingLeft(), second.getPaddingBottom());

                view.addView(second);
                view.addView(first);
            } else if (itemGravity == GravityEnum.START && isRTL() && view.getChildAt(1) instanceof CompoundButton) {
                CompoundButton first = (CompoundButton) view.getChildAt(1);
                view.removeView(first);

                TextView second = (TextView) view.getChildAt(0);
                view.removeView(second);
                second.setPadding(second.getPaddingRight(), second.getPaddingTop(),
                        second.getPaddingRight(), second.getPaddingBottom());

                view.addView(first);
                view.addView(second);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRTL() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            return false;
        Configuration config = dialog.getBuilder().getContext().getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
}