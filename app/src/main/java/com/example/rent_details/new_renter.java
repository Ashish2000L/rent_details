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

public class new_renter extends AppCompatActivity {
    EditText name, username, password, phone;
    ProgressDialog progressDialog;
    String byadmin,category="Renter",url="https://rentdetails.000webhostapp.com/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_renter);

        name = findViewById(R.id.name_ed);
        username = findViewById(R.id.username_ed);
        password = findViewById(R.id.password_ed);
        phone = findViewById(R.id.phone_number);

        Intent intent= getIntent();
        byadmin = intent.getStringExtra("username");
        getSupportActionBar().setTitle(byadmin);
    }

    public void insert_new_renter(View view) {

        final String names = name.getText().toString();
        final String usernames = username.getText().toString().trim();
        final String passwords = password.getText().toString().trim();
        final String phones = phone.getText().toString().trim();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Adding User...");
        progressDialog.setCancelable(false);

        if (names.isEmpty()) {
            Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_LONG).show();
        } else if (usernames.isEmpty()) {
            Toast.makeText(this, "Unit cannot be empty", Toast.LENGTH_LONG).show();
        }
        if (passwords.isEmpty()) {
            Toast.makeText(this, "Rent status required", Toast.LENGTH_SHORT).show();
        } else if (phones.isEmpty()) {
            Toast.makeText(this, "Bill status required", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    if (response.equalsIgnoreCase("registration successfull")) {
                        progressDialog.dismiss();
                        name.setText("");
                        password.setText("");
                        username.setText("");
                        phone.setText("");
                        Toast.makeText(new_renter.this, "Renter Regestration complete", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(new_renter.this, response, Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(new_renter.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("Name", names);
                    params.put("username", usernames);
                    params.put("password", passwords);
                    params.put("phonenumber", phones);
                    params.put("byadmin", byadmin);
                    params.put("category", category);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(new_renter.this);

            requestQueue.add(request);

        }
    }
}
