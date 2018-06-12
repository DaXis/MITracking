package com.mitracking;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitracking.fragments.ConfigFragment;
import com.mitracking.fragments.ErrorsFragment;
import com.mitracking.fragments.LoginFragment;
import com.mitracking.fragments.MainFragment;
import com.mitracking.fragments.TrackingFragment;
import com.mitracking.objs.ConfigObj;
import com.mitracking.objs.LoginObj;
import com.mitracking.objs.TrackObj;
import com.mitracking.service.SendService;
import com.mitracking.utils.ConnectToServer;
import com.mitracking.utils.Connectivity;
import com.mitracking.utils.Constants;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FrameLayout mainContent;
    private LoginFragment loginFragment;
    private MainFragment mainFragment;
    private TrackingFragment trackingFragment;
    private ErrorsFragment errorsFragment;
    private ConfigFragment configFragment;
    private LoginObj loginObj;
    private final String TAG = getClass().getSimpleName();
    private static final String DB_PATH = "/data/data/com.mitracking/databases/";
    private LinearLayout buttons;
    private ImageButton home, track, error, config;
    private ArrayList<ConfigObj> array;
    private ArrayList<TrackObj> tracks;
    //cambio

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        mainContent = (FrameLayout)findViewById(R.id.mainContent);
        Singleton.setCurrentActivity(this);
        Singleton.setFragmentManager(getSupportFragmentManager());

        buttons = (LinearLayout)findViewById(R.id.buttons);
        home = (ImageButton)findViewById(R.id.home);
        home.setOnClickListener(this);
        track = (ImageButton)findViewById(R.id.track);
        track.setOnClickListener(this);
        error = (ImageButton)findViewById(R.id.error);
        error.setOnClickListener(this);
        config = (ImageButton)findViewById(R.id.config);
        config.setOnClickListener(this);

        setToolbar();
        initFragments();

        if(!Singleton.getSettings().getBoolean(Constants.FLAG_LOGIN, false))
            initLoginFragment();
        else {
            if(Singleton.getSettings().getString(Constants.JSON_LOGIN, "").length() > 0)
                getResponse(Singleton.getSettings().getString(Constants.JSON_LOGIN, ""));
            else
                initLoginFragment();
        }

        Singleton.getBdh().eraseData();
    }

    private void setToolbar() {
        /*ImageView action_img = (ImageView)findViewById(R.id.back_btn);
        Singleton.setActionImg(action_img);
        action_img.setOnClickListener(this);

        ImageView profile = (ImageView)findViewById(R.id.profile);
        Singleton.setProfileImg(profile);
        profile.setOnClickListener(this);

        ImageView info = (ImageView)findViewById(R.id.info);
        Singleton.setInfoImg(info);
        info.setOnClickListener(this);

        ImageView addBtn = (ImageView)findViewById(R.id.addBtn);
        Singleton.setAddImg(addBtn);
        addBtn.setOnClickListener(this);

        ImageView menu = (ImageView)findViewById(R.id.menu_btn);
        Singleton.setMenuImg(menu);
        menu.setOnClickListener(this);*/

        Button btn = (Button)findViewById(R.id.btn);
        Singleton.setBtn(btn);
        btn.setOnClickListener(this);

        TextView action_txt = (TextView)findViewById(R.id.action_txt);
        Singleton.setActionTxt(action_txt);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            action_txt.setText("MI v."+pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        RelativeLayout toolbar = (RelativeLayout)findViewById(R.id.toolbar);
        Singleton.setToolbar(toolbar);
        action_txt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyBD();
                return false;
            }
        });
    }

    private void initFragments(){
        loginFragment = new LoginFragment();
        mainFragment = new MainFragment();
        trackingFragment = new TrackingFragment();
        errorsFragment = new ErrorsFragment();
        configFragment = new ConfigFragment();
    }

    private void removeFragments(){
        if(Singleton.getCurrentFragment() != null){
            Log.d("fragment remove", Singleton.getCurrentFragment().getClass().toString());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(Singleton.getCurrentFragment()).commit();
        }
    }

    private void initLoginFragment(){
        buttons.setVisibility(View.GONE);
        if(Singleton.getCurrentFragment() != loginFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", mainContent.getId());
            if(loginFragment.getArguments() == null)
                loginFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(mainContent.getId(), loginFragment)
                    .addToBackStack(TAG)
                    .commit();
        }
    }

    public void initMainFragment(LoginObj loginObj){
        buttons.setVisibility(View.VISIBLE);
        if(Singleton.getCurrentFragment() != mainFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", mainContent.getId());
            bundle.putSerializable("loginObj", loginObj);
            if(mainFragment.getArguments() == null)
                mainFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(mainContent.getId(), mainFragment).addToBackStack(TAG).commit();
        }
    }

    public void initConfigFragment(){
        buttons.setVisibility(View.VISIBLE);
        if(Singleton.getCurrentFragment() != configFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", mainContent.getId());
            //bundle.putSerializable("loginObj", loginObj);
            if(configFragment.getArguments() == null)
                configFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(mainContent.getId(), configFragment).addToBackStack(TAG).commit();
        }
    }

    public void initTrackFragment(){
        buttons.setVisibility(View.VISIBLE);
        if(Singleton.getCurrentFragment() != trackingFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", mainContent.getId());
            //bundle.putSerializable("loginObj", loginObj);
            if(trackingFragment.getArguments() == null)
                trackingFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(mainContent.getId(), trackingFragment).addToBackStack(TAG).commit();
        }
    }

    public void initTrackErrorFragment(){
        buttons.setVisibility(View.VISIBLE);
        if(Singleton.getCurrentFragment() != errorsFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", mainContent.getId());
            //bundle.putSerializable("loginObj", loginObj);
            if(errorsFragment.getArguments() == null)
                errorsFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(mainContent.getId(), errorsFragment).addToBackStack(TAG).commit();
        }
    }
    /*private void initConnection(){
        Singleton.showLoadDialog(getSupportFragmentManager());
        JSONObject TrackUserInfo = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            json.put("SAASName", Constants.SAASName);
            json.put("TenantID", Constants.TenantID);
            json.put("UserLoginID", user.getText().toString());
            json.put("UserPwsdID", pass.getText().toString());
            json.put("MobileType", "0004");
            TrackUserInfo.put("trackUserInfo", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] objs = new Object[]{Constants.URL_ValidateUser, 0, this, TrackUserInfo};
        ConnectToServer connectToServer = new ConnectToServer(objs);
    }*/

    public void getResponse(String arg){
        Log.d("response login", arg);
        try {
            JSONObject jsonObject = new JSONObject(arg);
            JSONObject ValidateUserResult = jsonObject.getJSONObject("ValidateUserResult");
            if(ValidateUserResult.getString("ServerStatus").equals("DONE") &&
                    ValidateUserResult.getString("IsValidUser").equals("TRUE")){
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
                initMainFragment(loginObj);
            } else {
                Singleton.dissmissLoad();
                Singleton.showCustomDialog(getSupportFragmentManager(), "¡Atención!", jsonObject.getString("mensaje"), "Aceptar", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.savePreferences(Constants.FLAG_LOGIN, false);
            Singleton.dissmissLoad();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn:
                Intent intent = new Intent(Singleton.getCurrentActivity(), SendService.class);
                stopService(intent);
                long day = System.currentTimeMillis();
                String MobileTrackDate = dateFormat(day);
                String UTCTrackDate = dateFormatUniversal(day);
                Singleton.getBdh().insertNewTrack(MobileTrackDate, UTCTrackDate, "0", "0", "0", day);
                Singleton.getBdh().updateTrack("FAIL", "Cerrar Sesión", 0, day);
                initConnection();
                break;
            case R.id.home:
                home.setImageResource(R.drawable.tab_home);
                track.setImageResource(R.drawable.tab_tracking_off);
                error.setImageResource(R.drawable.tab_system_off);
                config.setImageResource(R.drawable.tab_config_off);
                initMainFragment(Singleton.getLoginObj());
                break;
            case R.id.track:
                home.setImageResource(R.drawable.tab_home_off);
                track.setImageResource(R.drawable.tab_tracking);
                error.setImageResource(R.drawable.tab_system_off);
                config.setImageResource(R.drawable.tab_config_off);
                initTrackFragment();
                break;
            case R.id.error:
                home.setImageResource(R.drawable.tab_home_off);
                track.setImageResource(R.drawable.tab_tracking_off);
                error.setImageResource(R.drawable.tab_system);
                config.setImageResource(R.drawable.tab_config_off);
                initTrackErrorFragment();
                break;
            case R.id.config:
                home.setImageResource(R.drawable.tab_home_off);
                track.setImageResource(R.drawable.tab_tracking_off);
                error.setImageResource(R.drawable.tab_system_off);
                config.setImageResource(R.drawable.tab_config);
                initConfigFragment();
                break;
        }
    }

    public void copyBD(){
        try {
            String path = Singleton.getCacheCarpet().getAbsolutePath()+"/Tracking.sqlite";
            Log.d("copy db", path);
            OutputStream databaseOutputStream = new FileOutputStream(path);
            InputStream databaseInputStream;

            byte[] buffer = new byte[1024];
            @SuppressWarnings("unused")
            int length;

            File file = new File(DB_PATH, "Tracking");
            databaseInputStream = new FileInputStream(file);

            while ((length = databaseInputStream.read(buffer)) > 0) {
                databaseOutputStream.write(buffer);
            }

            databaseInputStream.close();
            databaseOutputStream.flush();
            databaseOutputStream.close();
            //file.delete();
            //Log.v("copyDataBase()", "copy ended");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setArray(ArrayList<ConfigObj> arg){
        array = arg;
    }

    public ArrayList<ConfigObj> getArray() {
        return array;
    }

    @Override
    public void onBackPressed(){
        if(Singleton.getCurrentFragment().getClass() == MainFragment.class) {

        } else if(Singleton.getCurrentFragment().getClass() == LoginFragment.class){
            finish();
        } else if(Singleton.getCurrentFragment().getClass() == ConfigFragment.class){

        } else if(Singleton.getCurrentFragment().getClass() == ErrorsFragment.class){

        } else if(Singleton.getCurrentFragment().getClass() == TrackingFragment.class){

        } else
            super.onBackPressed();
    }

    //*******************************
    private void initConnection() {
        Singleton.showLoadDialog(getSupportFragmentManager());
        parseLogin(Singleton.getSettings().getString(Constants.JSON_LOGIN, ""));
        JSONObject TrackUserInfo = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            json.put("SAASName", Constants.SAASName);
            json.put("TenantID", Constants.TenantID);

            JSONArray TrackGeoItems = new JSONArray();
            tracks = Singleton.getBdh().getTrackList("FAIL");
            for (int i = 0; i < tracks.size(); i++) {
                JSONObject TrackGeoItem = new JSONObject();
                TrackGeoItem.put("UserLoginID", Singleton.getSettings().getString(Constants.UserLoginID_TAG, ""));
                TrackGeoItem.put("ErrorDateTime", tracks.get(i).MobileTrackDate);
                TrackGeoItem.put("ErrorEvent", tracks.get(i).GpsTrackStatus);
                TrackGeoItem.put("ErrorDetail", tracks.get(i).GpsErrorCode);
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

    public void getTrackResponse(String result) {
        Log.d("gps error response", result);
        try {
            JSONObject json = new JSONObject(result);
            JSONObject TrackMobilePositionResult = json.getJSONObject("SendErrorLogResult");
            if(TrackMobilePositionResult.getString("ServerStatus").equals("DONE") &&
                    TrackMobilePositionResult.getString("ServerErrorCode").equals("0000")){
                for(int i = 0; i < tracks.size(); i++){
                    Singleton.getBdh().updateTrack(1, tracks.get(i).ID);
                }
            }
            Singleton.savePreferences(Constants.JSON_LOGIN, "");
            Singleton.savePreferences(Constants.FLAG_LOGIN, false);
            Singleton.setLoginObj(null);
            Singleton.getBdh().eraseAllData();
            initLoginFragment();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tracks.clear();
        Singleton.dissmissLoad();
    }


    private String dateFormat(long time) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd kk:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        date = simpleDateFormat.format(new Date(time));
        return date;
    }

    private String dateFormatUniversal(long time) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd kk:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = simpleDateFormat.format(new Date(time));
        return date;
    }

}
