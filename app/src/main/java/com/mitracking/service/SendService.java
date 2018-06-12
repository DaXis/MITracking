package com.mitracking.service;

import android.Manifest;
import android.app.Notification;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mitracking.MainActivity;
import com.mitracking.R;
import com.mitracking.Singleton;
import com.mitracking.interfaces.LocationTracker;
import com.mitracking.objs.LoginObj;
import com.mitracking.objs.TrackObj;
import com.mitracking.utils.ConnectToServer;
import com.mitracking.utils.Connectivity;
import com.mitracking.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SendService extends Service implements LocationTracker, LocationTracker.LocationUpdateListener  {

    private static Timer timer;
    private static long TIME, INITIAL_DELAY = 5500;
    private static double latitud, longitud;
    private static float accuracy;
    private ArrayList<TrackObj> array;
    private ArrayList<TrackObj> array_b;
    //********************************
    private boolean isRunning;
    private ProviderLocationTracker gps;
    private ProviderLocationTracker net;
    private LocationUpdateListener listener;
    private static Location lastLoc;
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
        startOnBack();
        if(timer == null){
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                synchronized public void run() {
                    System.gc();
                    trackingFunction();
                }
            }, INITIAL_DELAY, TIME);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStartCommand");
        //return START_STICKY;
        return START_NOT_STICKY;
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
        boolean days = dayValidation();
        //Log.d("dayValidation", ""+days);
        if (days) {
            boolean hours = hourValidation();
            //Log.d("hourValidation", ""+hours);
            if (hours) {
                initInsert();
                if(Connectivity.isConnected(this)) {
                    initConnection();
                    initErrorConnection();
                }
            }
        }
    }

    private boolean dayValidation() {
        String arg = Singleton.getSettings().getString(Constants.TrackWorkDays_TAG, "").replace("0", "");
        String arg0 = Singleton.getSettings().getString(Constants.TrackWeekEndDays_TAG, "").replace("0", "");

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day-1;
        if(day == 0)
            day = 7;

        String[] daysS = arg.split("[|]");
        ArrayList<String> days = new ArrayList<>(Arrays.asList(daysS));

        if (arg0.length() > 0) {
            String[] wEnd = arg0.split("[|]");
            for (int i = 0; i < wEnd.length; i++) {
                days.add(wEnd[i]);
            }
        }

        /*for(int i = 0; i < days.size(); i++){
            Log.d("day "+i, days.get(i));
        }*/

        if (days.contains("" + day))
            return true;
        else
            return false;
    }

    private boolean hourValidation() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day-1;
        if(day == 0)
            day = 7;
        SimpleDateFormat parser = new SimpleDateFormat("kk:mm");
        Date userDate = null;
        String current = "";
        try {
            String hour = "" + calendar.get(Calendar.HOUR_OF_DAY);
            if (calendar.get(Calendar.HOUR_OF_DAY) < 10)
                hour = "0" + hour;
            current = hour + ":" + calendar.get(Calendar.MINUTE);
            //Log.d("current", current);
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
        if(lastLoc != null){
            latitud = round(lastLoc.getLatitude(), 6);
            longitud = round(lastLoc.getLongitude(), 6);
            accuracy = lastLoc.getAccuracy();
        }
        Singleton.getBdh().insertNewTrack(MobileTrackDate, UTCTrackDate, "" + latitud, "" + longitud, "" + accuracy, day);
        if(Connectivity.isConnected(this)){
            if (latitud == 0 && longitud == 0 && accuracy == 0) {
                Singleton.getBdh().updateTrack("FAIL", getErrorCode(0), 0, day);
            } else {
                Singleton.getBdh().updateTrack("DONE", getErrorCode(4), 0, day);
            }
        } else
            Singleton.getBdh().updateTrack("FAIL", getErrorCode(0), 0, day);
        Log.d("initInsert", "complete");
    }

    private String getErrorCode(int arg) {
        String GpsErrorCode = "";
        if (arg == 0) {
            if (!Connectivity.isConnected(this))
                GpsErrorCode = "Sin conexión a internet";
            else if (Singleton.getSettings().getBoolean(Constants.GPS_TAG, false))
                GpsErrorCode = "GPS desactivado";
            else if (latitud == 0 && longitud == 0)
                GpsErrorCode = "Latitud y longitud = 0";
            else
                GpsErrorCode = "";
        } else if (arg == 1)
            GpsErrorCode = "201";
        else if (arg == 2)
            GpsErrorCode = "400";
        else if (arg == 3)
            GpsErrorCode = "500";
        else if (arg == 4)
            GpsErrorCode = "Envio exitoso desde background";

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
            array = Singleton.getBdh().getTrackList("DONE");
            for (int i = 0; i < array.size(); i++) {
                JSONObject TrackGeoItem = new JSONObject();
                TrackGeoItem.put("MobileTrackDate", array.get(i).MobileTrackDate);
                TrackGeoItem.put("UTCTrackDate", array.get(i).UTCTrackDate);
                TrackGeoItem.put("Latitude", array.get(i).Latitude);
                TrackGeoItem.put("Longitude", array.get(i).Longitude);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd kk:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        date = simpleDateFormat.format(new Date(time));
        ;
        return date;
    }

    private String dateFormatUniversal(long time) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd kk:mm:ss");
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
        /*boolean update = false;

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
        }*/
        lastLoc = newLoc;
        Log.d("lat lon", lastLoc.getLatitude()+", "+lastLoc.getLongitude());
    }

    public void getResponse(String result) {
        Log.d("gps response", result);
        try {
            JSONObject json = new JSONObject(result);
            JSONObject TrackMobilePositionResult = json.getJSONObject("TrackMobilePositionResult");
            if(TrackMobilePositionResult.getString("ServerStatus").equals("DONE") &&
                    TrackMobilePositionResult.getString("ServerErrorCode").equals("0000")){
                for(int i = 0; i < array.size(); i++){
                    //Singleton.getBdh().updateTrack("DONE", getErrorCode(4), 1, array.get(i).ID);
                    Singleton.getBdh().updateTrack(1, array.get(i).ID);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.clear();
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

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void startOnBack(){
        //Notification notification = new Notification(R.drawable.tab_tracking, getText(R.string.service), System.currentTimeMillis());
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Location Updates Service")
                .setContentText("Getting Location Updates")
                .setSmallIcon(R.drawable.tab_tracking)
                .setTicker(getText(R.string.service))
                .build();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        startForeground(1, notification);
    }

    //*********************************
    private void initErrorConnection() {
        parseLogin(Singleton.getSettings().getString(Constants.JSON_LOGIN, ""));
        JSONObject TrackUserInfo = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            json.put("SAASName", Constants.SAASName);
            json.put("TenantID", Constants.TenantID);

            JSONArray TrackGeoItems = new JSONArray();
            array_b = Singleton.getBdh().getTrackList("FAIL");
            for (int i = 0; i < array_b.size(); i++) {
                JSONObject TrackGeoItem = new JSONObject();
                TrackGeoItem.put("UserLoginID", Singleton.getSettings().getString(Constants.UserLoginID_TAG, ""));
                TrackGeoItem.put("ErrorDateTime", array_b.get(i).MobileTrackDate);
                TrackGeoItem.put("ErrorEvent", array_b.get(i).GpsTrackStatus);
                TrackGeoItem.put("ErrorDetail", array_b.get(i).GpsErrorCode);
                TrackGeoItems.put(TrackGeoItem);
            }

            json.put("TrackErrorItem", TrackGeoItems);
            TrackUserInfo.put("TrackErrorInfo", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] objs = new Object[]{Constants.URL_ErrorTrack, 5, this, TrackUserInfo};
        ConnectToServer connectToServer = new ConnectToServer(objs);
    }

    public void getErrorResponse(String result) {
        Log.d("gps error response", result);
        try {
            JSONObject json = new JSONObject(result);
            JSONObject TrackMobilePositionResult = json.getJSONObject("SendErrorLogResult");
            if(TrackMobilePositionResult.getString("ServerStatus").equals("DONE") &&
                    TrackMobilePositionResult.getString("ServerErrorCode").equals("0000")){
                for(int i = 0; i < array_b.size(); i++){
                    Singleton.getBdh().updateTrack(1, array_b.get(i).ID);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.clear();
    }
    //*********************************

}

