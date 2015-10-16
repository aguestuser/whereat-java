package org.tlc.whereat.modules.pubsub.receivers;

import android.content.Context;

import java.util.Arrays;

public class SettingsActivityReceivers extends Receivers {

    public static final String TAG = MainActivityReceivers.class.getSimpleName();

    public SettingsActivityReceivers(Context ctx){
        super(ctx);

        mReceivers = Arrays.asList(new LocationNotificationReceivers(mCtx, mLbm));
    }
}
