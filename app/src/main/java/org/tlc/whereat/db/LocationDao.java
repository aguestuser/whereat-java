package org.tlc.whereat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.tlc.whereat.model.UserLocation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationDao {

    // FIELDS

    public static final String TAG = LocationDao.class.getSimpleName();
    protected Context mCtx;
    protected SQLiteDatabase mDb;
    protected Dao mDao;
    protected String[] mAllColumns = {
        Dao.COLUMN_ID,
        Dao.COLUMN_LAT,
        Dao.COLUMN_LON,
        Dao.COLUMN_TIME };
    protected boolean mConnected;

    // CONSTRUCTOR

    public LocationDao(Context ctx){
        mCtx = ctx;
        mDao = Dao.getInstance(ctx);
    }

    // GETTERS

    public boolean isConnected(){
        return mConnected;
    }

    // PUBLIC METHODS

    public LocationDao connect() {
        try {
            tryConnect();
            mConnected = true;
        } catch (SQLException e) {
            Log.e(TAG, "Error connecting to DB.");
            e.printStackTrace();
        }
        return this;
    }

    public void disconnect() {
        mDao.close();
        mDb.close();
    }

    private void tryConnect() throws SQLException {
        mDb = mDao.getWritableDatabase();
    }

    // CRUD

    public long save(UserLocation loc){
        return mDb.replace(Dao.TABLE_LOCATIONS, null, parseRow(loc));
    }

    public UserLocation get(String id){
        Cursor c = mDb.query(Dao.TABLE_LOCATIONS, mAllColumns, idEquals(id), null, null, null, null);
        return parseUserLocation(c);
    }

    public List<UserLocation> getAll(){
        Cursor c = mDb.query(Dao.TABLE_LOCATIONS, mAllColumns, null, null, null, null, null);
        return parseUserLocations(c);
    }

    public List<UserLocation> getAllSince(long t){
        Cursor c = mDb.query(Dao.TABLE_LOCATIONS, mAllColumns, timeGreaterThan(t), null, null, null, null, null);
        return parseUserLocations(c);
    }

    public int delete(String id) {
        return mDb.delete(Dao.TABLE_LOCATIONS, idEquals(id), null);
    }

    public int forgetSince(long t) {
        return mDb.delete(Dao.TABLE_LOCATIONS, timeLessThan(t), null);
    }

    public int clear(){
        return mDb.delete(Dao.TABLE_LOCATIONS,null,null);
    }

    public long count(){
        return DatabaseUtils.queryNumEntries(mDb, Dao.TABLE_LOCATIONS);
    }

    // HELPERS

    protected ContentValues parseRow(UserLocation loc){
        ContentValues vals = new ContentValues();
        vals.put(Dao.COLUMN_ID, loc.getId());
        vals.put(Dao.COLUMN_LAT, loc.getLatitude());
        vals.put(Dao.COLUMN_LON, loc.getLongitude());
        vals.put(Dao.COLUMN_TIME, loc.getTime());

        return vals;
    }

    protected UserLocation parseUserLocation(Cursor c){
        c.moveToFirst();
        UserLocation l = doParseUserLocation(c);
        c.close();

        return l;
    }

    protected List<UserLocation> parseUserLocations(Cursor c){
        List<UserLocation> ls = new ArrayList<>();

        c.moveToFirst();
        while(!c.isAfterLast()){
            UserLocation l = doParseUserLocation(c);
            ls.add(l);
            c.moveToNext();
        }
        c.close();

        return ls;
    }

    protected UserLocation doParseUserLocation(Cursor c){
        //TODO use a builder here for greater type safety?
        return UserLocation.create(
            c.getString(0), //id
            c.getDouble(1), //lat
            c.getDouble(2), //lon
            c.getLong(3)    //time
        );
    }

    protected static String idEquals(String id){
        return String.format("%s = '%s'", Dao.COLUMN_ID, id);
    }

    protected static String timeGreaterThan(long t){
        return String.format("%s > %s", Dao.COLUMN_TIME, t);
    }

    protected static String timeLessThan(long t) {
        return String.format("%s < %s", Dao.COLUMN_TIME, t);
    }

}
