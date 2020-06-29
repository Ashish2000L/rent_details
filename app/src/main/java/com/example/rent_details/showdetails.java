package com.example.rent_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    String url ="https://rentdetails.000webhostapp.com/retrive.php";
    renter renter;
    String st_username;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdetails);

        listView=findViewById(R.id.listview);

        Intent intent = getIntent();
        st_username = intent.getStringExtra("username");
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
                            case 1: startActivity(new Intent(getApplicationContext(),updatedata.class)
                            .putExtra("position",position)
                            .putExtra("username",st_username));
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
    public void retrivedata()
    {   progressDialog =new ProgressDialog(this);
    progressDialog.setCancelable(false);
    progressDialog.setMessage("Retriving users data");
    progressDialog.show();

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
                                    String units=object.getString("fifo");
                                    String rent=object.getString("rent");
                                    String bill=object.getString("bill");
                                    String addedon = object.getString("addedon");
                                    String lastupdate = object.getString("lastupdate");

                                    renter = new renter(i+1,date,amount,units,rent,bill,addedon,lastupdate);
                                    renterArrayList.add(renter);
                                    myadapter.notifyDataSetChanged();

                                }
                            }

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(showdetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("username",st_username);
                return params;
            }
        };;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    public void deletedata(final String date)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("WARNING");
        builder.setIcon(R.drawable.warning);
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete!!.\n Once deleted cannot be retirved back...");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(showdetails.this);
                progressDialog.setMessage("Deleting data...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                StringRequest request = new StringRequest(Request.Method.POST, "http://rentdetails.000webhostapp.com/delete.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equalsIgnoreCase("Data deleted successfully")){
                                    progressDialog.dismiss();
                                    retrivedata();
                                    Toast.makeText(showdetails.this, response, Toast.LENGTH_SHORT).show();
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(showdetails.this, response, Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(showdetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> params = new HashMap<>();
                        params.put("date", date);
                        params.put("username",st_username);
                        return  params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(request);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                retrivedata();
            }
        });
        builder.create().show();
    }
}
