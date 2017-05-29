package thefloowtt.giacomo.com.thefloowtt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.ToggleButton;

public class Settings extends AppCompatActivity {
    private Switch track;
    private static final String TAG = Settings.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        track = (Switch) findViewById(R.id.tracking);
        SharedPreferences sharedPref = getSharedPreferences("tracking",MODE_PRIVATE);
        String isAppTracking = sharedPref.getString("track","null");
        if(isAppTracking.equalsIgnoreCase("on")){
            track.setChecked(true);
        }else{
            track.setChecked(false);
        }
        track.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(track.isChecked()){
                    Log.i(TAG,"on");
                    SharedPreferences sharedPref = getSharedPreferences("tracking",MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString("track","on");
                    prefEditor.commit();
                }else{
                    Log.i(TAG,"off");
                    SharedPreferences sharedPref = getSharedPreferences("tracking",MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString("track","off");
                    prefEditor.commit();
                }

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
