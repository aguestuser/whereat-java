package org.tlc.whereat.api;

import org.tlc.whereat.model.UserLocationTimestamped;
import org.tlc.whereat.model.UserLocation;

import java.util.List;

import retrofit.RestAdapter;
import rx.Observable;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

public class WhereatApiClient implements WhereatApi {

    private static String mRoot = "https://whereat-server.herokuapp.com";
    private static WhereatApiClient mInstance;
    private WhereatApi mApi;


    // CONSTRUCTORS

    public static WhereatApiClient getInstance(){
        if (mInstance == null) mInstance = new WhereatApiClient(mRoot);
        return mInstance;
    }

    public static WhereatApiClient getInstance(String root){
        if (mInstance == null) mInstance = new WhereatApiClient(root);
        return mInstance;
    }

    private WhereatApiClient(String root){
        RestAdapter ra = new RestAdapter.Builder()
            .setEndpoint(root)
            .build();

        mApi = ra.create(WhereatApi.class);
    }

    // API METHODS

    @Override
    public Observable<List<UserLocation>> init(UserLocation l) {
        return mApi.init(l);
    }

    @Override
    public Observable<List<UserLocation>> refresh(UserLocationTimestamped l) {
        return mApi.refresh(l);
    }
}
