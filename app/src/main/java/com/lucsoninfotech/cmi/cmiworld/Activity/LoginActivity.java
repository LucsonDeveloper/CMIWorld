package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.helper.SessionManager;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {


    private ProgressDialog pDialog;
    private EditText email;
    private EditText password;
    private String mail;
    private String pass;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        TextView tv_forgotpassword = findViewById(R.id.forgotpassword);
        Button registration = findViewById(R.id.registration);
        Button btn_login = findViewById(R.id.email_sign_in_button);


        tv_forgotpassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, PreForgotPassword.class);
                startActivity(i);
            }
        });

        registration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mail = email.getText().toString().trim();
                pass = password.getText().toString().trim();

                System.out.println(Patterns.EMAIL_ADDRESS.matcher(mail).matches());
                if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    Toast.makeText(LoginActivity.this, "Please enter valid email address", Toast.LENGTH_LONG).show();
                } else if (pass.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter valid password", Toast.LENGTH_LONG).show();
                } else {

                    if (Constant.isOnline(getApplicationContext())) {
                        Login();
                    } else {
                        Toast.makeText(LoginActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }


    private void Login() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Constant.LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();
                Log.d("Resp", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");
                    String errorMsg4 = jObj.getString("error_string");
                    // Check for error node in json
                    switch (error) {
                        case 0: {
                            JSONArray data = jObj.getJSONArray("data");
                            session.setLogin(true);
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject c = data.getJSONObject(i);

                                String id = c.getString("id");
                                String sname = c.getString("name");
                                String semail = c.getString("email");
                                String smobile = c.getString("mobile");
                                String spassword = c.getString("password");
                                String user_role = c.getString("user_role");

                                System.out.println("user_type :::" + user_role);
                                Constant.USER_TYPE = user_role;
                                Constant.USER_ID = id;
                                db.addUser(id, sname, semail, spassword, smobile, user_role);
                            }
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                            break;
                        }
                        case 6: {

                            Toast.makeText(getApplicationContext(), errorMsg4, Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this, OTPActivity.class);
                            i.putExtra("mail", mail);
                            startActivity(i);
                            finish();

                            break;
                        }
                        case 9: {

                            JSONObject data = new JSONObject(jObj.getString("data"));
                            Constant.USER_ID = data.getString("id");
                            Toast.makeText(getApplicationContext(), errorMsg4, Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this, QueAnsActivity.class);
                            startActivity(i);
                            finish();

                            break;
                        }
                        default: {

                            Toast.makeText(getApplicationContext(),
                                    errorMsg4, Toast.LENGTH_LONG).show();

                            break;
                        }
                    }
                } catch (JSONException e) {
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
                params.put("user_name", mail);
                params.put("password", pass);
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

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
