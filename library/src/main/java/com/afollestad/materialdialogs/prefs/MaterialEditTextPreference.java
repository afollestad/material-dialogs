package com.afollestad.materialdialogs.prefs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.afollestad.materialdialogs.R;

/**
 * @author Marc Holder Kluver (marchold), Mark Sutherland (msutherland4807)
 */
<<<<<<< HEAD
public class MaterialEditTextPreference extends EditTextPreference
{
	/**
	 * Holds colorAccent from theme, initialized in constructor
	 */
	private int mColor = 0;

	/**
	 * Local EditText that we're going to display. Refers to same obj as parent class
	 */
	private EditText mEditText;

	@Override
	public EditText getEditText() { return mEditText; }

	/**
	 * Constructor
	 */
	public MaterialEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Find the accent color for the EditText background (underline)
		if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
			TypedValue value = new TypedValue();
			context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
			mColor = value.data;
		}

		mEditText = super.getEditText();
	}

	/**
	 * Constructor
	 */
	public MaterialEditTextPreference(Context context) {
		this(context, null);
	}

	/**
	 * Overridden from EditTextPreference
	 */
	@Override
	protected void showDialog(Bundle state) {
		Context context = getContext();

		// Color our EditText if need be. Lollipop does it by default
		if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP)
			mEditText.getBackground().setColorFilter(mColor, PorterDuff.Mode.SRC_ATOP);

		// Set up our builder
		Builder mBuilder = new MaterialDialog.Builder(getContext())
				.title(getDialogTitle())
				.icon(getDialogIcon())
				.positiveText(getPositiveButtonText())
				.negativeText(getNegativeButtonText())
				.callback(callback)
				.content(getDialogMessage());

		// Create our layout, put the EditText inside, then add to dialog
		FrameLayout layout = (FrameLayout)LayoutInflater.from(context).inflate(R.layout.md_input_dialog, null);
		onBindDialogView(layout);
		mBuilder.customView(layout, false);

		// Create the dialog
		MaterialDialog mDialog = mBuilder.build();
		if (state != null)
			mDialog.onRestoreInstanceState(state);

		// Show soft keyboard
		requestInputMethod(mDialog);

		mDialog.setOnDismissListener(this);
		mDialog.show();
	}

	/**
	 * Adds the EditText widget of this preference to the dialog's view.
	 *
	 * Overridden from EditTextPreference so we don't go searching for internal
	 * Android layouts
	 */
	@Override
	protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
		ViewGroup viewGroup = (ViewGroup) dialogView;
		viewGroup.removeAllViews();
		viewGroup.addView(editText, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	/**
	 * Callback listener for the MaterialDialog. Positive button checks with
	 * OnPreferenceChangeListener before committing user entered text
	 */
	private final ButtonCallback callback = new ButtonCallback()
	{
		@Override
		public void onPositive(MaterialDialog dialog) {
			String value = mEditText.getText().toString();
			if (callChangeListener(value) && isPersistent())
				setText(value);
		}
	};

	/**
	 * Copied from DialogPreference.java
	 */
	private void requestInputMethod(Dialog dialog) {
		Window window = dialog.getWindow();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
=======
public class MaterialEditTextPreference extends EditTextPreference {
    private int mColor = 0;
    private EditText mEditText;

    @Override
    public EditText getEditText() {
        return mEditText;
    }

    public MaterialEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Find the accent color for the EditText background (underline)
        if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
            mColor = value.data;
        }

        mEditText = super.getEditText();
    }

    public MaterialEditTextPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void showDialog(Bundle state) {
        Context context = getContext();

        // Color our EditText if need be. Lollipop does it by default
        if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP)
            mEditText.getBackground().setColorFilter(mColor, PorterDuff.Mode.SRC_ATOP);

        // Set up our builder
        Builder mBuilder = new MaterialDialog.Builder(getContext())
                .title(getDialogTitle())
                .icon(getDialogIcon())
                .positiveText(getPositiveButtonText())
                .negativeText(getNegativeButtonText())
                .callback(callback)
                .content(getDialogMessage());

        // Create our layout, put the EditText inside, then add to dialog
        FrameLayout layout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.md_input_dialog, null);
        onBindDialogView(layout);
        mBuilder.customView(layout, false);

        // Create the dialog
        MaterialDialog mDialog = mBuilder.build();
        if (state != null)
            mDialog.onRestoreInstanceState(state);

        // Show soft keyboard
        requestInputMethod(mDialog);

        mDialog.setOnDismissListener(this);
        mDialog.show();
    }

    /**
     * Adds the EditText widget of this preference to the dialog's view.
     * <p/>
     * Overridden from EditTextPreference so we don't go searching for internal
     * Android layouts
     */
    @Override
    protected void onAddEditTextToDialogView(@NonNull View dialogView, @NonNull EditText editText) {
        ViewGroup viewGroup = (ViewGroup) dialogView;
        viewGroup.removeAllViews();
        viewGroup.addView(editText, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Callback listener for the MaterialDialog. Positive button checks with
     * OnPreferenceChangeListener before committing user entered text
     */
    private final ButtonCallback callback = new ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            String value = mEditText.getText().toString();
            if (callChangeListener(value) && isPersistent())
                setText(value);
        }
    };

    /**
     * Copied from DialogPreference.java
     */
    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
>>>>>>> upstream/master
}