package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.util.DialogUtils;

class MaterialDialogAdapter extends ArrayAdapter<CharSequence> {

    final int itemColor;
    final MaterialDialog dialog;

    public MaterialDialogAdapter(MaterialDialog dialog, int resource, int textViewResourceId, CharSequence[] objects) {
        super(dialog.mBuilder.context, resource, textViewResourceId, objects);
        this.dialog = dialog;
        itemColor = DialogUtils.resolveColor(getContext(), R.attr.md_item_color, dialog.defaultItemColor);
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
        return view;
    }
}