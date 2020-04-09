package com.ogangi.Messangi.SDK.Demo;



import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.messaging.sdk.MessagingNotification;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    public static String CLASS_TAG=ListAdapter.class.getSimpleName();
    public static String TAG="MessangiSDK";

    public Context context;
    public List<MessagingNotification> messagingNotificationList;
    public LayoutInflater inflater;
    public ListAdapter(Context context, ArrayList<MessagingNotification> messagingNotificationArrayList) {
        this.context=context;
        inflater= LayoutInflater.from(this.context);
        this.messagingNotificationList = messagingNotificationArrayList;
    }

    public class ViewHolder {

        TextView data;
        TextView date;
    }

    @Override
    public int getCount() {
        return messagingNotificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return messagingNotificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView== null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_notification, null);

            holder.data =  convertView.findViewById(R.id.Texview_value);
            holder.date =  convertView.findViewById(R.id.texview_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(messagingNotificationList.get(position).getData()!=null&& messagingNotificationList.get(position).getData().size()>0) {
            holder.data.setText("" + messagingNotificationList.get(position).getData());

            holder.date.setText("" + convertSecondsToHMmSs(messagingNotificationList.get(position).getSentTime()));

            if(messagingNotificationList.get(position).getNotification()!=null){
                holder.data.append("Has Notification"+"\n");
                holder.data.append(""+ messagingNotificationList.get(position).getNotification().getTitle()+"\n");
                holder.data.append(""+ messagingNotificationList.get(position).getNotification().getBody());
                holder.date.setText("" + convertSecondsToHMmSs(messagingNotificationList.get(position).getSentTime()));
            }
        }else{
            holder.data.setText("Hasn't data"+"\n");

        }


        return convertView;
    }

    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }
}
