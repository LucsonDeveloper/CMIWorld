package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CMIDonate extends AppCompatActivity {

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String TAG = CMIDonate.class.getSimpleName();
    // PayPal configuration
    private static final PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Constant.PAYPAL_ENVIRONMENT).clientId(
                    Constant.PAYPAL_CLIENT_ID);
    private ProgressDialog pDialog;
    private String project_id;
    private String pass_ammount;
    private String pass_note;
    private EditText edt_note;
    private EditText edt_amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmidonate);

        Button btn_checkout = findViewById(R.id.btn_checkout);
        edt_note = findViewById(R.id.edt_usernote);
        edt_amount = findViewById(R.id.edt_amount);
        Spinner spn_currency = findViewById(R.id.spn_currency);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        SQLiteHandler db = new SQLiteHandler(this);
        HashMap<String, String> user_detail = db.getUserDetails();
        Log.e("UserID", "" + user_detail.get("id"));
        Constant.USER_ID = user_detail.get("id");

        Bundle b = getIntent().getExtras();

        ArrayList arrayList = new ArrayList();
        arrayList.add("USD");
        ArrayAdapter adar = new ArrayAdapter(CMIDonate.this, android.R.layout.simple_list_item_1, arrayList);
        spn_currency.setAdapter(adar);

        if (b != null) {
            project_id = b.getString("project_id");
        } else {
            project_id = "1";
        }

        System.out.println("project_id in CMI donate ::::: " + project_id);


        // Starting PayPal service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);


        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pass_ammount = edt_amount.getText().toString().trim();
                pass_note = edt_note.getText().toString().trim();


                System.out.println("passs ::::::" + pass_ammount + pass_note);

                if (TextUtils.isEmpty(pass_note)) {
                    Toast.makeText(CMIDonate.this, "Please Enter Your Donation Note", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(pass_ammount)) {
                    Toast.makeText(CMIDonate.this, "Please Enter Your Donation Ammount", Toast.LENGTH_SHORT).show();
                } else {
                    launchPayPalPayment();
                }

            }
        });


    }

    private void launchPayPalPayment() {

        PayPalPayment thingsToBuy = new PayPalPayment(new BigDecimal(pass_ammount), "USD",
                pass_note, PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(CMIDonate.this,
                PaymentActivity.class);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);

        /*= prepareFinalCart();

        Intent intent = new Intent(CMIDonate.this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);*/
    }

    private PayPalPayment prepareFinalCart() {

        // Total amount
        BigDecimal subtotal = BigDecimal.valueOf(500);


        PayPalPayment payment = new PayPalPayment(
                subtotal,
                Constant.DEFAULT_CURRENCY,
                "Hello this is description about product",
                Constant.PAYMENT_INTENT);


        // Custom field like invoice_number etc.,
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    PaymentConfirmation confirm = data
                            .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirm != null) {
                        try {
                            Log.e(TAG, confirm.toJSONObject().toString(4));
                            Log.e(TAG, confirm.getPayment().toJSONObject()
                                    .toString(4));

                            String paymentId = confirm.toJSONObject()
                                    .getJSONObject("response").getString("id");

                            JSONObject payment_client = confirm.getPayment()
                                    .toJSONObject();


                            String ammount = payment_client.getString("amount");
                            String currency_code = payment_client.getString("currency_code");
                            String description = payment_client.getString("short_description");


                            Log.e(TAG, "paymentId: " + paymentId
                                    + ", payment_json: " + payment_client);

                            verifyPaymentOnServer(paymentId, ammount, currency_code, description);

                        } catch (JSONException e) {
                            Log.e(TAG, "an extremely unlikely failure occurred: ",
                                    e);
                        }
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e(TAG, "The user canceled.");
                    break;
                case PaymentActivity.RESULT_EXTRAS_INVALID:
                    Log.e(TAG,
                            "An invalid Payment or PayPalConfiguration was submitted.");
                    break;
            }
        }
    }

    private void verifyPaymentOnServer(final String payment_id, final String ammount, String currency_code, final String description) {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait");
        pDialog.setTitle("Please Wait");
        showDialog();


        StringRequest strReq = new StringRequest(Request.Method.POST, Constant.VerifyPayment_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        Toast.makeText(CMIDonate.this, "Donation Submitted Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(CMIDonate.this,
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
                params.put("project_id", project_id);
                params.put("donation_amount", ammount);
                params.put("user_comment", description);
                params.put("transaction_id", payment_id);
                params.put("transaction_number", "");
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
