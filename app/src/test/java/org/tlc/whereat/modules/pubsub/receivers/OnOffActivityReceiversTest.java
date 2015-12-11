package org.tlc.whereat.modules.pubsub.receivers;

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
import org.tlc.whereat.activities.OnOffActivity;
import org.tlc.whereat.support.ActivityHelpers;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)


public class OnOffActivityReceiversTest extends ReceiversTest {

    MainActivityReceivers rcv;

    @Before
    public void setup(){
        ctx = ActivityHelpers.createActivity(OnOffActivity.class);
        lbm = spy(LocalBroadcastManager.getInstance(RuntimeEnvironment.application));
        ifArg = ArgumentCaptor.forClass(IntentFilter.class);
        rcv = new MainActivityReceivers(ctx);
    }

    @Test
    public void constructor_should_addChildReceivers(){
        assertThat(rcv.mReceivers).hasSize(2);
        assertThat(rcv.mReceivers.get(0)).isInstanceOf(LocationNotificationReceivers.class);
        assertThat(rcv.mReceivers.get(1)).isInstanceOf(GoogleApiReceivers.class);
    }

    @Test
    public void register_should_registerChildren(){
        addSpies(rcv.mReceivers);
        rcv.register();
        for(Receivers r: rcv.mReceivers) verify(r).register();
    }

    @Test
    public void unregister_should_unregisterChildren(){
        addSpies(rcv.mReceivers);
        rcv.unregister();
        for(Receivers r: rcv.mReceivers) verify(r).unregister();
    }

}
