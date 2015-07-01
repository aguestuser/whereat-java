package org.tlc.whereat.modules;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

public class LocationProvider implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public interface LocationCallback {
        void handleNewLocation(Location loc);
        void handleFailedLocationRequest(String msg);
        void handleNoPlayServices();
    }

    // FIELDS

    public static final String TAG = LocationProvider.class.getSimpleName();
    public static final int POLLING_INTERVAL = 5 * 1000; // 5 seconds
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000; //TODO: move to resources
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000; //TODO: move to resources

    private LocationCallback mLocationCallback;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean mPolling;

    // CONSTRUCTOR

    public LocationProvider(Context ctx, LocationCallback cb) {

        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(POLLING_INTERVAL)
                .setFastestInterval(POLLING_INTERVAL);

        mLocationCallback = cb;
        mContext = ctx;
        mPolling = false;
    }

    // PUBLIC METHODS

    public void connect(){
        if (shouldConnect()) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnect(){
        if (shouldDisconnect()) {
            turnOffLocationUpdates();
            stopPolling();
            mGoogleApiClient.disconnect();
        }
    }

    public void get(){
        Location loc = lastApiLocation();
        if (loc == null) {
            enableLocationServices();
            mLocationCallback.handleFailedLocationRequest("Location request failed.");
        }
        else mLocationCallback.handleNewLocation(loc);
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

    // LOCATION ACCESSORS

    @Override
    public void onLocationChanged(Location location) {
        mLocationCallback.handleNewLocation(location);
    }

    private Location lastApiLocation(){
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    // CONNECTION CALLBACKS

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        //if (mPolling) turnOnLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution() && mContext instanceof Activity) {
            try {
                connectionResult.startResolutionForResult((Activity)mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    // LOCATION SERVICE SWITCH

    private void enableLocationServices() { if (locationServicesOff()) turnOnLocationServices(); }

    private boolean locationServicesOff(){ return newerThanKitKat() ? newLsOff() : oldLsOff(); }

    private boolean newerThanKitKat(){
        return Build.VERSION.SDK_INT > 19;
    }

    private boolean newLsOff(){
        int off = Settings.Secure.LOCATION_MODE_OFF;
        int current = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, off);
        return current == off;
    }

    private boolean oldLsOff(){
        String locProviders = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return locProviders == null || locProviders.equals("");
    }

    // LOCATION REQUEST SWITCH

    private void turnOnLocationServices(){
        new AlertDialog.Builder(mContext)
                .setMessage("Location Services not enabled.")
                .setPositiveButton("Enable",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
    }

    private void turnOnLocationUpdates(){
        Log.i(TAG, "Turning on location updates.");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void turnOffLocationUpdates(){
        Log.i(TAG, "Turning on location updates.");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // HELPERS

    private boolean shouldConnect(){
        return hasPlayServices() && !mGoogleApiClient.isConnected();
    }

    private boolean shouldDisconnect(){
        return mGoogleApiClient.isConnected();
    }

    public boolean hasPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil
                        .getErrorDialog(resultCode, (Activity) mContext, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                mLocationCallback.handleNoPlayServices();
            }
            return false;
        }
        return true;
    }
}
