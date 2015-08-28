package org.tlc.whereat.support;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import org.tlc.whereat.api.WhereatApiClient;
import org.tlc.whereat.db.Dao;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.pubsub.LocationPublisher;
import static org.mockito.Mockito.*;



/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */
public class FakeLocationPublisher extends LocationPublisher {

    private LocationDao mDao = mock(LocationDao.class);
    private WhereatApiClient mWhereatApiClient = mock(WhereatApiClient.class);

    public LocationDao getDao(){
        return mDao;
    }

    public WhereatApiClient getWhereatApiClient(){
        return mWhereatApiClient;
    }

}


