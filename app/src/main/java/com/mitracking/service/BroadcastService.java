package com.mitracking.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mitracking.Singleton;
import com.mitracking.utils.Constants;

public class BroadcastService extends BroadcastReceiver {

    @Override
    public void onReceive(Context aContext, Intent aIntent) {
        Singleton.getInstance();
        if(Singleton.getSettings().getString(Constants.IsValidConfiguration_TAG, "").equals("TRUE"))
            aContext.startService(new Intent(aContext, SendService.class));
    }
}