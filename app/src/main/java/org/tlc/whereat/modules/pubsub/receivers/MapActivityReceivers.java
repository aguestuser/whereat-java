package org.tlc.whereat.modules.pubsub.receivers;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;

public class MapActivityReceivers extends Receivers {

    //FIELDS
    public static final String TAG = MainActivityReceivers.class.getSimpleName();

    //CONSTRUCTOR
    public MapActivityReceivers(Context ctx){
        super(ctx);

        mReceivers = Arrays.asList(
            new LocationNotificationReceivers(mCtx, mLbm),
            new LocationMappingReceivers(mCtx, mLbm));
    }
}
