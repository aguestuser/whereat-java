package org.tlc.whereat.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.tlc.whereat.R;
import org.tlc.whereat.db.LocationDao;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private static final LatLng LIBERTY = new LatLng(40.7092529,-74.0112551);
    private GoogleMap mMap;
    private LocationDao mLocationDao;

    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLocationDao = new LocationDao(this);
        mLocationDao.connect();

        setUpMap(allLocations()); // TODO DB call should be async
    }


    @Override
    protected void onResume(){
        super.onResume();
        //TODO implement updateMap(newLocations());
    }


    // UI EVENT HANDLERS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_main:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.action_map:
                startActivity(new Intent(this, MapActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    // HELPERS

    private List<Location> allLocations(){
        return mLocationDao.getAll();
    }

    private void setUpMap(List<Location> ls){
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        mMap.setMyLocationEnabled(true);
        addLocations(mMap,ls);
    }

    private void addLocations(GoogleMap m, List<Location> ls){
        if(!ls.isEmpty()){
            LatLng ctr = parseLatLon(ls.get(0));
            for (Location l : ls) mMap.addMarker(parseMarker(l));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ctr, 15));
        }
    }

    private LatLng parseLatLon(Location loc){
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    private MarkerOptions parseMarker(Location l){
        return new MarkerOptions().position(parseLatLon(l)).title(parseTime(l));
    }

    private String parseTime(Location loc){
        return "time when this was posted";
    }
}
