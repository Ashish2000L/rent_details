package com.example.rent_details;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class forgroundservice extends Service {

    public static final  String  CHANNEL_ID="channel";
    public static final  String  CHANNEL_IDs="channel1";
    public static final String  shared_pref = "example";
    public static final String  TAG = "forgroundserviceexample";
    int num;
    int y=0,id=101;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        num = sharedPreferences.getInt("number",1);
        createprefs(num+1);
        createnotificationchannel();
    }
    public void createprefs(int x){
        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("number",x);
        editor.apply();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createnotificationchannel() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,"Example Channel", NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);

            NotificationChannel serviceChannel1 = new NotificationChannel(CHANNEL_IDs,"Example Channel1", NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager1 = getSystemService(NotificationManager.class);
            assert manager1 != null;
            manager1.createNotificationChannel(serviceChannel1);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Rent Detail running")
                .setContentText("Rent Details is running in background")
                .setSmallIcon(R.mipmap.profile1_round)
                .build();

        startForeground(100,notification);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String title = "Started "+num+" times";
                final String body = "Running for "+y+" minutes";
                Notification notification1 = new NotificationCompat.Builder(forgroundservice.this,CHANNEL_IDs)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.mipmap.profile1_round)
                        .build();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    NotificationManager manager1 = getSystemService(NotificationManager.class);
                    assert manager1 != null;
                    if(id>100000){
                        id=101;
                    }
                    manager1.notify(id,notification1);
                }
                handler.postDelayed(this,60000);
                Log.d(TAG, "run: "+y);
                y++;
                id++;
            }
        };
        runnable.run();

        return START_STICKY;
    }
}
