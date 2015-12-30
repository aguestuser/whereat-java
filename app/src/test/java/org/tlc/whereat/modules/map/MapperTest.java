package org.tlc.whereat.modules.map;

import android.app.Activity;
import android.util.Pair;

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

import static org.tlc.whereat.util.CollectionUtils.last;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.tlc.whereat.support.LocationHelpers.*;


@RunWith(Enclosed.class)

public class MapperTest {

    protected static final UserLocation s17 = s17UserLocationStub();
    protected static final UserLocation s17_ = s17UserLocationStubMoved();
    protected static final UserLocation n17 = n17UserLocationStub();

    protected static final UserLocation n17_ = n17UserLocationStubMoved();

    protected static final List<UserLocation> noLocs = new ArrayList<>();
    protected static final List<UserLocation> oneLoc = Arrays.asList(s17);
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
            assertThat(m.mMapFactory).isInstanceOf(GoogleMapContainerFactory.class);
            assertThat(m.mMarkers).isEqualTo(new ConcurrentHashMap<>());
            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mRendered).isFalse();
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class Accessors {
        MapActivity ctx;
        Mapper m;
        MapContainer mc;

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
        public void hasRendered_should_returnValueOfRenderedBooleanFlag(){
            m.mRendered = false;
            assertThat(m.hasRendered()).isFalse();

            m.mRendered = true;
            assertThat(m.hasRendered()).isTrue();
        }
    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class Initialization {
        MapActivity ctx;
        Mapper m;
        MapContainer mc;
        MarkerContainer s17mrk;
        MarkerContainer s17_mrk;
        MarkerContainer n17mrk;
        MarkerContainer n17_mrk;

        @Before
        public void setup(){

            ctx = Robolectric.buildActivity(MapActivity.class).get();
            m = spy(new Mapper(ctx));
            mc = mock(MapContainer.class);
            m.mMapFactory = spy(m.mMapFactory);

            doReturn(mc).when(m.mMapFactory).getInstance();
            doReturn(mc).when(mc).getMap();
            doReturn(mc).when(mc).showUserLocation();
            doReturn(mc).when(mc).center(any(LatLon.class));

            s17mrk = mock(MarkerContainer.class);
            s17_mrk = mock(MarkerContainer.class);
            n17mrk = mock(MarkerContainer.class);
            n17_mrk = mock(MarkerContainer.class);

            doReturn(s17mrk).when(mc).addMarker(s17.asLatLon(), s17.asDateTime());
            doReturn(s17_mrk).when(s17mrk).move(s17_.asLatLon());
            doReturn(n17mrk).when(mc).addMarker(n17.asLatLon(), n17.asDateTime());
            doReturn(n17_mrk).when(n17mrk).move(n17_.asLatLon());
        }

        //#render

        @Test
        public void render_should_returnThis(){
            assertThat(m.render(noLocs)).isEqualTo(m);
        }

        @Test
        public void render_whenPassedNoLocs_should_recordNoPingAndRenderMapWithNoMarkers(){

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();

            m.render(noLocs);

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();
            assertThat(m.mMap).isEqualTo(mc);

            verify(mc).showUserLocation();
            verify(mc).center(Mapper.LIBERTY);
            verify(m, never()).plotMany(anyListOf(UserLocation.class));
        }

        @Test
        public void render_whenPassedOneLoc_should_renderMapWithOneMarker(){

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();

            m.render(oneLoc);

            assertThat(m.mMap).isEqualTo(mc);
            assertThat(m.mLastPing).isEqualTo(s17.getTime());
            assertThat(m.mMarkers).hasSize(1);

            verify(mc).showUserLocation();
            verify(mc).center(s17.asLatLon());
            verify(m).plotMany(oneLoc);
            verify(m).plot(s17);
            verify(m).addPlot(s17);
            verify(mc).addMarker(s17.asLatLon(), s17.asDateTime());

            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers.get(s17.getId())).isEqualTo(Pair.create(s17.getTime(), s17mrk));
        }

        @Test
        public void render_whenPassedManyLocs_should_renderMapWithNoMarkers(){

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();

            m.render(locs);

            assertThat(m.mMap).isEqualTo(mc);
            assertThat(m.mLastPing).isEqualTo(n17.getTime());
            assertThat(m.mMarkers).hasSize(2);

            verify(mc).showUserLocation();
            verify(mc).center(last(locs).asLatLon());
            verify(m).plotMany(locs);
            for (UserLocation l : locs){
                verify(m).plot(l);
                verify(m).addPlot(l);
                verify(mc).addMarker(l.asLatLon(), l.asDateTime());
            }

            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers.get(s17.getId())).isEqualTo(Pair.create(s17.getTime(), s17mrk));
            assertThat(m.mMarkers).containsKey(n17.getId());
            assertThat(m.mMarkers.get(n17.getId())).isEqualTo(Pair.create(n17.getTime(), n17mrk));
        }

        // #refresh

        @Test
        public void refresh_whenPassedNoLocs_should_addNoMarkersToMap(){

            m.render(noLocs);

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();

            m.refresh(noLocs);

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();
        }

        @Test
        public void refresh_whenEmptyAndPassedOneLoc_should_addOneMarkerToMap(){

            m.render(noLocs);

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();

            m.refresh(oneLoc);

            assertThat(m.mLastPing).isEqualTo(s17.getTime());
            assertThat(m.mMarkers).hasSize(1);

            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers.get(s17.getId())).isEqualTo(Pair.create(s17.getTime(), s17mrk));
        }

        @Test
        public void refresh_whenNotEmptyAndPassedOneLoc_should_addOneMarkerToMap(){

            m.render(oneLoc);

            assertThat(m.mLastPing).isEqualTo(s17.getTime());
            assertThat(m.mMarkers).hasSize(1);
            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers).doesNotContainKey(n17.getId());

            m.refresh(Arrays.asList(n17));

            assertThat(m.mLastPing).isEqualTo(n17.getTime());
            assertThat(m.mMarkers).hasSize(2);
            assertThat(m.mMarkers).containsKey(n17.getId());
            assertThat(m.mMarkers.get(n17.getId())).isEqualTo(Pair.create(n17.getTime(), n17mrk));
        }

        @Test
        public void refresh_whenNotEmptyAndPassedManyLocs_should_moveMarkers(){

            m.render(locs);

            assertThat(m.mLastPing).isEqualTo(n17.getTime());
            assertThat(m.mMarkers).hasSize(2);
            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers).containsKey(n17.getId());

            m.refresh(movedLocs);

            assertThat(m.mLastPing).isEqualTo(n17_.getTime());
            assertThat(m.mMarkers).hasSize(2);

            verify(m, never()).addPlot(s17_);
            verify(m).rePlot(s17_);
            verify(s17mrk).move(s17_.asLatLon());

            verify(m, never()).addPlot(n17_);
            verify(m).rePlot(n17_);
            verify(n17mrk).move(n17_.asLatLon());

            assertThat(m.mMarkers.get(s17.getId())).isEqualTo(Pair.create(s17_.getTime(), s17_mrk));
            /*assertThat(m.mMarkers.get(n17.getId())).isEqualTo(Pair.create(n17_.getTime(), n17_mrk));*/
        }

        // #record

        @Test
        public void record_whenEmpty_shouldAddOneMarker(){

            m.render(noLocs);

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();

            m.record(s17);

            assertThat(m.mLastPing).isEqualTo(s17.getTime());
            assertThat(m.mMarkers).hasSize(1);
            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers.get(s17.getId())).isEqualTo(Pair.create(s17.getTime(), s17mrk));
        }

        @Test
        public void record_whenNotEmptyAndPassedNewLoc_shouldAddOneMarker(){

            m.render(oneLoc);

            assertThat(m.mLastPing).isEqualTo(s17.getTime());
            assertThat(m.mMarkers).hasSize(1);
            assertThat(m.mMarkers).doesNotContainKey(n17.getId());

            m.record(n17);

            assertThat(m.mLastPing).isEqualTo(n17.getTime());
            assertThat(m.mMarkers).hasSize(2);
            assertThat(m.mMarkers).containsKey(n17.getId());
            assertThat(m.mMarkers.get(n17.getId())).isEqualTo(Pair.create(n17.getTime(), n17mrk));
        }

        @Test
        public void record_whenNotEmptyAndPassedOldLoc_shouldMoveMarker(){

            m.render(oneLoc);

            assertThat(m.mLastPing).isEqualTo(s17.getTime());
            assertThat(m.mMarkers).hasSize(1);
            assertThat(m.mMarkers).containsKey(s17.getId());

            m.record(s17_);

            verify(s17mrk).move(s17_.asLatLon());

            assertThat(m.mLastPing).isEqualTo(s17_.getTime());
            assertThat(m.mMarkers).hasSize(1);
            assertThat(m.mMarkers.get(s17.getId())).isEqualToComparingFieldByField(Pair.create(s17_.getTime(), s17_mrk));
        }

        // # forgetSince

        @Test

        public void forgetSince_whenEmpty_should_doNothing(){

            m.render(noLocs);
            assertThat(m.mMarkers).isEmpty();

            m.forgetSince(s17.getTime());
            assertThat(m.mMarkers).isEmpty();
        }



        @Test
        public void forgetSince_whenOneExpiredLoc_should_removeOneMarker(){

            m.render(oneLoc);

            assertThat(m.mMarkers).hasSize(1);
            assertThat(m.mMarkers).containsKey(s17.getId());

            m.forgetSince(n17.getTime());

            assertThat(m.mMarkers).isEmpty();
            assertThat(m.mMarkers).doesNotContainKey(s17.getId());
        }

        @Test
        public void forgetSince_whenTwoExpiredLocs_should_removeTwoMarkers(){

            m.render(locs);

            assertThat(m.mMarkers).hasSize(2);
            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers).containsKey(n17.getId());

            m.forgetSince(n17.getTime() + 1L);

            assertThat(m.mMarkers).isEmpty();
            assertThat(m.mMarkers).doesNotContainKey(s17.getId());
            assertThat(m.mMarkers).doesNotContainKey(n17.getId());
        }

        @Test
        public void forgetSince_whenOneExpiredOneUnexpiredLoc_should_removeOneMarker(){

            m.render(locs);

            assertThat(m.mMarkers).hasSize(2);
            assertThat(m.mMarkers).containsKey(s17.getId());
            assertThat(m.mMarkers).containsKey(n17.getId());

            m.forgetSince(n17.getTime());

            assertThat(m.mMarkers).hasSize(1);
            assertThat(m.mMarkers).doesNotContainKey(s17.getId());
            assertThat(m.mMarkers).containsKey(n17.getId());
        }

        // #clear

        @Test
        public void clear_whenEmpty_should_doNothing(){

            m.render(noLocs);

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();

            m.clear();

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();
        }

        @Test
        public void clear_whenNotEmpty_should_removeMarkersAndResetLastPing(){

            m.render(locs);

            assertThat(m.mLastPing).isEqualTo(n17.getTime());
            assertThat(m.mMarkers).hasSize(2);

            m.clear();

            assertThat(m.mLastPing).isEqualTo(-1L);
            assertThat(m.mMarkers).isEmpty();
        }
    }
}