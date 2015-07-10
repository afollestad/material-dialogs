package com.afollestad.materialdialogs.prefs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.reflect.Method;
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
    private MaterialDialog mDialog;

    public MaterialMultiSelectListPreference(Context context) {
        this(context, null);
    }

    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        if (mDialog != null)
            mDialog.setItems(entries);
    }

    private void init(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
            setWidgetLayoutResource(0);
    }

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    @Override
    protected void showDialog(Bundle state) {
        List<Integer> indices = new ArrayList<>();
        for (String s : getValues()) {
            int index = findIndexOfValue(s);
            if (index >= 0)
                indices.add(findIndexOfValue(s));
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(getDialogTitle())
                .content(getDialogMessage())
                .icon(getDialogIcon())
                .negativeText(getNegativeButtonText())
                .positiveText(getPositiveButtonText())
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }
                })
                .items(getEntries())
                .itemsCallbackMultiChoice(indices.toArray(new Integer[indices.size()]), new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        onClick(null, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                        final Set<String> values = new HashSet<>();
                        for (int i : which) {
                            values.add(getEntryValues()[i].toString());
                        }
                        if (callChangeListener(values))
                            setValues(values);
                        return true;
                    }
                })
                .dismissListener(this);

        final View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.customView(contentView, false);
        } else {
            builder.content(getDialogMessage());
        }

        try {
            PreferenceManager pm = getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod(
                    "registerOnActivityDestroyListener",
                    PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, this);
        } catch (Exception ignored) {
        }

        mDialog = builder.build();
        if (state != null)
            mDialog.onRestoreInstanceState(state);
        mDialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            PreferenceManager pm = getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod(
                    "unregisterOnActivityDestroyListener",
                    PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, this);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        Dialog dialog = getDialog();
        if (dialog == null || !dialog.isShowing()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = dialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    // From DialogPreference
    private static class SavedState extends BaseSavedState {
        boolean isDialogShowing;
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            isDialogShowing = source.readInt() == 1;
            dialogBundle = source.readBundle();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isDialogShowing ? 1 : 0);
            dest.writeBundle(dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
