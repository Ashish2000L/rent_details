package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    EditText ed_username,ed_password;
    String str_category,str_username,str_password;
    String url="http://rentdetails.000webhostapp.com/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_passwords);
    }

    public void Login(View view) {

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

                        Toast.makeText(login.this, response, Toast.LENGTH_LONG).show();

                        ed_username.setText("");
                        ed_password.setText("");

                    }else if(response.equalsIgnoreCase("renter")){
                        Toast.makeText(login.this, response, Toast.LENGTH_LONG).show();

                        ed_username.setText("");
                        ed_password.setText("");
                    }
                    else{
                        Toast.makeText(login.this, response, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
