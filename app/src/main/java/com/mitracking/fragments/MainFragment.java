package com.mitracking.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mitracking.MainActivity;
import com.mitracking.R;
import com.mitracking.Singleton;
import com.mitracking.objs.LoginObj;
import com.mitracking.utils.ConnectToServer;
import com.mitracking.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private TextView user, email;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main, container, false);

        user = (TextView)rootView.findViewById(R.id.user);
        email = (TextView)rootView.findViewById(R.id.email);

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
        Log.d("response login", arg);
        try {
            JSONObject jsonObject = new JSONObject(arg);
            JSONObject GetConfigurationResult = jsonObject.getJSONObject("GetConfigurationResult");
            if(GetConfigurationResult.getString("ServerStatus").equals("DONE") &&
                    GetConfigurationResult.getString("IsValidConfiguration").equals("TRUE")){

                Singleton.savePreferences(Constants.EmailSupport,
                        GetConfigurationResult.getString("EmailSupport"));
                Singleton.savePreferences(Constants.IsValidConfiguration,
                        GetConfigurationResult.getString("IsValidConfiguration"));
                Singleton.savePreferences(Constants.ReadConfigurationAt,
                        GetConfigurationResult.getString("ReadConfigurationAt"));
                Singleton.savePreferences(Constants.RefreshConfig,
                        GetConfigurationResult.getString("RefreshConfig"));
                Singleton.savePreferences(Constants.SAASName,
                        GetConfigurationResult.getString("SAASName"));
                Singleton.savePreferences(Constants.ShowAdvanceConfig,
                        GetConfigurationResult.getString("ShowAdvanceConfig"));
                Singleton.savePreferences(Constants.TenantID,
                        GetConfigurationResult.getString("TenantID"));
                Singleton.savePreferences(Constants.TrackDaysHistory,
                        GetConfigurationResult.getString("TrackDaysHistory"));
                Singleton.savePreferences(Constants.TrackMode,
                        GetConfigurationResult.getString("TrackMode"));
                Singleton.savePreferences(Constants.TrackModeValue,
                        GetConfigurationResult.getString("TrackModeValue"));
                Singleton.savePreferences(Constants.TrackWeekEndDays,
                        GetConfigurationResult.getString("TrackWeekEndDays"));
                Singleton.savePreferences(Constants.TrackWeekEndHours,
                        GetConfigurationResult.getString("TrackWeekEndHours"));
                Singleton.savePreferences(Constants.TrackWorkDays,
                        GetConfigurationResult.getString("TrackWorkDays"));
                Singleton.savePreferences(Constants.TrackWorkHours,
                        GetConfigurationResult.getString("TrackWorkHours"));
                Singleton.savePreferences(Constants.URL_GetConfiguration,
                        GetConfigurationResult.getString("URL_GetConfiguration"));
                Singleton.savePreferences(Constants.URL_TrackMobilePosition,
                        GetConfigurationResult.getString("URL_TrackMobilePosition"));
                Singleton.savePreferences(Constants.URL_ValidateUser,
                        GetConfigurationResult.getString("URL_ValidateUser"));

                user.setText(Singleton.getLoginObj().EmployeeName);
                //email.setText(Singleton.getLoginObj().EmployeeName);

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
