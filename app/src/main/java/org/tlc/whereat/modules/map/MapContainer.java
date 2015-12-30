package org.tlc.whereat.modules.map;

import android.app.Activity;

interface MapContainer {

    MapContainer getMap();
    MapContainer clear();
    MapContainer showUserLocation();
    MapContainer center(LatLon latLon);

    MarkerContainer addMarker(LatLon latLon, String msg);
}
