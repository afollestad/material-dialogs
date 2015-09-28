package com.afollestad.materialdialogs.simplelist;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.commons.R;
import com.afollestad.materialdialogs.internal.MDAdapter;

/**
 * See the sample project to understand how this is used. Mimics the Simple List dialog style
 * displayed on Google's guidelines site: https://www.google.com/design/spec/components/dialogs.html#dialogs-simple-dialogs
 *
 * @author Aidan Follestad (afollestad)
 */
public class MaterialSimpleListAdapter extends ArrayAdapter<MaterialSimpleListItem> implements MDAdapter {

    private MaterialDialog dialog;

    public MaterialSimpleListAdapter(Context context) {
        super(context, R.layout.md_simplelist_item, android.R.id.title);
    }

    @Override
    public void setDialog(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        final View view = super.getView(index, convertView, parent);
        if (dialog != null) {
            final MaterialSimpleListItem item = getItem(index);
            ImageView ic = (ImageView) view.findViewById(android.R.id.icon);
            if (item.getIcon() != null) {
                ic.setImageDrawable(item.getIcon());
                ic.setPadding(item.getIconPadding(), item.getIconPadding(),
                        item.getIconPadding(), item.getIconPadding());
                ic.getBackground().setColorFilter(item.getBackgroundColor(),
                        PorterDuff.Mode.SRC_ATOP);
            } else {
                ic.setVisibility(View.GONE);
            }
            TextView tv = (TextView) view.findViewById(android.R.id.title);
            tv.setTextColor(dialog.getBuilder().getItemColor());
            tv.setText(item.getContent());
            dialog.setTypeface(tv, dialog.getBuilder().getRegularFont());
        }
        return view;
    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    private boolean isRTL() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
//            return false;
//        Configuration config = getContext().getResources().getConfiguration();
//        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
//    }
}