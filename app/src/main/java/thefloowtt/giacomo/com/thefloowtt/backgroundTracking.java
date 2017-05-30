package thefloowtt.giacomo.com.thefloowtt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import thefloowtt.giacomo.com.thefloowtt.helper.MySQLiteHelper;
import thefloowtt.giacomo.com.thefloowtt.journey.Journey;
import static com.google.android.gms.wearable.DataMap.TAG;

public class backgroundTracking extends Service implements LocationListener,SensorEventListener {
    public backgroundTracking() {
    }

    private GoogleApiClient client;
    public LocationManager locationManager;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private static final long SLEEP_TIME = 180000; // in Milliseconds
    private LocationListener MyLocationListener = backgroundTracking.this;
    private Boolean howIsTracking = false;
    private GoogleMap myMap;
    private ArrayList<LatLng> points = new ArrayList<LatLng>();
    private ArrayList<Location> locations = new ArrayList<Location>();
    MySQLiteHelper db;
    SQLiteDatabase Db;
    private Location userLocation;
    private Date startDate;
    private Date endDate;
    private SensorManager senseManager;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private Boolean goToSleep=false;
    private long lastShake;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        db = new MySQLiteHelper(getApplicationContext());
        final Handler h = new Handler();
        final int delay = 3000; //milliseconds
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final String TAG = backgroundTracking.class.getSimpleName();
        senseManager = (SensorManager)this.getSystemService(SENSOR_SERVICE); // initialization of sensorManager
        Sensor linearAcceleration;
        linearAcceleration = senseManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // initialization of LINEAR_ACCELERATION
        senseManager.registerListener(this,linearAcceleration,SensorManager.SENSOR_DELAY_NORMAL);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //setting up checker fevery X second, to check all actions regarding location requests
        h.postDelayed(new Runnable() {
            public void run() {
              // Log.i(TAG,db.getTableAsString(Db,"journeys"));
                Log.i(TAG,"is the app tracking? "+String.valueOf(isAppTracking()));
                //Log.i(TAG,"how is tracking? "+String.valueOf(howIsTracking));
                putAppToSleep();
                if (isAppTracking()&& !howIsTracking && (System.currentTimeMillis()-lastShake)<=SLEEP_TIME ){
                    startTracking(MyLocationListener,locationManager);
                }else if(!isAppTracking()&&howIsTracking){
                    stopTracking(MyLocationListener,locationManager);
                }else{
                    //nothing yet
                }
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public void onDestroy() {
        stopTracking(MyLocationListener, locationManager);
    }

    public void stopTracking(LocationListener customListener, LocationManager customManager) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        howIsTracking = false;
        //getting time when journey stopped
        String tempDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            endDate = format.parse(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (locations.size()>0){
            Location firstLoc = locations.get(0);
            Location lastLoc = locations.get(locations.size()-1);
            long duration = lastLoc.getTime()-firstLoc.getTime();
            float distance = firstLoc.distanceTo(lastLoc);
            float avgSpeed = (firstLoc.getSpeed()+lastLoc.getSpeed())/2;
            //I didn't want to spend time getting actual altitude,
            // on my testing env it was always 0, I am just saving data for showing purpose
            double heightDiff = firstLoc.getAltitude();
            Journey jou = new Journey(String.valueOf(startDate),String.valueOf(endDate),duration,distance,avgSpeed,heightDiff,savePoints(points),saveLocations(locations));
            db.createJourney(jou);
            locations.clear();
            points.clear();

        }
        customManager.removeUpdates(customListener);
    }
    public void startTracking(LocationListener customListener, LocationManager customManager) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        howIsTracking = true;
        //getting time of journey start
        String tempDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = format.parse(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //starting location listener
        customManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                customListener);

    }
    //check settings value of tracking
    public Boolean isAppTracking(){
        SharedPreferences sharedPref = getSharedPreferences("tracking",MODE_PRIVATE);
        String gotTracking = sharedPref.getString("track","null");
        if(gotTracking.equalsIgnoreCase("on")){
            return true;
        }else{
            return false;
        }
    }
    /*check if device is moving on left on a table,
    if it hasn't moved for the past X minutes stop tracking  until next movement to save battery*/
    public void putAppToSleep(){
        //Log.i("lastShake",String.valueOf(System.currentTimeMillis()-lastShake));
        if(isAppTracking()){
            if((System.currentTimeMillis()-lastShake)>=SLEEP_TIME && howIsTracking){
                stopTracking(MyLocationListener,locationManager);
                //Log.i(TAG,"going to sleep");
            }else if((System.currentTimeMillis()-lastShake)<=SLEEP_TIME && !howIsTracking){
                startTracking(MyLocationListener,locationManager);
                //Log.i(TAG,"waking up");
            }
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        locations.add(location);
        userLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        points.add(latLng); //added
        MainActivity.redrawLine(points,latLng,isAppTracking()); //added
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderEnabled(String s) {}
    @Override
    public void onProviderDisabled(String s) {}
    /*
    * saving all Locations objects in database as json array
    * only for showing that it can be done, they are not used in this test*/
    public String saveLocations(ArrayList<Location> locations) {
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(locations).getAsJsonArray();
        return myCustomArray.toString();
    }
    /*
    * Saving LatLon point to redraw journey without touching location points*/
    public String savePoints(ArrayList<LatLng> pointsCustom) {
        Log.i(TAG,"Saving points");
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(pointsCustom).getAsJsonArray();
        return myCustomArray.toString();
    }
    /*
    * Getting accellerometer data to check sleep time*/
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = sensorEvent.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x*x + y*y + z*z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Detecting user motion in order to make app sleep
            if(mAccel > 3){
                lastShake = System.currentTimeMillis();
                /*Log.i("sensor","Value of X: "+sensorEvent.values[0]);
                Log.i("sensor","Value of Y: "+sensorEvent.values[1]);
                Log.i("sensor","Value of Z: "+sensorEvent.values[2]);*/
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
