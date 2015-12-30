package org.tlc.whereat.modules.map;

import android.app.Activity;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * coded with <3 for where@
 */

public class GoogleMapContainerFactoryTest {

    @Test
    public void getInstance_should_delegateToGoogleMapContainerAndReturnAMapContainer() throws Exception {
        Activity ctx = mock(Activity.class);
        GoogleMapContainerFactory mcf = new GoogleMapContainerFactory(ctx);

        assertThat(mcf.mCtx).isEqualTo(ctx);
        assertThat(mcf.getInstance()).isInstanceOf(MapContainer.class);
    }
}