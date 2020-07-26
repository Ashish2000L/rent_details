package com.example.rent_details;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class Restarter extends BroadcastReceiver {

    public static final String TAG="servicefilenames";
    Context context1;
    jobschedularservice jobschedularservice = new jobschedularservice();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "service tried to stop");
        Log.d(TAG, "service restarted");
        context1=context;

        if(!ismyservicerunning(forgroundservice.class)) {
            //startService(new Intent(login.this,forgroundservice.class));
            if(Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")){
                Log.d(TAG, "onReceive: booting is set in restart.java");
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, forgroundservice.class));
                }else{
                    context.startService(new Intent(context,forgroundservice.class));
                }
            }
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, forgroundservice.class));
            }else{
                context.startService(new Intent(context,forgroundservice.class));
            }

//            ComponentName componentName = new ComponentName(context, com.example.rent_details.jobschedularservice.class);
//            JobInfo info = new JobInfo.Builder(123,componentName)
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                    .setPersisted(true)
//                    .setPeriodic(60*1000)
//                    .build();
//            JobScheduler scheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//            assert scheduler != null;
//            int resultcode=scheduler.schedule(info);
//            if(resultcode==JobScheduler.RESULT_SUCCESS){
//                Log.d(TAG, "job scheduling successful");
//            }else{
//                Log.d(TAG, "jobscheduler failed");
//            }
        }
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            context.startForegroundService(new Intent(context,forgroundservice.class));
//            //context.startForegroundService(new Intent(context,volleynotificationservice.class));
//        }else{
//            context.startService(new Intent(context,forgroundservice.class));
//            //context.startService(new Intent(context,volleynotificationservice.class));
//        }
    }
    private boolean ismyservicerunning(Class<?> serviceclass){

        ActivityManager manager = (ActivityManager)context1.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceclass.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "running " + serviceclass.getName());
                return true;
            }
        }
        Log.d(TAG, "not running "+serviceclass.getName());
        return false;
    }
}
