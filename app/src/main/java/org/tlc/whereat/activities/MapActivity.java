package org.tlc.whereat.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.tlc.whereat.R;
import org.tlc.whereat.modules.map.Mapper;
import org.tlc.whereat.modules.ui.MenuHandler;
import org.tlc.whereat.services.LocPubManager;
import org.tlc.whereat.modules.pubsub.receivers.MapActivityReceivers;
import org.tlc.whereat.modules.db.LocationDao;
import org.tlc.whereat.model.UserLocation;


public class MapActivity extends AppCompatActivity {

    protected LocPubManager mLocPubMgr;
    protected MapActivityReceivers mReceivers;
    protected LocationDao mLocDao;
    protected Mapper mMapper;
    protected MenuHandler mMenu;
    protected boolean mRunning;

    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLocPubMgr = new LocPubManager(this);
        mReceivers = new MapActivityReceivers(this);
        mLocDao = new LocationDao(this);
        mMapper = new Mapper(this);
        mMenu = new MenuHandler(this);
        mRunning = false;

        findViewById(R.id.refresh_map_button).setOnClickListener((View v) -> refresh());
    }

    @Override
    protected void onResume(){
        super.onResume();

        mLocPubMgr.bind();
        mReceivers.register();

        if(!mRunning) run();
        else mMapper.refresh(mLocDao.getAllSince(mMapper.lastPing()));
    }

    @Override
    protected void onPause(){
        super.onPause();
        mLocPubMgr.unbind();
        mReceivers.unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapper.clear();
        mLocDao.disconnect();
        mLocPubMgr.stop();
    }

    // EVENT HANDLERS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenu.create(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenu.select(item, super::onOptionsItemSelected);
    }

    // PUBLIC METHODS

    public void map(UserLocation ul){
        mMapper.map(ul);
    }

    public void forgetSince(long time) {
        mMapper.forgetSince(time);
        mLocDao.forgetSince(time);
    }

    // HELPER METHODS

    protected void run(){
        mLocPubMgr.start();
        mLocDao.connect();
        mMapper.initialize(mLocDao.getAll());
        mRunning = true;
    }

    protected void refresh(){
        mLocDao.clear();
        mMapper.clear();
        mLocPubMgr.ping();
    }
}
