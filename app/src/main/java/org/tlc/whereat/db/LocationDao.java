package org.tlc.whereat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.nfc.Tag;
import android.util.Log;

import java.sql.SQLException;

public class LocationDao {

    // FIELDS

    public static final String TAG = LocationDao.class.getSimpleName();
    protected SQLiteDatabase mDb;
    private Dao mDao;
    private String[] mAllColumns = {
        Dao.COLUMN_ID,
        Dao.COLUMN_LAT,
        Dao.COLUMN_LON,
        Dao.COLUMN_TIME
    };
    private Context mCtx;

    // CONSTRUCTOR

    public LocationDao(Context ctx){
        mCtx = ctx;
        mDao = Dao.getInstance(ctx);
    }

    // PUBLIC METHODS

    public void open() {
        try {
            tryOpen();
        } catch (SQLException e) {
            Log.e(TAG, "Error connecting to DB.");
            e.printStackTrace();
        }
    }

    public void close() {
        mDao.close();
    }

    private void tryOpen() throws SQLException {
        if (mDao == null) mDao = Dao.getInstance(mCtx);
        mDb = mDao.getWritableDatabase();
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
