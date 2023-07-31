package com.wahana.wahanamobile.Ops.manifestSortir;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.BuatSJAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static android.content.ContentValues.TAG;

/**
 * Created by sadewa on 09/06/17.
 */

public class ManifestSortirActivity extends Activity implements ZBarScannerView.ResultHandler {
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
    String ttkManual,employeeCode,kodekotatujuanMS;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    int koliMatang;
    //    Camera mCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifestsortir_scanner);
        progressDialog = new ProgressDialog(ManifestSortirActivity.this, R.style.AppTheme_Dark_Dialog);

        Intent intent = getIntent();
        kodekotatujuanMS = intent.getStringExtra("kodekotatujuanMS");


        session = new SessionManager(ManifestSortirActivity.this);
        employeeCode = session.getID();

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(ManifestSortirActivity.this);
        ttkList = db.getAllTtkMS();
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



                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManifestSortirActivity.this);
                alertDialog.setTitle("Input Manual TTK");
                alertDialog.setMessage("Masukkan no. TTK");

                final EditText input = new EditText(ManifestSortirActivity.this);
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
                                ttkList = db.getAllTtkMS();
                                int ttk = db.checkMS(ttkManual);
                                Log.d("Hasil", ""+ttk);
//                                if (!isTTK(ttkManual)) {
//                                    mySong= MediaPlayer.create(ManifestSortirActivity.this, R.raw.error);
//                                    mySong.start();
//                                    Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
                                if (ttkList.size()>=20){
                                    mySong=MediaPlayer.create(ManifestSortirActivity.this, R.raw.error);
                                    Toast.makeText(getApplicationContext(), "Jumlah Maksimal 20 ", Toast.LENGTH_SHORT).show();
                                }else {
                                     if (ttk==0){
//                                         mySong=MediaPlayer.create(ManifestSortirActivity.this, R.raw.success);
//                                         BuatSJ sj = new BuatSJ();
//                                         sj.setTtk(ttkManual);
//                                         db.addMS(sj);
//                                         populate();

                                         cekttkms(ttkManual);

                                     }else{
                                         mySong=MediaPlayer.create(ManifestSortirActivity.this, R.raw.error);
                                         Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                                         mySong.start();
                                     }
                                }
