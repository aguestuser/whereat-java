package org.tlc.whereat.modules.pubsub.receivers;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

public class MainActivityReceivers extends Receiver {

    //FIELDS
    public static final String TAG = MainActivityReceivers.class.getSimpleName();

    //CONSTRUCTOR

    public MainActivityReceivers(Context ctx){
        mCtx = ctx;
        mLbm = LocalBroadcastManager.getInstance(mCtx);

        mReceivers = new ArrayList<>();
        mReceivers.add(new LocationNotificationReceivers(mCtx, mLbm));
        mReceivers.add(new GoogleApiReceivers(mCtx, mLbm));
    }

    // PUBLIC METHODS

    public void register(){
        for (Receiver r : mReceivers) {
            r.register();
        }
    }

    public void unregister(){
        for (Receiver r: mReceivers) r.unregister();
    }

}
