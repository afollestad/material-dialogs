package com.afollestad.materialdialogssample;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class MaterialPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
