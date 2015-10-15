package org.tlc.whereat.services;

import android.content.ComponentName;
import android.content.SharedPreferences;
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
import org.robolectric.shadows.ShadowPreferenceManager;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.model.UserLocationTimestamped;
import org.tlc.whereat.modules.api.WhereatApiClient;
import org.tlc.whereat.modules.db.LocationDao;
import org.tlc.whereat.modules.pubsub.broadcasters.LocPubBroadcasters;
import org.tlc.whereat.modules.schedule.Scheduler;
import org.tlc.whereat.support.FakeLocationPublisher;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;
import static org.tlc.whereat.support.LocationHelpers.*;


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

            lp.initialize();

            assertThat(lp.mGoogClient).isNotNull();

            assertThat(lp.mPrefs).isNotNull();
            assertThat(lp.mPrefListener).isNotNull();
            assertThat(lp.mPollInterval).isEqualTo(30000);
            assertThat(lp.mTtl).isEqualTo(3600000L);
            assertThat(lp.mLocReq).isNotNull();
            assertThat(lp.mLocReq.getInterval()).isEqualTo(30000L);

            assertThat(lp.mWhereatClient).isNotNull();
            assertThat(lp.mDao).isNotNull();
            assertThat(lp.mScheduler).isNotNull();
            assertThat(lp.mBroadcast).isNotNull();
            assertThat(lp.mLocProvider).isNotNull();

            assertThat(lp.mLocSub).isEqualToComparingFieldByField(lp.mBroadcast::map);

            assertThat(lp.mUserId).isEqualTo("123");
            assertThat(lp.mPolling).isFalse();
        }

        @Test
        public void run_should_connectToApisScheduleRunnablesAndListenToPrefs() {
            LocationPublisher lp = spy(LocationPublisher.class);
            lp.mGoogClient = mock(GoogleApiClient.class);
            lp.mDao = mock(LocationDao.class);
            lp.mPrefs = mock(SharedPreferences.class);
            lp.mScheduler = mock(Scheduler.class);
            doReturn(false).when(lp.mGoogClient).isConnected();

            lp.run();

            verify(lp.mGoogClient).connect();
            verify(lp.mDao).connect();
            verify(lp.mPrefs).registerOnSharedPreferenceChangeListener(lp.mPrefListener);
            verify(lp.mScheduler).forget(LocationPublisher.sForgetInterval, lp.mTtl);
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

    public static class PreferenceListeners {

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

            lp.mPrefs = ShadowPreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
            lp.mPrefListener = lp.buildPrefListener();
            lp.mPrefs.registerOnSharedPreferenceChangeListener(lp.mPrefListener);
        }

        @Test
        public void onSharedPreferenceChanged_should_updatePollIntervalAndRestartPolling(){

            setPollInterval(lp.getString(R.string.pref_loc_share_interval_value_0));

            verify(lp, times(1)).resetPollInterval();
            assertThat(lp.mPollInterval).isEqualTo(5000);
            assertThat(lp.mLocReq.getInterval()).isEqualTo(5000);
            verify(lp, times(1)).restartPolling();

            setPollInterval(lp.getString(R.string.pref_loc_share_interval_value_1));

            verify(lp, times(2)).resetPollInterval();
            assertThat(lp.mPollInterval).isEqualTo(15000);
            assertThat(lp.mLocReq.getInterval()).isEqualTo(15000);
            verify(lp, times(2)).restartPolling();

            setPollInterval(lp.getString(R.string.pref_loc_share_interval_value_2));

            verify(lp, times(3)).resetPollInterval();
            assertThat(lp.mPollInterval).isEqualTo(30000);
            assertThat(lp.mLocReq.getInterval()).isEqualTo(30000);
            verify(lp, times(3)).restartPolling();

            setPollInterval(lp.getString(R.string.pref_loc_share_interval_value_3));

            verify(lp, times(4)).resetPollInterval();
            assertThat(lp.mPollInterval).isEqualTo(60000);
            assertThat(lp.mLocReq.getInterval()).isEqualTo(60000);
            verify(lp, times(4)).restartPolling();

            setPollInterval(lp.getString(R.string.pref_loc_share_interval_value_4));

            verify(lp, times(5)).resetPollInterval();
            assertThat(lp.mPollInterval).isEqualTo(300000);
            assertThat(lp.mLocReq.getInterval()).isEqualTo(300000);
            verify(lp, times(5)).restartPolling();
        }

        @Test
        public void restartPolling_shouldOnlyRestartIfPollingInProgress(){
            lp.mPolling = true;
            lp.restartPolling();

            assertThat(lp.mPolling).isTrue();
            verify(lp, times(1)).stopPolling();
            verify(lp, times(1)).poll();

            lp.mPolling = false;
            lp.restartPolling();

            assertThat(lp.mPolling).isFalse();
            verify(lp, times(1)).stopPolling();
            verify(lp, times(1)).poll();
        }

        protected void setPollInterval(String value){
            lp.mPrefs.edit().putString("pref_loc_share_interval_key", value).apply();
        }

        @Test
        public void onSharedPreferenceChanged_should_updateTtlAndRescheduleForget(){
            lp.mScheduler = mock(Scheduler.class);
            long fi = LocationPublisher.sForgetInterval;

            setTtl(lp.getString(R.string.pref_loc_ttl_value_0));

            verify(lp, times(1)).resetTtl();
            assertThat(lp.mTtl).isEqualTo(1800000L);
            verify(lp.mScheduler).forget(fi, 1800000L);

            setTtl(lp.getString(R.string.pref_loc_ttl_value_1));

            verify(lp, times(2)).resetTtl();
            assertThat(lp.mTtl).isEqualTo(3600000L);
            verify(lp.mScheduler).forget(fi, 3600000L);

            setTtl(lp.getString(R.string.pref_loc_ttl_value_2));

            verify(lp, times(3)).resetTtl();
            assertThat(lp.mTtl).isEqualTo(7200000L);
            verify(lp.mScheduler).forget(fi, 7200000L);
        }

        protected void setTtl(String value){
            lp.mPrefs.edit().putString("pref_loc_ttl_key", value).apply();
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
