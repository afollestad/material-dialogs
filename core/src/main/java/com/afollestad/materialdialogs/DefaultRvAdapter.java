package com.afollestad.materialdialogs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;

/** @author Aidan Follestad (afollestad) */
class DefaultRvAdapter extends RecyclerView.Adapter<DefaultRvAdapter.DefaultVH> {

  private final MaterialDialog dialog;
  @LayoutRes private final int layout;
  private final GravityEnum itemGravity;
  private InternalListCallback callback;

  DefaultRvAdapter(MaterialDialog dialog, @LayoutRes int layout) {
    this.dialog = dialog;
    this.layout = layout;
    this.itemGravity = dialog.builder.itemsGravity;
  }

  void setCallback(InternalListCallback callback) {
    this.callback = callback;
  }

  @Override
  public DefaultVH onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    DialogUtils.setBackgroundCompat(view, dialog.getListSelector());
    return new DefaultVH(view, this);
  }

  @Override
  public void onBindViewHolder(DefaultVH holder, int position) {
    final View view = holder.itemView;
    boolean disabled = DialogUtils.isIn(position, dialog.builder.disabledIndices);
    int itemTextColor =
        disabled
            ? DialogUtils.adjustAlpha(dialog.getBuilder().getItemColor(position), 0.4f)
            : dialog.getBuilder().getItemColor(position);
    holder.itemView.setEnabled(!disabled);

    switch (dialog.listType) {
      case SINGLE:
        {
          @SuppressLint("CutPasteId")
          RadioButton radio = (RadioButton) holder.control;
          boolean selected = dialog.builder.selectedIndex == position;
          if (dialog.builder.choiceWidgetColor != null) {
            MDTintHelper.setTint(radio, dialog.builder.choiceWidgetColor);
          } else {
            MDTintHelper.setTint(radio, dialog.builder.widgetColor);
          }
          radio.setChecked(selected);
          radio.setEnabled(!disabled);
          break;
        }
      case MULTI:
        {
          @SuppressLint("CutPasteId")
          CheckBox checkbox = (CheckBox) holder.control;
          boolean selected = dialog.selectedIndicesList.contains(position);
          if (dialog.builder.choiceWidgetColor != null) {
            MDTintHelper.setTint(checkbox, dialog.builder.choiceWidgetColor);
          } else {
            MDTintHelper.setTint(checkbox, dialog.builder.widgetColor);
          }
          checkbox.setChecked(selected);
          checkbox.setEnabled(!disabled);
          break;
        }
    }

    holder.title.setText(dialog.builder.items.get(position));
    holder.title.setTextColor(itemTextColor);
    dialog.setTypeface(holder.title, dialog.builder.regularFont);

    setupGravity((ViewGroup) view);

    if (dialog.builder.itemIds != null) {
      if (position < dialog.builder.itemIds.length) {
        view.setId(dialog.builder.itemIds[position]);
      } else {
        view.setId(-1);
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      ViewGroup group = (ViewGroup) view;
      if (group.getChildCount() == 2) {
        // Remove circular selector from check boxes and radio buttons on Lollipop
        if (group.getChildAt(0) instanceof CompoundButton) {
          group.getChildAt(0).setBackground(null);
        } else if (group.getChildAt(1) instanceof CompoundButton) {
          group.getChildAt(1).setBackground(null);
        }
      }
    }
  }

  @Override
  public int getItemCount() {
    return dialog.builder.items != null ? dialog.builder.items.size() : 0;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private void setupGravity(ViewGroup view) {
    final LinearLayout itemRoot = (LinearLayout) view;
    final int gravityInt = itemGravity.getGravityInt();
    itemRoot.setGravity(gravityInt | Gravity.CENTER_VERTICAL);

    if (view.getChildCount() == 2) {
      if (itemGravity == GravityEnum.END
          && !isRTL()
          && view.getChildAt(0) instanceof CompoundButton) {
        CompoundButton first = (CompoundButton) view.getChildAt(0);
        view.removeView(first);

        TextView second = (TextView) view.getChildAt(0);
        view.removeView(second);
        second.setPadding(
            second.getPaddingRight(),
            second.getPaddingTop(),
            second.getPaddingLeft(),
            second.getPaddingBottom());

        view.addView(second);
        view.addView(first);
      } else if (itemGravity == GravityEnum.START
          && isRTL()
          && view.getChildAt(1) instanceof CompoundButton) {
        CompoundButton first = (CompoundButton) view.getChildAt(1);
        view.removeView(first);

        TextView second = (TextView) view.getChildAt(0);
        view.removeView(second);
        second.setPadding(
            second.getPaddingRight(),
            second.getPaddingTop(),
            second.getPaddingRight(),
            second.getPaddingBottom());

        view.addView(first);
        view.addView(second);
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private boolean isRTL() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      return false;
    }
    Configuration config = dialog.getBuilder().getContext().getResources().getConfiguration();
    return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
  }

  interface InternalListCallback {

    boolean onItemSelected(
        MaterialDialog dialog, View itemView, int position, CharSequence text, boolean longPress);
  }

  static class DefaultVH extends RecyclerView.ViewHolder
      implements View.OnClickListener, View.OnLongClickListener {

    final CompoundButton control;
    final TextView title;
    final DefaultRvAdapter adapter;

    DefaultVH(View itemView, DefaultRvAdapter adapter) {
      super(itemView);
      control = (CompoundButton) itemView.findViewById(R.id.md_control);
      title = (TextView) itemView.findViewById(R.id.md_title);
      this.adapter = adapter;
      itemView.setOnClickListener(this);
      if (adapter.dialog.builder.listLongCallback != null) {
        itemView.setOnLongClickListener(this);
      }
    }

    @Override
    public void onClick(View view) {
      if (adapter.callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
        CharSequence text = null;
        if (adapter.dialog.builder.items != null
            && getAdapterPosition() < adapter.dialog.builder.items.size()) {
          text = adapter.dialog.builder.items.get(getAdapterPosition());
        }
        adapter.callback.onItemSelected(adapter.dialog, view, getAdapterPosition(), text, false);
      }
    }

    @Override
    public boolean onLongClick(View view) {
      if (adapter.callback != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
        CharSequence text = null;
        if (adapter.dialog.builder.items != null
            && getAdapterPosition() < adapter.dialog.builder.items.size()) {
          text = adapter.dialog.builder.items.get(getAdapterPosition());
        }
        return adapter.callback.onItemSelected(
            adapter.dialog, view, getAdapterPosition(), text, true);
      }
      return false;
    }
  }
}
