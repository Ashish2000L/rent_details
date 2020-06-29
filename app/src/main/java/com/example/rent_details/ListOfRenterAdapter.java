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

public class ListOfRenterAdapter extends ArrayAdapter<ListOfRenters> {

    Context contexts;
    List<ListOfRenters> arrayListOfRenters;

    public ListOfRenterAdapter(@NonNull Context context, List<ListOfRenters> arrayListOfRenters) {
        super(context, R.layout.custom_list_of_renters,arrayListOfRenters);

        this.contexts=context;
        this.arrayListOfRenters = arrayListOfRenters;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_of_renters,null,true);

        TextView tv_name=view.findViewById(R.id.tv_name);
        TextView tv_dateofjoin= view.findViewById(R.id.tv_date_of_join);

        tv_name.setText(arrayListOfRenters.get(position).getName());
        tv_dateofjoin.setText(arrayListOfRenters.get(position).getDate());

        return view;
    }
}
