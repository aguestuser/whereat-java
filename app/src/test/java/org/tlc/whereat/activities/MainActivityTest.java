package org.tlc.whereat.activities;

import android.content.ComponentName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowService;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.services.LocationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tlc.whereat.support.TestHelpers.lastToast;
import static org.tlc.whereat.support.TestHelpers.nextActivity;
import static org.tlc.whereat.support.TestHelpers.nextService;
import static org.tlc.whereat.support.TestHelpers.shadowBind;

import static org.robolectric.Shadows.shadowOf;

//import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)

public class MainActivityTest {

    private MainActivity createActivity(){
        return Robolectric.buildActivity(MainActivity.class).create().get();
    }

    private MainActivity resumeActivity(){
        return Robolectric.buildActivity(MainActivity.class).create().resume().visible().get();
    }

    @Test
    public void selectingMapFromMenu_should_switchToMapView(){

        MainActivity a = createActivity();
        a.onOptionsItemSelected(new RoboMenuItem(R.id.action_map));

        assertThat(nextActivity(a))
            .isEqualTo(MapActivity.class.getName());

    }

    @Test
    public void creatingActivity_should_startLocationPublisher(){

        MainActivity a = createActivity();

        assertThat(nextService(a))
            .isEqualTo(LocationService.class.getName());

    }


//    @Test
//    public void clickingGoButton_should_toastLocation(){
//
//        ShadowService ls = shadowOf(Robolectric.setupService(LocationService.class));
//        ComponentName cn = new ComponentName(LocationService.class.getCanonicalName(), LocationService.class.getSimpleName());
//
//        shadowBind(cn, new LocationService.LocationServiceBinder());
//
//        MainActivity sa = resumeActivity();
//        sa.findViewById(R.id.go_button).performClick();
//
//        assertThat(lastToast()).contains("Location shared");
//
//    }

}