package com.wahana.wahanamobile.Ops.modaAngkutan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.BuatSJ;
import com.wahana.wahanamobile.ModelApiOPS.ModaAngkutan.cekMsforMA;
import com.wahana.wahanamobile.Ops.manifestSortir.InputNoLabelActivity;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.BuatSJAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sadewa on 10/06/17.
 */

public class ScanMAActivity extends Activity implements ZBarScannerView.ResultHandler {
    private static final String TAG = "MAScanActivity";
    private ZBarScannerView mScannerView;
    private Button flashButton, flashoff, addManual;
    private boolean mFlash;
    private static final String FLASH_STATE = "FLASH_STATE";
    ListView listTTK;
    DatabaseHandler db;
    List<BuatSJ> ttkList;
    BuatSJAdapter adapter;
    TextView jumlahTTK;
    Button previewTTK,submit;
    String ttkManual,employeeCode;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    String tujuan,asal,tujuan2;
    MediaPlayer mySong;
    RequestApiWahanaOps mApiInterface;
    //    Camera mCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moda_angkutan);
        progressDialog = new ProgressDialog(ScanMAActivity.this, R.style.AppTheme_Dark_Dialog);

        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);

        session = new SessionManager(ScanMAActivity.this);
        employeeCode = session.getID();

        Intent intent = getIntent();
