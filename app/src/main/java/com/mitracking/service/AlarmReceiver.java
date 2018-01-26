package com.mitracking.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mitracking.Singleton;
import com.mitracking.objs.LoginObj;
import com.mitracking.utils.ConnectToServer;
import com.mitracking.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Singleton.getInstance();
        initConnection();
    }

    private void initConnection(){
        parseLogin(Singleton.getSettings().getString(Constants.JSON_LOGIN, ""));
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
        Object[] objs = new Object[]{Constants.URL_GetConfiguration, 4, this, TrackUserInfo};
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

            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

}