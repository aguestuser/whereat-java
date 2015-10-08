package org.tlc.whereat.support;

import com.google.android.gms.common.api.GoogleApiClient;

import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.pubsub.LocationPublisher;
import org.tlc.whereat.pubsub.Scheduler;


/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */
public class FakeLocationPublisher extends LocationPublisher {

    public FakeLocationPublisher setGoogleApiClient(GoogleApiClient cl){
        mGoogClient = cl;
        return this;
    }

    public FakeLocationPublisher setDao(LocationDao dao){
        mDao = dao;
        return this;
    }

    public FakeLocationPublisher setScheduler(Scheduler sch){
        mScheduler = sch;
        return this;
    }

}


