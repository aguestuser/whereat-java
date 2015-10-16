package org.tlc.whereat.activities;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.modules.pubsub.receivers.SettingsActivityReceivers;
import org.tlc.whereat.modules.ui.MenuHandler;
import org.tlc.whereat.support.ActivityWithMenuHandlersTest;

import rx.functions.Func1;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.*;

@RunWith(Enclosed.class)

public class SettingsActivityTest {


    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class OnCreate {

        @Test
        public void onCreate_should_initializeActivityCorrectly(){
            SettingsActivity a = createActivity(SettingsActivity.class);

            assertThat(a.mMenu).isNotNull();
            assertThat(
                a.getFragmentManager().findFragmentByTag("settings_fragment"))
                .isNotNull();
        }
    }


    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class PostCreate {

        SettingsActivity a;

        @Before
        public void setup(){
            a = createActivity(SettingsActivity.class);
            a.mReceivers = mock(SettingsActivityReceivers.class);
        }

        @Test
        public void onResume_should_registerReceivers(){
            a.onResume();
            verify(a.mReceivers).register();
        }


        @Test
        public void onPause_should_unregisterReceivers(){
            a.onPause();
            verify(a.mReceivers).unregister();
        }
    }



    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class MenuHandlers extends ActivityWithMenuHandlersTest {

        SettingsActivity a;

        @Before
        public void setup() {
            a = createActivity(SettingsActivity.class);
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