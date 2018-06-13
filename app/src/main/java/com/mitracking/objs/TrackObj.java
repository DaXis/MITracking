package com.mitracking.objs;

import java.io.Serializable;

public class TrackObj implements Serializable {

    public String MobileTrackDate, UTCTrackDate, Latitude, Longitude, GpsAccuracy, GpsTrackStatus = "", GpsErrorCode = "",BatteryStatus;
    public int ID, TrackGeoItemSend,BatteryPercent;
    public long TrackGeoItemDate;

}
