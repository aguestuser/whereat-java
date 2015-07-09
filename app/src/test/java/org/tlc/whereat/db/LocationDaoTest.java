package org.tlc.whereat.db;

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.support.FakeLocationDao;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.LocationHelpers.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;



@RunWith(Enclosed.class)

public class LocationDaoTest {

    public static class NoRecords {

        private FakeLocationDao mLocDao;
        private Dao mMockDao;
        private SQLiteDatabase mMockDb;

        @Before
        public void setup(){
            mMockDao = mock(Dao.class);
            mMockDb = mock(SQLiteDatabase.class);
            when(mMockDao.getWritableDatabase()).thenReturn(mMockDb);

            mLocDao = new FakeLocationDao(RuntimeEnvironment.application)
                .setDao(mMockDao);
        }

        @Test
        public void connect_should_connectToDatabase(){
            mLocDao.connect();

            verify(mMockDao, times(1)).getWritableDatabase();
            assertThat(mLocDao.getDb()).isEqualTo(mMockDb);
        }

        @Test
        public void disconnect_should_disconnectFromDatabase(){
            mLocDao.setDb(mMockDb);
            mLocDao.disconnect();

            verify(mMockDao, times(1)).close();
            verify(mMockDb, times(1)).close();
        }

    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class)

    public static class OneRecord {

        private FakeLocationDao mLocDao;

        @Before
        public void setup(){
            mLocDao = new FakeLocationDao(RuntimeEnvironment.application);
            mLocDao.connect();
        }

        @After
        public void teardown(){
            mLocDao.clear();
            mLocDao.disconnect();
        }

        @Test
        public void save_should_saveLocationToDb(){
            Location l = s17AndroidLocationMock();
            assertThat(mLocDao.getAll().size()).isEqualTo(0);

            mLocDao.save(l);
            List<Location> saved = mLocDao.getAll();

            assertThat(saved.size()).isEqualTo(1);
            assertTrue(areEqual(l, saved.get(0)));
        }

        @Test
        public void get_should_retrieveLocationFromDb(){
            Location l = s17AndroidLocationMock();
            mLocDao.save(l);
            Location retrieved = mLocDao.get(1L);

            assertTrue(areEqual(l, retrieved));
        }

        @Test
        public void count_should_countHowManyLocationsAreInTheDb(){
            Location s17 = s17AndroidLocationMock();
            Location n17 = n17AndroidLocationMock();

            mLocDao.save(s17);
            assertThat(mLocDao.count()).isEqualTo(1);

            mLocDao.save(n17);
            assertThat(mLocDao.count()).isEqualTo(2);
        }

    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class)

    public static class ManyRecords {

        private FakeLocationDao mLocDao;
        private Location s17 = s17AndroidLocationMock();
        private Location n17 = n17AndroidLocationMock();

        @Before
        public void setup() {
            mLocDao = new FakeLocationDao(RuntimeEnvironment.application);
            mLocDao.connect();
            mLocDao.save(s17);
            mLocDao.save(n17);
        }

        @After
        public void teardown() {
            mLocDao.clear();
            mLocDao.disconnect();
        }

        @Test
        public void getAll_should_retrieveAllLocationsFromDb(){
            List<Location> all = mLocDao.getAll();

            assertTrue(areEqual(all.get(0), s17));
            assertTrue(areEqual(all.get(1), n17));
        }

        @Test
        public void getAllSince_should_retrieveAllLocationsNewerThanX(){
            List<Location> all = mLocDao.getAllSince(S17_MILLIS + 1);

            assertThat(all.size()).isEqualTo(1);
            assertFalse(areEqual(all.get(0), s17));
            assertTrue(areEqual(all.get(0), n17));
        }

        @Test
        public void clear_should_clearTheDb(){
            int deleted = mLocDao.clear();

            assertThat(deleted).isEqualTo(2);
            assertTrue(mLocDao.getAll().isEmpty());
            assertThat(mLocDao.count()).isEqualTo(0);
        }
    }

}