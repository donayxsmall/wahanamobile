package com.wahana.wahanamobile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static me.dm7.barcodescanner.core.CameraUtils.getCameraInstance;

public class BarcodeScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    private Button flashButton, flashoff;
    private boolean mFlash;
    private static final String FLASH_STATE = "FLASH_STATE";
    String nokeranjang;

    private final int RESULT_KERANJANG = 02;

//    public static final="RESULT_KERANJANG";
//    Camera mCamera;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner);
//        setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);
        flashButton = (Button) findViewById(R.id.flash);
        flashoff = (Button) findViewById(R.id.flashoff);
        mFlash = false;
        flashButton.setVisibility(View.GONE);
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) == true) {
            flashButton.setVisibility(View.VISIBLE);
            flashButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mScannerView.setFlash(true);
                    flashButton.setVisibility(View.GONE);
                    flashoff.setVisibility(View.VISIBLE);
                }
            });
            flashoff.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mScannerView.setFlash(false);
                    flashoff.setVisibility(View.GONE);
                    flashButton.setVisibility(View.VISIBLE);
                }
            });
        }


        Intent intent = getIntent();
        nokeranjang = intent.getStringExtra("keranjang");

        Log.d("keranjang",""+nokeranjang);


    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(this, "Contents = " + rawResult.getContents() +
//                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();


            Intent pindah = new Intent(BarcodeScannerActivity.this, DeliveredAirways.class);
            pindah.putExtra("ttk", rawResult.getContents());
            pindah.putExtra("keranjang", nokeranjang);
            setResult(RESULT_OK, pindah);
            finish();


        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(BarcodeScannerActivity.this);
//            }
//        }, 2000);
    }
}
