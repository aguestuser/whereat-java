package org.tlc.whereat.modules.db;

import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.model.UserLocation;
import org.tlc.whereat.support.FakeLocationDao;
import org.tlc.whereat.support.SampleTimes;

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
        public void isConnected_should_getConnectedField(){
            mLocDao.mConnected = false;
            assertThat(mLocDao.isConnected()).isFalse();

            mLocDao.mConnected = true;
            assertThat(mLocDao.isConnected()).isTrue();
        }

        @Test
        public void connect_should_connectToDatabase(){
            assertThat(mLocDao.mConnected).isFalse();
            mLocDao.connect();

            verify(mMockDao, times(1)).getWritableDatabase();
            assertThat(mLocDao.getDb()).isEqualTo(mMockDb);
            assertThat(mLocDao.mConnected).isTrue();
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
    @Config(constants = BuildConfig.class, sdk = 21)

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
        public void save_should_saveANewLocation(){
            UserLocation l = s17UserLocationStub();
            assertThat(mLocDao.getAll().size()).isEqualTo(0);

            long res = mLocDao.save(l);

            assertThat(res).isEqualTo(1L);
            assertThat(mLocDao.count()).isEqualTo(1);
            assertTrue(l.equals(mLocDao.getAll().get(0)));
        }

        @Test
        public void save_should_overWriteAnOldLocation(){
            UserLocation l1 = UserLocation.create(S17_UUID, S17_LAT, S17_LON, S17_MILLIS);
            UserLocation l2 = UserLocation.create(S17_UUID, N17_LAT, N17_LON, N17_MILLIS);

            long res = mLocDao.save(l1);

            assertThat(res).isEqualTo(1L);
            assertThat(mLocDao.count()).isEqualTo(1);
            assertTrue(mLocDao.get(S17_UUID).equals(l1));

            long res2 = mLocDao.save(l2);

            assertThat(res2).isEqualTo(2L);
            assertThat(mLocDao.count()).isEqualTo(1);
            assertThat(mLocDao.getAll().size()).isEqualTo(1);
            assertTrue(mLocDao.get(S17_UUID).equals(l2));
        }


        @Test
        public void get_should_retrieveLocationFromDb(){
            UserLocation ul = s17UserLocationStub();
            mLocDao.save(ul);
            UserLocation retrieved = mLocDao.get(S17_UUID);

            assertTrue(areEqual(ul, retrieved));
        }

        @Test
        public void count_should_countHowManyLocationsAreInTheDb(){
            UserLocation s17 = s17UserLocationStub();
            UserLocation n17 = n17UserLocationStub();

            mLocDao.save(s17);
            assertThat(mLocDao.count()).isEqualTo(1);

            mLocDao.save(n17);
            assertThat(mLocDao.count()).isEqualTo(2);
        }

    }

    @RunWith(RobolectricGradleTestRunner.class)
    @Config(constants = BuildConfig.class, sdk = 21)

    public static class ManyRecords {

        private FakeLocationDao mLocDao;
        private UserLocation s17 = s17UserLocationStub();
        private UserLocation n17 = n17UserLocationStub();

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
            List<UserLocation> all = mLocDao.getAll();

            assertTrue(areEqual(all.get(0), s17));
            assertTrue(areEqual(all.get(1), n17));
        }

        @Test
        public void getAllSince_should_retrieveAllLocationsNewerThanX(){
            List<UserLocation> all = mLocDao.getAllSince(S17_MILLIS + 1);

            assertThat(all.size()).isEqualTo(1);
            assertFalse(areEqual(all.get(0), s17));
            assertTrue(areEqual(all.get(0), n17));
        }

        @Test
        public void delete_should_deleteOneRecord(){
            assertThat(mLocDao.count()).isEqualTo(2);
            int deleteCount = mLocDao.delete(s17.getId());

            assertThat(deleteCount).isEqualTo(1);
            assertThat(mLocDao.count()).isEqualTo(1);
        }

        @Test
        public void forgetSince_should_deleteRecordsOlderThanAnExpiryDate(){
            assertThat(mLocDao.count()).isEqualTo(2);

            int deleteCount1 = mLocDao.forgetSince(SampleTimes.S17 + 1L);
            assertThat(deleteCount1).isEqualTo(1);
            assertThat(mLocDao.count()).isEqualTo(1);

            int deleteCount2 = mLocDao.forgetSince(SampleTimes.N17);
            assertThat(deleteCount2).isEqualTo(0);
            assertThat(mLocDao.count()).isEqualTo(1);
        }


        @Test
        public void clear_should_clearTheDb(){
            int deleted = mLocDao.clear();

            assertThat(deleted).isEqualTo(2);
            assertTrue(mLocDao.getAll().isEmpty());
            assertThat(mLocDao.count()).isEqualTo(0);
        }
    }

    public static class Helpers {

        @Test
        public void timeGreaterThan_should_generateCorrectSqlQuery(){
            assertThat(LocationDao.timeGreaterThan(SampleTimes.S17))
                .isEqualTo("time > " + SampleTimes.S17_STR);
        }

        @Test
        public void timeLessThan_should_generateCorrectSqlQuery(){
            assertThat(LocationDao.timeLessThan(SampleTimes.S17))
                .isEqualTo("time < " + SampleTimes.S17_STR);
        }

        @Test
        public void idEquals_should_generateCorrectSqlQuery(){
            assertThat(LocationDao.idEquals("1"))
                .isEqualTo("_id = '1'");
        }
    }

}