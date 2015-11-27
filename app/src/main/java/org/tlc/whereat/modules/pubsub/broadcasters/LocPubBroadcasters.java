package org.tlc.whereat.modules.pubsub.broadcasters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;

import org.tlc.whereat.model.ApiMessage;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.modules.pubsub.Dispatcher;

public class LocPubBroadcasters {

    // FIELDS

    public static String TAG = LocPubBroadcasters.class.getSimpleName();

    public static final String ACTION_GOOGLE_API_CLIENT_DISCONNECTED = TAG + ".GOOGLE_API_CLIENT_DISCONNECTED";
    public static final String ACTION_LOCATION_PUBLISHED = TAG + ".LOCATION_PUBLISHED";
    public static final String ACTION_LOCATION_RECEIVED = TAG + ".LOCATION_RECEIVED";
    public static final String ACTION_LOCATION_REQUEST_FAILED = TAG + ".LOCATION_REQUEST_FAILED";
    public static final String ACTION_LOCATION_SERVICES_DISABLED = TAG + ".LOCATION_SERVICES_DISABLED";
    public static final String ACTION_PLAY_SERVICES_DISABLED = TAG + ".PLAY_SERVICES_DISABLED";
    public static final String ACTION_LOCATIONS_CLEARED = TAG + ".ACTION_LOCATIONS_CLEARED";


    protected Context mCtx;
    protected LocalBroadcastManager mLbm;

    // CONSTRUCTORS

    public static LocPubBroadcasters getInstance (Context ctx){
        return new LocPubBroadcasters(ctx, LocalBroadcastManager.getInstance(ctx));
    }

    public static LocPubBroadcasters getInstance(Context c, LocalBroadcastManager lbm){
        return new LocPubBroadcasters(c, lbm);
    }

    protected LocPubBroadcasters(Context ctx, LocalBroadcastManager lbm){
        mCtx = ctx;
        mLbm = lbm;
    }

    // PUBLIC METHODS

    public void pub() {
        Intent i = new Intent().setAction(ACTION_LOCATION_PUBLISHED);
        Dispatcher.broadcast(mLbm, mCtx, i);
    }

    public void clear(ApiMessage msg){
        Intent i = new Intent().setAction(ACTION_LOCATIONS_CLEARED);
        Dispatcher.broadcast(mLbm, mCtx, i);
    }

    public void map(UserLocation l){
        Intent i = new Intent()
            .setAction(ACTION_LOCATION_RECEIVED)
            .putExtra(ACTION_LOCATION_RECEIVED, l);
        Dispatcher.broadcast(mLbm, mCtx, i);
    }

    public void fail(){
        Intent i = new Intent().setAction(ACTION_LOCATION_REQUEST_FAILED);
        Dispatcher.broadcast(mLbm, mCtx, i);
    }

    public void googApiDisconnected(ConnectionResult cr){
        Intent i = new Intent()
            .setAction(ACTION_GOOGLE_API_CLIENT_DISCONNECTED)
            .putExtra(ACTION_GOOGLE_API_CLIENT_DISCONNECTED, cr);
        Dispatcher.broadcast(mLbm, mCtx, i);
    }

    public void locServicesDisabled(){
        Intent i = new Intent().setAction(ACTION_LOCATION_SERVICES_DISABLED);
        Dispatcher.broadcast(mLbm, mCtx, i);
    }

    public void playServicesDisabled(){
        Intent i = new Intent().setAction(ACTION_PLAY_SERVICES_DISABLED);
        Dispatcher.broadcast(mLbm, mCtx, i);
    }
}
