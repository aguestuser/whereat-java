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

    private static final String BASE_URL = "https://whereat-server.herokuapp.com";
    private WhereatApi mApi;

    // CONSTRUCTORS
    private static final WhereatApiClient INSTANCE = new WhereatApiClient();

    private WhereatApiClient(){
        RestAdapter ra = new RestAdapter.Builder()
            .setEndpoint(BASE_URL)
            .build();

        mApi = ra.create(WhereatApi.class);
    }

    public static WhereatApiClient getInstance(){
        return INSTANCE;
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
