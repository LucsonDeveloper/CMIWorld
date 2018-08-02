package com.lucsoninfotech.cmi.cmiworld.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.Activity.ChatRoomActivity;
import com.lucsoninfotech.cmi.cmiworld.Activity.ProfileActivity;
import com.lucsoninfotech.cmi.cmiworld.Activity.ProjectListActivity;
import com.lucsoninfotech.cmi.cmiworld.Activity.SettingActivity;
import com.lucsoninfotech.cmi.cmiworld.Activity.TestimonialsActivity;
import com.lucsoninfotech.cmi.cmiworld.Activity.UserStatusActivity;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.helper.SQLiteHandler;
import com.lucsoninfotech.cmi.cmiworld.other.AppController;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    CardView finalstep;
    private String url;
    private ProgressDialog pDialog;
    private ImageView profile_picture;
    private TextView username;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        LinearLayout btn_testimonials = view.findViewById(R.id.btn_testimonials);
        LinearLayout btn_messages = view.findViewById(R.id.btn_messages);
        LinearLayout btn_setting = view.findViewById(R.id.btn_setting);
        LinearLayout btn_status = view.findViewById(R.id.btn_status);
        profile_picture = view.findViewById(R.id.profile_picture);
        username = view.findViewById(R.id.username);
        finalstep = view.findViewById(R.id.finalstep);
        SQLiteHandler db = new SQLiteHandler(getActivity());
        HashMap<String, String> user_detail = db.getUserDetails();
        Log.e("UserID", "" + user_detail.get("id"));
        Constant.USER_ID = user_detail.get("id");
        Constant.USER_TYPE = user_detail.get("user_type");
        //  System.out.println("user type from database::::::" + Constant.USER_TYPE);
        Log.e("USER_TYPE", "" + Constant.USER_TYPE);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        url = Constant.ViewProfileUrl + Constant.USER_ID;

        if (Constant.isOnline(getActivity())) {
            getProfileData();
        } else {
            Toast.makeText(getActivity(), "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }

        finalstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isOnline(getActivity())) {
                    Intent i = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_testimonials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.USER_TYPE.equals("2")) {
                    Intent i = new Intent(getActivity(), ProjectListActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getActivity(), TestimonialsActivity.class);
                    startActivity(i);
                }
            }
        });


        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), UserStatusActivity.class);
                startActivity(i);

            }
        });


        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), SettingActivity.class);
                startActivity(i);
            }
        });

        btn_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ChatRoomActivity.class);
                startActivity(i);
            }
        });

        return view;

    }

    private void getProfileData() {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait");
        pDialog.setTitle("Processing");
        showDialog();
        System.out.println("url in profile fragment" + url);

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {

                        JSONObject data = jObj.getJSONObject("data");

                        String name = data.getString("name");
                        String sprofile_picture = data.getString("profile_picture");
                        Constant.USER_DEVELOPMENT = data.getInt("user_level");
                        Constant.USER_EXPERIENCE = data.getInt("user_experience_level");

                        Constant.USER_NAME = name;
                        Constant.USER_IMAGE = sprofile_picture;

                        username.setText(name);

                        Picasso.get().load(sprofile_picture).into(profile_picture);


                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(getActivity(),
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
