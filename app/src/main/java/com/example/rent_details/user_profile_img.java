package com.example.rent_details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class user_profile_img extends AppCompatActivity {

    ImageView imageView;
    String imageurl,name,username;
    int ids;
    boolean viewingadminprofile;
    ProgressBar progressBar;
    Bitmap bitmap;
    String encodedimage;
    int size;
    String TAG ="uploadingimage",urls="https://rentdetails.000webhostapp.com/uploadimage.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_img);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        imageView = findViewById(R.id.userprofileimage);
        progressBar = findViewById(R.id.progressbar);


        Intent intent = getIntent();
        ids = intent.getExtras().getInt("id");
        imageurl = intent.getExtras().getString("url","https://rentdetails.000webhostapp.com/images/customprofile.jpg");
        name = intent.getExtras().getString("name","Image");
        viewingadminprofile = intent.getExtras().getBoolean("isadmin",false);
        username = intent.getExtras().getString("username");
        getSupportActionBar().setTitle(name);

        loadimage();


    }
    public void loadimage()
    {
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(user_profile_img.this)
                .load(imageurl)
                .into(imageView);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(ids==0 && viewingadminprofile==false){

        }else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.userimagemenu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.edit:

                selectimagefromgalary();

                break;
            case R.id.upload:
                //new uploadimagetoserver().execute(urls);
                new compressimage().execute(bitmap);

                break;
        }

        return true;
    }

    private void uploadimage() {


        StringRequest request = new StringRequest(Request.Method.POST, "https://rentdetails.000webhostapp.com/uploadimage.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(user_profile_img.this, response, Toast.LENGTH_LONG).show();
                if(response.equalsIgnoreCase("Uploaded successfully")) {
                    startActivity(new Intent(user_profile_img.this, details_all.class)
                            .putExtra("username", username)
                            .putExtra("category", ids)
                            .putExtra("isadmin", viewingadminprofile));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(user_profile_img.this, "error "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<String, String>();
                params.put("image",encodedimage);
                params.put("username",username);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(user_profile_img.this);
        queue.add(request);

    }

    private void selectimagefromgalary() {

        Dexter.withActivity(user_profile_img.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        Intent intents = new Intent(Intent.ACTION_PICK);
                        intents.setType("image/*");
                        startActivityForResult(Intent.createChooser(intents,"Select Image"),100);

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

        if(requestCode==100 && resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null)
        {
            Uri filepath = data.getData();
            Log.d(TAG, "selected image uri "+filepath);
            CropImage.activity(filepath)
                    .setCropMenuCropButtonTitle("Crop Image")
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                Log.d(TAG, "uri of croped image is "+resultUri);
                File file = new File(resultUri.toString());
                if(file.exists()){
                    Toast.makeText(this, "length using file method "+file.length(), Toast.LENGTH_SHORT).show();
                }
                try {
                    InputStream inputStream = getContentResolver().openInputStream(resultUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: "+ error.getMessage());
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    //this is to compress image to 1mb
    public class compressimage extends AsyncTask<Bitmap,Void,Void>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(user_profile_img.this);
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
            new uploadimagetoserver().execute(urls);

        }
    }

    private void storeimage(Bitmap bitmap) {
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
        //Toast.makeText(this, "image size"+lengths, Toast.LENGTH_SHORT).show();
        Log.d("encoded",encodedimage);
    }

    public class uploadimagetoserver extends AsyncTask<String,Void,Void>{

        final ProgressDialog progressDialog = new ProgressDialog(user_profile_img.this);
        String message="";
        Handler handler = new Handler();
        int num=0;

        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Uploading image...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            //storeimage(bitmap);
        }

        @Override
        protected Void doInBackground(String... strings) {

            String url = strings[0];

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    message=response;
                    if(response.equalsIgnoreCase("Uploaded successfully")) {
                        startActivity(new Intent(user_profile_img.this, details_all.class)
                                .putExtra("username", username)
                                .putExtra("category", ids)
                                .putExtra("isadmin", viewingadminprofile));

                        Log.d(TAG, "onResponse: "+response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    NetworkResponse networkResponse = error.networkResponse;

                    getSupportActionBar().setTitle("Upload Failed...");

                    if(error.getMessage()!=null) {
                        message = error.getMessage();
                        Toast.makeText(user_profile_img.this, "Response code "+networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: networktime "+networkResponse.networkTimeMs);
                        Log.d(TAG, "onErrorResponse: statuscode "+networkResponse.statusCode);
                        Log.d(TAG, "onErrorResponse: datalength "+networkResponse.data.length);
                        Log.d(TAG, "onErrorResponse: allheader "+networkResponse.allHeaders.size());
                        Log.d(TAG, "onErrorResponse: header "+networkResponse.headers);
                        Log.d(TAG, "onErrorResponse: notmodified "+networkResponse.notModified);
                    }else{
                        message = "Failed to upload Image!";
                        Toast.makeText(user_profile_img.this, "Response code "+networkResponse.statusCode, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: networktime if null "+networkResponse.networkTimeMs);
                        Log.d(TAG, "onErrorResponse: statuscode if null "+networkResponse.statusCode);
                        Log.d(TAG, "onErrorResponse: datalength if null "+networkResponse.data.length);
                        Log.d(TAG, "onErrorResponse: allheader if null "+networkResponse.allHeaders.size());
                        Log.d(TAG, "onErrorResponse: header if null "+networkResponse.headers);
                        Log.d(TAG, "onErrorResponse: notmodified if null "+networkResponse.notModified);
                    }
                    Log.d(TAG, "onErrorResponse: error"+message);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String, String>();
                    params.put("image",encodedimage);
                    params.put("username",username);
                    return params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(5*60*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(user_profile_img.this);
            queue.add(request);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(!message.equals(""))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(user_profile_img.this, message, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onPostExecute: "+message);
                    }else{
                        handler.postDelayed(this,1000);
                        Log.d(TAG, "run: "+num++);
                        if(num>300){
                            progressDialog.dismiss();
                            uploadimagetoserver uploadimagetoserver = new uploadimagetoserver();
                            uploadimagetoserver.cancel(true);
                            if(uploadimagetoserver.isCancelled()){
                                Toast.makeText(user_profile_img.this, "cancelled task ", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(user_profile_img.this, "Task cancel failed ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            };
            runnable.run();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(user_profile_img.this,details_all.class)
        .putExtra("username",username)
        .putExtra("category",ids).putExtra("isadmin", viewingadminprofile));
    }
}
