package com.afollestad.materialdialogs.folderselector;

import static com.afollestad.materialdialogs.util.DialogUtils.checkNotNull;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.commons.R;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** @author Aidan Follestad (afollestad) */
public class FolderChooserDialog extends DialogFragment implements MaterialDialog.ListCallback {

  private static final String DEFAULT_TAG = "[MD_FOLDER_SELECTOR]";

  private File parentFolder;
  private File[] parentContents;
  private boolean canGoUp = false;
  private FolderCallback callback;

  public FolderChooserDialog() {}

  String[] getContentsArray() {
    if (parentContents == null) {
      if (canGoUp) {
        return new String[] {getBuilder().goUpLabel};
      }
      return new String[] {};
    }
    String[] results = new String[parentContents.length + (canGoUp ? 1 : 0)];
    if (canGoUp) {
      results[0] = getBuilder().goUpLabel;
    }
    for (int i = 0; i < parentContents.length; i++) {
      results[canGoUp ? i + 1 : i] = parentContents[i].getName();
    }
    return results;
  }

  File[] listFiles() {
    File[] contents = parentFolder.listFiles();
    List<File> results = new ArrayList<>();
    if (contents != null) {
      for (File fi : contents) {
        if (fi.isDirectory()) {
          results.add(fi);
        }
      }
      Collections.sort(results, new FolderSorter());
      return results.toArray(new File[results.size()]);
    }
    return null;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
      return new MaterialDialog.Builder(getActivity())
          .title(R.string.md_error_label)
          .content(R.string.md_storage_perm_error)
          .positiveText(android.R.string.ok)
          .build();
    }
    if (getArguments() == null || !getArguments().containsKey("builder")) {
      throw new IllegalStateException("You must create a FolderChooserDialog using the Builder.");
    }
    if (!getArguments().containsKey("current_path")) {
      getArguments().putString("current_path", getBuilder().initialPath);
    }
    parentFolder = new File(getArguments().getString("current_path"));
    checkIfCanGoUp();
    parentContents = listFiles();
    MaterialDialog.Builder builder =
        new MaterialDialog.Builder(getActivity())
            .typeface(getBuilder().mediumFont, getBuilder().regularFont)
            .title(parentFolder.getAbsolutePath())
            .items((CharSequence[]) getContentsArray())
            .itemsCallback(this)
            .onPositive(
                new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(MaterialDialog dialog, DialogAction which) {
                    dialog.dismiss();
                    callback.onFolderSelection(FolderChooserDialog.this, parentFolder);
                  }
                })
            .onNegative(
                new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(MaterialDialog dialog, DialogAction which) {
                    dialog.dismiss();
                  }
                })
            .autoDismiss(false)
            .positiveText(getBuilder().chooseButton)
            .negativeText(getBuilder().cancelButton);
    if (getBuilder().allowNewFolder) {
      builder.neutralText(getBuilder().newFolderButton);
      builder.onNeutral(
          new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
              createNewFolder();
            }
          });
    }
    if ("/".equals(getBuilder().initialPath)) {
      canGoUp = false;
    }
    return builder.build();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (callback != null) {
      callback.onFolderChooserDismissed(this);
    }
  }

  private void createNewFolder() {
    new MaterialDialog.Builder(checkNotNull(getActivity(), "activity"))
        .title(getBuilder().newFolderButton)
        .input(
            0,
            0,
            false,
            new MaterialDialog.InputCallback() {
              @Override
              public void onInput(MaterialDialog dialog, CharSequence input) {
                //noinspection ResultOfMethodCallIgnored
                final File newFi = new File(parentFolder, input.toString());
                if (!newFi.mkdir()) {
                  String msg =
                      "Unable to create folder "
                          + newFi.getAbsolutePath()
                          + ", make sure you have the WRITE_EXTERNAL_STORAGE permission or root permissions.";
                  Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                } else {
                  reload();
                }
              }
            })
        .show();
  }

  @Override
  public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence s) {
    if (canGoUp && i == 0) {
      parentFolder = parentFolder.getParentFile();
      if (parentFolder.getAbsolutePath().equals("/storage/emulated")) {
        parentFolder = parentFolder.getParentFile();
      }
      canGoUp = parentFolder.getParent() != null;
    } else {
      parentFolder = parentContents[canGoUp ? i - 1 : i];
      canGoUp = true;
      if (parentFolder.getAbsolutePath().equals("/storage/emulated")) {
        parentFolder = Environment.getExternalStorageDirectory();
      }
    }
    reload();
  }

  private void checkIfCanGoUp() {
    try {
      canGoUp = parentFolder.getPath().split("/").length > 1;
    } catch (IndexOutOfBoundsException e) {
      canGoUp = false;
    }
  }

  private void reload() {
    parentContents = listFiles();
    MaterialDialog dialog = (MaterialDialog) getDialog();
    dialog.setTitle(parentFolder.getAbsolutePath());
    checkNotNull(getArguments(), "arguments")
        .putString("current_path", parentFolder.getAbsolutePath());
    dialog.setItems((CharSequence[]) getContentsArray());
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (getActivity() instanceof FolderCallback) {
      callback = (FolderCallback) getActivity();
    } else if (getTargetFragment() instanceof FolderCallback) {
      callback = (FolderCallback) getTargetFragment();
    } else {
      throw new IllegalStateException(
          "FolderChooserDialog needs to be shown from an Activity/Fragment implementing FolderCallback.");
    }
  }

  public void show() {
    final FragmentManager fragmentManager =
        getBuilder().fragment != null
            ? getBuilder().fragment.getChildFragmentManager()
            : getBuilder().activity.getSupportFragmentManager();

    final String tag = getBuilder().tag;
    Fragment frag = fragmentManager.findFragmentByTag(tag);
    if (frag != null) {
      ((DialogFragment) frag).dismiss();
      fragmentManager.beginTransaction().remove(frag).commit();
    }

    if (getBuilder().fragment != null) {
      setTargetFragment(getBuilder().fragment, 63);
    }
    show(fragmentManager, tag);
  }

  private Builder getBuilder() {
    return (Builder) checkNotNull(getArguments(), "arguments").getSerializable("builder");
  }

  public interface FolderCallback {

    void onFolderSelection(FolderChooserDialog dialog, File folder);

    void onFolderChooserDismissed(FolderChooserDialog dialog);
  }

  @SuppressWarnings({"unused", "SameParameterValue"})
  public static class Builder implements Serializable {

    transient FragmentActivity activity;
    transient Fragment fragment;

    @StringRes int chooseButton;
    @StringRes int cancelButton;
    String initialPath;
    String tag;
    boolean allowNewFolder;
    @StringRes int newFolderButton;
    String goUpLabel;
    @Nullable String mediumFont;
    @Nullable String regularFont;

    private Builder() {
      chooseButton = R.string.md_choose_label;
      cancelButton = android.R.string.cancel;
      goUpLabel = "...";
      initialPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public <T extends FragmentActivity & FolderCallback> Builder(T activity) {
      this();
      this.activity = activity;
    }

    public <T extends Fragment & FolderCallback> Builder(T fragment) {
      this();
      this.fragment = fragment;
    }

    public Builder typeface(@Nullable String medium, @Nullable String regular) {
      this.mediumFont = medium;
      this.regularFont = regular;
      return this;
    }

    public Builder chooseButton(@StringRes int text) {
      chooseButton = text;
      return this;
    }

    public Builder cancelButton(@StringRes int text) {
      cancelButton = text;
      return this;
    }

    public Builder goUpLabel(String text) {
      goUpLabel = text;
      return this;
    }

    public Builder allowNewFolder(boolean allow, @StringRes int buttonLabel) {
      allowNewFolder = allow;
      if (buttonLabel == 0) {
        buttonLabel = R.string.new_folder;
      }
      newFolderButton = buttonLabel;
      return this;
    }

    public Builder initialPath(@Nullable String initialPath) {
      if (initialPath == null) {
        initialPath = File.separator;
      }
      this.initialPath = initialPath;
      return this;
    }

    public Builder tag(@Nullable String tag) {
      if (tag == null) {
        tag = DEFAULT_TAG;
      }
      this.tag = tag;
      return this;
    }

    public FolderChooserDialog build() {
      FolderChooserDialog dialog = new FolderChooserDialog();
      Bundle args = new Bundle();
      args.putSerializable("builder", this);
      dialog.setArguments(args);
      return dialog;
    }

    public FolderChooserDialog show() {
      FolderChooserDialog dialog = build();
      dialog.show();
      return dialog;
    }
  }

  private static class FolderSorter implements Comparator<File> {
    @Override
    public int compare(File lhs, File rhs) {
      return lhs.getName().compareTo(rhs.getName());
    }
  }
}
