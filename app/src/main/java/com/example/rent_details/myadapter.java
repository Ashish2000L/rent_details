package com.example.rent_details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class myadapter extends ArrayAdapter<renter> {


    Context context;
    List<renter> arraylistrenter;
    public myadapter(@NonNull Context context, List<renter> arraylistrenter) {
        super(context, R.layout.custom_list_item,arraylistrenter);

        this.context=context;
        this.arraylistrenter=arraylistrenter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_item,null,true);
        //TextView tv_id = view.findViewById(R.id.txt_id);
        TextView tv_date=view.findViewById(R.id.txt_date);
        TextView tv_amount = view.findViewById(R.id.txt_amount);
        TextView tv_units = view.findViewById(R.id.txt_units);
        TextView tv_rent = view.findViewById(R.id.txt_rent);
        TextView tv_bill = view.findViewById(R.id.txt_bill);

        tv_date.setText(arraylistrenter.get(position).getDate());
        tv_amount.setText(arraylistrenter.get(position).getAmount());
        tv_units.setText(arraylistrenter.get(position).getUnit());
        tv_rent.setText(arraylistrenter.get(position).getRent());
        tv_bill.setText(arraylistrenter.get(position).getBill());



        return view;
    }
}
