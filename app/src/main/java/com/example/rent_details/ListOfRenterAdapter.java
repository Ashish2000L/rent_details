package com.example.rent_details;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class ListOfRenterAdapter extends ArrayAdapter<ListOfRenters> {

    Context contexts;
    ImageView imageView;
    List<ListOfRenters> arrayListOfRenters;
    String TAG="profileloading";

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
        imageView = view.findViewById(R.id.profileimageofrenter);

        tv_name.setText(arrayListOfRenters.get(position).getName());
        tv_dateofjoin.setText(arrayListOfRenters.get(position).getDate());
        loadimage(arrayListOfRenters.get(position).getProfileimage());
        return view;
    }

    public  void  loadimage(String url){

        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(contexts);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.circleCrop();
        requestOptions.priority(Priority.HIGH);
        requestOptions.fitCenter();

        Glide.with(contexts)
                .load(url)
                .apply(requestOptions)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        assert e != null;
                        //Toast.makeText(contexts, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onLoadFailed: "+e.getMessage());
                        circularProgressDrawable.stop();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //Toast.makeText(contexts, "Image Loaded", Toast.LENGTH_SHORT).show();
                        circularProgressDrawable.stop();
                        Log.d(TAG, "onResourceReady: loaded image");
                        return false;
                    }
                })
                .into(imageView);
    }
}
