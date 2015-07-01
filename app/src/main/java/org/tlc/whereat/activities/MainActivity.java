package org.tlc.whereat.activities;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.tlc.whereat.R;
import org.tlc.whereat.modules.LocationProvider;

public class MainActivity
        extends AppCompatActivity
        implements LocationProvider.LocationCallback {

    // FIELDS

    public static final String TAG = MainActivity.class.getSimpleName();
    private LocationProvider mLocationProvider;

    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationProvider = new LocationProvider(this, this);

        final Button shareLocationButton = (Button) findViewById(R.id.go_button);

        shareLocationButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                return mLocationProvider.isPolling() ? stop(v) : go(v);
            }
        });

        shareLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocationProvider.isPolling()) mLocationProvider.get();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        mLocationProvider.connect();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mLocationProvider.disconnect();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mLocationProvider.disconnect();
    }

    // GO BUTTON HELPERS

    private boolean go(View v){
        v.setBackground(getResources().getDrawable(R.drawable.go_button_on));
        mLocationProvider.poll();
        shortToast("Location sharing on.");
        return true;
    }

    private boolean stop(View v){
        v.setBackground(getResources().getDrawable(R.drawable.go_button_off));
        mLocationProvider.stopPolling();
        shortToast("Location sharing off.");
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

    // LOCATION PROVIDER CALLBACKS

    public void handleNewLocation(Location loc){
        Log.i(TAG, "Received location: " + loc.toString());
        toastLocation(loc);
    }

    public void handleFailedLocationRequest(String msg) {
        shortToast(msg);
    }

    public void handleNoPlayServices(){
        shortToast("This device does not support Google Play Services, which is required for location sharing.");
        finish();
    }

    // TOASTERS

    private void toastLocation(Location loc) {
        shortToast("Location shared: " + loc.toString());
    }

    private void shortToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
