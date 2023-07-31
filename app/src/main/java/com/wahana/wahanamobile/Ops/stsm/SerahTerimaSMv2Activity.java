package com.wahana.wahanamobile.Ops.stsm;

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
import com.wahana.wahanamobile.Data.ListMsSTSM;
import com.wahana.wahanamobile.ModelApiOPS.STSM.setDataSTSM;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.Ops.PreviewResultScannerNotFound;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.BuatSJAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sadewa on 09/06/17.
 */

public class SerahTerimaSMv2Activity extends Activity implements ZBarScannerView.ResultHandler {
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
    String ttkManual,employeeCode,sm,ma;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    int totalSTSMsumber;
    int jumlahnotfound;

    ArrayList<String> msFound = new ArrayList<String>();
    ArrayList<String> msNotFound = new ArrayList<String>();

    RequestApiWahanaOps mApiInterface;

    //    Camera mCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serah_terima_sm);
        progressDialog = new ProgressDialog(SerahTerimaSMv2Activity.this, R.style.AppTheme_Dark_Dialog);

        Intent intent = getIntent();
        sm = intent.getStringExtra("sm");
        ma = intent.getStringExtra("ma");

        session = new SessionManager(SerahTerimaSMv2Activity.this);
        employeeCode = session.getID();

        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(SerahTerimaSMv2Activity.this);
//        db.deleteListSTSM(ma);
        ttkList = db.getAllTtkSTSMV2(ma);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SerahTerimaSMv2Activity.this);
                alertDialog.setTitle("Input Manual No Manifest");
                alertDialog.setMessage("Masukkan no. Manifest");

                final EditText input = new EditText(SerahTerimaSMv2Activity.this);
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
                                int ttk = db.checkSTSMV2(ttkManual);

                                int ttksumber = db.checkListSTSMSumber(ttkManual);


                                Log.d("Hasil", ""+ttksumber);
                                if (!isMS(ttkManual)) {
                                    mySong= MediaPlayer.create(SerahTerimaSMv2Activity.this, R.raw.error);
                                    mySong.start();
                                    Toast.makeText(getApplicationContext(), "MS tidak sesuai", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                if (ttksumber != 0) {

                                    if (ttk==0){
//                                    submitForm(ttkManual.trim());

                                        mySong=MediaPlayer.create(SerahTerimaSMv2Activity.this, R.raw.success);
                                        ListMsSTSM ms = new ListMsSTSM();
                                        ms.setNoMs(ttkManual);
                                        ms.setNoMa(ma);
                                        db.addSTSMV2(ms);
                                        mySong.start();
                                        populate();


                                    }else {
                                        mySong= MediaPlayer.create(SerahTerimaSMv2Activity.this, R.raw.error);
                                        Toast.makeText(getApplicationContext(), "No Manifest Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                                        mySong.start();
                                    }

                                }else{

                                    mySong= MediaPlayer.create(SerahTerimaSMv2Activity.this, R.raw.error);
                                    Toast.makeText(getApplicationContext(), "No Manifest Tidak ditemukan di No Ma "+ma, Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder adb=new AlertDialog.Builder(SerahTerimaSMv2Activity.this);
                adb.setTitle("Hapus No Manifest ?");
                adb.setMessage("Apakah anda yakin ingin mendelete No Manifest " + nottk.getText().toString());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteMsSTSM(nottk.getText().toString());
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



                AlertDialog.Builder adb=new AlertDialog.Builder(SerahTerimaSMv2Activity.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin MS yang di scan sudah sesuai ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        sendForm();

                        alertttknotfound();
                    }});

                adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //db.deleteListPickup();
//                                        Toast.makeText(getApplicationContext(), "neutralize", Toast.LENGTH_SHORT).show();
                        Intent pindah = new Intent(SerahTerimaSMv2Activity.this, PreviewResultScanner.class);
                        pindah.putExtra("ma", ma);
                        pindah.putExtra("asal", "stsm");
                        startActivity(pindah);

                    }
                });

                adb.show();
            }
        });

    }


    private void alertttknotfound() {

        jumlahnotfound = db.countListSTSMnotFound();

        AlertDialog.Builder adb = new AlertDialog.Builder(SerahTerimaSMv2Activity.this);
        adb.setTitle("Info");
        adb.setMessage("Ada " + jumlahnotfound + " MS yang belum di Scan ");
        adb.setNegativeButton("Lihat", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent pindah = new Intent(SerahTerimaSMv2Activity.this, PreviewResultScannerNotFound.class);
                pindah.putExtra("ma", ma);
                pindah.putExtra("asal", "stsm");
                startActivity(pindah);
            }
        });

        adb.setPositiveButton("Lanjutkan", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                submitSTSM();

            }
        });

        adb.show();

    }


    private void submitSTSM(){

        msFound = db.getAllMsSTSMFix(ma);
        msNotFound = db.getAllMsSTSMnotFoundFix();

        JSONObject json = new JSONObject();
        try {
            JSONArray jsArrayMsscan = new JSONArray(msFound);
            JSONArray jsArrayMsnotScan = new JSONArray(msNotFound);

            json.put("msfound", jsArrayMsscan);
            json.put("msnotfound", jsArrayMsnotScan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("stsm",""+json);

        Call<setDataSTSM> result = mApiInterface.setDataSTSM(
                "aoSetDataSTSM",
                getString(R.string.partnerid),
                session.getID(),
                json,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        result.enqueue(new Callback<setDataSTSM>() {

            @Override
            public void onResponse(Call<setDataSTSM> call, Response<setDataSTSM> response) {

                Log.d("error", "" + response.body().getText());

                progressDialog.dismiss();

                if (response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();

                        if (vrcode.equals("0")) {

                            db.deleteListSTSM(ma);
                            Intent pindah = new Intent(SerahTerimaSMv2Activity.this, HasilProses.class);
                            pindah.putExtra("proses","stsm");
                            pindah.putExtra("no", response.body().getBtno());
                            startActivity(pindah);
                            finish();


                        } else {


                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(SerahTerimaSMv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();

                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(SerahTerimaSMv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                } else {

                    new SweetAlertDialog(SerahTerimaSMv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }


            }

            @Override
            public void onFailure(Call<setDataSTSM> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(SerahTerimaSMv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });






    }

    public void sendForm(){
        db.deleteListSTSM(ma);
        Intent pindah = new Intent(SerahTerimaSMv2Activity.this, HasilProses.class);
        pindah.putExtra("proses","stsm");
        pindah.putExtra("no","");
        startActivity(pindah);
        finish();
    }

//    private void submitForm(final String ms) {
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        String formattedDate = df.format(c.getTime());
//        JSONObject json = new JSONObject();
//        try {
//            json.put("service", "setDataSTSM");
//            json.put("employeeCode", employeeCode);
//            json.put("tgl", formattedDate);
//            json.put("ms", ms);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        ArrayList<String> parameter = new ArrayList<String>();
//        parameter.add("doSSQL");
//        parameter.add(session.getSessionID());
//        parameter.add("apiGeneric");
//        parameter.add("20");
//        parameter.add("0");
//        parameter.add("jsonp");
//        parameter.add(""+json);
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
//                            Toast.makeText(SerahTerimaSMActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
//                                    mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.success);
//                                    BuatSJ sj = new BuatSJ();
//                                    sj.setTtk(ms);
//                                    db.addSTSM(sj);
//                                    populate();
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            Toast.makeText(SerahTerimaSMActivity.this, "Berhasil Serah Terima",Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }else{
//                                    mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            Toast.makeText(SerahTerimaSMActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }
//                            }else{
//                                mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
//                                runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(SerahTerimaSMActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//                        }else{
//                            progressDialog.dismiss();
//                            mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(SerahTerimaSMActivity.this, text,Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                    }catch (Exception e){
//                        mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(SerahTerimaSMActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                                progressDialog.dismiss();
//                            }
//                        });
//                    }
//                    mySong.start();
//                }
//            }
//        }.execute(parameter);
//
//    }

    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "stsm");
        pindah.putExtra("ma", ma);
        startActivity(pindah);
    }

    private void populate(){
        urut = urut +1;

        totalSTSMsumber = db.countSTSMSumber();

        ttkList.clear();
        ttkList = db.getAllTtkSTSMV2(ma);

        String textJumlah = Integer.toString(ttkList.size());
        jumlahTTK.setText(textJumlah + "/" + totalSTSMsumber);
//        Log.d("Hasil", ""+ttkList);
        adapter = new BuatSJAdapter(SerahTerimaSMv2Activity.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private boolean validateKode() {
        ArrayList<String> ttks = db.getAllMsSTSMFix(ma);
        if (ttks.size()<1) {
            Toast.makeText(this, "Mohon Masukkan No Manifest", Toast.LENGTH_SHORT).show();

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
        AlertDialog.Builder adb = new AlertDialog.Builder(SerahTerimaSMv2Activity.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteListSTSM(ma);

                finish();
            }
        });
        adb.show();
    }

    @Override
    public void handleResult(Result rawResult) {
        int ttk = db.checkSTSM(rawResult.getContents().toUpperCase());

        int ttksumber = db.checkListSTSMSumber(rawResult.getContents().toUpperCase());


        Log.d("Hasil", ""+ttksumber);
        if (!isMS(rawResult.getContents().toUpperCase())) {
            mySong= MediaPlayer.create(SerahTerimaSMv2Activity.this, R.raw.error);
            mySong.start();
            Toast.makeText(getApplicationContext(), "MS tidak sesuai", Toast.LENGTH_SHORT).show();
            return;
        }


        if (ttksumber != 0) {

            if (ttk==0){
//                                    submitForm(ttkManual.trim());

                mySong=MediaPlayer.create(SerahTerimaSMv2Activity.this, R.raw.success);
                ListMsSTSM ms = new ListMsSTSM();
                ms.setNoMs(rawResult.getContents().toUpperCase());
                ms.setNoMa(ma);
                db.addSTSMV2(ms);
                mySong.start();
                populate();


            }else {
                mySong= MediaPlayer.create(SerahTerimaSMv2Activity.this, R.raw.error);
                Toast.makeText(getApplicationContext(), "No Manifest Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                mySong.start();
            }

        }else{

            mySong= MediaPlayer.create(SerahTerimaSMv2Activity.this, R.raw.error);
            Toast.makeText(getApplicationContext(), "No Manifest Tidak ditemukan di No Ma "+ma, Toast.LENGTH_SHORT).show();
            mySong.start();
        }






////        Log.d("Hasil", ""+ttk);
//        if (!isMS(rawResult.getContents().toUpperCase())) {
//            mySong= MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
//            mySong.start();
//            Toast.makeText(getApplicationContext(), "MS tidak sesuai", Toast.LENGTH_SHORT).show();
//        }else{
//            if (ttk==0){
////                submitForm(rawResult.getContents().toUpperCase());
//            }else {
//                mySong= MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
//                Toast.makeText(getApplicationContext(), "No Manifest Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
//                mySong.start();
//            }
//        }
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SerahTerimaSMv2Activity.this);
            }
        }, 2000);
    }

    public boolean isMS(String ms){

        return ms.contains("MS-");
    }

}