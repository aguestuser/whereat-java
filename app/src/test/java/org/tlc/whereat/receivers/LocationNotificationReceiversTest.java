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
import org.tlc.whereat.R;
import org.tlc.whereat.pubsub.LocationPublisher;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class LocationNotificationReceiversTest extends ReceiversTest {

    LocationNotificationReceivers rcv;

    @Before
    public void setup(){
        ctx = createActivity(Activity.class);
        lbm = spy(LocalBroadcastManager.getInstance(RuntimeEnvironment.application));
        rcv = new LocationNotificationReceivers(ctx, lbm);
        ifArg = ArgumentCaptor.forClass(IntentFilter.class);
    }

    @Test
    public void register_should_registerBroadcastReceivers(){
        rcv.register();

        verify(lbm).registerReceiver(eq(rcv.mLocationPublicationReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATION_PUBLISHED)).isTrue();

        verify(lbm).registerReceiver(eq(rcv.mLocationsClearedReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATIONS_CLEARED)).isTrue();

        verify(lbm).registerReceiver(eq(rcv.mLocationRetrievalFailureReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATION_REQUEST_FAILED)).isTrue();
    }

    @Test
    public void unregister_should_unRegisterAllReceivers(){
        rcv.unregister();

        verify(lbm).unregisterReceiver(rcv.mLocationPublicationReceiver);
        verify(lbm).unregisterReceiver(rcv.mLocationsClearedReceiver);
        verify(lbm).unregisterReceiver(rcv.mLocationRetrievalFailureReceiver);
    }

    @Test
    public void locationPublicationReceiver_should_notifyUserOfLocationPublication(){
        rcv.register();
        lbm.sendBroadcast(new Intent().setAction(LocationPublisher.ACTION_LOCATION_PUBLISHED));

        assertThat(lastToast()).isEqualTo(ctx.getString(R.string.loc_shared_toast));
    }

    @Test
    public void locationRetrievalFailureReceiver_should_notifyUserOfFailureToRetrieveLocation(){
        rcv.register();
        lbm.sendBroadcast(new Intent().setAction(LocationPublisher.ACTION_LOCATION_REQUEST_FAILED));

        assertThat(lastToast()).isEqualTo(ctx.getString(R.string.loc_retrieval_failed_toast));
    }

    @Test
    public void locationsClearedReceiver_should_notifyUserOfDeletion(){
        rcv.register();
        lbm.sendBroadcast(new Intent().setAction(LocationPublisher.ACTION_LOCATIONS_CLEARED));

        assertThat(lastToast()).isEqualTo(ctx.getString(R.string.loc_clear_toast));
    }

}