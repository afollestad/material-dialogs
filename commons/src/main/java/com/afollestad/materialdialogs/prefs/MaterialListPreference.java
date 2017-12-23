package com.afollestad.materialdialogs.prefs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.lang.reflect.Field;

/** @author Marc Holder Kluver (marchold), Aidan Follestad (afollestad) */
public class MaterialListPreference extends ListPreference {

  private Context context;
  private MaterialDialog dialog;
  private MaterialDialog.Builder builder;

  public MaterialListPreference(Context context) {
    super(context);
    init(context, null);
  }

  public MaterialListPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public MaterialListPreference(
      Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    this.context = context;
    PrefUtil.setLayoutResource(context, this, attrs);
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
      setWidgetLayoutResource(0);
    }
  }

  @Override
  public void setEntries(CharSequence[] entries) {
    super.setEntries(entries);
    if (dialog != null) {
      dialog.setItems(entries);
    }
  }

  @Override
  public Dialog getDialog() {
    return dialog;
  }

  public RecyclerView getRecyclerView() {
    if (getDialog() == null) {
      return null;
    }
    return ((MaterialDialog) getDialog()).getRecyclerView();
  }
  
  public MaterialDialog.Builder resetBuilder() {
      int preselect = findIndexOfValue(getValue());
      return builder =
              new MaterialDialog.Builder(context)
                      .title(getDialogTitle())
                      .icon(getDialogIcon())
                      .dismissListener(this)
                      .onAny(
                          new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                              switch (which) {
                                default:
                                  MaterialListPreference.this.onClick(
                                      dialog, DialogInterface.BUTTON_POSITIVE);
                                  break;
                                case NEUTRAL:
                                  MaterialListPreference.this.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
                                  break;
                                case NEGATIVE:
                                  MaterialListPreference.this.onClick(
                                      dialog, DialogInterface.BUTTON_NEGATIVE);
                                  break;
                              }
                            }
                          })
                      .negativeText(getNegativeButtonText())
                      .items(getEntries())
                      .autoDismiss(true) // immediately close the dialog after selection
                      .itemsCallbackSingleChoice(
                          preselect,
                          new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(
                                MaterialDialog dialog, View itemView, int which, CharSequence text) {
                              onClick(null, DialogInterface.BUTTON_POSITIVE);
                              if (which >= 0 && getEntryValues() != null) {
                                try {
                                  Field clickedIndex =
                                      ListPreference.class.getDeclaredField("mClickedDialogEntryIndex");
                                  clickedIndex.setAccessible(true);
                                  clickedIndex.set(MaterialListPreference.this, which);
                                } catch (Exception e) {
                                  e.printStackTrace();
                                }
                              }
                              return true;
                            }
                          });
  }

  /**
   * @param builder receiving null is the same that resetBuilder()
   */
  public MaterialDialog.Builder setBuilder(@Nullable final MaterialDialog.Builder builder) {
    this.builder = builder;
    return builder;
  }

  @Override
  protected void showDialog(Bundle state) {
    if (getEntries() == null || getEntryValues() == null) {
      throw new IllegalStateException(
          "ListPreference requires an entries array and an entryValues array.");
    }

    if (builder == null) {
      resetBuilder();
    }
        
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
    onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
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
    public void writeToParcel(@NonNull Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeInt(isDialogShowing ? 1 : 0);
      dest.writeBundle(dialogBundle);
    }
  }
}
