package org.tlc.whereat.support;

import org.tlc.whereat.activities.MainActivity;
import org.tlc.whereat.fragments.SecurityAlertFragment;
import org.tlc.whereat.receivers.MainActivityReceivers;
import org.tlc.whereat.pubsub.LocPubManager;


public class FakeMainActivity extends MainActivity {

    public FakeMainActivity setLocPub(LocPubManager locPub){
        mLocPub = locPub;
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
}