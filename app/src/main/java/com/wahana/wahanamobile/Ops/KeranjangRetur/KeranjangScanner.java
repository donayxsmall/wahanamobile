package com.wahana.wahanamobile.Ops.KeranjangRetur;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.TtkKeranjangRetur;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.Ops.SJP.SuratJalanPenerusScanner;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterKeranjangTTKRetur;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
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

public class KeranjangScanner extends Activity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    private Button flashButton, flashoff, addManual;
    private boolean mFlash;
    private static final String FLASH_STATE = "FLASH_STATE";
    DatabaseHandler db;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    TextView jumlahTTK;
    Button previewTTK,submit;
    String ttkManual,employeeCode,via,nokeranjang,jeniskeranjang;
    ListView listTTK;
    List<TtkKeranjangRetur> ttkList;
    int urut;
    AdapterKeranjangTTKRetur adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang_scanner);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(KeranjangScanner.this));

        progressDialog = new ProgressDialog(KeranjangScanner.this, R.style.AppTheme_Dark_Dialog);

        Intent intent=getIntent();
        nokeranjang=intent.getStringExtra("nokeranjang");
        jeniskeranjang=intent.getStringExtra("jenis");

        Log.d("jenis",""+jeniskeranjang);

        session = new SessionManager(KeranjangScanner.this);
        employeeCode = session.getID();

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);

        db = new DatabaseHandler(KeranjangScanner.this);
        ttkList = db.getAllTTKKeranjangRetur();
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(KeranjangScanner.this);
                alertDialog.setTitle("Input Manual TTK");
                alertDialog.setMessage("Masukkan no. TTK");

                final EditText input = new EditText(KeranjangScanner.this);
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
                                int ttk = db.checkTtkReturKeranjang(ttkManual);
                                Log.d("Hasil", ""+ttk);
//                                if (!isTTK(ttkManual)) {
//                                    mySong= MediaPlayer.create(SuratJalanPenerusScanner.this, R.raw.error);
//                                    mySong.start();
//                                    Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
                                if (ttk==0){
                                    mySong=MediaPlayer.create(KeranjangScanner.this, R.raw.success);
                                    TtkKeranjangRetur sj = new TtkKeranjangRetur();
                                    sj.setTtk(ttkManual);
                                    db.addTTKreturKeranjang(sj);
                                    populate();
                                }else {
                                    mySong=MediaPlayer.create(KeranjangScanner.this, R.raw.error);
                                    Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                                }
                                mySong.start();
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
                android.app.AlertDialog.Builder adb=new android.app.AlertDialog.Builder(KeranjangScanner.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                final int positionToRemoveDB = position + 1;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new android.app.AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKKeranjangRetur(nottk.getText().toString());
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
                android.app.AlertDialog.Builder adb=new android.app.AlertDialog.Builder(KeranjangScanner.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new android.app.AlertDialog.OnClickListener() {
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
        ArrayList<String> ttks = db.getAllTtkKeranjangReturFix();
        JSONObject json = new JSONObject();
        try {
            json.put("service", "setKeranjangRetur");
            json.put("employeeCode", employeeCode);
            json.put("nokeranjang", nokeranjang);
            json.put("jeniskeranjang", jeniskeranjang);
            JSONArray jsArray = new JSONArray(ttks);
            json.put("tgl", formattedDate);
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
                            Toast.makeText(KeranjangScanner.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(2);
//                                Log.d("hasil soap data", ""+d.getJSONObject(1).getString("status"));
                                if (d.getJSONObject(1).getString("status").equals("1")){
                                    Intent pindah = new Intent(KeranjangScanner.this, HasilProses.class);
                                    db.deleteKeranjangReturTtk();
                                    pindah.putExtra("proses","ttkkeranjang");
                                    JSONArray msd = data.getJSONArray(1);
                                    String sjp = msd.getString(1);
                                    pindah.putExtra("no", sjp);
//                                    pindah.putExtra("no", "SJP-123-123");
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(KeranjangScanner.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(KeranjangScanner.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(KeranjangScanner.this, HasilError.class);
                            pindah.putExtra("proses","ttkkeranjang");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(KeranjangScanner.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);

    }



    private void populate(){
        urut = urut +1;
        ttkList.clear();
        ttkList = db.getAllTTKKeranjangRetur();
        String textJumlah = Integer.toString(ttkList.size());
        jumlahTTK.setText(textJumlah);
        Log.d("Hasil", ""+ttkList);
        adapter = new AdapterKeranjangTTKRetur(KeranjangScanner.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "ttkreturkeranjang");
        startActivity(pindah);
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
        int ttk = db.checkTtkReturKeranjang(rawResult.getContents());
        Log.d("Hasil", ""+ttk);
//        if (!isTTK(rawResult.getContents().toUpperCase())) {
//            mySong= MediaPlayer.create(SuratJalanPenerusScanner.this, R.raw.error);
//            mySong.start();
//            Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
//        }
        if (ttk==0){
            mySong=MediaPlayer.create(KeranjangScanner.this, R.raw.success);
            TtkKeranjangRetur sj = new TtkKeranjangRetur();
            sj.setTtk(rawResult.getContents().toUpperCase());
            db.addTTKreturKeranjang(sj);
            populate();
        }else {
            mySong=MediaPlayer.create(KeranjangScanner.this, R.raw.error);
            Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
        }
        mySong.start();
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(KeranjangScanner.this);
            }
        }, 2000);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb=new AlertDialog.Builder(KeranjangScanner.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteKeranjangReturTtk();
                finish();
            }});
        adb.show();
    }


    private boolean validateKode() {
        ArrayList<String> ttks = db.getAllTtkKeranjangReturFix();
        if (ttks.size()<1) {
            Toast.makeText(this, "Mohon Masukkan Data nya", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }









}
