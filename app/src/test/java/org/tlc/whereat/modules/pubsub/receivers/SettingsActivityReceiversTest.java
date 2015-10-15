package org.tlc.whereat.modules.pubsub.receivers;

import android.support.v4.content.LocalBroadcastManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.activities.SettingsActivity;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.createActivity;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class SettingsActivityReceiversTest extends ReceiversTest {

    SettingsActivityReceivers rcv;

    @Before
    public void setup() {
        ctx = createActivity(SettingsActivity.class);
        lbm = spy(LocalBroadcastManager.getInstance(RuntimeEnvironment.application));
        rcv = new SettingsActivityReceivers(ctx);
    }

    @Test
    public void constructor_should_addChildReceivers(){
        assertThat(rcv.mReceivers).hasSize(1);
        assertThat(rcv.mReceivers.get(0)).isInstanceOf(LocationNotificationReceivers.class);
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