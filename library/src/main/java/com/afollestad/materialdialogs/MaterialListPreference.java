package com.afollestad.materialdialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

/**
 * Adapted from http://stackoverflow.com/a/27429926/1247248
 *
 * @author Marc Holder Kluver (marchold)
 */
public class MaterialListPreference extends ListPreference {

    private MaterialDialog.Builder mBuilder;
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
        mBuilder = new MaterialDialog.Builder(context)
                .title(getTitle())
                .icon(getDialogIcon())
                .positiveText(getPositiveButtonText())
                .negativeText(getNegativeButtonText())
                .items(getEntries())
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        onClick(null, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();

                        if (which >= 0 && getEntryValues() != null) {
                            String value = getEntryValues()[which].toString();
                            if (callChangeListener(value))
                                setValue(value);
                        }
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
}