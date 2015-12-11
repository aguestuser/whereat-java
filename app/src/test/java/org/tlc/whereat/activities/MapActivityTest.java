package org.tlc.whereat.activities;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
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

                assertThat(a.mLocPubMgr).isNotNull();
                assertThat(a.mReceivers).isNotNull();
                assertThat(a.mLocDao).isNotNull();
                assertThat(a.mMapper).isNotNull();
                assertThat(a.mMenu).isNotNull();

                assertThat(a.mRunning).isFalse();
                assertThat(a.mLocPubMgr.isRunning()).isFalse();
                assertThat(a.mLocDao.isConnected()).isFalse();
                assertThat(a.mMapper.hasInitialized()).isFalse();

                assertThat(shadowOf(a).getContentView().getId()).isEqualTo(R.id.map_activity);
                assertThat(a.findViewById(R.id.refresh_map_button)).isNotNull();
            }
        }

        @RunWith(RobolectricGradleTestRunner.class)
        @Config(constants = BuildConfig.class, sdk = 21)

        public static class PostCreate {

            MapActivity a;

            @Before
            public void setup(){
                a = createActivity(MapActivity.class);
                a.mLocPubMgr = mock(LocPubManager.class);
                a.mReceivers = mock(MapActivityReceivers.class);
                a.mLocDao = mock(LocationDao.class);
                a.mMapper = mock(Mapper.class);

                doReturn(a.mMapper).when(a.mMapper).initialize(anyListOf(UserLocation.class));
                doNothing().when(a.mMapper).refresh(anyListOf(UserLocation.class));
            }

            @Test
            public void onResume_should_bindToServiceAndRegisterReceivers(){
                a.onResume();

                verify(a.mLocPubMgr).bind();
                verify(a.mReceivers).register();
            }

            @Test
            public void onResume_should_runDatabaseMapperandLocPubMgr_OnFirstView(){
                assertThat(a.mRunning).isFalse();
                a.onResume();

                verify(a.mLocPubMgr, times(1)).start();
                verify(a.mLocDao, times(1)).connect();
                verify(a.mMapper, times(1)).initialize(anyListOf(UserLocation.class));

                assertThat(a.mRunning).isTrue();
            }

            @Test
            public void onResume_should_refreshDatabaseAndNothingElse_OnSubsequentViews(){
                a.mRunning = true;
                a.onResume();

                verify(a.mLocPubMgr, times(0)).start();
                verify(a.mLocDao, times(0)).connect();
                verify(a.mMapper, times(0)).initialize(anyListOf(UserLocation.class));

                verify(a.mMapper, times(1)).refresh(anyListOf(UserLocation.class));
            }

            @Test
            public void onPause_should_unbindServicesAndUnregisterReceivers(){
                a.onPause();

                verify(a.mLocPubMgr).unbind();
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
            a.mLocPubMgr = mock(LocPubManager.class);
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
            a.mLocPubMgr = mock(LocPubManager.class);
            a.mReceivers = mock(MapActivityReceivers.class);
            a.mLocDao = mock(LocationDao.class);
            a.mMapper = mock(Mapper.class);

            doReturn(a.mMapper).when(a.mMapper).initialize(anyListOf(UserLocation.class));
        }

        @Test
        public void clickingRefreshButton_should_clearMap_thenClearDao_thenPingLocPubMgr(){

            a.findViewById(R.id.refresh_map_button).performClick();

            InOrder inOrder = inOrder(a.mMapper, a.mLocDao, a.mLocPubMgr);
            inOrder.verify(a.mLocDao).clear();
            inOrder.verify(a.mMapper).clear();
            inOrder.verify(a.mLocPubMgr).ping();
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