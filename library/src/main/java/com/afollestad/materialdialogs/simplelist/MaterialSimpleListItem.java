package com.afollestad.materialdialogs.simplelist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialSimpleListItem {

    private Builder mBuilder;

    private MaterialSimpleListItem(Builder builder) {
        mBuilder = builder;
    }

    public Drawable getIcon() {
        return mBuilder.mIcon;
    }

    public CharSequence getContent() {
        return mBuilder.mContent;
    }

    public static class Builder {

        private Context mContext;
        protected Drawable mIcon;
        protected CharSequence mContent;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder icon(Drawable icon) {
            this.mIcon = icon;
            return this;
        }

        public Builder icon(@DrawableRes int iconRes) {
            return icon(ContextCompat.getDrawable(mContext, iconRes));
        }

        public Builder content(CharSequence content) {
            this.mContent = content;
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            return content(mContext.getString(contentRes));
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