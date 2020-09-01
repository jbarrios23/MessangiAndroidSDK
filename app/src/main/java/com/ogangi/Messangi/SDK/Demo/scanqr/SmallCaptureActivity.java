package com.ogangi.Messangi.SDK.Demo.scanqr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.ogangi.Messangi.SDK.Demo.R;

public class SmallCaptureActivity extends CaptureActivity {

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_small_capture);
        return (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
    }
}