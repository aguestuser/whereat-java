package org.tlc.whereat.activities;

import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;


import org.robolectric.shadows.ShadowPreferenceManager;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.modules.ui.MenuHandler;
import org.tlc.whereat.services.LocPubManager;
import org.tlc.whereat.modules.pubsub.receivers.MainActivityReceivers;
import org.tlc.whereat.support.ActivityWithMenuHandlersTest;
import org.tlc.whereat.support.FakeMainActivity;

import rx.functions.Func1;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.*;

@RunWith(Enclosed.class)

public class MainActivityTest {


    @RunWith(Enclosed.class)

    public static class LifeCycleMethods{

        @RunWith(RobolectricGradleTestRunner.class)
        @Config(constants = BuildConfig.class, sdk = 21)

        public static class OnCreate {

            MainActivity a;

            @Before
            public void setup(){
                a = createActivity(MainActivity.class);
            }

            @Test
            public void onCreate_should_initializeActivityAndApplicationCorrectly(){

                assertThat(a.mLocPubMgr).isNotNull();
                assertThat(a.mReceivers).isNotNull();
                assertThat(a.mMenu).isNotNull();

                assertThat(a.mPolling).isTrue();
                assertThat(a.mSecAlerted).isFalse();

                assertThat(
                    ShadowPreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application)
                ).isNotNull();

                assertThat(a.findViewById(R.id.go_button)).isNotNull();
            }
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class PostCreate {

        MainActivity a;

        @Before
        public void setup(){
            a = createActivity(MainActivity.class);
            a.mLocPubMgr = mock(LocPubManager.class);
            a.mReceivers = mock(MainActivityReceivers.class);
        }

        @Test
        public void onResume_should_bindToLocPubAndRegisterReceivers(){
            a.onResume();

            verify(a.mLocPubMgr).bind();
            verify(a.mReceivers).register();
        }


        @Test
        public void onPause_should_unbindFromLocPubAndUnregisterReceivers(){
            a.onPause();

            verify(a.mLocPubMgr).unbind();
            verify(a.mReceivers).unregister();
        }

        @Test
        public void onDestroy_should_stopLocPub(){
            a.onDestroy();

            verify(a.mLocPubMgr).stop();
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class EventHandlers {

        @Test
        public void selectingMapFromMenu_should_switchToMapView(){
            MainActivity a = createActivity(MainActivity.class);
            a.onOptionsItemSelected(new RoboMenuItem(R.id.action_map));

            assertThat(nextActivity(a))
                .isEqualTo(MapActivity.class.getName());
        }

        @Test
        public void clickingGoButton_shouldToggleLocationPolling(){
            LocPubManager mockLocPub = mock(LocPubManager.class);
            FakeMainActivity a = createActivity(FakeMainActivity.class).setLocPub(mockLocPub);
            Button go = (Button) a.findViewById(R.id.go_button);

            go.performClick();
            verify(mockLocPub).stopPolling();
            assertThat(lastToast()).isEqualTo("Location sharing off.");

            go.performClick();
            verify(mockLocPub).poll();
            assertThat(lastToast()).isEqualTo("Location sharing on.");

        }

    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class MenuHandlers extends ActivityWithMenuHandlersTest {

        MainActivity a;

        @Before
        public void setup() {
            a = createActivity(MainActivity.class);
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