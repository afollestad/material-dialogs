package com.afollestad.materialdialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialTextView extends TextView {

    public static final int THIN = 0;
    public static final int LIGHT = 1;
    public static final int REGULAR = 2;
    public static final int MEDIUM = 3;
    public static final int BLACK = 4;
    public static final int CONDENSED = 5;
    public static final int CONDENSED_LIGHT = 6;


    public MaterialTextView(Context context) {
        super(context);
        init(null);
    }

    public MaterialTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MaterialTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        if (attrs == null) {
            setTypeface(Typeface.createFromAsset(getContext().getResources().getAssets(), "Roboto-Regular.ttf"));
            return;
        }

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.MaterialTextView, 0, 0);
        try {
            final int font = a.getInteger(R.styleable.MaterialTextView_font, REGULAR);
            final boolean bold = a.getBoolean(R.styleable.MaterialTextView_bold, false);
            setFont(font, bold);
        } finally {
            a.recycle();
        }
    }

    public void setFont(int font, boolean bold) {
        String ttf;
        switch (font) {
            default: // THIN
                ttf = "Roboto-Thin.ttf";
                break;
            case LIGHT:
                ttf = "Roboto-Light.ttf";
                break;
            case REGULAR:
                if (bold) ttf = "Roboto-Bold.ttf";
                else ttf = "Roboto-Regular.ttf";
                break;
            case MEDIUM:
                ttf = "Roboto-Medium.ttf";
                break;
            case BLACK:
                ttf = "Roboto-Black.ttf";
                break;
            case CONDENSED:
                if (bold) ttf = "RobotoCondensed-Bold.ttf";
                else ttf = "RobotoCondensed-Regular.ttf";
                break;
            case CONDENSED_LIGHT:
                ttf = "RobotoCondensed-Light.ttf";
                break;
        }
        setTypeface(Typeface.createFromAsset(getContext().getResources().getAssets(), ttf));
    }
}
