package com.ogangi.Messangi.SDK.Demo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.messaging.sdk.Messaging;
import com.ogangi.Messangi.SDK.Demo.scanqr.CaptureActivityAnyOrientation;
import com.ogangi.Messangi.SDK.Demo.scanqr.SmallCaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_PHONE;
import static android.text.InputType.TYPE_CLASS_TEXT;

public class LoginActivity extends AppCompatActivity {
    public static String CLASS_TAG=LoginActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    public Button button_get_started;
    public TextView scan_title;
    public ImageView imageView;
    private String nameMethod;
    public LinearLayout linearLayout;
    public EditText customField,customEmail,customPhone;
    public EditText editText;
    public int numberFields=2;
//    public String [] myListName;
//    public String [] myListType;
    public String [] myListResult;
    public List<String> myListName;
    public List<String> myListType;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        button_get_started=findViewById(R.id.button);
        scan_title=findViewById(R.id.textView_scan);
        imageView=findViewById(R.id.imageView_visualizer);
        linearLayout=findViewById(R.id.linearLayoutData);
        progressBar=findViewById(R.id.progressBar);
//        myListName= new String[]{"masukan jenis kursus","Tambah jenis kursus",
//                "masukan jenis kursus","masukan jenis kursus","Tambah jenis kursus",
//                 "new task","new task","new task"};
//        customField=findViewById(R.id.editText_custom_field);
//        customEmail=findViewById(R.id.editText_email);
//        customPhone=findViewById(R.id.editText_phone);

        button_get_started.setText(getResources().getText(R.string.get_started));
        imageView.setVisibility(View.VISIBLE);

//        // Create EditText
//        editText= new EditText(this);
//        editText.setHint(R.string.hint);
//        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        editText.setPadding(20, 20, 20, 20);


        button_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button_get_started.getText().equals(getResources().getText(R.string.get_finish))){
                    //LoginActivity.this.finish();
                    showLinearData();



                }else{
                    if(button_get_started.getText().equals(getResources().getText(R.string.get_continue))){

                        getDataFromEditText();
                    }else{
                        callScanQr();
                        button_get_started.setText(getResources().getText(R.string.get_finish));
                        scan_title.setVisibility(View.VISIBLE);
                        //imageView.setImageResource(R.drawable.common_google_signin_btn_text_light);
                        imageView.setVisibility(View.GONE);
                        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + button_get_started.getText());
                    }

                }


            }
        });
    }

    private void addEditTextDynamically(LinearLayout mParentLayout, List<String> myList){

        for (int i=0;i<myList.size();i++){
            editText = new EditText(mParentLayout.getContext());
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20,20,0,30);
            editText.setLayoutParams(lp);
            editText.setId(i);
            editText.setTag(myListName.get(i));
            editText.setHint(myListName.get(i));
            if(myListType.get(i).equals("STRING")){
                editText.setInputType(TYPE_CLASS_TEXT);
            }else if(myListType.get(i).equals("NUMBER")){
                editText.setInputType(TYPE_CLASS_PHONE);
            }
            editText.setPadding(20, 20, 20, 20);
            editText.setHintTextColor(getResources().getColor(R.color.greyColor));
            editText.setBackgroundColor(getResources().getColor(R.color.greyColorSoft));
            mParentLayout.addView(editText);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": register LocalBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_FETCH_FIELDS));

    }

    private void callScanQr() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(LoginActivity.this);

        //IntentIntegrator scanIntegrator = IntentIntegrator.forSupportFragment(ScanFragment.this);

        //scanIntegrator.setPrompt("Scan QR");
        scanIntegrator.setBeepEnabled(true);

        //enable the following line if you want QR code
        //scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        scanIntegrator.setCaptureActivity(SmallCaptureActivity.class);
        scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.initiateScan();
    }

    @SuppressLint("ResourceType")
    private void getDataFromEditText() {
    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
    myListResult=getInputArrayFromEditTexts(linearLayout);
    Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + Arrays.toString(myListResult));
    Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + Arrays.toString(new List[]{myListName}));
    JSONObject jsonObject = new JSONObject();
    for(int i=0;i<myListName.size();i++){

        try {
            jsonObject.put(myListName.get(i),myListResult[i]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + jsonObject);
    LoginActivity.this.finish();

    }

    private String[] getInputArrayFromEditTexts(LinearLayout mParentLayout){
        String[] inputArray = new String [mParentLayout.getChildCount()];
        for (int i = 0; i <inputArray.length ; i++) {
            editText  =(EditText) mParentLayout.getChildAt(i);
            inputArray[i] = editText.getText().toString();
        }
        return inputArray;
    }

    public void showLinearData() {
        if(button_get_started.getText().equals(getResources().getText(R.string.get_continue))){
            
        }else {
            imageView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            scan_title.setVisibility(View.VISIBLE);
            scan_title.setTextSize(25);
            scan_title.setText(getResources().getText(R.string.let_get_started_title));
            button_get_started.setText(getResources().getText(R.string.get_continue));
            // Add EditText to LinearLayout
            if(myListName!=null && myListName.size()>0) {
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }
                addEditTextDynamically(linearLayout, myListName);
            }else{
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }
                 LoginActivity.this.finish();
            }
            Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + button_get_started.getText());
        }

    }

    @Override
    protected void onDestroy() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": unregister LocalBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            String scanContent = "";
            String scanFormat = "";
            if (scanningResult.getContents() != null) {
                scanContent = scanningResult.getContents().toString();
                scanFormat = scanningResult.getFormatName().toString();
            }

            Log.d(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + scanContent + "    type:" + scanFormat);
            if(!scanContent.equals("")&& !scanContent.isEmpty()){
                String[] prvHandlerMessage=scanContent.split(":%:");
                String prvTokenApp=prvHandlerMessage[0];
                String provHostApp=prvHandlerMessage[1];
                Log.d(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "Token: " +prvTokenApp+" Host "+provHostApp);
                //new HttpRequestTaskGet(provHostApp,prvTokenApp).execute();
                //showLinearData(prvTokenApp,provHostApp);
                progressBar.setVisibility(View.VISIBLE);
                Messaging.fetchFields(getApplicationContext(),prvTokenApp,provHostApp);
            }else{
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_LONG).show();
                Log.d(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " +"Cancelled");
            }

        } else {
            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_SHORT).show();
        }


    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

            boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
            Log.d(TAG,"ERROR: "+CLASS_TAG+": "+nameMethod+": Has error:  "+ hasError);
            if (!hasError ) {
                //Serializable data=intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
                String data=intent.getStringExtra(Messaging.INTENT_EXTRA_DATA);
                if(intent.getAction().equals(Messaging.ACTION_FETCH_FIELDS) && data!=null){
                    Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": data:  "+ data);
                    JSONArray arr = null;
                    try {
                        arr = new JSONArray(data);
                         myListName = new ArrayList<String>();
                         myListType = new ArrayList<String>();
                        for(int i = 0; i < arr.length(); i++){
                            myListName.add(arr.getJSONObject(i).getString("name"));
                            myListType.add(arr.getJSONObject(i).getString("type"));
                        }
                        showLinearData();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }else{
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_LONG).show();
                    Log.e(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": An error occurred on action:  "
                            + intent.getAction());
                }

            }else{
                Log.e(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": An error occurred on action:  "
                        + intent.getAction());
                Toast.makeText(getApplicationContext(),"An error occurred on action "
                        +intent.getAction(),Toast.LENGTH_LONG).show();

            }

        }

    };


}