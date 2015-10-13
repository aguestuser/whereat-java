package org.tlc.whereat.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.tlc.whereat.R;
import org.tlc.whereat.fragments.SettingsFragment;
import org.tlc.whereat.modules.ui.MenuHandler;

public class SettingsActivity extends AppCompatActivity {//extends PreferenceActivity {

    MenuHandler mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMenu = new MenuHandler(this);

        getFragmentManager().beginTransaction().replace(
              android.R.id.content,
              new SettingsFragment(),
              "settings_fragment"
          ).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenu.create(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenu.select(item, super::onOptionsItemSelected);
    }
}

