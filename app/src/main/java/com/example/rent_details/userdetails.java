package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

public class userdetails extends AppCompatActivity {

    EditText txtName,txtEmail,txtContact,txtAddress,txtbill;
    Button btn_insert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        txtName     = findViewById(R.id.edtName);
        txtEmail    = findViewById(R.id.edtEmail);
        txtContact  = findViewById(R.id.edtContact);
        txtAddress  = findViewById(R.id.edtAddress);
        txtbill = findViewById(R.id.edtbill);
        btn_insert = findViewById(R.id.btnInsert);

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertData();
            }
        });
    }

    private void insertData() {

        final String name = txtName.getText().toString().trim();
        final String email = txtEmail.getText().toString().trim();
        final String contact = txtContact.getText().toString().trim();
        final String address = txtAddress.getText().toString().trim();
        final String bill = txtbill.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        if(name.isEmpty()){
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(email.isEmpty()){
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(contact.isEmpty()){
            Toast.makeText(this, "Enter Contact", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(address.isEmpty()){
            Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
            return;
        }else if(bill.isEmpty())
        {
            Toast.makeText(this, "bill required", Toast.LENGTH_SHORT).show();
        }

        else{
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, "http://rentdetails.000webhostapp.com/insert.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if(response.equalsIgnoreCase("Data Inserted")){
                                Toast.makeText(userdetails.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                txtName.setText("");
                                txtAddress.setText("");
                                txtContact.setText("");
                                txtEmail.setText("");
                                txtbill.setText("");
                            }
                            else{
                                Toast.makeText(userdetails.this, response, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                txtName.setText("");
                                txtAddress.setText("");
                                txtContact.setText("");
                                txtEmail.setText("");
                                txtbill.setText("");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(userdetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    txtName.setText("");
                    txtAddress.setText("");
                    txtContact.setText("");
                    txtEmail.setText("");
                    txtbill.setText("");
                }
            }

            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String,String>();

                    params.put("date",name);
                    params.put("amount",email);
                    params.put("units",contact);
                    params.put("rent",address);
                    params.put("bill",bill);



                    return params;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(userdetails.this);
            requestQueue.add(request);



        }




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
