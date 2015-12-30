package org.tlc.whereat.modules.map;

import com.google.android.gms.maps.model.Marker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.model.UserLocation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.tlc.whereat.support.LocationHelpers.*;

/**
 * coded with <3 for where@
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class GoogleMarkerContainerTest {

    UserLocation s17 = s17UserLocationStub();
    GoogleMarkerContainer mc;
    Marker gm;

    @Before
    public void setup(){
        gm = mock(Marker.class);
        mc = (GoogleMarkerContainer) GoogleMarkerContainer.getInstance(gm);

        doNothing().when(gm).setPosition(s17.asLatLon().asGoogleLatLon());
        doNothing().when(gm).remove();
    }

    @Test
    public void getInstance_should_instantiateMarkerContainerWrappingGoogleMarker () throws Exception {
        assertThat(mc).isInstanceOf(MarkerContainer.class);
        assertThat(mc.mMarker).isEqualTo(gm);
    }

    @Test
    public void move_should_delegateToGoogleMapsAndReturnThis() throws Exception {
        assertThat(mc.move(s17.asLatLon())).isEqualTo(mc);
        verify(gm).setPosition(s17.asLatLon().asGoogleLatLon());
    }

    @Test
    public void remove_should_delegateToGoogleMaps() {
        mc.remove();
        verify(gm).remove();
    }
}