package org.tlc.whereat.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.tlc.whereat.R;
import org.tlc.whereat.fragments.SettingsFragment;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences);
        getFragmentManager().beginTransaction().replace(
              android.R.id.content,
              new SettingsFragment(),
              "settings_fragment"
          ).commit();
    }
}

