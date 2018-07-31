package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterActivity extends AppCompatActivity {


    private Spinner spinner_country;
    private Spinner spinner_tag;
    private Spinner spinner_byNGO;
    private Spinner spinner_category;
    private ArrayList array_country;
    private ArrayList array_category;
    private ArrayList array_tags;
    private ArrayList array_byngo;
    private ProgressDialog pDialog;
    private List<HashMap<String, String>> category_list;
    private List<HashMap<String, String>> tags_list;
    private List<HashMap<String, String>> byngo_list;
    private List<HashMap<String, String>> country_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        spinner_country = (SearchableSpinner) findViewById(R.id.spinner_country);
        spinner_tag = (SearchableSpinner) findViewById(R.id.spinner_tag);
        spinner_byNGO = (SearchableSpinner) findViewById(R.id.spinner_byNGO);
        spinner_category = (SearchableSpinner) findViewById(R.id.spinner_category);
        Button btn_done = findViewById(R.id.btn_done);

        pDialog = new ProgressDialog(FilterActivity.this);
        pDialog.setCancelable(false);
        category_list = new ArrayList<>();
        byngo_list = new ArrayList<>();
        tags_list = new ArrayList<>();
        country_list = new ArrayList<>();
        array_country = new ArrayList();
        array_category = new ArrayList();
        array_tags = new ArrayList();
        array_byngo = new ArrayList();
        getFilterData();


        spinner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Constant.Country_ID = country_list.get(i).get("id");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("enter in nothing select country");

                Constant.Country_ID = "";

            }
        });
        spinner_byNGO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Constant.NGO_ID = byngo_list.get(i).get("id");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("enter in nothing select ngo");
                Constant.NGO_ID = "";
            }
        });
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Constant.Category_ID = category_list.get(i).get("id");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("enter in nothing select category");
                Constant.Category_ID = "";
            }
        });
        spinner_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Constant.Tags_ID = tags_list.get(i).get("id");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("enter in nothing select tag");
                Constant.Tags_ID = "";
            }
        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(FilterActivity.this, MainActivity.class);
                Constant.Search_Fragment_Flag = true;
                startActivity(i);
                finish();
            }
        });


    }

    private void getFilterData() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Getting Data ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Constant.FilterUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);


                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {
                        JSONObject data = jObj.getJSONObject("data");

                        JSONArray categories = data.getJSONArray("categories");
                        JSONArray tags = data.getJSONArray("tags");
                        JSONArray coutries = data.getJSONArray("coutries");
                        JSONArray ngo_names = data.getJSONArray("ngo_names");


                        for (int i = 0; i < categories.length(); i++) {

                            JSONObject d = categories.getJSONObject(i);

                            String id = d.getString("id");
                            String category_name = d.getString("category_name");

                            HashMap<String, String> map_project = new HashMap<>();
                            map_project.put("id", id);
                            map_project.put("category_name", category_name);

                            category_list.add(map_project);
                            array_category.add(category_name);
                        }

                        for (int i = 0; i < tags.length(); i++) {

                            JSONObject d = tags.getJSONObject(i);

                            String id = d.getString("id");
                            String tag_name = d.getString("tag_name");

                            HashMap<String, String> map_project = new HashMap<>();
                            map_project.put("id", id);
                            map_project.put("tag_name", tag_name);

                            tags_list.add(map_project);
                            array_tags.add(tag_name);

                        }
                        for (int i = 0; i < coutries.length(); i++) {

                            JSONObject d = coutries.getJSONObject(i);

                            String id = d.getString("id");
                            String country = d.getString("name");

                            HashMap<String, String> map_project = new HashMap<>();
                            map_project.put("id", id);
                            map_project.put("country", country);

                            country_list.add(map_project);
                            array_country.add(country);

                        }
                        for (int i = 0; i < ngo_names.length(); i++) {

                            JSONObject d = ngo_names.getJSONObject(i);

                            String id = d.getString("id");
                            String ngo_name = d.getString("ngo_name");

                            HashMap<String, String> map_project = new HashMap<>();
                            map_project.put("id", id);
                            map_project.put("ngo_name", ngo_name);

                            byngo_list.add(map_project);
                            array_byngo.add(ngo_name);
                        }

                        ArrayAdapter adar_country = new ArrayAdapter(FilterActivity.this, R.layout.spiner_item, array_country);
                        ArrayAdapter adar_tag = new ArrayAdapter(FilterActivity.this, R.layout.spiner_item, array_tags);
                        ArrayAdapter adar_category = new ArrayAdapter(FilterActivity.this, R.layout.spiner_item, array_category);
                        ArrayAdapter adar_byngo = new ArrayAdapter(FilterActivity.this, R.layout.spiner_item, array_byngo);

                        spinner_country.setAdapter(adar_country);
                        spinner_category.setAdapter(adar_category);
                        spinner_byNGO.setAdapter(adar_byngo);
                        spinner_tag.setAdapter(adar_tag);
                    } else {

                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(FilterActivity.this,
                                errorMsg4, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                return new HashMap<>();

            }


        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
