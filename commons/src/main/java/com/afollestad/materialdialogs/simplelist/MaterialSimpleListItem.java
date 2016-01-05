package com.afollestad.materialdialogs.simplelist;

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

    private final Builder mBuilder;

    private MaterialSimpleListItem(Builder builder) {
        mBuilder = builder;
    }

    public Drawable getIcon() {
        return mBuilder.mIcon;
    }

    public CharSequence getContent() {
        return mBuilder.mContent;
    }

    public int getIconPadding() {
        return mBuilder.mIconPadding;
    }

    @ColorInt
    public int getBackgroundColor() {
        return mBuilder.mBackgroundColor;
    }

    public long getId() {
        return mBuilder.mId;
    }

    @Nullable
    public Object getTag() {
        return mBuilder.mTag;
    }

    public static class Builder {

        private final Context mContext;
        protected Drawable mIcon;
        protected CharSequence mContent;
        protected int mIconPadding;
        protected int mBackgroundColor;
        protected long mId;
        protected Object mTag;

        public Builder(Context context) {
            mContext = context;
            mBackgroundColor = Color.parseColor("#BCBCBC");
        }

        public Builder icon(Drawable icon) {
            this.mIcon = icon;
            return this;
        }

        public Builder icon(@DrawableRes int iconRes) {
            return icon(ContextCompat.getDrawable(mContext, iconRes));
        }

        public Builder iconPadding(@IntRange(from = 0, to = Integer.MAX_VALUE) int padding) {
            this.mIconPadding = padding;
            return this;
        }

        public Builder iconPaddingDp(@IntRange(from = 0, to = Integer.MAX_VALUE) int paddingDp) {
            this.mIconPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp,
                    mContext.getResources().getDisplayMetrics());
            return this;
        }

        public Builder iconPaddingRes(@DimenRes int paddingRes) {
            return iconPadding(mContext.getResources().getDimensionPixelSize(paddingRes));
        }

        public Builder content(CharSequence content) {
            this.mContent = content;
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            return content(mContext.getString(contentRes));
        }

        public Builder backgroundColor(@ColorInt int color) {
            this.mBackgroundColor = color;
            return this;
        }

        public Builder backgroundColorRes(@ColorRes int colorRes) {
            return backgroundColor(DialogUtils.getColor(mContext, colorRes));
        }

        public Builder backgroundColorAttr(@AttrRes int colorAttr) {
            return backgroundColor(DialogUtils.resolveColor(mContext, colorAttr));
        }

        public Builder id(long id) {
            mId = id;
            return this;
        }

        public Builder tag(@Nullable Object tag) {
            mTag = tag;
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