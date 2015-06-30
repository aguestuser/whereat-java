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

    public static final String TAG = MainActivity.class.getSimpleName();
    private LocationProvider mLocationProvider;
    private Boolean mPollingOwnLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationProvider = new LocationProvider(this, this);
        mLocationProvider.connect();
        mPollingOwnLocation = false;

        final Button shareLocationButton = (Button) findViewById(R.id.go_button);
        shareLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationProvider.getLocation();
            }
        });
        shareLocationButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                if (!mPollingOwnLocation){
                    v.setBackground(getResources().getDrawable(R.drawable.go_button_on));
                    mLocationProvider.pollLocation();
                    mPollingOwnLocation = true;

                }
                else {
                    v.setBackground(getResources().getDrawable(R.drawable.go_button_off));
                    mLocationProvider.stopPollingLocation();
                    mPollingOwnLocation = false;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mLocationProvider.disconnect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mLocationProvider.disconnect();
    }

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


    public void handleNewLocation(Location loc){
        Log.i(TAG, "Received own location: " + loc.toString());
        toastLocation(loc);
    }

    public void handleFailedLocationRequest(String msg) {
        Toast.makeText(
                getApplicationContext(), msg, Toast.LENGTH_SHORT)
                .show();
    }

    private void toastLocation(Location loc){
        Toast.makeText(
                getApplicationContext(), "Location shared: " + loc.toString(), Toast.LENGTH_SHORT)
                .show();
    }

    private void toastLocationShared(){
        Toast.makeText(getApplicationContext(), "Location shared.", Toast.LENGTH_SHORT).show();
    }

}
