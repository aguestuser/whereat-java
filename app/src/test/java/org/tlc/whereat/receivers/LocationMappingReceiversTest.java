package org.tlc.whereat.receivers;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.activities.MapActivity;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.pubsub.LocationPublisher;
import org.tlc.whereat.pubsub.Scheduler;
import org.tlc.whereat.support.ActivityHelpers;
import org.tlc.whereat.support.FakeMapActivity;
import org.tlc.whereat.support.LocationHelpers;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.LocationHelpers.s17UserLocationStub;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class LocationMappingReceiversTest extends ReceiversTest {

    LocationMappingReceivers rcv;

    @Before
    public void setup(){
        ctx = mock(MapActivity.class);
        lbm = spy(LocalBroadcastManager.getInstance(RuntimeEnvironment.application));
        ifArg = ArgumentCaptor.forClass(IntentFilter.class);
        rcv = new LocationMappingReceivers(ctx, lbm);
    }
    @Test
    public void register_should_registerBroadcastReceivers(){
        rcv.register();

        verify(lbm).registerReceiver(eq(rcv.mLocationReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATION_RECEIVED)).isTrue();
    }

    @Test
    public void unregister_should_unRegisterAllReceivers(){
        rcv.unregister();

        verify(lbm).unregisterReceiver(rcv.mLocationReceiver);
    }

    @Test
    public void locationReceiver_should_addReceivedLocationToMap(){
        rcv.register();
        rcv.mCtx = spy(rcv.mCtx);
        UserLocation loc = s17UserLocationStub();

        lbm.sendBroadcast(new Intent()
            .setAction(LocationPublisher.ACTION_LOCATION_RECEIVED)
            .putExtra(LocationPublisher.ACTION_LOCATION_RECEIVED, loc));

        verify((MapActivity)rcv.mCtx).map(loc);
    }

}