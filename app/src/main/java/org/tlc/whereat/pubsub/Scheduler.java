package org.tlc.whereat.pubsub;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import org.tlc.whereat.R;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.util.TimeUtils;

import java.util.Date;

public class Scheduler {

    // FIELDS
    public static String TAG = Scheduler.class.getSimpleName();
    public static String ACTION_LOCATIONS_FORGOTTEN = TAG + ".LOCATIONS_FORGOTTEN";

    protected Context mCtx;
    protected LocalBroadcastManager mLbm;
    protected Handler mForgetHandler;
    protected Runnable mForgetRunnable;

    // CONSTRUCTOR

    public Scheduler(Context ctx, LocalBroadcastManager lbm){
        mCtx = ctx;
        mLbm = lbm;
    }

    /**
     * Periodically deletes all records older than a certain time-to-live threshold from DB
     *
     * @param dao Database accessor object
     * @param interval Interval for running `forget` (in millis)
     * @param now Current time (in millis)
     * @param ttl Amount of time (in millis) that a loc record should live before being forgotten
     */

    public void forget(LocationDao dao, long interval, long ttl, long... now){

        HandlerThread thread = new HandlerThread("HandlerThread");
        thread.start();
        mForgetHandler = new Handler(Looper.getMainLooper());

        Runnable forget = new Runnable() {
            @Override
            public void run() {
                long rightNow = now.length > 0 ? now[0] : new Date().getTime();
                broadcastForget(mLbm, mCtx, rightNow - ttl);
                mForgetHandler.postDelayed(this, interval);
            }
        };

        mForgetHandler.postDelayed(forget, interval);
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
