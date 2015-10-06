package org.tlc.whereat.support;

import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.tlc.whereat.activities.MapActivity;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.pubsub.LocPubManager;
import org.tlc.whereat.receivers.MapActivityReceivers;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */
public class FakeMapActivity extends MapActivity {

    public FakeMapActivity setMap(GoogleMap map){
        mMap = map;
        return this;
    }

    public FakeMapActivity setLocDao(LocationDao dao){
        mLocDao = dao;
        return this;
    }

    public FakeMapActivity setLocPub(LocPubManager locPub){
        mLocPub = locPub;
        return this;
    }

    public FakeMapActivity setLocSub(MapActivityReceivers locSub){
        mReceivers = locSub;
        return this;
    }

    public FakeMapActivity setMarkers(ConcurrentHashMap<String, Pair<Long,Marker>> markers){
        mMarkers = markers;
        return this;
    }

    public FakeMapActivity setLastPing(Long ping) {
        mLastPing = ping;
        return this;
    }

    @Override
    protected void initialize(){}

}
