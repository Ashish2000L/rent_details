package com.example.rent_details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Objects;

public class showdetails extends AppCompatActivity {

    ListView listView;
    myadapter myadapter;
    public static ArrayList<renter> renterArrayList = new ArrayList<>();
    String url ="https://rentdetails.000webhostapp.com/retrive.php";
    renter renter;
    String st_username;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    //ListOfRentersForAdmin listOfRentersForAdmin;
    private int category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdetails);

        listView=findViewById(R.id.listviews);
        swipeRefreshLayout = findViewById(R.id.swipref);
        Intent intent = getIntent();
        st_username = intent.getExtras().getString("username");
        //Objects.requireNonNull(getSupportActionBar()).setTitle(st_username);
        category = intent.getExtras().getInt("category");
        getSupportActionBar().setTitle(st_username);

        if(category==1) {
            Toast.makeText(getApplicationContext(), "Welcome " + st_username, Toast.LENGTH_SHORT).show();
        }

        myadapter = new myadapter(this,renterArrayList);
        listView.setAdapter(myadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(category==0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    CharSequence[] dialogitem = {"View Data", "Edit Data", "Delete Data"};

                    builder.setTitle(renterArrayList.get(position).getDate());
                    builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            switch (i) {
                                case 0:
                                    startActivity(new Intent(getApplicationContext(), viewdata.class).putExtra(
                                            "position", position));
                                    break;
                                case 1:
                                    startActivity(new Intent(getApplicationContext(), updatedata.class)
                                            .putExtra("position", position)
                                            .putExtra("username", st_username));
                                    break;
                                case 2:
                                    deletedata(renterArrayList.get(position).getDate());
                                    break;
                            }

                        }
                    });

                    builder.create().show();
                }else if(category==1){
                    startActivity(new Intent(getApplicationContext(), viewdata.class).putExtra(
                            "position", position));
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrivedata();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        retrivedata();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(category==0) {
            MenuInflater inflater =getMenuInflater();
            inflater.inflate(R.menu.toolbar, menu);
        }else if(category==1){
            MenuInflater inflater =getMenuInflater();
            inflater.inflate(R.menu.details_renter, menu);
        }
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


            switch (item.getItemId()) {
                case R.id.insert:
                    startActivity(new Intent(getApplicationContext(), userdetails.class)
                            .putExtra("username", st_username));
                    break;
                case R.id.renter_detail:
                    startActivity(new Intent(getApplicationContext(), details_all.class)
                    .putExtra("category",0)
                    .putExtra("username",st_username));
                    break;
                case R.id.renter_setting:
                    startActivity(new Intent(getApplicationContext(), details_all.class)
                            .putExtra("category",1)
                            .putExtra("username",st_username));
                    break;
//                case R.id.due_bills:
//                    break;
            }

        return true;
        //return super.onOptionsItemSelected(item);
    }

    public void retrivedata()
    {
       progressDialog =new ProgressDialog(showdetails.this);
    progressDialog.setCancelable(false);
    progressDialog.setMessage("Retriving users data");
    progressDialog.show();

        listView.setVisibility(View.VISIBLE);
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
                                for(int i =jsonArray.length()-1 ; i>=0;i--)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String date=object.getString("date");
                                    String amount=object.getString("amount");
                                    String units=object.getString("units");
                                    String rent=object.getString("rent");
                                    String bill=object.getString("bill");
                                    String addedon = object.getString("addedon");
                                    String lastupdate = object.getString("lastupdate");

                                    renter = new renter(date,amount,units,rent,bill,addedon,lastupdate);
                                    renterArrayList.add(renter);
                                    myadapter.notifyDataSetChanged();

                                }
                            }

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(showdetails.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        };

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
