package org.tlc.whereat.support;


import android.location.Location;

import org.tlc.whereat.model.UserLocationTimestamped;
import org.tlc.whereat.model.UserLocation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationHelpers {

    public static final String S17_UUID = "75782cd4-1a42-4af1-9130-05c63b2aa9ff";
    public static final double S17_LAT = 40.7092529;
    public static final double S17_LON = -74.0112551;
    public static final long S17_MILLIS = 1316232000000L;

    public static final double S17_MOVED_LAT = 40.709000;

    public static final String N17_UUID = "8d3f4369-e829-4ca5-8d9b-123264aeb469";
    public static final double N17_LAT = 40.706877;
    public static final double N17_LON = -74.0112654;
    public static final long N17_MILLIS = 1321506000000L;

    public static final double N17_MOVED_LAT = 40.706000;

    public static final String S17_JSON =
        "{" +
            "\"id\":\"75782cd4-1a42-4af1-9130-05c63b2aa9ff\"," +
            "\"lat\":40.7092529," +
            "\"lon\":-74.0112551," +
            "\"time\":1316232000000" +
        "}";


    public static final String API_INIT_RESPONSE =
        "[{" +
            "\"id\":\"75782cd4-1a42-4af1-9130-05c63b2aa9ff\"," +
            "\"lat\":40.7092529," +
            "\"lon\":-74.0112551," +
            "\"time\":1316232000000" +
        "},{" +
            "\"id\":\"8d3f4369-e829-4ca5-8d9b-123264aeb469\"," +
            "\"lat\":40.706877," +
            "\"lon\":-74.0112654," +
            "\"time\":1321506000000" +
        "}]";

    public static final String API_REFRESH_RESPONSE =
        "[{" +
            "\"id\":\"8d3f4369-e829-4ca5-8d9b-123264aeb469\"," +
            "\"lat\":40.706877," +
            "\"lon\":-74.0112654," +
            "\"time\":1321506000000" +
        "}]";


    public static final String S17_WITH_PING_JSON =
        "{" +
            "\"lastPing\":1316232000000," +
            "\"location\":{" +
                "\"id\":\"75782cd4-1a42-4af1-9130-05c63b2aa9ff\"," +
                "\"lat\":40.7092529," +
                "\"lon\":-74.0112551," +
                "\"time\":1316232000001" +
            "}" +
        "}";

    public static Location s17AndroidLocationMock(){
        Location l = mock(Location.class);
        when(l.getLatitude()).thenReturn(S17_LAT);
        when(l.getLongitude()).thenReturn(S17_LON);
        when(l.getTime()).thenReturn(S17_MILLIS);
        return l;
    }

    public static UserLocation s17UserLocationStub(){
        return UserLocation.create(S17_UUID, S17_LAT, S17_LON, S17_MILLIS);
    }

    public static UserLocation s17UserLocationStub(String id){
        return UserLocation.create(id, S17_LAT, S17_LON, S17_MILLIS);
    }


    public static UserLocation s17UserLocationStubLater(){
        return UserLocation.create(S17_UUID, S17_LAT, S17_LON, S17_MILLIS + 1L);
    }

    public static UserLocation s17UserLocationStubMoved(){
        return UserLocation.create(S17_UUID, S17_MOVED_LAT, S17_LON, S17_MILLIS + 1L);
    }



    public static UserLocation n17UserLocationStub(){
        return UserLocation.create(N17_UUID, N17_LAT, N17_LON, N17_MILLIS);
    }

    public static UserLocation n17UserLocationStubMoved(){
        return UserLocation.create(N17_UUID, N17_MOVED_LAT, N17_LON, N17_MILLIS + 1L);
    }

    public static UserLocationTimestamped s17LocationTimestampedStub(){
        return new UserLocationTimestamped(S17_MILLIS, s17UserLocationStubLater());
    }

    public static UserLocationTimestamped n17LocationTimestampedStub(){
        return new UserLocationTimestamped(S17_MILLIS - 1L, n17UserLocationStub());
    }

    public static UserLocationTimestamped updateInitStub(){
        return new UserLocationTimestamped(-1L, n17UserLocationStub());
    }

    public static UserLocationTimestamped updateRefreshStub(){
        return new UserLocationTimestamped(S17_MILLIS, n17UserLocationStub());
    }


    public static boolean areEqual(UserLocation l1, UserLocation l2){
        return
            l1.getLatitude() == l2.getLatitude() &&
            l1.getLongitude() == l2.getLongitude() &&
            l1.getTime() == l2.getTime();
    }

}
