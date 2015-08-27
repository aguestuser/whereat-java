package org.tlc.whereat.pubsub;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.util.PopToast;

public class LocSubMain implements LocationSubscriber {

    public static final String TAG = LocSubMain.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000; //TODO: move to resources
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000; //TODO: move to resources

    private Context mContext;

    private BroadcastReceiver mLocationPublicationReceiver = locationPublicationReceiver();
    private BroadcastReceiver mLocationsClearedReceiver = locationsClearedReceiver();
    private BroadcastReceiver mFailedLocationRequestReceiver = failedLocationRequestReceiver();
    private BroadcastReceiver mApiClientDisconnected = apiClientDisconnected();
    private BroadcastReceiver mLocationServicesDisabledReceiver = locationServicesDisabledReceiver();
    private BroadcastReceiver mPlayServicesDisabledReceiver = playServicesDisabledReceiver();

    // CONSTRUCTOR

    public LocSubMain(Context ctx){
        mContext = ctx;
    }

    // LIFE CYCLE METHODS

    public void register(){
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(mContext);
        Dispatcher.register(bm, mLocationPublicationReceiver, LocationPublisher.ACTION_LOCATION_PUBLISHED);
        Dispatcher.register(bm, mLocationsClearedReceiver, LocationPublisher.ACTION_LOCATIONS_CLEARED);
        Dispatcher.register(bm, mFailedLocationRequestReceiver, LocationPublisher.ACTION_LOCATION_REQUEST_FAILED);
        Dispatcher.register(bm, mApiClientDisconnected, LocationPublisher.ACTION_GOOGLE_API_CLIENT_DISCONNECTED);
        Dispatcher.register(bm, mLocationServicesDisabledReceiver, LocationPublisher.ACTION_LOCATION_SERVICES_DISABLED);
        Dispatcher.register(bm, mPlayServicesDisabledReceiver, LocationPublisher.ACTION_PLAY_SERVICES_DISABLED);
    }

    public void unregister(){
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(mContext);
        bm.unregisterReceiver(mLocationPublicationReceiver);
        bm.unregisterReceiver(mLocationsClearedReceiver);
        bm.unregisterReceiver(mFailedLocationRequestReceiver);
        bm.unregisterReceiver(mApiClientDisconnected);
        bm.unregisterReceiver(mLocationServicesDisabledReceiver);
        bm.unregisterReceiver(mPlayServicesDisabledReceiver);
    }

    // BROADCAST RECEIVERS

    //TODO replace BroadcastReceivers with calls to AndroidObservable.fromBroacast()
    // see http://blog.danlew.net/2014/10/08/grokking-rxjava-part-4/

    private BroadcastReceiver locationPublicationReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PopToast.briefly(mContext, "Location shared.");
            }
        };
    }

    private BroadcastReceiver locationsClearedReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PopToast.briefly(mContext, "User data cleared from server.");
            }
        };
    }

    private BroadcastReceiver locationReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                UserLocation l = i.getExtras().getParcelable(LocationPublisher.ACTION_LOCATION_RECEIVED);
                PopToast.location(mContext, l);
            }
        };
    }

    IntentFilter clearFilter = new IntentFilter(LocationPublisher.ACTION_LOCATIONS_CLEARED);


    private BroadcastReceiver failedLocationRequestReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                PopToast.briefly(mContext, "Location request failed");
            }
        };
    }

    private BroadcastReceiver apiClientDisconnected(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                ConnectionResult cr = i.getExtras().getParcelable(LocationPublisher.ACTION_GOOGLE_API_CLIENT_DISCONNECTED);
                fixApiConnection(cr);
            }
        };
    }

    private BroadcastReceiver locationServicesDisabledReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fixLocationServices();
            }
        };
    }

    private BroadcastReceiver playServicesDisabledReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fixPlayServices();
            }
        };
    }

    // FIXERS

    private void fixLocationServices(){
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

    private void fixApiConnection(ConnectionResult cr){
        if (cr.hasResolution() && mContext instanceof Activity) {
            try {
                cr.startResolutionForResult((Activity) mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            PopToast.briefly(mContext, "Location service disconnected;");
            Log.i(TAG, "Location services connection failed with code " + cr.getErrorCode());
        }
    }

    private void fixPlayServices(){
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

        if (GooglePlayServicesUtil.isUserRecoverableError(code)) {
            GooglePlayServicesUtil
                .getErrorDialog(code, (Activity) mContext, PLAY_SERVICES_RESOLUTION_REQUEST)
                .show();
        }
        else {
            Toast.makeText(
                mContext,
                "This device does not support Google Play Services, which is required for location sharing.",
                Toast.LENGTH_SHORT).show();
        }
    }
}
