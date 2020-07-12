package com.example.rent_details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

                uploadimage();

                break;
        }

        return true;
    }

    private void uploadimage() {

        final ProgressDialog progressDialog = new ProgressDialog(user_profile_img.this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        storeimage(bitmap);
        StringRequest request = new StringRequest(Request.Method.POST, "https://rentdetails.000webhostapp.com/uploadimage.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
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
                progressDialog.dismiss();
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

        if(requestCode==100 && resultCode==RESULT_OK && data!=null)
        {
            Uri filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

                //storeimage(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void storeimage(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);


        byte[] imagebytes = byteArrayOutputStream.toByteArray();
        encodedimage = android.util.Base64.encodeToString(imagebytes, Base64.DEFAULT);

        Log.d("encoded",encodedimage);
    }
}
