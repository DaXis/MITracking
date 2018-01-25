package com.mitracking.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mitracking.MainActivity;
import com.mitracking.R;
import com.mitracking.Singleton;
import com.mitracking.adapter.ConfigAdapter;
import com.mitracking.objs.ConfigObj;
import com.mitracking.objs.LoginObj;
import com.mitracking.utils.ConnectToServer;
import com.mitracking.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private TextView user, email;
    private ListView config;
    private ConfigAdapter adapter;

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

        user = (TextView)rootView.findViewById(R.id.user);
        email = (TextView)rootView.findViewById(R.id.email);
        config = (ListView)rootView.findViewById(R.id.config);

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

                user.setText(Singleton.getLoginObj().EmployeeName);

                ArrayList<ConfigObj> array = new ArrayList<>();
                ConfigObj configObj = new ConfigObj();
                configObj.tag = Constants.EmailSupport_TAG;
                configObj.value = Constants.EmailSupport;
                array.add(configObj);
                ConfigObj configObj0 = new ConfigObj();
                configObj0.tag = Constants.SAASName_TAG;
                configObj0.value = Constants.SAASName;
                array.add(configObj0);
                ConfigObj configObj1 = new ConfigObj();
                configObj1.tag = Constants.TenantID_TAG;
                configObj1.value = Constants.TenantID;
                array.add(configObj1);
                ConfigObj configObj2 = new ConfigObj();
                configObj2.tag = Constants.TrackDaysHistory_TAG;
                configObj2.value = Constants.TrackDaysHistory;
                array.add(configObj2);
                ConfigObj configObj3 = new ConfigObj();
                configObj3.tag = Constants.TrackMode_TAG;
                configObj3.value = Constants.TrackMode;
                array.add(configObj3);
                ConfigObj configObj4 = new ConfigObj();
                configObj4.tag = Constants.TrackModeValue_TAG;
                configObj4.value = Constants.TrackModeValue;
                array.add(configObj4);
                ConfigObj configObj5 = new ConfigObj();
                configObj5.tag = Constants.TrackWeekEndDays_TAG;
                configObj5.value = Constants.TrackWeekEndDays;
                array.add(configObj5);
                ConfigObj configObj6 = new ConfigObj();
                configObj6.tag = Constants.TrackWeekEndHours_TAG;
                configObj6.value = Constants.TrackWeekEndHours;
                array.add(configObj6);
                ConfigObj configObj7 = new ConfigObj();
                configObj7.tag = Constants.TrackWorkDays_TAG;
                configObj7.value = Constants.TrackWorkDays;
                array.add(configObj7);
                ConfigObj configObj8 = new ConfigObj();
                configObj8.tag = Constants.TrackWorkHours_TAG;
                configObj8.value = Constants.TrackWorkHours;
                array.add(configObj8);
                ConfigObj configObj9 = new ConfigObj();
                configObj9.tag = Constants.URL_GetConfiguration_TAG;
                configObj9.value = Constants.URL_GetConfiguration;
                array.add(configObj9);
                ConfigObj configObj10 = new ConfigObj();
                configObj10.tag = Constants.URL_TrackMobilePosition_TAG;
                configObj10.value = Constants.URL_TrackMobilePosition;
                array.add(configObj10);
                ConfigObj configObj11 = new ConfigObj();
                configObj11.tag = Constants.URL_ValidateUser_TAG;
                configObj11.value = Constants.URL_ValidateUser;
                array.add(configObj11);

                if(Constants.ShowAdvanceConfig.equals("TRUE")){
                    adapter = new ConfigAdapter(this, array);
                    config.setAdapter(adapter);
                }

                Singleton.dissmissLoad();
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

}
