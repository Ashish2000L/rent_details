package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class viewdata extends AppCompatActivity {

    TextView tvdate,tvamount,tvunits,tvaddedon,tvlastupdate,tvrent,tvbill;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdata);

        tvdate=findViewById(R.id.text_date);
        tvamount=findViewById(R.id.text_amount);
        tvunits=findViewById(R.id.text_units);
        tvaddedon = findViewById(R.id.useraddeddate);
        tvlastupdate = findViewById(R.id.lastupdate);
        tvrent = findViewById(R.id.rent);
        tvbill = findViewById(R.id.bill);

        Intent intent=getIntent();
        position= Objects.requireNonNull( intent.getExtras()).getInt("position");
        tvdate.setText(showdetails.renterArrayList.get(position).getDate());
        tvamount.setText(showdetails.renterArrayList.get(position).getAmount());
        tvunits.setText(showdetails.renterArrayList.get(position).getUnit());

        if(showdetails.renterArrayList.get(position).getRent().equals("Paid")){
            tvrent.setBackgroundColor(Color.GREEN);
            tvrent.setText("Paid");
        }else{
            tvrent.setBackgroundColor(Color.RED);
            tvrent.setText("UnPaid");
        }

        if(showdetails.renterArrayList.get(position).getBill().equals("Paid")){
            tvbill.setBackgroundColor(Color.GREEN);
            tvbill.setText("Paid");
        }else{
            tvbill.setBackgroundColor(Color.RED);
            tvbill.setText("Unpaid");
        }

        tvaddedon.setText(showdetails.renterArrayList.get(position).getAddedon());
        tvlastupdate.setText(showdetails.renterArrayList.get(position).getLastupdate());
    }
    @Override
    protected void onDestroy() {
        stopService(new Intent(this, volleynotificationservice.class));
        super.onDestroy();
    }
}
