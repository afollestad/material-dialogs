package com.afollestad.materialdialogs.prefs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class only works on Honeycomb (API 11) and above.
 *
 * @author Aidan Follestad (afollestad)
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MaterialMultiSelectListPreference extends MultiSelectListPreference {

    private Context context;

    public MaterialMultiSelectListPreference(Context context) {
        this(context, null);
    }

    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
            setWidgetLayoutResource(0);
    }

    private String[] getPersistedValues() {
        Set<String> values = getPreferenceManager().getSharedPreferences().getStringSet(getKey(), null);
        if (values == null)
            return null;
        return values.toArray(new String[values.size()]);
    }

    private Integer[] getPersistedIndices() {
        String[] values = getPersistedValues();
        if (values == null)
            return null;
        List<Integer> indices = new ArrayList<>();
        for (String v : values)
            indices.add(findIndexOfValue(v));
        return indices.toArray(new Integer[indices.size()]);
    }

    private void persistValues(Integer[] indices) {
        final CharSequence[] values = getEntryValues();
        Set<String> valueSet = new HashSet<>();
        for (int index : indices)
            valueSet.add(values[index].toString());
        getEditor().putStringSet(getKey(), valueSet).commit();
    }

    @Override
    protected void showDialog(Bundle state) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(getDialogTitle())
                .content(getDialogMessage())
                .icon(getDialogIcon())
                .negativeText(getNegativeButtonText())
                .positiveText(getPositiveButtonText())
                .items(getEntries())
                .itemsCallbackMultiChoice(getPersistedIndices(), new MaterialDialog.ListCallbackMulti() {
                    @Override
                    public void onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        onClick(null, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                        if (callChangeListener(which) && isPersistent()) {
                            persistValues(which);
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
