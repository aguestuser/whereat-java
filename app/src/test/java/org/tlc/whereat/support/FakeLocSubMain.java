package org.tlc.whereat.support;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import org.robolectric.shadows.ShadowLocalBroadcastManager;
import org.tlc.whereat.pubsub.LocSubMain;

public class FakeLocSubMain extends LocSubMain {

    public FakeLocSubMain(Context ctx){
        super(ctx);
    }

    public <T extends LocalBroadcastManager> FakeLocSubMain setLbm(T lbm){
        mLbm = lbm;
        return this;
    }

    public LocalBroadcastManager getLbm(){
        return mLbm;
    }
}
