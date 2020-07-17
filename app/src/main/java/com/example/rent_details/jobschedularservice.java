package com.example.rent_details;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class jobschedularservice extends JobService {

    private static final String TAG="forgroundserviceexample";
    private boolean jobcancelled = false;
    forgroundservice forgroundservice = new forgroundservice();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onStartJob(JobParameters params) {

        Log.d(TAG, "Job started");
        if(!jobcancelled) {

            //shownotification("Job started in background ","job started");
            if (!ismyservicerunning(forgroundservice.getClass())) {
                startForegroundService(new Intent(jobschedularservice.this, forgroundservice.getClass()));
            }

        }else{
            Log.d(TAG, "job canceled");
        }

        Log.d(TAG, "job finished");
        jobFinished(params,true);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        Log.d(TAG, "job cancelled");
        jobcancelled=true;
        return true;
    }

    private boolean ismyservicerunning(Class<?> serviceclass){

        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceclass.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "running in jobservice" + serviceclass.getName());
                return true;
            }
        }
        Log.d(TAG, "not running in jobservice "+serviceclass.getName());
        return false;
    }

    private void shownotification(String body,String title){

        SharedPreferences sharedPreferences = getSharedPreferences("hello world",MODE_PRIVATE);
        int Notification_id = sharedPreferences.getInt("notifids",1000);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channel ="this is sample";
            long[] same={3000,3000};
            NotificationChannel channel1 = new NotificationChannel(channel,"show latest update notif", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is to fetch latest notification");
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel1.enableLights(true);
            channel1.shouldShowLights();
            channel1.enableVibration(true);
            channel1.setLightColor(Color.BLUE);
            channel1.setVibrationPattern(same);

            Intent transferintent = new Intent(jobschedularservice.this,login.class);
            transferintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntents = PendingIntent.getActivity(jobschedularservice.this,100,transferintent,PendingIntent.FLAG_ONE_SHOT);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel1);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(jobschedularservice.this);
            Notification notification = new NotificationCompat.Builder(jobschedularservice.this,channel)
                    .setSmallIcon(R.drawable.notification_icon_rent_detail_youmurdered)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSound(uri)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntents)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            if(Notification_id>100000){
                Notification_id=1000;
            }
            notificationManager.notify(Notification_id,notification);
        }else{
            Intent transferintent = new Intent(jobschedularservice.this,login.class);
            transferintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntents = PendingIntent.getActivity(jobschedularservice.this,100,transferintent,PendingIntent.FLAG_ONE_SHOT);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder noficationed = new NotificationCompat.Builder(jobschedularservice.this);
            noficationed.setSmallIcon(R.drawable.notification_icon_rent_detail_youmurdered)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(uri)
                    .setContentIntent(pendingIntents);

            NotificationManager managers = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            if(Notification_id>100000){
                Notification_id=1000;
            }

            assert managers != null;
            managers.notify(Notification_id,noficationed.build());
        }

        sharedPreferences = getSharedPreferences("hello world",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notifids",Notification_id+1);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: jobschedular service ");

        Intent intent = new Intent(this,Restarter.class);
        PendingIntent pendingIntent  = PendingIntent.getBroadcast(this,5,intent,0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        long triggerafter =5000;
        long triggerevery = 60000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,triggerafter,triggerevery,pendingIntent);

        //<action android:name="restartservice"/>
    }
}
