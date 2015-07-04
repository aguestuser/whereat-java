package org.tlc.whereat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.tlc.whereat.R;
import org.tlc.whereat.broadcast.location.MainLocationSubscriber;
import org.tlc.whereat.services.LocationServiceManager;
import org.tlc.whereat.modules.PopToast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private LocationServiceManager mLocPub;
    private MainLocationSubscriber mLocSub;

    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocPub = new LocationServiceManager(this).start();
        mLocSub = new MainLocationSubscriber(this);
        mLocSub.register();

        final Button shareLocationButton = (Button) findViewById(R.id.go_button);

        shareLocationButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                return mLocPub.isPolling() ? stop(v) : go(v);
            }
        });

        shareLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocPub.isPolling()) mLocPub.get(); //TODO fix state loss (if set to green then click to map, turns red)
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
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
        v.setBackground(getResources().getDrawable(R.drawable.go_button_on));
        mLocPub.poll();
        PopToast.briefly(this, "Location sharing on.");
        return true;
    }

    private boolean stop(View v){
        v.setBackground(getResources().getDrawable(R.drawable.go_button_off));
        mLocPub.stopPolling();
        PopToast.briefly(this, "Location sharing off.");
        return true;
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
}
