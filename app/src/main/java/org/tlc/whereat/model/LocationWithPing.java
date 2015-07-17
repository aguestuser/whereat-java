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

    // ACESSORS
    public UserLocation getUserLocation() {
        return mUserLocation;
    }

    public Long getLastPing() {
        return mLastPing;
    }
}
