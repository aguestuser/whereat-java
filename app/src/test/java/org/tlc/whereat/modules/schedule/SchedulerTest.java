package org.tlc.whereat.modules.schedule;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import static org.robolectric.Shadows.shadowOf;

import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.services.LocationPublisher;
import org.tlc.whereat.support.SampleTimes;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;


@RunWith(Enclosed.class)

public class SchedulerTest {

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class SchedulingForget {

        LocationPublisher locPub;
        LocalBroadcastManager lbm;
        Scheduler sked;
        ArgumentCaptor<Intent> intentArg;
        ArgumentCaptor<Long> longArg;

        long millis = 50;
        long now = SampleTimes.S17;
        long ttl = 1;
        long offset = 5;

        @Before
        public void setup(){
            locPub = mock(LocationPublisher.class);
            lbm = mock(LocalBroadcastManager.class);
            sked = Scheduler.getInstance(locPub, lbm);

            intentArg = ArgumentCaptor.forClass(Intent.class);
            longArg = ArgumentCaptor.forClass(Long.class);
        }

        @Test
        public void forget_should_forgetRecordsAtSpecifiedInterval() throws InterruptedException {

            sked.forget(millis, ttl, now);
            org.robolectric.Robolectric.getForegroundThreadScheduler().advanceBy(2 * millis + offset);

            verify(lbm, times(2)).sendBroadcast(intentArg.capture());
            assertThat(intentArg.getValue().getAction())
                .isEqualTo(Scheduler.ACTION_LOCATIONS_FORGOTTEN);
            assertThat(intentArg.getValue().getExtras().getLong(Scheduler.ACTION_LOCATIONS_FORGOTTEN))
                .isEqualTo(now - ttl);
        }

        @Test
        public void forget_should_incrementExpirationThresholdEveryCall(){

            sked.forget(millis, ttl);
            InOrder inOrder = inOrder(sked.mLbm);

            org.robolectric.Robolectric.getForegroundThreadScheduler().advanceBy(millis + offset);

            inOrder.verify(lbm).sendBroadcast(intentArg.capture());
            long time1 = intentArg.getValue().getExtras().getLong(Scheduler.ACTION_LOCATIONS_FORGOTTEN);

            org.robolectric.Robolectric.getForegroundThreadScheduler().advanceBy(millis + offset);
            inOrder.verify(lbm).sendBroadcast(intentArg.capture());
            long time2 = intentArg.getValue().getExtras().getLong(Scheduler.ACTION_LOCATIONS_FORGOTTEN);

            org.robolectric.Robolectric.getForegroundThreadScheduler().advanceBy(millis + offset);
            inOrder.verify(lbm).sendBroadcast(intentArg.capture());
            long time3 = intentArg.getValue().getExtras().getLong(Scheduler.ACTION_LOCATIONS_FORGOTTEN);

            assertThat(time1 < time2);
            assertThat(time2 < time3);
            assertThat(time2 - time1 == millis);
            assertThat(time3 - time2 == millis);
            assertThat(time3 - time1 == 2*millis);
        }

        @Test
        public void cancelForget_should_removeForgetCallbacksFromHandler(){

            Handler handler = mock(Handler.class);
            Runnable runnable = mock(Runnable.class);

            sked.mForgetHandler = handler;
            sked.mForgetRunnable = runnable;

            sked.cancelForget();
            verify(handler, times(1)).removeCallbacks(runnable);
        }
    }
}