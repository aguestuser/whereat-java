package org.tlc.whereat.support;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import org.tlc.whereat.pubsub.Scheduler;

public class FakeScheduler extends Scheduler {

    public FakeScheduler(Context ctx, LocalBroadcastManager lbm){
        super(ctx, lbm);
    }

    public FakeScheduler setForgetHandler(Handler h){
        mForgetHandler = h;
        return this;
    }

    public FakeScheduler setForgetRunnable(Runnable r){
        mForgetRunnable = r;
        return this;
    }
}
