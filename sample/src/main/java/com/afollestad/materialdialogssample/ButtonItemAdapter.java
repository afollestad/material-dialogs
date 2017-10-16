package com.afollestad.materialdialogssample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/** Simple adapter example for custom items in the dialog */
class ButtonItemAdapter extends RecyclerView.Adapter<ButtonItemAdapter.ButtonVH> {

  private final CharSequence[] items;
  private ItemCallback itemCallback;
  private ButtonCallback buttonCallback;

  ButtonItemAdapter(Context context, @ArrayRes int arrayResId) {
    this(context.getResources().getTextArray(arrayResId));
  }

  private ButtonItemAdapter(CharSequence[] items) {
    this.items = items;
  }

  void setCallbacks(ItemCallback itemCallback, ButtonCallback buttonCallback) {
    this.itemCallback = itemCallback;
    this.buttonCallback = buttonCallback;
  }

  @Override
  public ButtonVH onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.dialog_customlistitem, parent, false);
    return new ButtonVH(view, this);
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onBindViewHolder(ButtonVH holder, int position) {
    holder.title.setText(items[position] + " (" + position + ")");
    holder.button.setTag(position);
  }

  @Override
  public int getItemCount() {
    return items.length;
  }

  interface ItemCallback {

    void onItemClicked(int itemIndex);
  }

  interface ButtonCallback {

    void onButtonClicked(int buttonIndex);
  }

  static class ButtonVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    final TextView title;
    final Button button;
    final ButtonItemAdapter adapter;

    ButtonVH(View itemView, ButtonItemAdapter adapter) {
      super(itemView);
      title = (TextView) itemView.findViewById(R.id.md_title);
      button = (Button) itemView.findViewById(R.id.md_button);

      this.adapter = adapter;
      itemView.setOnClickListener(this);
      button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      if (adapter.itemCallback == null) {
        return;
      }
      if (view instanceof Button) {
        adapter.buttonCallback.onButtonClicked(getAdapterPosition());
      } else {
        adapter.itemCallback.onItemClicked(getAdapterPosition());
      }
    }
  }
}
