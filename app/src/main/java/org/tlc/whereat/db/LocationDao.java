package org.tlc.whereat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public LocationDao connect() {
        try {
            tryConnect();
        } catch (SQLException e) {
            Log.e(TAG, "Error connecting to DB.");
            e.printStackTrace();
        }
        return this;
    }

    public void disconnect() {
        mDao.close();
    }

    private void tryConnect() throws SQLException {
        mDao = mDao == null ? Dao.getInstance(mCtx) : mDao;
        mDb = mDao.getWritableDatabase();
    }

    // CRUD

    public long save(Location loc){
        ContentValues vals = new ContentValues();
        vals.put(Dao.COLUMN_LAT, loc.getLatitude());
        vals.put(Dao.COLUMN_LON, loc.getLongitude());
        vals.put(Dao.COLUMN_TIME, loc.getTime());

        return mDb.insert(Dao.TABLE_LOCATIONS, null, vals);
    }

    public List<Location> getAll(){
        Cursor c = mDb.query(Dao.TABLE_LOCATIONS, mAllColumns, null, null, null, null, null);
        return parseLocations(c);
    }

    public List<Location> getAllSince(long t){
        Cursor c = mDb.rawQuery(
            "select * from " + Dao.TABLE_LOCATIONS +
                " where " + Dao.COLUMN_TIME + " > " + String.valueOf(t) + ";",
            null);
        return parseLocations(c);
    }

    public void clear(){
        mDb.delete(Dao.TABLE_LOCATIONS,null,null);
    }

    // HELPERS

    private List<Location> parseLocations(Cursor c){
        List<Location> ls = new ArrayList<>();

        c.moveToFirst();
        while(!c.isAfterLast()){
            Location l = parseLocation(c);
            ls.add(l);
            c.moveToNext();
        }
        c.close();

        return ls;
    }

    private Location parseLocation(Cursor c){
        Location l = new Location("");
        l.setLatitude(c.getDouble(1));
        l.setLongitude(c.getDouble(2));
        l.setTime(c.getLong(3));
        return l;
    }

}
