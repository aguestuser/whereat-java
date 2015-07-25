package org.tlc.whereat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.tlc.whereat.R;
import org.tlc.whereat.pubsub.LocationSubscriberMap;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.util.MapUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MapActivity extends AppCompatActivity {

    private static final LatLng LIBERTY = new LatLng(40.7092529,-74.0112551);

    protected GoogleMap mMap;
    protected LocationDao mLocDao;
    protected LocationSubscriberMap mLocSub;
    protected ConcurrentHashMap<String, Marker> mMarkers;
    protected Long mLastPing;

    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLocSub = new LocationSubscriberMap(this);
        mLocDao = new LocationDao(this).connect();
        mMarkers = new ConcurrentHashMap<>();
        mLastPing = -1L;

        findViewById(R.id.clear_map_button)
            .setOnClickListener((View v) -> clear());

        initialize(); // TODO make DB calls async?

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMap.clear();
        mLocDao.disconnect();
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

    // PUBLIC MAP MUTATORS

    public void map(UserLocation l){
        plot(l);
        recordPing(l);
    }

    public void clear(){
        mMap.clear();
        mMarkers.clear();
        mLocDao.clear();
        mLastPing = null;
        //TODO tell the server to delete this particular location ? Via broadcast to LocationPublisher? (which knows the id?)
    }

    // PRIVATE MAP MUTATORS


    protected void initialize(){
        List<UserLocation> ls = allLocations();
        createMap(ls);
        recordPing(ls);
    }

    private void createMap(List<UserLocation> ls) {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        mMap.setMyLocationEnabled(true);
        createMarkers(ls);
    }

    private void createMarkers(List<UserLocation> ls){
        if(!ls.isEmpty()){
            mapMany(ls);
            centerZoom(last(ls));
        }
        else centerZoom(LIBERTY);
    }

    private void refresh(){
        if (hasBeenViewed()) {
            List<UserLocation> ls = mLocDao.getAllSince(mLastPing);
            mapMany(ls);
            recordPing(ls);
        }
    }


    // HELPERS

    private void mapMany(List<UserLocation> ls){
        if (!ls.isEmpty()) for (UserLocation l : ls) plot(l);
    }

    private void plot(UserLocation l){
        if (plotted(l)) rePlot(l);
        else addPlot(l);
    }

    private boolean plotted(UserLocation l){
        return mMarkers.containsKey(l.getId());
    }

    private void rePlot(UserLocation l){
        mMarkers.get(l.getId()).setPosition(MapUtils.parseLatLon(l));
    }

    private void addPlot(UserLocation l){
        mMarkers.put(l.getId(), mMap.addMarker(MapUtils.parseMarker(l)));
    }

    private List<UserLocation> allLocations(){
        return mLocDao.getAll();
    }

    private void centerZoom(UserLocation l){
        centerZoom(MapUtils.parseLatLon(l));
    }

    private void centerZoom(LatLng ctr){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ctr, 15));
    }

    private void center(UserLocation l){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MapUtils.parseLatLon(l)));
    }

    private void recordPing(List<UserLocation> ls){
        if (!ls.isEmpty()) recordPing(last(ls));
    }

    private void recordPing(UserLocation l){
        mLastPing =  l.getTime();
    }

    private boolean hasBeenViewed(){
        return mLastPing != null;
    }

    private UserLocation last(List<UserLocation> ls){
        return ls.get(ls.size() - 1);
    }

}
