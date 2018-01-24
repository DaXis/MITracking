package com.mitracking.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mitracking.Singleton;
import com.mitracking.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SendService extends Service {

    private Timer timer;
    private static long TIME;

    @Override
    public void onCreate() {
        Log.d("Service", "onCreate");
        Singleton.getInstance();
        int secons = Integer.parseInt(Constants.TrackModeValue);
        TIME = TimeUnit.SECONDS.toMillis(secons);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStartCommand");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                Log.d("Service", "onStartCommand timer");
            }
        }, 0, TIME);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "onDestroy");
        timer.cancel();
        timer = null;
    }
}

//cambio
