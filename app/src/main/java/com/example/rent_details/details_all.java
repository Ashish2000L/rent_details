package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class details_all extends AppCompatActivity {

    EditText name,password,category,joiningdate,lastupdate,phonenumber;
    TextView username;
    private int id;
    private String usernames;
    ProgressDialog progressDialog;
    String names,byadmin,pass,cat,join,update,phone;
    Button updatedetail;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_all);

        //listOfRentersForAdmin.listOfRentersArrayList.get()
        Intent intent = getIntent();
        usernames = intent.getExtras().getString("username",null);
        id = intent.getExtras().getInt("category");
        flag = intent.getBooleanExtra("isadmin",false);


        name = findViewById(R.id.names);
        password = findViewById(R.id.passwords);
        category = findViewById(R.id.category);
        joiningdate = findViewById(R.id.joiningdate);
        lastupdate = findViewById(R.id.lastupdated);
        username = findViewById(R.id.usernames);
        updatedetail =findViewById(R.id.update_detail);
        phonenumber = findViewById(R.id.phone_number);

        username.setText(usernames);
        fetch_user_data();

    }

    private void fetch_user_data(){
        progressDialog = new ProgressDialog(details_all.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Retriving users data...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "https://rentdetails.000webhostapp.com/fetch_userdata.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{

                            JSONObject jsonObject = new JSONObject(response);
                            String success =jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if(success.equals("1"))
                            {
                                for(int i =0 ; i<jsonArray.length();i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    names=object.getString("name");
                                    pass=object.getString("password");
                                    cat=object.getString("category");
                                    byadmin=object.getString("byadmin");
                                    join=object.getString("joiningdate");
                                    update = object.getString("lastupdate");
                                    phone = object.getString("phonenumber");
                                    ///Toast.makeText(details_all.this, names, Toast.LENGTH_SHORT).show();
                                }
                            }

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(details_all.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        //Toast.makeText(details_all.this, names, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        if(id==0){
                            showdatatoadmin(names,pass,join,update,cat,phone);
                        }else{
                            showdetailtorenter(names,pass,join,update,cat,phone);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(details_all.this, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",usernames);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void showdatatoadmin(String nam,String pa,String jone,String up,String ca,String phn)
    {
        //Toast.makeText(this, "entered show data", Toast.LENGTH_SHORT).show();
        if(flag==false) {
            Toast.makeText(this, phn, Toast.LENGTH_LONG).show();

            password.setText(pa);
            category.setText(ca);
            joiningdate.setText(jone);
            lastupdate.setText(up);
            name.setText(nam);
            phonenumber.setText(phn);
            updatedetail.setVisibility(View.GONE);
        }else if(flag==true){
            password.setText(pass);
            password.setEnabled(true);
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            category.setText(cat);
            joiningdate.setText(join);
            lastupdate.setText(update);
            name.setText(names);
            phonenumber.setText(phone);
            phonenumber.setVisibility(View.GONE);
            name.setEnabled(true);
        }
    }

    private void showdetailtorenter(String nam,String pa,String jone,String up,String ca,String phn)
    {
        password.setText(pa);
        password.setEnabled(true);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        category.setText(ca);
        joiningdate.setText(jone);
        lastupdate.setText(up);
        name.setText(nam);
        name.setEnabled(true);
        phonenumber.setText(phn);
        phonenumber.setEnabled(true);
    }

    public void updetail(View view) {
        final String naf = name.getText().toString();
        final String pas = password.getText().toString();
        phone = phonenumber.getText().toString();

        progressDialog = new ProgressDialog(details_all.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Retriving users data...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "https://rentdetails.000webhostapp.com/update_userdata.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equalsIgnoreCase("Data Updated"))
                        {
                            progressDialog.dismiss();
                            name.setEnabled(false);
                            password.setEnabled(false);
                            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            updatedetail.setVisibility(View.GONE);
                            Toast.makeText(details_all.this, response, Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(details_all.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(details_all.this, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",usernames);
                params.put("name",naf);
                params.put("password",pas);
                params.put("phonenumber",phone);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
