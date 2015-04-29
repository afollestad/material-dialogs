package com.afollestad.materialdialogs.prefs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.afollestad.materialdialogs.R;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;

import java.lang.reflect.Method;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialEditTextPreference extends EditTextPreference {

    private int mColor = 0;
    private MaterialDialog mDialog;

    public MaterialEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mColor = DialogUtils.resolveColor(context, R.attr.colorAccent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mColor = DialogUtils.resolveColor(context, android.R.attr.colorAccent, mColor);
        }
    }

    public MaterialEditTextPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onAddEditTextToDialogView(@NonNull View dialogView, @NonNull EditText editText) {
        if (editText.getParent() != null)
            ((ViewGroup) getEditText().getParent()).removeView(editText);
        ((ViewGroup) dialogView).addView(editText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        getEditText().setText("");
        if (getText() != null)
            getEditText().setText(getText());
        ViewParent oldParent = getEditText().getParent();
        if (oldParent != view) {
            if (oldParent != null)
                ((ViewGroup) oldParent).removeView(getEditText());
            onAddEditTextToDialogView(view, getEditText());
        }
    }

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    @Override
    protected void showDialog(Bundle state) {
        Builder mBuilder = new MaterialDialog.Builder(getContext())
                .title(getDialogTitle())
                .icon(getDialogIcon())
                .positiveText(getPositiveButtonText())
                .negativeText(getNegativeButtonText())
                .callback(callback)
                .dismissListener(this)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (getEditText().getText().length() > 0)
                            getEditText().setSelection(getEditText().length());
                    }
                });

        View layout = LayoutInflater.from(getContext()).inflate(R.layout.md_stub_inputpref, null);
        onBindDialogView(layout);

        MDTintHelper.setTint(getEditText(), mColor);

        TextView message = (TextView) layout.findViewById(android.R.id.message);
        if (getDialogMessage() != null && getDialogMessage().toString().length() > 0) {
            message.setVisibility(View.VISIBLE);
            message.setText(getDialogMessage());
        } else {
            message.setVisibility(View.GONE);
        }
        mBuilder.customView(layout, false);

        PreferenceManager pm = getPreferenceManager();
        try {
            Method method = pm.getClass().getDeclaredMethod(
                    "registerOnActivityDestroyListener",
                    PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDialog = mBuilder.build();
        if (state != null)
            mDialog.onRestoreInstanceState(state);
        requestInputMethod(mDialog);

        mDialog.show();
    }

    /**
     * Callback listener for the MaterialDialog. Positive button checks with
     * OnPreferenceChangeListener before committing user entered text
     */
    private final ButtonCallback callback = new ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            String value = getEditText().getText().toString();
            if (callChangeListener(value) && isPersistent())
                setText(value);
        }
    };

    /**
     * Copied from DialogPreference.java
     */
    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }
}