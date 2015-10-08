package org.tlc.whereat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.tlc.whereat.R;
import org.tlc.whereat.modules.Mapper;
import org.tlc.whereat.pubsub.LocPubManager;
import org.tlc.whereat.receivers.MapActivityReceivers;
import org.tlc.whereat.db.LocationDao;
import org.tlc.whereat.model.UserLocation;


public class MapActivity extends AppCompatActivity {

    protected LocPubManager mLocPub;
    protected MapActivityReceivers mReceivers;
    protected LocationDao mLocDao;
    protected Mapper mMapper;


    // LIFE CYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLocPub = new LocPubManager(this);
        mReceivers = new MapActivityReceivers(this);
        mLocDao = new LocationDao(this);
        mMapper = new Mapper(this);

        findViewById(R.id.clear_map_button).setOnClickListener((View v) -> clear());
    }

    @Override
    protected void onResume(){
        super.onResume();

        mLocPub.bind();
        mReceivers.register();

        if(!mLocDao.isConnected()) mLocDao.connect();
        if(!mMapper.hasInitialized()) mMapper.initialize(mLocDao.getAll());
        if(mMapper.hasPinged()) mMapper.refresh(mLocDao.getAllSince(mMapper.lastPing()));
    }

    @Override
    protected void onPause(){
        super.onPause();
        mLocPub.unbind();
        mReceivers.unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapper.clear();
        mLocDao.disconnect();
    }

    // EVENT HANDLERS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_main:
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // PUBLIC METHODS

    public void map(UserLocation ul){
        mMapper.map(ul);
    }

    public void clear(){
        mMapper.clear();
        mLocPub.clear();
        mLocDao.clear();
    }

    public void forgetSince(long time) {
        mMapper.forgetSince(time);
        mLocDao.forgetSince(time);
    }

}
