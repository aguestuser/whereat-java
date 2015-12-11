package org.tlc.whereat.modules.ui;


import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import org.tlc.whereat.R;
import org.tlc.whereat.activities.OnOffActivity;
import org.tlc.whereat.activities.MapActivity;
import org.tlc.whereat.activities.SettingsActivity;

import rx.functions.Func1;

public class MenuHandler {

    Activity mCtx;

    public MenuHandler(Activity ctx){
        mCtx = ctx;
    }

    public boolean create(Menu menu){
        mCtx.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean select(MenuItem item, Func1<MenuItem, Boolean> fn){
        switch(item.getItemId()){
            case R.id.action_on_off:
                mCtx.startActivity(new Intent(mCtx, OnOffActivity.class));
                break;
            case R.id.action_map:
                mCtx.startActivity(new Intent(mCtx, MapActivity.class));
                break;
            case R.id.action_prefs:
                mCtx.startActivity(new Intent(mCtx, SettingsActivity.class));
                break;
        }
        return fn.call(item);
    }
}
