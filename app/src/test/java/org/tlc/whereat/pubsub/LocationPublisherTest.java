package org.tlc.whereat.pubsub;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.location.Location;
import android.os.IBinder;

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
import static org.tlc.whereat.support.ReflectionHelpers.*;

import org.tlc.whereat.api.WhereatApiClient;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.model.UserLocationTimestamped;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.*;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */


@RunWith(Enclosed.class)

public class LocationPublisherTest {

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class)

    public static class LifeCycleMethods {

        //#onStartCommand()

        @Test
        private void onStartCommand_whenPlayServicesEnabled_initalizesLocPub(){

        }

        @Test
        private void onStartCommand_whenPlayServicesDisabled_broadcastsPlayServicesDisabled() {

        }

        //#initialize()

        @Test
        private void initialize_should_initializePrivateFields(){

        }

        //#connect()
        @Test
        private void connect_whenAlreadyConnected_doesNothing(){

        }

        @Test
        private void connect_whenNotConnected_connectsGoogleApiClient(){

        }

        //#onBind()

        @Test
        private void onBind_returnsLocationServiceBinderWith_getServiceThatReturnsThis(){

        }

        //#onDestroy()

        @Test
        private void onDestory_cleansUpResources(){

        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class)

    public static class PublicMethods {



        @Before
        public void setup(){
//            LocationPublisher.LocationServiceBinder mockBinder = mock(LocationPublisher.LocationServiceBinder.class);
//            when(mockBinder.getService()).thenReturn(mock(LocationPublisher.class));
            shadowOf(RuntimeEnvironment.application).setComponentNameAndServiceForBindService(
                new ComponentName("org.tlc.whereat.pubsub", "LocationPublisher"), mock(IBinder.class));


        }

        @After
        public void teardown(){

        }

        //#ping()

        @Test
        public void ping_whenLocationNull_broadcastsFailedLocationRequest(){
            // mock LocationServices.FusedLocationApi.getLastLocation for call to lastApiLocation()
            // test broadcasts (maybe just mock Dispatcher, but in Dispatcher, then:)
            // https://github.com/upsight/playhaven-robolectric/blob/master/src/test/java/com/xtremelabs/robolectric/shadows/LocalBroadcastManagerTest.java
            // https://searchcode.com/codesearch/view/25270441/

        }

        @Test
        public void ping_whenLocationExists_relaysLocation(){
            Location s17 = s17AndroidLocationMock();
            LocationPublisher lp = spy(LocationPublisher.class);
            doReturn(s17).when(lp).lastApiLocation();
            doNothing().when(lp).relay(s17);

            lp.ping();
            verify(lp).relay(s17);
        }

        //#ping helpers

        @Test
        public void relay_broadcastsPostsAndSavesLocThenSetsLastPing(){
            Location s17 = s17AndroidLocationMock();
            UserLocation s17ul = s17UserLocationStub();
            LocationDao mockDao = mock(LocationDao.class);
            doReturn(1L).when(mockDao).save(s17ul);

            LocationPublisher lp = spy(LocationPublisher.class);
            Field lastPing = publicify(LocationPublisher.class, "mLastPing");
            try { lastPing.set(lp, -1L); } catch (Exception e) { return; }
            Field userId = publicify(LocationPublisher.class, "mUserId");
            try { userId.set(lp, S17_UUID); } catch (Exception e) { return; }
            Field dao = publicify(LocationPublisher.class, "mLocDao");
            try { dao.set(lp, mockDao); } catch (Exception e) { return; }
            doNothing().when(lp).update(s17ul);

            lp.relay(s17);

            verify(lp).broadcastLocationPublished(s17ul);
            verify(lp).update(s17ul);
            verify(mockDao).save(s17ul);
            try { assertThat(lastPing.getLong(lp)).isEqualTo(s17ul.getTime()); } catch (Exception e) { return; }
        }

        // #update

        @Test
        public void update_returnsAnObservableThatResultsInManyBroadcasts(){
            List<UserLocation> locs = Arrays.asList(s17UserLocationStub(), n17UserLocationStub());
            UserLocation s17ul = s17UserLocationStub();
            UserLocationTimestamped s17ult = s17ul.withTimestamp(-1L);
            WhereatApiClient mockClient = mock(WhereatApiClient.class);
            doReturn(Observable.from(locs)).when(mockClient).update(s17ult);

            LocationPublisher lp = spy(LocationPublisher.class);
            Field client = publicify(LocationPublisher.class, "mWhereatClient");
            try { client.set(LocationPublisher.class, mockClient); } catch (Exception e) { return; }
            Field lastPing = publicify(LocationPublisher.class, "mLastPing");
            try { lastPing.set(lp, -1L); } catch (Exception e) { return; }

            lp.update(s17ul);
            try { Thread.sleep(30L); } catch (Exception e) { return ; }

            verify(lp, times(1)).broadcastLocationPublished(s17UserLocationStub());
            verify(lp, times(1)).broadcastLocationPublished(n17UserLocationStub());
        }

        // #broadcastLocationPublished

        @Test

            public void broadcastLocationPublished_broadcastsIntentContainingLocation(){

        }

        //#poll

        @Test
        public void poll_turnsOnPolling(){
            // calls LocationServices.FusedLocationApi.requestLocationUpdates
            // sets mPolling true
        }

        //#stopPolling
        public void stopPolling_stopsPolling(){
            // calls LocationServices.FusedLocationApi.removeLocationUpdates
            // sets mPolling false
        }

        //#clear
        public void clear_clearsUserFromServerAllLocsFromPhone(){
            // requires mock DAO, API (dao has to mock getting (mUserId)
            // calls clien.remove()
            // calls dao.clear()
        }
    }






}