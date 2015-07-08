package org.tlc.whereat.support;


import android.location.Location;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationHelpers {

    public static final double S17_LAT = 40.7092529;
    public static final double S17_LON = -74.0112551;
    public static final long S17_MILLIS = 1505606400000L;
    public static final String S17_UUID = "75782cd4-1a42-4af1-9130-05c63b2aa9ff";

    public static final double N17_LAT = 40.706877;
    public static final double N17_LON = -74.0112654;
    public static final long N17_MILLIS = 1510876800000L;
    public static final String N17_UUID = "8d3f4369-e829-4ca5-8d9b-123264aeb469";

    public static Location getS17Mock(){
        Location l = mock(Location.class);
        when(l.getLatitude()).thenReturn(S17_LAT);
        when(l.getLongitude()).thenReturn(S17_LON);
        when(l.getTime()).thenReturn(S17_MILLIS);
        return l;
    }

    public static Location getN17Mock(){
        Location l = mock(Location.class);
        when(l.getLatitude()).thenReturn(N17_LAT);
        when(l.getLongitude()).thenReturn(N17_LON);
        when(l.getTime()).thenReturn(N17_MILLIS);
        return l;
    }

    public static boolean areEqual(Location l1, Location l2){
        return
            l1.getLatitude() == l2.getLatitude() &&
            l1.getLongitude() == l2.getLongitude() &&
            l1.getTime() == l2.getTime();
    }

}
