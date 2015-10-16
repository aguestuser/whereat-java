package org.tlc.whereat.support;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.tlc.whereat.modules.db.Dao;
import org.tlc.whereat.modules.db.LocationDao;

public class FakeLocationDao extends LocationDao {

    public FakeLocationDao(Context ctx){
        super(ctx);
        mDao = FakeDao.getInstance(ctx);
    }

    public FakeLocationDao setDao(Dao dao){
        mDao = dao;
        return this;
    }

    public FakeLocationDao setDb(SQLiteDatabase db){
        mDb = db;
        return this;
    }

    public SQLiteDatabase getDb(){
        return mDb;
    }
}
