package org.tlc.whereat.activities;

import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;


import org.robolectric.shadows.ShadowPreferenceManager;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.fragments.SecurityAlertFragment;
import org.tlc.whereat.modules.ui.MenuHandler;
import org.tlc.whereat.services.LocPubManager;
import org.tlc.whereat.modules.pubsub.receivers.MainActivityReceivers;
import org.tlc.whereat.services.LocationPublisher;
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

            @Test
            public void onCreate_should_initializeActivityAndApplicationCorrectly(){

                MainActivity a = createActivity(MainActivity.class);

                assertThat(a.mLocPub).isNotNull();
                assertThat(a.mReceivers).isNotNull();
                assertThat(a.mSecAlert).isNotNull();
                assertThat(a.mMenu).isNotNull();

                assertThat(a.mPolling).isFalse();
                assertThat(a.mSecAlerted).isFalse();

                assertThat(
                    ShadowPreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application)).
                    isNotNull();

                assertThat(a.findViewById(R.id.go_button)).isNotNull();
            }
        }


    }


    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class PostCreate {

        @Test
        public void creatingActivity_should_startLocationPublisher(){
            MainActivity a = createActivity(FakeMainActivity.class);

            assertThat(nextService(a))
                .isEqualTo(LocationPublisher.class.getName());
        }

        @Test
        public void resumingActivity_should_bindToLocPubAndRegisterToLocSub(){
            LocPubManager mockLocPub = mock(LocPubManager.class);
            MainActivityReceivers mockLocSub = mock(MainActivityReceivers.class);
            FakeMainActivity a = createActivity(FakeMainActivity.class)
                .setLocPub(mockLocPub)
                .setLocSub(mockLocSub);

            a.onResume();

            verify(mockLocPub, times(1)).bind();
            verify(mockLocSub, times(1)).register();
        }


        @Test
        public void pausingActivity_should_unbindFromLocPubAndUnregisterFromLocSub(){
            LocPubManager mockLocPub = mock(LocPubManager.class);
            MainActivityReceivers mockLocSub = mock(MainActivityReceivers.class);
            FakeMainActivity a = createActivity(FakeMainActivity.class)
                .setLocPub(mockLocPub)
                .setLocSub(mockLocSub);

            a.onPause();

            verify(mockLocPub, times(1)).unbind();
            verify(mockLocSub, times(1)).unregister();
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
        public void clickingGoButton_shouldPingLocation(){
            FakeMainActivity a = Robolectric.buildActivity(FakeMainActivity.class).create().get();
            LocPubManager mockLocPub = mock(LocPubManager.class);
            a.setLocPub(mockLocPub);
            Button go = (Button) a.findViewById(R.id.go_button);

            go.performClick();
            verify(mockLocPub, times(1)).ping();

            go.performClick();
            verify(mockLocPub, times(2)).ping();
        }

        @Test
        public void longClickingGoButton_shouldToggleLocationPolling(){
            LocPubManager mockLocPub = mock(LocPubManager.class);
            FakeMainActivity a = createActivity(FakeMainActivity.class).setLocPub(mockLocPub);
            Button go = (Button) a.findViewById(R.id.go_button);

            go.performLongClick();
            verify(mockLocPub).poll();
            assertThat(lastToast()).isEqualTo("Location sharing on.");

            go.performLongClick();
            verify(mockLocPub).stopPolling();
            assertThat(lastToast()).isEqualTo("Location sharing off.");
        }

    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class SecurityAlert {

        @Test
        public void securityAlert_should_beShownOnceAndOnlyOnce(){
            SecurityAlertFragment mockSecAlert = mock(SecurityAlertFragment.class);
            FakeMainActivity a = createActivity(FakeMainActivity.class)
                .setLocPub(mock(LocPubManager.class))
                .setSecAlert(mockSecAlert);

            a.onResume();
            a.onPause();
            a.onResume();

            verify(mockSecAlert, times(1)).show(a.getFragmentManager(), a.getString(R.string.sec_alert_fragment_tag));
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