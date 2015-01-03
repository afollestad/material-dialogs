package com.afollestad.materialdialogssample;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ColorChooserDialog extends DialogFragment {

    private Callback mCallback;

    public static interface Callback {
        void onColorSelection(int primary);
    }

    public ColorChooserDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.color_chooser)
                .autoDismiss(false)
                .customView(R.layout.dialog_color_chooser, false)
                .build();

        TypedArray ta = getActivity().getResources().obtainTypedArray(R.array.material_colors_500);
        int[] mColors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++)
            mColors[i] = ta.getColor(i, 0);
        ta.recycle();
        GridLayout list = (GridLayout) dialog.getCustomView().findViewById(R.id.grid);
        for (int i = 0; i < list.getChildCount(); i++) {
            View child = list.getChildAt(i);
            ShapeDrawable circle = new ShapeDrawable(new OvalShape());
            circle.getPaint().setColor(mColors[i]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                child.setBackground(circle);
            else child.setBackgroundDrawable(circle);
        }
        return dialog;
    }

    public void show(Activity context, Callback callback) {
        mCallback = callback;
        show(context.getFragmentManager(), "COLOR_SELECTOR");
    }
}
