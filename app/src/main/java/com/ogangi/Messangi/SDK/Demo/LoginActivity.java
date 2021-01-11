package com.ogangi.Messangi.SDK.Demo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
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
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.messaging.sdk.Messaging;

import com.messaging.sdk.MessagingDevice;
import com.messaging.sdk.MessagingUser;
import com.ogangi.Messangi.SDK.Demo.scanqr.CaptureActivityAnyOrientation;
import com.ogangi.Messangi.SDK.Demo.scanqr.SmallCaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_PHONE;
import static android.text.InputType.TYPE_CLASS_TEXT;

public class LoginActivity extends AppCompatActivity {
    public static String CLASS_TAG=LoginActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    public Button button_get_started,skip;
    public TextView scan_title;
    public ImageView imageView;
    private String nameMethod;
    public LinearLayout linearLayout;
    public EditText customField,customEmail,customPhone;
    public EditText editText;
    public int numberFields=2;
    public String prvTokenApp;
    public String provHostApp;
    public String [] myListResult;
    public ArrayList<String> listField;
    public ArrayList<String> listTypes;
    public HashMap<String,String> dataInputToSendUser;
    public HashMap<String,String> dataInput;
    public ArrayList<HashMap<String,String>> dataInputList;
    public ProgressBar progressBar;
    public boolean flagError=true;
    public Messaging messaging;
    public MessagingUser messagingUser;
    public boolean userUpdate=false;
    public boolean useQrScan=false;
    public boolean onetimeFlag=true;
    public boolean onetimeFlagUser=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
        button_get_started=findViewById(R.id.button);
        skip=findViewById(R.id.button_skip);
        scan_title=findViewById(R.id.textView_scan);
        imageView=findViewById(R.id.imageView_visualizer);
        linearLayout=findViewById(R.id.linearLayoutData);
        progressBar=findViewById(R.id.progressBar);
        messaging=Messaging.getInstance(this);
        messagingUser = new MessagingUser();

        button_get_started.setText(getResources().getText(R.string.get_started));
        imageView.setVisibility(View.VISIBLE);
        skip.setVisibility(View.INVISIBLE);

