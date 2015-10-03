package org.tlc.whereat.util;

import org.junit.Test;
import org.tlc.whereat.support.SampleTimes;

import static org.assertj.core.api.Assertions.*;

public class TimeUtilsTest {

    @Test
    public void fullDate_should_parseMillisToFullDateString(){
        assertThat(TimeUtils.fullDate(SampleTimes.S17)).isEqualTo("09/17 12:00AM");
    }
}