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
import org.tlc.whereat.pubsub.Scheduler;
import org.tlc.whereat.support.SampleTimes;
import org.tlc.whereat.util.TimeUtils;

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

        verify(lbm).registerReceiver(eq(rcv.mPub), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATION_PUBLISHED)).isTrue();

        verify(lbm).registerReceiver(eq(rcv.mClear), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATIONS_CLEARED)).isTrue();

        verify(lbm).registerReceiver(eq(rcv.mFail), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocationPublisher.ACTION_LOCATION_REQUEST_FAILED)).isTrue();

        verify(lbm).registerReceiver(eq(rcv.mForget), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(Scheduler.ACTION_LOCATIONS_FORGOTTEN)).isTrue();
    }

    @Test
    public void unregister_should_unRegisterAllReceivers(){
        rcv.unregister();

        verify(lbm).unregisterReceiver(rcv.mPub);
        verify(lbm).unregisterReceiver(rcv.mClear);
        verify(lbm).unregisterReceiver(rcv.mFail);
        verify(lbm).unregisterReceiver(rcv.mForget);
    }

    @Test
    public void pub_should_notifyUserOfLocationPublication(){
        rcv.register();
        lbm.sendBroadcast(new Intent().setAction(LocationPublisher.ACTION_LOCATION_PUBLISHED));

        assertThat(lastToast()).isEqualTo(ctx.getString(R.string.loc_shared_toast));
    }

    @Test
    public void fail_should_notifyUserOfFailureToRetrieveLocation(){
        rcv.register();
        lbm.sendBroadcast(new Intent().setAction(LocationPublisher.ACTION_LOCATION_REQUEST_FAILED));

        assertThat(lastToast()).isEqualTo(ctx.getString(R.string.loc_retrieval_failed_toast));
    }

    @Test
    public void clear_should_notifyUserOfDeletion(){
        rcv.register();
        lbm.sendBroadcast(new Intent().setAction(LocationPublisher.ACTION_LOCATIONS_CLEARED));

        assertThat(lastToast()).isEqualTo(ctx.getString(R.string.loc_clear_toast));
    }

    @Test
    public void forget_should_notifyUserOfForgetting(){
        rcv.register();
        lbm.sendBroadcast(new Intent(Scheduler.ACTION_LOCATIONS_FORGOTTEN)
                .putExtra(Scheduler.ACTION_LOCATIONS_FORGOTTEN, SampleTimes.S17));

        assertThat(lastToast())
            .isEqualTo(ctx.getString(R.string.loc_forget_prefix) + TimeUtils.fullDate(SampleTimes.S17));
    }

}