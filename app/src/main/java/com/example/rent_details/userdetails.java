package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    EditText ed_date,ed_amount,ed_units;
    Button btn_insert;
    RadioGroup radioGroup_bill,radioGroup_rent;
    RadioButton radioButton_rent,radioButton_bill,a,b,c,d;
    String bill,rent,username,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        username = intent.getExtras().getString("username");
        getSupportActionBar().setTitle(username);
        ed_date     = findViewById(R.id.edtName);
        ed_amount    = findViewById(R.id.edtEmail);
        ed_units  = findViewById(R.id.edtContact);
        btn_insert = findViewById(R.id.btnInsert);
        radioGroup_bill = findViewById(R.id.radio_grp_bill_details);
        radioGroup_rent = findViewById(R.id.radio_grp_rent_details);
        a = findViewById(R.id.radio_paid_details);
        b = findViewById(R.id.radio_unpaid_details);
        c= findViewById(R.id.radio_paid_bill_details);
        d = findViewById(R.id.radio_unpaid_bill_details);

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertData();
            }
        });
    }

    public void radio_bill(View view)
    {
        int radioid = radioGroup_bill.getCheckedRadioButtonId();
        radioButton_bill = findViewById(radioid);

        bill=radioButton_bill.getText().toString();
        Toast.makeText(this, "Bill: "+bill, Toast.LENGTH_SHORT).show();
    }

    public void radio_rent(View view)
    {
        int radioid = radioGroup_rent.getCheckedRadioButtonId();
        radioButton_rent = findViewById(radioid);

        rent = radioButton_rent.getText().toString();
        Toast.makeText(this, "rent: "+rent, Toast.LENGTH_SHORT).show();
    }

    private void insertData() {

        final String name = ed_date.getText().toString().trim();
        final String email = ed_amount.getText().toString().trim();
        final String contact = ed_units.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        if(name.isEmpty()){
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();

        }
        else if(email.isEmpty()){
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();

        }
        else if(contact.isEmpty()){
            Toast.makeText(this, "Enter Contact", Toast.LENGTH_SHORT).show();

        }
        else if(rent.isEmpty()){
            Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
        }else if(bill.isEmpty())
        {
            Toast.makeText(this, "bill required", Toast.LENGTH_SHORT).show();
        }

        else{
            progressDialog.show();
            ed_date.setEnabled(false);
            ed_amount.setEnabled(false);
            ed_units.setEnabled(false);
            a.setEnabled(false);
            b.setEnabled(false);
            c.setEnabled(false);
            d.setEnabled(false);
            StringRequest request = new StringRequest(Request.Method.POST, "http://rentdetails.000webhostapp.com/insert.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if(response.equalsIgnoreCase("Data Inserted")){
                                Toast.makeText(userdetails.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                btn_insert.setVisibility(View.GONE);
                            }
                            else{
                                Toast.makeText(userdetails.this, response, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                ed_date.setEnabled(true);
                                ed_amount.setEnabled(true);
                                ed_units.setEnabled(true);
                                a.setEnabled(true);
                                b.setEnabled(true);
                                c.setEnabled(true);
                                d.setEnabled(true);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(userdetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    ed_date.setEnabled(true);
                    ed_amount.setEnabled(true);
                    ed_units.setEnabled(true);
                    a.setEnabled(true);
                    b.setEnabled(true);
                    c.setEnabled(true);
                    d.setEnabled(true);
                }
            }

            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String,String>();

                    params.put("username",username);
                    params.put("date",name);
                    params.put("amount",email);
                    params.put("units",contact);
                    params.put("rent",rent);
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
