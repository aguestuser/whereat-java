package org.tlc.whereat.activities;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowLog;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.modules.Mapper;
import org.tlc.whereat.pubsub.LocPubManager;
import org.tlc.whereat.receivers.MapActivityReceivers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.tlc.whereat.support.ActivityHelpers.createActivity;
import static org.tlc.whereat.support.ActivityHelpers.nextActivity;
import static org.tlc.whereat.support.LocationHelpers.s17UserLocationStub;

@RunWith(Enclosed.class)

public class MapActivityTest {

    static { ShadowLog.stream = System.out; }
    static UserLocation s17 = s17UserLocationStub();

    @RunWith(Enclosed.class)

    public static class LifeCycleMethods {

        @RunWith(RobolectricGradleTestRunner.class)
        @Config(constants = BuildConfig.class, sdk = 21)

        public static class OnCreate {

            @Test
            public void onCreate_should_initializeActivityCorrectly(){

                MapActivity a = createActivity(MapActivity.class);

                assertThat(a.mLocPub).isNotNull();
                assertThat(a.mReceivers).isNotNull();
                assertThat(a.mLocDao).isNotNull();
                assertThat(a.mMapper).isNotNull();

                assertThat(shadowOf(a).getContentView().getId()).isEqualTo(R.id.map_activity);
                assertThat(a.findViewById(R.id.clear_map_button)).isNotNull();
            }
        }

        @RunWith(RobolectricGradleTestRunner.class)
        @Config(constants = BuildConfig.class, sdk = 21)

        public static class PostCreate {

            MapActivity a;

            @Before
            public void setup(){
                a = createActivity(MapActivity.class);
                a.mLocPub = mock(LocPubManager.class);
                a.mReceivers = mock(MapActivityReceivers.class);
                a.mLocDao = mock(LocationDao.class);
                a.mMapper = mock(Mapper.class);

                doReturn(a.mMapper).when(a.mMapper).initialize(anyListOf(UserLocation.class));
            }

            @Test
            public void onResume_should_bindToServiceAndRegisterReceivers(){
                a.onResume();

                verify(a.mLocPub).bind();
                verify(a.mReceivers).register();
            }

            @Test
            public void onResume_should_connectToDatabaseOnceAndOnlyOnce(){
                doReturn(false).when(a.mLocDao).isConnected();
                a.onResume();
                verify(a.mLocDao, times(1)).connect();

                doReturn(true).when(a.mLocDao).isConnected();
                a.onResume();
                verify(a.mLocDao, times(1)).connect();

            }

            @Test
            public void onResume_should_initializeMapperOnceAndOnlyOnce(){
                doReturn(false).when(a.mMapper).hasInitialized();
                a.onResume();
                verify(a.mMapper, times(1)).initialize(anyListOf(UserLocation.class));

                doReturn(true).when(a.mMapper).hasInitialized();
                a.onResume();
                verify(a.mMapper, times(1)).initialize(anyListOf(UserLocation.class));
            }

            @Test
            public void onResume_should_refreshMapIfNecessary(){
                doReturn(false).when(a.mMapper).hasPinged();
                a.onResume();
                verify(a.mMapper, never()).refresh(anyListOf(UserLocation.class));

                doReturn(true).when(a.mMapper).hasPinged();
                a.onResume();
                verify(a.mMapper, times(1)).refresh(anyListOf(UserLocation.class));

            }

            @Test
            public void onPause_should_unbindServicesAndUnregisterReceivers(){
                a.onPause();

                verify(a.mLocPub).unbind();
                verify(a.mReceivers).unregister();
            }

            @Test
            public void onDestroy_should_cleanUpResources(){
                a.onDestroy();

                verify(a.mMapper).clear();
                verify(a.mLocDao).disconnect();
            }
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class PublicMethods {

        MapActivity a;

        @Before
        public void setup() {
            a = createActivity(MapActivity.class);
            a.mLocPub = mock(LocPubManager.class);
            a.mReceivers = mock(MapActivityReceivers.class);
            a.mLocDao = mock(LocationDao.class);
            a.mMapper = mock(Mapper.class);

            doReturn(a.mMapper).when(a.mMapper).initialize(anyListOf(UserLocation.class));
        }

        @Test
        public void map_should_delegateToChildren(){
            a.map(s17);
            verify(a.mMapper).map(s17);
        }

        @Test
        public void clear_should_delegateToChildren(){
            a.clear();

            verify(a.mMapper).clear();
            verify(a.mLocPub).clear();
            verify(a.mLocDao).clear();
        }

        @Test
        public void forgetSince_should_delegateToChildren(){
            long t = s17.getTime();
            a.forgetSince(t);

            verify(a.mMapper).forgetSince(t);
            verify(a.mLocDao).forgetSince(t);
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class EventHandlers {

        MapActivity a;

        @Before
        public void setup(){
            a = createActivity(MapActivity.class);
            a.mLocPub = mock(LocPubManager.class);
            a.mReceivers = mock(MapActivityReceivers.class);
            a.mLocDao = mock(LocationDao.class);
            a.mMapper = mock(Mapper.class);

            doReturn(a.mMapper).when(a.mMapper).initialize(anyListOf(UserLocation.class));
        }

        @Test
        public void menu_should_displayCorrectContents(){
            RoboMenu menu = new RoboMenu(a);
            a.onCreateOptionsMenu(menu);

            assertThat(menu.getItem(0).getItemId()).isEqualTo(R.id.action_main);
            assertThat(menu.getItem(0).getTitle()).isEqualTo(a.getString(R.string.main_activity_title));

            assertThat(menu.getItem(1).getItemId()).isEqualTo(R.id.action_map);
            assertThat(menu.getItem(1).getTitle()).isEqualTo(a.getString(R.string.map_activity_title));
        }

        @Test
        public void selectingHomeFromMenu_should_startMainActivity() {
            a.onOptionsItemSelected(new RoboMenuItem(R.id.action_main));
            assertThat(nextActivity(a)).isEqualTo(MainActivity.class.getName());

        }

        @Test
        public void clickingClearButton_should_clearMap(){
            a.findViewById(R.id.clear_map_button).performClick();

            verify(a.mMapper).clear();
            verify(a.mLocPub).clear();
            verify(a.mLocDao).clear();
        }
    }
}