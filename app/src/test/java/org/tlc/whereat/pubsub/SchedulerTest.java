package org.tlc.whereat.pubsub;

import android.content.Context;
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
import org.tlc.whereat.R;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.support.FakeScheduler;
import org.tlc.whereat.support.SampleTimes;
import org.tlc.whereat.util.TimeUtils;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.IntentHelper.*;


@RunWith(Enclosed.class)

public class SchedulerTest {

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class SchedulingForget {

        LocationPublisher locPub;
        LocalBroadcastManager lbm;
        LocationDao dao;
        Scheduler scheduler;
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
            dao = mock(LocationDao.class);
            scheduler = new Scheduler(locPub, lbm);
            intentArg = ArgumentCaptor.forClass(Intent.class);
            longArg = ArgumentCaptor.forClass(Long.class);
        }

        @Test
        public void forget_should_forgetRecordsAtSpecifiedInterval() throws InterruptedException {

            Intent expectedIntent = new Intent()
                .setAction(Scheduler.ACTION_LOCATIONS_FORGOTTEN)
                .putExtra(
                    Scheduler.ACTION_LOCATIONS_FORGOTTEN,
                    scheduler.mCtx.getString(R.string.loc_forget_prefix) + TimeUtils.fullDate(now - ttl));

            scheduler.forget(dao, millis, ttl, now);
            org.robolectric.Robolectric.getForegroundThreadScheduler().advanceBy(2 * millis + offset);

            verify(dao, times(2)).deleteOlderThan(now - ttl);
            verify(lbm, times(2)).sendBroadcast(intentArg.capture());
            assertThat(sameAction(intentArg.getValue(), expectedIntent)).isTrue();
        }

        @Test
        public void forget_should_incrementExpirationThresholdEveryCall(){

            scheduler.forget(dao, millis, ttl);
            InOrder inOrder = inOrder(dao);

            org.robolectric.Robolectric.getForegroundThreadScheduler().advanceBy(millis + offset);
            inOrder.verify(dao).deleteOlderThan(longArg.capture());
            long time1 = longArg.getValue();

            org.robolectric.Robolectric.getForegroundThreadScheduler().advanceBy(millis + offset);
            inOrder.verify(dao).deleteOlderThan(longArg.capture());
            long time2 = longArg.getValue();

            org.robolectric.Robolectric.getForegroundThreadScheduler().advanceBy(millis + offset);
            inOrder.verify(dao).deleteOlderThan(longArg.capture());
            long time3 = longArg.getValue();

            assertThat(time1 < time2);
            assertThat(time2 < time3);
            assertThat(time2 - time1 == millis);
            assertThat(time3 - time2 == millis);
            assertThat(time3 - time1 == 2*millis);
        }
    }

    @Test
    public void cancelForget_should_removeForgetCallbacksFromHandler(){

        Handler handler = mock(Handler.class);
        Runnable runnable = mock(Runnable.class);

        FakeScheduler scheduler = new FakeScheduler(mock(Context.class), mock(LocalBroadcastManager.class))
            .setForgetHandler(handler)
            .setForgetRunnable(runnable);

        scheduler.cancelForget();
        verify(handler, times(1)).removeCallbacks(runnable);
    }
}