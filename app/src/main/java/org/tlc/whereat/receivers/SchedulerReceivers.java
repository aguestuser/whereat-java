package org.tlc.whereat.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.tlc.whereat.pubsub.Dispatcher;
import org.tlc.whereat.pubsub.Scheduler;
import org.tlc.whereat.util.PopToast;

public class SchedulerReceivers extends Receiver {

    //FIELDS

    public static final String TAG = MainActivityReceivers.class.getSimpleName();

    protected BroadcastReceiver mForgetReceiver = forgetReceiver();

    //CONSTRUCTOR

    public SchedulerReceivers(Context ctx, LocalBroadcastManager lbm){
        mCtx = ctx;
        mLbm = lbm;
    }

    //PUBLIC METHODS

    public void register() {
        Dispatcher.register(mLbm, mForgetReceiver, Scheduler.ACTION_LOCATIONS_FORGOTTEN);
    }

    public void unregister(){
        mLbm.unregisterReceiver(mForgetReceiver);
    }

    //RECEIVERS

    protected BroadcastReceiver forgetReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getExtras().getString(Scheduler.ACTION_LOCATIONS_FORGOTTEN);
                PopToast.briefly(mCtx, msg);
            }
        };
    }

}
