package com.example.rent_details;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.FutureTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class forgroundservice extends Service {

    public static final  String  CHANNEL_ID="channel";
    public static final  String  CHANNEL_IDs="channel1";
    public static final String  shared_pref = "example";
    public static final String  TAG = "forgroundserviceexample";
    Intent intent1;
    private Bitmap bitmap;
    public int number=0;
    public  int Notification_id=101;
    int num;
    int y=0,id=1000;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref, MODE_PRIVATE);
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
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,"notification", NotificationManager.IMPORTANCE_LOW);
            serviceChannel.setDescription("this channel is to show user notifications");
            serviceChannel.setName("Notification channel");
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);

//            NotificationChannel serviceChannel1 = new NotificationChannel(CHANNEL_IDs,"Example Channel1", NotificationManager.IMPORTANCE_LOW);
//            serviceChannel1.enableVibration(true);
//            serviceChannel1.shouldVibrate();
//            serviceChannel1.setVibrationPattern(new long[]{4000,4000,4000});
//            serviceChannel1.setShowBadge(true);
//            serviceChannel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//
//            NotificationManager manager1 = getSystemService(NotificationManager.class);
//            assert manager1 != null;
//            manager1.createNotificationChannel(serviceChannel1);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Rent Details running")
                    .setContentText("Rent Details is running in background")
                    .setSmallIcon(R.drawable.notification_icon_rent_detail_youmurdered)
                    .build();

            startForeground(100, notification);
        }else{
            startForeground(1,new Notification());
        }




        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url ="https://rentdetails.000webhostapp.com/fetchnotif.php";
                //String imgurl= "https://rentdetails.000webhostapp.com/images/customprofile.jpg";
                fetechdata fetechdata = new fetechdata();
                fetechdata.execute(url);

               // new loadimagetobitmap().execute(imgurl);
                Log.d(TAG, "run: running for "+num+" times");
                Log.d(TAG, "running loops for "+number);
                Boolean running =ismyservicerunning(forgroundservice.class);
                number++;

                handler.postDelayed(this,60000);
            }
        };
        runnable.run();

        return START_STICKY;
    }

    private boolean ismyservicerunning(Class<?> serviceclass){

        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceclass.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "running " + serviceclass.getName());
                return true;
            }
        }
        Log.d(TAG, "not running "+serviceclass.getName());
        return false;
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

