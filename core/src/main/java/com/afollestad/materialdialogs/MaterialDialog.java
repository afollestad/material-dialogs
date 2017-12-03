package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.internal.MDButton;
import com.afollestad.materialdialogs.internal.MDRootLayout;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.afollestad.materialdialogs.util.RippleHelper;
import com.afollestad.materialdialogs.util.TypefaceHelper;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** @author Aidan Follestad (afollestad) */
@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
public class MaterialDialog extends DialogBase
    implements View.OnClickListener, DefaultRvAdapter.InternalListCallback {

  protected final Builder builder;
  private final Handler handler;
  protected ImageView icon;
  protected TextView title;
  protected TextView content;

  EditText input;
  RecyclerView recyclerView;
  View titleFrame;
  FrameLayout customViewFrame;
  ProgressBar progressBar;
  TextView progressLabel;
  TextView progressMinMax;
  TextView inputMinMax;
  CheckBox checkBoxPrompt;
  MDButton positiveButton;
  MDButton neutralButton;
  MDButton negativeButton;
  ListType listType;
  List<Integer> selectedIndicesList;

  @SuppressLint("InflateParams")
  protected MaterialDialog(Builder builder) {
    super(builder.context, DialogInit.getTheme(builder));
    handler = new Handler();
    this.builder = builder;
    final LayoutInflater inflater = LayoutInflater.from(getContext());
    view = (MDRootLayout) inflater.inflate(DialogInit.getInflateLayout(builder), null);
    DialogInit.init(this);

    // Don't keep a Context reference in the Builder after this point
    builder.context = null;
  }

  public final Builder getBuilder() {
    return builder;
  }

  public final void setTypeface(TextView target, @Nullable Typeface t) {
    if (t == null) {
      return;
    }
    int flags = target.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG;
    target.setPaintFlags(flags);
    target.setTypeface(t);
  }

  @Nullable
  public Object getTag() {
    return builder.tag;
  }

  final void checkIfListInitScroll() {
    if (recyclerView == null) {
      return;
    }
    recyclerView
        .getViewTreeObserver()
        .addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
              @SuppressWarnings("ConstantConditions")
              @Override
              public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                  //noinspection deprecation
                  recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                  recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                if (listType == ListType.SINGLE || listType == ListType.MULTI) {
                  int selectedIndex;
                  if (listType == ListType.SINGLE) {
                    if (builder.selectedIndex < 0) {
                      return;
                    }
                    selectedIndex = builder.selectedIndex;
                  } else {
                    if (selectedIndicesList == null || selectedIndicesList.size() == 0) {
                      return;
                    }
                    Collections.sort(selectedIndicesList);
                    selectedIndex = selectedIndicesList.get(0);
                  }

                  final int fSelectedIndex = selectedIndex;
                  recyclerView.post(
                      new Runnable() {
                        @Override
                        public void run() {
                          recyclerView.requestFocus();
                          builder.layoutManager.scrollToPosition(fSelectedIndex);
                        }
                      });
                }
              }
            });
  }

  /** Sets the dialog RecyclerView's adapter/layout manager, and it's item click listener. */
  final void invalidateList() {
    if (recyclerView == null) {
      return;
    } else if ((builder.items == null || builder.items.size() == 0) && builder.adapter == null) {
      return;
    }
    if (builder.layoutManager == null) {
      builder.layoutManager = new LinearLayoutManager(getContext());
    }
    if (recyclerView.getLayoutManager() == null) {
      recyclerView.setLayoutManager(builder.layoutManager);
    }
    recyclerView.setAdapter(builder.adapter);
    if (listType != null) {
      ((DefaultRvAdapter) builder.adapter).setCallback(this);
    }
  }

  @Override
  public boolean onItemSelected(
      MaterialDialog dialog, View view, int position, CharSequence text, boolean longPress) {
    if (!view.isEnabled()) {
      return false;
    }
    if (listType == null || listType == ListType.REGULAR) {
      // Default adapter, non choice mode
      if (builder.autoDismiss) {
        // If auto dismiss is enabled, dismiss the dialog when a list item is selected
        dismiss();
      }
      if (!longPress && builder.listCallback != null) {
        builder.listCallback.onSelection(this, view, position, builder.items.get(position));
      }
      if (longPress && builder.listLongCallback != null) {
        return builder.listLongCallback.onLongSelection(
            this, view, position, builder.items.get(position));
      }
    } else {
      // Default adapter, choice mode
      if (listType == ListType.MULTI) {
        final CheckBox cb = view.findViewById(R.id.md_control);
        if (!cb.isEnabled()) {
          return false;
        }
        final boolean shouldBeChecked = !selectedIndicesList.contains(position);
        if (shouldBeChecked) {
          // Add the selection to the states first so the callback includes it (when
          // alwaysCallMultiChoiceCallback)
          selectedIndicesList.add(position);
          if (builder.alwaysCallMultiChoiceCallback) {
            // If the checkbox wasn't previously selected, and the callback returns true, add it to
            // the states and check it
            if (sendMultiChoiceCallback()) {
              cb.setChecked(true);
            } else {
              // The callback cancelled selection, remove it from the states
              selectedIndicesList.remove(Integer.valueOf(position));
            }
          } else {
            // The callback was not used to check if selection is allowed, just select it
            cb.setChecked(true);
          }
        } else {
          // Remove the selection from the states first so the callback does not include it (when
          // alwaysCallMultiChoiceCallback)
          selectedIndicesList.remove(Integer.valueOf(position));
          if (builder.alwaysCallMultiChoiceCallback) {
            // If the checkbox was previously selected, and the callback returns true, remove it
            // from the states and uncheck it
            if (sendMultiChoiceCallback()) {
              cb.setChecked(false);
            } else {
              // The callback cancelled unselection, re-add it to the states
              selectedIndicesList.add(position);
            }
          } else {
            // The callback was not used to check if the unselection is allowed, just uncheck it
            cb.setChecked(false);
          }
        }
      } else if (listType == ListType.SINGLE) {
        final RadioButton radio = view.findViewById(R.id.md_control);
        if (!radio.isEnabled()) {
          return false;
        }
        boolean allowSelection = true;
        final int oldSelected = builder.selectedIndex;

        if (builder.autoDismiss && builder.positiveText == null) {
          // If auto dismiss is enabled, and no action button is visible to approve the selection,
          // dismiss the dialog
          dismiss();
          // Don't allow the selection to be updated since the dialog is being dismissed anyways
          allowSelection = false;
          // Update selected index and send callback
          builder.selectedIndex = position;
          sendSingleChoiceCallback(view);
        } else if (builder.alwaysCallSingleChoiceCallback) {
          // Temporarily set the new index so the callback uses the right one
          builder.selectedIndex = position;
          // Only allow the radio button to be checked if the callback returns true
          allowSelection = sendSingleChoiceCallback(view);
          // Restore the old selected index, so the state is updated below
          builder.selectedIndex = oldSelected;
        }
        // Update the checked states
        if (allowSelection) {
          builder.selectedIndex = position;
          radio.setChecked(true);
          builder.adapter.notifyItemChanged(oldSelected);
          builder.adapter.notifyItemChanged(position);
        }
      }
    }
    return true;
  }

  final Drawable getListSelector() {
    if (builder.listSelector != 0) {
      return ResourcesCompat.getDrawable(getContext().getResources(), builder.listSelector, null);
    }
    final Drawable d = DialogUtils.resolveDrawable(getContext(), R.attr.md_list_selector);
    if (d != null) {
      return d;
    }
    return DialogUtils.resolveDrawable(getContext(), R.attr.md_list_selector);
  }

  public RecyclerView getRecyclerView() {
    return recyclerView;
  }

  public boolean isPromptCheckBoxChecked() {
    return checkBoxPrompt != null && checkBoxPrompt.isChecked();
  }

  public void setPromptCheckBoxChecked(boolean checked) {
    if (checkBoxPrompt != null) {
      checkBoxPrompt.setChecked(checked);
    }
  }

  /* package */ Drawable getButtonSelector(DialogAction which, boolean isStacked) {
    if (isStacked) {
      if (builder.btnSelectorStacked != 0) {
        return ResourcesCompat.getDrawable(
            getContext().getResources(), builder.btnSelectorStacked, null);
      }
      final Drawable d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_stacked_selector);
      if (d != null) {
        return d;
      }
      return DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_stacked_selector);
    } else {
      switch (which) {
        default:
          {
            if (builder.btnSelectorPositive != 0) {
              return ResourcesCompat.getDrawable(
                  getContext().getResources(), builder.btnSelectorPositive, null);
            }
            Drawable d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_positive_selector);
            if (d != null) {
              return d;
            }
            d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_positive_selector);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              RippleHelper.applyColor(d, builder.buttonRippleColor);
            }
            return d;
          }
        case NEUTRAL:
          {
            if (builder.btnSelectorNeutral != 0) {
              return ResourcesCompat.getDrawable(
                  getContext().getResources(), builder.btnSelectorNeutral, null);
            }
            Drawable d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_neutral_selector);
            if (d != null) {
              return d;
            }
            d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_neutral_selector);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              RippleHelper.applyColor(d, builder.buttonRippleColor);
            }
            return d;
          }
        case NEGATIVE:
          {
            if (builder.btnSelectorNegative != 0) {
              return ResourcesCompat.getDrawable(
                  getContext().getResources(), builder.btnSelectorNegative, null);
            }
            Drawable d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_negative_selector);
            if (d != null) {
              return d;
            }
            d = DialogUtils.resolveDrawable(getContext(), R.attr.md_btn_negative_selector);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              RippleHelper.applyColor(d, builder.buttonRippleColor);
            }
            return d;
          }
      }
    }
  }

  private boolean sendSingleChoiceCallback(View v) {
    if (builder.listCallbackSingleChoice == null) {
      return false;
    }
    CharSequence text = null;
    if (builder.selectedIndex >= 0 && builder.selectedIndex < builder.items.size()) {
      text = builder.items.get(builder.selectedIndex);
    }
    return builder.listCallbackSingleChoice.onSelection(this, v, builder.selectedIndex, text);
  }

  private boolean sendMultiChoiceCallback() {
    if (builder.listCallbackMultiChoice == null) {
      return false;
    }
    Collections.sort(selectedIndicesList); // make sure the indices are in order
    List<CharSequence> selectedTitles = new ArrayList<>();
    for (Integer i : selectedIndicesList) {
      if (i < 0 || i > builder.items.size() - 1) {
        continue;
      }
      selectedTitles.add(builder.items.get(i));
    }
    return builder.listCallbackMultiChoice.onSelection(
        this,
        selectedIndicesList.toArray(new Integer[selectedIndicesList.size()]),
        selectedTitles.toArray(new CharSequence[selectedTitles.size()]));
  }

  @Override
  public final void onClick(View v) {
    DialogAction tag = (DialogAction) v.getTag();
    switch (tag) {
      case POSITIVE:
        if (builder.onPositiveCallback != null) {
          builder.onPositiveCallback.onClick(this, tag);
        }
        if (!builder.alwaysCallSingleChoiceCallback) {
          sendSingleChoiceCallback(v);
        }
        if (!builder.alwaysCallMultiChoiceCallback) {
          sendMultiChoiceCallback();
        }
        if (builder.inputCallback != null && input != null && !builder.alwaysCallInputCallback) {
          builder.inputCallback.onInput(this, input.getText());
        }
        if (builder.autoDismiss) {
          dismiss();
        }
        break;
      case NEGATIVE:
        if (builder.onNegativeCallback != null) {
          builder.onNegativeCallback.onClick(this, tag);
        }
        if (builder.autoDismiss) {
          cancel();
        }
        break;
      case NEUTRAL:
        if (builder.onNeutralCallback != null) {
          builder.onNeutralCallback.onClick(this, tag);
        }
        if (builder.autoDismiss) {
          dismiss();
        }
        break;
    }
    if (builder.onAnyCallback != null) {
      builder.onAnyCallback.onClick(this, tag);
    }
  }

  @Override
  @UiThread
  public void show() {
    try {
      super.show();
    } catch (WindowManager.BadTokenException e) {
      throw new DialogException(
          "Bad window token, you cannot show a dialog "
              + "before an Activity is created or after it's hidden.");
    }
  }

  /**
   * Retrieves the view of an action button, allowing you to modify properties such as whether or
   * not it's enabled. Use {@link #setActionButton(DialogAction, int)} to change text, since the
   * view returned here is not the view that displays text.
   *
   * @param which The action button of which to get the view for.
   * @return The view from the dialog's layout representing this action button.
   */
  public final MDButton getActionButton(DialogAction which) {
    switch (which) {
      default:
        return positiveButton;
      case NEUTRAL:
        return neutralButton;
      case NEGATIVE:
        return negativeButton;
    }
  }

  /** Retrieves the view representing the dialog as a whole. Be careful with this. */
  public final View getView() {
    return view;
  }

  @Nullable
  public final EditText getInputEditText() {
    return input;
  }

  /**
   * Retrieves the TextView that contains the dialog title. If you want to update the title, use
   * #{@link #setTitle(CharSequence)} instead.
   */
  public final TextView getTitleView() {
    return title;
  }

  /** Retrieves the ImageView that contains the dialog icon. */
  public ImageView getIconView() {
    return icon;
  }

  /**
   * Retrieves the TextView that contains the dialog content. If you want to update the content
   * (message), use #{@link #setContent(CharSequence)} instead.
   */
  @Nullable
  public final TextView getContentView() {
    return content;
  }

  /**
   * Retrieves the custom view that was inflated or set to the MaterialDialog during building.
   *
   * @return The custom view that was passed into the Builder.
   */
  @Nullable
  public final View getCustomView() {
    return builder.customView;
  }

  /**
   * Updates an action button's title, causing invalidation to check if the action buttons should be
   * stacked. Setting an action button's text to null is a shortcut for hiding it, too.
   *
   * @param which The action button to update.
   * @param title The new title of the action button.
   */
  @UiThread
  public final void setActionButton(final DialogAction which, @Nullable final CharSequence title) {
    switch (which) {
      default:
        builder.positiveText = title;
        positiveButton.setText(title);
        positiveButton.setVisibility(title == null ? View.GONE : View.VISIBLE);
        break;
      case NEUTRAL:
        builder.neutralText = title;
        neutralButton.setText(title);
        neutralButton.setVisibility(title == null ? View.GONE : View.VISIBLE);
        break;
      case NEGATIVE:
        builder.negativeText = title;
        negativeButton.setText(title);
        negativeButton.setVisibility(title == null ? View.GONE : View.VISIBLE);
        break;
    }
  }

  /**
   * Updates an action button's title, causing invalidation to check if the action buttons should be
   * stacked.
   *
   * @param which The action button to update.
   * @param titleRes The string resource of the new title of the action button.
   */
  public final void setActionButton(DialogAction which, @StringRes int titleRes) {
    setActionButton(which, getContext().getText(titleRes));
  }

  /**
   * Gets whether or not the positive, neutral, or negative action button is visible.
   *
   * @return Whether or not 1 or more action buttons is visible.
   */
  public final boolean hasActionButtons() {
    return numberOfActionButtons() > 0;
  }

  /**
   * Gets the number of visible action buttons.
   *
   * @return 0 through 3, depending on how many should be or are visible.
   */
  @SuppressWarnings("WeakerAccess")
  public final int numberOfActionButtons() {
    int number = 0;
    if (positiveButton.getVisibility() == View.VISIBLE) {
      number++;
    }
    if (neutralButton.getVisibility() == View.VISIBLE) {
      number++;
    }
    if (negativeButton.getVisibility() == View.VISIBLE) {
      number++;
    }
    return number;
  }

  @UiThread
  @Override
  public final void setTitle(CharSequence newTitle) {
    title.setText(newTitle);
  }

  @UiThread
  @Override
  public final void setTitle(@StringRes int newTitleRes) {
    setTitle(getContext().getString(newTitleRes));
  }

  @UiThread
  public final void setTitle(@StringRes int newTitleRes, @Nullable Object... formatArgs) {
    setTitle(getContext().getString(newTitleRes, formatArgs));
  }

  @UiThread
  public void setIcon(@DrawableRes final int resId) {
    icon.setImageResource(resId);
    icon.setVisibility(resId != 0 ? View.VISIBLE : View.GONE);
  }

  @UiThread
  public void setIcon(@Nullable final Drawable d) {
    icon.setImageDrawable(d);
    icon.setVisibility(d != null ? View.VISIBLE : View.GONE);
  }

  @UiThread
  public void setIconAttribute(@AttrRes int attrId) {
    Drawable d = DialogUtils.resolveDrawable(getContext(), attrId);
    setIcon(d);
  }

  @UiThread
  public final void setContent(CharSequence newContent) {
    content.setText(newContent);
    content.setVisibility(TextUtils.isEmpty(newContent) ? View.GONE : View.VISIBLE);
  }

  @UiThread
  public final void setContent(@StringRes int newContentRes) {
    setContent(getContext().getString(newContentRes));
  }

  @UiThread
  public final void setContent(@StringRes int newContentRes, @Nullable Object... formatArgs) {
    setContent(getContext().getString(newContentRes, formatArgs));
  }

  @Nullable
  public final ArrayList<CharSequence> getItems() {
    return builder.items;
  }

  @UiThread
  public final void setItems(@Nullable CharSequence... items) {
    if (builder.adapter == null) {
      throw new IllegalStateException(
          "This MaterialDialog instance does not "
              + "yet have an adapter set to it. You cannot use setItems().");
    }
    if (items != null) {
      builder.items = new ArrayList<>(items.length);
      Collections.addAll(builder.items, items);
    } else {
      builder.items = null;
    }
    if (!(builder.adapter instanceof DefaultRvAdapter)) {
      throw new IllegalStateException(
          "When using a custom adapter, setItems() "
              + "cannot be used. Set items through the adapter instead.");
    }
    notifyItemsChanged();
  }

  @UiThread
  public final void notifyItemInserted(@IntRange(from = 0, to = Integer.MAX_VALUE) int index) {
    builder.adapter.notifyItemInserted(index);
  }

  @UiThread
  public final void notifyItemChanged(@IntRange(from = 0, to = Integer.MAX_VALUE) int index) {
    builder.adapter.notifyItemChanged(index);
  }

  @UiThread
  public final void notifyItemsChanged() {
    builder.adapter.notifyDataSetChanged();
  }

  public final int getCurrentProgress() {
    if (progressBar == null) {
      return -1;
    }
    return progressBar.getProgress();
  }

  public ProgressBar getProgressBar() {
    return progressBar;
  }

  public final void incrementProgress(final int by) {
    setProgress(getCurrentProgress() + by);
  }

  public final void setProgress(final int progress) {
    if (builder.progress <= -2) {
      Log.w(
          "MaterialDialog",
          "Calling setProgress(int) on an indeterminate progress dialog has no effect!");
      return;
    }
    progressBar.setProgress(progress);
    final String progressNumberFormat = builder.progressNumberFormat;
    final NumberFormat progressPercentFormat = builder.progressPercentFormat;
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            if (progressLabel != null) {
              progressLabel.setText(
                  progressPercentFormat.format(
                      (float) getCurrentProgress() / (float) getMaxProgress()));
            }
            if (progressMinMax != null) {
              progressMinMax.setText(
                  String.format(progressNumberFormat, getCurrentProgress(), getMaxProgress()));
            }
          }
        });
  }

  public final boolean isIndeterminateProgress() {
    return builder.indeterminateProgress;
  }

  public final int getMaxProgress() {
    if (progressBar == null) {
      return -1;
    }
    return progressBar.getMax();
  }

  public final void setMaxProgress(final int max) {
    if (builder.progress <= -2) {
      throw new IllegalStateException("Cannot use setMaxProgress() on this dialog.");
    }
    progressBar.setMax(max);
  }

  /**
   * Change the format of the small text showing the percentage of progress. The default is
   * NumberFormat.getPercentageInstance().
   */
  public final void setProgressPercentFormat(NumberFormat format) {
    builder.progressPercentFormat = format;
    setProgress(getCurrentProgress()); // invalidates display
  }

  /**
   * Change the format of the small text showing current and maximum units of progress. The default
   * is "%1d/%2d".
   */
  public final void setProgressNumberFormat(String format) {
    builder.progressNumberFormat = format;
    setProgress(getCurrentProgress()); // invalidates display
  }

  public final boolean isCancelled() {
    return !isShowing();
  }

  /**
   * Convenience method for getting the currently selected index of a single choice list.
   *
   * @return Currently selected index of a single choice list, or -1 if not showing a single choice
   *     list
   */
  public int getSelectedIndex() {
    if (builder.listCallbackSingleChoice != null) {
      return builder.selectedIndex;
    } else {
      return -1;
    }
  }

  /**
   * Convenience method for setting the currently selected index of a single choice list. This only
   * works if you are not using a custom adapter; if you're using a custom adapter, an
   * IllegalStateException is thrown. Note that this does not call the respective single choice
   * callback.
   *
   * @param index The index of the list item to check.
   */
  @UiThread
  public void setSelectedIndex(int index) {
    builder.selectedIndex = index;
    if (builder.adapter != null && builder.adapter instanceof DefaultRvAdapter) {
      builder.adapter.notifyDataSetChanged();
    } else {
      throw new IllegalStateException(
          "You can only use setSelectedIndex() " + "with the default adapter implementation.");
    }
  }

  /**
   * Convenience method for getting the currently selected indices of a multi choice list
   *
   * @return Currently selected index of a multi choice list, or null if not showing a multi choice
   *     list
   */
  @Nullable
  public Integer[] getSelectedIndices() {
    if (builder.listCallbackMultiChoice != null) {
      return selectedIndicesList.toArray(new Integer[selectedIndicesList.size()]);
    } else {
      return null;
    }
  }

  /**
   * Convenience method for setting the currently selected indices of a multi choice list. This only
   * works if you are not using a custom adapter; if you're using a custom adapter, an
   * IllegalStateException is thrown. Note that this does not call the respective multi choice
   * callback.
   *
   * @param indices The indices of the list items to check.
   */
  @UiThread
  public void setSelectedIndices(Integer[] indices) {
    selectedIndicesList = new ArrayList<>(Arrays.asList(indices));
    if (builder.adapter != null && builder.adapter instanceof DefaultRvAdapter) {
      builder.adapter.notifyDataSetChanged();
    } else {
      throw new IllegalStateException(
          "You can only use setSelectedIndices() " + "with the default adapter implementation.");
    }
  }

  /** Clears all selected checkboxes from multi choice list dialogs. */
  public void clearSelectedIndices() {
    clearSelectedIndices(true);
  }

  /**
   * Clears all selected checkboxes from multi choice list dialogs.
   *
   * @param sendCallback Defaults to true. True will notify the multi-choice callback, if any.
   */
  public void clearSelectedIndices(boolean sendCallback) {
    if (listType == null || listType != ListType.MULTI) {
      throw new IllegalStateException(
          "You can only use clearSelectedIndices() " + "with multi choice list dialogs.");
    }
    if (builder.adapter != null && builder.adapter instanceof DefaultRvAdapter) {
      if (selectedIndicesList != null) {
        selectedIndicesList.clear();
      }
      builder.adapter.notifyDataSetChanged();
      if (sendCallback && builder.listCallbackMultiChoice != null) {
        sendMultiChoiceCallback();
      }
    } else {
      throw new IllegalStateException(
          "You can only use clearSelectedIndices() " + "with the default adapter implementation.");
    }
  }

  /** Selects all checkboxes in multi choice list dialogs. */
  public void selectAllIndices() {
    selectAllIndices(true);
  }

  /**
   * Selects all checkboxes in multi choice list dialogs.
   *
   * @param sendCallback Defaults to true. True will notify the multi-choice callback, if any.
   */
  public void selectAllIndices(boolean sendCallback) {
    if (listType == null || listType != ListType.MULTI) {
      throw new IllegalStateException(
          "You can only use selectAllIndices() with " + "multi choice list dialogs.");
    }
    if (builder.adapter != null && builder.adapter instanceof DefaultRvAdapter) {
      if (selectedIndicesList == null) {
        selectedIndicesList = new ArrayList<>();
      }
      for (int i = 0; i < builder.adapter.getItemCount(); i++) {
        if (!selectedIndicesList.contains(i)) {
          selectedIndicesList.add(i);
        }
      }
      builder.adapter.notifyDataSetChanged();
      if (sendCallback && builder.listCallbackMultiChoice != null) {
        sendMultiChoiceCallback();
      }
    } else {
      throw new IllegalStateException(
          "You can only use selectAllIndices() with the " + "default adapter implementation.");
    }
  }

  @Override
  public final void onShow(DialogInterface dialog) {
    if (input != null) {
      DialogUtils.showKeyboard(this);
      if (input.getText().length() > 0) {
        input.setSelection(input.getText().length());
      }
    }
    super.onShow(dialog);
  }

  void setInternalInputCallback() {
    if (input == null) {
      return;
    }
    input.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            final int length = s.toString().length();
            boolean emptyDisabled = false;
            if (!builder.inputAllowEmpty) {
              emptyDisabled = length == 0;
              final View positiveAb = getActionButton(DialogAction.POSITIVE);
              positiveAb.setEnabled(!emptyDisabled);
            }
            invalidateInputMinMaxIndicator(length, emptyDisabled);
            if (builder.alwaysCallInputCallback) {
              builder.inputCallback.onInput(MaterialDialog.this, s);
            }
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });
  }

  void invalidateInputMinMaxIndicator(int currentLength, boolean emptyDisabled) {
    if (inputMinMax != null) {
      if (builder.inputMaxLength > 0) {
        inputMinMax.setText(
            String.format(Locale.getDefault(), "%d/%d", currentLength, builder.inputMaxLength));
        inputMinMax.setVisibility(View.VISIBLE);
      } else {
        inputMinMax.setVisibility(View.GONE);
      }
      final boolean isDisabled =
          (emptyDisabled && currentLength == 0)
              || (builder.inputMaxLength > 0 && currentLength > builder.inputMaxLength)
              || currentLength < builder.inputMinLength;
      final int colorText = isDisabled ? builder.inputRangeErrorColor : builder.contentColor;
      final int colorWidget = isDisabled ? builder.inputRangeErrorColor : builder.widgetColor;
      if (builder.inputMaxLength > 0) {
        inputMinMax.setTextColor(colorText);
      }
      MDTintHelper.setTint(input, colorWidget);
      final View positiveAb = getActionButton(DialogAction.POSITIVE);
      positiveAb.setEnabled(!isDisabled);
    }
  }

  @Override
  public void dismiss() {
    if (input != null) {
      DialogUtils.hideKeyboard(this);
    }
    super.dismiss();
  }

  enum ListType {
    REGULAR,
    SINGLE,
    MULTI;

    public static int getLayoutForType(ListType type) {
      switch (type) {
        case REGULAR:
          return R.layout.md_listitem;
        case SINGLE:
          return R.layout.md_listitem_singlechoice;
        case MULTI:
          return R.layout.md_listitem_multichoice;
        default:
          throw new IllegalArgumentException("Not a valid list type");
      }
    }
  }

  /** A callback used for regular list dialogs. */
  public interface ListCallback {
    void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text);
  }

  /** A callback used for regular list dialogs. */
  public interface ListLongCallback {
    boolean onLongSelection(MaterialDialog dialog, View itemView, int position, CharSequence text);
  }

  /** A callback used for multi choice (check box) list dialogs. */
  public interface ListCallbackSingleChoice {

    /**
     * Return true to allow the radio button to be checked, if the alwaysCallSingleChoice() option
     * is used.
     *
     * @param dialog The dialog of which a list item was selected.
     * @param which The index of the item that was selected.
     * @param text The text of the item that was selected.
     * @return True to allow the radio button to be selected.
     */
    boolean onSelection(
        MaterialDialog dialog, View itemView, int which, @Nullable CharSequence text);
  }

  /** A callback used for multi choice (check box) list dialogs. */
  public interface ListCallbackMultiChoice {

    /**
     * Return true to allow the check box to be checked, if the alwaysCallSingleChoice() option is
     * used.
     *
     * @param dialog The dialog of which a list item was selected.
     * @param which The indices of the items that were selected.
     * @param text The text of the items that were selected.
     * @return True to allow the checkbox to be selected.
     */
    boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text);
  }

  /** An alternate way to define a single callback. */
  public interface SingleButtonCallback {
    void onClick(MaterialDialog dialog, DialogAction which);
  }

  public interface InputCallback {
    void onInput(MaterialDialog dialog, CharSequence input);
  }

  private static class DialogException extends WindowManager.BadTokenException {
    DialogException(@SuppressWarnings("SameParameterValue") String message) {
      super(message);
    }
  }

  /** The class used to construct a MaterialDialog. */
  @SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "ConstantConditions"})
  public static class Builder {

    protected Context context;
    protected CharSequence title;
    protected GravityEnum titleGravity = GravityEnum.START;
    protected GravityEnum contentGravity = GravityEnum.START;
    protected GravityEnum btnStackedGravity = GravityEnum.END;
    protected GravityEnum itemsGravity = GravityEnum.START;
    protected GravityEnum buttonsGravity = GravityEnum.START;
    protected int buttonRippleColor = 0;
    protected int titleColor = -1;
    protected int contentColor = -1;
    protected CharSequence content;
    protected ArrayList<CharSequence> items;
    protected CharSequence positiveText;
    protected CharSequence neutralText;
    protected CharSequence negativeText;
    protected boolean positiveFocus;
    protected boolean neutralFocus;
    protected boolean negativeFocus;
    protected View customView;
    protected int widgetColor;
    protected ColorStateList choiceWidgetColor;
    protected ColorStateList positiveColor;
    protected ColorStateList negativeColor;
    protected ColorStateList neutralColor;
    protected ColorStateList linkColor;
    protected SingleButtonCallback onPositiveCallback;
    protected SingleButtonCallback onNegativeCallback;
    protected SingleButtonCallback onNeutralCallback;
    protected SingleButtonCallback onAnyCallback;
    protected ListCallback listCallback;
    protected ListLongCallback listLongCallback;
    protected ListCallbackSingleChoice listCallbackSingleChoice;
    protected ListCallbackMultiChoice listCallbackMultiChoice;
    protected boolean alwaysCallMultiChoiceCallback = false;
    protected boolean alwaysCallSingleChoiceCallback = false;
    protected Theme theme = Theme.LIGHT;
    protected boolean cancelable = true;
    protected boolean canceledOnTouchOutside = true;
    protected float contentLineSpacingMultiplier = 1.2f;
    protected int selectedIndex = -1;
    protected Integer[] selectedIndices = null;
    protected Integer[] disabledIndices = null;
    protected boolean autoDismiss = true;
    protected Typeface regularFont;
    protected Typeface mediumFont;
    protected Drawable icon;
    protected boolean limitIconToDefaultSize;
    protected int maxIconSize = -1;
    protected RecyclerView.Adapter<?> adapter;
    protected RecyclerView.LayoutManager layoutManager;
    protected OnDismissListener dismissListener;
    protected OnCancelListener cancelListener;
    protected OnKeyListener keyListener;
    protected OnShowListener showListener;
    protected StackingBehavior stackingBehavior;
    protected boolean wrapCustomViewInScroll;
    protected int dividerColor;
    protected int backgroundColor;
    protected int itemColor;
    protected boolean indeterminateProgress;
    protected boolean showMinMax;
    protected int progress = -2;
    protected int progressMax = 0;
    protected CharSequence inputPrefill;
    protected CharSequence inputHint;
    protected InputCallback inputCallback;
    protected boolean inputAllowEmpty;
    protected int inputType = -1;
    protected boolean alwaysCallInputCallback;
    protected int inputMinLength = -1;
    protected int inputMaxLength = -1;
    protected int inputRangeErrorColor = 0;
    protected int[] itemIds;
    protected CharSequence checkBoxPrompt;
    protected boolean checkBoxPromptInitiallyChecked;
    protected CheckBox.OnCheckedChangeListener checkBoxPromptListener;
    protected InputFilter[] inputFilters;

    protected String progressNumberFormat;
    protected NumberFormat progressPercentFormat;
    protected boolean indeterminateIsHorizontalProgress;

    protected boolean titleColorSet = false;
    protected boolean contentColorSet = false;
    protected boolean itemColorSet = false;
    protected boolean positiveColorSet = false;
    protected boolean neutralColorSet = false;
    protected boolean negativeColorSet = false;
    protected boolean widgetColorSet = false;
    protected boolean dividerColorSet = false;

    @DrawableRes protected int listSelector;
    @DrawableRes protected int btnSelectorStacked;
    @DrawableRes protected int btnSelectorPositive;
    @DrawableRes protected int btnSelectorNeutral;
    @DrawableRes protected int btnSelectorNegative;

    protected Object tag;

    public Builder(Context context) {
      this.context = context;
      final int materialBlue = DialogUtils.getColor(context, R.color.md_material_blue_600);

      // Retrieve default accent colors, which are used on the action buttons and progress bars
      this.widgetColor = DialogUtils.resolveColor(context, R.attr.colorAccent, materialBlue);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.widgetColor =
            DialogUtils.resolveColor(context, android.R.attr.colorAccent, this.widgetColor);
      }

      this.positiveColor = DialogUtils.getActionTextStateList(context, this.widgetColor);
      this.negativeColor = DialogUtils.getActionTextStateList(context, this.widgetColor);
      this.neutralColor = DialogUtils.getActionTextStateList(context, this.widgetColor);
      this.linkColor =
          DialogUtils.getActionTextStateList(
              context, DialogUtils.resolveColor(context, R.attr.md_link_color, this.widgetColor));

      int fallback = 0;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        fallback = DialogUtils.resolveColor(context, android.R.attr.colorControlHighlight);
      }
      this.buttonRippleColor =
          DialogUtils.resolveColor(
              context,
              R.attr.md_btn_ripple_color,
              DialogUtils.resolveColor(context, R.attr.colorControlHighlight, fallback));

      this.progressPercentFormat = NumberFormat.getPercentInstance();
      this.progressNumberFormat = "%1d/%2d";

      // Set the default theme based on the Activity theme's primary color darkness (more white or
      // more black)
      final int primaryTextColor =
          DialogUtils.resolveColor(context, android.R.attr.textColorPrimary);
      this.theme = DialogUtils.isColorDark(primaryTextColor) ? Theme.LIGHT : Theme.DARK;

      // Load theme values from the ThemeSingleton if needed
      checkSingleton();

      // Retrieve gravity settings from global theme attributes if needed
      this.titleGravity =
          DialogUtils.resolveGravityEnum(context, R.attr.md_title_gravity, this.titleGravity);
      this.contentGravity =
          DialogUtils.resolveGravityEnum(context, R.attr.md_content_gravity, this.contentGravity);
      this.btnStackedGravity =
          DialogUtils.resolveGravityEnum(
              context, R.attr.md_btnstacked_gravity, this.btnStackedGravity);
      this.itemsGravity =
          DialogUtils.resolveGravityEnum(context, R.attr.md_items_gravity, this.itemsGravity);
      this.buttonsGravity =
          DialogUtils.resolveGravityEnum(context, R.attr.md_buttons_gravity, this.buttonsGravity);

      final String mediumFont = DialogUtils.resolveString(context, R.attr.md_medium_font);
      final String regularFont = DialogUtils.resolveString(context, R.attr.md_regular_font);
      try {
        typeface(mediumFont, regularFont);
      } catch (Throwable ignored) {
      }

      if (this.mediumFont == null) {
        try {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL);
          } else {
            this.mediumFont = Typeface.create("sans-serif", Typeface.BOLD);
          }
        } catch (Throwable ignored) {
          this.mediumFont = Typeface.DEFAULT_BOLD;
        }
      }
      if (this.regularFont == null) {
        try {
          this.regularFont = Typeface.create("sans-serif", Typeface.NORMAL);
        } catch (Throwable ignored) {
          this.regularFont = Typeface.SANS_SERIF;
          if (this.regularFont == null) {
            this.regularFont = Typeface.DEFAULT;
          }
        }
      }
    }

    public final Context getContext() {
      return context;
    }

    public final int getItemColor() {
      return itemColor;
    }

    public final Typeface getRegularFont() {
      return regularFont;
    }

    @SuppressWarnings("ConstantConditions")
    private void checkSingleton() {
      if (ThemeSingleton.get(false) == null) {
        return;
      }
      ThemeSingleton s = ThemeSingleton.get();
      if (s.darkTheme) {
        this.theme = Theme.DARK;
      }
      if (s.titleColor != 0) {
        this.titleColor = s.titleColor;
      }
      if (s.contentColor != 0) {
        this.contentColor = s.contentColor;
      }
      if (s.positiveColor != null) {
        this.positiveColor = s.positiveColor;
      }
      if (s.neutralColor != null) {
        this.neutralColor = s.neutralColor;
      }
      if (s.negativeColor != null) {
        this.negativeColor = s.negativeColor;
      }
      if (s.itemColor != 0) {
        this.itemColor = s.itemColor;
      }
      if (s.icon != null) {
        this.icon = s.icon;
      }
      if (s.backgroundColor != 0) {
        this.backgroundColor = s.backgroundColor;
      }
      if (s.dividerColor != 0) {
        this.dividerColor = s.dividerColor;
      }
      if (s.btnSelectorStacked != 0) {
        this.btnSelectorStacked = s.btnSelectorStacked;
      }
      if (s.listSelector != 0) {
        this.listSelector = s.listSelector;
      }
      if (s.btnSelectorPositive != 0) {
        this.btnSelectorPositive = s.btnSelectorPositive;
      }
      if (s.btnSelectorNeutral != 0) {
        this.btnSelectorNeutral = s.btnSelectorNeutral;
      }
      if (s.btnSelectorNegative != 0) {
        this.btnSelectorNegative = s.btnSelectorNegative;
      }
      if (s.widgetColor != 0) {
        this.widgetColor = s.widgetColor;
      }
      if (s.linkColor != null) {
        this.linkColor = s.linkColor;
      }
      this.titleGravity = s.titleGravity;
      this.contentGravity = s.contentGravity;
      this.btnStackedGravity = s.btnStackedGravity;
      this.itemsGravity = s.itemsGravity;
      this.buttonsGravity = s.buttonsGravity;
    }

    public Builder title(@StringRes int titleRes) {
      title(this.context.getText(titleRes));
      return this;
    }

    public Builder title(CharSequence title) {
      this.title = title;
      return this;
    }

    public Builder titleGravity(GravityEnum gravity) {
      this.titleGravity = gravity;
      return this;
    }

    public Builder buttonRippleColor(@ColorInt int color) {
      this.buttonRippleColor = color;
      return this;
    }

    public Builder buttonRippleColorRes(@ColorRes int colorRes) {
      return buttonRippleColor(DialogUtils.getColor(this.context, colorRes));
    }

    public Builder buttonRippleColorAttr(@AttrRes int colorAttr) {
      return buttonRippleColor(DialogUtils.resolveColor(this.context, colorAttr));
    }

    public Builder titleColor(@ColorInt int color) {
      this.titleColor = color;
      this.titleColorSet = true;
      return this;
    }

    public Builder titleColorRes(@ColorRes int colorRes) {
      return titleColor(DialogUtils.getColor(this.context, colorRes));
    }

    public Builder titleColorAttr(@AttrRes int colorAttr) {
      return titleColor(DialogUtils.resolveColor(this.context, colorAttr));
    }

    /**
     * Sets the fonts used in the dialog. It's recommended that you use {@link #typeface(String,
     * String)} instead, to avoid duplicate Typeface allocations and high memory usage.
     *
     * @param medium The font used on titles and action buttons. Null uses device default.
     * @param regular The font used everywhere else, like on the content and list items. Null uses
     *     device default.
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder typeface(@Nullable Typeface medium, @Nullable Typeface regular) {
      this.mediumFont = medium;
      this.regularFont = regular;
      return this;
    }

    /**
     * Sets the fonts used in the dialog, by file names. This also uses TypefaceHelper in order to
     * avoid any un-needed allocations (it recycles typefaces for you).
     *
     * @param medium The name of font in assets/fonts used on titles and action buttons (null uses
     *     device default). E.g. [your-project]/app/main/assets/fonts/[medium]
     * @param regular The name of font in assets/fonts used everywhere else, like content and list
     *     items (null uses device default). E.g. [your-project]/app/main/assets/fonts/[regular]
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder typeface(@Nullable String medium, @Nullable String regular) {
      if (medium != null && !medium.trim().isEmpty()) {
        this.mediumFont = TypefaceHelper.get(this.context, medium);
        if (this.mediumFont == null) {
          throw new IllegalArgumentException("No font asset found for \"" + medium + "\"");
        }
      }
      if (regular != null && !regular.trim().isEmpty()) {
        this.regularFont = TypefaceHelper.get(this.context, regular);
        if (this.regularFont == null) {
          throw new IllegalArgumentException("No font asset found for \"" + regular + "\"");
        }
      }
      return this;
    }

    public Builder icon(Drawable icon) {
      this.icon = icon;
      return this;
    }

    public Builder iconRes(@DrawableRes int icon) {
      this.icon = ResourcesCompat.getDrawable(context.getResources(), icon, null);
      return this;
    }

    public Builder iconAttr(@AttrRes int iconAttr) {
      this.icon = DialogUtils.resolveDrawable(context, iconAttr);
      return this;
    }

    public Builder content(@StringRes int contentRes) {
      return content(contentRes, false);
    }

    public Builder content(@StringRes int contentRes, boolean html) {
      CharSequence text = this.context.getText(contentRes);
      if (html) {
        text = Html.fromHtml(text.toString().replace("\n", "<br/>"));
      }
      return content(text);
    }

    public Builder content(CharSequence content) {
      if (this.customView != null) {
        throw new IllegalStateException(
            "You cannot set content() " + "when you're using a custom view.");
      }
      this.content = content;
      return this;
    }

    public Builder content(@StringRes int contentRes, Object... formatArgs) {
      String str =
          String.format(this.context.getString(contentRes), formatArgs).replace("\n", "<br/>");
      //noinspection deprecation
      return content(Html.fromHtml(str));
    }

    public Builder contentColor(@ColorInt int color) {
      this.contentColor = color;
      this.contentColorSet = true;
      return this;
    }

    public Builder contentColorRes(@ColorRes int colorRes) {
      contentColor(DialogUtils.getColor(this.context, colorRes));
      return this;
    }

    public Builder contentColorAttr(@AttrRes int colorAttr) {
      contentColor(DialogUtils.resolveColor(this.context, colorAttr));
      return this;
    }

    public Builder contentGravity(GravityEnum gravity) {
      this.contentGravity = gravity;
      return this;
    }

    public Builder contentLineSpacing(float multiplier) {
      this.contentLineSpacingMultiplier = multiplier;
      return this;
    }

    public Builder items(Collection collection) {
      if (collection.size() > 0) {
        final CharSequence[] array = new CharSequence[collection.size()];
        int i = 0;
        for (Object obj : collection) {
          array[i] = obj.toString();
          i++;
        }
        items(array);
      } else if (collection.size() == 0) {
        items = new ArrayList<>();
      }
      return this;
    }

    public Builder items(@ArrayRes int itemsRes) {
      items(this.context.getResources().getTextArray(itemsRes));
      return this;
    }

    public Builder items(CharSequence... items) {
      if (this.customView != null) {
        throw new IllegalStateException(
            "You cannot set items()" + " when you're using a custom view.");
      }
      this.items = new ArrayList<>();
      Collections.addAll(this.items, items);
      return this;
    }

    public Builder itemsCallback(ListCallback callback) {
      this.listCallback = callback;
      this.listCallbackSingleChoice = null;
      this.listCallbackMultiChoice = null;
      return this;
    }

    public Builder itemsLongCallback(ListLongCallback callback) {
      this.listLongCallback = callback;
      this.listCallbackSingleChoice = null;
      this.listCallbackMultiChoice = null;
      return this;
    }

    public Builder itemsColor(@ColorInt int color) {
      this.itemColor = color;
      this.itemColorSet = true;
      return this;
    }

    public Builder itemsColorRes(@ColorRes int colorRes) {
      return itemsColor(DialogUtils.getColor(this.context, colorRes));
    }

    public Builder itemsColorAttr(@AttrRes int colorAttr) {
      return itemsColor(DialogUtils.resolveColor(this.context, colorAttr));
    }

    public Builder itemsGravity(GravityEnum gravity) {
      this.itemsGravity = gravity;
      return this;
    }

    public Builder itemsIds(int[] idsArray) {
      this.itemIds = idsArray;
      return this;
    }

    public Builder itemsIds(@ArrayRes int idsArrayRes) {
      return itemsIds(context.getResources().getIntArray(idsArrayRes));
    }

    public Builder buttonsGravity(GravityEnum gravity) {
      this.buttonsGravity = gravity;
      return this;
    }

    /**
     * Pass anything below 0 (such as -1) for the selected index to leave all options unselected
     * initially. Otherwise pass the index of an item that will be selected initially.
     *
     * @param selectedIndex The checkbox index that will be selected initially.
     * @param callback The callback that will be called when the presses the positive button.
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder itemsCallbackSingleChoice(int selectedIndex, ListCallbackSingleChoice callback) {
      this.selectedIndex = selectedIndex;
      this.listCallback = null;
      this.listCallbackSingleChoice = callback;
      this.listCallbackMultiChoice = null;
      return this;
    }

    /**
     * By default, the single choice callback is only called when the user clicks the positive
     * button or if there are no buttons. Call this to force it to always call on item clicks even
     * if the positive button exists.
     *
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder alwaysCallSingleChoiceCallback() {
      this.alwaysCallSingleChoiceCallback = true;
      return this;
    }

    /**
     * Pass null for the selected indices to leave all options unselected initially. Otherwise pass
     * an array of indices that will be selected initially.
     *
     * @param selectedIndices The radio button indices that will be selected initially.
     * @param callback The callback that will be called when the presses the positive button.
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder itemsCallbackMultiChoice(
        @Nullable Integer[] selectedIndices, ListCallbackMultiChoice callback) {
      this.selectedIndices = selectedIndices;
      this.listCallback = null;
      this.listCallbackSingleChoice = null;
      this.listCallbackMultiChoice = callback;
      return this;
    }

    /**
     * Sets indices of items that are not clickable. If they are checkboxes or radio buttons, they
     * will not be toggleable.
     *
     * @param disabledIndices The item indices that will be disabled from selection.
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder itemsDisabledIndices(@Nullable Integer... disabledIndices) {
      this.disabledIndices = disabledIndices;
      return this;
    }

    /**
     * By default, the multi choice callback is only called when the user clicks the positive button
     * or if there are no buttons. Call this to force it to always call on item clicks even if the
     * positive button exists.
     *
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder alwaysCallMultiChoiceCallback() {
      this.alwaysCallMultiChoiceCallback = true;
      return this;
    }

    public Builder positiveText(@StringRes int positiveRes) {
      if (positiveRes == 0) {
        return this;
      }
      positiveText(this.context.getText(positiveRes));
      return this;
    }

    public Builder positiveText(CharSequence message) {
      this.positiveText = message;
      return this;
    }

    public Builder positiveColor(@ColorInt int color) {
      return positiveColor(DialogUtils.getActionTextStateList(context, color));
    }

    public Builder positiveColorRes(@ColorRes int colorRes) {
      return positiveColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
    }

    public Builder positiveColorAttr(@AttrRes int colorAttr) {
      return positiveColor(
          DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
    }

    public Builder positiveColor(ColorStateList colorStateList) {
      this.positiveColor = colorStateList;
      this.positiveColorSet = true;
      return this;
    }

    public Builder positiveFocus(boolean isFocusedDefault) {
      this.positiveFocus = isFocusedDefault;
      return this;
    }

    public Builder neutralText(@StringRes int neutralRes) {
      if (neutralRes == 0) {
        return this;
      }
      return neutralText(this.context.getText(neutralRes));
    }

    public Builder neutralText(CharSequence message) {
      this.neutralText = message;
      return this;
    }

    public Builder negativeColor(@ColorInt int color) {
      return negativeColor(DialogUtils.getActionTextStateList(context, color));
    }

    public Builder negativeColorRes(@ColorRes int colorRes) {
      return negativeColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
    }

    public Builder negativeColorAttr(@AttrRes int colorAttr) {
      return negativeColor(
          DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
    }

    public Builder negativeColor(ColorStateList colorStateList) {
      this.negativeColor = colorStateList;
      this.negativeColorSet = true;
      return this;
    }

    public Builder negativeText(@StringRes int negativeRes) {
      if (negativeRes == 0) {
        return this;
      }
      return negativeText(this.context.getText(negativeRes));
    }

    public Builder negativeText(CharSequence message) {
      this.negativeText = message;
      return this;
    }

    public Builder negativeFocus(boolean isFocusedDefault) {
      this.negativeFocus = isFocusedDefault;
      return this;
    }

    public Builder neutralColor(@ColorInt int color) {
      return neutralColor(DialogUtils.getActionTextStateList(context, color));
    }

    public Builder neutralColorRes(@ColorRes int colorRes) {
      return neutralColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
    }

    public Builder neutralColorAttr(@AttrRes int colorAttr) {
      return neutralColor(
          DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
    }

    public Builder neutralColor(ColorStateList colorStateList) {
      this.neutralColor = colorStateList;
      this.neutralColorSet = true;
      return this;
    }

    public Builder neutralFocus(boolean isFocusedDefault) {
      this.neutralFocus = isFocusedDefault;
      return this;
    }

    public Builder linkColor(@ColorInt int color) {
      return linkColor(DialogUtils.getActionTextStateList(context, color));
    }

    public Builder linkColorRes(@ColorRes int colorRes) {
      return linkColor(DialogUtils.getActionTextColorStateList(this.context, colorRes));
    }

    public Builder linkColorAttr(@AttrRes int colorAttr) {
      return linkColor(DialogUtils.resolveActionTextColorStateList(this.context, colorAttr, null));
    }

    public Builder linkColor(ColorStateList colorStateList) {
      this.linkColor = colorStateList;
      return this;
    }

    public Builder listSelector(@DrawableRes int selectorRes) {
      this.listSelector = selectorRes;
      return this;
    }

    public Builder btnSelectorStacked(@DrawableRes int selectorRes) {
      this.btnSelectorStacked = selectorRes;
      return this;
    }

    public Builder btnSelector(@DrawableRes int selectorRes) {
      this.btnSelectorPositive = selectorRes;
      this.btnSelectorNeutral = selectorRes;
      this.btnSelectorNegative = selectorRes;
      return this;
    }

    public Builder btnSelector(@DrawableRes int selectorRes, DialogAction which) {
      switch (which) {
        default:
          this.btnSelectorPositive = selectorRes;
          break;
        case NEUTRAL:
          this.btnSelectorNeutral = selectorRes;
          break;
        case NEGATIVE:
          this.btnSelectorNegative = selectorRes;
          break;
      }
      return this;
    }

    /**
     * Sets the gravity used for the text in stacked action buttons. By default, it's #{@link
     * GravityEnum#END}.
     *
     * @param gravity The gravity to use.
     * @return The Builder instance so calls can be chained.
     */
    public Builder btnStackedGravity(GravityEnum gravity) {
      this.btnStackedGravity = gravity;
      return this;
    }

    public Builder checkBoxPrompt(
        CharSequence prompt,
        boolean initiallyChecked,
        @Nullable CheckBox.OnCheckedChangeListener checkListener) {
      this.checkBoxPrompt = prompt;
      this.checkBoxPromptInitiallyChecked = initiallyChecked;
      this.checkBoxPromptListener = checkListener;
      return this;
    }

    public Builder checkBoxPromptRes(
        @StringRes int prompt,
        boolean initiallyChecked,
        @Nullable CheckBox.OnCheckedChangeListener checkListener) {
      return checkBoxPrompt(
          context.getResources().getText(prompt), initiallyChecked, checkListener);
    }

    public Builder customView(@LayoutRes int layoutRes, boolean wrapInScrollView) {
      LayoutInflater li = LayoutInflater.from(this.context);
      return customView(li.inflate(layoutRes, null), wrapInScrollView);
    }

    public Builder customView(View view, boolean wrapInScrollView) {
      if (this.content != null) {
        throw new IllegalStateException("You cannot use customView() when you have content set.");
      } else if (this.items != null) {
        throw new IllegalStateException("You cannot use customView() when you have items set.");
      } else if (this.inputCallback != null) {
        throw new IllegalStateException("You cannot use customView() with an input dialog");
      } else if (this.progress > -2 || this.indeterminateProgress) {
        throw new IllegalStateException("You cannot use customView() with a progress dialog");
      }
      if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
        ((ViewGroup) view.getParent()).removeView(view);
      }
      this.customView = view;
      this.wrapCustomViewInScroll = wrapInScrollView;
      return this;
    }

    /**
     * Makes this dialog a progress dialog.
     *
     * @param indeterminate If true, an infinite circular spinner is shown. If false, a horizontal
     *     progress bar is shown that is incremented or set via the built MaterialDialog instance.
     * @param max When indeterminate is false, the max value the horizontal progress bar can get to.
     * @return An instance of the Builder so calls can be chained.
     */
    public Builder progress(boolean indeterminate, int max) {
      if (this.customView != null) {
        throw new IllegalStateException(
            "You cannot set progress() when you're using a custom view.");
      }
      if (indeterminate) {
        this.indeterminateProgress = true;
        this.progress = -2;
      } else {
        this.indeterminateIsHorizontalProgress = false;
        this.indeterminateProgress = false;
        this.progress = -1;
        this.progressMax = max;
      }
      return this;
    }

    /**
     * Makes this dialog a progress dialog.
     *
     * @param indeterminate If true, an infinite circular spinner is shown. If false, a horizontal
     *     progress bar is shown that is incremented or set via the built MaterialDialog instance.
     * @param max When indeterminate is false, the max value the horizontal progress bar can get to.
     * @param showMinMax For determinate dialogs, the min and max will be displayed to the left
     *     (start) of the progress bar, e.g. 50/100.
     * @return An instance of the Builder so calls can be chained.
     */
    public Builder progress(boolean indeterminate, int max, boolean showMinMax) {
      this.showMinMax = showMinMax;
      return progress(indeterminate, max);
    }

    /**
     * hange the format of the small text showing current and maximum units of progress. The default
     * is "%1d/%2d".
     */
    public Builder progressNumberFormat(String format) {
      this.progressNumberFormat = format;
      return this;
    }

    /**
     * Change the format of the small text showing the percentage of progress. The default is
     * NumberFormat.getPercentageInstance().
     */
    public Builder progressPercentFormat(NumberFormat format) {
      this.progressPercentFormat = format;
      return this;
    }

    /**
     * By default, indeterminate progress dialogs will use a circular indicator. You can change it
     * to use a horizontal progress indicator.
     */
    public Builder progressIndeterminateStyle(boolean horizontal) {
      this.indeterminateIsHorizontalProgress = horizontal;
      return this;
    }

    public Builder widgetColor(@ColorInt int color) {
      this.widgetColor = color;
      this.widgetColorSet = true;
      return this;
    }

    public Builder widgetColorRes(@ColorRes int colorRes) {
      return widgetColor(DialogUtils.getColor(this.context, colorRes));
    }

    public Builder widgetColorAttr(@AttrRes int colorAttr) {
      return widgetColor(DialogUtils.resolveColor(this.context, colorAttr));
    }

    public Builder choiceWidgetColor(@Nullable ColorStateList colorStateList) {
      this.choiceWidgetColor = colorStateList;
      return this;
    }

    public Builder dividerColor(@ColorInt int color) {
      this.dividerColor = color;
      this.dividerColorSet = true;
      return this;
    }

    public Builder dividerColorRes(@ColorRes int colorRes) {
      return dividerColor(DialogUtils.getColor(this.context, colorRes));
    }

    public Builder dividerColorAttr(@AttrRes int colorAttr) {
      return dividerColor(DialogUtils.resolveColor(this.context, colorAttr));
    }

    public Builder backgroundColor(@ColorInt int color) {
      this.backgroundColor = color;
      return this;
    }

    public Builder backgroundColorRes(@ColorRes int colorRes) {
      return backgroundColor(DialogUtils.getColor(this.context, colorRes));
    }

    public Builder backgroundColorAttr(@AttrRes int colorAttr) {
      return backgroundColor(DialogUtils.resolveColor(this.context, colorAttr));
    }

    public Builder onPositive(SingleButtonCallback callback) {
      this.onPositiveCallback = callback;
      return this;
    }

    public Builder onNegative(SingleButtonCallback callback) {
      this.onNegativeCallback = callback;
      return this;
    }

    public Builder onNeutral(SingleButtonCallback callback) {
      this.onNeutralCallback = callback;
      return this;
    }

    public Builder onAny(SingleButtonCallback callback) {
      this.onAnyCallback = callback;
      return this;
    }

    public Builder theme(Theme theme) {
      this.theme = theme;
      return this;
    }

    public Builder cancelable(boolean cancelable) {
      this.cancelable = cancelable;
      this.canceledOnTouchOutside = cancelable;
      return this;
    }

    public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside) {
      this.canceledOnTouchOutside = canceledOnTouchOutside;
      return this;
    }

    /**
     * This defaults to true. If set to false, the dialog will not automatically be dismissed when
     * an action button is pressed, and not automatically dismissed when the user selects a list
     * item.
     *
     * @param dismiss Whether or not to dismiss the dialog automatically.
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder autoDismiss(boolean dismiss) {
      this.autoDismiss = dismiss;
      return this;
    }

    /**
     * Sets a custom {@link android.support.v7.widget.RecyclerView.Adapter} for the dialog's list
     *
     * @param adapter The adapter to set to the list.
     * @param layoutManager The layout manager to use in the RecyclerView. Pass null to use the
     *     default linear manager.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder adapter(
        RecyclerView.Adapter<?> adapter, @Nullable RecyclerView.LayoutManager layoutManager) {
      if (this.customView != null) {
        throw new IllegalStateException(
            "You cannot set adapter() when " + "you're using a custom view.");
      }
      if (layoutManager != null
          && !(layoutManager instanceof LinearLayoutManager)
          && !(layoutManager instanceof GridLayoutManager)) {
        throw new IllegalStateException(
            "You can currently only use LinearLayoutManager"
                + " and GridLayoutManager with this library.");
      }
      this.adapter = adapter;
      this.layoutManager = layoutManager;
      return this;
    }

    /** Limits the display size of a set icon to 48dp. */
    public Builder limitIconToDefaultSize() {
      this.limitIconToDefaultSize = true;
      return this;
    }

    public Builder maxIconSize(int maxIconSize) {
      this.maxIconSize = maxIconSize;
      return this;
    }

    public Builder maxIconSizeRes(@DimenRes int maxIconSizeRes) {
      return maxIconSize((int) this.context.getResources().getDimension(maxIconSizeRes));
    }

    public Builder showListener(OnShowListener listener) {
      this.showListener = listener;
      return this;
    }

    public Builder dismissListener(OnDismissListener listener) {
      this.dismissListener = listener;
      return this;
    }

    public Builder cancelListener(OnCancelListener listener) {
      this.cancelListener = listener;
      return this;
    }

    public Builder keyListener(OnKeyListener listener) {
      this.keyListener = listener;
      return this;
    }

    /**
     * Sets action button stacking behavior.
     *
     * @param behavior The behavior of the action button stacking logic.
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder stackingBehavior(StackingBehavior behavior) {
      this.stackingBehavior = behavior;
      return this;
    }

    public Builder input(
        @Nullable CharSequence hint,
        @Nullable CharSequence prefill,
        boolean allowEmptyInput,
        InputCallback callback) {
      if (this.customView != null) {
        throw new IllegalStateException(
            "You cannot set content() when " + "you're using a custom view.");
      }
      this.inputCallback = callback;
      this.inputHint = hint;
      this.inputPrefill = prefill;
      this.inputAllowEmpty = allowEmptyInput;
      return this;
    }

    public Builder input(
        @Nullable CharSequence hint, @Nullable CharSequence prefill, InputCallback callback) {
      return input(hint, prefill, true, callback);
    }

    public Builder input(
        @StringRes int hint,
        @StringRes int prefill,
        boolean allowEmptyInput,
        InputCallback callback) {
      return input(
          hint == 0 ? null : context.getText(hint),
          prefill == 0 ? null : context.getText(prefill),
          allowEmptyInput,
          callback);
    }

    public Builder input(@StringRes int hint, @StringRes int prefill, InputCallback callback) {
      return input(hint, prefill, true, callback);
    }

    public Builder inputType(int type) {
      this.inputType = type;
      return this;
    }

    public Builder inputRange(
        @IntRange(from = 0, to = Integer.MAX_VALUE) int minLength,
        @IntRange(from = -1, to = Integer.MAX_VALUE) int maxLength) {
      return inputRange(minLength, maxLength, 0);
    }

    /** @param errorColor Pass in 0 for the default red error color (as specified in guidelines). */
    public Builder inputRange(
        @IntRange(from = 0, to = Integer.MAX_VALUE) int minLength,
        @IntRange(from = -1, to = Integer.MAX_VALUE) int maxLength,
        @ColorInt int errorColor) {
      if (minLength < 0) {
        throw new IllegalArgumentException(
            "Min length for input dialogs " + "cannot be less than 0.");
      }
      this.inputMinLength = minLength;
      this.inputMaxLength = maxLength;
      if (errorColor == 0) {
        this.inputRangeErrorColor = DialogUtils.getColor(context, R.color.md_edittext_error);
      } else {
        this.inputRangeErrorColor = errorColor;
      }
      if (this.inputMinLength > 0) {
        this.inputAllowEmpty = false;
      }
      return this;
    }

    /**
     * Same as #{@link #inputRange(int, int, int)}, but it takes a color resource ID for the error
     * color.
     */
    public Builder inputRangeRes(
        @IntRange(from = 0, to = Integer.MAX_VALUE) int minLength,
        @IntRange(from = -1, to = Integer.MAX_VALUE) int maxLength,
        @ColorRes int errorColor) {
      return inputRange(minLength, maxLength, DialogUtils.getColor(context, errorColor));
    }

    public Builder inputFilters(@Nullable InputFilter... filters) {
      this.inputFilters = filters;
      return this;
    }

    public Builder alwaysCallInputCallback() {
      this.alwaysCallInputCallback = true;
      return this;
    }

    public Builder tag(@Nullable Object tag) {
      this.tag = tag;
      return this;
    }

    @UiThread
    public MaterialDialog build() {
      return new MaterialDialog(this);
    }

    @UiThread
    public MaterialDialog show() {
      MaterialDialog dialog = build();
      dialog.show();
      return dialog;
    }
  }
}
