package com.afollestad.materialdialogssample;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Niklas Baudy (Vanniktech)
 *         https://github.com/vanniktech/
 */
public class HMSDialog extends DialogFragment implements View.OnClickListener {
    private static final String ARG_HOURS   = "hours";
    private static final String ARG_MINUTES = "minutes";
    private static final String ARG_SECONDS = "seconds";

    @NonNull
    private Callback            mCallback   = NullCallback.INSTANCE;
    private TextView            mTextView;

    private int[]               mValues     = new int[] { 0, 0, 0, 0, 0 };
    private boolean             mIsPositive = true;

    @Override
    public void onClick(final View v) {
        if (this.isViewDigitButton(v)) {
            this.addCharacter(Integer.parseInt(String.valueOf(v.getTag())));
        }
    }

    private boolean isViewDigitButton(@Nullable final View view) {
        return view != null && view instanceof Button && view.getTag() != null && view.getTag() instanceof String;
    }

    private void removeLastOne() {
        System.arraycopy(mValues, 1, mValues, 0, mValues.length - 1); // Shift one to the left
        this.updateTextView();
    }

    private void addCharacter(final int value) {
        System.arraycopy(mValues, 0, mValues, 1, mValues.length - 1); // Shift one to the right
        mValues[0] = value;

        this.updateTextView();
    }

    private void updateTextView() {
        final String text = String.valueOf(mIsPositive ? '+' : '-') + mValues[4] + 'h' + mValues[3] + mValues[2] + 'm' + mValues[1] + mValues[0] + 's';
        mTextView.setText(text);
    }

    public HMSDialog() {}

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).customView(R.layout.dialog_hms, false).positiveText(android.R.string.ok).negativeText(android.R.string.cancel).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(final MaterialDialog dialog) {
                final int hours = mValues[4];
                final int minutes = mValues[3] * 10 + mValues[2];
                final int seconds = mValues[1] * 10 + mValues[0];
                mCallback.onHMSSelection(mIsPositive, hours, minutes, seconds);
            }
        }).build();

        final GridLayout grid = (GridLayout) dialog.getCustomView().findViewById(R.id.grid);

        mTextView = (TextView) grid.findViewById(R.id.dialog_hms_text_view);

        grid.findViewById(R.id.dialog_hms_backspace_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                removeLastOne();
            }
        });

        grid.findViewById(R.id.dialog_hms_minus_plus_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mIsPositive = !mIsPositive;
                updateTextView();
            }
        });

        for (int i = 0; i < grid.getChildCount(); i++) {
            final View view = grid.getChildAt(i);

            if (this.isViewDigitButton(view)) {
                view.setOnClickListener(this);
            }
        }

        final Bundle arguments = this.getArguments();

        if (arguments != null) {
            final int hours = arguments.getInt(ARG_HOURS);
            mValues[4] = hours % 10;

            final int minutes = arguments.getInt(ARG_MINUTES);
            mValues[3] = minutes / 10;
            mValues[2] = minutes % 10;

            final int seconds = arguments.getInt(ARG_SECONDS);
            mValues[1] = seconds / 10;
            mValues[0] = seconds % 10;
        }

        this.updateTextView();

        return dialog;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        }
    }

    /**
     * will initialize hours minutes and seconds to 0
     *
     * @param context action bar activity
     */
    public void show(final ActionBarActivity context) {
        this.show(context, 0, 0, 0);
    }

    /**
     * @param context action bar activity
     * @param hours initial hours
     * @param minutes initial minutes
     * @param seconds initial seconds
     */
    public void show(final ActionBarActivity context, final int hours, final int minutes, final int seconds) {
        final Bundle args = new Bundle(3);
        args.putInt(ARG_HOURS, hours);
        args.putInt(ARG_MINUTES, minutes);
        args.putInt(ARG_SECONDS, seconds);
        this.setArguments(args);

        this.show(context.getSupportFragmentManager(), "HSM_SELECTOR");
    }

    public interface Callback {
        /**
         * @param isPositive whether it is positive or negative
         * @param hours the selected hours
         * @param minutes the selected minutes
         * @param seconds the selected seconds
         */
        void onHMSSelection(final boolean isPositive, final int hours, final int minutes, final int seconds);
    }

    private static class NullCallback implements Callback {
        private static final NullCallback INSTANCE = new NullCallback();

        private NullCallback() {}

        @Override
        public void onHMSSelection(final boolean isPositive, final int hours, final int minutes, final int seconds) {}
    }
}
