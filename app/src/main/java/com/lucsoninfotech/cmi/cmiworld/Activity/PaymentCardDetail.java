package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentCardDetail extends AppCompatActivity {


    String month;
    String year;
    private EditText spn_month;
    private EditText spn_year;
    private EditText edt_card_no;
    private String cardnourl;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_card_detail);

       /* spn_month = (EditText) findViewById(R.id.spn_month);
        spn_year = (EditText) findViewById(R.id.spn_year);
        edt_card_no = (EditText) findViewById(R.id.edt_cardno);*/
        CreditCardView creditCardView = new CreditCardView(this);


        Button done = findViewById(R.id.btn_done);
        ArrayList arr_month = new ArrayList();
        ArrayList arr_year = new ArrayList();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if (Constant.Payment_flag) {
            Bundle b = getIntent().getExtras();

            creditCardView.setCardNumber("123456789");
            creditCardView.setCardExpiry(b.getString("month") + "/" + b.getString("year"));
            System.out.println("cardd no" + b.getString("card_no"));

         /*   edt_card_no.setText(b.getString("card_no"));
            spn_month.setText(b.getString("month"));
            spn_year.setText(b.getString("year"));*/
            Constant.Payment_flag = false;

            cardnourl = Constant.UpdateUserCard + b.getString("id");
        } else {
            cardnourl = Constant.AddUserCard;
        }

       /* for (int i = 1; i < 10; i++) {

            arr_month.add("0" + i);
            arr_year.add("200" + i);
        }
        arr_month.add("10");
        arr_month.add("11");
        arr_month.add("12");

        for (int i = 10; i < 51; i++) {
            arr_year.add("20" + i);
        }
*/

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddCard();

            }
        });

    }

    private void AddCard() {
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

                        Intent i = new Intent(PaymentCardDetail.this, PaymentDetailActivity.class);
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
                params.put("card_number", edt_card_no.getText().toString().trim());
                params.put("expiry_month", spn_month.getText().toString());
                params.put("expiry_year", spn_year.getText().toString());

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
