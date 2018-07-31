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

public class ForgotPassword extends AppCompatActivity {

    private EditText edt_tocken;
    private EditText edt_password;
    private EditText edt_conformpassword;
    private String url;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        edt_tocken = findViewById(R.id.edt_tocken);
        edt_password = findViewById(R.id.edt_password);
        edt_conformpassword = findViewById(R.id.edt_conformpassword);
        Button btn_ok = findViewById(R.id.btn_done);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String Otp = edt_tocken.getText().toString().trim();
                String Password = edt_password.getText().toString().trim();
                String CPassword = edt_conformpassword.getText().toString().trim();

                Bundle b = getIntent().getExtras();
                String email = b.getString("email");
                url = Constant.Forgot_Password_Step2 + email + "&newpassword=" + Password
                        + "&c_password=" + CPassword +
                        "&otp_code=" + Otp;
                if (Constant.isOnline(getApplicationContext())) {
                    if (Otp.equals("") || Otp.length() != 6) {
                        Toast.makeText(ForgotPassword.this, "Please enter valid OTP code.", Toast.LENGTH_SHORT).show();
                    } else
                        ChangePassword();
                } else {
                    Toast.makeText(ForgotPassword.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ChangePassword() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage(" Please Wait  ...");
        showDialog();
        System.out.println("url in forgot password" + url);

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");
                    if (error == 0) {

                        Toast.makeText(ForgotPassword.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ForgotPassword.this, MainActivity.class);
                        startActivity(i);
                        finish();

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