//                                mySong.start();
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
                AlertDialog.Builder adb=new AlertDialog.Builder(ManifestSortirActivity.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKMS(nottk.getText().toString());
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
                final InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManifestSortirActivity.this);
                alertDialog.setTitle("Input Koli Matang");
                alertDialog.setMessage("Masukkan jumlah koli matang ");

                final EditText input = new EditText(ManifestSortirActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                                String isi = input.getText().toString();
                                if (isi.isEmpty()){
                                    Toast.makeText(getApplicationContext(), "Mohon isi jumlah koli matang", Toast.LENGTH_SHORT).show();
                                }else {
                                    koliMatang = Integer.parseInt(isi);
                                    if(koliMatang<1){
                                        Toast.makeText(getApplicationContext(), "Jumlah koli matang minimal 1", Toast.LENGTH_SHORT).show();
                                    }else{
                                        AlertDialog.Builder adb=new AlertDialog.Builder(ManifestSortirActivity.this);
                                        adb.setTitle("Confirmation ?");
                                        adb.setMessage("Apakah anda yakin ?");
                                        adb.setNegativeButton("Cancel", null);
                                        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                submitForm();
                                            }});
                                        adb.show();
                                    }
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

    }

    private void submitForm() {
        if (!validateKode()) {
            return;
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        ArrayList<String> ttks = db.getAllTtkMSFix();
        JSONObject json = new JSONObject();
        try {
            json.put("service", "setDataMS");
            json.put("kodeTujuan", kodekotatujuanMS);
            json.put("employeeCode", employeeCode);
            JSONArray jsArray = new JSONArray(ttks);
            json.put("tgl", formattedDate);
            json.put("jumlah",ttks.size());
            json.put("ttk", jsArray);
            json.put("koliMatang", koliMatang);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("hasil json", ""+json);

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add(""+json);
        progressDialog.setCancelable(false);
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ManifestSortirActivity.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty(0).toString();
                        final String text = result.getProperty(1).toString();
                        if (code.equals("1")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(2);
//                                Log.d("hasil soap data", ""+d.getJSONObject(1).getString("status"));
                                if (d.getJSONObject(1).getString("status").equals("1")){
                                    db.deleteMS();
                                    Intent pindah = new Intent(ManifestSortirActivity.this, HasilProses.class);
                                    pindah.putExtra("proses","ms");
                                    JSONArray msd = data.getJSONArray(1);
                                    String msId = msd.getString(0);
                                    String ms = msd.getString(1);
                                    pindah.putExtra("no", ms);
                                    pindah.putExtra("msId", msId);
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(ManifestSortirActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(ManifestSortirActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(ManifestSortirActivity.this, HasilError.class);
                            pindah.putExtra("proses","ms");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ManifestSortirActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }



    private void cekttkms(final String ttk) {
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("cekAPIGetTTKInfoForMS");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ttk");
        parameter.add(ttk);
        parameter.add("kodetujuan");
        parameter.add(kodekotatujuanMS);
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ManifestSortirActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){

                                final JSONArray d = data.getJSONArray(1);

                                String approvesb=d.getString(2);
                                String nomorsb=d.getString(3);
                                String statusaprov=d.getString(4);


                                Log.d("mslog",""+approvesb+"|"+nomorsb+"|"+statusaprov);


                                if(Integer.parseInt(approvesb) > 0 && nomorsb.equals("null")) {

                                    Toast.makeText(ManifestSortirActivity.this, "TTK bisa dibuatkan MS", Toast.LENGTH_LONG).show();

                                    mySong = MediaPlayer.create(ManifestSortirActivity.this, R.raw.success);
                                    BuatSJ sj = new BuatSJ();
                                    sj.setTtk(ttk);
                                    db.addMS(sj);
                                    populate();

                                    mySong.start();

                                }else{

                                    if(Integer.parseInt(statusaprov) == 1 || Integer.parseInt(statusaprov) == 2){
                                        Toast.makeText(ManifestSortirActivity.this, "TTK Termasuk Selisih Berat : Status belum di approve oleh kaops",Toast.LENGTH_LONG).show();
                                    }else if(Integer.parseInt(statusaprov) == 4){
                                        Toast.makeText(ManifestSortirActivity.this, "TTK Termasuk Selisih Berat : Status Reject Harap Dibuat Ulang",Toast.LENGTH_LONG).show();
                                    }else{

                                        Toast.makeText(ManifestSortirActivity.this, "TTK bisa dibuatkan MS", Toast.LENGTH_LONG).show();

                                        mySong = MediaPlayer.create(ManifestSortirActivity.this, R.raw.success);
                                        BuatSJ sj = new BuatSJ();
                                        sj.setTtk(ttk);
                                        db.addMS(sj);
                                        populate();

                                        mySong.start();
                                    }

                                }

//                                Intent pindah = new Intent(InputKeranjang.this, KeranjangScanner.class);
//                                pindah.putExtra("nokeranjang",input_keranjang.getText().toString().toUpperCase());
//                                startActivity(pindah);
//                                finish();

                            }else{
                                mySong=MediaPlayer.create(ManifestSortirActivity.this, R.raw.error);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(ManifestSortirActivity.this, "TTK tidak bisa dibuatkan MS",Toast.LENGTH_LONG).show();
                                    }
                                });

                                mySong.start();
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(ManifestSortirActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ManifestSortirActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }




    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "ms");
        startActivity(pindah);
    }

    private void populate(){
        urut = urut +1;
        ttkList.clear();
        ttkList = db.getAllTtkMS();
        String textJumlah = Integer.toString(ttkList.size());
        jumlahTTK.setText(textJumlah);
        Log.d("Hasil", ""+ttkList);
        adapter = new BuatSJAdapter(ManifestSortirActivity.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private boolean validateKode() {
        ArrayList<String> ttks = db.getAllTtkMSFix();
        if (ttks.size()<1) {
            Toast.makeText(this, "Mohon Masukkan TTK nya", Toast.LENGTH_SHORT).show();

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

        int ttk = db.checkMS(rawResult.getContents());
        Log.d("Hasil", ""+ttk);
        ttkList = db.getAllTtkMS();
//        if (!isTTK(rawResult.getContents().toUpperCase())) {
//            mySong= MediaPlayer.create(ManifestSortirActivity.this, R.raw.error);
//            mySong.start();
//            Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
//        }
        if (ttkList.size()>=20){
            mySong=MediaPlayer.create(ManifestSortirActivity.this, R.raw.error);
            Toast.makeText(getApplicationContext(), "Jumlah Maksimal 20 ", Toast.LENGTH_SHORT).show();
        }else {
            if (ttk==0){
//                mySong=MediaPlayer.create(ManifestSortirActivity.this, R.raw.success);
//                BuatSJ sj = new BuatSJ();
//                sj.setTtk(rawResult.getContents().toUpperCase());
//                db.addMS(sj);
//                populate();

                cekttkms(rawResult.getContents());


            }else{
                mySong=MediaPlayer.create(ManifestSortirActivity.this, R.raw.error);
                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                mySong.start();
            }
//            mySong.start();
        }
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ManifestSortirActivity.this);
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb=new AlertDialog.Builder(ManifestSortirActivity.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteMS();
                finish();
            }});
        adb.show();
    }

    public boolean isTTK(String ttk){

        return ttk.matches("^([A-Z][A-Z][A-Z])([0-9][0-9][0-9][0-9][0-9])$");
    }
}