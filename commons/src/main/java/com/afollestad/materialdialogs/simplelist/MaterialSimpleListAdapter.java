package com.afollestad.materialdialogs.simplelist;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.commons.R;
import com.afollestad.materialdialogs.internal.MDAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * See the sample project to understand how this is used. Mimics the Simple List dialog style
 * displayed on Google's guidelines site:
 * https://www.google.com/design/spec/components/dialogs.html#dialogs-simple-dialogs
 *
 * @author Aidan Follestad (afollestad)
 */
public class MaterialSimpleListAdapter
    extends RecyclerView.Adapter<MaterialSimpleListAdapter.SimpleListVH> implements MDAdapter {

  private MaterialDialog dialog;
  private List<MaterialSimpleListItem> items;
  private Callback callback;

  public MaterialSimpleListAdapter(Callback callback) {
    items = new ArrayList<>(4);
    this.callback = callback;
  }

  public void add(MaterialSimpleListItem item) {
    items.add(item);
    notifyItemInserted(items.size() - 1);
  }

  public void clear() {
    items.clear();
    notifyDataSetChanged();
  }

  public MaterialSimpleListItem getItem(int index) {
    return items.get(index);
  }

  @Override
  public void setDialog(MaterialDialog dialog) {
    this.dialog = dialog;
  }

  @Override
  public SimpleListVH onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.md_simplelist_item, parent, false);
    return new SimpleListVH(view, this);
  }

  @Override
  public void onBindViewHolder(SimpleListVH holder, int position) {
    if (dialog != null) {
      final MaterialSimpleListItem item = items.get(position);
      if (item.getIcon() != null) {
        holder.icon.setImageDrawable(item.getIcon());
        holder.icon.setPadding(
            item.getIconPadding(),
            item.getIconPadding(),
            item.getIconPadding(),
            item.getIconPadding());
        holder
            .icon
            .getBackground()
            .setColorFilter(item.getBackgroundColor(), PorterDuff.Mode.SRC_ATOP);
      } else {
        holder.icon.setVisibility(View.GONE);
      }
      holder.title.setTextColor(dialog.getBuilder().getItemColor());
      holder.title.setText(item.getContent());
      dialog.setTypeface(holder.title, dialog.getBuilder().getRegularFont());
    }
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  public interface Callback {

    void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item);
  }

  static class SimpleListVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    final ImageView icon;
    final TextView title;
    final MaterialSimpleListAdapter adapter;

    SimpleListVH(View itemView, MaterialSimpleListAdapter adapter) {
      super(itemView);
      icon = (ImageView) itemView.findViewById(android.R.id.icon);
      title = (TextView) itemView.findViewById(android.R.id.title);
      this.adapter = adapter;
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      if (adapter.callback != null) {
        adapter.callback.onMaterialListItemSelected(
            adapter.dialog, getAdapterPosition(), adapter.getItem(getAdapterPosition()));
      }
    }
  }
}
