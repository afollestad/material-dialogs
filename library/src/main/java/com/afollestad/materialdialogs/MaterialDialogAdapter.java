package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.util.DialogUtils;

class MaterialDialogAdapter extends ArrayAdapter<CharSequence> {

    private final int itemColor;
    private final MaterialDialog dialog;
    private final GravityEnum itemGravity;

    public MaterialDialogAdapter(MaterialDialog dialog, int resource, int textViewResourceId, CharSequence[] objects) {
        super(dialog.mBuilder.context, resource, textViewResourceId, objects);
        this.dialog = dialog;
        this.itemGravity = dialog.mBuilder.itemsGravity;
        this.itemColor = DialogUtils.resolveColor(getContext(), R.attr.md_item_color, dialog.defaultItemColor);
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
        switch (dialog.listType) {
            case SINGLE: {
                @SuppressLint("CutPasteId")
                RadioButton radio = (RadioButton) view.findViewById(R.id.control);
                radio.setChecked(dialog.mBuilder.selectedIndex == index);
                break;
            }
            case MULTI: {
                @SuppressLint("CutPasteId")
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.control);
                checkbox.setChecked(dialog.selectedIndicesList.contains(index));
                break;
            }
        }
        tv.setText(dialog.mBuilder.items[index]);
        tv.setTextColor(itemColor);
        dialog.setTypeface(tv, dialog.mBuilder.regularFont);
        view.setTag(index + ":" + dialog.mBuilder.items[index]);
        setupGravity(view);
        return view;
    }

    private void setupGravity(View view) {
        if (view instanceof LinearLayout) {
            // Basic list item
            final LinearLayout itemRoot = (LinearLayout) view;
            final int gravityInt = MaterialDialog.gravityEnumToGravity(itemGravity);
            itemRoot.setGravity(gravityInt | Gravity.CENTER_VERTICAL);
            for (int i = 0; i < itemRoot.getChildCount(); i++)
                ((LinearLayout.LayoutParams) itemRoot.getChildAt(i).getLayoutParams()).gravity = gravityInt;
        } else {
            // Choice list item
            final RelativeLayout itemRoot = (RelativeLayout) view;
            for (int i = 0; i < itemRoot.getChildCount(); i++)
                setupGravityRelative(itemRoot.getChildAt(i), i);
        }
    }

    private void setupGravityRelative(View child, int index) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();

        // Layout alignment
        if (itemGravity == GravityEnum.CENTER) {
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (index == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(itemGravity == GravityEnum.START ?
                        RelativeLayout.ALIGN_PARENT_START : RelativeLayout.ALIGN_PARENT_END);
            } else {
                params.addRule(itemGravity == GravityEnum.START ?
                        RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_RIGHT);
            }
        }

        if (index == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Removes the circular background selector from checkboxes or radio buttons on Lollipop
            child.setBackground(null);
        }

        // Relative positioning
        if (index == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(itemGravity == GravityEnum.START || itemGravity == GravityEnum.CENTER ?
                        RelativeLayout.END_OF : RelativeLayout.START_OF, R.id.control);
            } else {
                params.addRule(itemGravity == GravityEnum.START || itemGravity == GravityEnum.CENTER ?
                        RelativeLayout.RIGHT_OF : RelativeLayout.LEFT_OF, R.id.control);
            }
        }

        // Margin
        final int frameMargin = (int) getContext().getResources().getDimension(R.dimen.md_dialog_frame_margin);
        final int controlMargin = (int) getContext().getResources().getDimension(R.dimen.md_listitem_control_margin);
        if (index == 0) {
            if (itemGravity == GravityEnum.START || itemGravity == GravityEnum.CENTER) {
                params.leftMargin = !isRTL() ? frameMargin : controlMargin;
                params.rightMargin = isRTL() ? frameMargin : controlMargin;
            } else {
                params.leftMargin = !isRTL() ? controlMargin : frameMargin;
                params.rightMargin = isRTL() ? controlMargin : frameMargin;
            }
        } else {
            if (itemGravity == GravityEnum.START || itemGravity == GravityEnum.CENTER) {
                params.leftMargin = !isRTL() ? 0 : frameMargin;
                params.rightMargin = isRTL() ? frameMargin : 0;
            } else {
                params.leftMargin = !isRTL() ? frameMargin : 0;
                params.rightMargin = isRTL() ? 0 : frameMargin;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRTL() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            return false;
        Configuration config = getContext().getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
}