//    private class loadimagetobitmap extends AsyncTask<String,Void,Void>{
//
//        public Bitmap bitmap;
//        @Override
//        protected Void doInBackground(String... strings) {
//
//            String url = strings[0];
//            Log.d(TAG, "doInBackground: imgurl "+url);
//            FutureTarget bitmapFutureTarget = Glide.with(forgroundservice.this)
//                    .asBitmap()
//                    .load(url)
//                    .submit();
//
//            try {
//                bitmap = (Bitmap) bitmapFutureTarget.get();
//                Log.d(TAG, "doInBackground: bitmap "+bitmap);
//
//                final String title = "Started "+num+" times";
//                final String body = "Running for "+y+" minutes";
//                final String bigtext = "this is a bog text to show the demoto how to we show the big text in the notification section with the not letter loss "+y;
//                Notification notification1 = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    notification1 = new NotificationCompat.Builder(forgroundservice.this,CHANNEL_IDs)
//                            .setContentTitle(title)
//                            .setContentText(bigtext)
//                            .setDefaults(Notification.DEFAULT_VIBRATE)
//                            .setDefaults(Notification.BADGE_ICON_LARGE)
//                            .setSmallIcon(R.drawable.notification_icon_rent_detail)
//                            .setStyle(new NotificationCompat.BigTextStyle().bigText(bigtext))
//                            //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null))
//                            .setLargeIcon(bitmap)
//                            .build();
//                }
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    NotificationManager manager1 = getSystemService(NotificationManager.class);
//                    assert manager1 != null;
//                    if(id>100000){
//                        id=101;
//                    }
//                    manager1.notify(id,notification1);
//                }
//                Log.d(TAG, "run: y "+y);
//                y++;
//                id++;
//
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: forground service");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved: called it");
    }

    private class fetechdata extends AsyncTask <String,Void,String>{

        private String url,username,token,title,body,tok,tokenname="token",systemmessage= "";
        public static final  String shared_pref="shared_prefs";
        public static  final String user="username";
        public boolean isfetchingdone=false;

        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tok=sharedPreferences.getString(tokenname,"0");
        }

        @Override
        protected String doInBackground(String... strings)
        {
           url = strings[0];
            username = sharedPreferences.getString(user,"");
            Log.d(TAG, "doInBackground: looking for the username "+username);

            Log.d(TAG, "doInBackground: url "+url);


           if(!username.equals("") && checkConnection())
           {

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
                                   Log.d(TAG, "onResponse: token "+token);
                                   Log.d(TAG, "onResponse: title "+title);
                                   Log.d(TAG, "onResponse: body "+body);
                                   systemmessage="fetch successful";

                                   searchforlatestnotif(token, title, body);
                               }
                           }
                       } catch (JSONException ex) {
                           ex.printStackTrace();
                           systemmessage=ex.getMessage();
                           new erroinfetch().execute("json error: "+ex.getMessage());
                       }
                   }

               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                        systemmessage=error.getMessage();
                        if(error.networkResponse!=null){
                            new erroinfetch().execute("status code "+error.networkResponse.statusCode);
                        }
                   }
               }) {

                   @Override
                   protected Map<String, String> getParams() throws AuthFailureError {
                       Map<String,String> params = new HashMap<String,String>();
                       params.put("username", username);
                       return params;
                   }
               };

               RequestQueue queue = Volley.newRequestQueue(forgroundservice.this);
               queue.add(request);
           }else{
               Log.d(TAG, "service stoped as user not found");
               systemmessage = "fetch failed";
           }


            Log.d(TAG, "doInBackground: systemmessage"+systemmessage);
            return "complete the process";
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: "+s);
        }

        private void sharedprefrenced(String tokenz){
            SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(tokenname,tokenz);
            editor.apply();
        }

        private void searchforlatestnotif(String token, String title, String body){
            sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
            tok =sharedPreferences.getString(tokenname,"0");

            Log.d(TAG, "searchforlatestnotif: tok "+tok);

            if(!tok.equals(token) && token != null &&  !token.equals("")){
                sharedprefrenced(token);
                shownotification(body,title);
            }else{
                isfetchingdone =true;
            }
        }

        private void shownotification(String body,String title){

            Notification_id = sharedPreferences.getInt("notifid",101);

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
               String channel =sharedPreferences.getString(user,"");

               NotificationChannel channel1 = new NotificationChannel(channel,"show latest update notif",NotificationManager.IMPORTANCE_HIGH);
               channel1.setDescription("This is to fetch latest notification");
               channel1.setImportance(NotificationManager.IMPORTANCE_HIGH);
               channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
               channel1.setShowBadge(true);
               channel1.enableVibration(true);
               channel1.shouldVibrate();
               channel1.enableLights(true);
               channel1.setLightColor(Color.GREEN);

                Intent transferintent = new Intent(forgroundservice.this,login.class);
                transferintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntents = PendingIntent.getActivity(forgroundservice.this,100,transferintent,PendingIntent.FLAG_ONE_SHOT);

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationManager manager = getSystemService(NotificationManager.class);
                assert manager != null;
                manager.createNotificationChannel(channel1);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(forgroundservice.this);
                Notification notification = new NotificationCompat.Builder(forgroundservice.this,channel)
                        .setSmallIcon(R.drawable.notification_icon_1)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSound(uri)
                        .setVibrate(new long[]{1000,1000,1000,1000,1000})
                        //.setDefaults(Notification.DEFAULT_VIBRATE)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setContentIntent(pendingIntents)
                        .setAutoCancel(true)
                        .setLights(Color.BLACK,1000,5000)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .build();
                if(Notification_id>100000){
                    Notification_id=101;
                }
                notificationManager.notify(Notification_id,notification);
            }else{
                Intent transferintent = new Intent(forgroundservice.this,login.class);
                transferintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntents = PendingIntent.getActivity(forgroundservice.this,100,transferintent,PendingIntent.FLAG_ONE_SHOT);

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder noficationed = new NotificationCompat.Builder(forgroundservice.this);
                noficationed.setSmallIcon(R.drawable.notification_icon_1)
                        .setContentText(body)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        //.setVibrate(new long[]{1000,1000,1000,1000,1000})
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(Notification.VISIBILITY_PUBLIC)
                        .setLights(Color.BLUE,1000,5000)
                        .setSound(uri)
                        .setContentIntent(pendingIntents);

                NotificationManager managers = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                if(Notification_id>100000){
                    Notification_id=101;
                }

                assert managers != null;
                managers.notify(Notification_id,noficationed.build());
            }

            sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("notifid",Notification_id+1);
            editor.apply();
        }
    }

    public class erroinfetch extends AsyncTask<String,Void,Void>{

        public static final  String shared_pref="shared_prefs";
        public static  final String user="username";
        public String loginusername;
        private String errorurl="https://rentdetails.000webhostapp.com/error_in_app.php";
        private String TAG="errorfinding";
        private String classname="forgroundservice: ";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
            loginusername=sharedPreferences.getString(user,"");
        }

        @Override
        protected Void doInBackground(String... strings) {

            final String errors=strings[0];

            StringRequest request = new StringRequest(Request.Method.POST, errorurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: ");
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("username",loginusername);
                    params.put("error",classname+errors);
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(forgroundservice.this);
            queue.add(request);

            return null;
        }
    }

}
