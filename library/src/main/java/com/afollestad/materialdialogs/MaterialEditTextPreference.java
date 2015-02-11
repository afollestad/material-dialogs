package com.afollestad.materialdialogs;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * @author Marc Holder Kluver (marchold)
 */
public class MaterialEditTextPreference extends EditTextPreference {

    private final Context context;
    private EditText editText;

    public MaterialEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        editText = new EditText(context, attrs);
    }

    public MaterialEditTextPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialEditTextPreference(Context context) {
        this(context, null, 0);
    }

    @Override
    protected void showDialog(Bundle state) {
        final FrameLayout layout = new FrameLayout(context);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getContext().getResources().getDisplayMetrics());
        layout.setPadding(margin, 0, margin, 0);
        editText.setText(getText());
        editText.setId(android.R.id.edit);
        if (editText.getParent() != null)
            ((ViewGroup) editText.getParent()).removeView(editText);
        layout.addView(editText);
        MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(context)
                .title(getTitle())
                .icon(getDialogIcon())
                .positiveText(android.R.string.ok)
                .customView(layout, false)
                .negativeText(getNegativeButtonText())
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        layout.removeView(editText);
                        String value = editText.getText().toString();
                        if (callChangeListener(value)) {
                            setText(value);
                        }
                    }

                    public void onNegative(MaterialDialog dialog) {
                        layout.removeView(editText);
                    }

                    public void onNeutral(MaterialDialog dialog) {
                        layout.removeView(editText);
                    }


                });

        final View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            mBuilder.customView(contentView, false);
        } else {
            mBuilder.content(getDialogMessage());
        }

        mBuilder.show();
    }

    public EditText getEditText() {
        return editText;
    }
}