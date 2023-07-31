package com.wahana.wahanamobile.Ops.suratJalan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.wahana.wahanamobile.webserviceClient.SoapClient;
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

public class SuratJalanScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
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
    String ttkManual,kurir,via,employeeCode;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    String barcode = "";
    EditText inputscan;
    //    Camera mCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_jalan_scanner);
        progressDialog = new ProgressDialog(SuratJalanScannerActivity.this, R.style.AppTheme_Dark_Dialog);

        Intent intent = getIntent();
        kurir = intent.getStringExtra("kurir");
        via = intent.getStringExtra("via");

        session = new SessionManager(SuratJalanScannerActivity.this);
        employeeCode = session.getID();

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(SuratJalanScannerActivity.this);
//        db.deleteBuatSJ();
        ttkList = db.getAllTtkSJ();
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

        inputscan=(EditText)findViewById(R.id.inputscan);

        inputscan.setVisibility(View.GONE);

//        inputscan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (!hasFocus) {
//                    Toast.makeText(SuratJalanScannerActivity.this, "Focus Lose", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(SuratJalanScannerActivity.this, "Get Focus", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });



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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SuratJalanScannerActivity.this);
                alertDialog.setTitle("Input Manual TTK");
                alertDialog.setMessage("Masukkan no. TTK");

                final EditText input = new EditText(SuratJalanScannerActivity.this);
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
                                int ttk = db.checkSJ(ttkManual);
//                                if (!isTTK(ttkManual)) {
//                                    mySong= MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
//                                    mySong.start();
//                                    Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
                                if (ttk==0){
                                    submitForm(ttkManual);

//                                    BuatSJ sj = new BuatSJ();
//                                    sj.setTtk(ttkManual);
//                                    db.addBuatSJ(sj);
//                                    populate();
                                }else {
                                    mySong=MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
                                    Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder adb=new AlertDialog.Builder(SuratJalanScannerActivity.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                final int positionToRemoveDB = position + 1;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKSJ(nottk.getText().toString());
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
                AlertDialog.Builder adb=new AlertDialog.Builder(SuratJalanScannerActivity.this);
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

    private void submitForm() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        ArrayList<String> ttks = db.getAllTtkSJFix();
        JSONObject json = new JSONObject();
        try {
            json.put("service", "setDataSJ");
            json.put("kurir", kurir);
            json.put("via",via);
            json.put("employeeCode", employeeCode);
            JSONArray jsArray = new JSONArray(ttks);
            json.put("tgl", formattedDate);
            json.put("jumlah",ttks.size());
            json.put("ttk", jsArray);
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
                            Toast.makeText(SuratJalanScannerActivity.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        final String code = result.getProperty(0).toString();
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
                                    db.deleteBuatSJ();
                                    Intent pindah = new Intent(SuratJalanScannerActivity.this, HasilProses.class);
                                    pindah.putExtra("proses","sj");
                                    JSONArray msd = data.getJSONArray(1);
                                    String sjId = msd.getString(0);
                                    String sj = msd.getString(1);
                                    pindah.putExtra("no", sj);
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(SuratJalanScannerActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(SuratJalanScannerActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(SuratJalanScannerActivity.this, HasilError.class);
                            pindah.putExtra("proses","sj");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SuratJalanScannerActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    private void submitForm(final String ttk) {

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getNikTTK");
        parameter.add("20");
        parameter.add("0");
        parameter.add("nik");
        parameter.add(kurir);
        parameter.add("ttk");
        parameter.add(ttk);
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
                            Toast.makeText(SuratJalanScannerActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty("resMessage").toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            String so = result.getProperty("resData").toString();
                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");

                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                int berat = d.getInt(4);
                                int jenis=d.getInt(5);
                                if(via.equals("101") || via.equals("102")){

                                    if(jenis==9404){
                                        mySong=MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Jenis TTK Internal Marketing, Harus masuk SJ Mobil", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else {

                                        if (berat <= 25) {
                                            mySong = MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.success);
                                            BuatSJ sj = new BuatSJ();
                                            sj.setTtk(ttk);
                                            db.addBuatSJ(sj);
                                            populate();
                                        } else {
                                            mySong = MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Berat diatas 25KG masuk SJ mobil", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }

                                }else{
                                        mySong = MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.success);
                                        BuatSJ sj = new BuatSJ();
                                        sj.setTtk(ttk);
                                        db.addBuatSJ(sj);
                                        populate();
                                }
                                mySong.start();
                            }else{
                                mySong=MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
                                mySong.start();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Tujan TTK tidak sesuai", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            mySong=MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
                            mySong.start();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SuratJalanScannerActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        mySong=MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SuratJalanScannerActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                        mySong.start();
                    }
                }
            }
        }.execute(parameter);

    }

    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "sj");
        startActivity(pindah);
    }

    private void populate(){
        urut = urut +1;
        ttkList.clear();
        ttkList = db.getAllTtkSJ();
        String textJumlah = Integer.toString(ttkList.size());
        jumlahTTK.setText(textJumlah);
        adapter = new BuatSJAdapter(SuratJalanScannerActivity.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private boolean validateKode() {
        ArrayList<String> ttks = db.getAllTtkSJFix();
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
    public boolean dispatchKeyEvent(KeyEvent e) {

        if(e.getAction()==KeyEvent.ACTION_DOWN){
            Log.i(TAG,"dispatchKeyEvent: "+e.toString());
            char pressedKey = (char) e.getUnicodeChar();
            barcode += pressedKey;
        }
        if (e.getAction()==KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//            Toast.makeText(getApplicationContext(),
//                    "barcode--->>>" + barcode, Toast.LENGTH_LONG)
//                    .show();

            barcode = barcode.replaceAll("\n","");

//            Toast.makeText(getApplicationContext(), "TTK"+barcode, Toast.LENGTH_SHORT).show();

//            int ttk = db.checkSJ(barcode);

//            Toast.makeText(getApplicationContext(), "TTK1 "+ttk, Toast.LENGTH_SHORT).show();
//
            yu(barcode);

////        if (!isTTK(rawResult.getContents().toUpperCase())) {
////            mySong= MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
////            mySong.start();
////            Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
////        }
//            if (ttk==0){
//                submitForm(barcode);
//            }else {
//                mySong= MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
//                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
//                mySong.start();
//            }

            barcode="";
        }

        return super.dispatchKeyEvent(e);
    }

    public void yu(String bar){

//        int ttk = db.checkSJ(bar);

        Toast.makeText(getApplicationContext(), "TTK1 "+bar, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleResult(Result rawResult) {
        int ttk = db.checkSJ(rawResult.getContents());
//        if (!isTTK(rawResult.getContents().toUpperCase())) {
//            mySong= MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
//            mySong.start();
//            Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
//        }
        if (ttk==0){
            submitForm(rawResult.getContents().toUpperCase());
        }else {
            mySong= MediaPlayer.create(SuratJalanScannerActivity.this, R.raw.error);
            Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
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
                mScannerView.resumeCameraPreview(SuratJalanScannerActivity.this);
            }
        }, 2000);
    }

    public boolean isTTK(String ttk){

        return ttk.matches("^([A-Z][A-Z][A-Z])([0-9][0-9][0-9][0-9][0-9])$");
    }
}
