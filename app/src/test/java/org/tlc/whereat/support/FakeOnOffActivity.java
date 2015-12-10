package org.tlc.whereat.support;

import org.tlc.whereat.activities.OnOffActivity;
import org.tlc.whereat.modules.pubsub.receivers.MainActivityReceivers;
import org.tlc.whereat.services.LocPubManager;


public class FakeOnOffActivity extends OnOffActivity {

    public FakeOnOffActivity setLocPub(LocPubManager locPub){
        mLocPubMgr = locPub;
        return this;
    }
    public FakeOnOffActivity setLocSub(MainActivityReceivers locSub){
        mReceivers = locSub;
        return this;
    }

    public FakeOnOffActivity setReceivers(MainActivityReceivers rs){
        mReceivers = rs;
        return this;
    }
}