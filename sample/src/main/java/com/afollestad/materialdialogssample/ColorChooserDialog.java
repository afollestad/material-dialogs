package com.afollestad.materialdialogssample;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ColorChooserDialog extends DialogFragment implements ColorChooserAdapter.Callback {

    private Callback mCallback;

    @Override
    public void onColorSelected(int index, int primary) {
        mCallback.onColorSelection(primary);
    }

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
                .positiveText(R.string.choose)
                .build();
        RecyclerView list = (RecyclerView) dialog.getCustomView().findViewById(R.id.list);
        if (list != null) {
            list.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.color_chooser_columns)));
            list.setAdapter(new ColorChooserAdapter(getActivity(), this));
        }
        return dialog;
    }

    public void show(Activity context, Callback callback) {
        mCallback = callback;
        show(context.getFragmentManager(), "COLOR_SELECTOR");
    }
}
