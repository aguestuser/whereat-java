package org.tlc.whereat.services;

import android.content.ComponentName;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.robolectric.Shadows.shadowOf;
import org.tlc.whereat.BuildConfig;

import static org.mockito.Mockito.*;
import static org.tlc.whereat.support.LocationHelpers.*;

import org.tlc.whereat.modules.api.WhereatApiClient;
import org.tlc.whereat.modules.schedule.Scheduler;
import org.tlc.whereat.modules.pubsub.broadcasters.LocPubBroadcasters;
import org.tlc.whereat.modules.db.LocationDao;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.model.UserLocationTimestamped;
import org.tlc.whereat.support.FakeLocationPublisher;

import rx.observers.TestSubscriber;

import java.util.Arrays;
import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Assertions.*;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */


@RunWith(Enclosed.class)

public class LocationPublisherTest {

    static Location s17raw = s17AndroidLocationMock();
    static UserLocation s17ul = s17UserLocationStub();
    static UserLocation s17 = s17UserLocationStub();
    static UserLocation n17 = n17UserLocationStub();
    static List<UserLocation> locs = Arrays.asList(s17, n17);


    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class LifeCycleMethods {

        //#onStartCommand()

        @Test
        public void initialize_should_initializePrivateFields() {

            LocationPublisher lp = spy(new FakeLocationPublisher());
            when(lp.getRandomId()).thenReturn("123");
            doNothing().when(lp).run();

            lp.initialize();

            assertThat(lp.mGoogClient).isNotNull();
            assertThat(lp.mLocReq).isNotNull();
            assertThat(lp.mWhereatClient).isNotNull();
            assertThat(lp.mDao).isNotNull();
            assertThat(lp.mScheduler).isNotNull();
            assertThat(lp.mLocSub).isNotNull();
            assertThat(lp.mBroadcast).isNotNull();

            assertThat(lp.mPolling).isFalse();
            assertThat(lp.mUserId).isEqualTo("123");
        }

        @Test
        public void run_should_connectToApisAndScheduleRunnables() {
            LocationPublisher lp = spy(LocationPublisher.class);
            lp.mGoogClient = mock(GoogleApiClient.class);
            lp.mDao = mock(LocationDao.class);
            lp.mScheduler = mock(Scheduler.class);
            doReturn(false).when(lp.mGoogClient).isConnected();

            lp.run();

            verify(lp.mGoogClient).connect();
            verify(lp.mDao).connect();
            verify(lp.mScheduler).forget(LocationPublisher.FORGET_INTERVAL, LocationPublisher.TIME_TO_LIVE);
        }

        @Test
        public void onBind_returnsLocationServiceBinderWith_getServiceThatReturnsThis() {

        }

        @Test
        public void onDestroy_cleansUpResources() {

        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class PublicMethods {

        LocationPublisher lp;

        @Before
        public void setup() {
            lp = spy(LocationPublisher.class);
            lp.mGoogClient = mock(GoogleApiClient.class);
            lp.mLocProvider = mock(FusedLocationProviderApi.class);
            shadowOf(RuntimeEnvironment.application)
                .setComponentNameAndServiceForBindService(
                    new ComponentName("org.tlc.whereat.modules.pubsub", "LocationPublisher"),
                    mock(IBinder.class));
        }

        @After
        public void teardown() {

        }

        @Test
        public void ping_whenLocationNull_broadcastsFailedLocationRequest() {

        }

        @Test
        public void ping_whenLocationExists_relaysLocation() {
            doReturn(s17raw).when(lp.mLocProvider).getLastLocation(lp.mGoogClient);
            doNothing().when(lp).relay(s17raw);

            lp.ping();

            verify(lp).relay(s17raw);
        }

        //#ping helpers

        @Test
        public void relay_broadcastsPostsAndSavesLocThenSetsLastPing() throws IllegalAccessException {
            lp.mLastPing = -1L;
            lp.mUserId = S17_UUID;
            lp.mDao = mock(LocationDao.class);
            lp.mBroadcast = mock(LocPubBroadcasters.class);
            doReturn(1L).when(lp.mDao).save(s17ul);
            doNothing().when(lp).update(s17ul);

            lp.relay(s17raw);

            verify(lp.mBroadcast).pub();
            verify(lp).update(s17ul);
            verify(lp.mDao).save(s17ul);
            assertThat(lp.mLastPing).isEqualTo(s17ul.getTime());
        }

        @Test
        public void update_relaysAnObservableApiResponseToASubscriber() {
            lp.mWhereatClient = mock(WhereatApiClient.class);
            doReturn(Observable.just(locs)).when(lp.mWhereatClient).update(any(UserLocationTimestamped.class));
            TestSubscriber<UserLocation> sub = new TestSubscriber<>();
            lp.mLocSub = sub::onNext;

            lp.update(s17);

            sub.assertNoErrors();
            sub.assertReceivedOnNext(locs);
        }


        @Test
        public void broadcastLocationPublished_broadcastsIntentContainingLocation() {

        }

        //#poll

        @Test
        public void poll_turnsOnPolling() {
            // calls LocationServices.FusedLocationApi.requestLocationUpdates
            // sets mPolling true
        }

        //#stopPolling
        public void stopPolling_stopsPolling() {
            // calls LocationServices.FusedLocationApi.removeLocationUpdates
            // sets mPolling false
        }

        //#clear
        public void clear_clearsUserFromServerAllLocsFromPhone() {
            // requires mock DAO, API (dao has to mock getting (mUserId)
            // calls clien.remove()
            // calls dao.clear()
        }
    }

}
