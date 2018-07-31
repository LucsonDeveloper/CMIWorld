package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.lucsoninfotech.cmi.cmiworld.Adapter.PaymentAdapter;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentDetailActivity extends AppCompatActivity {

    private final int GET_NEW_CARD = 2;
    private final int EDIT_CARD = 5;
    private ListView lv_card_detail;
    private String cardnourl;
    private String cardNumber;
    private String addcardurl;
    private String card_id;
    private PaymentAdapter paymentadapter;
    private List<HashMap<String, String>> project_list;
    private String[] seperate;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);


        lv_card_detail = findViewById(R.id.lv_card_Detail);
        Button btn_add = findViewById(R.id.add_more);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        project_list = new ArrayList<>();
        SQLiteHandler db = new SQLiteHandler(this);
        HashMap<String, String> user_detail = db.getUserDetails();
        Constant.USER_ID = user_detail.get("id");
        cardnourl = Constant.UserCardList + Constant.USER_ID;
        getCardNO();

        lv_card_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               /* Intent intent = new Intent(PaymentDetailActivity.this, PaymentCardDetail.class);
                intent.putExtra("id", project_list.get(i).get("id"));
                intent.putExtra("card_no", project_list.get(i).get("card_number"));
                intent.putExtra("month", project_list.get(i).get("expiry_month"));
                intent.putExtra("year", project_list.get(i).get("expiry_year"));
                startActivity(intent);*/

                Intent intent = new Intent(PaymentDetailActivity.this, CardEditActivity.class);

                intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, project_list.get(i).get("card_number"));
                intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, project_list.get(i).get("expiry_month") + "/" + project_list.get(i).get("expiry_year"));
                intent.putExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_BACK);
                intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, false); // pass "false" to discard expiry date validation.
                card_id = project_list.get(i).get("id");

                startActivityForResult(intent, EDIT_CARD);


            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PaymentDetailActivity.this, CardEditActivity.class);
                startActivityForResult(intent, GET_NEW_CARD);

            }
        });

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

    private void getCardNO() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage(" Please Wait  ...");
        pDialog.setTitle("Processing");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, cardnourl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        JSONArray data = jObj.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {

                            JSONObject d = data.getJSONObject(i);

                            String id = d.getString("id");
                            String user_id = d.getString("user_id");
                            String card_number = d.getString("card_number");
                            String expiry_month = d.getString("expiry_month");
                            String expiry_year = d.getString("expiry_year");

                            HashMap<String, String> map_project = new HashMap<>();

                            map_project.put("id", id);
                            map_project.put("user_id", user_id);
                            map_project.put("card_number", card_number);
                            map_project.put("expiry_month", expiry_month);
                            map_project.put("expiry_year", expiry_year);

                            project_list.add(map_project);
                        }
                        paymentadapter = new PaymentAdapter(PaymentDetailActivity.this, project_list);
                        if (paymentadapter != null) {
                            lv_card_detail.setAdapter(paymentadapter);
                        }
                    } else {
                        String errorMsg4 = jObj.getString("error_string");


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

    public void onActivityResult(int reqCode, int resultCode, Intent data) {


        String expiry;
        if (reqCode == EDIT_CARD) {
            if (resultCode == RESULT_OK) {
                addcardurl = Constant.UpdateUserCard + card_id;
                cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                seperate = expiry.split("/");
                AddCard();

            }
        } else if (reqCode == GET_NEW_CARD) {
            if (resultCode == RESULT_OK) {

                addcardurl = Constant.AddUserCard;


                cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                seperate = expiry.split("/");
                AddCard();
            }
        }
    }

    private void AddCard() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage(" Please Wait  ...");
        pDialog.setTitle("Processing");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, addcardurl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        Intent i = new Intent(PaymentDetailActivity.this, PaymentDetailActivity.class);
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
                params.put("card_number", cardNumber);
                params.put("expiry_month", seperate[0]);
                params.put("expiry_year", seperate[1]);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
