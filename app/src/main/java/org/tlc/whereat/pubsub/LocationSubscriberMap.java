package org.tlc.whereat.pubsub;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.tlc.whereat.activities.MapActivity;
import org.tlc.whereat.model.UserLocation;

public class LocationSubscriberMap implements LocationSubscriber {

    public static final String TAG = LocationSubscriberMap.class.getSimpleName();

    private MapActivity mContext;
    private BroadcastReceiver mLocationReceiver = locationReceiver();

    // CONSTRUCTOR

    public LocationSubscriberMap(MapActivity ctx){
        mContext = ctx;
    }

    // LIFE CYCLE METHODS

    public void register(){
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(mContext);
        Dispatcher.register(bm, mLocationReceiver, LocationPublisher.ACTION_LOCATION_RECEIVED);
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
                UserLocation l = i.getExtras().getParcelable(LocationPublisher.ACTION_LOCATION_RECEIVED);
                mContext.map(l);
            }
        };
    }
}
