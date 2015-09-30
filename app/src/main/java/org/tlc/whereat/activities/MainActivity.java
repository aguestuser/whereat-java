package org.tlc.whereat.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.tlc.whereat.R;
import org.tlc.whereat.fragments.SecurityAlertFragment;
import org.tlc.whereat.pubsub.LocPubManager;
import org.tlc.whereat.pubsub.LocSubMain;
import org.tlc.whereat.util.PopToast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    protected LocPubManager mLocPub;
    protected LocSubMain mLocSub;
    protected boolean mPolling;
    protected SecurityAlertFragment mSecAlert;

    protected boolean mSecAlerted;

    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocPub = new LocPubManager(this).start();
        mLocSub = new LocSubMain(this);
        mSecAlert = new SecurityAlertFragment();

        mPolling = false;
        mSecAlerted = false;

        final Button shareLocationButton = (Button) findViewById(R.id.go_button);

        shareLocationButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                return mPolling ? stop(v) : go(v);
            }
        });

        shareLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocPub.isPolling()) mLocPub.ping();
            }
        });
    }

    @Override
    protected void onResume(){

        super.onResume();
        if(!mSecAlerted) { showSecurityAlert(); }
        mLocPub.bind();
        mLocSub.register();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mLocPub.unbind();
        mLocSub.unregister();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mLocPub.stop();
    }

    // GO BUTTON HELPERS

    private boolean go(View v){
        v.setBackground(getDrawn(R.drawable.go_button_on));
        mLocPub.poll();
        mPolling = true;
        PopToast.briefly(this, "Location sharing on.");
        return true;
    }

    private boolean stop(View v){
        v.setBackground(getDrawn(R.drawable.go_button_off));
        mLocPub.stopPolling();
        mPolling = false;
        PopToast.briefly(this, "Location sharing off.");
        return true;
    }

    private Drawable getDrawn(int id){
        if (Build.VERSION.SDK_INT > 21) return super.getDrawable(id);
        else return getResources().getDrawable(id);
    }

    // MENU CALLBACKS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_map:
                startActivity(new Intent(this, MapActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // SECURITY ALERT ROUTINE

    protected void showSecurityAlert(){
        mSecAlert.show(getFragmentManager(),getString(R.string.security_alert_fragment_tag));
        mSecAlerted = true;
    }
}
