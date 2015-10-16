package org.tlc.whereat.modules.ui;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.tlc.whereat.R;
import org.tlc.whereat.activities.MainActivity;
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
            case R.id.action_main:
                Intent i = new Intent(mCtx, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // TODO: why did I add this?
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // TODO: or this?
                mCtx.startActivity(i);
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
