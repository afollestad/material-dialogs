package com.afollestad.materialdialogssample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ColorChooserAdapter extends RecyclerView.Adapter<ColorChooserAdapter.ViewHolder> implements View.OnClickListener {

    private Callback mCallback;
    private int[] mColors;

    @Override
    public void onClick(View v) {
        if (mCallback != null) {
            Integer index = (Integer) v.getTag();
            mCallback.onColorSelected(index, mColors[index]);
        }
    }

    public static interface Callback {
        void onColorSelected(int index, int primary);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public View mColorView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mColorView = v.findViewById(R.id.color);
        }
    }

    public ColorChooserAdapter(Context context, Callback callback) {
        mCallback = callback;
        TypedArray ta = context.getResources().obtainTypedArray(R.array.material_colors_500);
        mColors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++)
            mColors[i] = ta.getColor(i, 0);
        ta.recycle();
    }

    @Override
    public ColorChooserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_colorchooser, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mView.setTag(position);
        holder.mView.setOnClickListener(this);

        ShapeDrawable circle = new ShapeDrawable(new OvalShape());
        circle.getPaint().setColor(mColors[position]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            holder.mColorView.setBackground(circle);
        else holder.mColorView.setBackgroundDrawable(circle);

        // TODO add circular ripple selector over the colored circles here
        // TODO add activated state to circles, check mark similar to Today Calendar's color chooser
    }

    @Override
    public int getItemCount() {
        return mColors.length;
    }
}