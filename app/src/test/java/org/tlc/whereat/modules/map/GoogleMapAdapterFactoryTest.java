package org.tlc.whereat.modules.map;

import android.app.Activity;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * coded with <3 for where@
 */

public class GoogleMapAdapterFactoryTest {

    @Test
    public void getInstance_should_delegateToGoogleMapContainerAndReturnAMapContainer() throws Exception {
        Activity ctx = mock(Activity.class);
        GoogleMapAdapterFactory mcf = new GoogleMapAdapterFactory(ctx);

        assertThat(mcf.mCtx).isEqualTo(ctx);
        assertThat(mcf.createMapAdapter()).isInstanceOf(MapAdapter.class);
    }
}