package org.tlc.whereat.support;

import android.view.Menu;
import android.view.MenuItem;

import org.robolectric.fakes.RoboMenuItem;
import org.tlc.whereat.R;

public class ActivityWithMenuHandlersTest {

    //FIELDS

    protected Menu menu;
    protected MenuItem main = new RoboMenuItem(R.id.action_main);
    protected MenuItem map = new RoboMenuItem(R.id.action_map);
    protected MenuItem prefs = new RoboMenuItem(R.id.action_prefs);

}
