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
    private Long mLastPing;

    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLocSub = new MapLocationSubscriber(this);
        mLocDao = new LocationDao(this).connect();

        initialize(); // TODO make DB call async?
    }

    @Override
    protected void onResume(){
        super.onResume();
        mLocSub.register();
        refresh();
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
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ACCESSORS

    public void addLocation(Location l){
        mMap.addMarker(MapUtils.parseMarker(l));
        mLastPing = l.getTime();
    }

    // MAP MUTATORS

    private void initialize(){
        List<Location> ls = allLocations();
        mLastPing = getLastPing(ls);
        createMap(ls);
    }

    private void createMap(List<Location> ls) {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        mMap.setMyLocationEnabled(true);
        createMarkers(ls);
    }

    private void createMarkers(List<Location> ls){
        if(!ls.isEmpty()){
            center(MapUtils.parseLatLon(ls.get(ls.size() - 1)));
            addLocations(ls);
        }
        else center(LIBERTY);
    }

    private void refresh(){
        if (hasBeenViewed()) {
            List<Location> ls = mLocDao.getAllSince(mLastPing);
            mLastPing = getLastPing(ls);
            addLocations(ls);
        }
    }

    // HELPERS

    private void addLocations(List<Location> ls){
        if (!ls.isEmpty()) for (Location l : ls) mMap.addMarker(MapUtils.parseMarker(l));
    }

    private List<Location> allLocations(){
        return mLocDao.getAll();
    }

    private void center(LatLng ctr){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ctr, 15));
    }

    private long getLastPing(List<Location> ls){
        return ls.isEmpty() ? mLastPing : ls.get(ls.size() - 1).getTime();
    }

    private boolean hasBeenViewed(){
        return mLastPing != null;
    }

}
