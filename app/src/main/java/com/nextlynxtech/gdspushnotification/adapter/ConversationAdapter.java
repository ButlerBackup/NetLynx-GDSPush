package com.nextlynxtech.gdspushnotification.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nextlynxtech.gdspushnotification.MainApplication;
import com.nextlynxtech.gdspushnotification.R;
import com.nextlynxtech.gdspushnotification.classes.GenericResult;
import com.nextlynxtech.gdspushnotification.classes.Message;
import com.nextlynxtech.gdspushnotification.classes.SQLFunctions;
import com.nextlynxtech.gdspushnotification.classes.UpdateMessageRead;
import com.nextlynxtech.gdspushnotification.classes.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        int type = getItemViewType(position);
        if (convertView == null) {
            if (type == TYPE_LEFT) {
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
        if (type == TYPE_LEFT) {
            switch (m.getColor()) {
                case 0:
                    holder.tvMessageHeader.setTextColor(context.getResources().getColor(R.color.black));
                    break;
                case 1:
                    holder.tvMessageHeader.setTextColor(context.getResources().getColor(R.color.red));
                    break;
                case 2:
                    holder.tvMessageHeader.setTextColor(context.getResources().getColor(R.color.green));
                    break;
                case 3:
                    holder.tvMessageHeader.setTextColor(context.getResources().getColor(R.color.yellow));
                    break;
                case 4:
                    holder.tvMessageHeader.setTextColor(context.getResources().getColor(R.color.blue));
                    break;
                case 5:
                    holder.tvMessageHeader.setTextColor(context.getResources().getColor(R.color.magenta));
                    break;
                case 6:
                    holder.tvMessageHeader.setTextColor(context.getResources().getColor(R.color.cyan_500));
                    break;
                case 7:
                    holder.tvMessageHeader.setTextColor(context.getResources().getColor(R.color.white));
                    break;
            }
            holder.tvMessage.setText(m.getMessage());
            holder.tvMessageHeader.setText(m.getMessageHeader());
            if (m.getRecallFlag() == 1) {
                holder.ivRecallFlag.setVisibility(View.VISIBLE);
                holder.ivRecallFlag.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_warning)
                        .colorRes(R.color.red)
                        .actionBarSize());
                holder.ivRecallFlag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "This message needs your reply confirmation.", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (m.getRecallFlag() == 2) {
                holder.ivRecallFlag.setVisibility(View.VISIBLE);
                holder.ivRecallFlag.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_done_all)
                        .colorRes(R.color.green)
                        .actionBarSize());
                holder.ivRecallFlag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "You have replied to this message.", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                holder.ivRecallFlag.setVisibility(View.GONE);
            }
            long time = Long.parseLong(m.getMessageDate()) * 1000;
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("d LLL HH:mm a");
            holder.tvMessageTime.setText(format.format(date));
            if (m.getRead() != 1) {
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            int messageId = params[0];
                            GenericResult r = MainApplication.service.UpdateMessageReadStatus(new UpdateMessageRead("", new Utils(context).getUnique(), messageId));
                            Log.e("Result", r.getStatusCode());
                            if (r.getStatusCode().equals("1") && r.getStatusDescription().equals("OK")) {
                                SQLFunctions sql = new SQLFunctions(context);
                                sql.open();
                                sql.setMessageRead(String.valueOf(messageId));
                                sql.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(m.getMessageId());
            }
        } else {
            holder.tvMessageHeader.setText(m.getMessageHeader());
            holder.tvMessage.setText(m.getMessage());
            long time = Long.parseLong(m.getMessageDate()) * 1000;
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("d LLL HH:mm a");
            holder.tvMessageTime.setText(format.format(date));
            if (m.getReplySuccess() == 1) {
                holder.ivRecallFlag.setVisibility(View.VISIBLE);
                holder.ivRecallFlag.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_done_all)
                        .colorRes(R.color.green)
                        .actionBarSize());
                holder.ivRecallFlag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Reply successfully sent.", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (m.getReplySuccess() == 2) { //loading
                holder.ivRecallFlag.setVisibility(View.VISIBLE);
                holder.ivRecallFlag.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_report)
                        .colorRes(R.color.red)
                        .actionBarSize());
                holder.ivRecallFlag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Reply not sent at all.", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                holder.ivRecallFlag.setVisibility(View.VISIBLE);
                holder.ivRecallFlag.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_done)
                        .colorRes(R.color.blue)
                        .actionBarSize());
                holder.ivRecallFlag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Reply is waiting for server response.", Toast.LENGTH_LONG).show();
                    }
                });
            }
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
        @InjectView(R.id.tvMessageTime)
        TextView tvMessageTime;

        public ViewHolder(View base) {
            ButterKnife.inject(this, base);
        }
    }
}
