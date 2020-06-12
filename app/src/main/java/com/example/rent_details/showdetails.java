package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class showdetails extends AppCompatActivity {

    ListView listView;
    myadapter myadapter;
    public static ArrayList<renter> renterArrayList = new ArrayList<>();
    String url ="http://rentdetails.000webhostapp.com/retrive.php";
    renter renter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdetails);

        listView=findViewById(R.id.listview);

        myadapter = new myadapter(this,renterArrayList);
        listView.setAdapter(myadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                ProgressDialog progressDialog = new ProgressDialog(view.getContext());

                CharSequence[] dialogitem={"view data","edit data","delete data"};

                builder.setTitle(renterArrayList.get(position).getDate());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {


                        switch(i){
                            case 0:
                                startActivity(new Intent(getApplicationContext(),viewdata.class).putExtra(
                                        "position",position
                                ));
                                break;
                            case 1:
                                break;
                            case 2:
                                deletedata(renterArrayList.get(position).getDate());
                                break;
                        }

                    }
                });

                builder.create().show();
            }
        });

        retrivedata();
    }

    public void deletedata(final String position)
    {
        StringRequest request = new StringRequest(Request.Method.POST, "http://rentdetails.000webhostapp.com/delete.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equalsIgnoreCase("Data deleted successfully")){
                            Toast.makeText(showdetails.this, "Data deleted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(showdetails.this, "Not deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(showdetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("date", position);
                return super.getParams();
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    public void retrivedata()
    {

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        renterArrayList.clear();
                        try{

                            JSONObject jsonObject = new JSONObject(response);
                            String success =jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("userdetails");
                            if(success.equals("1"))
                            {
                                for(int i =0 ; i<jsonArray.length();i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String date=object.getString("date");
                                    String amount=object.getString("amount");
                                    String units=object.getString("units");
                                    String rent=object.getString("rent");
                                    String bill=object.getString("bill");

                                    renter = new renter(date,amount,units,rent,bill);
                                    renterArrayList.add(renter);
                                    myadapter.notifyDataSetChanged();

                                }
                            }

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(showdetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }
}
