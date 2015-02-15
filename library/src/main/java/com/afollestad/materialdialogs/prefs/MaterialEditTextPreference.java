package com.afollestad.materialdialogs.prefs;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.R;

/**
 * @author Marc Holder Kluver (marchold)
 */
public class MaterialEditTextPreference extends EditTextPreference {

    private EditText editText;

    public MaterialEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MaterialEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected void showDialog(Bundle state) {
        MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(getContext())
                .title(getTitle())
                .icon(getDialogIcon())
                .positiveText(getPositiveButtonText())
                .negativeText(getNegativeButtonText())
                .customView(R.layout.md_input_dialog, false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        String value = editText.getText().toString();
                        if (callChangeListener(value) && isPersistent())
                            setText(value);
                    }
                });

        mBuilder.content(getDialogMessage());
        MaterialDialog dialog = mBuilder.build();
        editText = (EditText) ((ViewGroup) dialog.getCustomView()).getChildAt(0);
        editText.setText(getText());
        dialog.show();
    }

    public final EditText getEditText() {
        return editText;
    }
}