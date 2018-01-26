package com.mitracking.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mitracking.Singleton;
import com.mitracking.interfaces.LocationTracker;
import com.mitracking.objs.LoginObj;
import com.mitracking.objs.TrackObj;
import com.mitracking.utils.ConnectToServer;
import com.mitracking.utils.Connectivity;
import com.mitracking.utils.Constants;
import com.mitracking.utils.GpsConfiguration;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SendService extends Service implements LocationTracker, LocationTracker.LocationUpdateListener  {

    private Timer timer;
    private static long TIME;
    private static double latitud, longitud;
    private static float accuracy;
    private ArrayList<TrackObj> array;
    //********************************
    private boolean isRunning;

    private ProviderLocationTracker gps;
    private ProviderLocationTracker net;

    private LocationUpdateListener listener;

    Location lastLoc;
    long lastTime;
    //********************************
    private AlarmManager alarmMgr;

    @Override
    public void onCreate() {
        Log.d("Service", "onCreate");
        Singleton.getInstance();
        int secons = Integer.parseInt(Singleton.getSettings().getString(Constants.TrackModeValue_TAG, ""));
        TIME = TimeUnit.SECONDS.toMillis(secons);
        //initGPS();
        //Log.d("GPS config", ""+Singleton.getGpsConfig().configuracionLocationManager());
        refreshConfig();
        gps = new ProviderLocationTracker(this, ProviderLocationTracker.ProviderType.GPS);
        net = new ProviderLocationTracker(this, ProviderLocationTracker.ProviderType.NETWORK);
        start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStartCommand");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                trackingFunction();
            }
        }, TimeUnit.SECONDS.toMillis(11), TIME);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "onDestroy");
        timer.cancel();
        timer = null;
    }

    private void trackingFunction() {
        if (dayValidation()) {
            if (hourValidation()) {
                initInsert();
                initConnection();
            }
        }
    }

    private boolean dayValidation() {
        String arg = Singleton.getSettings().getString(Constants.TrackWorkDays_TAG, "").replace("0", "");
        String arg0 = Singleton.getSettings().getString(Constants.TrackWeekEndDays_TAG, "").replace("0", "");


        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String[] daysS = arg.split("[|]");
        ArrayList<String> days = new ArrayList<>(Arrays.asList(daysS));

        if (arg0.length() > 0) {
            String[] wEnd = arg0.split("[|]");
            for (int i = 0; i < wEnd.length; i++) {
                days.add(wEnd[i]);
            }
        }

        if (days.contains("" + day))
            return true;
        else
            return false;
    }

    private boolean hourValidation() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        Date userDate = null;
        String current = "";
        try {
            String hour = "" + calendar.get(Calendar.HOUR_OF_DAY);
            if (calendar.get(Calendar.HOUR_OF_DAY) < 10)
                hour = "0" + hour;
            current = hour + ":" + calendar.get(Calendar.MINUTE);
            userDate = parser.parse(current);
        } catch (ParseException e) {
            return false;
        }

        if (day > 0 && day < 6) {
            try {
                String[] aux = Singleton.getSettings().getString(Constants.TrackWorkHours_TAG, "").split("[|]");
                for (int i = 0; i < aux.length; i++) {
                    String[] aux0 = aux[i].split("[-]");
                    Date one = parser.parse(aux0[0]);
                    Date two = parser.parse(aux0[1]);
                    if (userDate.after(one) && userDate.before(two)) {
                        return true;
                    }
                }
            } catch (ParseException e) {
                return false;
            }
        } else if (Singleton.getSettings().getString(Constants.TrackWeekEndHours_TAG, "").length() > 0) {
            try {
                String[] aux = Singleton.getSettings().getString(Constants.TrackWeekEndHours_TAG, "").split("[|]");
                for (int i = 0; i < aux.length; i++) {
                    String[] aux0 = aux[i].split("[-]");
                    Date one = parser.parse(aux0[0]);
                    Date two = parser.parse(aux0[1]);
                    if (userDate.after(one) && userDate.before(two)) {
                        return true;
                    }
                }
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }

    private void initInsert() {
        long day = System.currentTimeMillis();
        String MobileTrackDate = dateFormat(day);
        String UTCTrackDate = dateFormatUniversal(day);
        latitud = lastLoc.getLatitude();
        longitud = lastLoc.getLongitude();
        accuracy = lastLoc.getAccuracy();
        Singleton.getBdh().insertNewTrack(MobileTrackDate, UTCTrackDate, "" + latitud, "" + longitud, "" + accuracy, day);
        if (latitud == 0 && longitud == 0 && accuracy == 0) {
            Singleton.getBdh().updateTrack("FAIL", getErrorCode(0), 0, day);
        } else {
            Singleton.getBdh().updateTrack("DONE", getErrorCode(4), 0, day);
        }
    }

    private String getErrorCode(int arg) {
        String GpsErrorCode = "";
        if (arg == 0) {
            if (Connectivity.isConnected(this))
                GpsErrorCode = "003";
            else if (Singleton.getSettings().getBoolean(Constants.GPS_TAG, false))
                GpsErrorCode = "002";
            else if (latitud == 0 && longitud == 0)
                GpsErrorCode = "001";
            else
                GpsErrorCode = "";
        } else if (arg == 1)
            GpsErrorCode = "201";
        else if (arg == 2)
            GpsErrorCode = "400";
        else if (arg == 3)
            GpsErrorCode = "500";

        return GpsErrorCode;
    }

    private void initConnection() {
        parseLogin(Singleton.getSettings().getString(Constants.JSON_LOGIN, ""));
        JSONObject TrackUserInfo = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            json.put("SAASName", Constants.SAASName);
            json.put("TenantID", Constants.TenantID);
            json.put("SessionID", Singleton.getLoginObj().SessionID);
            json.put("AlignmentID", Singleton.getLoginObj().AlignmentID);
            json.put("EmployeeID", Singleton.getLoginObj().EmployeeID);
            json.put("MobileID", Singleton.getSettings().getString(Constants.MobileID_TAG, ""));

            JSONArray TrackGeoItems = new JSONArray();
            array = Singleton.getBdh().getTrackList();
            for (int i = 0; i < array.size(); i++) {
                JSONObject TrackGeoItem = new JSONObject();
                TrackGeoItem.put("MobileTrackDate", array.get(i).MobileTrackDate);
                TrackGeoItem.put("UTCTrackDate", array.get(i).UTCTrackDate);
                TrackGeoItem.put("GpsLatitude", array.get(i).Latitude);
                TrackGeoItem.put("GpsLongitude", array.get(i).Longitude);
                TrackGeoItem.put("GpsAccuracy", array.get(i).GpsAccuracy);
                TrackGeoItem.put("GpsTrackStatus", array.get(i).GpsTrackStatus);
                TrackGeoItem.put("GpsErrorCode", array.get(i).GpsErrorCode);
                TrackGeoItems.put(TrackGeoItem);
            }

            json.put("TrackGeoItems", TrackGeoItems);
            TrackUserInfo.put("trackMobileItem", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] objs = new Object[]{Constants.URL_TrackMobilePosition, 3, this, TrackUserInfo};
        ConnectToServer connectToServer = new ConnectToServer(objs);
    }

    public void parseLogin(String arg) {
        Log.d("response login", arg);
        try {
            LoginObj loginObj = new LoginObj();
            JSONObject jsonObject = new JSONObject(arg);
            JSONObject ValidateUserResult = jsonObject.getJSONObject("ValidateUserResult");
            if (ValidateUserResult.getString("ServerStatus").equals("DONE") &&
                    ValidateUserResult.getString("IsValidUser").equals("TRUE")) {
                loginObj = new LoginObj();
                loginObj.AlignmentID = ValidateUserResult.getString("AlignmentID");
                loginObj.AlignmentName = ValidateUserResult.getString("AlignmentName");
                loginObj.EmployeeID = ValidateUserResult.getString("EmployeeID");
                loginObj.EmployeeName = ValidateUserResult.getString("EmployeeName");
                loginObj.IsValidUser = ValidateUserResult.getString("IsValidUser");
                loginObj.IsValidUserReason = ValidateUserResult.getString("IsValidUserReason");
                loginObj.ServerErrorCode = ValidateUserResult.getString("ServerErrorCode");
                loginObj.ServerErrorDetail = ValidateUserResult.getString("ServerErrorDetail");
                loginObj.ServerStatus = ValidateUserResult.getString("ServerStatus");
                loginObj.SessionID = ValidateUserResult.getString("SessionID");

                Singleton.savePreferences(Constants.JSON_LOGIN, arg);
                Singleton.savePreferences(Constants.FLAG_LOGIN, true);
                Singleton.setLoginObj(loginObj);
                Singleton.dissmissLoad();
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.savePreferences(Constants.FLAG_LOGIN, false);
        }
    }

    /*@Override
    public void onGpsLocationInteraction(Location location) {
        latitud = location.getLatitude();
        longitud = location.getLongitude();
        accuracy = location.getAccuracy();
    }

    public void initGPS(){
        Singleton.initGPSConfig(this);
    }*/

    private String dateFormat(long time) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd kkmmss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        date = simpleDateFormat.format(new Date(time));
        ;
        return date;
    }

    private String dateFormatUniversal(long time) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd kkmmss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = simpleDateFormat.format(new Date(time));
        ;
        return date;
    }

    public void start(){
        if(isRunning){
            return;
        }
        gps.start(this);
        net.start(this);
        isRunning = true;
    }

    public void start(LocationUpdateListener update) {
        start();
        listener = update;
    }


    public void stop(){
        if(isRunning){
            gps.stop();
            net.stop();
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation(){
        //If either has a location, use it
        return gps.hasLocation() || net.hasLocation();
    }

    public boolean hasPossiblyStaleLocation(){
        //If either has a location, use it
        return gps.hasPossiblyStaleLocation() || net.hasPossiblyStaleLocation();
    }

    public Location getLocation(){
        Location ret = gps.getLocation();
        if(ret == null){
            ret = net.getLocation();
        }
        return ret;
    }

    public Location getPossiblyStaleLocation(){
        Location ret = gps.getPossiblyStaleLocation();
        if(ret == null){
            ret = net.getPossiblyStaleLocation();
        }
        return ret;
    }

    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
        Log.d("onUpdate", "onUpdate");
        boolean update = false;

        if(lastLoc == null){
            update = true;
        }
        else if(lastLoc != null && lastLoc.getProvider().equals(newLoc.getProvider())){
            update = true;
        }
        else if(newLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
            update = true;
        }
        else if (newTime - lastTime > 5 * 60 * 1000){
            update = true;
        }

        if(update){
            if(listener != null){
                listener.onUpdate(lastLoc, lastTime, newLoc, newTime);
            }
            lastLoc = newLoc;
            lastTime = newTime;
        }
    }

    public void getResponse(String result) {
        try {
            JSONObject json = new JSONObject(result);
            JSONObject TrackMobilePositionResult = json.getJSONObject("TrackMobilePositionResult");
            if(TrackMobilePositionResult.getString("ServerStatus").equals("DONE") &&
                    TrackMobilePositionResult.getString("ServerErrorCode").equals("0000")){
                for(int i = 0; i < array.size(); i++){
                    Singleton.getBdh().updateTrack("DONE", getErrorCode(4), 0, array.get(i).ID);
                }
                array.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshConfig(){
        if(Singleton.getSettings().getString(Constants.RefreshConfig_TAG, "").equals("TRUE")){
            int hour, minute;
            String[] aux = Constants.ReadConfigurationAt.split("[:]");
            hour = Integer.parseInt(aux[0]);
            minute = Integer.parseInt(aux[1]);

            alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 0, alarmIntent);
        }
    }

}

