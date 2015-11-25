package org.tlc.whereat.support;

import org.tlc.whereat.activities.MainActivity;
import org.tlc.whereat.fragments.SecurityAlertFragment;
import org.tlc.whereat.modules.pubsub.receivers.MainActivityReceivers;
import org.tlc.whereat.services.LocPubManager;


public class FakeMainActivity extends MainActivity {

    public FakeMainActivity setLocPub(LocPubManager locPub){
        mLocPubMgr = locPub;
        return this;
    }
    public FakeMainActivity setLocSub(MainActivityReceivers locSub){
        mReceivers = locSub;
        return this;
    }

    public SecurityAlertFragment getSecAlert(){
        return mSecAlert;
    }

    public FakeMainActivity setSecAlert(SecurityAlertFragment sFrag){
        mSecAlert = sFrag;
        return this;
    }


    public FakeMainActivity setSecAlerted(boolean value){
        mSecAlerted = value;
        return this;
    }

    public boolean hasSecAlerted(){
        return mSecAlerted;
    }

    public FakeMainActivity setReceivers(MainActivityReceivers rs){
        mReceivers = rs;
        return this;
    }
}