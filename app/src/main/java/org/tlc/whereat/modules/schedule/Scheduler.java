package org.tlc.whereat.modules.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import org.tlc.whereat.modules.pubsub.Dispatcher;

import java.util.Date;

public class Scheduler {

    // FIELDS
    public static String TAG = Scheduler.class.getSimpleName();
    public static String ACTION_LOCATIONS_FORGOTTEN = TAG + ".LOCATIONS_FORGOTTEN";

    protected Context mCtx;
    protected LocalBroadcastManager mLbm;
    protected Handler mForgetHandler;
    protected Runnable mForgetRunnable;

    // CONSTRUCTORS

    public static Scheduler getInstance(Context ctx){
        return new Scheduler(ctx, LocalBroadcastManager.getInstance(ctx));
    }

    public static Scheduler getInstance(Context ctx, LocalBroadcastManager lbm){
        return new Scheduler(ctx, lbm);
    }

    protected Scheduler(Context ctx, LocalBroadcastManager lbm){
        mCtx = ctx;
        mLbm = lbm;
    }

    /**
     * Periodically deletes all records older than a certain time-to-live threshold from DB
     *
     * @param interval Interval for running `forget` (in millis)
     * @param ttl Amount of time (in millis) that a loc record should live before being forgotten
     * @param now Current time in millis since 1970 (paramaterized as optional for mocking in tests)
     */

    public void forget(long interval, long ttl, long... now){

        HandlerThread thread = new HandlerThread("HandlerThread");
        thread.start();
        mForgetHandler = new Handler(Looper.getMainLooper());

        mForgetRunnable = new Runnable() {
            @Override
            public void run() {
                long rightNow = now.length > 0 ? now[0] : new Date().getTime();
                broadcastForget(mLbm, mCtx, rightNow - ttl);
                mForgetHandler.postDelayed(this, interval);
            }
        };

        mForgetHandler.postDelayed(mForgetRunnable, interval);
    }

    public void cancelForget(){
        if(mForgetHandler != null) mForgetHandler.removeCallbacks(mForgetRunnable);
    }

    protected void broadcastForget(LocalBroadcastManager lbm, Context ctx, long expiration){
        Intent i = new Intent()
            .setAction(ACTION_LOCATIONS_FORGOTTEN)
            .putExtra(ACTION_LOCATIONS_FORGOTTEN, expiration);
        Dispatcher.broadcast(lbm, ctx, i);
    }
}
