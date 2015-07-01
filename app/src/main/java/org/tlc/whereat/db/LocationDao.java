package org.tlc.whereat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.sql.SQLException;

public class LocationDao {

    // FIELDS

    private SQLiteDatabase mDb;
    private Dao mDao;
    private String[] mAllColumns = {
        Dao.COLUMN_ID,
        Dao.COLUMN_LAT,
        Dao.COLUMN_LON,
        Dao.COLUMN_TIME
    };

    // CONSTRUCTOR

    public LocationDao(Context ctx){
        mDao = new Dao(ctx);
    }

    // PUBLIC METHODS

    public void open() throws SQLException {
        mDb = mDao.getWritableDatabase();
    }

    public void close() {
        mDao.close();
    }

    // CRUD

    public long saveLocation(Location loc){
        ContentValues vals = new ContentValues();
        vals.put(Dao.COLUMN_LAT, loc.getLatitude());
        vals.put(Dao.COLUMN_LON, loc.getLongitude());
        vals.put(Dao.COLUMN_TIME, loc.getTime());

        return mDb.insert(Dao.TABLE_LOCATIONS, null, vals);
    }

}
