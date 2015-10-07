package org.tlc.whereat.pubsub;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import org.tlc.whereat.api.WhereatApiClient;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.model.ApiMessage;
import org.tlc.whereat.model.UserLocation;

import java.util.UUID;

import rx.Observable;
import rx.Subscriber;

public class LocationPublisher extends Service
    implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    // FIELDS

    public static final String TAG = LocationPublisher.class.getSimpleName();
    public static final String ACTION_GOOGLE_API_CLIENT_DISCONNECTED = TAG + ".GOOGLE_API_CLIENT_DISCONNECTED";
    public static final String ACTION_LOCATION_PUBLISHED = TAG + "LOCATION_PUBLISHED";
    public static final String ACTION_LOCATION_RECEIVED = TAG + "LOCATION_RECEIVED";
    public static final String ACTION_LOCATION_REQUEST_FAILED = TAG + "LOCATION_REQUEST_FAILED";
    public static final String ACTION_LOCATION_SERVICES_DISABLED = TAG + "LOCATION_SERVICES_DISABLED";
    public static final String ACTION_PLAY_SERVICES_DISABLED = TAG + "PLAY_SERVICES_DISABLED";
    public static final String ACTION_LOCATIONS_CLEARED = TAG + "ACTION_LOCATIONS_CLEARED";

    // FOR PROD/DEV:
//
    public static final int POLLING_INTERVAL = 15 * 1000; // 15 seconds
    public static final long FORGET_INTERVAL = 60*1000L; // 1 minute
    public static final long TIME_TO_LIVE = 60*60*1000L; // 1 hr

    // FOR DEBUGGING:

//     public static final int POLLING_INTERVAL = 25 * 1000; // 8 sec
//     public static final long FORGET_INTERVAL = 30 * 1000L; // 10 sec
//     public static final long TIME_TO_LIVE = 500L; // .5 sec

    protected IBinder mBinder = new LocationServiceBinder();
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationDao mDao;
    protected WhereatApiClient mWhereatClient;
    protected Scheduler mScheduler;
    protected String mUserId;
    protected boolean mPolling = false;
    protected long mLastPing = -1L;
    protected Subscriber<UserLocation> mLocSub;

    // LIFE CYCLE METHODS

    @Override
    public void onCreate(){
        Log.i(TAG, "Location service created.");
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId){
        if (playServicesDisabled()) {
            broadcastPlayServicesDisabled();
            return Service.START_REDELIVER_INTENT;
        }
        else {
            initialize();
            return Service.START_STICKY;
        }
    }

    protected void initialize(){

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(POLLING_INTERVAL)
            .setFastestInterval(POLLING_INTERVAL);

        mLocSub = new Subscriber<UserLocation>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(UserLocation userLocation) {
                broadcastLocationReceived(userLocation);
            }
        };

        mWhereatClient = WhereatApiClient.getInstance();
        mDao = new LocationDao(this);
        mScheduler = Scheduler.getInstance(this);

        mPolling = false;
        mUserId = getRandomId();

        run();
    }

    protected String getRandomId(){
        return UUID.randomUUID().toString();
    }

    protected void run(){
        if (!mGoogleApiClient.isConnected()) mGoogleApiClient.connect();
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
        mGoogleApiClient.disconnect();
        mDao.clear();
        mDao.disconnect();
        mScheduler.cancelForget();
    }

    // PUBLIC METHODS

    public void ping(){
        Location l = lastApiLocation();
        if (l == null) {
            broadcastFailedLocationRequest();
            if(locationServicesDisabled()) broadcastLocationServicesDisabled();
        }
        else relay(l);
    }

    public void poll(){
        turnOnLocationUpdates();
        mPolling = true;
    }

    public void stopPolling(){
        turnOffLocationUpdates();
        mPolling = false;
    }

    public boolean isPolling(){
        return mPolling;
    }

    public void clear(){
        UserLocation ul = mDao.get(mUserId);
        mWhereatClient.remove(ul).subscribe(this::broadcastLocationsCleared);
        mDao.clear();
    }

    // API HELPERS

    protected void relay(Location l){
        UserLocation ul = UserLocation.valueOf(mUserId, l);

        broadcastLocationPublished(ul);
        update(ul);
        mDao.save(ul);
        mLastPing = ul.getTime();
    }

    protected void update(UserLocation ul){
        mWhereatClient.update(ul.withTimestamp(mLastPing))
            .flatMap(Observable::from)
            .subscribe(mLocSub);
    }

    protected Location lastApiLocation(){
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private void turnOnLocationUpdates(){
        Log.i(TAG, "Turning on location updates.");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void turnOffLocationUpdates(){
        Log.i(TAG, "Turning off location updates.");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // API CALLBACKS

    @Override
    public void onLocationChanged(Location l) {
        relay(l);
    }

    @Override
    public void onConnected(Bundle bundle) { Log.i(TAG, "Location services connected."); }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult cr) { broadcastApiDisconnected(cr); }

    // BROADCASTS

    protected void broadcastLocationPublished(UserLocation l) {
        Intent i = new Intent(ACTION_LOCATION_PUBLISHED);
        i.setAction(ACTION_LOCATION_PUBLISHED);
        Dispatcher.broadcast(this, i);
    }

    private void broadcastLocationsCleared(ApiMessage msg){
        Intent i = new Intent(ACTION_LOCATIONS_CLEARED);
        i.setAction(ACTION_LOCATIONS_CLEARED);
        Dispatcher.broadcast(this, i);
    }

    private void broadcastLocationReceived(UserLocation l){
        Intent i = new Intent(ACTION_LOCATION_RECEIVED);
        i.setAction(ACTION_LOCATION_RECEIVED);
        i.putExtra(ACTION_LOCATION_RECEIVED, l);
        Dispatcher.broadcast(this, i);
    }

    private void broadcastFailedLocationRequest(){
        Intent i = new Intent(ACTION_LOCATION_REQUEST_FAILED);
        i.setAction(ACTION_LOCATION_REQUEST_FAILED);
        Dispatcher.broadcast(this, i);
    }

    private void broadcastApiDisconnected(ConnectionResult cr){
        Intent i = new Intent();
        i.setAction(ACTION_GOOGLE_API_CLIENT_DISCONNECTED);
        i.putExtra(ACTION_GOOGLE_API_CLIENT_DISCONNECTED, cr);
        Dispatcher.broadcast(this, i);
    }

    private void broadcastLocationServicesDisabled(){
        Intent i = new Intent();
        i.setAction(ACTION_LOCATION_SERVICES_DISABLED);
        Dispatcher.broadcast(this, i);
    }

    private void broadcastPlayServicesDisabled(){
        Intent i = new Intent();
        i.setAction(ACTION_PLAY_SERVICES_DISABLED);
        Dispatcher.broadcast(this, i);
    }

    // HELPERS

    private boolean playServicesDisabled() { return (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS); }
    private boolean locationServicesDisabled(){ return newerThanKitKat() ? newLsOff() : oldLsOff(); }
    private boolean newerThanKitKat(){ return Build.VERSION.SDK_INT > 19; }

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
