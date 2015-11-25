package org.tlc.whereat.activities;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.tlc.whereat.R;
import org.tlc.whereat.fragments.SecurityAlertFragment;
import org.tlc.whereat.modules.ui.MenuHandler;
import org.tlc.whereat.services.LocPubManager;
import org.tlc.whereat.modules.pubsub.receivers.MainActivityReceivers;

import static org.tlc.whereat.modules.ui.Toaster.shortToast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    protected LocPubManager mLocPubMgr;
    protected MainActivityReceivers mReceivers;
    protected boolean mPolling;
    protected SecurityAlertFragment mSecAlert;
    protected MenuHandler mMenu;

    protected boolean mSecAlerted;

    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocPubMgr = new LocPubManager(this).start();
        mReceivers = new MainActivityReceivers(this);
        mSecAlert = new SecurityAlertFragment();
        mMenu = new MenuHandler(this);

        mPolling = false;
        mSecAlerted = false;

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        findViewById(R.id.go_button).setOnClickListener(this::togglePolling);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!mSecAlerted) { showSecurityAlert(); }
        mLocPubMgr.bind();
        mReceivers.register();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mLocPubMgr.unbind();
        mReceivers.unregister();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mLocPubMgr.stop();
    }

    // GO BUTTON HELPERS

    protected void togglePolling(View v){
        if (mPolling) stopPolling(v);
        else poll(v);
    }

    protected void poll(View v){
        v.setBackground(getDrawn(R.drawable.go_button_on));
        mLocPubMgr.poll();
        mPolling = true;
        shortToast(this, "Location sharing on.");
    }

    protected void stopPolling(View v){
        v.setBackground(getDrawn(R.drawable.go_button_off));
        mLocPubMgr.stopPolling();
        mPolling = false;
        shortToast(this, "Location sharing off.");
    }

    private Drawable getDrawn(int id){
        if (Build.VERSION.SDK_INT > 21) return super.getDrawable(id);
        else return getResources().getDrawable(id);
    }

    // MENU CALLBACKS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenu.create(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenu.select(item, super::onOptionsItemSelected);
    }

    // SECURITY ALERT ROUTINE

    protected void showSecurityAlert(){
        mSecAlert.show(getFragmentManager(),getString(R.string.sec_alert_fragment_tag));
        mSecAlerted = true;
    }

}
