package com.example.rent_details;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class login extends AppCompatActivity {

    EditText ed_username,ed_password;
    String str_category,str_username,str_password;
    String url="https://rentdetails.000webhostapp.com/login.php";
    public static final  String shared_pref="shared_prefs";
    public static  final String user="username";
    public static final String pass="password";
    public static final String TAG="forgroundserviceexample";
    public static final String isrunningfirsttime= "isfirsttime",ispermissiongiven="ispermissiongiven";
    Button btnlogin;
    volleynotificationservice volleynotificationservices = new volleynotificationservice();
    forgroundservice forgroundservice = new forgroundservice();
    jobschedularservice jobschedularservice = new jobschedularservice();
    boolean status = false;

    //here category 1 means that the user is renter and 0 is that user is admin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_passwords);
        btnlogin= findViewById(R.id.btn_login);

        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        ed_username.setText(sharedPreferences.getString(user, ""));
        ed_password.setText(sharedPreferences.getString(pass, ""));

        if(!sharedPreferences.getBoolean(ispermissiongiven,false)){
            autostartpermissions();
        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection()) {
                    Login();
                }else{
                    Toast.makeText(login.this,"Internet Connection Required!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void autostartpermissions(){
        SharedPreferences sharedPreferences1 = getSharedPreferences(shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.putBoolean(ispermissiongiven,true);
        editor1.apply();
        autostarthandler.getInstance().getAutoStartPermission(this);
    }

    public void shared_prefs()
    {

        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(user,str_username);
        editor.putString(pass,str_password);
        editor.apply();
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

    public void Login() {

        final ProgressDialog progressDialog = new ProgressDialog( this);
        progressDialog.setMessage("Please wait...");

        if(ed_username.getText().toString().equals("")){
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(ed_password.getText().toString().equals("")){
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else{

            progressDialog.show();
            str_username=ed_username.getText().toString().trim();
            str_password=ed_password.getText().toString().trim();

            StringRequest request =new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    if(response.equalsIgnoreCase("admin")){

                        ed_username.setText("");
                        ed_password.setText("");
                        shared_prefs();
                        if(!ismyservicerunning(jobschedularservice.getClass())){
                            //startService(new Intent(login.this,forgroundservice.class));
                            ComponentName componentName = new ComponentName(login.this,jobschedularservice.getClass());
                            JobInfo info = new JobInfo.Builder(123,componentName)
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                    .setPersisted(true)
                                    .setPeriodic(15*60*1000)
                                    .build();
                            JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
                            assert scheduler != null;
                            int resultcode=scheduler.schedule(info);
                            if(resultcode==JobScheduler.RESULT_SUCCESS){
                                Log.d(TAG, "job scheduling successful");
                            }else{
                                Log.d(TAG, "jobscheduler failed");
                            }
                        }
//                        if(!ismyservicerunning(volleynotificationservices.getClass())){
//                            startService(new Intent(login.this,volleynotificationservice.class));
//                        }
                        startActivity(new Intent(getApplicationContext(),ListOfRentersForAdmin.class)
                                .putExtra("username",str_username));
                        finish();

                    }else if(response.equalsIgnoreCase("Renter")){

                        ed_username.setText("");
                        ed_password.setText("");
                        shared_prefs();
                        if(!ismyservicerunning(jobschedularservice.getClass())){
                            //startService(new Intent(login.this,forgroundservice.class));
                            ComponentName componentName = new ComponentName(login.this,jobschedularservice.getClass());
                            JobInfo info = new JobInfo.Builder(123,componentName)
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                    .setPersisted(true)
                                    .setPeriodic(15*60*1000)
                                    .build();
                            JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
                            assert scheduler != null;
                            int resultcode=scheduler.schedule(info);
                            if(resultcode==JobScheduler.RESULT_SUCCESS){
                                Log.d(TAG, "job scheduling successful");
                            }else{
                                Log.d(TAG, "jobscheduler failed");
                            }
                        }

//                        if(!ismyservicerunning(volleynotificationservices.getClass())){
//                            startService(new Intent(login.this,volleynotificationservice.class));
//                        }
                        startActivity(new Intent(login.this,showdetails.class)
                        .putExtra("username",str_username)
                        .putExtra("category",1));
                        finish();
                    }
                    else{
                        if(!response.equals("") && !response.equals(null)) {
                            Toast.makeText(login.this, response, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(login.this, "Unable to Connect to server, Try again later!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(login.this, "error "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("username",str_username);
                                params.put("password",str_password);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(login.this);

            requestQueue.add(request);

        }
    }

    public void moveToRegistration(View view) {
        startActivity(new Intent(getApplicationContext(), register.class));
        finish();
    }

    //this is to the check if the service is running or not...
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
}
