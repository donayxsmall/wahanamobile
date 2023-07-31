package com.wahana.wahanamobile.Ops.stms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.wahana.wahanamobile.Data.ListTtkSTMS;
import com.wahana.wahanamobile.ModelApiOPS.STMS.setDataSTMS;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.Ops.PreviewResultScannerNotFound;
import com.wahana.wahanamobile.Ops.stsm.SerahTerimaSMActivity;
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

import static android.content.ContentValues.TAG;

/**
 * Created by sadewa on 07/06/17.
 */

public class SerahTerimaMSv2Activity extends Activity implements ZBarScannerView.ResultHandler {
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
    String ttkManual,employeeCode,ms;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    int totalSTMSsumber;
    int jumlahnotfound;

    ArrayList<String> ttkFound = new ArrayList<String>();
    ArrayList<String> ttkNotFound = new ArrayList<String>();

    RequestApiWahanaOps mApiInterface;

    //    Camera mCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serah_terima_ms);
        progressDialog = new ProgressDialog(SerahTerimaMSv2Activity.this, R.style.AppTheme_Dark_Dialog);

        Intent intent = getIntent();
        ms = intent.getStringExtra("ms");

        session = new SessionManager(SerahTerimaMSv2Activity.this);
        employeeCode = session.getID();

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(SerahTerimaMSv2Activity.this);
        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);

