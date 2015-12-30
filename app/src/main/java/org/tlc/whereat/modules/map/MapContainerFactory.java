package org.tlc.whereat.modules.map;


import android.app.Activity;
import android.content.Context;

public abstract class MapContainerFactory {
    Activity mCtx;

    public MapContainerFactory (Activity ctx){
        mCtx = ctx;
    }

    public abstract MapContainer getInstance();

}
