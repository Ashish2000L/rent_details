package com.example.rent_details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DownloadManager;
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
import android.widget.TextView;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListOfRentersForAdmin extends AppCompatActivity {

    ListView listView;
    ListOfRenterAdapter listOfRenterAdapter;
    ArrayList<ListOfRenters> listOfRentersArrayList = new ArrayList<>();
    TextView details_of_server;
    ListOfRenters listOfRenters;
    String byadmin,welcomemsg;
    String url="https://rentdetails.000webhostapp.com/ListOfRenterRetrive.php";
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_renters_for_admin);

        Intent intent =getIntent();
        byadmin = intent.getStringExtra("username");

        welcomemsg = "Welcome "+ byadmin;
        getSupportActionBar().setTitle(welcomemsg);

        listView = findViewById(R.id.listofrenters_listview);
        swipeRefreshLayout = findViewById(R.id.listofrenters);
        listOfRenterAdapter=new ListOfRenterAdapter(this,listOfRentersArrayList);

        listView.setAdapter(listOfRenterAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder =new AlertDialog.Builder(view.getContext());
                ProgressDialog progressDialog = new ProgressDialog(view.getContext());

                CharSequence[] dialogitem ={"View Data","Delete Data"};

                builder.setTitle(listOfRentersArrayList.get(position).getName());

                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0: Intent intent1 = new Intent(getApplicationContext(),showdetails.class);
                                        intent1.putExtra("username",listOfRentersArrayList.get(position).getUsername());
                                        intent1.putExtra("category",0);
                                        startActivity(intent1);
                                break;
                            case 1: DeleteUserAndData(listOfRentersArrayList.get(position).getUsername());
                                break;

                        }
                    }
                });
                builder.create().show();
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrive_list_of_renters();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        retrive_list_of_renters();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.listofrenteradminmenu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.adminsettings)
        {
            startActivity(new Intent(getApplicationContext(),details_all.class)
            .putExtra("category",0)
            .putExtra("username",byadmin)
            .putExtra("isadmin",true));
        }else if(item.getItemId() == R.id.newrenter)
        {
            startActivity(new Intent(getApplicationContext(),new_renter.class)
            .putExtra("username",byadmin));
        }

        return true;
    }

    public void retrive_list_of_renters()
    {   progressDialog = new ProgressDialog(this);
    progressDialog.setCancelable(false);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setMessage("Retriving user data...");
    progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equalsIgnoreCase("username is not available"))
                {
                    listView.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    Toast.makeText(ListOfRentersForAdmin.this, response, Toast.LENGTH_LONG).show();
                }else {
                    listOfRentersArrayList.clear();

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                            for (int i = jsonArray.length()-1; i >=0; i--) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String name = object.getString("name");
                                String usernames = object.getString("username");
                                String date_of_joining = object.getString("joiningdate");
                                String password = object.getString("password");
                                String category = object.getString("category");
                                String lastupdated = object.getString("lastupdate");

                                //Toast.makeText(ListOfRentersForAdmin.this, usernames, Toast.LENGTH_LONG).show();
                                listOfRenters = new ListOfRenters(name, usernames, date_of_joining,lastupdated,password,category);
                                listOfRentersArrayList.add(listOfRenters);
                                listOfRenterAdapter.notifyDataSetChanged();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listView.setVisibility(View.GONE);
                        details_of_server.setVisibility(View.VISIBLE);
                        details_of_server.setText(error.getMessage());
                        Toast.makeText(ListOfRentersForAdmin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("byadmins",byadmin);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ListOfRentersForAdmin.this);
        requestQueue.add(request);
    }

    private void DeleteUserAndData(final String username) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ListOfRentersForAdmin.this);
        builder.setTitle("WARNING");
        builder.setIcon(R.drawable.warning);
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete!!.\nAll user data will be deleted.\nOnce deleted cannot be retirved back...");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(ListOfRentersForAdmin.this);
                progressDialog.setMessage("Deleting data...");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                StringRequest request = new StringRequest(Request.Method.POST, "https://rentdetails.000webhostapp.com/DeletingUserFromDatabase.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equalsIgnoreCase("data deleted")){
                                    progressDialog.dismiss();
                                    retrive_list_of_renters();
                                    Toast.makeText(ListOfRentersForAdmin.this, response, Toast.LENGTH_SHORT).show();
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(ListOfRentersForAdmin.this, response, Toast.LENGTH_SHORT).show();
                                    retrive_list_of_renters();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(ListOfRentersForAdmin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> params = new HashMap<>();
                        params.put("username",username);
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
                retrive_list_of_renters();
            }
        });
        builder.create().show();

    }

}
