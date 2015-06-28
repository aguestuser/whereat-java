package org.tlc.whereat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.tlc.whereat.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button shareLocationButton = (Button) findViewById(R.id.go_button);
        shareLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastLocationShared();
            }
        });

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

    private void toastLocationShared(){
        Toast.makeText(getApplicationContext(), "Location shared.", Toast.LENGTH_SHORT).show();
    }
}
