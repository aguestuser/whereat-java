package org.tlc.whereat.receivers;

import android.content.Intent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import org.tlc.whereat.R;
import org.tlc.whereat.pubsub.Dispatcher;
import org.tlc.whereat.pubsub.LocationPublisher;
import org.tlc.whereat.util.PopToast;


public class LocationNotificationReceivers extends Receiver {

    public static final String TAG = LocationNotificationReceivers.class.getSimpleName();

    protected BroadcastReceiver mLocationPublicationReceiver = locationPublicationReceiver();
    protected BroadcastReceiver mLocationsClearedReceiver = locationsClearedReceiver();
    protected BroadcastReceiver mLocationRetrievalFailureReceiver = locationRetrievalFailureReceiver();


    // CONSTRUCTOR

    public LocationNotificationReceivers(Context ctx, LocalBroadcastManager lbm){
        mCtx = ctx;
        mLbm = lbm;
    }

    // LIFE CYCLE METHODS

    public void register(){
        Dispatcher.register(mLbm, mLocationPublicationReceiver, LocationPublisher.ACTION_LOCATION_PUBLISHED);
        Dispatcher.register(mLbm, mLocationsClearedReceiver, LocationPublisher.ACTION_LOCATIONS_CLEARED);
        Dispatcher.register(mLbm, mLocationRetrievalFailureReceiver, LocationPublisher.ACTION_LOCATION_REQUEST_FAILED);
    }

    public void unregister(){
        mLbm.unregisterReceiver(mLocationPublicationReceiver);
        mLbm.unregisterReceiver(mLocationRetrievalFailureReceiver);
        mLbm.unregisterReceiver(mLocationsClearedReceiver);
    }

    // BROADCAST RECEIVERS

    //TODO replace BroadcastReceivers with calls to AndroidObservable.fromBroacast()?
    // see http://blog.danlew.net/2014/10/08/grokking-rxjava-part-4/

    protected BroadcastReceiver locationPublicationReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PopToast.briefly(mCtx, mCtx.getString(R.string.loc_shared_toast));
            }
        };
    }

    protected BroadcastReceiver locationRetrievalFailureReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                PopToast.briefly(mCtx, mCtx.getString(R.string.loc_retrieval_failed_toast));
            }
        };
    }

    protected BroadcastReceiver locationsClearedReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PopToast.briefly(mCtx, mCtx.getString(R.string.loc_clear_toast));
            }
        };
    }
}
