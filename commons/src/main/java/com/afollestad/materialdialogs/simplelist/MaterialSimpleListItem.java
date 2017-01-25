package com.afollestad.materialdialogs.simplelist;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.afollestad.materialdialogs.util.DialogUtils;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialSimpleListItem {

    private final Builder builder;

    private MaterialSimpleListItem(Builder builder) {
        this.builder = builder;
    }

    public Drawable getIcon() {
        return builder.icon;
    }

    public CharSequence getContent() {
        return builder.content;
    }

    public int getIconPadding() {
        return builder.iconPadding;
    }

    @ColorInt
    public int getBackgroundColor() {
        return builder.backgroundColor;
    }

    public long getId() {
        return builder.id;
    }

    @ColorInt
    public Integer getIconTint() { return builder.iconTint; }

    @Nullable
    public Object getTag() {
        return builder.tag;
    }

    public static class Builder {

        private final Context context;
        protected Drawable icon;
        protected CharSequence content;
        protected int iconPadding;
        protected Integer iconTint;
        protected int backgroundColor;
        protected long id;
        protected Object tag;

        public Builder(Context context) {
            this.context = context;
            backgroundColor = Color.parseColor("#BCBCBC");
        }

        public Builder icon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder icon(@DrawableRes int iconRes) {
            return icon(ContextCompat.getDrawable(context, iconRes));
        }

        public Builder iconPadding(@IntRange(from = 0, to = Integer.MAX_VALUE) int padding) {
            this.iconPadding = padding;
            return this;
        }

        public Builder iconPaddingDp(@IntRange(from = 0, to = Integer.MAX_VALUE) int paddingDp) {
            this.iconPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp,
                    context.getResources().getDisplayMetrics());
            return this;
        }

        public Builder iconPaddingRes(@DimenRes int paddingRes) {
            return iconPadding(context.getResources().getDimensionPixelSize(paddingRes));
        }

        @TargetApi(21)
        public Builder iconTintRes(@ColorRes int colorRes) {
            return iconTintColor(DialogUtils.getColor(context, colorRes));
        }

        @TargetApi(21)
        public Builder iconTintAttr(@AttrRes int colorAttr) {
            return iconTintColor(DialogUtils.resolveColor(context, colorAttr));
        }

        @TargetApi(21)
        public Builder iconTintColor(@ColorInt int color) {
            iconTint = color;
            return this;
        }

        public Builder content(CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            return content(context.getString(contentRes));
        }

        public Builder backgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder backgroundColorRes(@ColorRes int colorRes) {
            return backgroundColor(DialogUtils.getColor(context, colorRes));
        }

        public Builder backgroundColorAttr(@AttrRes int colorAttr) {
            return backgroundColor(DialogUtils.resolveColor(context, colorAttr));
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder tag(@Nullable Object tag) {
            this.tag = tag;
            return this;
        }

        public MaterialSimpleListItem build() {
            return new MaterialSimpleListItem(this);
        }
    }

    @Override
    public String toString() {
        if (getContent() != null)
            return getContent().toString();
        else return "(no content)";
    }
}
