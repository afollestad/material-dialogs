package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MDCheckBox extends CheckBox {

    public MDCheckBox(Context context) {
        super(context);
        init();
    }

    public MDCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MDCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MDCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        try {
            Field btnDrawable = CompoundButton.class.getDeclaredField("mButtonDrawable");
            btnDrawable.setAccessible(true);
            AnimatedStateListDrawable stateDrawable = (AnimatedStateListDrawable) btnDrawable.get(this);

            Field stateList = AnimatedStateListDrawable.class.getSuperclass().getDeclaredField("mStateListState");
            stateList.setAccessible(true);
            DrawableContainer.DrawableContainerState stateListState = (DrawableContainer.DrawableContainerState) stateList.get(stateDrawable);

            Drawable[] mDrawables = stateListState.getChildren();

            Field stateSets = stateListState.getClass().getSuperclass().getDeclaredField("mStateSets");
            stateSets.setAccessible(true);
            int[][] stateSetsValues = (int[][]) stateSets.get(stateListState);

            int index = 0;
            List<Drawable> checkDraws = new ArrayList<>();
            for (int[] state : stateSetsValues) {
                if (state == null || state.length == 0) continue;
                for (int stateSub : state) {
                    if (stateSub == android.R.attr.state_checked)
                        checkDraws.add(mDrawables[index]);
                }
                index++;
            }
            mCheckedDrawables = checkDraws.toArray(new Drawable[checkDraws.size()]);

            Log.v("TEMP", "TEMP");

        } catch (Throwable t) {
            Log.v("MDCompoundButtonColor", t.getLocalizedMessage());
        }
    }

    private int color;
    private Drawable[] mCheckedDrawables;

    public void setColorFilter(int color) {
        this.color = color;
        if (mCheckedDrawables != null) {
            for (Drawable d : mCheckedDrawables)
                d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mCheckedDrawables != null) {
            for (Drawable d : mCheckedDrawables)
                d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (mCheckedDrawables != null) {
            for (Drawable d : mCheckedDrawables)
                d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }
}