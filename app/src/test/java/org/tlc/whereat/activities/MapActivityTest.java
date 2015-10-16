package org.tlc.whereat.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowLog;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.modules.db.LocationDao;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.modules.map.Mapper;
import org.tlc.whereat.modules.ui.MenuHandler;
import org.tlc.whereat.services.LocPubManager;
import org.tlc.whereat.modules.pubsub.receivers.MapActivityReceivers;
import org.tlc.whereat.support.ActivityWithMenuHandlersTest;

import rx.functions.Func1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
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
                assertThat(a.mMenu).isNotNull();

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
        public void clickingClearButton_should_clearMap(){
            a.findViewById(R.id.clear_map_button).performClick();

            verify(a.mMapper).clear();
            verify(a.mLocPub).clear();
            verify(a.mLocDao).clear();
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class MenuHandlers extends ActivityWithMenuHandlersTest {

        MapActivity a;

        @Before
        public void setup() {
            a = createActivity(MapActivity.class);
            a.mMenu = mock(MenuHandler.class);
            menu = new RoboMenu(a);
        }

        @Test
        public void menu_should_delegateToMenuHandler(){
            a.onCreateOptionsMenu(menu);
            verify(a.mMenu).create(menu);
        }

        @Test
        public void selectingActivityFromMenu_should_delegateToMenuHandler() {

            a.onOptionsItemSelected(main);
            verify(a.mMenu).select(eq(main), any(Func1.class));

            a.onOptionsItemSelected(map);
            verify(a.mMenu).select(eq(map), any(Func1.class));

            a.onOptionsItemSelected(prefs);
            verify(a.mMenu).select(eq(prefs), any(Func1.class));
        }
    }
}