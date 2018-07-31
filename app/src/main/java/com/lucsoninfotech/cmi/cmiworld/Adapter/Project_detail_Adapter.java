package com.lucsoninfotech.cmi.cmiworld.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucsoninfotech.cmi.cmiworld.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Wish List Adapter.
 */

public class Project_detail_Adapter extends RecyclerView.Adapter {

    private final List<HashMap<String, String>> project_list;
    String post_data;
    private TextView tv_post;
    private TextView tv_name;
    private TextView tv_time;
    private ImageView img_profile;

    public Project_detail_Adapter(Context context, List<HashMap<String, String>> project_list) {
        Activity context1 = (Activity) context;
        this.project_list = project_list;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_project_detail, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        Picasso.get().load(project_list.get(i).get("profile_picture"))
                .placeholder(R.drawable.bg_timeline).into(img_profile);

        tv_post.setText(project_list.get(i).get("sem_comment"));
        tv_name.setText(project_list.get(i).get("name"));
        tv_time.setText(project_list.get(i).get("time"));

    }

    @Override
    public int getItemCount() {
        return project_list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(View convertView) {
            super(convertView);

            tv_post = convertView.findViewById(R.id.tv_post);
            tv_name = convertView.findViewById(R.id.tv_name);
            tv_time = convertView.findViewById(R.id.tv_time);
            img_profile = convertView.findViewById(R.id.imageview);

        }
    }
}
