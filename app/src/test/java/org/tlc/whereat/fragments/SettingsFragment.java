package org.tlc.whereat.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.tlc.whereat.R;

public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle state){
        super.onCreate(state);
        addPreferencesFromResource(R.xml.preferences);
    }
}
