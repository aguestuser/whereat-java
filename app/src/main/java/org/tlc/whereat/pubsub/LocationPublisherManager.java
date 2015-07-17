package org.tlc.whereat.pubsub;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class LocationPublisherManager {

    public static final String TAG = LocationPublisherManager.class.getSimpleName();

    private Context mContext;
    private LocationPublisher mLocationPublisher;
    private ServiceConnection mLocationServiceConnection = getLocationServiceConnection();

    private static LocationPublisherManager mInstance;

    // CONSTRUCTOR

    public static LocationPublisherManager getInstance(Context ctx){
        if (mInstance == null) return new LocationPublisherManager(ctx).start();
        else return mInstance;
    }

    private LocationPublisherManager(Context ctx){
        mContext = ctx;
    }

    // LIFE CYCLE METHODS

    public LocationPublisherManager start(){
        Intent i = new Intent(mContext, LocationPublisher.class);
        mContext.startService(i);
        return this;
    }

    public void stop(){
        Intent i = new Intent(mContext, LocationPublisher.class);
        mContext.stopService(i);
    }

    public boolean bind(){
        Intent i = new Intent(mContext, LocationPublisher.class);
        return mContext.bindService(i, mLocationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind(){
        mContext.unbindService(mLocationServiceConnection);
    }

    private ServiceConnection getLocationServiceConnection(){
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "Location service connected.");
                mLocationPublisher = ((LocationPublisher.LocationServiceBinder) service).getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "Location service disconnected.");
                mLocationPublisher = null;
            }
        };
    }

    // LOCATION SERVICE ACCESSORS

    public void ping(){
        mLocationPublisher.ping();
    }

    public boolean isPolling(){
        return mLocationPublisher.isPolling();
    }

    public void poll(){
        mLocationPublisher.poll();
    }

    public void stopPolling(){
        mLocationPublisher.stopPolling();
    }


}
