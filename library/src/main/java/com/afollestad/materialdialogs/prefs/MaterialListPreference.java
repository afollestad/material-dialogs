package com.afollestad.materialdialogs.prefs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Adapted from http://stackoverflow.com/a/27429926/1247248
 *
 * @author Marc Holder Kluver (marchold)
 */
public class MaterialListPreference extends ListPreference {

    private Context context;

    public MaterialListPreference(Context context) {
        super(context);
        this.context = context;
    }

    public MaterialListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void showDialog(Bundle state) {
        int preselect = findIndexOfValue(getValue());
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(getDialogTitle())
                .icon(getDialogIcon())
                .negativeText(getNegativeButtonText())
                .items(getEntries())
                .itemsCallbackSingleChoice(preselect, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        onClick(null, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                        if (which >= 0 && getEntryValues() != null) {
                            String value = getEntryValues()[which].toString();
                            if (callChangeListener(value) && isPersistent())
                                setValue(value);
                        }
                    }
                });

        final View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.customView(contentView, false);
        } else {
            builder.content(getDialogMessage());
        }

        builder.show();
    }
}
