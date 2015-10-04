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
import org.tlc.whereat.pubsub.Scheduler;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)


public class SchedulerReceiverTest extends ReceiversTest {

    SchedulerReceivers rcv;

    @Before
    public void setup(){
        ctx = mock(Activity.class);
        lbm = spy(LocalBroadcastManager.getInstance(RuntimeEnvironment.application));
        rcv = new SchedulerReceivers(ctx, lbm);
        ifArg = ArgumentCaptor.forClass(IntentFilter.class);
    }

    @Test
    public void register_should_registerBroadcastReceivers(){
        rcv.register();

        verify(lbm).registerReceiver(eq(rcv.mForgetReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(Scheduler.ACTION_LOCATIONS_FORGOTTEN)).isTrue();
    }

    @Test
    public void unregister_should_unRegisterAllReceivers(){
        rcv.unregister();

        verify(lbm).unregisterReceiver(rcv.mForgetReceiver);
    }

    @Test
    public void forgetReceiver_should_toastForgetMessage(){
        rcv.register();
        String msg = "Deleted records older than 09/17 12:00AM";
        Intent i = new Intent()
            .setAction(Scheduler.ACTION_LOCATIONS_FORGOTTEN)
            .putExtra(Scheduler.ACTION_LOCATIONS_FORGOTTEN, msg);

        lbm.sendBroadcast(i);
        assertThat(lastToast()).isEqualTo(msg);
    }

}
