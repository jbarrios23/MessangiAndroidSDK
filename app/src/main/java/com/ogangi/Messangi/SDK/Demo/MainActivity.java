package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ogangi.messangi.sdk.Messangi;
import com.ogangi.messangi.sdk.ServiceCallback;
import com.ogangi.messangi.sdk.MessangiDev;
import com.ogangi.messangi.sdk.MessangiUserDevice;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, ServiceCallback {

    Activity activity = MainActivity.this;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public Messangi messangi;
    public Button getPhone;
    public TextView imprime;
    public MessangiDev messangiDev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imprime=findViewById(R.id.textView_imprimir);
        messangi=Messangi.getInst(this);


        getPhone=findViewById(R.id.button_getPhone);



        getPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



             creatAlert();
            }
        });

        imprime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messangi.setUserPhone("0414522544");
                messangi.setUserEmail("Jb@Jb.com");
                messangi.setUserExternalId("55223366");
                messangi.setUserProperties("age","12");

                Log.e(CLASS_TAG,""+messangi.getTags());

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        messangi.requestDevice(false,MainActivity.this);
    }

    private void creatAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

            // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                Log.e(CLASS_TAG,m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.e(CLASS_TAG,"PERMISSION_GRANTED");
//                    messangi.getPhone(activity);
//                } else {
//                    Toast.makeText(activity,"Permission Denied. ", Toast.LENGTH_LONG).show();
//
//                }
//                break;
//        }
//    }


    @Override
    public void handlerGetMessangiDevice(MessangiDev result) {

        messangiDev=result;
        messangiDev.requestUserByDevice(getApplicationContext(),false,MainActivity.this);
        String provId=messangiDev.getId();
        Log.e(CLASS_TAG,"Id "+provId);
        imprime.setText("ID "+provId);


    }

    @Override
    public void handlerGetMessangiUser(MessangiUserDevice result) {
        MessangiUserDevice messangiUserDevice=result;


        Log.e(CLASS_TAG,"mobile "+messangiUserDevice.getMobile());

    }

    @Override
    public void handlerPostDevice() {
        Log.e(CLASS_TAG,"get device ");

    }
}
