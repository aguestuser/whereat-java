package org.tlc.whereat.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class LocationWithPing {

    @SerializedName("lastPing") private Long mLastPing;
    @SerializedName("location") private UserLocation mUserLocation;

    public LocationWithPing(Long lp, UserLocation ul) {
        mLastPing = lp;
        mUserLocation = ul;
    }

    // CONVERTERS

    public String toJson(){
        return new Gson().toJson(this);
    }

    public static LocationWithPing fromJson(String json){
        return new Gson().fromJson(json, LocationWithPing.class);
    }

    // ACESSORS
    public UserLocation getUserLocation() {
        return mUserLocation;
    }

    public Long getLastPing() {
        return mLastPing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationWithPing that = (LocationWithPing) o;

        if (!mLastPing.equals(that.mLastPing)) return false;
        return mUserLocation.equals(that.mUserLocation);

    }

    @Override
    public int hashCode() {
        int result = mLastPing.hashCode();
        result = 31 * result + mUserLocation.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LocationWithPing{" +
            "lastPing=" + mLastPing +
            ", userLocation=" + mUserLocation +
            '}';
    }
}
