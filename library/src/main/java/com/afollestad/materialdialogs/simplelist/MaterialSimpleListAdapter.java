package com.afollestad.materialdialogs.simplelist;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialSimpleListAdapter extends ArrayAdapter<MaterialSimpleListItem> {

    private MaterialDialog dialog;

    public void setDialog(MaterialDialog dialog) {
        setDialog(dialog, true);
    }

    public void setDialog(MaterialDialog dialog, boolean notifyDataSetChanged) {
        this.dialog = dialog;
        if (notifyDataSetChanged)
            notifyDataSetChanged();
    }

    public MaterialSimpleListAdapter(Context context) {
        super(context, R.layout.md_simplelist_item, android.R.id.title);
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
            if (item.getIcon() != null)
                ic.setImageDrawable(item.getIcon());
            else
                ic.setVisibility(View.GONE);
            TextView tv = (TextView) view.findViewById(android.R.id.title);
            tv.setTextColor(dialog.getBuilder().getItemColor());
            tv.setText(item.getContent());
            dialog.setTypeface(tv, dialog.getBuilder().getRegularFont());
            setupGravity((ViewGroup) view);
        }
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setupGravity(ViewGroup view) {
        final LinearLayout itemRoot = (LinearLayout) view;
        final GravityEnum gravity = dialog.getBuilder().getItemsGravity();
        final int gravityInt = gravity.getGravityInt();
        itemRoot.setGravity(gravityInt | Gravity.CENTER_VERTICAL);

        if (view.getChildCount() == 2) {
            if (dialog.getBuilder().getItemsGravity() == GravityEnum.END && !isRTL() && view.getChildAt(0) instanceof ImageView) {
                CompoundButton first = (CompoundButton) view.getChildAt(0);
                view.removeView(first);

                TextView second = (TextView) view.getChildAt(0);
                view.removeView(second);
                second.setPadding(second.getPaddingRight(), second.getPaddingTop(),
                        second.getPaddingLeft(), second.getPaddingBottom());

                view.addView(second);
                view.addView(first);
            } else if (gravity == GravityEnum.START && isRTL() && view.getChildAt(1) instanceof ImageView) {
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
        Configuration config = getContext().getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
}