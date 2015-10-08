package org.tlc.whereat.modules;

import android.app.Activity;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.tlc.whereat.R;
import org.tlc.whereat.model.UserLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.tlc.whereat.util.CollectionUtils.last;

public class Mapper {

    // FIELDS

    public static final String TAG = Mapper.class.getSimpleName();
    protected static final LatLng LIBERTY = new LatLng(40.7092529,-74.0112551);

    protected Activity mCtx;
    protected GoogleMap mMap;
    protected ConcurrentHashMap<String, Pair<Long, Marker>> mMarkers;
    protected Long mLastPing;
    protected boolean mInitialized;

    // CONSTRUCTORS

    public static Mapper getInstance(Activity ctx, List<UserLocation> uls){
        return new Mapper(ctx).initialize(uls);
    }

    public Mapper(Activity ctx){
        mCtx = ctx;
        mMarkers = new ConcurrentHashMap<>();
        mLastPing = -1L;
        mInitialized = false;
    }

    public Mapper initialize(List<UserLocation> uls){
        initMap(uls);
        recordLastPing(uls);
        mInitialized = true;
        return this;
    }

    // GETTERS

    public boolean hasInitialized(){ return mInitialized; }
    public long lastPing(){ return mLastPing; }
    public boolean hasPinged() { return mLastPing > -1L; }

    // PUBLIC METHODS

    public void refresh(List<UserLocation> uls){
        plotMany(uls);
        recordLastPing(uls);
    }

    public void map(UserLocation ul){
        plot(ul);
        recordPing(ul);
    }

    public void clear(){
        mMap.clear();
        mMarkers.clear();
        mLastPing = -1L;
    }

    public void forgetSince(long expiration){
        for (Map.Entry<String, Pair<Long,Marker>> entry : mMarkers.entrySet()){
            Pair<Long, Marker> pair = entry.getValue();
            if(pair.first < expiration){
                pair.second.remove();
                mMarkers.remove(entry.getKey());
            }
        }
    }


    // CONSTRUCTION HELPERS

    protected void initMap(List<UserLocation> ls) {
        mMap = getMap();
        mMap.setMyLocationEnabled(true);
        if(!ls.isEmpty()){
            initMarkers(ls);
            initCenter(last(ls));
        }
        else initCenter(LIBERTY);
    }

    protected GoogleMap getMap(){ // helper function for testing seem. see: https://github.com/robolectric/robolectric/issues/1145
        return ((MapFragment) mCtx.getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
    }

    void initMarkers(List<UserLocation> ls){
        if(!ls.isEmpty()) plotMany(ls);
    }

    protected void initCenter(UserLocation l){
        initCenter(l.asLatLng());
    }

    protected void initCenter(LatLng ctr){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ctr, 15));
    }

    // HELPERS

    protected void plotMany(List<UserLocation> ls){
        if (!ls.isEmpty()) for (UserLocation l : ls) plot(l);
    }

    protected boolean plot(UserLocation l){
        return mMarkers.containsKey(l.getId()) ? rePlot(l) : addPlot(l);
    }

    protected boolean rePlot(UserLocation l){
        String id = l.getId();

        mMarkers.get(id).second.setPosition(l.asLatLng());
        mMarkers.put(id, Pair.create(l.getTime(), mMarkers.get(id).second));
        return true;
    }

    protected boolean addPlot(UserLocation l){
        mMarkers.put(l.getId(), Pair.create(l.getTime(), addMarker(l)));
        return true;
    }

    protected Marker addMarker(UserLocation l){ // extracted as testing workaround. grr..
        return mMap.addMarker(l.asMarkerOptions());
    }

    protected void recordLastPing(List<UserLocation> ls){
        if (!ls.isEmpty()) recordPing(last(ls));
    }

    protected void recordPing(UserLocation l){
        mLastPing =  l.getTime();
    }


}
