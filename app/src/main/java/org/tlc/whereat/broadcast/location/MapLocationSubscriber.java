package org.tlc.whereat.broadcast.location;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.GoogleMap;

import org.tlc.whereat.activities.MapActivity;
import org.tlc.whereat.services.LocationService;
import org.tlc.whereat.broadcast.Dispatcher;

public class MapLocationSubscriber implements LocationSubscriber {

    public static final String TAG = MapLocationSubscriber.class.getSimpleName();

    private MapActivity mContext;
    private BroadcastReceiver mLocationReceiver = locationReceiver();

    // CONSTRUCTOR

    public MapLocationSubscriber(MapActivity ctx){
        mContext = ctx;
    }

    // LIFE CYCLE METHODS

    public void register(){
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(mContext);
        Dispatcher.register(bm, mLocationReceiver, LocationService.ACTION_LOCATION_RECEIVED);
    }

    public void unregister(){
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(mContext);
        bm.unregisterReceiver(mLocationReceiver);
    }

    // BROADCAST RECEIVERS

    private BroadcastReceiver locationReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                Location l = i.getExtras().getParcelable(LocationService.ACTION_LOCATION_RECEIVED);
                mContext.addLocation(l);
            }
        };
    }
}
