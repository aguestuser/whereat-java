package org.tlc.whereat.modules.map;

import com.google.android.gms.maps.model.Marker;

public class GoogleMarkerContainer implements MarkerContainer {

    Marker mMarker;

    public static MarkerContainer getInstance(Marker marker){
        return new GoogleMarkerContainer(marker);
    }

    public GoogleMarkerContainer(Marker marker){
        mMarker = marker;
    }

    public GoogleMarkerContainer move(LatLon latLon){
        mMarker.setPosition(latLon.asGoogleLatLon());
        return this;
    }

    public void remove(){
        mMarker.remove();
    }

}
