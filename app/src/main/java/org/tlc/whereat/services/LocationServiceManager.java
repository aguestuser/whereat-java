package org.tlc.whereat.services;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class LocationServiceManager {

    public static final String TAG = LocationServiceManager.class.getSimpleName();

    private Context mContext;
    private LocationService mLocationService;
    private ServiceConnection mLocationServiceConnection = getLocationServiceConnection();

    private static LocationServiceManager mInstance;

    // CONSTRUCTOR

    public static LocationServiceManager getInstance(Context ctx){
        if (mInstance == null) return new LocationServiceManager(ctx).start();
        else return mInstance;
    }

    private LocationServiceManager(Context ctx){
        mContext = ctx;
    }

    // LIFE CYCLE METHODS

    public LocationServiceManager start(){
        Intent i = new Intent(mContext, LocationService.class);
        mContext.startService(i);
        return this;
    }

    public void stop(){
        Intent i = new Intent(mContext, LocationService.class);
        mContext.stopService(i);
    }

    public boolean bind(){
        Intent i = new Intent(mContext, LocationService.class);
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
                mLocationService = ((LocationService.LocationServiceBinder) service).getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "Location service disconnected.");
                mLocationService = null;
            }
        };
    }

    // LOCATION SERVICE ACCESSORS

    public void ping(){
        mLocationService.ping();
    }

    public boolean isPolling(){
        return mLocationService.isPolling();
    }

    public void poll(){
        mLocationService.poll();
    }

    public void stopPolling(){
        mLocationService.stopPolling();
    }


}
