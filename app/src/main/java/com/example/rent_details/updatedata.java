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
import android.widget.TextView;
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

public class updatedata extends AppCompatActivity {

    RadioGroup radioGroup_bill,radioGroup_rent;
    RadioButton radioButton_rent,radioButton_bill;
    String bill,rent,username,dates, amounts, units;
    TextView date_tv;
    EditText amount, unit;
    private int position;
    private String url="https://rentdetails.000webhostapp.com/UpdateDetail.php";
    ProgressDialog progressDialog;
    Button btn_update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatedata);
        radioGroup_bill=findViewById(R.id.radio_grp_bill);
        radioGroup_rent=findViewById(R.id.radio_grp_rent);
        date_tv=findViewById(R.id.date_tv);
        amount = findViewById(R.id.amountss);
        unit = findViewById(R.id.unitss);
        btn_update = findViewById(R.id.btn_update);

        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");
        username=intent.getStringExtra("username");
        date_tv.setText(showdetails.renterArrayList.get(position).getDate());
        amount.setText(showdetails.renterArrayList.get(position).getAmount());
        unit.setText(showdetails.renterArrayList.get(position).getUnit());
        dates=showdetails.renterArrayList.get(position).getDate();
    }

    public void radio_rent(View view){

        int radioid = radioGroup_rent.getCheckedRadioButtonId();
        radioButton_rent = findViewById(radioid);

        rent = radioButton_rent.getText().toString();
        Toast.makeText(this, "You checked: "+ rent, Toast.LENGTH_SHORT).show();
    }

    public void radio_bill(View view){
        int radioid = radioGroup_bill.getCheckedRadioButtonId();
        radioButton_bill = findViewById(radioid);

        bill = radioButton_bill.getText().toString();
        Toast.makeText(this, "You checked: "+ bill, Toast.LENGTH_SHORT).show();
    }
    public void update_data(View view){

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Updating data...");
        progressDialog.setCancelable(false);

        if(amount.getText().toString().trim().equals("")){
            Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_LONG).show();
        }else if(unit.getText().toString().trim().equals("")){
            Toast.makeText(this,"Unit cannot be empty",Toast.LENGTH_LONG).show();
        }if(rent.trim().isEmpty()){
            Toast.makeText(this, "Rent status required", Toast.LENGTH_SHORT).show();
        }else if(bill.trim().isEmpty()){
            Toast.makeText(this, "Bill status required", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.show();
            amounts = amount.getText().toString().trim();
            units = unit.getText().toString();
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    if(response.equalsIgnoreCase("Data Updated")){
                        btn_update.setVisibility(View.GONE);
                        amount.setEnabled(false);
                        unit.setEnabled(false);
                        radioButton_bill.setEnabled(false);
                        radioButton_rent.setEnabled(false);
                        Toast.makeText(updatedata.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(updatedata.this, response, Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(updatedata.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("amounts", amounts);
                    params.put("rent",rent);
                    params.put("bill",bill);
                    params.put("date",dates);
                    params.put("username",username);
                    params.put("units", units);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(updatedata.this);

            requestQueue.add(request);
        }

    }
}
