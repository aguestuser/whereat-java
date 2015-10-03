package org.tlc.whereat.pubsub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class Dispatcher {

    public static void register(LocalBroadcastManager bm, BroadcastReceiver br, String action){
        bm.registerReceiver(br, new IntentFilter(action));
    }

    public static void broadcast(Context ctx, Intent i){
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(i);
    }

    public static void broadcast(LocalBroadcastManager bm, Context ctx, Intent i){
        LocalBroadcastManager bm_ = (bm != null) ? bm : LocalBroadcastManager.getInstance(ctx);
        bm_.sendBroadcast(i);
    }
}
