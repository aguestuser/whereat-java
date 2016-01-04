package org.tlc.whereat.modules.map;

import android.app.Activity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.activities.MapActivity;
import org.tlc.whereat.model.UserLocation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.tlc.whereat.support.LocationHelpers.*;

/**
 * coded with <3 for where@
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class GoogleMapAdapterTest {

    UserLocation s17 = s17UserLocationStub();
    Activity ctx;
    GoogleMapAdapter mc;

    GoogleMap gm = mock(GoogleMap.class);
    CameraUpdate cu = mock(CameraUpdate.class);
    MarkerOptions mo = mock(MarkerOptions.class);

    @Before
    public void setup(){
        ctx = Robolectric.buildActivity(MapActivity.class).get();
        mc = spy((GoogleMapAdapter) GoogleMapAdapter.getInstance(ctx));

        doReturn(gm).when(mc).getGoogleMap();
        doReturn(cu).when(mc).getCameraUpdate(s17.asLatLon());
        doReturn(mo).when(mc).getMarkerOptions(s17.asLatLon(), s17.asDateTime());
    }

    @Test
    public void constructor_should_setContextAndMapFields(){
        assertThat(mc.mCtx).isEqualTo(ctx);
    }

    @Test
    public void getMap_should_getGoogleMap(){
        mc.getMap();

        verify(mc).getGoogleMap();
        assertThat(mc.mMap).isEqualTo(gm);
    }

    @Test
    public void clear_should_delegateToGoogleMapAndReturnThis(){
        mc.getMap();
        assertThat(mc.clear()).isEqualTo(mc);
        verify(gm).clear();
    }

    @Test
    public void showUserLocation_should_delegateToGoogleMapAndReturnThis(){
        mc.getMap();
        assertThat(mc.showUserLocation()).isEqualTo(mc);
        verify(gm).setMyLocationEnabled(true);
    }

    @Test
    public void center_should_delegateToGoogleMapAndReturnThis(){
        mc.getMap();
        assertThat(mc.center(s17.asLatLon())).isEqualTo(mc);
        verify(gm).moveCamera(cu);
    }

    @Test
    public void addMarker_should_delegateToGoogleMapAndReturnMarkerContainer(){
        mc.getMap();
        assertThat(mc.addMarker(s17.asLatLon(), s17.asDateTime())).isInstanceOf(MarkerAdapter.class);
        verify(gm).addMarker(mo);
    }

}