package org.tlc.whereat.modules.map;


import android.app.Activity;

public abstract class MapAdapterFactory {
    Activity mCtx;

    public MapAdapterFactory(Activity ctx){
        mCtx = ctx;
    }

    public abstract MapAdapter createMapAdapter();

}
