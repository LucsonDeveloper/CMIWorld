package com.lucsoninfotech.cmi.cmiworld.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lucsoninfotech.cmi.cmiworld.R;

import java.util.HashMap;
import java.util.List;

/**
 * Wish List Adapter.
 */

public class PaymentAdapter extends BaseAdapter {

    private final List<HashMap<String, String>> project_list;
    private final Activity context;
    int count = 1;

    public PaymentAdapter(Activity context, List<HashMap<String, String>> project_list) {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            if (vi != null) {
                view = vi.inflate(R.layout.list_item_payment_detail, null);
            }
            holder = createViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_card_no.setText(project_list.get(i).get("card_number"));
        holder.tvNumber.setText(String.valueOf(i + 1));
        i++;

        return view;
    }

    private ViewHolder createViewHolder(View convertView) {

        ViewHolder holder = new ViewHolder();

        holder.tvNumber = convertView.findViewById(R.id.tvNumber);
        holder.tv_card_no = convertView.findViewById(R.id.tv_card_no);


        return holder;
    }


    private class ViewHolder {

        TextView tvNumber, tv_card_no;

    }


}
