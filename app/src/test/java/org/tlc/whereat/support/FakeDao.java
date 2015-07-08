package org.tlc.whereat.support;

import android.content.Context;

import org.tlc.whereat.db.Dao;

public class FakeDao extends Dao {

    private static final String FAKE_DB_NAME = "whereat-test.db";
    private static FakeDao sInstance;

    public static synchronized FakeDao getInstance(Context ctx){
        if (sInstance == null) sInstance = new FakeDao(ctx);
        return sInstance;
    }

    private FakeDao(Context ctx){ super(ctx, FAKE_DB_NAME); }

}
