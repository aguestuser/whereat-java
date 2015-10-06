package org.tlc.whereat.receivers;

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
import org.tlc.whereat.support.ActivityHelpers;
import org.tlc.whereat.support.FakeMapActivity;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class MapActivityReceiversTest extends ReceiversTest{

    MapActivityReceivers rcv;

    @Before
    public void setup(){
        ctx = ActivityHelpers.createActivity(FakeMapActivity.class);
        lbm = spy(LocalBroadcastManager.getInstance(RuntimeEnvironment.application));
        ifArg = ArgumentCaptor.forClass(IntentFilter.class);
        rcv = new MapActivityReceivers(ctx);
    }

    @Test
    public void constructor_should_addChildReceivers(){
        assertThat(rcv.mReceivers.size()).isEqualTo(3);
        assertThat(rcv.mReceivers.get(0).getClass()).isEqualTo(LocationNotificationReceivers.class);
        assertThat(rcv.mReceivers.get(1).getClass()).isEqualTo(LocationMappingReceivers.class);
    }

    @Test
    public void register_should_registerChildren(){
        addSpies(rcv.mReceivers);
        rcv.register();
        for(Receiver r: rcv.mReceivers) verify(r).register();
    }

    @Test
    public void unregister_should_unregisterChildren(){
        addSpies(rcv.mReceivers);
        rcv.unregister();
        for(Receiver r: rcv.mReceivers) verify(r).unregister();
    }

}
