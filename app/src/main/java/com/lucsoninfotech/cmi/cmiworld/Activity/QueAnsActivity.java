package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.Fragment.QueAnsFragment;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueAnsActivity extends AppCompatActivity implements QueAnsFragment.OnGetFromUserClickListener {

    private ProgressDialog pDialog;
    private ViewPager viewpager;
    private QueAnsAdapter queAnsAdapter;
    private ArrayList<QueAnsFragment> fragment;
    private Button btn_next;
    private Button btn_previous;
    private Button btn_submit;
    private QueAnsFragment frag1;
    private ArrayList answer;
    private String[] ans;
    HashMap<String, String> user_detail = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_que_ans);

        viewpager = findViewById(R.id.view_question);
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        btn_submit = findViewById(R.id.btn_submit);

        //user_detail = new SQLiteHandler(QueAnsActivity.this).getUserDetails();
        //Constant.USER_ID = user_detail.get("id");
        //Constant.USER_TYPE = user_detail.get("user_type");
        System.out.println("user type from database::::::" + Constant.USER_ID);

        pDialog = new ProgressDialog(QueAnsActivity.this);
        pDialog.setCancelable(false);
        fragment = new ArrayList();
        answer = new ArrayList();
        ans = new String[5];

        for (int i = 0; i < 5; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("id", i);
            frag1 = new QueAnsFragment();
            frag1.setArguments(bundle);
            fragment.add(frag1);
            answer.add("");
        }
        queAnsAdapter = new QueAnsAdapter(getSupportFragmentManager(), fragment);

        viewpager.setAdapter(queAnsAdapter);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.set(viewpager.getCurrentItem(), fragment.get(viewpager.getCurrentItem()).getFromUser());
                System.out.println("array" + answer);
                if (viewpager.getCurrentItem() == 4) {
                    setQueAns();
                }
                viewpager.setCurrentItem(getItem(+1));
            }
        });


        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(getItem(-1));
            }
        });

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position != 0) {
                    if (position == 4) {
                        btn_next.setText("Submit");
                    } else {
                        btn_next.setText("Next");
                    }
                    btn_previous.setVisibility(View.VISIBLE);
                } else {
                    btn_previous.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private int getItem(int i) {
        return viewpager.getCurrentItem() + i;
    }

    @Override
    public String getFromUser(String message) {

        return message;
    }

    private void setQueAns() {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait");
        pDialog.setTitle("Processing");
        showDialog();
        Map<String, String> params = new HashMap<>();

        params.put("user_id", Constant.USER_ID);
        params.put("a1", String.valueOf(answer.get(0)));
        params.put("a2", String.valueOf(answer.get(1)));
        params.put("a3", String.valueOf(answer.get(2)));
        params.put("a4", String.valueOf(answer.get(3)));
        params.put("a5", String.valueOf(answer.get(4)));
        Log.e("post param = > ", "" + params.toString());

        StringRequest strReq = new StringRequest(Request.Method.POST, Constant.Question_Url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("post Response ans = > ", "" + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");
                    // Check for error node in json
                    if (error == 0) {
                        Toast.makeText(QueAnsActivity.this, "Answer Submitted Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(QueAnsActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(QueAnsActivity.this, errorMsg4, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(QueAnsActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(QueAnsActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("user_id", Constant.USER_ID);
                params.put("a1", String.valueOf(answer.get(0)));
                params.put("a2", String.valueOf(answer.get(1)));
                params.put("a3", String.valueOf(answer.get(2)));
                params.put("a4", String.valueOf(answer.get(3)));
                params.put("a5", String.valueOf(answer.get(4)));
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

    private class QueAnsAdapter extends FragmentStatePagerAdapter {

        final ArrayList<QueAnsFragment> fragment;

        QueAnsAdapter(FragmentManager fm, ArrayList<QueAnsFragment> fragment) {
            super(fm);
            this.fragment = fragment;
        }

        @Override
        public Fragment getItem(int position) {
            return fragment.get(position);
        }

        @Override
        public int getCount() {
            return fragment.size();
        }
    }
}
