package org.tlc.whereat.support;

import org.robolectric.annotation.Implementation;
import org.tlc.whereat.activities.MainActivity;
import org.tlc.whereat.broadcast.location.MainLocationSubscriber;
import org.tlc.whereat.services.LocationServiceManager;


public class FakeMainActivity extends MainActivity {

    public FakeMainActivity setLocPub(LocationServiceManager locPub){
        mLocPub = locPub;
        return this;
    }
    public FakeMainActivity setLocSub(MainLocationSubscriber locSub){
        mLocSub = locSub;
        return this;
    }
}