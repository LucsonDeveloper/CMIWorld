package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class FAQActivity extends AppCompatActivity {


    private TextView txt_faq;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);


        txt_faq = findViewById(R.id.txt_faq);
        progressBar = findViewById(R.id.progressBar);

        if (Constant.isOnline(getApplicationContext())) {
            getFAQ();
        } else {
            Toast.makeText(FAQActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void getFAQ() {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";


        StringRequest strReq = new StringRequest(Request.Method.POST, Constant.FaqUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);


                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {


                        JSONObject data = jObj.getJSONObject("data");

                        String description = data.getString("description");

                        txt_faq.setText(description);


                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(FAQActivity.this,
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
                progressBar.setVisibility(View.GONE);

            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
