package com.lucsoninfotech.cmi.cmiworld.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lucsoninfotech.cmi.cmiworld.Activity.ProjectDetailActivity;
import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Wish List Adapter.
 */

public class SearchAdapter extends BaseAdapter {

    private final List<HashMap<String, String>> project_list;
    private final Activity context;
    private ProgressDialog pDialog;

    public SearchAdapter(Activity context, List<HashMap<String, String>> project_list) {
        this.context = context;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        SearchAdapter.ViewHolder holder;

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            if (vi != null) {
                view = vi.inflate(R.layout.list_item_home, null);
            }
            holder = createViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (SearchAdapter.ViewHolder) view.getTag();
        }

        System.out.println("enater in adapter");
        SearchAdapter.ViewHolder finalHolder = holder;

       /* if (project_list.get(i).get("like_variable").equals("0")) {
            holder.toggle_like.setChecked(false);
        } else if (project_list.get(i).get("like_variable").equals("1")) {
            holder.toggle_like.setChecked(true);
        }
        holder.toggle_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeurl = Constant.default_url + "project-like.php?user_id=" + Constant.USER_ID + "&project_id=" + project_list.get(i).get("id");
                Like(i);
            }
        });*/
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectDetailActivity.class);
                System.out.println("position in adapter" + i);
                intent.putExtra("project_id", project_list.get(i).get("id"));
                intent.putExtra("like_variable", project_list.get(i).get("like_variable"));
                context.startActivity(intent);
            }
        });

        if (project_list.get(i).get("collected_donation").equals("0") || project_list.get(i).get("collected_donation").equals("null")) {
            holder.progress_donation.setProgress(0);

        } else
            holder.progress_donation.setProgress(((Integer.parseInt(project_list.get(i).get("collected_donation")) * 100) / Integer.parseInt(project_list.get(i).get("estimated_donation"))));
        holder.txt_backers.setText("0");//project_list.get(i).get("backer")
        holder.txt_location.setText(project_list.get(i).get("location"));
        holder.project_name.setText(project_list.get(i).get("project_name"));
        holder.txt_posted_date.setText(project_list.get(i).get("posted_date"));
        holder.txt_collected.setText("0");
        // holder.txt_collected.setText(project_list.get(i).get("collected_donation"));
        holder.txt_estimated.setText(project_list.get(i).get("estimated_donation"));
        holder.progress_donation.getProgressDrawable().setColorFilter(Color.parseColor("#68b04d"), PorterDuff.Mode.SRC_ATOP);
        Picasso.get().load(Constant.Image_url + project_list.get(i).get("featured_image"))
                .placeholder(R.drawable.bg_timeline).into(holder.featured_images);
        return view;
    }

    private SearchAdapter.ViewHolder createViewHolder(View convertView) {

        SearchAdapter.ViewHolder holder = new SearchAdapter.ViewHolder();
        holder.progress_donation = convertView.findViewById(R.id.progress_donation);
        holder.txt_backers = convertView.findViewById(R.id.txt_backers);
        holder.project_name = convertView.findViewById(R.id.project_name);
        holder.txt_posted_date = convertView.findViewById(R.id.txt_posted_date);
        holder.txt_location = convertView.findViewById(R.id.txt_location);
        holder.txt_estimated = convertView.findViewById(R.id.txt_estimated);
        holder.txt_collected = convertView.findViewById(R.id.txt_collected);
        holder.featured_images = convertView.findViewById(R.id.background_image);
        //     holder.toggle_like = (ToggleButton) convertView.findViewById(R.id.toggle_like);
        holder.root = convertView.findViewById(R.id.root);
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);

        return holder;
    }

    /* private void Like(final int i) {
         // Tag used to cancel the request
         String tag_string_req = "req_login";
         pDialog.setMessage("Please Wait ...");
         showDialog();
         System.out.println("like url" + likeurl);

         StringRequest strReq = new StringRequest(Request.Method.POST, likeurl, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 hideDialog();
                 try {
                     JSONObject jObj = new JSONObject(response);
                     int error = jObj.getInt("error_code");
                     // Check for error node in json
                     if (error == 0) {
                         JSONArray data = jObj.getJSONArray("data");

                         string_like = data.getString(0);
                         System.out.println("string like :::" + string_like);
                         if (string_like.equals("Like")) {
                             finalHolder.toggle_like.setChecked(true);
                         } else {
                             finalHolder.toggle_like.setChecked(false);
                         }

                         if(string_like.equals("Like")){
                             project_list.get(i).put("like_variable","1");
                         }else if(string_like.equals("Unlike")){
                             project_list.get(i).put("like_variable","0");
                         }

                     } else {
                         String errorMsg4 = jObj.getString("error_string");
                         Toast.makeText(context,
                                 errorMsg4, Toast.LENGTH_LONG).show();

                     }
                 } catch (JSONException e) {
                     // JSON error
                     e.printStackTrace();
                     Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                 }
             }
         }, new Response.ErrorListener() {

             @Override
             public void onErrorResponse(VolleyError error) {

                 Toast.makeText(context,
                         error.getMessage(), Toast.LENGTH_LONG).show();
                 hideDialog();
             }
         }) {
             @Override
             protected Map<String, String> getParams() {
                 // Posting parameters to login url
                 Map<String, String> params = new HashMap<String, String>();
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

    private class ViewHolder {

        ProgressBar progress_donation;
        TextView project_name, txt_backers, txt_posted_date, txt_location, txt_estimated, txt_collected;
        ImageView featured_images;
        //  ToggleButton toggle_like;
        LinearLayout root;

    }


}
