package org.tlc.whereat.model;

import org.junit.Test;


import static org.tlc.whereat.support.LocationHelpers.*;
import static org.assertj.core.api.Assertions.*;

public class LocationWithPingTest {

    @Test
    public void toJson_should_serializeToJsonCorrectly(){
        LocationWithPing l = s17LocationWithPingStub();

        assertThat(l.toJson()).isEqualTo(S17_WITH_PING_JSON);
    }

}