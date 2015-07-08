package org.tlc.whereat.activities;

import android.widget.Button;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;

import static org.mockito.Mockito.*;

import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.broadcast.location.MainLocationSubscriber;
import org.tlc.whereat.services.LocationService;
import org.tlc.whereat.services.LocationServiceManager;
import org.tlc.whereat.support.FakeMainActivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tlc.whereat.support.TestHelpers.lastToast;
import static org.tlc.whereat.support.TestHelpers.nextActivity;
import static org.tlc.whereat.support.TestHelpers.nextService;
import static org.tlc.whereat.support.TestHelpers.createActivity;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)

public class MainActivityTest {

    @Test
    public void creatingActivity_should_startLocationPublisher(){
        MainActivity a = createActivity(FakeMainActivity.class);

        assertThat(nextService(a))
            .isEqualTo(LocationService.class.getName());
    }

    @Test
    public void resumingActivity_should_bindToLocPubAndRegisterToLocSub(){
        LocationServiceManager mockLocPub = mock(LocationServiceManager.class);
        MainLocationSubscriber mockLocSub = mock(MainLocationSubscriber.class);
        FakeMainActivity a = createActivity(FakeMainActivity.class)
            .setLocPub(mockLocPub)
            .setLocSub(mockLocSub);

        a.onResume();

        verify(mockLocPub, times(1)).bind();
        verify(mockLocSub, times(1)).register();
    }


    @Test
    public void pausingActivity_should_unbindFromLocPubAndUnregisterFromLocSub(){
        LocationServiceManager mockLocPub = mock(LocationServiceManager.class);
        MainLocationSubscriber mockLocSub = mock(MainLocationSubscriber.class);
        FakeMainActivity a = createActivity(FakeMainActivity.class)
            .setLocPub(mockLocPub)
            .setLocSub(mockLocSub);

        a.onPause();

        verify(mockLocPub, times(1)).unbind();
        verify(mockLocSub, times(1)).unregister();
    }

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
        LocationServiceManager mockLocPub = mock(LocationServiceManager.class);
        a.setLocPub(mockLocPub);
        Button go = (Button) a.findViewById(R.id.go_button);

        go.performClick();
        verify(mockLocPub, times(1)).ping();

        go.performClick();
        verify(mockLocPub, times(2)).ping();
    }

    @Test
    public void longClickingGoButton_shouldToggleLocationPolling(){
        LocationServiceManager mockLocPub = mock(LocationServiceManager.class);
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