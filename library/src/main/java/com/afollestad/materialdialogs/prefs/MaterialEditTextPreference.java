package com.afollestad.materialdialogs.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.afollestad.materialdialogs.R;

/**
 * @author Marc Holder Kluver (marchold)
 */
public class MaterialEditTextPreference extends EditTextPreference {

	public MaterialEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedValue value = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
		color = value.data;
	}

	public MaterialEditTextPreference(Context context) {
		this(context, null);
	}

	private MaterialDialog dialog;
	private int color = 0;

	@Override
	protected void showDialog(Bundle state) {

		// Build our dialog for the first time
		if (dialog == null)
		{
			if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP)
				getEditText().getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

			dialog = new MaterialDialog.Builder(getContext())
					.title(getTitle())
					.icon(getDialogIcon())
					.positiveText(getPositiveButtonText())
					.negativeText(getNegativeButtonText())
					.customView(R.layout.md_input_dialog, false)
					.callback(callback)
					.content(getDialogMessage()).build();

			// So we maintain margins
			FrameLayout layout = (FrameLayout) dialog.getCustomView();
			if (layout != null)
			{
				layout.removeAllViews();
				layout.addView(getEditText());
			}
		}

		// Built it, now show it
		dialog.show();
	}

	private final ButtonCallback callback = new ButtonCallback()
	{
		@Override
		public void onPositive(MaterialDialog dialog)
		{
			String value = getEditText().getText().toString();
			if (callChangeListener(value) && isPersistent())
				setText(value);
		}
	};
}