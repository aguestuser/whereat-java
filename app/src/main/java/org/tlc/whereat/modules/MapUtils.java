package org.tlc.whereat.modules;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapUtils {

    public MapUtils(){}

    public static MarkerOptions parseMarker(Location l){
        return new MarkerOptions().position(parseLatLon(l)).title(parseTime(l));
    }

    public static LatLng parseLatLon(Location loc){
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    private static String parseTime(Location loc){
        return "time when this was posted"; // TODO implement this!
    }

}
