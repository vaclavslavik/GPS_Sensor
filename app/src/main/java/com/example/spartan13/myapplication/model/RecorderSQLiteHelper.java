package com.example.spartan13.myapplication.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by spartan13 on 30. 1. 2015.
 */
public class RecorderSQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RecorderDB";
    private static final String KEY_TRACK_ID = "track_id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_DATE = "date";
    private static final String KEY_ALTITUDE = "altitude";
    private static final String[] COLUMNS = {KEY_ALTITUDE, KEY_DATE, KEY_LATITUDE, KEY_LONGITUDE, KEY_TRACK_ID};
    private static final String TABLE_NAME = "track_records";

    public RecorderSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void addRecorder(Recorder recorder) {

        int trackId = this.getMaxId()+1;

        Log.d("addRecorder", recorder.toString());

        SQLiteDatabase db = this.getWritableDatabase();


        for (Location location : recorder.getLocations()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_ALTITUDE, location.getAltitude());
            contentValues.put(KEY_DATE, location.getTime());
            contentValues.put(KEY_LATITUDE, location.getLatitude());
            contentValues.put(KEY_LONGITUDE, location.getLongitude());
            contentValues.put(KEY_TRACK_ID, trackId);
            db.insert(TABLE_NAME, null, contentValues);
        }
        db.close();
    }

    public boolean deleteTrack(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        return  database.delete(TABLE_NAME, KEY_TRACK_ID +" = "+id, null) > 0;
    }

    public int getCountRows(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME
                , new String[]{"COUNT(" + KEY_TRACK_ID + ")"}
                , KEY_TRACK_ID +"  = ? "
                , new String[]{String.valueOf(id)}
                , null
                , null
                , null
                , null
        );


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getString(0) == null){
                    return 0;
                }

                Integer intour = Integer.parseInt(cursor.getString(0));

                return intour;
            }
        }
        return 0;
    }

    public Recorder getRecorder(int trackId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME
                , COLUMNS
                , "" + KEY_TRACK_ID + " = ?"
                , new String[]{String.valueOf(trackId)}
                , null
                , null
                , KEY_DATE + " ASC"
        );

        Recorder recorder = new Recorder();
        if (cursor.moveToFirst()) {
            do {
                Location location = new Location("NA");
                location.setTime(Long.parseLong(cursor.getString(1)));
                location.setAltitude(Double.parseDouble(cursor.getString(0)));
                location.setLatitude(Double.parseDouble(cursor.getString(2)));
                location.setLongitude(Double.parseDouble(cursor.getString(3)));
                recorder.addLocation(location);
            } while (cursor.moveToNext());
        }


        return recorder;
    }

    public ArrayList<String> getListNames(boolean orderDesc){
        ArrayList<String> trackList = new ArrayList<>();

        String orderBy = KEY_TRACK_ID;
        if (orderDesc == true){
            orderBy = orderBy + " DESC";
        }else{
            orderBy = orderBy + " ASC";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                TABLE_NAME
                , new String[]{KEY_TRACK_ID,"MIN(date)"}
                , null
                , null
                , KEY_TRACK_ID
                , null
                , orderBy
                , null
        );
        if (cursor.moveToFirst()) {
            do {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
                trackList.add(simpleDateFormat.format(new java.util.Date(Long.parseLong(cursor.getString(1)))));
            } while (cursor.moveToNext());
        }
        db.close();


        return trackList;
    }

    public ArrayList<Integer> getTrackIds(boolean orderDesc) {

        String orderBy = KEY_TRACK_ID;
        if (orderDesc == true){
            orderBy = orderBy + " DESC";
        }else{
            orderBy = orderBy + " ASC";
        }

        ArrayList<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                TABLE_NAME
                , new String[]{KEY_TRACK_ID}
                , null
                , null
                , KEY_TRACK_ID
                , null
                , orderBy
                , null
        );

        if (cursor.moveToFirst()) {
            do {
                ids.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        db.close();
        return ids;
    }

    public int getMaxId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME
                , new String[]{"MAX(" + KEY_TRACK_ID + ")"}
                , null
                , null
                , null
                , null
                , null
                , null
        );


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getString(0) == null){
                    return 0;
                }

                Integer intour = Integer.parseInt(cursor.getString(0));

                return intour;
            }
        }
        return 0;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE track_records (" +
                "  track_id INTEGER NOT NULL" +
                ", latitude REAL NOT NULL" +
                ", longitude REAL NOT NULL" +
                ", altitude INTEGER NOT NULL" +
                ", date INTEGER NOT NULL" +
                "" +
                ")";

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS track_records");
        this.onCreate(db);
    }


}
