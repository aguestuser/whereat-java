package org.tlc.whereat.modules.map;

interface MarkerContainer {

    MarkerContainer move(LatLon latLon);
    void remove();
}
