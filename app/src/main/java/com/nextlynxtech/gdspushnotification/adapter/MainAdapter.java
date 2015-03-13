package com.nextlynxtech.gdspushnotification.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nextlynxtech.gdspushnotification.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Probook2 on 24/2/2015.
 */
public class MainAdapter extends BaseAdapter {
    Context context;
    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    private LayoutInflater mLayoutInflater = null;

    public MainAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.data = data;
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.activity_main_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, String> item = data.get(position);
        holder.tvEventName.setText(item.get("title"));
        holder.tvMessage.setText(item.get("message"));
        if (Integer.parseInt(item.get("count")) > 0) {
            holder.tvMessage.setTypeface(null, Typeface.BOLD);
            holder.tvEventName.setTypeface(null, Typeface.BOLD);
        } else {
            holder.tvMessage.setTypeface(null, Typeface.NORMAL);
            holder.tvEventName.setTypeface(null, Typeface.NORMAL);
        }
        if (item.get("notification").equals("1")) {
            holder.ivNotification.setVisibility(View.VISIBLE);
            holder.ivNotification.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_warning)
                    .colorRes(R.color.red)
                    .actionBarSize());
        } else {
            holder.ivNotification.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class ViewHolder {
        @InjectView(R.id.tvEventName)
        TextView tvEventName;

        @InjectView(R.id.tvMessage)
        TextView tvMessage;

        @InjectView(R.id.ivNotification)
        ImageView ivNotification;

        public ViewHolder(View base) {
            ButterKnife.inject(this, base);
        }
    }
}
