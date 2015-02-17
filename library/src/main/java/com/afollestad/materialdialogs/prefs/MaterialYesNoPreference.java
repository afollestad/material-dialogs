package com.afollestad.materialdialogs.prefs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;

/**
 * @author Mark Sutherland (msutherland4807)
 */
public class MaterialYesNoPreference extends DialogPreference
{
	public MaterialYesNoPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void showDialog(Bundle state)
	{
		MaterialDialog dialog = new Builder(getContext())
				.title(getDialogTitle())
				.icon(getDialogIcon())
				.content(getDialogMessage())
				.positiveText(getPositiveButtonText())
				.negativeText(getNegativeButtonText())
				.callback(callback)
				.build();

		// Restore state if we can
		if (state != null)
			dialog.onRestoreInstanceState(state);

		dialog.setOnDismissListener(this);
		dialog.show();
	}

	/**
	 * Callback listener for the MaterialDialog. Both positive and negative buttons
	 * check with the change listener before persisting true/false respectively
	 */
	private final ButtonCallback callback = new ButtonCallback()
	{
		@Override
		public void onNegative(MaterialDialog dialog)
		{
			if (callChangeListener(false) && isPersistent())
				persistBoolean(false);
		}

		@Override
		public void onPositive(MaterialDialog dialog) {
			if (callChangeListener(true) && isPersistent())
				persistBoolean(true);
		}
	};
}
