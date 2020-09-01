package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.ogangi.Messangi.SDK.Demo.scanqr.CaptureActivityAnyOrientation;

import org.json.JSONException;
import org.json.JSONObject;

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
                    showLinearData();



                }else{
                    if(button_get_started.getText().equals(getResources().getText(R.string.get_continue))){

                        getDataFromEditText();
                    }else{
                        callScanQr();
                        button_get_started.setText(getResources().getText(R.string.get_finish));
                        scan_title.setVisibility(View.VISIBLE);
                        imageView.setImageResource(R.drawable.common_google_signin_btn_text_light);
                        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + button_get_started.getText());
                    }

                }


            }
        });
    }

    private void callScanQr() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(LoginActivity.this);
        //IntentIntegrator scanIntegrator = IntentIntegrator.forSupportFragment(ScanFragment.this);

        scanIntegrator.setPrompt("Scan");
        scanIntegrator.setBeepEnabled(true);

        //enable the following line if you want QR code
        scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
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

    private void showLinearData() {
        if(button_get_started.getText().equals(getResources().getText(R.string.get_continue))){


        }else {
            imageView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            scan_title.setVisibility(View.VISIBLE);
            scan_title.setTextSize(25);
            scan_title.setText(getResources().getText(R.string.let_get_started_title));
            button_get_started.setText(getResources().getText(R.string.get_continue));
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

            Toast.makeText(this, scanContent + "   type:" + scanFormat, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + scanContent + "    type:" + scanFormat);
            showLinearData();
            //textView.setText(scanContent + "    type:" + scanFormat);
//            try {
//                JSONObject dataReceive= new JSONObject(scanContent);
//                appController.initialize(dataReceive);
//                Navigation.findNavController(root).navigate(R.id.nav_result);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

        } else {
            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_SHORT).show();
        }


    }
}