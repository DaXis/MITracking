package com.mitracking.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.mitracking.Singleton;
import com.mitracking.objs.TrackObj;
import com.mitracking.utils.Constants;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private Context ctx;

    final String sqlCreate0 = "CREATE TABLE GeoItems (ID INTEGER PRIMARY KEY AUTOINCREMENT, MobileTrackDate TEXT, UTCTrackDate TEXT, " +
            "Latitude TEXT, Longitude TEXT, GpsAccuracy TEXT, GpsTrackStatus TEXT, GpsErrorCode TEXT, TrackGeoItemSend INTEGER, " +
            "TrackGeoItemDate LONG)";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void insertNewTrack(String MobileTrackDate, String UTCTrackDate, String Latitude, String Longitude, String GpsAccuracy,
                               long TrackGeoItemDate){
        String query = "INSERT INTO GeoItems (MobileTrackDate, UTCTrackDate, Latitude, Longitude, GpsAccuracy, TrackGeoItemDate) "+
                "VALUES ('"+MobileTrackDate+"', '"+UTCTrackDate+"', '"+Latitude+"', '"+Longitude+"', '"+GpsAccuracy+"', "+
                TrackGeoItemDate+")";
        Singleton.getDb().execSQL(query);
    }

    public void updateTrack(String GpsTrackStatus, String GpsErrorCode, int TrackGeoItemSend, int ID){
        String query = "UPDATE GeoItems SET " +
                "GpsTrackStatus = '"+GpsTrackStatus+"', "+
                "GpsErrorCode = '"+GpsErrorCode+"' ,"+
                "TrackGeoItemSend = "+TrackGeoItemSend+
                "WHERE ID = "+ID;
        Singleton.getDb().execSQL(query);
    }

    public void updateTrack(String GpsTrackStatus, String GpsErrorCode, int TrackGeoItemSend, long TrackGeoItemDate){
        String query = "UPDATE GeoItems SET " +
                "GpsTrackStatus = '"+GpsTrackStatus+"', "+
                "GpsErrorCode = '"+GpsErrorCode+"' ,"+
                "TrackGeoItemSend = "+TrackGeoItemSend+" "+
                "WHERE TrackGeoItemDate = "+TrackGeoItemDate;
        Singleton.getDb().execSQL(query);
    }

    public void updateGpsErrorCode(String GpsErrorCode, int ID){
        String query = "UPDATE GeoItems SET " +
                "GpsErrorCode = '"+GpsErrorCode+"' ,"+
                "WHERE ID = "+ID;
        Singleton.getDb().execSQL(query);
    }

    public void eraseData(){
        Singleton.getDb().execSQL("DELETE FROM GeoItems WHERE TrackGeoItemDate <= date('now','-"+ Constants.TrackDaysHistory+"')");
    }

    public ArrayList<TrackObj> getTrackList(){
        ArrayList<TrackObj> array = new ArrayList<>();

        String query = "SELECT ID, MobileTrackDate, UTCTrackDate, Latitude, Longitude, GpsAccuracy, GpsTrackStatus, GpsErrorCode, " +
                "TrackGeoItemSend, TrackGeoItemDate FROM GeoItems WHERE TrackGeoItemSend = 0";

        Cursor cursor = Singleton.getDb().rawQuery(query, null);
        if (cursor != null)
            cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            TrackObj trackObj = new TrackObj();
            trackObj.ID = cursor.getInt(0);
            trackObj.MobileTrackDate = cursor.getString(1);
            trackObj.UTCTrackDate = cursor.getString(2);
            trackObj.Latitude = cursor.getString(3);
            trackObj.Longitude = cursor.getString(4);
            trackObj.GpsAccuracy = cursor.getString(5);
            trackObj.GpsTrackStatus = cursor.getString(6);
            trackObj.GpsErrorCode = cursor.getString(7);
            trackObj.TrackGeoItemSend = cursor.getInt(8);
            trackObj.TrackGeoItemDate = cursor.getLong(9);
            array.add(trackObj);
            cursor.moveToNext();
        }

        return array;
    }

}
