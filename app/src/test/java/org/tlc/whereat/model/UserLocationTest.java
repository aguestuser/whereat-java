package org.tlc.whereat.model;

import android.location.Location;

import org.junit.Test;

import static org.tlc.whereat.support.LocationHelpers.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class UserLocationTest {

    @Test
    public void valueOfWithId_should_convertLocationToUserLocation(){
        Location l = s17AndroidLocationMock();
        UserLocation ul = UserLocation.valueOf(S17_UUID, l);

        assertTrue(ul.equals(s17UserLocationStub()));
    }

    @Test
    public void valueOfWithoutId_should_convertLocationToUserLocation(){
        Location l = s17AndroidLocationMock();
        UserLocation ul = UserLocation.valueOf(l);

        assertTrue(ul.equals(s17UserLocationStub(ul.getId())));
    }

    @Test
    public void valueOf_should_produceUniqueIds(){
        Location l = s17AndroidLocationMock();
        UserLocation ul1 = UserLocation.valueOf(l);
        UserLocation ul2 = UserLocation.valueOf(l);

        assertThat(ul1.getId()).isNotEqualTo(ul2.getId());
        assertFalse(ul1.equals(ul2));
    }


}