package org.tlc.whereat.model;


import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static java.util.UUID.randomUUID;

public class UserLocation implements Parcelable {

    @SerializedName("id") private String mId;
    @SerializedName("lat") private double mLat;
    @SerializedName("lon") private double mLon;
    @SerializedName("time") private long mTime;

    // CONSTRUCTORS

    public static UserLocation valueOf(Location l){
        String id = randomUUID().toString();
        return valueOf(id, l);
    }

    public static UserLocation valueOf(String id, Location l){
        return new UserLocation(
            id,
            l.getLatitude(),
            l.getLongitude(),
            l.getTime()
        );
    }

    public static UserLocation create(String id, double lat, double lon, long time){
        return new UserLocation(id, lat, lon, time);
    }

    private UserLocation(String id, double lat, double lon, long time){
        mId = id; mLat = lat; mLon = lon; mTime = time;
    }

    protected UserLocation(Parcel in) {
        mId = in.readString();
        mLat = in.readDouble();
        mLon = in.readDouble();
        mTime = in.readLong();
    }

    // CONVERTERS

    public UserLocationTimestamped withTimestamp(Long time) {
        return new UserLocationTimestamped(time, this);
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public static String toJsonList(List<UserLocation> uls){
        return new Gson().toJson(uls);
    }

    public static UserLocation fromJson(String json){
        return new Gson().fromJson(json, UserLocation.class);
    }

    public static List<UserLocation> fromJsonList(String json){
        Type type = new TypeToken<List<UserLocation>>(){}.getType();
        return new Gson().fromJson(json, type);
    }


    // ACCESSORS

    public String getId() {
        return mId;
    }

    public double getLatitude() {
        return mLat;
    }

    public double getLongitude() {
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

    // EQUALS AND HASH CODE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserLocation that = (UserLocation) o;

        if (Double.compare(that.mLat, mLat) != 0) return false;
        if (Double.compare(that.mLon, mLon) != 0) return false;
        if (mTime != that.mTime) return false;
        return mId.equals(that.mId);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mId.hashCode();
        temp = Double.doubleToLongBits(mLat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mLon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (mTime ^ (mTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
            "id=" + mId +
            "lat=" + mLat +
            ", lon=" + mLon +
            ", time=" + mTime +
            '}';
    }
}
