package org.tlc.whereat.api;

import org.tlc.whereat.model.ApiMessage;
import org.tlc.whereat.model.UserLocationTimestamped;
import org.tlc.whereat.model.UserLocation;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

public interface WhereatApi {

    @POST("/locations/update")
    Observable<List<UserLocation>> update(@Body UserLocationTimestamped ult);

    @POST("/locations/clear")
    Observable<ApiMessage> remove(@Body UserLocation ul);

}