//        noKendaraan = intent.getStringExtra("no_kendaraan");
        tujuan = intent.getStringExtra("tujuan");
        asal = intent.getStringExtra("kdkantor");
        tujuan2 = intent.getStringExtra("kdtujuan2");

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(ScanMAActivity.this);
//        db.deleteMAAllFix();
        ttkList = db.getAllMA(tujuan);
        jumlahTTK = (TextView) findViewById(R.id.jumlah_label) ;
        populate();
        urut = 0;
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);
        flashButton = (Button) findViewById(R.id.flash);
        flashoff = (Button) findViewById(R.id.flashoff);
        previewTTK = (Button) findViewById(R.id.btn_preview_ttk);
        addManual = (Button) findViewById(R.id.btn_add_manual);
        submit = (Button) findViewById(R.id.submit);
        mFlash = false;
        flashButton.setVisibility(View.GONE);
        previewTTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preview();
            }
        });

        addManual.setOnClickListener(new View.OnClickListener() {
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScanMAActivity.this);
                alertDialog.setTitle("Input Manual MS");
                alertDialog.setMessage("Masukkan no. MS");

                final EditText input = new EditText(ScanMAActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
//                alertDialog.setIcon(R.drawable.key);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                                ttkManual = input.getText().toString().toUpperCase();
                                int ttk = db.checkMA(ttkManual);
//                                Log.d("Hasil", ""+ttk);
                                if (ttk==0){
//                                    cekMs(ttkManual);

                                    cekMsForMA(ttkManual);

                                }else{
                                    mySong=MediaPlayer.create(ScanMAActivity.this, R.raw.error);
                                    Toast.makeText(getApplicationContext(), "MS Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                                    mySong.start();
                                }
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                            }
                        });
                input.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                alertDialog.show();

            }
        });
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
        listTTK.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                final TextView nottk = (TextView)v.findViewById(R.id.no_ttk_isi);
                AlertDialog.Builder adb=new AlertDialog.Builder(ScanMAActivity.this);
                adb.setTitle("Hapus MS ?");
                adb.setMessage("Apakah anda yakin ingin menghapus MS " + nottk.getText().toString());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteMA(nottk.getText().toString());
                        populate();
                    }});
                adb.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateKode()) {
                    return;
                }
                AlertDialog.Builder adb=new AlertDialog.Builder(ScanMAActivity.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submitForm();
                    }});
                adb.show();
            }
        });

    }


    private void cekMsForMA(final String ms){

        Call<cekMsforMA> result = mApiInterface.cekMsforMA(
                "cekMSForMA",
                getString(R.string.partnerid),
                session.getID(),
                ms,
                tujuan,
                tujuan2,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();


        result.enqueue(new Callback<cekMsforMA>() {

            @Override
            public void onResponse(Call<cekMsforMA> call, Response<cekMsforMA> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();

                        if (vrcode.equals("0")) {

                            BuatSJ sj = new BuatSJ();
                            sj.setTtk(ms);
                            sj.setTujuan(tujuan);
                            try{
                                db.addMA(sj);
                                populate();
                                mySong= MediaPlayer.create(ScanMAActivity.this, R.raw.success);
                                mySong.start();
                            }catch (SQLiteException e){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Manifest Sortir Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                mySong= MediaPlayer.create(ScanMAActivity.this, R.raw.error);
                                mySong.start();
                            }

                        } else {

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(ScanMAActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();


                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(ScanMAActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(ScanMAActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<cekMsforMA> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(ScanMAActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }



//    private void cekMs(final String ms){
//        ArrayList<String> parameter = new ArrayList<String>();
//        parameter.add("doSSQL");
//        parameter.add(session.getSessionID());
//        parameter.add("cekMS");
//        parameter.add("0");
//        parameter.add("0");
//        parameter.add("ms");
//        parameter.add(ms);
//        parameter.add("tujuan");
//        parameter.add("%"+asal+"-"+tujuan+"%");
//
//        new SoapClientMobile(){
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                Log.i(TAG, "onPreExecute");
//                progressDialog.setIndeterminate(true);
//                progressDialog.setMessage("Authenticating...");
//                progressDialog.show();
//            }
//
//            @Override
//            protected void onPostExecute(SoapObject result) {
//                super.onPostExecute(result);
//                Log.d("hasil soap", ""+result);
//                if(result==null){
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(ScanMAActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
//                        }
//                    });
//                }else {
//                    try{
//                        final String text = result.getProperty(1).toString();
//                        Log.d("hasil soap data", ""+text);
//                        if (text.equals("OK")) {
//                            progressDialog.dismiss();
//                            String so = result.getProperty(2).toString();
//
//                            JSONObject jsonObj = new JSONObject(so);
//                            JSONArray data = jsonObj.getJSONArray("data");
//                            if (data.length()>1){
//                                final JSONArray d = data.getJSONArray(1);
//                                Log.d("hasil soap data", "aaa "+d.getString(10));
//                                if (d.getString(6) == "null"){
//                                    mySong= MediaPlayer.create(ScanMAActivity.this, R.raw.success);
//                                    mySong.start();
//                                    BuatSJ sj = new BuatSJ();
//                                    sj.setTtk(ms);
//                                    sj.setTujuan(tujuan);
//                                    db.addMA(sj);
//                                    populate();
//                                }else {
//                                    if (d.getString(10) == "null"){
//                                        mySong= MediaPlayer.create(ScanMAActivity.this, R.raw.error);
//                                        mySong.start();
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                Toast.makeText(ScanMAActivity.this, "MS sudah dibuatkan SM",Toast.LENGTH_LONG).show();
//                                            }
//                                        });
//                                    }else{
//                                        BuatSJ sj = new BuatSJ();
//                                        sj.setTtk(ms);
//                                        sj.setTujuan(tujuan);
//                                        try{
//                                            db.addMA(sj);
//                                            populate();
//                                            mySong= MediaPlayer.create(ScanMAActivity.this, R.raw.success);
//                                            mySong.start();
//                                        }catch (SQLiteException e){
//                                            runOnUiThread(new Runnable() {
//                                                public void run() {
//                                                    Toast.makeText(getApplicationContext(), "Manifest Sortir Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                            mySong= MediaPlayer.create(ScanMAActivity.this, R.raw.error);
//                                            mySong.start();
//                                        }
//                                    }
//                                }
//                            }else{
//                                mySong= MediaPlayer.create(ScanMAActivity.this, R.raw.error);
//                                mySong.start();
//                                runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(ScanMAActivity.this, "Mohon Cek Kembali No MS Anda",Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//                        }else{
//                            progressDialog.dismiss();
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(ScanMAActivity.this, text,Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                    }catch (Exception e){
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(ScanMAActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                                progressDialog.dismiss();
//                            }
//                        });
//                    }
//                }
//            }
//        }.execute(parameter);
//    }

    private void submitForm() {

        if (!validateKode()) {
            return;
        }

        Intent pindah = new Intent(this, AddDataMAActivity.class);
//        pindah.putExtra("no_kendaraan", noKendaraan);
        pindah.putExtra("tujuan", tujuan);
        pindah.putExtra("asal", asal);
        pindah.putExtra("tujuan2", tujuan2);
        startActivity(pindah);
        finish();
    }

    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "ma");
//        pindah.putExtra("noKendaraan",noKendaraan);
        pindah.putExtra("tujuan",tujuan);
        startActivity(pindah);
    }

    private void populate(){
        urut = urut +1;
        ttkList.clear();
        ttkList = db.getAllMA(tujuan);
        String textJumlah = Integer.toString(ttkList.size());
        jumlahTTK.setText(textJumlah);
//        Log.d("Hasil", ""+ttkList);
        adapter = new BuatSJAdapter(ScanMAActivity.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private boolean validateKode() {
        ArrayList<String> ttks = db.getAllMAFix(tujuan);
        if (ttks.size()<1) {
            Toast.makeText(this, "Mohon Masukkan Data nya", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
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
        int ttk = db.checkMA(rawResult.getContents());
//        ttkList = db.getAllMA(tujuan);
        if (ttk==0){
//            cekMs(rawResult.getContents().toUpperCase());

            cekMsForMA(rawResult.getContents().toUpperCase());
        }else{
            mySong= MediaPlayer.create(ScanMAActivity.this, R.raw.error);
            Toast.makeText(getApplicationContext(), "Manifest Sortir Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
            mySong.start();
        }
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScanMAActivity.this);
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb=new AlertDialog.Builder(ScanMAActivity.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }});
        adb.show();
    }
}
