package org.tlc.whereat.modules.map;

import com.google.android.gms.maps.model.LatLng;

public class LatLon {

    // FIELDS

    protected double mLat;
    protected double mLon;

    // CONTRUCTOR

    public LatLon(double lat, double lon){
        mLat = lat;
        mLon = lon;
    }

    // ACCESSORS

    public double getLat(){
        return mLat;
    }
    public double getLon(){
        return mLon;
    }

    // CONVERTERS

    public LatLng asGoogleLatLon() { return new LatLng(mLat, mLon); }


    // EQUALITY

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatLon latLon = (LatLon) o;

        if (Double.compare(latLon.mLat, mLat) != 0) return false;
        return Double.compare(latLon.mLon, mLon) == 0;
    }
}
