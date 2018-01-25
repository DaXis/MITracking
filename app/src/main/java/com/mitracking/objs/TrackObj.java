package com.mitracking.objs;

import java.io.Serializable;

public class TrackObj implements Serializable {

    public String MobileTrackDate, UTCTrackDate, Latitude, Longitude, GpsAccuracy, GpsTrackStatus = "", GpsErrorCode = "";
    public int ID, TrackGeoItemSend;
    public long TrackGeoItemDate;

}
