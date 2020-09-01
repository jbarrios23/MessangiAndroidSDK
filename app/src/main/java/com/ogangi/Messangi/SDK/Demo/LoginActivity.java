package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.messaging.sdk.Messaging;
import com.ogangi.Messangi.SDK.Demo.scanqr.CaptureActivityAnyOrientation;
import com.ogangi.Messangi.SDK.Demo.scanqr.SmallCaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    public static String CLASS_TAG=LoginActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    public Button button_get_started;
    public TextView scan_title;
    public ImageView imageView;
    private String nameMethod;
    public LinearLayout linearLayout;
    public EditText customField,customEmail,customPhone;

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
        customField=findViewById(R.id.editText_custom_field);
        customEmail=findViewById(R.id.editText_email);
        customPhone=findViewById(R.id.editText_phone);

        button_get_started.setText(getResources().getText(R.string.get_started));
        imageView.setVisibility(View.VISIBLE);

        button_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button_get_started.getText().equals(getResources().getText(R.string.get_finish))){
                    //LoginActivity.this.finish();
                    showLinearData("", "");



                }else{
                    if(button_get_started.getText().equals(getResources().getText(R.string.get_continue))){

                        getDataFromEditText();
                    }else{
                        callScanQr();
                        button_get_started.setText(getResources().getText(R.string.get_finish));
                        scan_title.setVisibility(View.VISIBLE);
                        //imageView.setImageResource(R.drawable.common_google_signin_btn_text_light);
                        imageView.setVisibility(View.INVISIBLE);
                        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + button_get_started.getText());
                    }

                }


            }
        });
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

    private void getDataFromEditText() {
        String provCustomField=customField.getText().toString();
        String provCustomEmail=customEmail.getText().toString();
        String provCustomPhone=customPhone.getText().toString();
        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + provCustomField
                +" Email "+provCustomEmail+" Phone "+provCustomPhone);
        LoginActivity.this.finish();

    }

    public void showLinearData(String prvTokenApp, String provHostApp) {
        if(button_get_started.getText().equals(getResources().getText(R.string.get_continue))){


        }else {
            imageView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            scan_title.setVisibility(View.VISIBLE);
            scan_title.setTextSize(25);
            scan_title.setText(getResources().getText(R.string.let_get_started_title));
            button_get_started.setText(getResources().getText(R.string.get_continue));
            customField.setText(prvTokenApp);
            customEmail.setText(prvTokenApp);
            customPhone.setText(provHostApp);
            Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + button_get_started.getText());
        }

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
                showLinearData(prvTokenApp,provHostApp);
            }else{
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_LONG).show();
                Log.d(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " +"Cancelled");
            }



        } else {
            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_SHORT).show();
        }


    }

    private static class HttpRequestTaskGet extends AsyncTask<Void,Void,String> {

        public String provAppHost;
        private String provAppToken;
        private String nameMethod;
        private String provUrl;
        private String server_response;
        @SuppressLint("StaticFieldLeak")


        public HttpRequestTaskGet(String appHost, String appToken) {
            this.provAppHost=appHost;
            this.provAppToken=appToken;

        }


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {

                String authToken= provAppToken;
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                String param ="Bearer "+authToken;
                provUrl= provAppHost+"/fields";

                Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + "GET "+provUrl);
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    Log.e(TAG, "ERROR: " + CLASS_TAG + ": " + nameMethod + " Invalid response from server: " + code);

                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());

                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR: " + CLASS_TAG + ": " + nameMethod + "Exception "+e.getStackTrace().toString());

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try{
                if(!response.equals("")) {
                    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                    Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + "Get Field Successful "+response);
                    JSONObject resp=new JSONObject(response);
                    LoginActivity loginActivity=new LoginActivity();
                    loginActivity.showLinearData(provAppToken,provAppHost);

                }
            }catch (NullPointerException e){
                Log.e(TAG, "ERROR: " + CLASS_TAG + ": " + nameMethod + "Field not Get! NullPointerException "+e.getStackTrace().toString());


            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR: " + CLASS_TAG + ": " + nameMethod + "Field not Get! JSONException "+e.getStackTrace().toString());

            }
        }

        public String readStream(InputStream inputStream) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();

        }
    }
}