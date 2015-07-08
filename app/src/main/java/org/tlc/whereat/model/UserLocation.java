package org.tlc.whereat.model;


import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class UserLocation implements Parcelable {

    private String mId;
    private double mLat;
    private double mLon;
    private long mTime;


    // CONSTRUCTORS

    protected UserLocation(Parcel in) {
        mId = in.readString();
        mLat = in.readDouble();
        mLon = in.readDouble();
        mTime = in.readLong();
    }


    public UserLocation valueOf(Location l){
        return new UserLocation(
            UUID.randomUUID().toString(),
            l.getLatitude(),
            l.getLongitude(),
            l.getTime()
        );
    }

    public UserLocation create(String id, double lat, double lon, long time){
        return new UserLocation(id, lat, lon, time);
    }

    private UserLocation(String id, double lat, double lon, long time){
        mId = id; mLat = lat; mLon = lon; mTime = time;
    }

    // ACCESSORS

    public String getId() {
        return mId;
    }

    public double getLat() {
        return mLat;
    }

    public double getLon() {
        return mLon;
    }

    public long getTime() {
        return mTime;
    }

    // PARCELABLE IMPLEMENTATION

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeDouble(mLat);
        dest.writeDouble(mLon);
        dest.writeLong(mTime);
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            String id = in.readString();
            Double lat = in.readDouble();
            Double lon = in.readDouble();
            long time = in.readLong();
            return new UserLocation(id, lat, lon, time);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

}
