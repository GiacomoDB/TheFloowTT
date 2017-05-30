package thefloowtt.giacomo.com.thefloowtt.journey;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thefloowtt.giacomo.com.thefloowtt.R;
import thefloowtt.giacomo.com.thefloowtt.helper.MySQLiteHelper;

public class displayJourney extends AppCompatActivity
        implements GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static GoogleMap mMap;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String TAG = displayJourney.class.getSimpleName();
    static Polyline line;
    private GoogleApiClient client;
    private MySQLiteHelper db;
    private List<String> pointsTemp = new ArrayList<String>();
    private ArrayList<LatLng> points = new ArrayList<LatLng>();
    private int journeyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_journey);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        db = new MySQLiteHelper(getApplicationContext());
        //inizialing map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Intent intent = getIntent();
        journeyId = intent.getIntExtra("journeyId",0);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /*drawing selected journey when map is loaded*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        Journey jou = db.getSingleJourney(journeyId);
        pointsTemp =  Arrays.asList(jou.getInformations().split(","));
        double longitude = 0;
        double latitude = 0;
        try
        {
            JSONArray jr = new JSONArray(jou.getInformations());
            Log.i(TAG,String.valueOf(jr));
            for(int i=0;i<jr.length();i++)
            {
                JSONObject jb2 = jr.getJSONObject(i);
                longitude = Double.parseDouble(jb2.getString("longitude"));
                latitude = Double.parseDouble(jb2.getString("latitude"));
                LatLng location = new LatLng(latitude, longitude);
                Log.i(TAG,String.valueOf(location));
                points.add(location);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Log.i(TAG,String.valueOf(points.size()));
        drawJourney(points);    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    /*drawing journey from array of LatLng points*/
    public static void drawJourney(ArrayList<LatLng> points) {
        if(mMap!=null){
            Log.i(TAG,"map is not null");
            //clears all Markers and Polylines
            int halfSize = points.size()/2;
            mMap.clear();
                PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                for (int i = 0; i < points.size(); i++) {
                    LatLng point = points.get(i);
                    if(i==0){
                        placeMarker(point,0);
                    }else if(i==points.size()-1){
                        placeMarker(point,1);
                    }
                    Log.i(TAG,String.valueOf(point));
                    options.add(point);
                }
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(points.get(halfSize), 16);
                Log.i(TAG,String.valueOf(points.get(0)));
                mMap.animateCamera(cameraUpdate);
                line = mMap.addPolyline(options);
            }else{
            Log.i(TAG,"map is null");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
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
    /*method to place marker at start and end of the journey(0 -> start 1->end*/
    public static void placeMarker(LatLng pos, int attribute) {
        MarkerOptions m;
        m = new MarkerOptions().position(pos);

        if(attribute==0){
             m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else{
             m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        mMap.addMarker(m);
    }
}
