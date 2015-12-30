package org.tlc.whereat.modules.map;

import android.app.Activity;
import android.content.Context;

/**
 * coded with <3 for where@
 */

public class GoogleMapContainerFactory extends MapContainerFactory {

    public GoogleMapContainerFactory(Activity ctx){
        super(ctx);
    }

    public MapContainer getInstance(){
        return GoogleMapContainer.getInstance(mCtx);
    }
}
