package org.tlc.whereat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.tlc.whereat.R;

public class MapActivity extends AppCompatActivity {

    private GoogleMap map;
    static final LatLng LIBERTY = new LatLng(40.7092529,-74.0112551);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_main:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.action_map:
                startActivity(new Intent(this, MapActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpMap(){
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        Marker liberty = map.addMarker(new MarkerOptions().position(LIBERTY).title("Liberty"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LIBERTY, 15));
    }
}
