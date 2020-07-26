package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class userdetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText ed_amount,ed_units,notetext;
    Button btn_insert;
    RadioGroup radioGroup_bill,radioGroup_rent;
    RadioButton radioButton_rent,radioButton_bill,a,b,c,d;
    String bill,rent,username,url;
    String TAG="datepickerpickthedate";
    TextView ed_date;
    CheckBox checkBox;
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
        notetext = findViewById(R.id.notetext);
        checkBox = findViewById(R.id.checkboxfornote);
        radioButton_rent = findViewById(R.id.radio_paid_details);
        radioButton_bill = findViewById(R.id.radio_paid_bill_details);
        a = findViewById(R.id.radio_paid_details);
        b = findViewById(R.id.radio_unpaid_details);
        c= findViewById(R.id.radio_paid_bill_details);
        d = findViewById(R.id.radio_unpaid_bill_details);

        notetext.setText("");
        ed_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new datepickerfragment();
                datepicker.show(getSupportFragmentManager(),"Date Picker");
            }
        });

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertData();
            }
        });



    }

    public void takenotehere(View view){
        if(checkBox.isChecked()){
            notetext.setVisibility(View.VISIBLE);
        }else
            if (!checkBox.isChecked()){
                notetext.setVisibility(View.GONE);
        }
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
        final String noteontherent;
        if(checkBox.isChecked()) {
            noteontherent = notetext.getText().toString();
        }else{
            noteontherent="";
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        if(name.isEmpty()){
            Toast.makeText(this, "Enter Date", Toast.LENGTH_SHORT).show();

        }
        else if(email.isEmpty()){
            Toast.makeText(this, "Enter Amount", Toast.LENGTH_SHORT).show();

        }
        else if(contact.isEmpty()){
            Toast.makeText(this, "Enter Units", Toast.LENGTH_SHORT).show();

        }
        else if(!radioButton_rent.isChecked()){
            Toast.makeText(this, "Rent required", Toast.LENGTH_SHORT).show();
        }else if(!radioButton_bill.isChecked())
        {
            Toast.makeText(this, "Bill required", Toast.LENGTH_SHORT).show();
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
            notetext.setEnabled(false);
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
                    if(String.valueOf(error.networkResponse.statusCode)!=null){
                        new erroinfetch().execute("status code: "+error.networkResponse.statusCode);
                        new erroinfetch().execute("error message: "+error.getMessage());
                    }
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
                    params.put("note",noteontherent);
                    Log.d(TAG, "getParams: "+noteontherent);
                    return params;
                }
            };


            RequestQueue requestQueue = Volley.newRequestQueue(userdetails.this);
            requestQueue.add(request);

        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        String currentdatestring = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
        String datefield = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(c.getTime());
        String yearfield = DateFormat.getDateInstance(DateFormat.YEAR_FIELD).format(c.getTime());
        String monthfield = DateFormat.getDateInstance(DateFormat.MONTH_FIELD).format(c.getTime());

        Log.d(TAG, "onDateSet: date field "+datefield);
        Log.d(TAG, "onDateSet: year field "+yearfield);
        Log.d(TAG, "onDateSet: month field "+monthfield );

        ed_date.setText(currentdatestring);

    }

    public class erroinfetch extends AsyncTask<String,Void,Void> {

        public static final  String shared_pref="shared_prefs";
        public static  final String user="username";
        public String loginusername;
        private String errorurl="https://rentdetails.000webhostapp.com/error_in_app.php";
        private String TAG="errorfinding";
        private String classname="splash: ";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences sharedPreferences =getSharedPreferences(shared_pref,MODE_PRIVATE);
            loginusername=sharedPreferences.getString(user,"");
        }

        @Override
        protected Void doInBackground(String... strings) {

            final String errors=strings[0];

            StringRequest request = new StringRequest(Request.Method.POST, errorurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: ");
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("username",loginusername);
                    params.put("error",classname+errors);
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(userdetails.this);
            queue.add(request);

            return null;
        }
    }
}
