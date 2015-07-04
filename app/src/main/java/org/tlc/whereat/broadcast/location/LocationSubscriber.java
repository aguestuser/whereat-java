package org.tlc.whereat.broadcast.location;

import android.content.BroadcastReceiver;

public interface LocationSubscriber {

    void register();
    void unregister();

}
