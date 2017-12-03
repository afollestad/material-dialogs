package com.afollestad.materialdialogssample;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import com.afollestad.materialdialogs.MaterialDialog;

/** @author Aidan Follestad (afollestad) */
public class AboutDialog extends DialogFragment {

  public static void show(AppCompatActivity context) {
    AboutDialog dialog = new AboutDialog();
    dialog.show(context.getSupportFragmentManager(), "[ABOUT_DIALOG]");
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new MaterialDialog.Builder(getActivity())
        .title(R.string.about)
        .positiveText(R.string.dismiss)
        .content(R.string.about_body, true)
        .contentLineSpacing(1.6f)
        .build();
  }
}
