package com.mitracking.utils;

import com.mitracking.Singleton;

public class Constants {

    public static final String JSON_LOGIN = "json_login";
    public static final String FLAG_LOGIN = "flag_login";

    //Constantes de precarga de al app
    public static final String SAASName_TAG = "SAASName";
    public static final String TenantID_TAG = "TenantID";
    public static final String EmailSupport_TAG = "EmailSupport";
    public static final String PhoneSupport_TAG = "PhoneSupport ";
    public static final String ReadConfigurationAt_TAG = "ReadConfigurationAt";
    public static final String SaveTimeToDB_TAG = "SaveTimeToDB";
    public static final String URL_ValidateUser_TAG = "URL_ValidateUser";
    public static final String URL_GetConfiguration_TAG = "URL_GetConfiguration";
    public static final String URL_TrackMobilePosition_TAG = "URL_TrackMobilePosition";
    public static final String URL_ErrorTrack_TAG = "URL_SendErrorLog";
    public static final String MobileID_TAG = "MobileID";
    public static final String GPS_TAG = "gps_flag";
    public static final int DURACION_SPLASH = 3000;

    public static String SAASName = Singleton.getSettings().getString(SAASName_TAG, "SAAS3");
    public static String TenantID = Singleton.getSettings().getString(TenantID_TAG, "500041");
    public static String EmailSupport = Singleton.getSettings().getString(EmailSupport_TAG, "MI CALL VALIDATOR - LCL");
    public static String ReadConfigurationAt = Singleton.getSettings().getString(ReadConfigurationAt_TAG, "10:00");
    public static String SaveTimeToDB = Singleton.getSettings().getString(SaveTimeToDB_TAG, "4");
    public static String PhoneSupport = Singleton.getSettings().getString(PhoneSupport_TAG, "TEL://5555555555555");

    public static String URL_ValidateUser = Singleton.getSettings().getString(URL_ValidateUser_TAG,
//            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/ValidateUser");
            "https://la-mi-salesdemo.cegedim.com/PS3SGFR01/MI_TRK_SGFRD_WS.svc/ValidateUser");
    public static String URL_GetConfiguration = Singleton.getSettings().getString(URL_GetConfiguration_TAG,
//            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/GetConfiguration");
            "https://la-mi-salesdemo.cegedim.com/PS3SGFR01/MI_TRK_SGFRD_WS.svc/GetConfiguration");
    public static String URL_TrackMobilePosition = Singleton.getSettings().getString(URL_TrackMobilePosition_TAG,
//            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/TrackMobilePosition");
            "https://la-mi-salesdemo.cegedim.com/PS3SGFR01/MI_TRK_SGFRD_WS.svc/TrackMobilePosition");
    public static String URL_ErrorTrack = Singleton.getSettings().getString(URL_ErrorTrack_TAG,
//            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/SendErrorLog");
            "https://la-mi-salesdemo.cegedim.com/PS3SGFR01/MI_TRK_SGFRD_WS.svc/SendErrorLog");

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
    public static String TrackModeValue = Singleton.getSettings().getString(TrackModeValue_TAG, "600");

    public static final String TrackWeekEndDays_TAG = "TrackWeekEndDays";
    public static String TrackWeekEndDays = Singleton.getSettings().getString(TrackWeekEndDays_TAG, "");

    public static final String TrackWeekEndHours_TAG = "TrackWeekEndHours";
    public static String TrackWeekEndHours = Singleton.getSettings().getString(TrackWeekEndHours_TAG, "");

    public static final String TrackWorkDays_TAG = "TrackWorkDays";
    public static String TrackWorkDays = Singleton.getSettings().getString(TrackWorkDays_TAG, "01|02|03|04|05");

    public static final String TrackWorkHours_TAG = "TrackWorkHours";
    public static String TrackWorkHours = Singleton.getSettings().getString(TrackWorkHours_TAG, "08:00-13:00|14:00-17:00");

    public static final String SendTrackLogValue_TAG = "SendTrackLogValue";
    public static String SendTrackLogValue = Singleton.getSettings().getString(SendTrackLogValue_TAG, "1");

    public static final String DeleteLogAt_TAG = "DeleteLogAt";
    public static String DeleteLogAt = Singleton.getSettings().getString(DeleteLogAt_TAG, "08");

    public static final String LogDays_TAG = "LogDays";
    public static int LogDays = Singleton.getSettings().getInt(LogDays_TAG, 30);

    public static final String debugTrack_TAG = "debugTrack";
    public static Boolean debugTrack = Singleton.getSettings().getBoolean(debugTrack_TAG, false);

}


/* Valores de prueba
    public static String SAASName = Singleton.getSettings().getString(SAASName_TAG, "SLDEMO");
    public static String TenantID = Singleton.getSettings().getString(TenantID_TAG, "500010");
    public static String EmailSupport = Singleton.getSettings().getString(EmailSupport_TAG, "MI CALL VALIDATOR - LCL");
    public static String ReadConfigurationAt = Singleton.getSettings().getString(ReadConfigurationAt_TAG, "10:00");
    public static String SaveTimeToDB = Singleton.getSettings().getString(SaveTimeToDB_TAG, "4");
    public static String PhoneSupport = Singleton.getSettings().getString(PhoneSupport_TAG, "TEL://5555555555555");

    public static String SAASName = Singleton.getSettings().getString(SAASName_TAG, "SAAS3");
    public static String TenantID = Singleton.getSettings().getString(TenantID_TAG, "500041");
    public static String EmailSupport = Singleton.getSettings().getString(EmailSupport_TAG, "MI CALL VALIDATOR - LCL");
    public static String ReadConfigurationAt = Singleton.getSettings().getString(ReadConfigurationAt_TAG, "10:00");
    public static String SaveTimeToDB = Singleton.getSettings().getString(SaveTimeToDB_TAG, "4");
    public static String PhoneSupport = Singleton.getSettings().getString(PhoneSupport_TAG, "TEL://5555555555555");

    public static String URL_ValidateUser = Singleton.getSettings().getString(URL_ValidateUser_TAG,
//            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/ValidateUser");
    "https://la-mi-salesdemo.cegedim.com/PS3SGFR01/MI_TRK_SGFRD_WS.svc/ValidateUser");
    public static String URL_GetConfiguration = Singleton.getSettings().getString(URL_GetConfiguration_TAG,
//            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/GetConfiguration");
    "https://la-mi-salesdemo.cegedim.com/PS3SGFR01/MI_TRK_SGFRD_WS.svc/GetConfiguration");
    public static String URL_TrackMobilePosition = Singleton.getSettings().getString(URL_TrackMobilePosition_TAG,
//            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/TrackMobilePosition");
    "https://la-mi-salesdemo.cegedim.com/PS3SGFR01/MI_TRK_SGFRD_WS.svc/TrackMobilePosition");
    public static String URL_ErrorTrack = Singleton.getSettings().getString(URL_ErrorTrack_TAG,
//            "https://la-mi-salesdemo.cegedim.com/MITrackingWS/MITrackingWS.svc/SendErrorLog");
    "https://la-mi-salesdemo.cegedim.com/PS3SGFR01/MI_TRK_SGFRD_WS.svc/SendErrorLog");
 */