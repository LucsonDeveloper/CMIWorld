package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

public class OTPActivity extends AppCompatActivity {


    private EditText edt_otp;
    private String otp;
    private String verifyurl;
    private String id;
    private String otpurl;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        edt_otp = findViewById(R.id.edt_mail);
        Button btn_done = findViewById(R.id.btn_done);
        TextView resend_otp = findViewById(R.id.resend_otp);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Bundle b = getIntent().getExtras();
        id = b.getString("mail");

        resend_otp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                otpurl = Constant.VerifyOTP_Url + id + "&send_email=1";
                if (Constant.isOnline(getApplicationContext())) {
                    Resend_otp();
                } else {
                    Toast.makeText(OTPActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btn_done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                otp = edt_otp.getText().toString().trim();

                if (otp.equals("") || otp.length() != 6) {
                    Toast.makeText(OTPActivity.this, "Please enter valid OTP code.", Toast.LENGTH_SHORT).show();
                } else {
                    verifyurl = Constant.VerifyOTP_Url + id + "&verif_code=" + otp;
                    if (Constant.isOnline(getApplicationContext())) {
                        verifyAccount();
                    } else {
                        Toast.makeText(OTPActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }


    private void verifyAccount() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait  ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, verifyurl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {
                        Toast.makeText(OTPActivity.this, "Account has been successfully verified", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(OTPActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();

                        /*Intent i = new Intent(OTPActivity.this,LoginActivity.class);
                        startActivity(i);
                        finish();*/
                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(getApplicationContext(),
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
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void Resend_otp() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Resending OTP  ...");
        showDialog();
        System.out.println("url in login" + verifyurl);

        StringRequest strReq = new StringRequest(Request.Method.POST, otpurl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        Toast.makeText(OTPActivity.this, "Mail sent Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(getApplicationContext(),
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
