package org.tlc.whereat.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.tlc.whereat.activities.MapActivity;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.pubsub.Dispatcher;
import org.tlc.whereat.pubsub.LocationPublisher;
import org.tlc.whereat.pubsub.Scheduler;
import org.tlc.whereat.util.PopToast;

import java.text.ParseException;

import rx.Subscriber;

public class LocationMappingReceivers extends Receiver {

    //FIELDS

    protected BroadcastReceiver mPub = pub();
    public static final String TAG = LocationMappingReceivers.class.getSimpleName();
    protected BroadcastReceiver mForget = forget();

    // CONSTRUCTOR

    public LocationMappingReceivers(Context ctx, LocalBroadcastManager lbm){
        mCtx = ctx;
        mLbm = lbm;
    }

    // PUBLIC METHODS

    public void register(){
        Dispatcher.register(mLbm, mPub, LocationPublisher.ACTION_LOCATION_RECEIVED);
        Dispatcher.register(mLbm, mForget, Scheduler.ACTION_LOCATIONS_FORGOTTEN);
    }

    public void unregister(){
        mLbm.unregisterReceiver(mPub);
        mLbm.unregisterReceiver(mForget);
    }

    // BROADCAST RECEIVERS

    private BroadcastReceiver pub(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                UserLocation l = i.getExtras().getParcelable(LocationPublisher.ACTION_LOCATION_RECEIVED);
                ((MapActivity) mCtx).map(l);
            }
        };
    }

    private BroadcastReceiver forget(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                long time = i.getExtras().getLong(Scheduler.ACTION_LOCATIONS_FORGOTTEN);
                ((MapActivity) mCtx).forgetSince(time);
            }
        };
    }
}
