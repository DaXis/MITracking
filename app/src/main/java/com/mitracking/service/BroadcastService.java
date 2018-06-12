package com.mitracking.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mitracking.Singleton;
import com.mitracking.utils.Constants;

public class BroadcastService extends BroadcastReceiver {

    @Override
    public void onReceive(Context aContext, Intent aIntent) {
        Singleton.getInstance();
        if(Singleton.getSettings().getString(Constants.IsValidConfiguration_TAG, "").equals("TRUE"))
            if(!isMyServiceRunning(aContext, SendService.class)) {
                Log.d("BroadcastService", "reinit service");
                aContext.startService(new Intent(aContext, SendService.class));
            }
    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}