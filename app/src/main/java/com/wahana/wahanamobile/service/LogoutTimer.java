package com.wahana.wahanamobile.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.wahana.wahanamobile.LoginActivity;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by team-it on 13/12/17.
 */

public class LogoutTimer extends Service {
    int time;
    SessionManager sesssion;
    Activity activity;
    Context context;
    
    public LogoutTimer() {
    }

    public LogoutTimer(Activity activity, Context context) {
        this.activity = activity;
        sesssion = new SessionManager(activity);
        this.context = context;
    }

    @Nullable
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        time = intent.getIntExtra("time", 1000);
        Timer timer = new Timer();
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, time);

        return START_STICKY;
    }

    public class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            if (applicationInForeground()){
                sesssion.logoutUser();
            }else{
                sesssion.clearSessionData();
            }
        }
    }

    public void updateTime(Activity activity,int time){
        this.activity=activity;
        this.time = time;
        sesssion = new SessionManager(activity);
    }

    private boolean applicationInForeground() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName
                .equalsIgnoreCase(activity.getPackageName())) {
            isActivityFound = true;
        }

        return isActivityFound;
    }
}