//        db.deleteSTMS();
        ttkList = db.getAllTtkSTMSScan(ms);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SerahTerimaMSv2Activity.this);
                alertDialog.setTitle("Input Manual TTK");
                alertDialog.setMessage("Masukkan no. TTK");

                final EditText input = new EditText(SerahTerimaMSv2Activity.this);
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

                                int ttk = db.checkSTMSScan(ttkManual,ms);
                                int ttksumber = db.checkListSTMSSumber(ttkManual);
                                Log.d("Hasil", ""+ttk);

                                if (ttksumber != 0) {

                                    if (ttk==0){
                                        mySong= MediaPlayer.create(SerahTerimaMSv2Activity.this, R.raw.success);
                                        mySong.start();
                                        ListTtkSTMS sj = new ListTtkSTMS();
                                        sj.setNoTtk(ttkManual);
                                        sj.setNoMs(ms);
                                        db.addSTMSScan(sj);
                                        populate();
                                    }else {
                                        mySong= MediaPlayer.create(SerahTerimaMSv2Activity.this, R.raw.error);
                                        mySong.start();
                                        Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    mySong= MediaPlayer.create(SerahTerimaMSv2Activity.this, R.raw.error);
                                    Toast.makeText(getApplicationContext(), "No TTK Tidak ditemukan di No Ms "+ms, Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder adb=new AlertDialog.Builder(SerahTerimaMSv2Activity.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                final int positionToRemoveDB = position + 1;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKSTMS(nottk.getText().toString());
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
                AlertDialog.Builder adb=new AlertDialog.Builder(SerahTerimaMSv2Activity.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        submitForm();
                        alertttknotfound();

                    }});
                adb.show();
            }
        });

    }


    private void alertttknotfound(){

        jumlahnotfound = db.countListSTMSnotFound(ms);

        AlertDialog.Builder adb = new AlertDialog.Builder(SerahTerimaMSv2Activity.this);
        adb.setTitle("Info");
        adb.setMessage("Ada " + jumlahnotfound + " TTK yang belum di Scan ");
        adb.setNegativeButton("Lihat", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent pindah = new Intent(SerahTerimaMSv2Activity.this, PreviewResultScannerNotFound.class);
                pindah.putExtra("ms", ms);
                pindah.putExtra("asal", "stms");
                startActivity(pindah);
            }
        });

        adb.setPositiveButton("Lanjutkan", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                submitSTMS();

            }
        });

        adb.show();

    }


    private void submitSTMS(){

        ttkFound = db.getAllTtkSTMSFixScan(ms);
        ttkNotFound = db.getAllTtkSTMSnotFoundFix(ms);

        JSONObject json = new JSONObject();
        try {
            JSONArray jsArrayTtkscan = new JSONArray(ttkFound);
            JSONArray jsArrayTtknotScan = new JSONArray(ttkNotFound);

            json.put("ttkfound", jsArrayTtkscan);
            json.put("ttknotfound", jsArrayTtknotScan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("stms",""+json);
        Log.d("stms1",""+String.valueOf(ttkFound.size()));


        Call<setDataSTMS> result = mApiInterface.setDataSTMS(
                "aoSetDataSTMS",
                getString(R.string.partnerid),
                session.getID(),
                json,
                String.valueOf(ttkFound.size()),
                ms,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        result.enqueue(new Callback<setDataSTMS>() {

            @Override
            public void onResponse(Call<setDataSTMS> call, Response<setDataSTMS> response) {

                Log.d("error", "" + response.body().getText());

                progressDialog.dismiss();

                if (response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();

                        if (vrcode.equals("0")) {

                            db.deleteListSTMS(ms);
                            Intent pindah = new Intent(SerahTerimaMSv2Activity.this, HasilProses.class);
                            pindah.putExtra("proses","stms");
                            pindah.putExtra("no",response.body().getBtno());
                            startActivity(pindah);
                            finish();


                        } else {


                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(SerahTerimaMSv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();

                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(SerahTerimaMSv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                } else {

                    new SweetAlertDialog(SerahTerimaMSv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }


            }

            @Override
            public void onFailure(Call<setDataSTMS> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(SerahTerimaMSv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });




    }

//    private void submitForm() {
//
//        if (!validateKode()) {
//            return;
//        }
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        String formattedDate = df.format(c.getTime());
//        ArrayList<String> ttks = db.getAllTtkSTMSFix();
//        JSONObject json = new JSONObject();
//        try {
//            json.put("service", "setDataSTMS");
//            json.put("ms", ms);
//            json.put("employeeCode", employeeCode);
//            JSONArray jsArray = new JSONArray(ttks);
//            json.put("tgl", formattedDate);
//            json.put("jumlah",ttks.size());
//            json.put("ttk", jsArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.d("hasil json", ""+json);
//
//        ArrayList<String> parameter = new ArrayList<String>();
//        parameter.add("doSSQL");
//        parameter.add(session.getSessionID());
//        parameter.add("apiGeneric");
//        parameter.add("20");
//        parameter.add("0");
//        parameter.add("jsonp");
//        parameter.add(""+json);
//        progressDialog.setCancelable(false);
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
//                            Toast.makeText(SerahTerimaMSActivity.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
//                        }
//                    });
//                }else {
//                    try{
//                        final String text = result.getProperty(1).toString();
//                        if (text.equals("OK")) {
//                            progressDialog.dismiss();
//                            String so = result.getProperty(2).toString();
//
//                            JSONObject jsonObj = new JSONObject(so);
//                            JSONArray data = jsonObj.getJSONArray("data");
//                            Log.d("hasil soap data", ""+data);
//                            if (data.length()>1){
//                                final JSONArray d = data.getJSONArray(2);
////                                Log.d("hasil soap data", ""+d.getJSONObject(1).getString("status"));
//                                if (d.getJSONObject(1).getString("status").equals("1")){
//                                    db.deleteSTMS();
//                                    JSONArray msd = data.getJSONArray(1);
//                                    String msId = msd.getString(0);
//                                    String stsm = msd.getString(1);
//                                    Intent pindah = new Intent(SerahTerimaMSActivity.this, HasilProses.class);
//                                    pindah.putExtra("proses","stms");
//                                    pindah.putExtra("no",stsm);
//                                    startActivity(pindah);
//                                    finish();
//                                }else{
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            Toast.makeText(SerahTerimaMSActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }
//                            }else{
//                                runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(SerahTerimaMSActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//                        }else{
//                            progressDialog.dismiss();
//                            Intent pindah = new Intent(SerahTerimaMSActivity.this, HasilError.class);
//                            pindah.putExtra("proses","stms");
//                            pindah.putExtra("no", text);
//                            startActivity(pindah);
//                        }
//                    }catch (Exception e){
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(SerahTerimaMSActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                                progressDialog.dismiss();
//                            }
//                        });
//                    }
//                }
//            }
//        }.execute(parameter);
//
//
//    }

    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "stmsv2");
        pindah.putExtra("ms", ms);
        startActivity(pindah);
    }

    private void populate(){
        urut = urut +1;

        totalSTMSsumber = db.countSTMSSumber();

        ttkList.clear();
        ttkList = db.getAllTtkSTMSScan(ms);

        String textJumlah = Integer.toString(ttkList.size());
        jumlahTTK.setText(textJumlah + "/" + totalSTMSsumber);

        Log.d("Hasil", ""+ttkList);
        adapter = new BuatSJAdapter(SerahTerimaMSv2Activity.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private boolean validateKode() {
        ArrayList<String> ttks = db.getAllTtkSTMSFixScan(ms);
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

        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
//                progressDialog.cancel();
                populate();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 300);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(SerahTerimaMSv2Activity.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteListSTMS(ms);

                finish();
            }
        });
        adb.show();
    }

    @Override
    public void handleResult(Result rawResult) {
        int ttk = db.checkSTMSScan(rawResult.getContents().toUpperCase(),ms);
        int ttksumber = db.checkListSTMSSumber(rawResult.getContents().toUpperCase());
        Log.d("Hasil", ""+ttk);

        if (ttksumber != 0) {

            if (ttk==0){
                mySong= MediaPlayer.create(SerahTerimaMSv2Activity.this, R.raw.success);
                mySong.start();
                ListTtkSTMS sj = new ListTtkSTMS();
                sj.setNoTtk(rawResult.getContents().toUpperCase());
                sj.setNoMs(ms);
                db.addSTMSScan(sj);
                populate();
            }else {
                mySong= MediaPlayer.create(SerahTerimaMSv2Activity.this, R.raw.error);
                mySong.start();
                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
            }

        }else{
            mySong= MediaPlayer.create(SerahTerimaMSv2Activity.this, R.raw.error);
            Toast.makeText(getApplicationContext(), "No TTK Tidak ditemukan di No Ms "+ms, Toast.LENGTH_SHORT).show();
            mySong.start();
        }


//        }
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SerahTerimaMSv2Activity.this);
            }
        }, 2000);
    }

    public boolean isTTK(String ttk){

        return ttk.matches("^([A-Z][A-Z][A-Z])([0-9][0-9][0-9][0-9][0-9])$");
    }
}
