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

import org.tlc.whereat.R;
import org.tlc.whereat.broadcast.location.MapLocationSubscriber;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.modules.MapUtils;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private static final LatLng LIBERTY = new LatLng(40.7092529,-74.0112551);

    private GoogleMap mMap;
    private LocationDao mLocDao;
    private MapLocationSubscriber mLocSub;

    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLocDao = new LocationDao(this).connect();

        setUpMap(allLocations()); // TODO DB call should be async
    }

    @Override
    protected void onResume(){
        super.onResume();
        mLocSub.register();
        //TODO implement updateMap(newLocations());
    }

    @Override
    protected void onPause(){
        super.onPause();
        mLocSub.unregister();
    }

    // EVENT HANDLERS

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
        }
        return super.onOptionsItemSelected(item);
    }

    // HELPERS

    private List<Location> allLocations(){
        return mLocDao.getAll();
    }

    private void setUpMap(List<Location> ls){
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        mMap.setMyLocationEnabled(true);

        if(!ls.isEmpty()){
            center(MapUtils.parseLatLon(ls.get(0)));
            addLocations(ls);
        }
        else center(LIBERTY);

        mLocSub = new MapLocationSubscriber(this, mMap);
    }

    private void addLocations(List<Location> ls){
        for (Location l : ls) mMap.addMarker(MapUtils.parseMarker(l));
    }

    private void center(LatLng ctr){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ctr, 15));
    }


}
