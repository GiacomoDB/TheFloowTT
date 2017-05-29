package thefloowtt.giacomo.com.thefloowtt.helper;

/**
 * Created by giaco on 27/05/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import thefloowtt.giacomo.com.thefloowtt.journey.Journey;

import static android.R.attr.format;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_JOURNEYS = "journeys";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_AVERAGE_SPEED = "average_speed";
    public static final String COLUMN_HEIGHT_DIFFERENCE = "height_difference";
    public static final String COLUMN_INFORMATIONS = "information";
    public static final String COLUMN_LOCATIONS = "locations";

    private static final String DATABASE_NAME = "thefloow_tt";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_JOURNEYS + "( " + COLUMN_ID
            + " integer primary key autoincrement, "+ COLUMN_START_DATE
            + " TEXT, " + COLUMN_END_DATE
            + " TEXT, " + COLUMN_DURATION
            + " INTEGER, " + COLUMN_DISTANCE
            + " REAL, " + COLUMN_AVERAGE_SPEED
            + " REAL, " + COLUMN_HEIGHT_DIFFERENCE
            + " REAL, " + COLUMN_INFORMATIONS
            + " TEXT, " + COLUMN_LOCATIONS
            + " TEXT );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNEYS);
        onCreate(db);
    }

    //insert journey data into table
    public void createJourney(Journey jou) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_START_DATE, String.valueOf(jou.getStartDate()));
        values.put(COLUMN_END_DATE, String.valueOf(jou.getEndDate()));
        values.put(COLUMN_DURATION, jou.getDuration());
        values.put(COLUMN_DISTANCE, jou.getDistance());
        values.put(COLUMN_AVERAGE_SPEED, jou.getAverageSpeed());
        values.put(COLUMN_HEIGHT_DIFFERENCE, jou.getHeightDifference());
        values.put(COLUMN_INFORMATIONS, jou.getInformations());
        values.put(COLUMN_LOCATIONS, jou.getLocations());
        // insert row
        db.insert(TABLE_JOURNEYS, null, values);

    }
    public Journey getJourney(int jou_id) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_JOURNEYS + " WHERE "
                + COLUMN_ID + " = " + jou_id;
        //Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Journey jou = new Journey();
        jou.setStartDate(c.getString(c.getColumnIndex(COLUMN_START_DATE)));
        jou.setEndDate(c.getString(c.getColumnIndex(COLUMN_END_DATE)));
        jou.setDuration((c.getInt(c.getColumnIndex(COLUMN_DURATION))));
        jou.setDistance((c.getInt(c.getColumnIndex(COLUMN_DISTANCE))));
        jou.setAverageSpeed((c.getInt(c.getColumnIndex(COLUMN_AVERAGE_SPEED))));
        jou.setHeightDifference(c.getInt(c.getColumnIndex(COLUMN_HEIGHT_DIFFERENCE)));
        jou.setInformations(c.getString(c.getColumnIndex(COLUMN_INFORMATIONS)));
        jou.setLocations(c.getString(c.getColumnIndex(COLUMN_LOCATIONS)));
        jou.setId(jou_id);
        c.close();
        return jou;
    }
    public String getTableAsString(SQLiteDatabase db, String tableName) {
        db = this.getReadableDatabase();
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name, allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";
            } while (allRows.moveToNext());
        }
        allRows.close();
        return tableString;
    }

    //getting all journeys
    public List<Journey> getAllJourneys() {
        List<Journey> journeys = new ArrayList<Journey>();
        String selectQuery = "SELECT  * FROM " + TABLE_JOURNEYS;
        // Log.e(LOG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Journey jou = new Journey();
                jou.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
                jou.setStartDate(c.getString(c.getColumnIndex(COLUMN_START_DATE)));
                jou.setEndDate(c.getString(c.getColumnIndex(COLUMN_END_DATE)));
                jou.setDuration((c.getInt(c.getColumnIndex(COLUMN_DURATION))));
                jou.setDistance((c.getInt(c.getColumnIndex(COLUMN_DISTANCE))));
                jou.setAverageSpeed((c.getInt(c.getColumnIndex(COLUMN_AVERAGE_SPEED))));
                jou.setHeightDifference(c.getInt(c.getColumnIndex(COLUMN_HEIGHT_DIFFERENCE)));
                jou.setInformations(c.getString(c.getColumnIndex(COLUMN_INFORMATIONS)));
                jou.setLocations(c.getString(c.getColumnIndex(COLUMN_LOCATIONS)));
                // adding to todo list
                journeys.add(jou);
            } while (c.moveToNext());
        }
        return journeys;
    }
    /*
     * get single journey
     */
    public Journey getSingleJourney(int journey_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_JOURNEYS + " WHERE "
                + COLUMN_ID + " = " + journey_id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Journey jou = new Journey();
        jou.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
        jou.setStartDate(c.getString(c.getColumnIndex(COLUMN_START_DATE)));
        jou.setEndDate(c.getString(c.getColumnIndex(COLUMN_END_DATE)));
        jou.setDuration((c.getInt(c.getColumnIndex(COLUMN_DURATION))));
        jou.setDistance((c.getInt(c.getColumnIndex(COLUMN_DISTANCE))));
        jou.setAverageSpeed((c.getInt(c.getColumnIndex(COLUMN_AVERAGE_SPEED))));
        jou.setHeightDifference(c.getInt(c.getColumnIndex(COLUMN_HEIGHT_DIFFERENCE)));
        jou.setInformations(c.getString(c.getColumnIndex(COLUMN_INFORMATIONS)));
        jou.setLocations(c.getString(c.getColumnIndex(COLUMN_LOCATIONS)));
        c.close();
        return jou;
    }
}