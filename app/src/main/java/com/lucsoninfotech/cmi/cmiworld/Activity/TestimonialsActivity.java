package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.Adapter.Project_detail_Adapter;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestimonialsActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private RecyclerView rv_testimonial;
    private TextView user_name;
    private Button btn_post;
    private EditText edt_user_post;
    private ImageView user_dp;
    private List<HashMap<String, String>> List_userdata;
    private Project_detail_Adapter projectdetailAdapter;
    private String post_data;
    private String testimonial_url;
    private String post_url;
    private LinearLayout linear_postcomment;
    private HashMap<String, String> user_detail = new HashMap<>();
    private List<HashMap<String, String>> testimonials_list;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonials);

        rv_testimonial = findViewById(R.id.rv_testimonials);
        user_dp = findViewById(R.id.user_dp);
        user_name = findViewById(R.id.user_name);
        edt_user_post = findViewById(R.id.edt_user_post);
        btn_post = findViewById(R.id.btn_post);
        linear_postcomment = findViewById(R.id.linear_postcomment);
        List_userdata = new ArrayList<>();
        pDialog = new ProgressDialog(TestimonialsActivity.this);
        pDialog.setCancelable(false);
        testimonials_list = new ArrayList<>();
        SQLiteHandler db = new SQLiteHandler(this);
        user_detail = db.getUserDetails();
        Constant.USER_ID = user_detail.get("id");
        Constant.USER_TYPE = user_detail.get("user_type");
        if (Constant.USER_TYPE.equals("2")) {
            Bundle b = getIntent().getExtras();
            linear_postcomment.setVisibility(View.VISIBLE);
            testimonial_url = Constant.TestimonialsVIew_URL
                    + "&sem_id=" + b.getString("sem_id") +
                    "&user_id=1" + "&page=1";
        } else {
            testimonial_url = Constant.TestimonialsVIew_URL
                    + "&sem_id=" + b.getString("sem_id") + "&user_id=1" + "&page=1";

        }
        if (Constant.isOnline(getApplicationContext())) {
            Picasso.get().load(Constant.USER_IMAGE).placeholder(R.drawable.bg_timeline).into(user_dp);
            user_name.setText(Constant.USER_NAME);


            getTestimonials();
        } else {
            Toast.makeText(TestimonialsActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }


        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.USER_TYPE.equals("2")) {
                    b = getIntent().getExtras();
                    post_url = Constant.Testimonials_URL + b.getString("sem_id")
                            + "&user_id=" + Constant.USER_ID + "&project_id=" + b.getString("project_id");
                } else {
                    b = getIntent().getExtras();
                    post_url = Constant.Testimonials_URL + b.getString("sem_id")
                            + "&user_id=" + Constant.USER_ID + "&project_id=" + b.getString("project_id");

                }
                if (Constant.isOnline(getApplicationContext())) {
                    PostComment();
                } else {
                    Toast.makeText(TestimonialsActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void PostComment() {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait");
        pDialog.setTitle("Please Wait");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, post_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("responce", response);
                Log.d("responce", post_url);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {


                        post_data = edt_user_post.getText().toString().trim();
                        HashMap<String, String> abc = new HashMap<>();


                        abc.put("time", "just now");
                        abc.put("name", Constant.USER_NAME);
                        abc.put("profile_picture", Constant.USER_IMAGE);
                        abc.put("sem_comment", post_data);
                        testimonials_list.add(0, abc);
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(TestimonialsActivity.this, R.anim.fadein);


                        projectdetailAdapter = new Project_detail_Adapter(TestimonialsActivity.this, testimonials_list);
                        LinearLayoutManager llm = new LinearLayoutManager(TestimonialsActivity.this);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        rv_testimonial.setLayoutManager(llm);
                        rv_testimonial.setAdapter(projectdetailAdapter);
                        rv_testimonial.startAnimation(fadeInAnimation);


                        edt_user_post.setText("");


                    } else {

                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(TestimonialsActivity.this,
                                errorMsg4, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(TestimonialsActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(TestimonialsActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("sem_comment", edt_user_post.getText().toString().trim());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getTestimonials() {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait");
        pDialog.setTitle("Please Wait");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, testimonial_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("responce", response);
                Log.d("responce", testimonial_url);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        JSONArray data = jObj.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject c = data.getJSONObject(i);

                            String id = c.getString("id");
                            String project_id = c.getString("project_id");
                            String sem_comment = c.getString("sem_comment");
                            String time = c.getString("added_datetime");
                            String name = c.getString("name");
                            String profile_picture = c.getString("profile_picture");
                            String project_name = c.getString("project_name");


                            HashMap<String, String> map_project = new HashMap<>();
                            map_project.put("id", id);
                            map_project.put("project_id", project_id);
                            map_project.put("sem_comment", sem_comment);
                            map_project.put("time", time);
                            map_project.put("name", name);
                            map_project.put("profile_picture", profile_picture);
                            map_project.put("project_name", project_name);

                            testimonials_list.add(map_project);


                        }

                        projectdetailAdapter = new Project_detail_Adapter(TestimonialsActivity.this, testimonials_list);
                        LinearLayoutManager llm = new LinearLayoutManager(TestimonialsActivity.this);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        rv_testimonial.setLayoutManager(llm);
                        rv_testimonial.setAdapter(projectdetailAdapter);


                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(TestimonialsActivity.this,
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
