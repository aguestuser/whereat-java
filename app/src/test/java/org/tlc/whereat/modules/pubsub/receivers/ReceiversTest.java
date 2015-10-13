package org.tlc.whereat.modules.pubsub.receivers;

import android.app.Activity;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.robolectric.shadows.ShadowLog;

import java.util.List;

public class ReceiversTest {

    static { ShadowLog.stream = System.out; }

    Activity ctx;
    LocalBroadcastManager lbm;
    ArgumentCaptor<IntentFilter> ifArg;

    protected void addSpies(List<Receiver> rs){
        for (int i=0; i < rs.size(); i++) rs.set(i, spy(rs.get(i)));
    }

}
