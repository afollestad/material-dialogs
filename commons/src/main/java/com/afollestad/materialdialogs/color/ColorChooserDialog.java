package com.afollestad.materialdialogs.color;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.commons.R;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;
import java.io.Serializable;
import java.util.Locale;

/** @author Aidan Follestad (afollestad) */
@SuppressWarnings({"FieldCanBeLocal", "ConstantConditions", "unused"})
public class ColorChooserDialog extends DialogFragment
    implements View.OnClickListener, View.OnLongClickListener {

  private int[] colorsTop;
  @Nullable private int[][] colorsSub;
  private int circleSize;
  private ColorCallback callback;
  private GridView grid;
  private View colorChooserCustomFrame;
  private EditText customColorHex;
  private View customColorIndicator;
  private TextWatcher customColorTextWatcher;
  private SeekBar customSeekA;
  private TextView customSeekAValue;
  private SeekBar customSeekR;
  private TextView customSeekRValue;
  private SeekBar customSeekG;
  private TextView customSeekGValue;
  private SeekBar customSeekB;
  private TextView customSeekBValue;
  private SeekBar.OnSeekBarChangeListener customColorRgbListener;
  private int selectedCustomColor;

  public ColorChooserDialog() {}

  @Nullable
  public static ColorChooserDialog findVisible(FragmentManager fragmentManager, Tag tag) {
    Fragment frag = fragmentManager.findFragmentByTag(tag.toString());
    if (frag != null && frag instanceof ColorChooserDialog) {
      return (ColorChooserDialog) frag;
    }
    return null;
  }

  private void generateColors() {
    Builder builder = getBuilder();
    if (builder.colorsTop != null) {
      colorsTop = builder.colorsTop;
      colorsSub = builder.colorsSub;
      return;
    }

    if (builder.accentMode) {
      colorsTop = ColorPalette.ACCENT_COLORS;
      colorsSub = ColorPalette.ACCENT_COLORS_SUB;
    } else {
      colorsTop = ColorPalette.PRIMARY_COLORS;
      colorsSub = ColorPalette.PRIMARY_COLORS_SUB;
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("top_index", topIndex());
    outState.putBoolean("in_sub", isInSub());
    outState.putInt("sub_index", subIndex());
    outState.putBoolean(
        "in_custom",
        colorChooserCustomFrame != null && colorChooserCustomFrame.getVisibility() == View.VISIBLE);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (getActivity() instanceof ColorCallback) {
      callback = (ColorCallback) getActivity();
    } else if (getTargetFragment() instanceof ColorCallback) {
      callback = (ColorCallback) getTargetFragment();
    } else {
      throw new IllegalStateException(
          "ColorChooserDialog needs to be shown from an Activity/Fragment implementing ColorCallback.");
    }
  }

  private boolean isInSub() {
    return getArguments().getBoolean("in_sub", false);
  }

  private void isInSub(boolean value) {
    getArguments().putBoolean("in_sub", value);
  }

  private int topIndex() {
    return getArguments().getInt("top_index", -1);
  }

  private void topIndex(int value) {
    if (value > -1) {
      findSubIndexForColor(value, colorsTop[value]);
    }
    getArguments().putInt("top_index", value);
  }

  private int subIndex() {
    if (colorsSub == null) {
      return -1;
    }
    return getArguments().getInt("sub_index", -1);
  }

  private void subIndex(int value) {
    if (colorsSub == null) {
      return;
    }
    getArguments().putInt("sub_index", value);
  }

  @StringRes
  public int getTitle() {
    Builder builder = getBuilder();
    int title;
    if (isInSub()) {
      title = builder.titleSub;
    } else {
      title = builder.title;
    }
    if (title == 0) {
      title = builder.title;
    }
    return title;
  }

  public String tag() {
    Builder builder = getBuilder();
    if (builder.tag != null) {
      return builder.tag;
    } else {
      return super.getTag();
    }
  }

  public boolean isAccentMode() {
    return getBuilder().accentMode;
  }

  @Override
  public void onClick(View v) {
    if (v.getTag() != null) {
      final String[] tag = ((String) v.getTag()).split(":");
      final int index = Integer.parseInt(tag[0]);
      final MaterialDialog dialog = (MaterialDialog) getDialog();
      final Builder builder = getBuilder();

      if (isInSub()) {
        subIndex(index);
      } else {
        topIndex(index);
        if (colorsSub != null && index < colorsSub.length) {
          dialog.setActionButton(DialogAction.NEGATIVE, builder.backBtn);
          isInSub(true);
        }
      }

      if (builder.allowUserCustom) {
        selectedCustomColor = getSelectedColor();
      }
      invalidateDynamicButtonColors();
      invalidate();
    }
  }

  @Override
  public boolean onLongClick(View v) {
    if (v.getTag() != null) {
      final String[] tag = ((String) v.getTag()).split(":");
      final int color = Integer.parseInt(tag[1]);
      ((CircleView) v).showHint(color);
      return true;
    }
    return false;
  }

  private void invalidateDynamicButtonColors() {
    final MaterialDialog dialog = (MaterialDialog) getDialog();
    if (dialog == null) {
      return;
    }
    final Builder builder = getBuilder();
    if (builder.dynamicButtonColor) {
      int selectedColor = getSelectedColor();
      if (Color.alpha(selectedColor) < 64
          || (Color.red(selectedColor) > 247
              && Color.green(selectedColor) > 247
              && Color.blue(selectedColor) > 247)) {
        // Once we get close to white or transparent,
        // the action buttons and seekbars will be a very light gray.
        selectedColor = Color.parseColor("#DEDEDE");
      }

      if (getBuilder().dynamicButtonColor) {
        dialog.getActionButton(DialogAction.POSITIVE).setTextColor(selectedColor);
        dialog.getActionButton(DialogAction.NEGATIVE).setTextColor(selectedColor);
        dialog.getActionButton(DialogAction.NEUTRAL).setTextColor(selectedColor);
      }

      if (customSeekR != null) {
        if (customSeekA.getVisibility() == View.VISIBLE) {
          MDTintHelper.setTint(customSeekA, selectedColor);
        }
        MDTintHelper.setTint(customSeekR, selectedColor);
        MDTintHelper.setTint(customSeekG, selectedColor);
        MDTintHelper.setTint(customSeekB, selectedColor);
      }
    }
  }

  @ColorInt
  private int getSelectedColor() {
    if (colorChooserCustomFrame != null
        && colorChooserCustomFrame.getVisibility() == View.VISIBLE) {
      return selectedCustomColor;
    }

    int color = 0;
    if (subIndex() > -1) {
      color = colorsSub[topIndex()][subIndex()];
    } else if (topIndex() > -1) {
      color = colorsTop[topIndex()];
    }
    if (color == 0) {
      int fallback = 0;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        fallback = DialogUtils.resolveColor(getActivity(), android.R.attr.colorAccent);
      }
      color = DialogUtils.resolveColor(getActivity(), R.attr.colorAccent, fallback);
    }
    return color;
  }

  private void findSubIndexForColor(int topIndex, int color) {
    if (colorsSub == null || colorsSub.length - 1 < topIndex) {
      return;
    }
    int[] subColors = colorsSub[topIndex];
    for (int subIndex = 0; subIndex < subColors.length; subIndex++) {
      if (subColors[subIndex] == color) {
        subIndex(subIndex);
        break;
      }
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    if (getArguments() == null || !getArguments().containsKey("builder")) {
      throw new IllegalStateException(
          "ColorChooserDialog should be created using its Builder interface.");
    }
    generateColors();

    int preselectColor;
    boolean foundPreselectColor = false;

    if (savedInstanceState != null) {
      foundPreselectColor = !savedInstanceState.getBoolean("in_custom", false);
      preselectColor = getSelectedColor();
    } else {
      if (getBuilder().setPreselectionColor) {
        preselectColor = getBuilder().preselectColor;
        if (preselectColor != 0) {
          for (int topIndex = 0; topIndex < colorsTop.length; topIndex++) {
            if (colorsTop[topIndex] == preselectColor) {
              foundPreselectColor = true;
              topIndex(topIndex);
              if (getBuilder().accentMode) {
                subIndex(2);
              } else if (colorsSub != null) {
                findSubIndexForColor(topIndex, preselectColor);
              } else {
                subIndex(5);
              }
              break;
            }

            if (colorsSub != null) {
              for (int subIndex = 0; subIndex < colorsSub[topIndex].length; subIndex++) {
                if (colorsSub[topIndex][subIndex] == preselectColor) {
                  foundPreselectColor = true;
                  topIndex(topIndex);
                  subIndex(subIndex);
                  break;
                }
              }
              if (foundPreselectColor) {
                break;
              }
            }
          }
        }
      } else {
        preselectColor = Color.BLACK;
        foundPreselectColor = true;
      }
    }

    circleSize = getResources().getDimensionPixelSize(R.dimen.md_colorchooser_circlesize);
    final Builder builder = getBuilder();

    MaterialDialog.Builder bd =
        new MaterialDialog.Builder(getActivity())
            .title(getTitle())
            .autoDismiss(false)
            .customView(R.layout.md_dialog_colorchooser, false)
            .negativeText(builder.cancelBtn)
            .positiveText(builder.doneBtn)
            .neutralText(builder.allowUserCustom ? builder.customBtn : 0)
            .typeface(builder.mediumFont, builder.regularFont)
            .onPositive(
                new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(MaterialDialog dialog, DialogAction which) {
                    callback.onColorSelection(ColorChooserDialog.this, getSelectedColor());
                    dismiss();
                  }
                })
            .onNegative(
                new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(MaterialDialog dialog, DialogAction which) {
                    if (isInSub()) {
                      dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().cancelBtn);
                      isInSub(false);
                      subIndex(-1); // Do this to avoid ArrayIndexOutOfBoundsException
                      invalidate();
                    } else {
                      dialog.cancel();
                    }
                  }
                })
            .onNeutral(
                new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(MaterialDialog dialog, DialogAction which) {
                    toggleCustom(dialog);
                  }
                })
            .showListener(
                new DialogInterface.OnShowListener() {
                  @Override
                  public void onShow(DialogInterface dialog) {
                    invalidateDynamicButtonColors();
                  }
                });

    if (builder.theme != null) {
      bd.theme(builder.theme);
    }

    final MaterialDialog dialog = bd.build();
    final View v = dialog.getCustomView();
    grid = v.findViewById(R.id.md_grid);

    if (builder.allowUserCustom) {
      selectedCustomColor = preselectColor;
      colorChooserCustomFrame = v.findViewById(R.id.md_colorChooserCustomFrame);
      customColorHex = v.findViewById(R.id.md_hexInput);
      customColorIndicator = v.findViewById(R.id.md_colorIndicator);
      customSeekA = v.findViewById(R.id.md_colorA);
      customSeekAValue = v.findViewById(R.id.md_colorAValue);
      customSeekR = v.findViewById(R.id.md_colorR);
      customSeekRValue = v.findViewById(R.id.md_colorRValue);
      customSeekG = v.findViewById(R.id.md_colorG);
      customSeekGValue = v.findViewById(R.id.md_colorGValue);
      customSeekB = v.findViewById(R.id.md_colorB);
      customSeekBValue = v.findViewById(R.id.md_colorBValue);

      if (!builder.allowUserCustomAlpha) {
        v.findViewById(R.id.md_colorALabel).setVisibility(View.GONE);
        customSeekA.setVisibility(View.GONE);
        customSeekAValue.setVisibility(View.GONE);
        customColorHex.setHint("2196F3");
        customColorHex.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
      } else {
        customColorHex.setHint("FF2196F3");
        customColorHex.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
      }

      if (!foundPreselectColor) {
        // If color wasn't found in the preset colors, it must be custom
        toggleCustom(dialog);
      }
    }

    invalidate();
    return dialog;
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (callback != null) {
      callback.onColorChooserDismissed(this);
    }
  }

  private void toggleCustom(MaterialDialog dialog) {
    if (dialog == null) {
      dialog = (MaterialDialog) getDialog();
    }
    if (grid.getVisibility() == View.VISIBLE) {
      dialog.setTitle(getBuilder().customBtn);
      dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().presetsBtn);
      dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().cancelBtn);
      grid.setVisibility(View.INVISIBLE);
      colorChooserCustomFrame.setVisibility(View.VISIBLE);

      customColorTextWatcher =
          new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              try {
                selectedCustomColor = Color.parseColor("#" + s.toString());
              } catch (IllegalArgumentException e) {
                selectedCustomColor = Color.BLACK;
              }
              customColorIndicator.setBackgroundColor(selectedCustomColor);
              if (customSeekA.getVisibility() == View.VISIBLE) {
                int alpha = Color.alpha(selectedCustomColor);
                customSeekA.setProgress(alpha);
                customSeekAValue.setText(String.format(Locale.US, "%d", alpha));
              }
              int red = Color.red(selectedCustomColor);
              customSeekR.setProgress(red);
              int green = Color.green(selectedCustomColor);
              customSeekG.setProgress(green);
              int blue = Color.blue(selectedCustomColor);
              customSeekB.setProgress(blue);
              isInSub(false);
              topIndex(-1);
              subIndex(-1);
              invalidateDynamicButtonColors();
            }

            @Override
            public void afterTextChanged(Editable s) {}
          };

      customColorHex.addTextChangedListener(customColorTextWatcher);
      customColorRgbListener =
          new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if (fromUser) {
                if (getBuilder().allowUserCustomAlpha) {
                  int color =
                      Color.argb(
                          customSeekA.getProgress(),
                          customSeekR.getProgress(),
                          customSeekG.getProgress(),
                          customSeekB.getProgress());
                  customColorHex.setText(String.format("%08X", color));
                } else {
                  int color =
                      Color.rgb(
                          customSeekR.getProgress(),
                          customSeekG.getProgress(),
                          customSeekB.getProgress());
                  customColorHex.setText(String.format("%06X", 0xFFFFFF & color));
                }
              }
              customSeekAValue.setText(String.format("%d", customSeekA.getProgress()));
              customSeekRValue.setText(String.format("%d", customSeekR.getProgress()));
              customSeekGValue.setText(String.format("%d", customSeekG.getProgress()));
              customSeekBValue.setText(String.format("%d", customSeekB.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
          };

      customSeekR.setOnSeekBarChangeListener(customColorRgbListener);
      customSeekG.setOnSeekBarChangeListener(customColorRgbListener);
      customSeekB.setOnSeekBarChangeListener(customColorRgbListener);
      if (customSeekA.getVisibility() == View.VISIBLE) {
        customSeekA.setOnSeekBarChangeListener(customColorRgbListener);
        customColorHex.setText(String.format("%08X", selectedCustomColor));
      } else {
        customColorHex.setText(String.format("%06X", 0xFFFFFF & selectedCustomColor));
      }
    } else {
      dialog.setTitle(getBuilder().title);
      dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().customBtn);
      if (isInSub()) {
        dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().backBtn);
      } else {
        dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().cancelBtn);
      }
      grid.setVisibility(View.VISIBLE);
      colorChooserCustomFrame.setVisibility(View.GONE);
      customColorHex.removeTextChangedListener(customColorTextWatcher);
      customColorTextWatcher = null;
      customSeekR.setOnSeekBarChangeListener(null);
      customSeekG.setOnSeekBarChangeListener(null);
      customSeekB.setOnSeekBarChangeListener(null);
      customColorRgbListener = null;
    }
  }

  private void invalidate() {
    if (grid.getAdapter() == null) {
      grid.setAdapter(new ColorGridAdapter());
      grid.setSelector(
          ResourcesCompat.getDrawable(getResources(), R.drawable.md_transparent, null));
    } else {
      ((BaseAdapter) grid.getAdapter()).notifyDataSetChanged();
    }
    if (getDialog() != null) {
      getDialog().setTitle(getTitle());
    }
  }

  private Builder getBuilder() {
    if (getArguments() == null || !getArguments().containsKey("builder")) {
      return null;
    }
    return (Builder) getArguments().getSerializable("builder");
  }

  private void dismissIfNecessary(FragmentManager fragmentManager, String tag) {
    Fragment frag = fragmentManager.findFragmentByTag(tag);
    if (frag != null) {
      ((DialogFragment) frag).dismiss();
      fragmentManager.beginTransaction().remove(frag).commit();
    }
  }

  public ColorChooserDialog show() {
    Tag tag;
    Builder builder = getBuilder();
    if (builder.colorsTop != null) {
      tag = Tag.CUSTOM;
    } else if (builder.accentMode) {
      tag = Tag.ACCENT;
    } else {
      tag = Tag.PRIMARY;
    }

    final FragmentManager fragmentManager =
        builder.fragment != null
            ? builder.fragment.getChildFragmentManager()
            : builder.activity.getSupportFragmentManager();
    if (builder.fragment != null) {
      setTargetFragment(builder.fragment, 67);
    }

    dismissIfNecessary(fragmentManager, tag.toString());
    show(fragmentManager, tag.toString());
    return this;
  }

  public enum Tag {
    PRIMARY("COLOR_CHOOSER_PRIMARY"),
    ACCENT("COLOR_CHOOSER_ACCENT"),
    CUSTOM("COLOR_CHOOSER_CUSTOM");

    private String value;

    Tag(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  public interface ColorCallback {

    void onColorSelection(ColorChooserDialog dialog, @ColorInt int selectedColor);

    void onColorChooserDismissed(ColorChooserDialog dialog);
  }

  @SuppressWarnings("SameParameterValue")
  public static class Builder implements Serializable {

    @Nullable final transient FragmentActivity activity;
    @Nullable final transient Fragment fragment;

    @Nullable String mediumFont;
    @Nullable String regularFont;
    @StringRes final int title;
    @StringRes int titleSub;
    @ColorInt int preselectColor;
    @StringRes int doneBtn = R.string.md_done_label;
    @StringRes int backBtn = R.string.md_back_label;
    @StringRes int cancelBtn = R.string.md_cancel_label;
    @StringRes int customBtn = R.string.md_custom_label;
    @StringRes int presetsBtn = R.string.md_presets_label;
    @Nullable int[] colorsTop;
    @Nullable int[][] colorsSub;
    @Nullable String tag;
    @Nullable Theme theme;

    boolean accentMode = false;
    boolean dynamicButtonColor = true;
    boolean allowUserCustom = true;
    boolean allowUserCustomAlpha = true;
    boolean setPreselectionColor = false;

    public <T extends FragmentActivity & ColorCallback> Builder(T activity, @StringRes int title) {
      this.activity = activity;
      this.fragment = null;
      this.title = title;
    }

    public <T extends Fragment & ColorCallback> Builder(T fragment, @StringRes int title) {
      this.activity = null;
      this.fragment = fragment;
      this.title = title;
    }

    public Builder typeface(@Nullable String medium, @Nullable String regular) {
      this.mediumFont = medium;
      this.regularFont = regular;
      return this;
    }

    public Builder titleSub(@StringRes int titleSub) {
      this.titleSub = titleSub;
      return this;
    }

    public Builder tag(@Nullable String tag) {
      this.tag = tag;
      return this;
    }

    public Builder theme(Theme theme) {
      this.theme = theme;
      return this;
    }

    public Builder preselect(@ColorInt int preselect) {
      preselectColor = preselect;
      setPreselectionColor = true;
      return this;
    }

    public Builder accentMode(boolean accentMode) {
      this.accentMode = accentMode;
      return this;
    }

    public Builder doneButton(@StringRes int text) {
      doneBtn = text;
      return this;
    }

    public Builder backButton(@StringRes int text) {
      backBtn = text;
      return this;
    }

    public Builder cancelButton(@StringRes int text) {
      cancelBtn = text;
      return this;
    }

    public Builder customButton(@StringRes int text) {
      customBtn = text;
      return this;
    }

    public Builder presetsButton(@StringRes int text) {
      presetsBtn = text;
      return this;
    }

    public Builder dynamicButtonColor(boolean enabled) {
      dynamicButtonColor = enabled;
      return this;
    }

    public Builder customColors(int[] topLevel, @Nullable int[][] subLevel) {
      colorsTop = topLevel;
      colorsSub = subLevel;
      return this;
    }

    public Builder customColors(@ArrayRes int topLevel, @Nullable int[][] subLevel) {
      final Context context = fragment != null ? fragment.getContext() : activity;
      colorsTop = DialogUtils.getColorArray(context, topLevel);
      colorsSub = subLevel;
      return this;
    }

    public Builder allowUserColorInput(boolean allow) {
      allowUserCustom = allow;
      return this;
    }

    public Builder allowUserColorInputAlpha(boolean allow) {
      allowUserCustomAlpha = allow;
      return this;
    }

    public ColorChooserDialog build() {
      ColorChooserDialog dialog = new ColorChooserDialog();
      Bundle args = new Bundle();
      args.putSerializable("builder", this);
      dialog.setArguments(args);
      return dialog;
    }

    public ColorChooserDialog show() {
      ColorChooserDialog dialog = build();
      dialog.show();
      return dialog;
    }
  }

  private class ColorGridAdapter extends BaseAdapter {
    ColorGridAdapter() {}

    @Override
    public int getCount() {
      if (isInSub()) {
        return colorsSub[topIndex()].length;
      } else {
        return colorsTop.length;
      }
    }

    @Override
    public Object getItem(int position) {
      if (isInSub()) {
        return colorsSub[topIndex()][position];
      } else {
        return colorsTop[position];
      }
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = new CircleView(getContext());
        convertView.setLayoutParams(new GridView.LayoutParams(circleSize, circleSize));
      }
      CircleView child = (CircleView) convertView;
      @ColorInt final int color = isInSub() ? colorsSub[topIndex()][position] : colorsTop[position];
      child.setBackgroundColor(color);
      if (isInSub()) {
        child.setSelected(subIndex() == position);
      } else {
        child.setSelected(topIndex() == position);
      }
      child.setTag(String.format("%d:%d", position, color));
      child.setOnClickListener(ColorChooserDialog.this);
      child.setOnLongClickListener(ColorChooserDialog.this);
      return convertView;
    }
  }
}
