package org.tlc.whereat.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
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

import org.tlc.whereat.R;
import org.tlc.whereat.model.ApiMessage;
import org.tlc.whereat.modules.api.WhereatApiClient;
import org.tlc.whereat.modules.schedule.Scheduler;
import org.tlc.whereat.modules.pubsub.broadcasters.LocPubBroadcasters;
import org.tlc.whereat.modules.db.LocationDao;
import org.tlc.whereat.model.UserLocation;

import java.util.UUID;

import rx.Observable;
import rx.functions.Action1;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class LocationPublisher extends Service
    implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    // FIELDS

    public static final String TAG = LocationPublisher.class.getSimpleName();
    public static final long sForgetInterval = 60 * 1000L; // 1 minute
//    public static final long sForgetInterval = 5 * 1000L; // 5 sec --> FOR DEBUGGING & DEMOS

    protected IBinder mBinder = new LocationServiceBinder();
    protected GoogleApiClient mGoogClient;
    protected FusedLocationProviderApi mLocProvider;
    protected LocationRequest mLocReq;
    protected LocationDao mDao;
    protected WhereatApiClient mWhereatClient;
    protected Scheduler mScheduler;
    protected LocPubBroadcasters mBroadcast;
    protected Action1<UserLocation> mLocSub;
    protected Action1<ApiMessage> mClearSub;
    protected SharedPreferences mPrefs;
    protected OnSharedPreferenceChangeListener mPrefListener;

    protected String mUserId;
    protected int mPollInterval;
    protected long mTtl;
    protected boolean mPolling = false;
    protected long mLastPing = -1L;
    protected boolean mStarted = false;

    // LIFE CYCLE METHODS

    @Override
    public void onCreate(){
        mBroadcast = LocPubBroadcasters.getInstance(this);
        Log.i(TAG, "Location service created.");
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId){
        if (playServicesDisabled()) {
            mBroadcast.playServicesDisabled();
            return Service.START_REDELIVER_INTENT;
        }
        else if (!mStarted) {
            initialize();
            run();
            mStarted = true;
            return Service.START_STICKY;
        }
        else {
            return Service.START_STICKY;
        }
    }

    protected void initialize(){

        mGoogClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefListener = buildPrefListener();
        mPollInterval = getPollIntervalPref();
        mTtl = getTtlPref();
        mLocReq = buildLocReq();

        mWhereatClient = WhereatApiClient.getInstance();
        mDao = new LocationDao(this);
        mScheduler = Scheduler.getInstance(this);
        mLocProvider = FusedLocationApi;
        //if (mBroadcast == null) mBroadcast = LocPubBroadcasters.getInstance(this);

        mLocSub = this::record;
        mClearSub = mBroadcast::clear;

        mUserId = getRandomId();
        mPolling = false;
    }



    protected void run(){
        if (!mGoogClient.isConnected()) mGoogClient.connect();
        mDao.connect();
        mPrefs.registerOnSharedPreferenceChangeListener(mPrefListener);
        mScheduler.forget(sForgetInterval, mTtl);

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

    protected OnSharedPreferenceChangeListener buildPrefListener(){
        return ((SharedPreferences sp, String key) -> {
            if (key.equals(getString(R.string.pref_loc_share_interval_key))) resetPollInterval();
            if (key.equals(getString(R.string.pref_loc_ttl_key))) resetTtl();
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        poll();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult cr) { mBroadcast.googApiDisconnected(cr); }

    @Override
    public void onLocationChanged(Location l) { relay(l); }


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
        mWhereatClient.remove(ul).subscribe(mClearSub);
        mDao.clear();
    }

    // LOCATION HANDLERS

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

    protected void record(UserLocation ul){
        mBroadcast.map(ul);
        mDao.save(ul);
    }

    // HELPERS

    protected LocationRequest buildLocReq(){
        return LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(mPollInterval)
            .setFastestInterval(mPollInterval);
    }

    protected void resetPollInterval(){
        mPollInterval = getPollIntervalPref();
        mLocReq = buildLocReq();
        restartPolling();
    }

    protected int getPollIntervalPref(){
        return parseInt(
            mPrefs.getString(
                getString(R.string.pref_loc_share_interval_key),
                getString(R.string.pref_loc_share_interval_value_2)));
    }

    protected void restartPolling(){
        if (mPolling) {
            stopPolling();
            poll();
        }
    }

    protected void resetTtl(){
        mTtl = getTtlPref();
        mScheduler.cancelForget();
        mScheduler.forget(sForgetInterval, mTtl);
    }

    protected long getTtlPref(){
        return parseLong(
            mPrefs.getString(
                getString(R.string.pref_loc_ttl_key),
                getString(R.string.pref_loc_ttl_value_1)));
    }

    protected String getRandomId(){
        return UUID.randomUUID().toString();
    }


    protected boolean playServicesDisabled() {
        return (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS);
    }
    protected boolean locationServicesDisabled(){
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
