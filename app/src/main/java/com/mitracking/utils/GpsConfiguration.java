package com.mitracking.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.mitracking.Singleton;

public class GpsConfiguration {

    public static LocationManager locationManager;
    private Context context;
    private Boolean provieneMapa;
    private final String TAG = "ConfiguracionGPS";
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private LocationListener locListener;
    private OnGpsLocationListener mListener;


    public GpsConfiguration(Context context, Boolean provieneMapa){
        this.context = context;
        this.provieneMapa = provieneMapa;
        locationManager = (LocationManager)  context.getSystemService(Context.LOCATION_SERVICE);
        mListener = (OnGpsLocationListener)context;
    }

    public boolean  configuracionLocationManager(){
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }else{
            LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
            LocationProvider networkProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
            if(provieneMapa) {
                locationManager.requestLocationUpdates(gpsProvider.getName(), 1L, 2F, (LocationListener) context);
                if (networkProvider != null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1L, 2F, (LocationListener) context);
                }

//	        Criteria locationCriteria = new Criteria();
//			// Y establecemos que se escuche al mejor proveedor de localización (
//			locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
//			Log.d("configuracion GPS", "si se configuran los valores del request");
//			locManager.requestLocationUpdates(
//					locManager.getBestProvider(locationCriteria, true), 1L, 2F,
//					(LocationListener) this);
            }else{
                //Log.i(TAG, "sin entra");
                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
                locationListenerEvent();
                locationManager.requestLocationUpdates(gpsProvider.getName(), 1L, 2F, locListener);
                if (networkProvider != null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1L, 2F, locListener);
                }
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                //      0, locListener);
            }
            return true;
        }
    }

    public void locationListenerEvent(){
        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                mListener.onGpsLocationInteraction(location);

            }

            public void onProviderDisabled(String provider) {}

            public void onProviderEnabled(String provider) {}

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                //Log.i("", "Provider Status: " + status);
                // descripcionEditText.append("Provider Status: " + status);
            }
        };
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public LocationManager removeUpdatesMapa(){
        return locationManager;
    }
    public void removeUpdates(){
        locationManager.removeUpdates(locListener);
    }

    public interface OnGpsLocationListener{
        public void onGpsLocationInteraction(Location location);
    }

    public boolean isGPSEnable(){
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Singleton.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog alerta = new AlertDialog.Builder(Singleton.getCurrentActivity()).create();
                    alerta.setTitle("Alerta");
                    alerta.setMessage("Es necesario encender el GPS");
                    alerta.setButton(-1, "Aceptar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    Singleton.getCurrentActivity().startActivity(i);
                                }
                            });
                    alerta.show();
                }
            });
            return false;
        } else {
            Log.d("GPS", "Encendido...");
            return true;
        }
    }

}
