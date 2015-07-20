package org.tlc.whereat.model;

import org.junit.Test;


import static org.tlc.whereat.support.LocationHelpers.*;
import static org.assertj.core.api.Assertions.*;

public class UserLocationTimestampedTest {

    @Test
    public void toJson_should_serializeToJson(){
        assertThat(
            s17LocationTimestampedStub().toJson())
            .isEqualTo(S17_WITH_PING_JSON);
    }

    @Test
    public void fromJson_should_deserializeFromJson(){
        assertThat(
            UserLocationTimestamped.fromJson(S17_WITH_PING_JSON))
            .isEqualTo(s17LocationTimestampedStub());
    }


}