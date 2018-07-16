package com.afollestad.materialdialogs.prefs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.support.v7.app.AppCompatDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.EditTextPreference;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.commons.R;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;

/** @author Aidan Follestad (afollestad) */
public class MaterialEditTextPreference extends EditTextPreference {

  private int color = 0;
  private MaterialDialog dialog;
  private EditText editText;

  public MaterialEditTextPreference(Context context) {
    super(context);
    init(context, null);
  }

  public MaterialEditTextPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public MaterialEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public MaterialEditTextPreference(
      Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    PrefUtil.setLayoutResource(context, this, attrs);
    int fallback;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      fallback = DialogUtils.resolveColor(context, android.R.attr.colorAccent);
    } else {
      fallback = 0;
    }
    fallback = DialogUtils.resolveColor(context, R.attr.colorAccent, fallback);
    color = DialogUtils.resolveColor(context, R.attr.md_widget_color, fallback);

    editText = new AppCompatEditText(context, attrs);
    // Give it an ID so it can be saved/restored
    editText.setId(android.R.id.edit);
    editText.setEnabled(true);
  }

  @Override
  protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
    ((ViewGroup) dialogView)
        .addView(
            editText,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
  }

  @SuppressLint("MissingSuperCall")
  @Override
  protected void onBindDialogView(View view) {
    EditText editText = this.editText;
    editText.setText(getText());
    // Initialize cursor to end of text
    if (editText.getText().length() > 0) {
      editText.setSelection(editText.length());
    }
    ViewParent oldParent = editText.getParent();
    if (oldParent != view) {
      if (oldParent != null) {
        ((ViewGroup) oldParent).removeView(editText);
      }
      onAddEditTextToDialogView(view, editText);
    }
  }

  @Override
  protected void onDialogClosed(boolean positiveResult) {
    if (positiveResult) {
      String value = editText.getText().toString();
      if (callChangeListener(value)) {
        setText(value);
      }
    }
  }

  @Override
  public EditText getEditText() {
    return editText;
  }

  @Override
  public AppCompatDialog getDialog() {
    return dialog;
  }

  @Override
  protected void showDialog(Bundle state) {
    Builder mBuilder =
        new MaterialDialog.Builder(getContext())
            .title(getDialogTitle())
            .icon(getDialogIcon())
            .positiveText(getPositiveButtonText())
            .negativeText(getNegativeButtonText())
            .dismissListener(this)
            .onAny(
                new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(MaterialDialog dialog, DialogAction which) {
                    switch (which) {
                      default:
                        MaterialEditTextPreference.this.onClick(
                            dialog, DialogInterface.BUTTON_POSITIVE);
                        break;
                      case NEUTRAL:
                        MaterialEditTextPreference.this.onClick(
                            dialog, DialogInterface.BUTTON_NEUTRAL);
                        break;
                      case NEGATIVE:
                        MaterialEditTextPreference.this.onClick(
                            dialog, DialogInterface.BUTTON_NEGATIVE);
                        break;
                    }
                  }
                })
            .dismissListener(this);

    @SuppressLint("InflateParams")
    View layout = LayoutInflater.from(getContext()).inflate(R.layout.md_stub_inputpref, null);
    onBindDialogView(layout);

    MDTintHelper.setTint(editText, color);

    TextView message = (TextView) layout.findViewById(android.R.id.message);
    if (getDialogMessage() != null && getDialogMessage().toString().length() > 0) {
      message.setVisibility(View.VISIBLE);
      message.setText(getDialogMessage());
    } else {
      message.setVisibility(View.GONE);
    }
    mBuilder.customView(layout, false);

    PrefUtil.registerOnActivityDestroyListener(this, this);

    dialog = mBuilder.build();
    if (state != null) {
      dialog.onRestoreInstanceState(state);
    }
    requestInputMethod(dialog);

    dialog.show();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    PrefUtil.unregisterOnActivityDestroyListener(this, this);
  }

  /** Copied from DialogPreference.java */
  private void requestInputMethod(AppCompatDialog dialog) {
    Window window = dialog.getWindow();
    if (window == null) {
      return;
    }
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
    AppCompatDialog dialog = getDialog();
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

    public static final Parcelable.Creator<SavedState> CREATOR =
        new Parcelable.Creator<SavedState>() {
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
