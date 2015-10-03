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
    protected final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000; //TODO: move to resources
    protected final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000; //TODO: move to resources

    protected Context mContext;
    protected LocalBroadcastManager mLbm;

    protected BroadcastReceiver mLocationPublicationReceiver = locationPublicationReceiver();
    protected BroadcastReceiver mLocationsClearedReceiver = locationsClearedReceiver();
    protected BroadcastReceiver mFailedLocationRequestReceiver = failedLocationRequestReceiver();
    protected BroadcastReceiver mApiClientDisconnected = apiClientDisconnected();
    protected BroadcastReceiver mLocationServicesDisabledReceiver = locationServicesDisabledReceiver();
    protected BroadcastReceiver mPlayServicesDisabledReceiver = playServicesDisabledReceiver();
    protected BroadcastReceiver mForgetReceiver = forgetReceiver();

    // CONSTRUCTOR

    public LocSubMain(Context ctx){
        mContext = ctx;
        mLbm = LocalBroadcastManager.getInstance(mContext);
    }

    // LIFE CYCLE METHODS

    public void register(){
        Dispatcher.register(mLbm, mLocationPublicationReceiver, LocationPublisher.ACTION_LOCATION_PUBLISHED);
        Dispatcher.register(mLbm, mLocationsClearedReceiver, LocationPublisher.ACTION_LOCATIONS_CLEARED);
        Dispatcher.register(mLbm, mFailedLocationRequestReceiver, LocationPublisher.ACTION_LOCATION_REQUEST_FAILED);
        Dispatcher.register(mLbm, mApiClientDisconnected, LocationPublisher.ACTION_GOOGLE_API_CLIENT_DISCONNECTED);
        Dispatcher.register(mLbm, mLocationServicesDisabledReceiver, LocationPublisher.ACTION_LOCATION_SERVICES_DISABLED);
        Dispatcher.register(mLbm, mPlayServicesDisabledReceiver, LocationPublisher.ACTION_PLAY_SERVICES_DISABLED);
        Dispatcher.register(mLbm, mForgetReceiver, Scheduler.ACTION_LOCATIONS_FORGOTTEN);
    }

    public void unregister(){
        mLbm.unregisterReceiver(mLocationPublicationReceiver);
        mLbm.unregisterReceiver(mLocationsClearedReceiver);
        mLbm.unregisterReceiver(mFailedLocationRequestReceiver);
        mLbm.unregisterReceiver(mApiClientDisconnected);
        mLbm.unregisterReceiver(mLocationServicesDisabledReceiver);
        mLbm.unregisterReceiver(mPlayServicesDisabledReceiver);
        mLbm.unregisterReceiver(mForgetReceiver);
    }

    // BROADCAST RECEIVERS

    //TODO replace BroadcastReceivers with calls to AndroidObservable.fromBroacast()
    // see http://blog.danlew.net/2014/10/08/grokking-rxjava-part-4/

    protected BroadcastReceiver locationPublicationReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PopToast.briefly(mContext, "Location shared.");
            }
        };
    }

    protected BroadcastReceiver locationsClearedReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PopToast.briefly(mContext, "User data cleared from server.");
            }
        };
    }

    protected BroadcastReceiver locationReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                UserLocation l = i.getExtras().getParcelable(LocationPublisher.ACTION_LOCATION_RECEIVED);
                PopToast.location(mContext, l);
            }
        };
    }

    IntentFilter clearFilter = new IntentFilter(LocationPublisher.ACTION_LOCATIONS_CLEARED);


    protected BroadcastReceiver failedLocationRequestReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                PopToast.briefly(mContext, "Location request failed");
            }
        };
    }

    protected BroadcastReceiver apiClientDisconnected(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                ConnectionResult cr = i.getExtras().getParcelable(LocationPublisher.ACTION_GOOGLE_API_CLIENT_DISCONNECTED);
                fixApiConnection(cr);
            }
        };
    }

    protected BroadcastReceiver locationServicesDisabledReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fixLocationServices();
            }
        };
    }

    protected BroadcastReceiver playServicesDisabledReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fixPlayServices();
            }
        };
    }

    protected BroadcastReceiver forgetReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getExtras().getString(Scheduler.ACTION_LOCATIONS_FORGOTTEN);
                PopToast.briefly(mContext, msg);
            }
        };
    }

    // FIXERS

    protected void fixLocationServices(){
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

    protected void fixApiConnection(ConnectionResult cr){
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

    protected void fixPlayServices(){
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
