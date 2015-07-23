package org.tlc.whereat.activities;

import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.pubsub.LocationSubscriberMap;
import org.tlc.whereat.support.FakeMapActivity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.tlc.whereat.support.ActivityHelpers.*;
import org.tlc.whereat.R;


/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)

public class MapActivityTest {

    @Test
    public void pressingClearMapButton_should_clearTheMap(){

        GoogleMap mockMap = mock(GoogleMap.class);
        LocationDao mockDao = mock(LocationDao.class);
        Marker m = mock(Marker.class);
        ConcurrentHashMap<String,Marker> stubMarkers = new ConcurrentHashMap<>();
        stubMarkers.put("fakeId", m);
        FakeMapActivity a = createActivity(FakeMapActivity.class)
            .setMap(mockMap)
            .setLocDao(mockDao)
            .setMarkers(stubMarkers);

        Button clear = (Button) a.findViewById(R.id.clear_map_button);
        clear.performClick();

        verify(mockMap, times(1)).clear();
        verify(mockDao, times(1)).clear();
        assertThat(stubMarkers).isEmpty();
    }

}