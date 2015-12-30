package org.tlc.whereat.modules.map;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Coded with <3 for where@
 */

public class LatLonTest {

    double lat = 1.2345;
    double lon = 5.4321;
    LatLon ll;

    @Before
    public void setup(){
        ll = new LatLon(lat, lon);
    }

    @Test
    public void constructor_should_instantiateObjectWithCorrectFields(){
        assertThat(ll).isNotNull();
        assertThat(ll.mLat).isEqualTo(lat);
        assertThat(ll.mLon).isEqualTo(lon);
    }

    @Test
    public void getters_should_retrieveFields(){
        assertThat(ll.getLat()).isEqualTo(lat);
        assertThat(ll.getLon()).isEqualTo(lon);
    }

    @Test
    public void asGoogleLatLon_should_convertToGoogleLatLon(){
        LatLng gll = ll.asGoogleLatLon();

        assertThat(gll).isNotNull();
        assertThat(gll.latitude).isEqualTo(ll.getLat());
        assertThat(gll.longitude).isEqualTo(ll.getLon());
    }

    @Test
    public void equals_should_compareLatAndLonFieldsButNotObjIdentity(){
        assertThat(ll).isEqualTo(new LatLon(ll.getLat(), ll.getLon()));
    }
}