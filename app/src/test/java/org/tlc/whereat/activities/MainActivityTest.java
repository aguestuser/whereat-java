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
import org.tlc.whereat.pubsub.LocPubManager;
import org.tlc.whereat.pubsub.LocSubMain;
import org.tlc.whereat.pubsub.LocationPublisher;
import org.tlc.whereat.support.FakeMainActivity;

import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.*;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)

public class MainActivityTest {

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