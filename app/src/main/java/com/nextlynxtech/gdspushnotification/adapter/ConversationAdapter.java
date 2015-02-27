package com.nextlynxtech.gdspushnotification.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nextlynxtech.gdspushnotification.R;
import com.nextlynxtech.gdspushnotification.classes.Message;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ConversationAdapter extends BaseAdapter {
    private static final int TYPES_COUNT = 2;
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;
    Context context;
    ArrayList<Message> data = new ArrayList<>();
    private LayoutInflater mLayoutInflater = null;

    public ConversationAdapter(Context context, ArrayList<Message> data) {
        this.context = context;
        this.data = data;

        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        return TYPES_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getMine() == 0) {
            return TYPE_LEFT;
        }
        return TYPE_RIGHT;
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
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            if (getItemViewType(position) == TYPE_LEFT) {
                convertView = mLayoutInflater.inflate(R.layout.activity_conversation_item_left, parent, false);
            } else {
                convertView = mLayoutInflater.inflate(R.layout.activity_conversation_item_right, parent, false);
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message m = data.get(position);
        holder.tvMessage.setText(m.getMessage());
        holder.tvMessageHeader.setText(m.getMessageHeader());
        if (m.getRecallFlag() == 1) {
            holder.ivRecallFlag.setVisibility(View.VISIBLE);
            holder.ivRecallFlag.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_report)
                    .colorRes(R.color.red)
                    .actionBarSize());
        } else {
            holder.ivRecallFlag.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class ViewHolder {
        @InjectView(R.id.tvMessage)
        TextView tvMessage;
        @InjectView(R.id.tvMessageHeader)
        TextView tvMessageHeader;
        @InjectView(R.id.ivRecallFlag)
        ImageView ivRecallFlag;

        public ViewHolder(View base) {
            ButterKnife.inject(this, base);
        }
    }
}
