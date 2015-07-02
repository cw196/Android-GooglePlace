package com.example.assignment4.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.assignment4.R;


public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String settings = getArguments().getString("settings_group");

        if ("all_settings".equals(settings)) {
            addPreferencesFromResource(R.xml.settings_all);
        }
    }
}
