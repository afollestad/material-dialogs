package com.afollestad.materialdialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialDialog extends AlertDialog implements View.OnClickListener {

    private final static String POSITIVE = "POSITIVE";
    private final static String NEGATIVE = "NEGATIVE";

    MaterialDialog(Builder builder) {
        super(new ContextThemeWrapper(builder.context, builder.theme == Theme.LIGHT ? R.style.Light : R.style.Dark));
        View view = LayoutInflater.from(builder.context).inflate(R.layout.material_dialog, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(builder.title);
        if (builder.theme == Theme.LIGHT) {
            title.setTextColor(LightColors.TITLE.get());
        } else {
            title.setTextColor(DarkColors.TITLE.get());
        }

        TextView body = (TextView) view.findViewById(R.id.content);
        body.setText(builder.content);
        body.setMovementMethod(new LinkMovementMethod());
        body.setVisibility(View.VISIBLE);
        if (builder.theme == Theme.LIGHT) {
            body.setTextColor(LightColors.CONTENT.get());
        } else {
            body.setTextColor(DarkColors.CONTENT.get());
        }

        this.callback = builder.callback;
        TextView positiveButton = (TextView) view.findViewById(R.id.buttonDefaultPositive);
        positiveButton.setTextColor(builder.positiveColor);
        positiveButton.setTag(POSITIVE);
        positiveButton.setOnClickListener(this);
        if (this.callback != null) {

        }

        TextView negativeButton = (TextView) view.findViewById(R.id.buttonDefaultNegative);
        negativeButton.setTag(NEGATIVE);
        negativeButton.setOnClickListener(this);
        if (builder.theme == Theme.LIGHT) {
            negativeButton.setTextColor(LightColors.BUTTON.get());
        } else {
            negativeButton.setTextColor(DarkColors.BUTTON.get());
        }

        setView(view);
    }

    @Override
    public void onClick(View v) {
        String tag = (String)v.getTag();
    }

    static enum LightColors {
        TITLE("#3C3C3D"), CONTENT("#535353"), ITEM("#535353"), BUTTON("#3C3C3D");

        final String mColor;

        LightColors(String color) {
            this.mColor = color;
        }

        public int get() {
            return Color.parseColor(mColor);
        }
    }

    static enum DarkColors {
        TITLE("#FFFFFF"), CONTENT("#EDEDED"), ITEM("#EDEDED"), BUTTON("#FFFFFF");

        final String mColor;

        DarkColors(String color) {
            this.mColor = color;
        }

        public int get() {
            return Color.parseColor(mColor);
        }
    }

    private SimpleCallback callback;
    private ListCallback listCallback;

    public static interface ListCallback {
        void onSelection(int which, String text);
    }

    public static interface SimpleCallback {
        void onPositive();
    }

    public static interface Callback extends SimpleCallback {
        void onPositive();

        void onNegative();
    }

    public static interface FullCallback extends Callback {
        void onNeutral();
    }

    public static class Builder {

        protected Activity context;
        protected String title;
        protected String content;
        protected String[] items;
        protected String positiveText;
        protected String neutralText;
        protected String negativeText;
        protected View customView;
        protected int positiveColor;
        protected SimpleCallback callback;
        protected ListCallback listCallback;
        protected Theme theme = Theme.LIGHT;

        public Builder(@NonNull Activity context) {
            this.context = context;
            this.positiveColor = context.getResources().getColor(R.color.material_blue_500);
        }

        public Builder title(@StringRes int titleRes) {
            title(this.context.getString(titleRes));
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            content(this.context.getString(contentRes));
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder items(@ArrayRes int itemsRes) {
            items(this.context.getResources().getStringArray(itemsRes));
            return this;
        }

        public Builder items(String[] items) {
            this.items = items;
            return this;
        }

        public Builder itemsCallback(ListCallback callback) {
            this.listCallback = callback;
            return this;
        }

        public Builder positiveText(@StringRes int postiveRes) {
            positiveText(this.context.getString(postiveRes));
            return this;
        }

        public Builder positiveText(String message) {
            this.positiveText = message;
            return this;
        }

        public Builder neutralText(@StringRes int neutralRes) {
            neutralText(this.context.getString(neutralRes));
            return this;
        }

        public Builder neutralText(String message) {
            this.neutralText = message;
            return this;
        }

        public Builder negativeText(@StringRes int negativeRes) {
            negativeText(this.context.getString(negativeRes));
            return this;
        }

        public Builder negativeText(String message) {
            this.negativeText = message;
            return this;
        }

        public Builder customView(@LayoutRes int layoutRes) {
            LayoutInflater li = LayoutInflater.from(this.context);
            customView(li.inflate(layoutRes, null));
            return this;
        }

        public Builder customView(View view) {
            this.customView = view;
            return this;
        }

        public Builder positiveColorRes(@ColorRes int colorRes) {
            positiveColor(this.context.getResources().getColor(colorRes));
            return this;
        }

        public Builder positiveColor(int color) {
            this.positiveColor = color;
            return this;
        }

        public Builder callback(SimpleCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder theme(Theme theme) {
            this.theme = theme;
            return this;
        }

        public MaterialDialog build() {
            return new MaterialDialog(this);
        }
    }
}
