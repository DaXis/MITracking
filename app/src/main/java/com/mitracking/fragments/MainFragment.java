package com.mitracking.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.mitracking.MainActivity;
import com.mitracking.R;
import com.mitracking.Singleton;
import com.mitracking.adapter.ConfigAdapter;
import com.mitracking.objs.ConfigObj;
import com.mitracking.objs.LoginObj;
import com.mitracking.service.SendService;
import com.mitracking.utils.ConnectToServer;
import com.mitracking.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private TextView user, email, number, user_test;
    //private ListView config;
    private Switch switch1, switch2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("setCurrentFragment", this.getClass().getSimpleName());
        Singleton.setCurrentFragment(this);
        Singleton.getToolbar().setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main, container, false);

        switch1 = (Switch)rootView.findViewById(R.id.switch1);
        switch1.setClickable(false);
        switch1.setFocusable(false);
        switch2 =  (Switch)rootView.findViewById(R.id.switch2);
        switch2.setClickable(false);
        switch2.setFocusable(false);

        user = (TextView)rootView.findViewById(R.id.user);
        email = (TextView)rootView.findViewById(R.id.email);
        number = (TextView)rootView.findViewById(R.id.number);
        user_test = (TextView)rootView.findViewById(R.id.user_test);

        if(!Singleton.getSettings().getBoolean(Constants.GPS_TAG, false)){
            switch1.setChecked(false);
            switch2.setChecked(true);
        } else {
            switch1.setChecked(true);
            switch2.setChecked(false);
        }

        initConnection();
        return rootView;
    }

    @Override
    public void onClick(View v) {

    }

    private void initConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        JSONObject TrackUserInfo = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            json.put("SAASName", Constants.SAASName);
            json.put("TenantID", Constants.TenantID);
            json.put("UserLoginID", Constants.UserLoginID);
            json.put("SessionID", Singleton.getLoginObj().SessionID);
            TrackUserInfo.put("trackConfigInfo", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] objs = new Object[]{Constants.URL_GetConfiguration, 2, this, TrackUserInfo};
        ConnectToServer connectToServer = new ConnectToServer(objs);
    }

    public void getResponse(String arg){
        Log.d("response config", arg);
        try {
            JSONObject jsonObject = new JSONObject(arg);
            JSONObject GetConfigurationResult = jsonObject.getJSONObject("GetConfigurationResult");
            if(GetConfigurationResult.getString("ServerStatus").equals("DONE") &&
                    GetConfigurationResult.getString("IsValidConfiguration").equals("TRUE")){

                Singleton.savePreferences(Constants.EmailSupport_TAG,
                        GetConfigurationResult.getString("EmailSupport"));
                Singleton.savePreferences(Constants.IsValidConfiguration_TAG,
                        GetConfigurationResult.getString("IsValidConfiguration"));
                Singleton.savePreferences(Constants.ReadConfigurationAt_TAG,
                        GetConfigurationResult.getString("ReadConfigurationAt"));
                Singleton.savePreferences(Constants.RefreshConfig_TAG,
                        GetConfigurationResult.getString("RefreshConfig"));
                Singleton.savePreferences(Constants.SAASName_TAG,
                        GetConfigurationResult.getString("SAASName"));
                Singleton.savePreferences(Constants.ShowAdvanceConfig_TAG,
                        GetConfigurationResult.getString("ShowAdvanceConfig"));
                Singleton.savePreferences(Constants.TenantID_TAG,
                        GetConfigurationResult.getString("TenantID"));
                Singleton.savePreferences(Constants.TrackDaysHistory_TAG,
                        GetConfigurationResult.getString("TrackDaysHistory"));
                Singleton.savePreferences(Constants.TrackMode_TAG,
                        GetConfigurationResult.getString("TrackMode"));
                Singleton.savePreferences(Constants.TrackModeValue_TAG,
                        GetConfigurationResult.getString("TrackModeValue"));
                Singleton.savePreferences(Constants.TrackWeekEndDays_TAG,
                        GetConfigurationResult.getString("TrackWeekEndDays"));
                Singleton.savePreferences(Constants.TrackWeekEndHours_TAG,
                        GetConfigurationResult.getString("TrackWeekEndHours"));
                Singleton.savePreferences(Constants.TrackWorkDays_TAG,
                        GetConfigurationResult.getString("TrackWorkDays"));
                Singleton.savePreferences(Constants.TrackWorkHours_TAG,
                        GetConfigurationResult.getString("TrackWorkHours"));
                Singleton.savePreferences(Constants.URL_GetConfiguration_TAG,
                        GetConfigurationResult.getString("URL_GetConfiguration"));
                Singleton.savePreferences(Constants.URL_TrackMobilePosition_TAG,
                        GetConfigurationResult.getString("URL_TrackMobilePosition"));
                Singleton.savePreferences(Constants.URL_ValidateUser_TAG,
                        GetConfigurationResult.getString("URL_ValidateUser"));

                Singleton.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable(){
                            public void run(){
                                user.setText(Singleton.getSettings().getString(Constants.UserLoginID_TAG, ""));
                                email.setText(Singleton.getSettings().getString(Constants.EmailSupport_TAG, ""));
                                number.setText(Singleton.getLoginObj().AlignmentName);
                                user_test.setText(Singleton.getLoginObj().EmployeeName);

                                ArrayList<ConfigObj> array = new ArrayList<>();
                                ConfigObj configObj = new ConfigObj();
                                configObj.tag = Constants.EmailSupport_TAG;
                                configObj.value = Singleton.getSettings().getString(Constants.EmailSupport_TAG, "");
                                array.add(configObj);
                                ConfigObj configObj0 = new ConfigObj();
                                configObj0.tag = Constants.SAASName_TAG;
                                configObj0.value = Singleton.getSettings().getString(Constants.SAASName_TAG, "");
                                array.add(configObj0);
                                ConfigObj configObj1 = new ConfigObj();
                                configObj1.tag = Constants.TenantID_TAG;
                                configObj1.value = Singleton.getSettings().getString(Constants.TenantID_TAG, "");
                                array.add(configObj1);
                                ConfigObj configObj2 = new ConfigObj();
                                configObj2.tag = Constants.TrackDaysHistory_TAG;
                                configObj2.value = Singleton.getSettings().getString(Constants.TrackDaysHistory_TAG, "");
                                array.add(configObj2);
                                ConfigObj configObj3 = new ConfigObj();
                                configObj3.tag = Constants.TrackMode_TAG;
                                configObj3.value = Singleton.getSettings().getString(Constants.TrackMode_TAG, "");
                                array.add(configObj3);
                                ConfigObj configObj4 = new ConfigObj();
                                configObj4.tag = Constants.TrackModeValue_TAG;
                                configObj4.value = Singleton.getSettings().getString(Constants.TrackModeValue_TAG, "");
                                array.add(configObj4);
                                ConfigObj configObj5 = new ConfigObj();
                                configObj5.tag = Constants.TrackWeekEndDays_TAG;
                                configObj5.value = Singleton.getSettings().getString(Constants.TrackWeekEndDays_TAG, "");
                                array.add(configObj5);
                                ConfigObj configObj6 = new ConfigObj();
                                configObj6.tag = Constants.TrackWeekEndHours_TAG;
                                configObj6.value = Singleton.getSettings().getString(Constants.TrackWeekEndHours_TAG, "");
                                array.add(configObj6);
                                ConfigObj configObj7 = new ConfigObj();
                                configObj7.tag = Constants.TrackWorkDays_TAG;
                                configObj7.value = Singleton.getSettings().getString(Constants.TrackWorkDays_TAG, "");
                                array.add(configObj7);
                                ConfigObj configObj8 = new ConfigObj();
                                configObj8.tag = Constants.TrackWorkHours_TAG;
                                configObj8.value = Singleton.getSettings().getString(Constants.TrackWorkHours_TAG, "");
                                array.add(configObj8);
                                ConfigObj configObj9 = new ConfigObj();
                                configObj9.tag = Constants.URL_GetConfiguration_TAG;
                                configObj9.value = Singleton.getSettings().getString(Constants.URL_GetConfiguration_TAG, "");
                                array.add(configObj9);
                                ConfigObj configObj10 = new ConfigObj();
                                configObj10.tag = Constants.URL_TrackMobilePosition_TAG;
                                configObj10.value = Singleton.getSettings().getString(Constants.URL_TrackMobilePosition_TAG, "");
                                array.add(configObj10);
                                ConfigObj configObj11 = new ConfigObj();
                                configObj11.tag = Constants.URL_ValidateUser_TAG;
                                configObj11.value = Singleton.getSettings().getString(Constants.URL_ValidateUser_TAG, "");
                                array.add(configObj11);
                                ((MainActivity)Singleton.getCurrentActivity()).setArray(array);

                                Log.d("isMyServiceRunning", ""+isMyServiceRunning(SendService.class));
                                if(!isMyServiceRunning(SendService.class)){
                                    //stopService(new Intent(ActivityName.this, ServiceClassName.class));
                                    Intent intent = new Intent(Singleton.getCurrentActivity(), SendService.class);
                                    Singleton.getCurrentActivity().startService(intent);
                                }

                                Singleton.dissmissLoad();
                            };
                        }, Constants.DURACION_SPLASH);
                    }
                });
            } else {
                Singleton.dissmissLoad();
                Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                        "Error en el servicio de configuración", "Aceptar", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.savePreferences(Constants.FLAG_LOGIN, false);
            Singleton.dissmissLoad();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)Singleton.getCurrentActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
