package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class PreForgotPassword extends AppCompatActivity {

    private EditText edt_mail;
    private String url;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_forgot_password);


        edt_mail = findViewById(R.id.edt_mail);
        Button btn_next = findViewById(R.id.btn_next);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btn_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = edt_mail.getText().toString().trim();
                url = Constant.Forgot_Password_Step1 + Email;
                if (Constant.isOnline(getApplicationContext())) {
                    if (Email.equals("")) {
                        Toast.makeText(PreForgotPassword.this, "Please enter valid Email Id.", Toast.LENGTH_SHORT).show();
                    } else
                        sentmail();
                } else {
                    Toast.makeText(PreForgotPassword.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void sentmail() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait  ...");
        showDialog();
        System.out.println("url in sent mail" + url);

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        Toast.makeText(PreForgotPassword.this, "OTP sent to " + edt_mail.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(PreForgotPassword.this, ForgotPassword.class);
                        i.putExtra("email", edt_mail.getText().toString().trim());
                        startActivity(i);


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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
