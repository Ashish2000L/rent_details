package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class register extends AppCompatActivity {

    EditText ed_username,ed_email,ed_password;
    String str_name,str_username,str_password;
    String category="admin";
    String url="https://rentdetails.000webhostapp.com/register.php";
    public static final  String shared_pref="shared_prefs";
    public static  final String user="username";
    public static final String pass="password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ed_email = findViewById(R.id.ed_email);
        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(register.this);
        builder.setTitle("Alert!!");
        builder.setMessage("If you are owner then register here, else ask you house owner to make a account. Are you a house owner?");
        builder.setPositiveButton("Yes",null);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(register.this,login.class));
                finish();
            }
        }).create().show();
    }

    private void pref(){

        SharedPreferences preferences =getSharedPreferences(shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor =preferences.edit();
        editor.putString(user,str_username);
        editor.putString(pass,str_password);
        editor.apply();
    }


    public void moveToLogin(View view) {
        startActivity(new Intent(this,login.class));
        finish();
    }

    public void Register(View view) {

        final ProgressDialog progressDialog = new ProgressDialog( this);
        progressDialog.setMessage("Please wait...");
        if(ed_username.getText().toString().equals("")){
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
        }
        else if(ed_email.getText().toString().equals("")){
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(ed_password.getText().toString().equals("")){
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.show();
            str_name=ed_username.getText().toString().trim();
            str_username=ed_email.getText().toString().trim();
            str_password=ed_password.getText().toString().trim();

            StringRequest request =new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.equalsIgnoreCase("registration successful"))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(register.this, response, Toast.LENGTH_LONG).show();
                        ed_email.setText("");
                        ed_username.setText("");
                        ed_password.setText("");
                        pref();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(register.this, response, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    if(error.getMessage()!=null){
                    Toast.makeText(register.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }else{
                        Toast.makeText(register.this, "error error", Toast.LENGTH_SHORT).show();
                    }
            }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();

                    params.put("Name",str_name);
                    params.put("username",str_username);
                    params.put("password",str_password);
                    params.put("category",category);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(register.this);

            requestQueue.add(request);
        }

    }
}
