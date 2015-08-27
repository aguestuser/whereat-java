package org.tlc.whereat.support;

import org.tlc.whereat.activities.MainActivity;
import org.tlc.whereat.pubsub.LocSubMain;
import org.tlc.whereat.pubsub.LocPubManager;


public class FakeMainActivity extends MainActivity {

    public FakeMainActivity setLocPub(LocPubManager locPub){
        mLocPub = locPub;
        return this;
    }
    public FakeMainActivity setLocSub(LocSubMain locSub){
        mLocSub = locSub;
        return this;
    }
}