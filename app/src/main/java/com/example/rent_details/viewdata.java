package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class viewdata extends AppCompatActivity {

    TextView tvdate,tvamount,tvunits,tvaddedon,tvlastupdate;
    int position;
    ImageView paid,unpaid,paid_bill,unpaid_bill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdata);

        tvdate=findViewById(R.id.text_date);
        tvamount=findViewById(R.id.text_amount);
        tvunits=findViewById(R.id.text_units);
        paid = findViewById(R.id.img_paid);
        unpaid = findViewById(R.id.img_unpaid);
        paid_bill = findViewById(R.id.img_paid_bill);
        unpaid_bill = findViewById(R.id.img_unpaid_bill);
        tvaddedon = findViewById(R.id.useraddeddate);
        tvlastupdate = findViewById(R.id.lastupdate);

        Intent intent=getIntent();
        position= Objects.requireNonNull( intent.getExtras()).getInt("position");

        tvdate.setText(showdetails.renterArrayList.get(position).getDate());
        tvamount.setText(showdetails.renterArrayList.get(position).getAmount());
        tvunits.setText(showdetails.renterArrayList.get(position).getUnit());
        //tvrent.setText(showdetails.renterArrayList.get(position).getRent());
        //tvbill.setText(showdetails.renterArrayList.get(position).getBill());
        if(showdetails.renterArrayList.get(position).getRent().equals("Paid")){
            paid.setVisibility(View.VISIBLE);
        }else{
            unpaid.setVisibility(View.VISIBLE);
        }

        if(showdetails.renterArrayList.get(position).getBill().equals("Paid")){
            paid_bill.setVisibility(View.VISIBLE);
        }else{
            unpaid_bill.setVisibility(View.VISIBLE);
        }

        tvaddedon.setText(showdetails.renterArrayList.get(position).getAddedon());
        tvlastupdate.setText(showdetails.renterArrayList.get(position).getLastupdate());
    }
}
