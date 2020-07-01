package com.example.rent_details;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

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
        LinearLayout linearLayout = view.findViewById(R.id.linearlayout);
        TextView tv_date=view.findViewById(R.id.txt_date);
        TextView tv_amount = view.findViewById(R.id.txt_amount);

        tv_date.setText(arraylistrenter.get(position).getDate());
        tv_amount.setText(arraylistrenter.get(position).getAmount());

        if(arraylistrenter.get(position).getRent().equals("Paid"))
        {
            if(arraylistrenter.get(position).getBill().equals("Paid"))
            {
                linearLayout.setBackgroundResource(R.drawable.gradient);
            }else{
                linearLayout.setBackgroundResource(R.drawable.gradient1);
            }
        }else{
            linearLayout.setBackgroundResource(R.drawable.gradient1);
        }

        return view;
    }
}
