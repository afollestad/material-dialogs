package com.afollestad.materialdialogs.prefs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/** @author Aidan Follestad (afollestad) */
public class MaterialDialogPreference extends DialogPreference {

  private Context context;
  private MaterialDialog dialog;

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public MaterialDialogPreference(Context context) {
    super(context);
    init(context, null);
  }

  public MaterialDialogPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public MaterialDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public MaterialDialogPreference(
      Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    this.context = context;
    PrefUtil.setLayoutResource(context, this, attrs);
  }

  @Override
  public Dialog getDialog() {
    return dialog;
  }

  @Override
  protected void showDialog(Bundle state) {
    MaterialDialog.Builder builder =
        new MaterialDialog.Builder(context)
            .title(getDialogTitle())
            .icon(getDialogIcon())
            .dismissListener(this)
            .onAny(
                new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(MaterialDialog dialog, DialogAction which) {
                    switch (which) {
                      default:
                        MaterialDialogPreference.this.onClick(
                            dialog, DialogInterface.BUTTON_POSITIVE);
                        break;
                      case NEUTRAL:
                        MaterialDialogPreference.this.onClick(
                            dialog, DialogInterface.BUTTON_NEUTRAL);
                        break;
                      case NEGATIVE:
                        MaterialDialogPreference.this.onClick(
                            dialog, DialogInterface.BUTTON_NEGATIVE);
                        break;
                    }
                  }
                })
            .positiveText(getPositiveButtonText())
            .negativeText(getNegativeButtonText())
            .autoDismiss(true); // immediately close the dialog after selection

    final View contentView = onCreateDialogView();
    if (contentView != null) {
      onBindDialogView(contentView);
      builder.customView(contentView, false);
    } else {
      builder.content(getDialogMessage());
    }

    PrefUtil.registerOnActivityDestroyListener(this, this);

    dialog = builder.build();
    if (state != null) {
      dialog.onRestoreInstanceState(state);
    }
    dialog.show();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    PrefUtil.unregisterOnActivityDestroyListener(this, this);
  }

  @Override
  public void onActivityDestroy() {
    super.onActivityDestroy();
    if (dialog != null && dialog.isShowing()) {
      dialog.dismiss();
    }
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

    public static final Creator<SavedState> CREATOR =
        new Creator<SavedState>() {
          public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
          }

          public SavedState[] newArray(int size) {
            return new SavedState[size];
          }
        };
    boolean isDialogShowing;
    Bundle dialogBundle;

    SavedState(Parcel source) {
      super(source);
      isDialogShowing = source.readInt() == 1;
      dialogBundle = source.readBundle();
    }

    SavedState(Parcelable superState) {
      super(superState);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeInt(isDialogShowing ? 1 : 0);
      dest.writeBundle(dialogBundle);
    }
  }
}
