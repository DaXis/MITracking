package com.mitracking;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.provider.Settings.Secure;

import com.mitracking.objs.BatteryObj;
import com.mitracking.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private final int DURACION_SPLASH = 3000;
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Thread.setDefaultUncaughtExceptionHandler(new ForceCloseCatch(this));
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.splash);
        Singleton.setCurrentActivity(this);

        String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        Singleton.savePreferences(Constants.MobileID_TAG, android_id);

        Singleton.setFragmentManager(getSupportFragmentManager());

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M)
            checkLocationPermission();
        else
            onUIThread();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
            Singleton.savePreferences(Constants.GPS_TAG, false);
            return;
        } else {
            Singleton.savePreferences(Constants.GPS_TAG, true);
            onUIThread();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Singleton.savePreferences(Constants.GPS_TAG, true);
                    onUIThread();
                } else {
                    Singleton.savePreferences(Constants.GPS_TAG, false);
                    Singleton.getEditor().commit();
                }
                break;
        }
    }

    private void onUIThread(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable(){
                    public void run(){
                        mainIntent();
                    };
                }, DURACION_SPLASH);
            }
        });
    }

    private void mainIntent(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
