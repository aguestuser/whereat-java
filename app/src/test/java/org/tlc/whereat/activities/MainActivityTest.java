package org.tlc.whereat.activities;

import android.app.AlertDialog;
import android.os.Build;
import android.widget.Button;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;

import static org.mockito.Mockito.*;

import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import static org.robolectric.Shadows.shadowOf;

import org.robolectric.util.ActivityController;
import org.robolectric.util.FragmentTestUtil;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.fragments.SecurityAlertFragment;
import org.tlc.whereat.pubsub.LocPubManager;
import org.tlc.whereat.pubsub.LocSubMain;
import org.tlc.whereat.pubsub.LocationPublisher;
import org.tlc.whereat.support.FakeMainActivity;

import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.*;



@RunWith(Enclosed.class)

public class MainActivityTest {

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class)

    public static class LifeCycleMethods {

        @Test
        public void creatingActivity_should_startLocationPublisher(){
            MainActivity a = createActivity(FakeMainActivity.class);

            assertThat(nextService(a))
                .isEqualTo(LocationPublisher.class.getName());
        }

        @Test
        public void resumingActivity_should_bindToLocPubAndRegisterToLocSub(){
            LocPubManager mockLocPub = mock(LocPubManager.class);
            LocSubMain mockLocSub = mock(LocSubMain.class);
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
            LocSubMain mockLocSub = mock(LocSubMain.class);
            FakeMainActivity a = createActivity(FakeMainActivity.class)
                .setLocPub(mockLocPub)
                .setLocSub(mockLocSub);

            a.onPause();

            verify(mockLocPub, times(1)).unbind();
            verify(mockLocSub, times(1)).unregister();
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class)

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
    @Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)

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

            verify(mockSecAlert, times(1)).show(a.getFragmentManager(), a.getString(R.string.security_alert_fragment_tag));
        }
    }
}