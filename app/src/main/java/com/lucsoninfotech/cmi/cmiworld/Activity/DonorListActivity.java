package com.lucsoninfotech.cmi.cmiworld.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class DonorListActivity extends AppCompatActivity {
    String project_name;
    private ListView lv_donorlist;
    private ProgressDialog pDialog;
    private String donorurl;
    private String image;
    private List<HashMap<String, String>> category_list;
    private ProjectAdapter projectadapter;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_list);

        lv_donorlist = findViewById(R.id.lv_donorlist);
        category_list = new ArrayList<>();
        pDialog = new ProgressDialog(DonorListActivity.this);
        pDialog.setCancelable(false);
        b = getIntent().getExtras();
        SQLiteHandler db = new SQLiteHandler(this);
        HashMap<String, String> user_detail = db.getUserDetails();
        Log.e("UserID", "" + user_detail.get("id"));
        Constant.USER_ID = user_detail.get("id");
        Constant.USER_TYPE = user_detail.get("user_type");

        donorurl = Constant.SEMDonerList + b.getString("project_id") + "&user_id=" + Constant.USER_ID;
        getDonorlist();

        lv_donorlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(DonorListActivity.this, TestimonialsActivity.class);
                intent.putExtra("user_id", category_list.get(i).get("id"));
                intent.putExtra("project_id", b.getString("project_id"));
                startActivity(intent);

            }
        });

    }

    private void getDonorlist() {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait");
        pDialog.setTitle("Please Wait");
        showDialog();
        System.out.println("url in donorlist " + donorurl);

        StringRequest strReq = new StringRequest(Request.Method.POST, donorurl, new Response.Listener<String>() {

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
                            JSONObject c = data.getJSONObject(i);

                            String id = c.getString("id");
                            String name = c.getString("name");
                            image = c.getString("profile_picture");

                            HashMap<String, String> map_project = new HashMap<>();
                            map_project.put("id", id);
                            map_project.put("name", name);
                            map_project.put("image", image);

                            category_list.add(map_project);

                        }
                        projectadapter = new ProjectAdapter(DonorListActivity.this, category_list);
                        lv_donorlist.setAdapter(projectadapter);


                    } else {
                        String errorMsg4 = jObj.getString("error_string");
                        Toast.makeText(DonorListActivity.this,
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


    private class ProjectAdapter extends BaseAdapter {
        final List<HashMap<String, String>> project_list;

        ProjectAdapter(Activity context, List<HashMap<String, String>> project_list) {
            this.project_list = project_list;
        }

        @Override
        public int getCount() {
            return project_list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ProjectAdapter.ViewHolder holder;

            LayoutInflater vi = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            if (view == null) {
                view = vi.inflate(R.layout.list_item_listing, null);
                holder = createViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ProjectAdapter.ViewHolder) view.getTag();
            }

            holder.name.setText(project_list.get(i).get("name"));
            System.out.println("image url " + project_list.get(i).get("image"));
            Picasso.get().load(project_list.get(i).get("image"))
                    .placeholder(R.drawable.bg_timeline).into(holder.image);
            return view;
        }

        private ProjectAdapter.ViewHolder createViewHolder(View convertView) {

            ProjectAdapter.ViewHolder holder = new ProjectAdapter.ViewHolder();
            holder.name = convertView.findViewById(R.id.tv_name);
            holder.image = convertView.findViewById(R.id.imageview);

            return holder;
        }


        private class ViewHolder {

            TextView name;
            ImageView image;
        }


    }
}
