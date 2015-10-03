package org.tlc.whereat.pubsub;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.robolectric.Shadows.shadowOf;

import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLocalBroadcastManager;
import org.robolectric.shadows.ShadowLog;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.activities.MainActivity;
import org.tlc.whereat.support.FakeLocSubMain;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class LocSubMainTest {

    static { ShadowLog.stream = System.out; }

    MainActivity mainActivity = createActivity(MainActivity.class);
    LocalBroadcastManager lbm;
    ShadowLocalBroadcastManager shLbm;
    FakeLocSubMain lsm;
    ArgumentCaptor<IntentFilter> ifArg;

    @Before
    public void setup(){
        lbm = spy(LocalBroadcastManager.class);
        lsm = new FakeLocSubMain(mainActivity).setLbm(lbm);
        ifArg = ArgumentCaptor.forClass(IntentFilter.class);
    }

    @Test
    public void register_should_registerBroadcastReceivers(){
        lsm.register();

        verify(lbm).registerReceiver(eq(lsm.mLocationPublicationReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATION_PUBLISHED)).isTrue();

        verify(lbm).registerReceiver(eq(lsm.mLocationsClearedReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATIONS_CLEARED)).isTrue();

        verify(lbm).registerReceiver(eq(lsm.mFailedLocationRequestReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATION_REQUEST_FAILED)).isTrue();

        verify(lbm).registerReceiver(eq(lsm.mApiClientDisconnected), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_GOOGLE_API_CLIENT_DISCONNECTED)).isTrue();

        verify(lbm).registerReceiver(eq(lsm.mLocationServicesDisabledReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATION_SERVICES_DISABLED)).isTrue();

        verify(lbm).registerReceiver(eq(lsm.mPlayServicesDisabledReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_PLAY_SERVICES_DISABLED)).isTrue();

        verify(lbm).registerReceiver(eq(lsm.mForgetReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(Scheduler.ACTION_LOCATIONS_FORGOTTEN)).isTrue();
    }

    @Test
    public void unregister_should_unRegisterAllReceivers(){
        lsm.unregister();

        verify(lbm).unregisterReceiver(lsm.mLocationPublicationReceiver);
        verify(lbm).unregisterReceiver(lsm.mLocationsClearedReceiver);
        verify(lbm).unregisterReceiver(lsm.mFailedLocationRequestReceiver);
        verify(lbm).unregisterReceiver(lsm.mApiClientDisconnected);
        verify(lbm).unregisterReceiver(lsm.mLocationServicesDisabledReceiver);
        verify(lbm).unregisterReceiver(lsm.mPlayServicesDisabledReceiver);
        verify(lbm).unregisterReceiver(lsm.mForgetReceiver);
    }

    @Test
    public void forgetReceiver_should_toastForgetMessage(){
        lsm.register();
        String msg = "Deleted records older than 09/17 12:00AM";
        Intent i = new Intent()
            .setAction(Scheduler.ACTION_LOCATIONS_FORGOTTEN)
            .putExtra(Scheduler.ACTION_LOCATIONS_FORGOTTEN, msg);

        lbm.sendBroadcast(i);
        assertThat(lastToast()).isEqualTo(msg);
    }

}