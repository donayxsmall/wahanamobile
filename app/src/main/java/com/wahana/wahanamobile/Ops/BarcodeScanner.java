package com.wahana.wahanamobile.Ops;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wahana.wahanamobile.Ops.ApprovalSJ.ListApprovalSJ;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.model.DataSJ;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import com.google.zxing.Result;


public class BarcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private Button flashButton, flashoff;
    private boolean mFlash;
    private static final String FLASH_STATE = "FLASH_STATE";

    private static final String TAG = "Scanner";
    ProgressDialog progressDialog;
    MediaPlayer mySong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        progressDialog = new ProgressDialog(BarcodeScanner.this, R.style.AppTheme_Dark_Dialog);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
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
    }



    private void checksj(final String sj){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add("12345");
        parameter.add("checksjApproval");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBTNO");
        parameter.add(sj);

        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil data SJ...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                progressDialog.dismiss();
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(BarcodeScanner.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String ttk = isi.getString(0);
                                            String statussj = isi.getString(1);

                                            if(statussj.equals("3")){
                                                mySong= MediaPlayer.create(BarcodeScanner.this, R.raw.error);
                                                mySong.start();
//                                                Toast.makeText(BarcodeScanner.this, sj+" ini Sudah di Approve",Toast.LENGTH_LONG).show();

                                                AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeScanner.this);
                                                builder.setTitle("Scan Result");
                                                builder.setMessage(sj+" ini Sudah di Approve");
                                                final AlertDialog alert1 = builder.create();
                                                alert1.show();

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                       alert1.dismiss();
                                                    }
                                                }, 1000);


                                            }else{
                                                mySong=MediaPlayer.create(BarcodeScanner.this, R.raw.success);
                                                Intent pindah = new Intent(BarcodeScanner.this, ListApprovalSJ.class);
                                                pindah.putExtra("ttk", sj);
                                                startActivity(pindah);

                                                mySong.start();
                                            }

                                        }

                                    }


                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(BarcodeScanner.this, text,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        mySong= MediaPlayer.create(BarcodeScanner.this, R.raw.error);
                                        mySong.start();
                                        Toast.makeText(BarcodeScanner.this, sj+" Bukan No SJ",Toast.LENGTH_LONG).show();
//                                        Intent pindah = new Intent(ListApprovalSJ.this, MainActivity.class);
//                                        startActivity(pindah);
//                                        finish();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(BarcodeScanner.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(BarcodeScanner.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }







    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler((ZXingScannerView.ResultHandler) this);
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

        checksj(rawResult.getText());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(BarcodeScanner.this);
            }
        }, 1000);

//        Intent pindah = new Intent(BarcodeScanner.this, ListApprovalSJ.class);
//        pindah.putExtra("ttk", rawResult.getContents());
//        setResult(RESULT_OK, pindah);
//        finish();
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
