package com.mitracking.utils;

import com.mitracking.Singleton;

public class Constants {

    public static final String JSON_LOGIN = "json_login";
    public static final String FLAG_LOGIN = "flag_login";

    //Constantes de precarga de al app
    public static final String SAASName_TAG = "SAASName";
    public static final String TenantID_TAG = "TenantID";
    public static final String EmailSupport_TAG = "EmailSupport";
    public static final String ReadConfigurationAt_TAG = "ReadConfigurationAt";
    public static final String URL_ValidateUser_TAG = "URL_ValidateUser";
    public static final String URL_GetConfiguration_TAG = "URL_GetConfiguration";
    public static final String URL_TrackMobilePosition_TAG = "URL_TrackMobilePosition";

    public static String SAASName = Singleton.getSettings().getString(SAASName_TAG, "SAAS1");
    public static String TenantID = Singleton.getSettings().getString(TenantID_TAG, "500084");
    public static String EmailSupport = Singleton.getSettings().getString(EmailSupport_TAG, "caguirre@mx.imshealth.com");
    public static String ReadConfigurationAt = Singleton.getSettings().getString(ReadConfigurationAt_TAG, "11:00");

    public static String URL_ValidateUser = Singleton.getSettings().getString(URL_ValidateUser_TAG,
            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/ValidateUser");
    public static String URL_GetConfiguration = Singleton.getSettings().getString(URL_GetConfiguration_TAG,
            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/GetConfiguration");
    public static String URL_TrackMobilePosition = Singleton.getSettings().getString(URL_TrackMobilePosition_TAG,
            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/TrackMobilePosition");

    //Constantes de login
    public static final String UserLoginID_TAG = "UserLoginID";
    public static String UserLoginID = Singleton.getSettings().getString(UserLoginID_TAG, "");

    //Constantes de configuracion
    public static final String IsValidConfiguration_TAG = "IsValidConfiguration";
    public static String IsValidConfiguration = Singleton.getSettings().getString(IsValidConfiguration_TAG, "");
    public static final String RefreshConfig_TAG = "RefreshConfig";
    public static String RefreshConfig = Singleton.getSettings().getString(RefreshConfig_TAG, "");
    public static final String ShowAdvanceConfig_TAG = "ShowAdvanceConfig";
    public static String ShowAdvanceConfig = Singleton.getSettings().getString(ShowAdvanceConfig_TAG, "");
    public static final String TrackDaysHistory_TAG = "TrackDaysHistory";
    public static String TrackDaysHistory = Singleton.getSettings().getString(TrackDaysHistory_TAG, "");
    public static final String TrackMode_TAG = "TrackMode";
    public static String TrackMode = Singleton.getSettings().getString(TrackMode_TAG, "");
    public static final String TrackModeValue_TAG = "TrackModeValue";
    public static String TrackModeValue = Singleton.getSettings().getString(TrackModeValue_TAG, "");
    public static final String TrackWeekEndDays_TAG = "TrackWeekEndDays";
    public static String TrackWeekEndDays = Singleton.getSettings().getString(TrackWeekEndDays_TAG, "");
    public static final String TrackWeekEndHours_TAG = "TrackWeekEndHours";
    public static String TrackWeekEndHours = Singleton.getSettings().getString(TrackWeekEndHours_TAG, "");
    public static final String TrackWorkDays_TAG = "TrackWorkDays";
    public static String TrackWorkDays = Singleton.getSettings().getString(TrackWorkDays_TAG, "");
    public static final String TrackWorkHours_TAG = "TrackWorkHours";
    public static String TrackWorkHours = Singleton.getSettings().getString(TrackWorkHours_TAG, "");

}
