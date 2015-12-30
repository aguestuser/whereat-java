package org.tlc.whereat.modules.map;

import android.app.Activity;
import android.util.Pair;

import org.tlc.whereat.model.UserLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.tlc.whereat.util.CollectionUtils.last;

public class Mapper {

    // FIELDS

    public static final String TAG = Mapper.class.getSimpleName();
    protected static final LatLon LIBERTY = new LatLon(40.7092529,-74.0112551);

    protected Activity mCtx;
    protected MapContainerFactory mMapFactory;
    protected MapContainer mMap;
    protected ConcurrentHashMap<String, Pair<Long, MarkerContainer>> mMarkers;
    protected Long mLastPing;
    protected boolean mRendered;

    // CONSTRUCTORS

    public Mapper(Activity ctx){
        mCtx = ctx;
        mMapFactory = new GoogleMapContainerFactory(ctx);
        mMarkers = new ConcurrentHashMap<>();
        mLastPing = -1L;
        mRendered = false;
    }

    // ACCESSORS

    public boolean hasRendered(){ return mRendered; }
    public long lastPing(){ return mLastPing; }

    // PUBLIC METHODS

    public Mapper render(List<UserLocation> uls){
        recordLastPing(uls);
        mMap = mMapFactory
            .getInstance()
            .getMap()
            .showUserLocation()
            .center(uls.isEmpty() ? LIBERTY : last(uls).asLatLon());
        if(!uls.isEmpty()) plotMany(uls);
        mRendered = true;
        return this;
    }

    public void refresh(List<UserLocation> uls){
        recordLastPing(uls);
        plotMany(uls);
    }

    public void record(UserLocation ul){
        recordPing(ul);
        plot(ul);
    }

    public void forgetSince(long expiration){
        for (Map.Entry<String, Pair<Long,MarkerContainer>> entry : mMarkers.entrySet()){
            Pair<Long, MarkerContainer> pair = entry.getValue();
            if(pair.first < expiration){
                pair.second.remove();
                mMarkers.remove(entry.getKey());
            }
        }
    }

    public void clear(){
        mMap.clear();
        mMarkers.clear();
        mLastPing = -1L;
    }

    // HELPERS

    protected void plotMany(List<UserLocation> uls){
        for (UserLocation ul : uls) plot(ul);
    }

    protected boolean plot(UserLocation ul){
        return mMarkers.containsKey(ul.getId()) ? rePlot(ul) : addPlot(ul);
    }

    protected boolean rePlot(UserLocation ul){
        String id = ul.getId();
        mMarkers.put(id, Pair.create(
                ul.getTime(),
                mMarkers.get(id).second.move(ul.asLatLon())));
        return true;
    }

    protected boolean addPlot(UserLocation ul){
        mMarkers.put(
            ul.getId(),
            Pair.create(ul.getTime(), mMap.addMarker(ul.asLatLon(), ul.asDateTime())));
        return true;
    }

    protected void recordLastPing(List<UserLocation> uls){
        if (!uls.isEmpty()) recordPing(last(uls));
    }

    protected void recordPing(UserLocation ul){
        mLastPing =  ul.getTime();
    }

}
