package org.tlc.whereat.modules.map;

import android.app.Activity;

/**
 * coded with <3 for where@
 */

public class GoogleMapAdapterFactory extends MapAdapterFactory {

    public GoogleMapAdapterFactory(Activity ctx){
        super(ctx);
    }

    public MapAdapter createMapAdapter(){
        return GoogleMapAdapter.getInstance(mCtx);
    }
}
