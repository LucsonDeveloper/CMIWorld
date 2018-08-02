package com.lucsoninfotech.cmi.cmiworld.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lucsoninfotech.cmi.cmiworld.Activity.FilterActivity;
import com.lucsoninfotech.cmi.cmiworld.Adapter.SearchAdapter;
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

public class SearchFragment extends Fragment {

    private ListView lv_search;
    private EditText edt_search;
    private SearchAdapter homeadapter;
    private ProgressDialog pDialog;
    private String projecturl;
    private List<HashMap<String, String>> project_list;
    private TextView nodata;
   @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
      /*  Toolbar toolbar = view.findViewById(R.id.toolbar_search);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(null);*/
        nodata = view.findViewById(R.id.nodata);
        lv_search = view.findViewById(R.id.lv_search);
        ImageView img_search = view.findViewById(R.id.img_search);
        edt_search = view.findViewById(R.id.edt_search);
        ImageView img_filter = view.findViewById(R.id.img_filter);

        project_list = new ArrayList<>();
     SQLiteHandler db = new SQLiteHandler(getActivity());
        HashMap<String, String> user_detail = db.getUserDetails();
        Log.e("UserID", "" + user_detail.get("id"));
        Constant.USER_ID = user_detail.get("id");

        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FilterActivity.class);
                startActivity(i);

            }
        });
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = edt_search.getText().toString().trim();

                if (Constant.isOnline(getActivity())) {
                    if (search.equals("")) {
                        Toast.makeText(getActivity(), "Please Enter Search Data", Toast.LENGTH_LONG).show();
                    } else if (Constant.NGO_ID.equals("") || Constant.Country_ID.equals("") || Constant.Category_ID.equals("") ||
                            Constant.Tags_ID.equals("")) {
                        projecturl = Constant.SearchProjectUrl + Constant.USER_ID + "&page=1&project_name=" + search
                                + "&ngo_names_id=" + "" + "&countries_id=" + "" +
                                "&categories_id=" + "" +
                                "&tags_id=" + "";
                        pDialog = new ProgressDialog(getActivity());
                        pDialog.setCancelable(false);
                        if (project_list.size() >= 1) {
                            project_list.clear();
                        }
                        GetProjectData();

                    } else {
                        projecturl = Constant.SearchProjectUrl + Constant.USER_ID + "&page=1"+
                                "&project_name=" + search + "&ngo_names_id=" + Constant.NGO_ID + "&countries_id=" + Constant.Country_ID +
                                "&categories_id=" + Constant.Category_ID +
                                "&tags_id=" + Constant.Tags_ID;
                        pDialog = new ProgressDialog(getActivity());
                        pDialog.setCancelable(false);
                        if (project_list.size() >= 1) {
                            project_list.clear();
                        }
                        GetProjectData();


                    }
                } else {
                    Toast.makeText(getActivity(), "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void GetProjectData() {

        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Getting Projects ...");
        showDialog();
        System.out.println("Search  url ::: " + projecturl);

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

                                project_list.add(map_project);

                            }
                            homeadapter = new SearchAdapter(getActivity(), project_list);
                            lv_search.setAdapter(homeadapter);
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
