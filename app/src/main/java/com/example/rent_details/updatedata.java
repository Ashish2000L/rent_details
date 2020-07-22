package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.Objects;

public class updatedata extends AppCompatActivity {

    RadioGroup radioGroup_bill,radioGroup_rent;
    RadioButton radioButton_rent,radioButton_bill;
    String bill,rent,username,dates, amounts, units,notees="";
    TextView date_tv;
    EditText amount, unit,note;
    private int position;
    private String url="https://rentdetails.000webhostapp.com/UpdateDetail.php";
    ProgressDialog progressDialog;
    Button btn_update;
    CheckBox notecheckbox;
    int category;
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
        note = findViewById(R.id.notetexts);
        notecheckbox = findViewById(R.id.checkboxfornotes);
        radioButton_rent = findViewById(R.id.radio_paid);
        radioButton_bill = findViewById(R.id.radio_paid_bill);


        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");
        username=intent.getStringExtra("username");
        category = intent.getIntExtra("category",0);
        Objects.requireNonNull(getSupportActionBar()).setTitle(username);
        date_tv.setText(showdetails.renterArrayList.get(position).getDate());
        amount.setText(showdetails.renterArrayList.get(position).getAmount());
        unit.setText(showdetails.renterArrayList.get(position).getUnit());
        dates=showdetails.renterArrayList.get(position).getDate();
        String notes = showdetails.renterArrayList.get(position).getNote();
        if(!notes.equals("")){
            notecheckbox.setChecked(true);
            note.setVisibility(View.VISIBLE);
            note.setText(notes);

        }else{
            notecheckbox.setChecked(false);
            note.setVisibility(View.GONE);
        }

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
        }if(!radioButton_rent.isChecked()){
            Toast.makeText(this, "Rent status required", Toast.LENGTH_SHORT).show();
        }else if(!radioButton_bill.isChecked()){
            Toast.makeText(this, "Bill status required", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.show();
            amounts = amount.getText().toString().trim();
            units = unit.getText().toString();
            notees = note.getText().toString();
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
                    params.put("note", notees);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(updatedata.this);

            requestQueue.add(request);
        }

    }

    public void takenotehere(View view) {

        if(notecheckbox.isChecked()){
            note.setVisibility(View.VISIBLE);
        }else{
            note.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(updatedata.this,showdetails.class)
        .putExtra("username",username)
        .putExtra("category",category));
    }
}
