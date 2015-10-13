package com.afollestad.materialdialogs.folderselector;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.commons.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class FolderChooserDialog extends DialogFragment implements MaterialDialog.ListCallback {

    private final static String TAG = "[MD_FOLDER_SELECTOR]";

    private File parentFolder;
    private File[] parentContents;
    private boolean canGoUp = true;
    private boolean showfiles = false;
    private FolderCallback mCallback;

    public interface FolderCallback {
        void onFolderSelection(File folder);
    }

    public FolderChooserDialog() {
    }

    String[] getContentsArray() {
        if (parentContents == null) return new String[]{};
        String[] results = new String[parentContents.length + (canGoUp ? 1 : 0)];
        if (canGoUp) results[0] = "...";
        for (int i = 0; i < parentContents.length; i++)
            results[canGoUp ? i + 1 : i] = parentContents[i].getName();
        return results;
    }

    File[] listFiles() {
        File[] contents = parentFolder.listFiles();
        boolean showFiles = getBuilder().mShowFiles;
        List<File> results = new ArrayList<>();
        if (contents != null) {
            for (File fi : contents) {
                if (showFiles) {
                    results.add(fi);
                } else {
                    if (fi.isDirectory()) results.add(fi);
                }
            }
            Collections.sort(results, new FolderSorter());
            return results.toArray(new File[results.size()]);
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            return new MaterialDialog.Builder(getActivity())
                    .title(R.string.md_error_label)
                    .content(R.string.md_storage_perm_error)
                    .positiveText(android.R.string.ok)
                    .build();
        }

        if (getArguments() == null || !getArguments().containsKey("builder"))
            throw new IllegalStateException("You must create a FolderChooserDialog using the Builder.");
        if (!getArguments().containsKey("current_path"))
            getArguments().putString("current_path", getBuilder().mInitialPath);
        parentFolder = new File(getArguments().getString("current_path"));
        parentContents = listFiles();
        return new MaterialDialog.Builder(getActivity())
                .title(parentFolder.getAbsolutePath())
                .items(getContentsArray())
                .itemsCallback(this)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        mCallback.onFolderSelection(parentFolder);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .autoDismiss(false)
                .positiveText(getBuilder().mChooseButton)
                .negativeText(getBuilder().mCancelButton)
                .build();
    }

    @Override
    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence s) {
        if (canGoUp && i == 0) {
            parentFolder = parentFolder.getParentFile();
            canGoUp = parentFolder.getParent() != null;
        } else {
            parentFolder = parentContents[canGoUp ? i - 1 : i];
            canGoUp = true;
        }
        parentContents = listFiles();
        MaterialDialog dialog = (MaterialDialog) getDialog();
        dialog.setTitle(parentFolder.getAbsolutePath());
        getArguments().putString("current_path", parentFolder.getAbsolutePath());
        dialog.setItems(getContentsArray());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (FolderCallback) activity;
    }

    public void show(AppCompatActivity context) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(TAG);
        if (frag != null) {
            ((DialogFragment) frag).dismiss();
            context.getSupportFragmentManager().beginTransaction()
                    .remove(frag).commit();
        }
        show(context.getSupportFragmentManager(), TAG);
    }

    public static class Builder implements Serializable {

        @NonNull
        protected final transient AppCompatActivity mContext;
        @StringRes
        protected int mChooseButton;
        @StringRes
        protected int mCancelButton;
        protected String mInitialPath;
        private boolean mShowFiles=false;

        public <ActivityType extends AppCompatActivity & FolderCallback> Builder(@NonNull ActivityType context) {
            mContext = context;
            mChooseButton = R.string.md_choose_label;
            mCancelButton = android.R.string.cancel;
            mInitialPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        @NonNull
        public Builder chooseButton(@StringRes int text) {
            mChooseButton = text;
            return this;
        }

        @NonNull
        public Builder cancelButton(@StringRes int text) {
            mCancelButton = text;
            return this;
        }

        @NonNull
        public Builder initialPath(@Nullable String initialPath) {
            if (initialPath == null)
                initialPath = File.separator;
            mInitialPath = initialPath;
            return this;
        }

        @NonNull
        public Builder isShowingFiles(@Nullable boolean ShowFiles) {

            mShowFiles = ShowFiles;
            return this;

        }

        @NonNull
        public FolderChooserDialog build() {
            FolderChooserDialog dialog = new FolderChooserDialog();
            Bundle args = new Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        public FolderChooserDialog show() {
            FolderChooserDialog dialog = build();
            dialog.show(mContext);
            return dialog;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    private Builder getBuilder() {
        return (Builder) getArguments().getSerializable("builder");
    }

    private static class FolderSorter implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}
