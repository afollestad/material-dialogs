package com.afollestad.materialdialogssample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ArrayRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Simple adapter example for custom items in the dialog
 */
class ButtonItemAdapter extends BaseAdapter implements View.OnClickListener {

    private Toast mToast;
    private final Context mContext;
    private final CharSequence[] mItems;

    public ButtonItemAdapter(Context context, @ArrayRes int arrayResId) {
        this(context, context.getResources().getTextArray(arrayResId));
    }

    private ButtonItemAdapter(Context context, CharSequence[] items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public CharSequence getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(mContext, R.layout.dialog_customlistitem, null);
        ((TextView) convertView.findViewById(R.id.title)).setText(mItems[position] + " (" + position + ")");
        Button button = (Button) convertView.findViewById(R.id.button);
        button.setTag(position);
        button.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(mContext, "Clicked button " + index, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
