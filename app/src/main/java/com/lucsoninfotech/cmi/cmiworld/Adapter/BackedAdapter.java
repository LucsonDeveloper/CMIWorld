package com.lucsoninfotech.cmi.cmiworld.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lucsoninfotech.cmi.cmiworld.R;
import com.lucsoninfotech.cmi.cmiworld.other.Constant;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Wish List Adapter.
 */

public class BackedAdapter extends RecyclerView.Adapter {

    private final List<HashMap<String, String>> project_list;
    private ProgressBar progress_donation;
    private TextView project_name;
    private TextView txt_backers;
    private TextView txt_posted_date;
    private TextView txt_location;
    private TextView txt_estimated;
    private TextView txt_collected;
    private TextView Txt_total_donation;
    private ImageView featured_images;

    public BackedAdapter(Context context, List<HashMap<String, String>> project_list) {
        Activity context1 = (Activity) context;
        this.project_list = project_list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_backed_project, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {


        txt_backers.setText(project_list.get(i).get("backer"));
        txt_location.setText(project_list.get(i).get("location"));
        project_name.setText(project_list.get(i).get("project_name"));
        txt_posted_date.setText(project_list.get(i).get("posted_date"));
        txt_collected.setText(project_list.get(i).get("collected_donation"));
        txt_estimated.setText(project_list.get(i).get("estimated_donation"));
        Txt_total_donation.setText(project_list.get(i).get("total_donation_amount"));
        progress_donation.getProgressDrawable().setColorFilter(Color.parseColor("#68b04d"), PorterDuff.Mode.SRC_ATOP);

        Picasso.get().load(Constant.Image_url + project_list.get(i).get("featured_image"))
                .placeholder(R.drawable.bg_timeline).into(featured_images);
    }

    @Override
    public int getItemCount() {
        return project_list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(View convertView) {
            super(convertView);
            progress_donation = convertView.findViewById(R.id.progress_donation);
            txt_backers = convertView.findViewById(R.id.txt_backers);
            project_name = convertView.findViewById(R.id.project_name);
            txt_posted_date = convertView.findViewById(R.id.txt_posted_date);
            txt_location = convertView.findViewById(R.id.txt_location);
            txt_estimated = convertView.findViewById(R.id.txt_estimated);
            txt_collected = convertView.findViewById(R.id.txt_collected);
            featured_images = convertView.findViewById(R.id.background_image);
            Txt_total_donation = convertView.findViewById(R.id.txt_me_donation);
        }
    }
}
