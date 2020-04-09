package com.ogangi.Messangi.SDK.Demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingNotification;

import java.util.ArrayList;

public class ListNotification extends AppCompatActivity {
    public static String CLASS_TAG=ListNotification.class.getSimpleName();
    public static String TAG="MessangiSDK";

    public Messaging messaging;
    public Button back,clear;
    public ListView list_notification;

    public ArrayList<MessagingNotification> messagingNotificationArrayList;
    public ListAdapter messangiNotificationAdapter;
    public ProgressBar progressBar;

    MessagingNotification messagingNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_list_notification);

        messaging = Messaging.getInst();
        list_notification = findViewById(R.id.list_notification_push);
        back=findViewById(R.id.button_back);
        clear=findViewById(R.id.button_clear);
        messagingNotificationArrayList = messaging.getMessagingNotifications();
        if(messagingNotificationArrayList.size()>0) {
            messangiNotificationAdapter = new ListAdapter(getApplicationContext(), messagingNotificationArrayList);
            list_notification.setAdapter(messangiNotificationAdapter);
        }else{
            list_notification.setVisibility(View.GONE);
            TextView provImp=findViewById(R.id.textView_noti);
            provImp.setText("Hasn't  Notification");
        }

        list_notification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                showAlertNotificaction(messagingNotificationArrayList.get(position));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagingNotificationArrayList.clear();
                finish();
                startActivity(getIntent());

            }
        });


    }

    private void showAlertNotificaction(MessagingNotification messagingNotification) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
        builder.setView(customLayout);

        TextView data=customLayout.findViewById(R.id.data_noti);
        if (messagingNotification.getData()!=null && messagingNotification.getData().size() > 0) {
            data.setText("data: " + messagingNotification.getData());
            if(messagingNotification.getNotification()!=null){
                data.append("Has Notification"+"\n");
                data.append(""+ messagingNotification.getNotification().getTitle()+"\n");
                data.append(""+ messagingNotification.getNotification().getBody());
            }
        } else {
            data.setText("Hasn't data"+"\n");

        }



        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
