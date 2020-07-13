package com.example.rent_details;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Restarter extends BroadcastReceiver {

    public static final String TAG="servicefilenames";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "service tried to stop");
        Log.d(TAG, "service restarted");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            context.startForegroundService(new Intent(context,volleynotificationservice.class));
        }else{
            context.startService(new Intent(context,volleynotificationservice.class));
        }
    }
}
