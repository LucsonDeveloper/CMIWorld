package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ApplyforSEMActivity extends AppCompatActivity {

    private ArrayList array_country;
    private String country_id;
    private EditText edt_ngo_name;
    private EditText edt_registration_id;
    private EditText edt_ngo_working_area;
    private EditText edt_location;
    private Button btn_next;
    private TextView txt_startting_date;
    private Spinner spipnner_country;
    private LinearLayout linear_date;
    private Calendar myCalendar;
    private List<HashMap<String, String>> country_list;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyfor_sem);

        btn_next = findViewById(R.id.btn_next);
        edt_ngo_name = findViewById(R.id.edt_ngo_name);
        edt_registration_id = findViewById(R.id.edt_registration_id);
        txt_startting_date = findViewById(R.id.txt_startting_date);
        edt_ngo_working_area = findViewById(R.id.edt_ngo_working_area);
        edt_location = findViewById(R.id.edt_location);
        linear_date = findViewById(R.id.linear_date);
        spipnner_country = findViewById(R.id.spinner_country);
        pDialog = new ProgressDialog(ApplyforSEMActivity.this);
        pDialog.setCancelable(false);
        array_country = new ArrayList();
        List<HashMap<String, String>> category_list = new ArrayList<>();
        country_list = new ArrayList<>();


        getCategory();

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };


        spipnner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                country_id = country_list.get(i).get("id");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                country_id = country_list.get(0).get("id");

            }
        });


        linear_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new DatePickerDialog(ApplyforSEMActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        btn_next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

               /* if(edt_ngo_name.getText().toString().equals("") || edt_startting_date.getText().toString().equals("") || edt_registration_id.getText().toString().equals("") || edt_ngo_working_area.getText().toString().equals("") )
                {
                    Toast.makeText(ApplyforSEMActivity.this, "Please fill all detail", Toast.LENGTH_SHORT).show();
                }else {*/


                        Intent i = new Intent(ApplyforSEMActivity.this, ApplyforSEMActivity2.class);
                        i.putExtra("country_id", country_id);
                        i.putExtra("ngo_name", edt_ngo_name.getText().toString().trim());
                        i.putExtra("reg_id", edt_registration_id.getText().toString().trim());
                        i.putExtra("starting_date", txt_startting_date.getText().toString().trim());
                        i.putExtra("ngo_working_area", edt_ngo_working_area.getText().toString().trim());
                        i.putExtra("location", edt_location.getText().toString());
                        startActivity(i);


                    }

                });


    }

    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txt_startting_date.setText(sdf.format(myCalendar.getTime()));
    }


    private void getCategory() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Getting Data ...");
        showDialog();
        System.out.println("login url" + Constant.FilterUrl);

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
                        JSONArray coutries = data.getJSONArray("coutries");


                        for (int i = 0; i < categories.length(); i++) {

                            JSONObject d = categories.getJSONObject(i);

                            String id = d.getString("id");
                            String category_name = d.getString("category_name");

                            HashMap<String, String> map_project = new HashMap<>();
                            map_project.put("id", id);
                            map_project.put("category_name", category_name);

                            Constant.category_list.add(map_project);
                            Constant.array_category.add(category_name);
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

                        ArrayAdapter adar_country = new ArrayAdapter(ApplyforSEMActivity.this, R.layout.spiner_item, array_country);
                        spipnner_country.setAdapter(adar_country);


                    } else {

                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(ApplyforSEMActivity.this,
                                errorMsg4, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(ApplyforSEMActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                    Toast.makeText(ApplyforSEMActivity.this, "Network Error Occured", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(ApplyforSEMActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ApplyforSEMActivity.this, "Parsing Error Occured,Contact Developer", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ApplyforSEMActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
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