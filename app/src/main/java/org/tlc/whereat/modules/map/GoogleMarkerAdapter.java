package org.tlc.whereat.modules.map;

import com.google.android.gms.maps.model.Marker;

public class GoogleMarkerAdapter implements MarkerAdapter {

    Marker mMarker;

    public static MarkerAdapter getInstance(Marker marker){
        return new GoogleMarkerAdapter(marker);
    }

    public GoogleMarkerAdapter(Marker marker){
        mMarker = marker;
    }

    public GoogleMarkerAdapter move(LatLon latLon){
        mMarker.setPosition(latLon.asGoogleLatLon());
        return this;
    }

    public void remove(){
        mMarker.remove();
    }

}
