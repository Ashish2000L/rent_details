package com.example.rent_details;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class volleynotificationservice extends Service {

    private Handler handler = new Handler();
    public static final String TAG = "servicefilenames";
    private static int nums=1;public static final  String shared_pref="shared_prefs";
    public static  final String user="username",tokenname="token";
    SharedPreferences sharedPreferences;
    String usernames;
    private String title,body,token,tok;
    Boolean isfetchingdone=false,islowerandroidlevel=false;
    public static int Notification_id;
    public volleynotificationservice() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        usernames = sharedPreferences.getString(user,"");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startmyownforgroundservice();
            islowerandroidlevel=false;
        }else{
            startForeground(1,new Notification());
            islowerandroidlevel=true;
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startmyownforgroundservice() {

        String NOTIFICATION_CHANNEL_ID=usernames;
        String channelname = "background service";
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,channelname,
                NotificationManager.IMPORTANCE_LOW);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationbuilder.setOngoing(true)
                .setContentTitle("App is running")
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2,notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!usernames.equals("")) {
            Log.d(TAG, usernames);
            fetchrunnable.run();
        }else{
            Log.d(TAG, "username not found");
        }
        return START_STICKY;
    }

    public void sharedprefrenced(String tokenz){
        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tokenname,tokenz);
        editor.apply();
    }

    private void searchforlatestnotif(String tokens,String titles,String bodys) {
        sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        tok =sharedPreferences.getString(tokenname,"0");

        Log.d(TAG, "tok value is "+tok);
        Log.d(TAG, "tokens value is "+tokens);
        if(!tok.equals(tokens)){
            sharedprefrenced(tokens);
            shownotification(bodys,titles);
        }else{
            isfetchingdone =true;
        }


    }

    private  void shownotification(String bodye, String titlee)
    {
        Notification_id = sharedPreferences.getInt("notifid",5);

        if(islowerandroidlevel){

            Intent transferintent = new Intent(volleynotificationservice.this,login.class);
            transferintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntents = PendingIntent.getActivity(volleynotificationservice.this,100,transferintent,PendingIntent.FLAG_ONE_SHOT);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder noficationed = new NotificationCompat.Builder(volleynotificationservice.this);
            noficationed.setSmallIcon(R.mipmap.profile1_foreground)
                    .setContentText(bodye)
                    .setContentTitle(titlee)
                    .setAutoCancel(true)
                    .setSound(uri)
                    .setContentIntent(pendingIntents);

            NotificationManager managers = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            if(Notification_id>100000){
                Notification_id=5;
            }

            assert managers != null;
            managers.notify(Notification_id,noficationed.build());
            Log.d(TAG, "send notification");

        }else
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channel_id = "channel1";
            NotificationChannel channel = new NotificationChannel(channel_id,"channel1",NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(bodye);

                Intent transferintent = new Intent(volleynotificationservice.this,login.class);
                transferintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntents = PendingIntent.getActivity(volleynotificationservice.this,100,transferintent,PendingIntent.FLAG_ONE_SHOT);

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                 NotificationManager manager = getSystemService(NotificationManager.class);
                assert manager != null;
                manager.createNotificationChannel(channel);


                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(volleynotificationservice.this);
                Notification notification = new NotificationCompat.Builder(this,channel_id)
                        .setSmallIcon(R.mipmap.profile1_foreground)
                        .setContentTitle(titlee)
                        .setContentText(bodye)
                        .setSound(uri)
                        .setContentIntent(pendingIntents)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .build();
                if(Notification_id>100000){
                    Notification_id=5;
                }
                notificationManager.notify(Notification_id,notification);
                Log.d(TAG, "send notification through channel");
        }


        sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notifid",Notification_id+1);
        editor.apply();

    }
    private Runnable fetchrunnable = new Runnable() {
        @Override
        public void run() {

            String url ="https://rentdetails.000webhostapp.com/fetchnotif.php";
            if(checkConnection()) {
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;
                        String success;
                        try {
                            jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    token = object.getString("token");
                                    title = object.getString("title");
                                    body = object.getString("body");

                                    Log.d(TAG, "into the request");
                                }
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        searchforlatestnotif(token, title, body);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", usernames);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(volleynotificationservice.this);
                requestQueue.add(request);

            }else{
                Log.d(TAG, "No internet, skipping string...");
            }

                handler.postDelayed(this, 60000);
                Log.d(TAG, "handling postdelay");

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy ");

        Intent brodcastintent = new Intent();
        brodcastintent.setAction("restartservice");
        brodcastintent.setClass(this,Restarter.class);
        this.sendBroadcast(brodcastintent);

    }

    public boolean checkConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        assert wifi != null;
        if(wifi.isConnected()){
            return true;

        }
        else {
            assert mobileNetwork != null;
            if (mobileNetwork.isConnected()){
                return true;
            }

        }
        return false;
    }
}
