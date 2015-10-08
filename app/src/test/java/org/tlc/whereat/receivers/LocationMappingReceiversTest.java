package org.tlc.whereat.receivers;

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
import org.tlc.whereat.broadcasters.LocPubBroadcasters;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.pubsub.Scheduler;

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

        verify(lbm).registerReceiver(eq(rcv.mMap), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocPubBroadcasters.ACTION_LOCATION_RECEIVED)).isTrue();

        verify(lbm).registerReceiver(eq(rcv.mForget), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(Scheduler.ACTION_LOCATIONS_FORGOTTEN)).isTrue();

    }

    @Test
    public void unregister_should_unRegisterAllReceivers(){
        rcv.unregister();

        verify(lbm).unregisterReceiver(rcv.mMap);
        verify(lbm).unregisterReceiver(rcv.mForget);
    }

    @Test
    public void locationReceiver_should_addReceivedLocationToMap(){
        rcv.register();
        rcv.mCtx = spy(rcv.mCtx);
        UserLocation loc = s17UserLocationStub();

        lbm.sendBroadcast(new Intent()
            .setAction(LocPubBroadcasters.ACTION_LOCATION_RECEIVED)
            .putExtra(LocPubBroadcasters.ACTION_LOCATION_RECEIVED, loc));

        verify((MapActivity)rcv.mCtx).map(loc);
    }

}