package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edt_oldpassword;
    private EditText edt_newpassword;
    private EditText edt_confpassword;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edt_confpassword = findViewById(R.id.edt_confpassword);
        edt_newpassword = findViewById(R.id.edt_newpassword);
        edt_oldpassword = findViewById(R.id.edt_oldpassword);
        Button done = findViewById(R.id.btn_done);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        SQLiteHandler db = new SQLiteHandler(this);
        HashMap<String, String> user_detail = db.getUserDetails();
        Log.e("UserID", "" + user_detail.get("id"));
        Constant.USER_ID = user_detail.get("id");
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isOnline(getApplicationContext())) {
                    ChangePassword();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void ChangePassword() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage(" Please Wait  ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Constant.Change_Password, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        Toast.makeText(ChangePasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ChangePasswordActivity.this, MainActivity.class);
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("user_id", Constant.USER_ID);
                params.put("c_password", edt_oldpassword.getText().toString().trim());
                params.put("n_password", edt_newpassword.getText().toString().trim());
                params.put("r_password", edt_confpassword.getText().toString().trim());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
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

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
