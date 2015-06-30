package org.tlc.whereat.modules;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
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
    }

    public static final String TAG = LocationProvider.class.getSimpleName();
    public static final int INTERVAL = 5 * 1000; // 60 seconds
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000; //TODO: move to resources

    private LocationCallback mLocationCallback;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // Constructor
    public LocationProvider(Context ctx, LocationCallback cb) {

        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(INTERVAL)
                .setFastestInterval(INTERVAL);

        mLocationCallback = cb;

        mContext = ctx;
    }

    // Public Methods

    public void connect(){
        mGoogleApiClient.connect();
    }

    public void disconnect(){
        if (mGoogleApiClient.isConnected()){
           mGoogleApiClient.disconnect();
        }
    }

    public void getLocation(){
        if (mGoogleApiClient.isConnected()){
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (loc != null) mLocationCallback.handleNewLocation(loc);
            else mLocationCallback.handleFailedLocationRequest("Location request failed");
        }
        else mLocationCallback.handleFailedLocationRequest("Location Services not connected.");
    }

    public void pollLocation(){
        Log.i(TAG, "Passive location sharing turned on.");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void stopPollingLocation(){
        Log.i(TAG, "Passive location sharing turned off.");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    // Location API Callbacks

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Location services connection failed.");
        if (connectionResult.hasResolution() && mContext instanceof Activity) {
            try {
                Log.i(TAG, "Trying to resolve location services connection.");
                Activity activity = (Activity)mContext;
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    // Location Listener Callbacks

    @Override
    public void onLocationChanged(Location location) {
        mLocationCallback.handleNewLocation(location);
    }

}
