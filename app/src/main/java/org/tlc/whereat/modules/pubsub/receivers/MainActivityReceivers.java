package org.tlc.whereat.modules.pubsub.receivers;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Arrays;

public class MainActivityReceivers extends Receivers {

    //FIELDS
    public static final String TAG = MainActivityReceivers.class.getSimpleName();

    //CONSTRUCTOR
    public MainActivityReceivers(Context ctx){
        super(ctx);

        mReceivers = Arrays.asList(
            new LocationNotificationReceivers(mCtx, mLbm),
            new GoogleApiReceivers(mCtx, mLbm));
    }
}
