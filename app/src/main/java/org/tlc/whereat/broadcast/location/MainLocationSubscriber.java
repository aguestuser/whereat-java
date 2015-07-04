package org.tlc.whereat.broadcast.location;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.tlc.whereat.modules.PopToast;
import org.tlc.whereat.services.LocationService;
import org.tlc.whereat.broadcast.Dispatcher;

public class MainLocationSubscriber implements LocationSubscriber {

    public static final String TAG = MainLocationSubscriber.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000; //TODO: move to resources
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000; //TODO: move to resources

    private Context mContext;

    private BroadcastReceiver mLocationReceiver = locationReceiver();
    private BroadcastReceiver mFailedLocationRequestReceiver = failedLocationRequestReceiver();
    private BroadcastReceiver mApiClientDisconnected = apiClientDisconnected();
    private BroadcastReceiver mLocationServicesDisabledReceiver = locationServicesDisabledReceiver();
    private BroadcastReceiver mPlayServicesDisabledReceiver = playServicesDisabledReceiver();

    // CONSTRUCTOR

    public MainLocationSubscriber(Context ctx){
        mContext = ctx;
    }

    // LIFE CYCLE METHODS

    public void register(){
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(mContext);
        Dispatcher.register(bm, mLocationReceiver, LocationService.ACTION_LOCATION_RECEIVED);
        Dispatcher.register(bm, mFailedLocationRequestReceiver, LocationService.ACTION_LOCATION_REQUEST_FAILED);
        Dispatcher.register(bm, mApiClientDisconnected, LocationService.ACTION_GOOGLE_API_CLIENT_DISCONNECTED);
        Dispatcher.register(bm, mLocationServicesDisabledReceiver, LocationService.ACTION_LOCATION_SERVICES_DISABLED);
        Dispatcher.register(bm, mPlayServicesDisabledReceiver, LocationService.ACTION_PLAY_SERVICES_DISABLED);
    }

    public void unregister(){
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(mContext);
        bm.unregisterReceiver(mLocationReceiver);
        bm.unregisterReceiver(mFailedLocationRequestReceiver);
        bm.unregisterReceiver(mApiClientDisconnected);
        bm.unregisterReceiver(mLocationServicesDisabledReceiver);
        bm.unregisterReceiver(mPlayServicesDisabledReceiver);
    }

    // BROADCAST RECEIVERS

    private BroadcastReceiver locationReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                Location l = i.getExtras().getParcelable(LocationService.ACTION_LOCATION_RECEIVED);
                PopToast.location(mContext, l);
            }
        };
    }

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
                ConnectionResult cr = i.getExtras().getParcelable(LocationService.ACTION_GOOGLE_API_CLIENT_DISCONNECTED);
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
