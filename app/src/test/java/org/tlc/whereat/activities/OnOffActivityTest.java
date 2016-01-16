package org.tlc.whereat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;


import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowPreferenceManager;
import org.robolectric.util.ActivityController;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.modules.ui.MenuHandler;
import org.tlc.whereat.services.LocPubManager;
import org.tlc.whereat.modules.pubsub.receivers.MainActivityReceivers;
import org.tlc.whereat.support.ActivityWithMenuHandlersTest;
import org.tlc.whereat.support.FakeOnOffActivity;

import rx.functions.Func1;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(Enclosed.class)

public class OnOffActivityTest {

    @RunWith(Enclosed.class)

    public static class LifeCycleMethods {

        @RunWith(RobolectricGradleTestRunner.class)
        @Config(constants = BuildConfig.class, sdk = 21)

        public static class OnCreate {

            OnOffActivity a;

            @Before
            public void setup() {
                a = createActivity(OnOffActivity.class);
            }

            @Test
            public void onCreate_should_initializeActivityAndApplicationCorrectly() {

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

        OnOffActivity a;

        @Before
        public void setup() {
            a = createActivity(OnOffActivity.class);
            a.mLocPubMgr = mock(LocPubManager.class);
            a.mReceivers = mock(MainActivityReceivers.class);
        }

        @Test
        public void onResume_should_bindToLocPubAndRegisterReceivers() {
            a.onResume();

            verify(a.mLocPubMgr).bind();
            verify(a.mReceivers).register();
        }


        @Test
        public void onPause_should_unbindFromLocPubAndUnregisterReceivers() {
            a.onPause();

            verify(a.mLocPubMgr).unbind();
            verify(a.mReceivers).unregister();
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class ReCreate {

        ActivityController<OnOffActivity> c;
        ActivityController<OnOffActivity> c_;
        ActivityController<OnOffActivity> c__;

        OnOffActivity a;
        OnOffActivity a_;
        OnOffActivity a__;

        Bundle b;
        Bundle b_;

        @Before
        public void setup() {
            c = Robolectric.buildActivity(OnOffActivity.class);
            c_ = Robolectric.buildActivity(OnOffActivity.class);
            c__ = Robolectric.buildActivity(OnOffActivity.class);

            a = c.create().get();
            a.mLocPubMgr = mock(LocPubManager.class);
            a.mReceivers = mock(MainActivityReceivers.class);

            b = new Bundle();
            b_ = new Bundle();
        }

        @Test
        public void onOffActivity_should_preservePollingStateAcrossLifeCycles() {

            a.mPolling = false;
            c.saveInstanceState(b).destroy();
            a_ = c_.create(b).start().get();

            assertThat(a_.mPolling).isFalse();

            a_.mPolling = true;
            c_.saveInstanceState(b_).destroy();
            a__ = c__.create().start().get();

            assertThat(a__.mPolling).isTrue();
        }

        /*
        * TODO:
        *
        * @Test
        * public void onOffActivity_should_onlyHaveOnceInstance(){}
        *
        * Test that switching to MapActivity, then back to OnOffActivity
        * starts the original instance of the OnOffActivity
        * instead of launching a new instance. (as specified by
        * `android:launchMode="singleInstance"` in AndroidManifest.xml)
        *
        * NOTE:
        *
        * This behavior has been verified by running in an emulator,
        * but testing it has proven difficult using only Robolectric.
        * Satisfactorily testing it might require integration tests with Espresso.
        *
        * [@aguestuser - 1.16.16]
        *
        * */
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class EventHandlers {

        @Test
        public void selectingMapFromMenu_should_switchToMapView(){
            OnOffActivity a = createActivity(OnOffActivity.class);
            a.onOptionsItemSelected(new RoboMenuItem(R.id.action_map));

            assertThat(nextActivity(a))
                .isEqualTo(MapActivity.class.getName());
        }

        @Test
        public void clickingGoButton_shouldToggleLocationPolling(){
            LocPubManager mockLocPub = mock(LocPubManager.class);
            FakeOnOffActivity a = createActivity(FakeOnOffActivity.class).setLocPub(mockLocPub);
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

        OnOffActivity a;

        @Before
        public void setup() {
            a = createActivity(OnOffActivity.class);
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