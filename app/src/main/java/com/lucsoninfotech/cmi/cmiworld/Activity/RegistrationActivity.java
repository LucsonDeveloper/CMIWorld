package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class RegistrationActivity extends AppCompatActivity {

    private Button registration;
    private TextView btn_sign_in;

    private EditText fullname;
    private EditText email;
    private EditText mobile_no;
    private EditText password;
    private EditText confirm_password;
    private String name;
    private String mail;
    private String mob;
    private String pass;
    private String con_pass;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        mobile_no = findViewById(R.id.mobile_number);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.conform_passwoprd);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        registration = findViewById(R.id.btn_register);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btn_sign_in.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        registration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                name = fullname.getText().toString().trim();
                mail = email.getText().toString().trim();
                mob = mobile_no.getText().toString().trim();
                pass = password.getText().toString().trim();
                con_pass = confirm_password.getText().toString().trim();

                if (name.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter valid user name.", Toast.LENGTH_LONG).show();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    Toast.makeText(getApplicationContext(), "Please enter valid email id.", Toast.LENGTH_LONG).show();

                } else if (!Patterns.PHONE.matcher(mob).matches()) {
                    Toast.makeText(getApplicationContext(), "Please enter valid user name.", Toast.LENGTH_LONG).show();

                } else if (pass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter valid password.", Toast.LENGTH_LONG).show();

                } else if (!pass.equals(con_pass)) {
                    Toast.makeText(getApplicationContext(), "Password doesn't match.", Toast.LENGTH_LONG).show();

                } else {


                    if (Constant.isOnline(getApplicationContext())) {
                        sendPostRequest(name, mail, mob, pass, con_pass);
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /*  public void Registration() {
          // Tag used to cancel the request
          String tag_string_req = "req_login";
          pDialog.setMessage("Please Wait  ...");
          showDialog();
          System.out.println("url in login" + url + "name=" + name + "&email=" + mail + "&mobile=" + mob + "&password=" + pass + "&re_password=" + con_pass);
          StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

              @Override
              public void onResponse(String response) {
                  Log.e("response=>", response);
                  hideDialog();
                  try {
                      JSONObject jObj = new JSONObject(response);
                      int error = jObj.getInt("error_code");
                      // Check for error node in json
                      if (error == 0) {
                          JSONObject data = jObj.getJSONObject("data");
                          id = data.getString("id");
                          Intent i = new Intent(RegistrationActivity.this, OTPActivity.class);
                          i.putExtra("mail", mail);
                          startActivity(i);
                          finish();
                          Toast.makeText(RegistrationActivity.this, "Mail sent to " + mail, Toast.LENGTH_SHORT).show();
                      } else {
                          String errorMsg4 = jObj.getString("error_string");
                          Toast.makeText(getApplicationContext(),
                                  errorMsg4, Toast.LENGTH_LONG).show();

                      }
                  } catch (JSONException e) {
                      // JSON error
                      e.printStackTrace();
                      Toast.makeText(RegistrationActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                  }
              }
          }, new Response.ErrorListener() {

              @Override
              public void onErrorResponse(VolleyError error) {

                  Toast.makeText(RegistrationActivity.this,
                          error.getMessage(), Toast.LENGTH_LONG).show();
                  hideDialog();
              }
          }) {
              @Override
              protected Map<String, String> getParams() {
                  // Posting parameters to login url
                  Map<String, String> params = new HashMap<String, String>();
                  params.put("name", name);
                  params.put("email", mail);
                  params.put("mobile", mob);
                  params.put("password", pass);
                  params.put("re_password", con_pass);
                  Set<String> keys = params.keySet();  //get all keys
                  for (String i : keys) {
                      System.out.println("CheckParam=>" + params.get(i));
                  }
                  return params;
              }
          };
          // Adding request to request queue
          AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
      }
  */
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void sendPostRequest(String... strings) {
        pDialog.setMessage("Please Wait  ...");
        showDialog();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... str) {
                String str0 = str[0];
                String str1 = str[1];
                String str2 = str[2];
                String str3 = str[3];
                String str4 = str[4];
                String json = "";
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost httpPost = new HttpPost(Constant.SIGNUP_URL);// replace with your url
                //httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
                List<NameValuePair> nameValuePairList = new ArrayList<>();

                nameValuePairList.add(new BasicNameValuePair("name", str0));
                nameValuePairList.add(new BasicNameValuePair("email", str1));
                nameValuePairList.add(new BasicNameValuePair("mobile", str2));
                nameValuePairList.add(new BasicNameValuePair("password", str3));
                nameValuePairList.add(new BasicNameValuePair("re_password", str4));
                // You can add more parameters like above
                try {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
                            nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    try {
                        HttpResponse httpResponse = httpClient
                                .execute(httpPost);
                        InputStream inputStream = httpResponse.getEntity()
                                .getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(
                                inputStream);
                        BufferedReader bufferedReader = new BufferedReader(
                                inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String bufferedStrChunk;
                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }
                        System.out.println("Responese :" + stringBuilder.toString());
                        json = stringBuilder.toString();
                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("First Exception coz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Second Exception coz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }
                } catch (Exception uee) {
                    System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }
                return json;
            }

            @Override
            protected void onPostExecute(String response) {
                Log.e("response=>", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");
                    // Check for error node in json
                    if (error == 0) {
                        Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(getApplicationContext(),
                                errorMsg4, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strings);
    }


}
