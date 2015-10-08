package org.tlc.whereat.receivers;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

public abstract class Receiver {

    protected Context mCtx;
    protected LocalBroadcastManager mLbm;
    protected List<Receiver> mReceivers;

    public abstract void register();
    public abstract void unregister();

}