        button_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button_get_started.getText().equals(getResources().getText(R.string.get_finish))){
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

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        verifyHasDeviceRegister();
    }

    private void verifyHasDeviceRegister() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MESSAGING_LOGIN", Context.MODE_PRIVATE);
        nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
        boolean isLogged=sharedPreferences.getBoolean("IS_LOGGED", false);
        if(isLogged){
            Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + " IS_LOGGED: " + isLogged);
            goToMainActivity();
        }else{
            skip.setVisibility(View.VISIBLE);
            skip.setEnabled(true);
            Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + " IS_LOGGED: " + isLogged);
        }
    }

    private void addEditTextDynamically(LinearLayout mParentLayout, ArrayList<HashMap<String, String>> myList){
        for (int i=0;i<myList.size();i++) {
            editText = new EditText(mParentLayout.getContext());
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20,20,0,30);
            editText.setLayoutParams(lp);
            editText.setId(i);
            if(myList.get(i).containsKey("label")){
                editText.setTag(myList.get(i).get("label"));
                editText.setHint(myList.get(i).get("label"));
                if(myList.get(i).containsKey("type")){
                    if(myList.get(i).get("type").equals("STRING")){
                        editText.setInputType(TYPE_CLASS_TEXT);
                    }else if(myList.get(i).get("type").equals("NUMBER")){
                        editText.setInputType(TYPE_CLASS_PHONE);
                    }
                }else{
                    editText.setInputType(TYPE_CLASS_TEXT);
                }
            }
            if(myList.get(i).containsKey("name")){
                editText.setTag(myList.get(i).get("name"));
                editText.setHint(myList.get(i).get("name"));
                if(myList.get(i).containsKey("type")){
                    if(myList.get(i).get("type").equals("STRING")){
                        editText.setInputType(TYPE_CLASS_TEXT);
                    }else if(myList.get(i).get("type").equals("NUMBER")){
                        editText.setInputType(TYPE_CLASS_PHONE);
                    }
                }else{
                    editText.setInputType(TYPE_CLASS_TEXT);
                }
            }

            editText.setTextSize(14f);
            editText.setPadding(20, 20, 20, 20);
            editText.setHintTextColor(getResources().getColor(R.color.greyColor));
            editText.setBackgroundColor(getResources().getColor(R.color.whiteColor));
            mParentLayout.addView(editText);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": register LocalBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Messaging.ACTION_FETCH_FIELDS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Messaging.ACTION_REGISTER_DEVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Messaging.ACTION_FETCH_USER));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Messaging.ACTION_SAVE_USER));
    }

    private void callScanQr() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(LoginActivity.this);

        //IntentIntegrator scanIntegrator = IntentIntegrator.forSupportFragment(ScanFragment.this);

        //scanIntegrator.setPrompt("Scan QR");
        scanIntegrator.setBeepEnabled(true);

        //enable the following line if you want QR code
        //scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        //scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        scanIntegrator.setCaptureActivity(SmallCaptureActivity.class);
        scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.initiateScan();
        Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + "ScanQr State: " + useQrScan);
    }

    @SuppressLint("ResourceType")
    private void getDataFromEditText() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        myListResult=getInputArrayFromEditTexts(linearLayout);
        listField=new ArrayList<>();
        listTypes=new ArrayList<>();
        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + Arrays.toString(myListResult));

        for(int i=0;i<dataInputList.size();i++){
            for (Map.Entry<String, String> entry : dataInputList.get(i).entrySet()) {

                if(entry.getKey().equals("field")){
                    listField.add(entry.getValue());
                }
                if(entry.getKey().equals("name")){
                    listField.add(entry.getValue());
                }
                if(entry.getKey().equals("type")){
                    listTypes.add(entry.getValue());
                }
            }

        }

        Log.i(TAG, "INFO: " + CLASS_TAG + " Fields : " + nameMethod + ": " + Arrays.toString(new List[]{listField}));
        Log.i(TAG, "INFO: " + CLASS_TAG + " Types : " + nameMethod + ": " + Arrays.toString(new List[]{listTypes}));
        dataInputToSendUser=new HashMap<String, String>();

        for(int i=0;i<myListResult.length;i++){
            dataInputToSendUser.put(listField.get(i),myListResult[i]);

            if(listTypes.get(i).equals("STRING")) {

                if (Utils.isNullOrEmpty(myListResult[i])) {
                    Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + " this field can not be blank: " + listField.get(i));
                    Toast.makeText(getApplicationContext(), " this field can not be blank: " + listField.get(i), Toast.LENGTH_LONG).show();
                    flagError = false;
                    break;
                } else {
                    flagError = true;
                }

                if (listField.get(i).equals("email")) {

                    if (!Utils.isValidMail(myListResult[i])) {
                        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + " It is not a valid email: "
                                + myListResult[i]);
                        Toast.makeText(getApplicationContext(), " It is not a valid email: "
                                + myListResult[i], Toast.LENGTH_LONG).show();
                        flagError = false;
                        break;
                    } else {
                        flagError = true;
                    }
                }

                if (listTypes.get(i).equals("DATE")) {

                    if (!Utils.checkDateFormat(myListResult[i])) {
                        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + " Format date is not valid: "
                                + myListResult[i]);
                        Toast.makeText(getApplicationContext(), " Format date is not valid: "
                                + myListResult[i], Toast.LENGTH_LONG).show();
                        flagError = false;
                        break;
                    } else {
                        flagError = true;
                    }

                }
            }

            if(listTypes.get(i).equals("NUMBER")) {

                if(!Utils.isNumeric(myListResult[i])){
                    Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + " this number does not have a valid format: "
                            + myListResult[i]);
                    Toast.makeText(getApplicationContext()," this number does not have a valid format: "
                            + myListResult[i],Toast.LENGTH_LONG).show();
                    flagError=false;
                    break;
                }else{
                    flagError=true;
                }
                if(listField.get(i).equals("phone")){

                    if(!Utils.isValidPhoneNumber(myListResult[i])){
                        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + " It is not a valid phone number: "
                                + myListResult[i]);
                        Toast.makeText(getApplicationContext()," It is not a valid phone number: "
                                + myListResult[i],Toast.LENGTH_LONG).show();
                        flagError=false;
                        break;
                    }else{
                        flagError=true;
                    }
                }
            }
        }

        if(flagError){
            //order: update config, create device and user, update user
            Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "def: " + dataInputToSendUser);
            reloadSdkParameter(true);
        }
    }

    private void reloadSdkParameter(boolean provUserUpdate) {
        nameMethod="reloadSdkParameter";
        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + provUserUpdate);
        userUpdate=provUserUpdate;
        messaging.reloadSdkParameter();
        // messaging.setConfigParameterFromApp(prvTokenApp,provHostApp);
        // Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "Creating Device: " + userUpdate);
        // messaging.createDeviceParameters();
        // Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "userUpdate: " + userUpdate);
    }

    private String[] getInputArrayFromEditTexts(LinearLayout mParentLayout){
        String[] inputArray = new String [mParentLayout.getChildCount()];
        for (int i = 0; i <inputArray.length ; i++) {
            editText  =(EditText) mParentLayout.getChildAt(i);
            inputArray[i] = editText.getText().toString();
            //Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "X: " + editText.getTag());
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
            skip.setVisibility(View.INVISIBLE);
            // Add EditText to LinearLayout
            if(dataInputList!=null && dataInputList.size()>0) {
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }
                //addEditTextDynamically(linearLayout, myListName);
            Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "dataInputList: "
                        + "With data");
                addEditTextDynamically(linearLayout, dataInputList);
            }else{
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }
                // update config, create device and user
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "dataInputList: "
                        + "No data");
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
                messaging.setConfiguration(scanContent);
                // String[] prvHandlerMessage=scanContent.split(":%:");
                // prvTokenApp=prvHandlerMessage[0];
                // provHostApp=prvHandlerMessage[1];
                // Log.d(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "Token: " +prvTokenApp+" Host "+provHostApp);
                progressBar.setVisibility(View.VISIBLE);
                useQrScan=true;
                // Messaging.fetchFields(getApplicationContext(),prvTokenApp,provHostApp);
            }else{
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_LONG).show();
                useQrScan=false;
                Log.d(TAG, "INFO: " + CLASS_TAG + ": "
                        + nameMethod + ": " +"Cancelled QR Scan "+useQrScan);
            }

        } else {
            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_SHORT).show();
        }


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
            String alertMessage = getResources().getString(getResources().getIdentifier(intent.getAction(), "string", getPackageName()));
            //Toast.makeText(getApplicationContext(), alertMessage, Toast.LENGTH_LONG).show();
            Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ":   " + alertMessage);
            boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
            Log.d(TAG,"ERROR: "+CLASS_TAG+": "+nameMethod+": Has error:  "+ hasError);
            if (!hasError) {
                Serializable dataSdk=intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
                String data=intent.getStringExtra(Messaging.INTENT_EXTRA_DATA_FIELD);
                if(intent.getAction().equals(Messaging.ACTION_FETCH_FIELDS) && data!=null){
                Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": data:  "+ data);

                    try {
                        JSONObject temp=new JSONObject(data);
                        JSONObject temp1=temp.getJSONObject(Messaging.MESSAGING_DATA);
                        JSONArray arr = temp1.getJSONArray(Messaging.FETCH_FIELDS_COLUMNS);

                        JSONArray sortedJsonArray =getJsonArraySorted(arr);
                    Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": sortedJsonArray:  "
                    + sortedJsonArray.toString());
                    if(arr.length()>0) {
                        dataInputList = new ArrayList<HashMap<String, String>>();
                        for (int i = 0; i < sortedJsonArray.length(); i++) {
                            dataInput = new HashMap<String, String>();
                            if (sortedJsonArray.getJSONObject(i).has("label")) {
                                dataInput.put("label", sortedJsonArray.getJSONObject(i).getString("label"));
                            }
                            if (sortedJsonArray.getJSONObject(i).has("type")) {
                                dataInput.put("type", sortedJsonArray.getJSONObject(i).getString("type"));
                            }
                            if (sortedJsonArray.getJSONObject(i).has("field")) {
                                dataInput.put("field", sortedJsonArray.getJSONObject(i).getString("field"));
                            }
                            if (sortedJsonArray.getJSONObject(i).has("name")) {
                                dataInput.put("name", sortedJsonArray.getJSONObject(i).getString("name"));
                            }
                            Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": dataInput:  "
                                    + dataInput);
                            dataInputList.add(dataInput);

                        }

                        Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": dataInputList:  "
                                + dataInputList.size());
                        showLinearData();
                    }else{
                        Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Don't have Columns:  ");
                        reloadSdkParameter(false);
                    }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        reloadSdkParameter(false);
                    }

                }else if(intent.getAction().equals(Messaging.ACTION_REGISTER_DEVICE)&& dataSdk!=null) {
                    MessagingDevice messagingDevice = (MessagingDevice) dataSdk;

                    Log.d(TAG, "Debug: " + CLASS_TAG + ": " + nameMethod + ": Data Register:  " + dataSdk +" userUpdate " + userUpdate);
                    if (userUpdate) {
                        if(onetimeFlagUser) {
                            Messaging.fetchUser(getApplicationContext(), true);
                            onetimeFlagUser=false;
                        }
                    } else {
                        if (useQrScan) {
                            goToMainActivity();
                            useQrScan=false;
                        }
                    }
                    skip.setVisibility(View.VISIBLE);
                    skip.setEnabled(true);

                }else if(intent.getAction().equals(Messaging.ACTION_FETCH_USER) && dataSdk!=null) {
                        messagingUser = (MessagingUser) dataSdk;

                        Log.d(TAG, "Debug: " + CLASS_TAG + ": " + nameMethod + "Action:  " + intent.getAction()+" "+dataSdk+" QR "+useQrScan);
                        if(useQrScan){
                            if(onetimeFlag) {
                                // sendUserUpdateData(dataInputToSendUser);
                                messaging.sendUserUpdateData(dataInputToSendUser);
                                onetimeFlag=false;
                            }
                        }

                }else if(intent.getAction().equals(Messaging.ACTION_SAVE_USER)&& dataSdk!=null) {
                        messagingUser = (MessagingUser) dataSdk;

                        Log.d(TAG, "Debug: " + CLASS_TAG + ": " + nameMethod + ": Save User:  " + dataSdk +" "+intent.getAction()+" QR "+useQrScan);
                        if(useQrScan) {
                            goToMainActivity();
                            useQrScan=false;
                        }

                }else{
                Toast.makeText(getApplicationContext(),": An error occurred on action:  "
                        +alertMessage,Toast.LENGTH_LONG).show();
                Log.e(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": An error occurred on action:  "
                            + alertMessage);
                    if(progressBar.isShown()){
                        progressBar.setVisibility(View.GONE);
                    }
                }

            } else {
                Log.e(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": An error occurred on action:  "
                        + alertMessage);
                Toast.makeText(getApplicationContext(),"An error occurred on action "
                        +alertMessage,Toast.LENGTH_LONG).show();
                if(intent.getAction().equals(Messaging.ACTION_REGISTER_DEVICE)){
                    skip.setText("Error Register Device please Get Started ");
                    skip.setEnabled(false);
                }

                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }

            }

        }

    };

    private void goToMainActivity() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG, "Debug: " + CLASS_TAG + ": " + nameMethod);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Static.extras = extras;
        }
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MESSAGING_LOGIN", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("IS_LOGGED", true).apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }



    public JSONArray getJsonArraySorted(JSONArray arr){
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jsonObject= null;
            try {
//                jsonObject = arr.getJSONObject(i);
//                jsonObject.put("order",order[i]);
                jsonValues.add(arr.getJSONObject(i));
                //jsonValues.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private static final String KEY_ORDER = "order";
            @Override
            public int compare(JSONObject a, JSONObject b) {
//          String valA = new String();
//          String valB = new String();
                int valA = 0;
                int valB = 0;

                try {
                    valA = (int) a.get(KEY_ORDER);
                    valB = (int) b.get(KEY_ORDER);
                }
                catch (JSONException e) {
                    //do something
                    //e.printStackTrace();

                }

                if(valA==valB) {
                    return 0;
                }else{
                    if(valA<valB){
                        return -1;
                    }else{
                        return 1;
                    }
                }
            }
        });

        for (int i = 0; i < arr.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

}