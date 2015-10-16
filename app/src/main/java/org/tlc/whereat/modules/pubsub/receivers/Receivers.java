package org.tlc.whereat.modules.pubsub.receivers;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

public abstract class Receivers {

    protected Context mCtx;
    protected LocalBroadcastManager mLbm;
    protected List<Receivers> mReceivers;

    public Receivers(Context ctx){
        mCtx = ctx;
        mLbm = LocalBroadcastManager.getInstance(mCtx);
    }

    public Receivers(Context ctx, LocalBroadcastManager lbm){
        mCtx = ctx;
        mLbm = lbm;
    }

    public void register() {
        for (Receivers r : mReceivers) r.register();
    }

    public void unregister() {
        for (Receivers r : mReceivers) r.unregister();
    }

}
