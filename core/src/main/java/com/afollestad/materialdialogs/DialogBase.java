package com.afollestad.materialdialogs;

import android.support.v7.app.AppCompatDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.internal.MDRootLayout;

/** @author Aidan Follestad (afollestad) */
class DialogBase extends AppCompatDialog implements DialogInterface.OnShowListener {

  protected MDRootLayout view;
  private DialogInterface.OnShowListener showListener;

  DialogBase(Context context, int theme) {
    super(context, theme);
  }

  @Override
  public View findViewById(int id) {
    return view.findViewById(id);
  }

  @Override
  public final void setOnShowListener(@Nullable OnShowListener listener) {
    showListener = listener;
  }

  final void setOnShowListenerInternal() {
    super.setOnShowListener(this);
  }

  final void setViewInternal(View view) {
    super.setContentView(view);
  }

  @Override
  public void onShow(DialogInterface dialog) {
    if (showListener != null) {
      showListener.onShow(dialog);
    }
  }

  @Override
  @Deprecated
  public void setContentView(int layoutResID) throws IllegalAccessError {
    throw new IllegalAccessError(
        "setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.");
  }

  @Override
  @Deprecated
  public void setContentView(View view) throws IllegalAccessError {
    throw new IllegalAccessError(
        "setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.");
  }

  @Override
  @Deprecated
  public void setContentView(View view, @Nullable ViewGroup.LayoutParams params)
      throws IllegalAccessError {
    throw new IllegalAccessError(
        "setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.");
  }
}
