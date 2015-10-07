package org.tlc.whereat.modules;

import android.app.Activity;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.activities.MapActivity;
import org.tlc.whereat.model.UserLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.tlc.whereat.support.LocationHelpers.*;


@RunWith(Enclosed.class)

public class MapperTest {

    protected static final UserLocation s17 = s17UserLocationStub();
    protected static final UserLocation s17_ = s17UserLocationStubMoved();
    protected static final Marker s17mrk = mock(Marker.class);

    protected static final UserLocation n17 = n17UserLocationStub();
    protected static final UserLocation n17_ = n17UserLocationStubMoved();
    protected static final Marker n17mrk = mock(Marker.class);

    protected static final List<UserLocation> noLocs = new ArrayList<>();
    protected static final List<UserLocation> locs = Arrays.asList(s17, n17);
    protected static final List<UserLocation> movedLocs = Arrays.asList(s17_, n17_);


    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class Constructor {

        @Test
        public void constructor_should_initializeNonMapRelatedFields(){
            Activity ctx = Robolectric.buildActivity(MapActivity.class).get();
            Mapper m = new Mapper(ctx);

            assertThat(m.mCtx).isEqualTo(ctx);
            assertThat(m.mMarkers).isEqualTo(new ConcurrentHashMap<>());
            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mInitialized).isFalse();
        }

    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class Initialization {
        MapActivity ctx;
        GoogleMap map;
        Mapper m;

        @Before
        public void setup(){
            ctx = Robolectric.buildActivity(MapActivity.class).get();
            map = mock(GoogleMap.class);
            m = spy(new Mapper(ctx));
            doReturn(map).when(m).getMap();
            doNothing().when(m).initCenter(any(LatLng.class));
        }


        @Test
        public void initialize_should_callInitMapAndRecordLastPingAndSetInitializedToTrue(){
            doNothing().when(m).initMap(locs);
            doNothing().when(m).recordLastPing(locs);

            m.initialize(locs);

            verify(m).initMap(locs);
            verify(m).recordLastPing(locs);
            assertThat(m.mInitialized).isTrue();
        }

        @Test
        public void initMap_should_initializeMapWithMarkers(){
            m.initMap(locs);

            assertThat(m.mMap).isEqualTo(map);
            verify(map).setMyLocationEnabled(true);
            verify(m).initCenter(n17);
        }

        @Test
        public void initMap_should_initializeMapWithNoMarkers(){
            m.initMap(new ArrayList<>());

            assertThat(m.mMap).isEqualTo(map);
            verify(map).setMyLocationEnabled(true);
            verify(m).initCenter(Mapper.LIBERTY);
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class Getters {
        MapActivity ctx;
        Mapper m;

        @Before
        public void setup() {
            ctx = Robolectric.buildActivity(MapActivity.class).get();
            m = spy(new Mapper(ctx));
        }

        @Test
        public void lastPing_should_returnValueOfLastPing(){
            m.mLastPing = -1L;
            assertThat(m.lastPing()).isEqualTo(-1L);

            m.mLastPing = 0L;
            assertThat(m.lastPing()).isEqualTo(0L);
        }

        @Test
        public void hasPinged_should_returnTrueIfLastPingGreaterThanSentinelValue(){
            m.mLastPing = -1L;
            assertThat(m.hasPinged()).isFalse();

            m.mLastPing = 0L;
            assertThat(m.hasPinged()).isTrue();
        }

        @Test
        public void hasInitialized_should_returnTrueIfMapperHasInitialized(){
            doNothing().when(m).initMap(anyListOf(UserLocation.class));
            doNothing().when(m).recordLastPing(anyListOf(UserLocation.class));

            assertThat(m.hasInitialized()).isFalse();
            m.initialize(noLocs);
            assertThat(m.hasInitialized()).isTrue();
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class PublicMethods {
        MapActivity ctx;
        GoogleMap map;
        Mapper m;
        ConcurrentHashMap<String, Pair<Long, Marker>> mMarkers;


        @Before
        public void setup() {
            ctx = Robolectric.buildActivity(MapActivity.class).get();
            map = mock(GoogleMap.class);
            m = spy(new Mapper(ctx));

            doNothing().when(m).initCenter(any(LatLng.class));
            doReturn(map).when(m).getMap();

            doReturn(s17mrk).when(m).addMarker(s17);
            doReturn(n17mrk).when(m).addMarker(n17);
        }

        @After
        public void teardown(){
            reset(s17mrk);
            reset(n17mrk);
        }

        //#refresh()

        @Test
        public void refresh_should_callPlotManyAndRecordLastPing(){
            doNothing().when(m).plotMany(anyListOf(UserLocation.class));
            doNothing().when(m).recordLastPing(anyListOf(UserLocation.class));

            m.initialize(noLocs);
            m.refresh(locs);

            verify(m).plotMany(locs);
            verify(m).recordLastPing(locs);
        }

        @Test
        public void plotMany_should_doNothingIfPassedEmptyList(){
            m.initialize(noLocs);
            m.plotMany(noLocs);

            verify(m, never()).plot(any(UserLocation.class));
        }

        @Test
        public void plotMany_should_addManyMarkersToEmptyMap(){
            m.initialize(noLocs);
            m.plotMany(locs);

            verify(m).plot(s17);
            verify(m).plot(n17);

            verify(m).addPlot(s17);
            verify(m).addPlot(n17);

            assertThat(m.mMarkers.size()).isEqualTo(2);
            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers).containsKey(n17.getId());

            Pair<Long, Marker> s17entry = m.mMarkers.get(s17.getId());
            assertThat(s17entry.first).isEqualTo(s17.getTime());
            assertThat(s17entry.second).isEqualTo(s17mrk);
            
            Pair<Long, Marker> n17entry = m.mMarkers.get(n17.getId());
            assertThat(n17entry.first).isEqualTo(n17.getTime());
            assertThat(n17entry.second).isEqualTo(n17mrk);
        }

        @Test
        public void plotMany_should_replotManyMarkersOnExistingMap(){
            m.initialize(noLocs);
            m.plotMany(locs);
            assertThat(m.mMarkers).hasSize(2);
            m.plotMany(movedLocs);
            
            verify(m).plot(s17_);
            verify(m).plot(n17_);

            verify(m).rePlot(s17_);
            verify(m).rePlot(n17_);

            assertThat(m.mMarkers).hasSize(2);

            assertThat(m.mMarkers.get(s17.getId()).first).isEqualTo(s17_.getTime());
            assertThat(m.mMarkers.get(s17.getId()).second).isEqualTo(s17mrk);
            verify(s17mrk).setPosition(s17_.asLatLng());

            assertThat(m.mMarkers.get(n17.getId()).first).isEqualTo(n17_.getTime());
            assertThat(m.mMarkers.get(n17.getId()).second).isEqualTo(n17mrk);
            verify(n17mrk).setPosition(n17_.asLatLng());
        }

        @Test
        public void recordLastPing_should_doNothingIfPassedEmptyList(){
            m.initialize(noLocs);
            m.recordLastPing(noLocs);

            verify(m, never()).recordPing(any(UserLocation.class));
        }

        @Test
        public void recordLastPing_should_setLastPingToValueOfLastLocInList(){
            m.initialize(noLocs);
            m.recordLastPing(locs);

            verify(m).recordPing(n17);
            assertThat(m.mLastPing).isEqualTo(n17.getTime());
        }

        @Test
        public void recordLastPing_should_updateLastPingOnRefresh(){
            m.initialize(locs);
            m.recordLastPing(movedLocs);

            verify(m).recordPing(n17_);
            assertThat(m.mLastPing).isEqualTo(n17_.getTime());
        }

        //#map()

        @Test
        public void map_should_callPlotAndRecordPing(){
            doReturn(true).when(m).plot(any(UserLocation.class));
            doNothing().when(m).recordPing(any(UserLocation.class));

            m.initialize(noLocs);
            m.map(s17);

            verify(m).plot(s17);
            verify(m).recordPing(s17);
        }

        @Test
        public void plot_should_addMarkerToEmptyMap(){
            m.initialize(noLocs);
            m.plot(s17);

            verify(m).addPlot(s17);

            assertThat(m.mMarkers).hasSize(1);
            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers.get(s17.getId()).first).isEqualTo(s17.getTime());
            assertThat(m.mMarkers.get(s17.getId()).second).isEqualTo(s17mrk);
        }

        @Test
        public void plot_should_addMarkerToMapWithOtherMarker(){
            m.initialize(Arrays.asList(s17));
            m.plot(n17);

            verify(m).addPlot(n17);

            assertThat(m.mMarkers).hasSize(2);
            assertThat(m.mMarkers).containsKey(s17.getId());

            assertThat(m.mMarkers).containsKey(n17.getId());
            assertThat(m.mMarkers.get(n17.getId()).first).isEqualTo(n17.getTime());
            assertThat(m.mMarkers.get(n17.getId()).second).isEqualTo(n17mrk);
        }

        @Test
        public void plot_should_replotExistingMarker(){
            m.initialize(locs);
            m.plot(n17_);

            verify(m).rePlot(n17_);

            assertThat(m.mMarkers).hasSize(2);
            assertThat(m.mMarkers.get(n17.getId()).first).isEqualTo(n17_.getTime());
            assertThat(m.mMarkers.get(n17.getId()).second).isEqualTo(n17mrk);
            verify(n17mrk).setPosition(n17_.asLatLng());
        }

        @Test
        public void recordPing_should_setLastPingToTimeUserLocationWasPublished(){
            m.initialize(noLocs);

            m.recordPing(s17);
            assertThat(m.mLastPing).isEqualTo(s17.getTime());

            m.recordPing(s17_);
            assertThat(m.mLastPing).isEqualTo(s17_.getTime());
        }

        //#clear

        @Test
        public void clear_should_clearMapAndSetLastPingToSentinalValue(){
            m.initialize(locs);

            m.clear();

            verify(map).clear();
            assertThat(m.mMarkers).isEmpty();
            assertThat(m.mLastPing).isEqualTo(-1L);
        }

        //#forgetSince()

        @Test
        public void forgetSince_should_doNothingIfNoLocsOlderThanExpiration(){
            m.initialize(locs);
            assertThat(m.mMarkers).hasSize(2);

            m.forgetSince(s17.getTime());
            assertThat(m.mMarkers).hasSize(2);
            verify(s17mrk, never()).remove();
            verify(n17mrk, never()).remove();
        }

        @Test
        public void forgetSince_should_deleteLocsOlderThanExpiration(){
            m.initialize(locs);
            assertThat(m.mMarkers).hasSize(2);

            m.forgetSince(n17.getTime());
            assertThat(m.mMarkers).hasSize(1);
            verify(s17mrk).remove();

            m.forgetSince(n17_.getTime());
            assertThat(m.mMarkers).isEmpty();
            verify(n17mrk).remove();
        }
    }
}