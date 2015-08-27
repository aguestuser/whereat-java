package org.tlc.whereat.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */
public class ApiMessage {

    @SerializedName("msg") private String mMsg;

    // CONSTRUCTORS

    public static ApiMessage of(String msg){
        return new ApiMessage(msg);
    }

    private ApiMessage(String msg){
        mMsg = msg;
    }

    public String get(){
        return mMsg;
    }

    // CONVERTERS

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static ApiMessage fromJson(String json){
        return new Gson().fromJson(json, ApiMessage.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiMessage that = (ApiMessage) o;

        return mMsg.equals(that.mMsg);

    }

    @Override
    public int hashCode() {
        return mMsg.hashCode();
    }
}
