package org.tlc.whereat.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.tlc.whereat.activities.MapActivity;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.pubsub.Dispatcher;
import org.tlc.whereat.pubsub.LocationPublisher;

public class LocationMappingReceivers extends Receiver {

    public static final String TAG = LocationMappingReceivers.class.getSimpleName();

    protected BroadcastReceiver mLocationReceiver = locationReceiver();

    // CONSTRUCTOR

    public LocationMappingReceivers(Context ctx, LocalBroadcastManager lbm){
        mCtx = ctx;
        mLbm = lbm;
    }

    // PUBLIC METHODS

    public void register(){
        Dispatcher.register(mLbm, mLocationReceiver, LocationPublisher.ACTION_LOCATION_RECEIVED);
    }

    public void unregister(){
        mLbm.unregisterReceiver(mLocationReceiver);
    }

    // BROADCAST RECEIVERS

    private BroadcastReceiver locationReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                UserLocation l = i.getExtras().getParcelable(LocationPublisher.ACTION_LOCATION_RECEIVED);
                ((MapActivity) mCtx).map(l);
            }
        };
    }
}
