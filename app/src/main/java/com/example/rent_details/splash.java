package com.example.rent_details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.Thread.sleep;

public class splash extends AppCompatActivity {
    Thread time;
    int version = BuildConfig.VERSION_CODE;
    FirebaseRemoteConfig firebaseRemoteConfig;
    WebView webView;
    private static final String VersionCode = "versioncodes";
    //private static final String Message = "message";
    private static final String force_update = "force_update";
    //private static final String updatedetails = "updatedetails";
    private static final String Url = "url";
    private Uri path;
    private Context context;
    private DownloadManager downloadManager;
    long download;
    TextView versions,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        versions = findViewById(R.id.version);
        status = findViewById(R.id.status);
        versions.setText(BuildConfig.VERSION_NAME);

        if(checkConnection()) {
            getdetails();
        }
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
        firebaseRemoteConfig.fetch(catchExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                if (task.isSuccessful()) {
                    check_for_update();
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
    private void check_for_update() {
        String versioncode = firebaseRemoteConfig.getString("version");
        Toast.makeText(this, "versioncode is:"+versioncode, Toast.LENGTH_LONG).show();
        boolean value = true;
        if (value == false){
            int ver = Integer.parseInt(versioncode);
            final Intent intent = new Intent(splash.this, login.class);
        if (ver == version) {
            time = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sleep(2000);
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            status.setText("Update Available!!");
            if (!firebaseRemoteConfig.getBoolean(force_update)) {
                displaywelcomemessagenotforce();
            } else if (firebaseRemoteConfig.getBoolean(force_update)) {
                updatebyforce();
            }

        }
    }

    }

    private void updatebyforce()
    {

        final String new_Url = firebaseRemoteConfig.getString(Url).trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Available")
                .setMessage("A newer version available ...")
                .setPositiveButton("Update now", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        status.setText("Checking for resources...");
                        webView.setVisibility(View.VISIBLE);
                        webView.loadUrl(new_Url);
                        //text.setText(new_Url);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setDownloadListener(new DownloadListener() {
                            @Override
                            public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {

                                File file_new = new File("/storage/emulated/0/Download/", URLUtil.guessFileName(url, contentDisposition, mimetype));

                                if(file_new.exists())
                                {
                                    if(!file_new.isDirectory())
                                    {
                                        if(file_new.delete())
                                        {
                                            //text.setText("File deleted");
                                            Toast.makeText(splash.this, "File Deleted", Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        //text.setText("File is a directory");
                                        Toast.makeText(splash.this, "File is a directory", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    //text.setText("file don't exist");
                                    Toast.makeText(splash.this, "File doesn't exist", Toast.LENGTH_LONG).show();
                                }

                                status.setText("Checking for permissions...");
                                Dexter.withActivity(splash.this)
                                        .withPermission(WRITE_EXTERNAL_STORAGE)
                                        .withListener(new PermissionListener() {
                                            @Override
                                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                                DownloadFile(url,userAgent,contentDisposition,mimetype,contentLength);
                                            }

                                            @Override
                                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                            }

                                            @Override
                                            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                                                token.continuePermissionRequest();
                                            }
                                        }).check();
                                webView.setVisibility(View.GONE);
                            }
                        });

                    }
                }).show();


    }


    //dialog for latest update
    private void displaywelcomemessagenotforce()
    {
        final String new_Url = firebaseRemoteConfig.getString(Url).trim();
        final Intent intent = new Intent(splash.this, login.class);


        //giving dialog for an available update
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(new_Url);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setDownloadListener(new DownloadListener() {
                    @Override
                    public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {

                        status.setText("Checking for resources...");
                        File file_new = new File("/storage/emulated/0/Download/", URLUtil.guessFileName(url, contentDisposition, mimetype));

                        if(file_new.exists())
                        {
                            if(!file_new.isDirectory())
                            {
                                if(file_new.delete())
                                {
                                    //text.setText("File deleted");
                                    Toast.makeText(splash.this, "File Deleted", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                //text.setText("File is a directory");
                                Toast.makeText(splash.this, "File is a directory", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            //text.setText("file don't exist");
                            Toast.makeText(splash.this, "File doesn't exist", Toast.LENGTH_LONG).show();
                        }
                        status.setText("Checking for permissions...");

                        Dexter.withActivity(splash.this)
                                .withPermission(WRITE_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        DownloadFile(url,userAgent,contentDisposition,mimetype,contentLength);
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();

                        webView.setVisibility(View.GONE);
                    }
                });

            }
        }).show();
    }

    //for starting downloading process
    public void DownloadFile(String url, String userAgent, final String contentDisposition, final String mimetype, final long contentLength )
    {

        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        destination += fileName;
        final Uri new_uri = Uri.parse("file://" + destination);
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

        Toast.makeText(splash.this, "Downloading....", Toast.LENGTH_LONG).show();

        status.setText("Downloading update ...");

        File file_dir = new File("/storage/emulated/0/Download/", URLUtil.guessFileName(url, contentDisposition, mimetype));
        path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file_dir);

        BroadcastReceiver new_brpdcast = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                final Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfOpenintent.setDataAndType(path, downloadManager.getMimeTypeForDownloadedFile(download));
                context.startActivity(pdfOpenintent);
                context.unregisterReceiver(this);
            }
        };
        registerReceiver(new_brpdcast, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public boolean checkConnection(){
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


