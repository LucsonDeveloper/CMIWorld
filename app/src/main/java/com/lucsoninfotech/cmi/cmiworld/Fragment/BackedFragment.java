package com.lucsoninfotech.cmi.cmiworld.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.Adapter.BackedAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class BackedFragment extends Fragment {

    ImageView donate, create_project;
    LinearLayoutManager linearlayout;
    private BackedAdapter backedadapter;
    private RecyclerView rv_backed;
    private ProgressDialog pDialog;
    private String projecturl;
    private List<HashMap<String, String>> project_list;
    private TextView nodata;

    public BackedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backed, container, false);

        project_list = new ArrayList<>();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        nodata = view.findViewById(R.id.nodata);
        SQLiteHandler db = new SQLiteHandler(getActivity());
        HashMap<String, String> user_detail = db.getUserDetails();
        Log.e("UserID", "" + user_detail.get("id"));
        Constant.USER_ID = user_detail.get("id");

        projecturl = Constant.BackedProject + Constant.USER_ID + "&page=1";

        if (Constant.isOnline(getActivity())) {
            GetProjectData();
        } else {
            Toast.makeText(getActivity(), "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }

        rv_backed = view.findViewById(R.id.view);

        return view;
    }

    private void GetProjectData() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Getting Details ...");
        showDialog();
        System.out.println("backed fragement url" + projecturl);

        StringRequest strReq = new StringRequest(Request.Method.POST, projecturl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);


                    int error = jObj.getInt("error_code");

                    // Check for error node in json
                    if (error == 0) {
                        JSONArray data = jObj.getJSONArray("data");
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject d = data.getJSONObject(i);

                                String id = d.getString("id");
                                String project_name = d.getString("project_name");
                                String short_description = d.getString("short_description");
                                String featured_image = d.getString("featured_image");
                                String estimated_donation = d.getString("estimated_donation");
                                String collected_donation = d.getString("collected_donation");
                                String backer = d.getString("total_donors");
                                String location = d.getString("location");
                                String posted_date = d.getString("posted_date");
                                String country_name = d.getString("country_name");
                                String total_donation_amount = d.getString("total_donation_amount");
                                HashMap<String, String> map_project = new HashMap<>();

                                map_project.put("id", id);
                                map_project.put("project_name", project_name);
                                map_project.put("short_description", short_description);
                                map_project.put("featured_image", featured_image);
                                map_project.put("estimated_donation", estimated_donation);
                                map_project.put("collected_donation", collected_donation);
                                map_project.put("backer", backer);
                                map_project.put("location", location);
                                map_project.put("posted_date", posted_date);
                                map_project.put("country_name", country_name);
                                map_project.put("total_donation_amount", total_donation_amount);

                                project_list.add(map_project);

                            }

                            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                            rv_backed.setLayoutManager(llm);
                            backedadapter = new BackedAdapter(getActivity(), project_list);
                            rv_backed.setAdapter(backedadapter);
                        } else {
                            String errorMsg4 = jObj.getString("error_string");
                            Toast.makeText(getActivity(),
                                    errorMsg4, Toast.LENGTH_LONG).show();
                            nodata.setText(errorMsg4);
                        }
                    } else {

                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(getActivity(),
                                errorMsg4, Toast.LENGTH_LONG).show();
                        nodata.setText(errorMsg4);

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                return new HashMap<>();
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
