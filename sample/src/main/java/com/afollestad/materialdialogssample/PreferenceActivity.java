package com.afollestad.materialdialogssample;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.prefs.MaterialYesNoPreference;

public class PreferenceActivity extends ActionBarActivity {

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            findPreference(getString(R.string.yesno_pref_key)).setOnPreferenceChangeListener(prefChangeListener);
        }

        private final OnPreferenceChangeListener prefChangeListener = new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String stringValue = newValue.toString();

                // Check if we have the correct preference, and parse the new value into a boolean
                if (preference instanceof MaterialYesNoPreference) {
                    final boolean isPositive = Boolean.parseBoolean(stringValue);

                    // Toast to our result!
                    Toast.makeText(getActivity(), isPositive ? "Positive" : "Negative", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // Preference change not handled
                return false;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity_custom);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}