package com.example.rent_details;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class login extends AppCompatActivity {

    EditText ed_username,ed_password;
    String str_category,str_username,str_password;
    String url="https://rentdetails.000webhostapp.com/login.php";
    public static final  String shared_pref="shared_prefs";
    public static  final String user="username";
    public static final String pass="password";
    Button btnlogin;

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
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    public void shared_prefs(){

        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(user,str_username);
        editor.putString(pass,str_password);
        editor.apply();
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
                        startActivity(new Intent(getApplicationContext(),ListOfRentersForAdmin.class)
                                .putExtra("username",str_username));
                        finish();

                    }else if(response.equalsIgnoreCase("renter")){

                        ed_username.setText("");
                        ed_password.setText("");
                        shared_prefs();
                        startActivity(new Intent(login.this,showdetails.class)
                        .putExtra("username",str_username)
                        .putExtra("category",1));
                        finish();
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
        startActivity(new Intent(getApplicationContext(), register.class));
        finish();
    }
}
