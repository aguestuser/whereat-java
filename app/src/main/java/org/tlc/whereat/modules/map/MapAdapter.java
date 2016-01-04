package org.tlc.whereat.modules.map;

interface MapAdapter {

    MapAdapter getMap();
    MapAdapter clear();
    MapAdapter showUserLocation();
    MapAdapter center(LatLon latLon);

    MarkerAdapter addMarker(LatLon latLon, String msg);
}
