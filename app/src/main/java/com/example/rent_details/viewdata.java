package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Objects;

public class viewdata extends AppCompatActivity {

    TextView tvdate,tvamount,tvunits,tvrent,tvbill;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdata);

        tvdate=findViewById(R.id.text_date);
        tvamount=findViewById(R.id.text_amount);
        tvunits=findViewById(R.id.text_units);
        tvrent=findViewById(R.id.text_rent);
        tvbill=findViewById(R.id.text_bill);

        Intent intent=getIntent();
        position= Objects.requireNonNull( intent.getExtras()).getInt("position");

        tvdate.setText(showdetails.renterArrayList.get(position).getDate());
        tvamount.setText(showdetails.renterArrayList.get(position).getAmount());
        tvunits.setText(showdetails.renterArrayList.get(position).getUnit());
        tvrent.setText(showdetails.renterArrayList.get(position).getRent());
        tvbill.setText(showdetails.renterArrayList.get(position).getBill());

    }
}
