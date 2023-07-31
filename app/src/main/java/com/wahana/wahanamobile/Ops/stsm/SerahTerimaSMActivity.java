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

public class SerahTerimaSMActivity extends Activity implements ZBarScannerView.ResultHandler {
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
    String ttkManual,employeeCode,sm;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    //    Camera mCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serah_terima_sm);
        progressDialog = new ProgressDialog(SerahTerimaSMActivity.this, R.style.AppTheme_Dark_Dialog);

        Intent intent = getIntent();
        sm = intent.getStringExtra("sm");

        session = new SessionManager(SerahTerimaSMActivity.this);
        employeeCode = session.getID();

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(SerahTerimaSMActivity.this);
        db.deleteSTSM();
        ttkList = db.getAllTtkSTSM();
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SerahTerimaSMActivity.this);
                alertDialog.setTitle("Input Manual No Manifest");
                alertDialog.setMessage("Masukkan no. Manifest");

                final EditText input = new EditText(SerahTerimaSMActivity.this);
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
                                int ttk = db.checkSTSM(ttkManual);
//                                Log.d("Hasil", ""+ttk);
                                if (!isMS(ttkManual)) {
                                    mySong= MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
                                    mySong.start();
                                    Toast.makeText(getApplicationContext(), "MS tidak sesuai", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (ttk==0){
                                    submitForm(ttkManual.trim());
                                }else {
                                    mySong= MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
                                    Toast.makeText(getApplicationContext(), "No Manifest Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
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
//        listTTK.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
//                final TextView nottk = (TextView)v.findViewById(R.id.no_ttk_isi);
//                AlertDialog.Builder adb=new AlertDialog.Builder(SerahTerimaSMActivity.this);
//                adb.setTitle("Hapus No Manifest ?");
//                adb.setMessage("Apakah anda yakin ingin mendelete No Manifest " + nottk.getText().toString());
//                final int positionToRemove = position;
//                adb.setNegativeButton("Cancel", null);
//                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        ttkList.remove(positionToRemove);
//                        adapter.notifyDataSetChanged();
//                        db.deleteTTKSTSM(nottk.getText().toString());
//                        populate();
//                    }});
//                adb.show();
//            }
//        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateKode()) {
                    return;
                }
                AlertDialog.Builder adb=new AlertDialog.Builder(SerahTerimaSMActivity.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendForm();
                    }});
                adb.show();
            }
        });

    }

    public void sendForm(){
        db.deleteSTSM();
        Intent pindah = new Intent(SerahTerimaSMActivity.this, HasilProses.class);
        pindah.putExtra("proses","stsm");
        pindah.putExtra("no","");
        startActivity(pindah);
        finish();
    }

    private void submitForm(final String ms) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        JSONObject json = new JSONObject();
        try {
            json.put("service", "setDataSTSM");
            json.put("employeeCode", employeeCode);
            json.put("tgl", formattedDate);
            json.put("ms", ms);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add(""+json);
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
                            Toast.makeText(SerahTerimaSMActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                    mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.success);
                                    BuatSJ sj = new BuatSJ();
                                    sj.setTtk(ms);
                                    db.addSTSM(sj);
                                    populate();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(SerahTerimaSMActivity.this, "Berhasil Serah Terima",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else{
                                    mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(SerahTerimaSMActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(SerahTerimaSMActivity.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SerahTerimaSMActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        mySong=MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SerahTerimaSMActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                    mySong.start();
                }
            }
        }.execute(parameter);

    }

    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "stsm");
        startActivity(pindah);
    }

    private void populate(){
        urut = urut +1;
        ttkList.clear();
        ttkList = db.getAllTtkSTSM();
        String textJumlah = Integer.toString(ttkList.size());
        jumlahTTK.setText(textJumlah);
//        Log.d("Hasil", ""+ttkList);
        adapter = new BuatSJAdapter(SerahTerimaSMActivity.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private boolean validateKode() {
        ArrayList<String> ttks = db.getAllTtkSTSMFix();
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        int ttk = db.checkSTSM(rawResult.getContents());
//        Log.d("Hasil", ""+ttk);
        if (!isMS(rawResult.getContents().toUpperCase())) {
            mySong= MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
            mySong.start();
            Toast.makeText(getApplicationContext(), "MS tidak sesuai", Toast.LENGTH_SHORT).show();
        }else{
            if (ttk==0){
                submitForm(rawResult.getContents().toUpperCase());
            }else {
                mySong= MediaPlayer.create(SerahTerimaSMActivity.this, R.raw.error);
                Toast.makeText(getApplicationContext(), "No Manifest Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                mySong.start();
            }
        }
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SerahTerimaSMActivity.this);
            }
        }, 2000);
    }

    public boolean isMS(String ms){

        return ms.contains("MS-");
    }

}
