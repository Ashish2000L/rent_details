package com.example.rent_details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.Thread.sleep;

public class splash extends AppCompatActivity {
    Thread time;
    int version = BuildConfig.VERSION_CODE;
    FirebaseRemoteConfig firebaseRemoteConfig;
    WebView webView;
    private static final String VersionCode = "versioncodes";
    private static final String force_update = "force_update";
    private static final String Url = "url";
    private Uri path;
    private Context context;
    private DownloadManager downloadManager;
    long download,contentlength;
    TextView versions,status;
    private String TAG = "Downloadingfiles",SHARED_PREFS="isfirsttime",ISRUNNINGFILRSTTIME="isrunninfirsttime";
    Dialog popupdialog;
    String manufacturer,respomessage;
    String urls="https://rentdetails.000webhostapp.com/checkforautostartpermission.php";
    SharedPreferences sharedPreferences;
    TextView popupmessage;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true).build(); //.setDeveloperModeEnabled(BuildConfig.DEBUG)
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        popupdialog = new Dialog(splash.this);

        sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        versions = findViewById(R.id.version);
        status = findViewById(R.id.status);
        versions.setText(BuildConfig.VERSION_NAME);
        webView = findViewById(R.id.webview);
        popupmessage = findViewById(R.id.popupmessage);

        Log.d(TAG, "onCreate: board "+Build.BOARD);
        Log.d(TAG, "onCreate: bootloader "+Build.BOOTLOADER);
        Log.d(TAG, "onCreate: brand "+Build.BRAND);
        Log.d(TAG, "onCreate: device "+Build.DEVICE);
        Log.d(TAG, "onCreate: display "+Build.DISPLAY);
        Log.d(TAG, "onCreate: fingerprint "+Build.FINGERPRINT);
        Log.d(TAG, "onCreate: hardware "+Build.HARDWARE);
        Log.d(TAG, "onCreate: host "+Build.HOST);
        Log.d(TAG, "onCreate: id "+Build.ID);
        Log.d(TAG, "onCreate: model "+Build.MODEL);
        Log.d(TAG, "onCreate: product "+Build.PRODUCT);
        Log.d(TAG, "onCreate: tags "+Build.TAGS);
        Log.d(TAG, "onCreate: type "+Build.TYPE);
        Log.d(TAG, "onCreate: user "+Build.USER);
        Log.d(TAG, "onCreate: time "+Build.TIME);
        Log.d(TAG, "onCreate: serial "+Build.SERIAL);
        Log.d(TAG, "onCreate: radio version"+Build.getRadioVersion());


        manufacturer = Build.MANUFACTURER;

        if (checkConnection()) {
        if(sharedPreferences.getBoolean(ISRUNNINGFILRSTTIME,true)) {
            new autostartpermissions().execute(urls,manufacturer);
            //popup();
        }else {

            status.setText("Checking for Internet connection...");

            Toast.makeText(this, "You are using " + manufacturer + " mobile", Toast.LENGTH_LONG).show();

                getdetails();
            }
        }else {
            status.setText("Internet connection Required!!");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(splash.this, login.class);
                    try {
                        Thread.sleep(3000);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void popup(){
        popupdialog.setContentView(R.layout.popupmessage);
        popupdialog.setCancelable(false);
        popupdialog.show();


    }

    private void getdetails()
    {   status.setText("Checking for updates...");
        boolean is_using_developerMode = firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled();
        int catchExpiration;
        if (is_using_developerMode) {

            catchExpiration = 0;

        } else {
            catchExpiration = 0;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                firebaseRemoteConfig.fetch(0).addOnCompleteListener(splash.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                                @Override
                                public void onComplete(@NonNull Task<Boolean> tasks) {
                                    if(tasks.isSuccessful()){
                                        check_for_update();
                                    }
                                }
                            });
                            //check_for_update();
                            //status.setText(firebaseRemoteConfig.getString(Url));
                            //Toast.makeText(splash.this, "fetch successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(splash.this, "Fetch Failed",
                                    Toast.LENGTH_LONG).show();
                            final Intent transfer= new Intent(splash.this,login.class); //change this after testing
                            startActivity(transfer);
                            finish();
                        }
                    }
                });

            }
        },0);

    }
    private void check_for_update()
    {
        String versioncode = firebaseRemoteConfig.getString(VersionCode);
            int ver = Integer.parseInt(versioncode);
            final Intent intent = new Intent(splash.this, login.class);
        if (ver == version) {
            time = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sleep(3000);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            time.start();

        } else {
            status.setText("Update Available!!");
            if (!firebaseRemoteConfig.getBoolean(force_update)) {
                displaywelcomemessagenotforce();
            } else if (firebaseRemoteConfig.getBoolean(force_update)) {
                updatebyforce();
            }

        }

    }

    private void updatebyforce()
    {
        Log.d(TAG, "updatebyforce: ");

        final String new_Url = firebaseRemoteConfig.getString(Url).trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);
        builder.setTitle("Update Available")
                .setMessage("A newer version available ...")
                .setPositiveButton("Update now", new DialogInterface.OnClickListener() {

                    @SuppressLint("SetJavaScriptEnabled")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        status.setText("Checking for resources...");
                        webView.setVisibility(View.VISIBLE);
                        status.setText(new_Url);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.loadUrl(new_Url);
                        status.setText("Checking for permissions...");
                        webView.setWebViewClient(new WebViewClient());
                        checkforpermission();

                    }
                })
                .setCancelable(false)
                 .create().show();


    }

    private void displaywelcomemessagenotforce()
    {
        final String new_Url =firebaseRemoteConfig.getString(Url).trim();
        final Intent intent = new Intent(splash.this, login.class);


        //giving dialog for an available update
        AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);
        builder.setTitle("Update Available");
        builder.setMessage("A newer vesion available ...");
        builder.setNegativeButton("Maybe later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.setPositiveButton("Update now", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                status.setText("Checking for resources...");
                webView.setVisibility(View.VISIBLE);
                status.setText(new_Url);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(new_Url);
                status.setText("Checking for permissions...");
                webView.setWebViewClient(new WebViewClient());
                checkforpermission();

            }
        })
                .setCancelable(false)
                .create().show();
    }
    
    public void checkforpermission()
    {
        if(ContextCompat.checkSelfPermission(splash.this, WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            status.setText("Waiting for server to Respond...");
            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Log.d(TAG, "url "+url);
                    Log.d(TAG, "useragent "+userAgent);
                    contentlength=contentLength;
                    downloadupdatedapk downloadupdatedapk = new downloadupdatedapk();
                    downloadupdatedapk.execute(url,userAgent,contentDisposition,mimetype);
                }
            });
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(splash.this, WRITE_EXTERNAL_STORAGE)){

                new AlertDialog.Builder(splash.this)
                        .setTitle("Permission needed")
                        .setMessage("Please provide to permission to start installing latest update :)")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(splash.this,new String[]{WRITE_EXTERNAL_STORAGE},10);
                            }
                        })
                        .setCancelable(false)
                        .create().show();

            }else{
                ActivityCompat.requestPermissions(splash.this,new String[]{WRITE_EXTERNAL_STORAGE},10);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode==10 ){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "onRequestPermissionsResult: ");
                status.setText("Waiting for server to Respond...");
                webView.setDownloadListener(new DownloadListener() {
                    @Override
                    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                        Log.d(TAG, "url "+url);
                        Log.d(TAG, "useragent "+userAgent);
                        contentlength=contentLength;
                        downloadupdatedapk downloadupdatedapk = new downloadupdatedapk();
                        downloadupdatedapk.execute(url,userAgent,contentDisposition,mimetype);
                    }
                });
            }else{
                status.setText("Permission denied!");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(splash.this,login.class);
                        try {
                            Thread.sleep(3000);
                            startActivity(intent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    //for starting downloading process
    public void DownloadFile(String url, String userAgent, final String contentDisposition, final String mimetype, final long contentLength )
    {
        Log.d(TAG, "DownloadFile: ");

        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        destination += fileName;
        final Uri new_uri = Uri.parse("file://" + destination);
        Log.d(TAG, "new uri "+new_uri);
        //text.setText("Downloading will start in a minute...");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setMimeType(mimetype);
        String cookie = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookie);
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription(fileName);
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(new_uri);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        assert downloadManager != null;
        download = downloadManager.enqueue(request);
        Log.d(TAG, "DownloadFile: download"+download);

        status.setText("Downloading update ...");

        File file_dir = new File("/storage/emulated/0/Download/", URLUtil.guessFileName(url, contentDisposition, mimetype));
        path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file_dir);

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

    public void closeonclick(View view) {
        popupdialog.dismiss();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ISRUNNINGFILRSTTIME,false);
        editor.apply();
        getdetails();
    }

    public void remindmelater(View view) {

        Toast.makeText(this, "remind me later", Toast.LENGTH_SHORT).show();
        popupdialog.dismiss();
        getdetails();
    }

    public void message(View view) {

        TextView popup =view.findViewById(R.id.popupmessage);
        popup.setText(respomessage);

    }


    public class downloadupdatedapk extends AsyncTask<String,Void,Void>{

            @Override
            protected void onPreExecute() {
                Toast.makeText(splash.this, "Downloading...", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Void doInBackground(String... strings) {
                String url = strings[0],useragent=strings[1],contentdepisition = strings[2],mimetype = strings[3];

                DownloadFile(url,useragent,contentdepisition,mimetype,contentlength);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d(TAG, "path "+path);
                BroadcastReceiver new_brpdcast = new BroadcastReceiver() {
                    @Override
                    public void onReceive(final Context context, Intent intent) {
                        Log.d(TAG, "onReceive: into the reciver");
                        final Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        pdfOpenintent.setDataAndType(path, downloadManager.getMimeTypeForDownloadedFile(download));
                        Log.d(TAG, "onReceive: "+downloadManager.getMimeTypeForDownloadedFile(download));
                        Log.d(TAG, "onReceive: uri"+downloadManager.getUriForDownloadedFile(download));
                        context.startActivity(pdfOpenintent);
                        context.unregisterReceiver(this);
                    }
                };
                registerReceiver(new_brpdcast, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        }

        public class autostartpermissions extends AsyncTask<String,Void,Void>{

            @Override
            protected Void doInBackground(String... strings) {

                String url = strings[0];
                final String manufacturarname=strings[1].toLowerCase();
                Log.d(TAG, "doInBackground: manufacturer "+manufacturarname);
                Log.d(TAG, "doInBackground: url "+url);

                final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "onResponse: requestvalue"+response);
                        respomessage=response;
                        if(!response.equals("")) {
                            popup();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.d(TAG, "onErrorResponse: networktime"+networkResponse.networkTimeMs);
                        Log.d(TAG, "onErrorResponse: statuscode"+networkResponse.statusCode);
                        Log.d(TAG, "onErrorResponse: datalength"+networkResponse.data.length);
                        Log.d(TAG, "onErrorResponse: allheadersize"+networkResponse.allHeaders.size());
                        Log.d(TAG, "onErrorResponse: header"+networkResponse.headers);

                    }
                }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String,String> params = new HashMap<String, String>();

                        params.put("manufacture",manufacturarname);

                        return params;
                    }
                };

                request.setRetryPolicy(new DefaultRetryPolicy(2 * 60 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue queue = Volley.newRequestQueue(splash.this);
                queue.add(request);

                return null;
            }
        }
}


