package org.tlc.whereat.modules.map;

interface MarkerAdapter {

    MarkerAdapter move(LatLon latLon);
    void remove();
}
