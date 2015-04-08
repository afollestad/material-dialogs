package com.afollestad.materialdialogs.internal;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.internal.widget.TintCheckBox;
import android.support.v7.internal.widget.TintEditText;
import android.support.v7.internal.widget.TintRadioButton;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.afollestad.materialdialogs.R;
import com.afollestad.materialdialogs.util.DialogUtils;

/**
 * Tints widgets
 */
public class MDTintHelper {

    public static void setRadioButtonTint(RadioButton radioButton, int color) {
        ColorStateList sl = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        }, new int[]{
                DialogUtils.resolveColor(radioButton.getContext(), android.R.attr.textColorSecondary),
                color
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radioButton.setButtonTintList(sl);
        } else if (radioButton instanceof TintRadioButton) {
            Drawable drawable = ContextCompat.getDrawable(radioButton.getContext(), R.drawable.abc_btn_radio_material);
            DrawableWrapper d = new DrawableWrapper(drawable);
            d.setTintList(sl);
            radioButton.setButtonDrawable(d);
        }
    }

    public static void setProgressBarTint(ProgressBar progressBar, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList stateList = ColorStateList.valueOf(color);
            progressBar.setProgressTintList(stateList);
            progressBar.setSecondaryProgressTintList(stateList);
            progressBar.setIndeterminateTintList(stateList);
        } else {
            if (progressBar.getIndeterminateDrawable() != null)
                progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            if (progressBar.getProgressDrawable() != null)
                progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public static void setEditTextTint(EditText editText, int color) {
        ColorStateList s1 = ColorStateList.valueOf(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setBackgroundTintList(s1);
        } else if (editText instanceof TintEditText) {
            Drawable drawable = ContextCompat.getDrawable(editText.getContext(), R.drawable.abc_edit_text_material);
            DrawableWrapper d = new DrawableWrapper(drawable);
            d.setTintList(s1);
            editText.setBackgroundDrawable(d);
        }
    }

    public static void setCheckBoxTint(CheckBox box, int color) {
        ColorStateList sl = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        }, new int[]{
                DialogUtils.resolveColor(box.getContext(), android.R.attr.textColorSecondary),
                color
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            box.setButtonTintList(sl);
        } else if (box instanceof TintCheckBox) {
            Drawable drawable = ContextCompat.getDrawable(box.getContext(), R.drawable.abc_btn_check_material);
            DrawableWrapper d = new DrawableWrapper(drawable);
            d.setTintList(sl);
            box.setButtonDrawable(d);
        }
    }
}
