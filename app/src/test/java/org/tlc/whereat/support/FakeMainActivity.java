package org.tlc.whereat.support;

import org.tlc.whereat.activities.MainActivity;
import org.tlc.whereat.pubsub.LocationSubscriberMain;
import org.tlc.whereat.pubsub.LocationPublisherManager;


public class FakeMainActivity extends MainActivity {

    public FakeMainActivity setLocPub(LocationPublisherManager locPub){
        mLocPub = locPub;
        return this;
    }
    public FakeMainActivity setLocSub(LocationSubscriberMain locSub){
        mLocSub = locSub;
        return this;
    }
}