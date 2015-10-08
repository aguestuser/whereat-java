package org.tlc.whereat.pubsub;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

import org.tlc.whereat.api.WhereatApiClient;
import org.tlc.whereat.broadcasters.LocPubBroadcasters;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.model.UserLocation;

import java.util.UUID;

import rx.Observable;
import rx.functions.Action1;

public class LocationPublisher extends Service
    implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    // FIELDS

    public static final String TAG = LocationPublisher.class.getSimpleName();

    // FOR PROD/DEV:

    public static final int POLLING_INTERVAL = 15 * 1000; // 15 seconds
    public static final long FORGET_INTERVAL = 60*1000L; // 1 minute
    public static final long TIME_TO_LIVE = 60*60*1000L; // 1 hr

    // FOR DEBUGGING:

//    public static final int POLLING_INTERVAL = 14 * 1000; // 14 sec
//    public static final long FORGET_INTERVAL = 30 * 1000L; // 30 sec
//    public static final long TIME_TO_LIVE = 5 * 1000L; // 5 sec

    protected IBinder mBinder = new LocationServiceBinder();
    protected GoogleApiClient mGoogClient;
    protected FusedLocationProviderApi mLocProvider;
    protected LocationRequest mLocReq;
    protected LocationDao mDao;
    protected WhereatApiClient mWhereatClient;
    protected Scheduler mScheduler;
    protected LocPubBroadcasters mBroadcast;
    protected Action1<UserLocation> mLocSub;
    protected String mUserId;
    protected boolean mPolling = false;
    protected long mLastPing = -1L;

    // LIFE CYCLE METHODS

    @Override
    public void onCreate(){
        Log.i(TAG, "Location service created.");
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId){
        if (playServicesDisabled()) {
            mBroadcast.playServicesDisable();
            return Service.START_REDELIVER_INTENT;
        }
        else {
            initialize();
            run();
            return Service.START_STICKY;
        }
    }

    protected void initialize(){

        mGoogClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mLocReq = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(POLLING_INTERVAL)
            .setFastestInterval(POLLING_INTERVAL);

        mWhereatClient = WhereatApiClient.getInstance();
        mDao = new LocationDao(this);
        mScheduler = Scheduler.getInstance(this);
        mBroadcast = LocPubBroadcasters.getInstance(this);
        mLocProvider = FusedLocationApi;

        mLocSub = mBroadcast::map;

        mUserId = getRandomId();
        mPolling = false;
    }

    protected void run(){
        if (!mGoogClient.isConnected()) mGoogClient.connect();
        mDao.connect();
        mScheduler.forget(FORGET_INTERVAL, TIME_TO_LIVE);
    }

    @Override
    public IBinder onBind(Intent arg0){
        return mBinder;
    }

    public class LocationServiceBinder extends android.os.Binder {
        public LocationPublisher getService(){
            return LocationPublisher.this;
        }
    }

    @Override
    public void onDestroy(){
        stopPolling();
        mGoogClient.disconnect();
        mDao.clear();
        mDao.disconnect();
        mScheduler.cancelForget();
    }

    // CALLBACKS

    @Override
    public void onLocationChanged(Location l) { relay(l); }

    @Override
    public void onConnected(Bundle bundle) { Log.i(TAG, "Location services connected."); }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult cr) { mBroadcast.googApiDisconnected(cr); }


    // PUBLIC METHODS

    public void ping(){
        Location l = mLocProvider.getLastLocation(mGoogClient);
        if (l == null) {
            mBroadcast.fail();
            if(locationServicesDisabled()) mBroadcast.locServicesDisabled();
        }
        else relay(l);
    }

    public void poll(){
        Log.i(TAG, "Turning on location polling.");
        mLocProvider.requestLocationUpdates(mGoogClient, mLocReq, this);
        mPolling = true;
    }

    public void stopPolling(){
        Log.i(TAG, "Turning off location polling.");
        mLocProvider.removeLocationUpdates(mGoogClient, this);
        mPolling = false;
    }

    public boolean isPolling(){
        return mPolling;
    }

    public void clear(){
        UserLocation ul = mDao.get(mUserId);
        mWhereatClient.remove(ul).subscribe(mBroadcast::clear);
        mDao.clear();
    }

    // HELPERS

    protected String getRandomId(){
        return UUID.randomUUID().toString();
    }

    protected void relay(Location l){
        UserLocation ul = UserLocation.valueOf(mUserId, l);

        mBroadcast.pub();
        update(ul);
        mDao.save(ul);
        mLastPing = ul.getTime();
    }

    protected void update(UserLocation ul){
        mWhereatClient.update(ul.withTimestamp(mLastPing))
            .flatMap(Observable::from)
            .subscribe(mLocSub);
    }

    private boolean playServicesDisabled() {
        return (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS);
    }
    private boolean locationServicesDisabled(){
        return Build.VERSION.SDK_INT > 19 ? newLsOff() : oldLsOff();
    }

    @TargetApi(20)
    private boolean newLsOff(){
        int off = Settings.Secure.LOCATION_MODE_OFF;
        int current = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE, off);
        return current == off;
    }

    private boolean oldLsOff(){
        String locProviders = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return locProviders == null || locProviders.equals("");
    }

}
