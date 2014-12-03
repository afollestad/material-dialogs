package com.afollestad.materialdialogssample;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class FolderSelectorDialog extends DialogFragment implements MaterialDialog.ListCallback, MaterialDialog.Callback {

    File parentFolder;
    File[] parentContents;
    boolean canGoUp = true;
    Callback mCallback;

    public static interface Callback {
        void onFolderSelection(File folder);
    }

    public FolderSelectorDialog() {
        parentFolder = Environment.getExternalStorageDirectory();
        parentContents = listFiles();
    }

    public String[] getContentsArray() {
        String[] results = new String[parentContents.length + (canGoUp ? 1 : 0)];
        if (canGoUp) results[0] = "...";
        for (int i = 0; i < parentContents.length; i++)
            results[canGoUp ? i + 1 : i] = parentContents[i].getName();
        return results;
    }

    public File[] listFiles() {
        File[] contents = parentFolder.listFiles();
        List<File> results = new ArrayList<File>();
        for (File fi : contents) {
            if (fi.isDirectory()) results.add(fi);
        }
        Collections.sort(results, new FolderSorter());
        return results.toArray(new File[results.size()]);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title(parentFolder.getAbsolutePath())
                .items(getContentsArray())
                .itemsCallback(this)
                .callback(this)
                .autoDismiss(false)
                .positiveText(R.string.choose)
                .negativeText(android.R.string.cancel)
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
        dialog.setItems(getContentsArray());
    }

    @Override
    public void onPositive(MaterialDialog materialDialog) {
        materialDialog.dismiss();
        mCallback.onFolderSelection(parentFolder);
    }

    public void show(Activity context, Callback callback) {
        mCallback = callback;
        show(context.getFragmentManager(), "FOLDER_SELECTOR");
    }

    @Override
    public void onNegative(MaterialDialog materialDialog) {
        materialDialog.dismiss();
    }

    public static class FolderSorter implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}
