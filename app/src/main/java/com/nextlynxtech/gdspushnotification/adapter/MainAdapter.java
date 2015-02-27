package com.nextlynxtech.gdspushnotification.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nextlynxtech.gdspushnotification.R;
import com.nextlynxtech.gdspushnotification.classes.Message;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Probook2 on 24/2/2015.
 */
public class MainAdapter extends BaseAdapter {
    Context context;
    ArrayList<Message> data = new ArrayList<>();
    private LayoutInflater mLayoutInflater = null;

    public MainAdapter(Context context, ArrayList<Message> data) {
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
    public Message getItem(int position) {
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
        holder.tvEventName.setText(data.get(position).getEventName());
        holder.tvMessage.setText(data.get(position).getMessage());
        return convertView;
    }

    public class ViewHolder {
        @InjectView(R.id.tvEventName)
        TextView tvEventName;

        @InjectView(R.id.tvMessage)
        TextView tvMessage;

        public ViewHolder(View base) {
            ButterKnife.inject(this, base);
        }
    }
}
