package com.example.rent_details;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class bugreport extends AppCompatActivity {
    CheckBox checkBox;
    Button button_image,sendbug;
    EditText email_ed,bug_report;
    TextView username,messageses,ticketnumber,regradmsg;
    Bitmap bitmap;
    String TAG="uploadbugdreport",encodedimage,urls="https://rentdetails.000webhostapp.com/bugreport.php",usernames;
    LinearLayout linearLayout_bug,linearLayout_response;
    String finalmsg="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugreport);

        checkBox = findViewById(R.id.checkboxbug);
        username = findViewById(R.id.username_bug);
        email_ed = findViewById(R.id.email_bug);
        bug_report = findViewById(R.id.error);
        button_image = findViewById(R.id.btn_bug_img);
        sendbug = findViewById(R.id.sendbug);
        linearLayout_bug = findViewById(R.id.layout_bug);
        linearLayout_response = findViewById(R.id.layout_response);
        messageses=findViewById(R.id.messagetouser);
        ticketnumber = findViewById(R.id.ticketgenerated);
        regradmsg = findViewById(R.id.regardmessage);

        linearLayout_bug.setVisibility(View.VISIBLE);
        linearLayout_response.setVisibility(View.GONE);

        getSupportActionBar().setTitle("Report Bug");

        Intent intent = getIntent();
        usernames = intent.getExtras().getString("username");
        username.setText(usernames);

        sendbug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportbugd();
            }
        });

    }
    public void selectimage(View view) {
        selectimagefromgalary();
    }

    private void selectimagefromgalary() {

        Dexter.withActivity(bugreport.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        Intent intents = new Intent(Intent.ACTION_PICK);
                        intents.setType("image/*");
                        startActivityForResult(Intent.createChooser(intents,"Select Image"),110);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==110 && resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null){
            Uri filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                new compressimage().execute(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void insert_bug_error_img(View view) {

        LinearLayout linearLayout = findViewById(R.id.layoutbugimage);

        if(checkBox.isChecked()){
            linearLayout.setVisibility(View.VISIBLE);
        }else{
            linearLayout.setVisibility(View.GONE);
        }

    }

    public void reportbugd() {
        String email,bugrep;
        email = email_ed.getText().toString().trim();
        bugrep = bug_report.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(this, "Email id required! ", Toast.LENGTH_LONG).show();
        }else
            if(bugrep.isEmpty()){
                Toast.makeText(this, "Please write few words on your issue...", Toast.LENGTH_LONG).show();
            }else
                if(checkBox.isChecked()){
                    if(encodedimage==null){
                        Toast.makeText(this, "Please select an image or uncheck the checkbox! ", Toast.LENGTH_LONG).show();
                    }else{
                        new uploadimagetoserver().execute(urls,email,bugrep);
                    }
                }else {
                    new uploadimagetoserver().execute(urls,email,bugrep);
                }
    }



    public class compressimage extends AsyncTask<Bitmap,Void,Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(bugreport.this);
            progressDialog.setMessage("Processing Image...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {

            Bitmap bitmap = bitmaps[0];

            int quality=100,lengths;
            String encodedfiles;

            do {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                byte[] imagebytes = byteArrayOutputStream.toByteArray();
                lengths=imagebytes.length;
                Log.d(TAG, "storeimage: size for quality "+quality+" is " + lengths);
                quality-=5;
                encodedfiles=android.util.Base64.encodeToString(imagebytes, Base64.DEFAULT);
            }while (quality>20 && lengths>1000000);

            encodedimage = encodedfiles;
            Log.d("encoded",encodedimage);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            Toast.makeText(bugreport.this, "Image selected Successfully", Toast.LENGTH_SHORT).show();


        }
    }
    public class uploadimagetoserver extends AsyncTask<String,Void,Void>{

        final ProgressDialog progressDialog = new ProgressDialog(bugreport.this);
        String message="";
        Handler handler = new Handler();
        int num=0;

        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Reporting issue, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            //storeimage(bitmap);
        }

        @Override
        protected Void doInBackground(String... strings) {

            String url = strings[0];
            final String email,bugrepo;
            email=strings[1];
            bugrepo =strings[2];

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    message=response;

                        Toast.makeText(bugreport.this,response, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: "+response);
                        finalmsg=response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    NetworkResponse networkResponse = error.networkResponse;

                    if(error.getMessage()!=null) {
                        message = error.getMessage();
                        Toast.makeText(bugreport.this, "Error code: "+networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: networktime "+networkResponse.networkTimeMs);
                        Log.d(TAG, "onErrorResponse: statuscode "+networkResponse.statusCode);
                        Log.d(TAG, "onErrorResponse: datalength "+networkResponse.data.length);
                        Log.d(TAG, "onErrorResponse: allheader "+networkResponse.allHeaders.size());
                        Log.d(TAG, "onErrorResponse: header "+networkResponse.headers);
                        Log.d(TAG, "onErrorResponse: notmodified "+networkResponse.notModified);
                    }else{
                        message = "Failed to upload Image!";
                        messageses.setText("Fail to send with error code: "+networkResponse.statusCode);
                        Toast.makeText(bugreport.this, "Error code: "+networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: networktime if null "+networkResponse.networkTimeMs);
                        Log.d(TAG, "onErrorResponse: statuscode if null "+networkResponse.statusCode);
                        Log.d(TAG, "onErrorResponse: datalength if null "+networkResponse.data.length);
                        Log.d(TAG, "onErrorResponse: allheader if null "+networkResponse.allHeaders.size());
                        Log.d(TAG, "onErrorResponse: header if null "+networkResponse.headers);
                        Log.d(TAG, "onErrorResponse: notmodified if null "+networkResponse.notModified);
                    }
                    Log.d(TAG, "onErrorResponse: error"+message);
                    new erroinfetch().execute("status code: "+error.networkResponse.statusCode);
                    new erroinfetch().execute("error message"+error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String, String>();
                    params.put("username",usernames);
                    params.put("email",email);
                    params.put("bugreport",bugrepo);
                    if(encodedimage!=null) {
                        if(!encodedimage.equals(""))
                            params.put("image", encodedimage);
                            Log.d(TAG, "getParams: encodeimage "+encodedimage);
                    }

                    return params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(5*60*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(bugreport.this);
            queue.add(request);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(finalmsg.equals("Failed to process your request, Try again later")){
                        messageses.setVisibility(View.GONE);
                        ticketnumber.setText(finalmsg);
                        regradmsg.setVisibility(View.GONE);
                    }else
                    if(!finalmsg.equals(""))
                    {
                        progressDialog.dismiss();
                        email_ed.setText("");
                        bug_report.setText("");
                        if(checkBox.isChecked()){
                            encodedimage="";
                            checkBox.setChecked(false);
                        }
                        linearLayout_bug.setVisibility(View.GONE);
                        linearLayout_response.setVisibility(View.VISIBLE);
                        messageses.setText("We are sorry to hear that you are facing some issues. Our agent will soon contact your for"+
                                " giving better solution."+"\nWe have opened a ticket regarding your issue. In case" +
                                " you don't find in mail check your spam in your mail");
                        ticketnumber.setText(finalmsg);
                        regradmsg.setText("Thank you for your patience \nTeam Rent Details");
                    }else{
                        handler.postDelayed(this,1000);
                        Log.d(TAG, "run: "+num++);
                        if(num>300){
                            progressDialog.dismiss();
                            uploadimagetoserver uploadimagetoserver = new uploadimagetoserver();
                            uploadimagetoserver.cancel(true);
                            if(uploadimagetoserver.isCancelled()){
                                Toast.makeText(bugreport.this, "cancelled task ", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(bugreport.this, "Task cancel failed ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            };
            runnable.run();
        }
    }

    public class erroinfetch extends AsyncTask<String,Void,Void>{

        public static final  String shared_pref="shared_prefs";
        public static  final String user="username";
        public String loginusername;
        private String errorurl="https://rentdetails.000webhostapp.com/error_in_app.php";
        private String TAG="errorfinding";
        private String classname="splash: ";

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
            RequestQueue queue = Volley.newRequestQueue(bugreport.this);
            queue.add(request);

            return null;
        }
    }
}
