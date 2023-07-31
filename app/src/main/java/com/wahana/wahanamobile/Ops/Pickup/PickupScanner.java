package com.wahana.wahanamobile.Ops.Pickup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.MediaPlayer;
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
import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterListPickup;
import com.wahana.wahanamobile.adapter.BuatSJAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by sadewa on 09/06/17.
 */

public class PickupScanner extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    private Button flashButton, flashoff, addManual;
    private boolean mFlash;
    private static final String FLASH_STATE = "FLASH_STATE";
    ListView listTTK;
    DatabaseHandler db;
    List<ListTtkPickup> ttkList;
    List<ListTtkPickup> ttkListAll;
    AdapterListPickup adapter;
    TextView jumlahTTK,judul;
    Button previewTTK,submit;
    String ttkManual,employeeCode,kodekotatujuanMS,kodeagen,kodeverifikasi,kodesortir;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    int koliMatang,totalpickupsumber;
    UserAPIService mApiInterface;
    JSONObject jsonlokasi;
    //    Camera mCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_scanner);
        progressDialog = new ProgressDialog(PickupScanner.this, R.style.AppTheme_Dark_Dialog);

        Intent intent = getIntent();
        kodekotatujuanMS = intent.getStringExtra("kodekotatujuanMS");

        kodeagen=intent.getStringExtra("kodeagen");
        kodeverifikasi=intent.getStringExtra("kodeverifikasi");
        kodesortir=intent.getStringExtra("kodesortir");

        mApiInterface = ApiClient.getClient().create(UserAPIService.class);


        Log.d("kodever",""+kodeagen+"/"+kodeverifikasi);



        session = new SessionManager(PickupScanner.this);
        employeeCode = session.getID();

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(PickupScanner.this);

        //ttkList = db.getAllDataPickup();
        jumlahTTK = (TextView) findViewById(R.id.jumlah_label) ;
        judul=(TextView)findViewById(R.id.judul);
//        populate();
        urut = 0;



//        db.deleteListPickup();

        totalpickupsumber=db.countPickupSumber();

        Log.d("jumlahsumber",""+totalpickupsumber);

//        judul.setText("Scan TTK /"+totalpickupsumber);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);

        populate();



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



                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PickupScanner.this);
                alertDialog.setTitle("Input Manual TTK");
                alertDialog.setMessage("Masukkan no. TTK");

                final EditText input = new EditText(PickupScanner.this);
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
                                ttkList = db.getAllDataPickupPreview(kodeagen,kodeverifikasi,kodesortir);
                                int ttk = db.checkListPickup(ttkManual,kodeagen,kodeverifikasi,kodesortir);


                                int ttksumber=db.checkListPickupSumber(ttkManual);

//                                Cursor ff=db.getListPickupSumber(ttkManual);
//
//
//                                Log.d("Hasilpc", ""+ff.getInt(0)+":"+ff.getInt(1));

//                                if (!isTTK(ttkManual)) {
//                                    mySong= MediaPlayer.create(PickupScanner.this, R.raw.error);
//                                    mySong.start();
//                                    Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
//                                    return;
////                                }
//                                if (ttkList.size()>=20){
//                                    mySong=MediaPlayer.create(PickupScanner.this, R.raw.error);
//                                    Toast.makeText(getApplicationContext(), "Jumlah Maksimal 20 ", Toast.LENGTH_SHORT).show();
//                                }else {

                                if(ttksumber!=0) {

                                    if (ttk == 0) {
                                        mySong = MediaPlayer.create(PickupScanner.this, R.raw.success);
                                        ListTtkPickup sj = new ListTtkPickup();
                                        sj.setTtk(ttkManual);
                                        sj.setKodeagen(kodeagen);
                                        sj.setKodeverifikasi(kodeverifikasi);
                                        sj.setTgl(getDateTime());
                                        sj.setKodesortir(kodesortir);
                                        //sj.setNoref(sj.getNoref());
                                        db.addTTKPickup(sj);
                                        populate();

                                        final AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);
                                        adb.setTitle("Info");
                                        adb.setMessage(ttkManual);


                                        adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();

                                            }});

                                        final AlertDialog alert = adb.create();
                                        alert.getWindow().setBackgroundDrawableResource(R.color.primary);

                                        alert.show();

                                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                        nbutton.setTextColor(getResources().getColor(R.color.white));

                                        timeAlert(alert);


                                    } else {
                                        mySong = MediaPlayer.create(PickupScanner.this, R.raw.error);
//                                        Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();

                                        final AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);
                                        adb.setTitle("Info");
                                        adb.setMessage(ttkManual+" TTK Sudah ada dalam daftar");


                                        adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();

                                            }});

                                        final AlertDialog alert = adb.create();
                                        alert.getWindow().setBackgroundDrawableResource(R.color.primary);

                                        alert.show();

                                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                        nbutton.setTextColor(getResources().getColor(R.color.white));

                                        timeAlert(alert);
                                    }

                                }else{
                                    mySong = MediaPlayer.create(PickupScanner.this, R.raw.error);
//                                    Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber", Toast.LENGTH_SHORT).show();

                                    final AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);


                                    adb.setTitle("Info");
                                    adb.setMessage(ttkManual+" TTK Tidak terdaftar di sumber");


                                    adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();

                                        }});

                                    final AlertDialog alert = adb.create();
                                    alert.getWindow().setBackgroundDrawableResource(R.color.primary);

                                    alert.show();

                                    Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    nbutton.setTextColor(getResources().getColor(R.color.white));

                                    timeAlert(alert);
                                }

//                                }

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
                AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKListPickup(nottk.getText().toString());
                        populate();
                    }});
                adb.show();
            }
        });


//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!validateKode()) {
//                    return;
//                }
//                final InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
//
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PickupScanner.this);
//                alertDialog.setTitle("Input Koli Matang");
//                alertDialog.setMessage("Masukkan jumlah koli matang ");
//
//                final EditText input = new EditText(PickupScanner.this);
//                input.setInputType(InputType.TYPE_CLASS_NUMBER);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT);
//                input.setLayoutParams(lp);
//                alertDialog.setView(input);
////                alertDialog.setIcon(R.drawable.key);
//
//                alertDialog.setPositiveButton("YES",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
//                                String isi = input.getText().toString();
//                                if (isi.isEmpty()){
//                                    Toast.makeText(getApplicationContext(), "Mohon isi jumlah koli matang", Toast.LENGTH_SHORT).show();
//                                }else {
//                                    koliMatang = Integer.parseInt(isi);
//                                    if(koliMatang<1){
//                                        Toast.makeText(getApplicationContext(), "Jumlah koli matang minimal 1", Toast.LENGTH_SHORT).show();
//                                    }else{
//                                        AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);
//                                        adb.setTitle("Confirmation ?");
//                                        adb.setMessage("Apakah anda yakin ?");
//                                        adb.setNegativeButton("Cancel", null);
//                                        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                submitForm();
//                                            }});
//                                        adb.show();
//                                    }
//                                }
//                            }
//                        });
//
//                alertDialog.setNegativeButton("NO",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
//
//                            }
//                        });
//                input.requestFocus();
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                alertDialog.show();
//            }
//        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateKode()) {
                    return;
                }

                Log.d("lokasi",""+jsonlokasi);

                AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin TTK yang sudah di scan sudah sesuai?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        testjson();
                    }});

                adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //db.deleteListPickup();
//                                        Toast.makeText(getApplicationContext(), "neutralize", Toast.LENGTH_SHORT).show();
                        Intent pindah = new Intent(PickupScanner.this, PreviewResultScanner.class);
                        pindah.putExtra("asal", "pu");
                        pindah.putExtra("kodeagen", kodeagen);
                        pindah.putExtra("kodeverifikasi", kodeverifikasi);
                        pindah.putExtra("kodesortir", kodesortir);
                        startActivity(pindah);

                    }});


                adb.show();

            }

        });


    }


    private void testjson(){

        ArrayList<String> ttks = db.getAllTTKPUfix(kodeagen,kodeverifikasi,kodesortir);

        JSONObject json = new JSONObject();


        try{
            JSONArray jsArray = new JSONArray(ttks);
            json.put("ttk", jsArray);
        }catch (Exception e){
            e.printStackTrace();
        }


        Log.d("hasilpu",""+json);


        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);

        Call<ApiPickup> result = mApiInterface.CreatePU(session.getID(),kodeagen,json,session.getSessionID(),jsonlokasi);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        result.enqueue(new Callback<ApiPickup>() {

            @Override
            public void onResponse(Call<ApiPickup> call, Response<ApiPickup> response) {
                try {

                    progressDialog.dismiss();

                    String vrcode=response.body().getVrcode();
                    String vrmsg=response.body().getVrmesg();
                    String ATRBTID=response.body().getATRBTID();

                    if(vrcode.equals("1")){
//                        Toast.makeText(PickupVerifikasi.this," response version "+response.body().getVrcode(),Toast.LENGTH_SHORT).show();

                        Log.d("retro",""+vrcode+"|"+vrmsg);

                        db.deleteListPickup(kodeagen,kodeverifikasi,kodesortir);

                        Intent pindah = new Intent(PickupScanner.this, HasilProses.class);
                        pindah.putExtra("proses","pu");
                        pindah.putExtra("no", vrmsg);
                        pindah.putExtra("ATRBTID", ATRBTID);
                        pindah.putExtra("kodeagen", kodeagen);
                        pindah.putExtra("kodesortir", kodesortir);
                        startActivity(pindah);
                        finish();

                    }else{
                        Toast.makeText(PickupScanner.this,vrmsg,Toast.LENGTH_SHORT).show();
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ApiPickup> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Log.d("error",""+t.toString());
            }
        });


        Log.d("scan",""+json);


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
                            Toast.makeText(PickupScanner.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
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
                                    Intent pindah = new Intent(PickupScanner.this, HasilProses.class);
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
                                            Toast.makeText(PickupScanner.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(PickupScanner.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(PickupScanner.this, HasilError.class);
                            pindah.putExtra("proses","ms");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(PickupScanner.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
        pindah.putExtra("asal", "pu");
        pindah.putExtra("kodeagen", kodeagen);
        pindah.putExtra("kodeverifikasi", kodeverifikasi);
        pindah.putExtra("kodesortir", kodesortir);
        startActivity(pindah);
    }

    private void populate(){
        urut = urut +1;
        //ttkList.clear();

//        Log.d("kodesortir",""+kodesortir);

        ttkList = db.getAllDataPickup(kodeagen,kodeverifikasi,kodesortir);

        ttkListAll=db.getAllDataPickupPreview(kodeagen,kodeverifikasi,kodesortir);
        String textJumlah = Integer.toString(ttkListAll.size());

        jumlahTTK.setText(textJumlah+"/"+totalpickupsumber);

        int sisasumber=totalpickupsumber-Integer.parseInt(textJumlah);

//        judul.setText("Scan TTK /"+sisasumber);

        Log.d("Hasil", ""+ttkList);
        adapter = new AdapterListPickup(PickupScanner.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private boolean validateKode() {
        List<ListTtkPickup> ttks = db.getAllDataPickupPreview(kodeagen,kodeverifikasi,kodesortir);

        Log.d("hasilsize",""+ttks.size());

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


        FusedLocation fusedLocation = new FusedLocation(PickupScanner.this, new FusedLocation.Callback(){
            @Override
            public void onLocationResult(Location location){
                //Do as you wish with location here
//                Toast.makeText(PickupScanner.this,
//                        "Latitude " +location.getLatitude() +" Longitude: " + location.getLongitude(),
//                        Toast.LENGTH_LONG).show();

                double latitude=location.getLatitude();
                double longitude=location.getLongitude();

                jsonlokasi = new JSONObject();
                try {
                    jsonlokasi.put("latitude", location.getLatitude());
                    jsonlokasi.put("longitude", location.getLongitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                getlokasi(latitude,longitude);


            }
        });

        if (!fusedLocation.isGPSEnabled()){
            fusedLocation.showSettingsAlert();
        }else{
            fusedLocation.getCurrentLocation(1);
        }





        }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        int ttk = db.checkListPickup(rawResult.getContents(),kodeagen,kodeverifikasi,kodesortir);
        Log.d("Hasil", ""+ttk);
        //ttkList = db.getAllDataPickup();

        int ttksumber=db.checkListPickupSumber(rawResult.getContents());

//        if (!isTTK(rawResult.getContents().toUpperCase())) {
//            mySong= MediaPlayer.create(PickupScanner.this, R.raw.error);
//            mySong.start();
//            Toast.makeText(getApplicationContext(), "TTK tidak sesuai", Toast.LENGTH_SHORT).show();
//        }
//        if (ttkList.size()>=20){
//            mySong=MediaPlayer.create(PickupScanner.this, R.raw.error);
//            Toast.makeText(getApplicationContext(), "Jumlah Maksimal 20 ", Toast.LENGTH_SHORT).show();
//        }else {

        Log.d("ttksumber",""+ttksumber+":"+ttk);

        if(ttksumber!=0) {

            if (ttk == 0) {
                mySong = MediaPlayer.create(PickupScanner.this, R.raw.success);
                ListTtkPickup sj = new ListTtkPickup();
                sj.setTtk(rawResult.getContents().toUpperCase());
                sj.setKodeagen(kodeagen);
                sj.setKodeverifikasi(kodeverifikasi);
                sj.setTgl(getDateTime());
                sj.setKodesortir(kodesortir);
                db.addTTKPickup(sj);
                populate();

                final AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);
                adb.setTitle("Info");
                adb.setMessage(rawResult.getContents());


                adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                     dialog.cancel();

                    }});

                final AlertDialog alert = adb.create();
                alert.getWindow().setBackgroundDrawableResource(R.color.primary);

                alert.show();

                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(getResources().getColor(R.color.white));

                timeAlert(alert);




            } else {
                mySong = MediaPlayer.create(PickupScanner.this, R.raw.error);
//                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();


                final AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);
                adb.setTitle("Info");
                adb.setMessage(rawResult.getContents()+" TTK Sudah ada dalam daftar");


                adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }});

                final AlertDialog alert = adb.create();
                alert.getWindow().setBackgroundDrawableResource(R.color.primary);

                alert.show();

                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(getResources().getColor(R.color.white));

                timeAlert(alert);

            }

        }else{

            mySong = MediaPlayer.create(PickupScanner.this, R.raw.error);
//            Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber", Toast.LENGTH_SHORT).show();

            final AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);
            adb.setTitle("Info");
            adb.setMessage(rawResult.getContents()+" TK Tidak terdaftar di sumber");


            adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }});

            final AlertDialog alert = adb.create();
            alert.getWindow().setBackgroundDrawableResource(R.color.primary);

            alert.show();

            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(getResources().getColor(R.color.white));

            timeAlert(alert);

        }
            mySong.start();
//        }
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(PickupScanner.this);
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb=new AlertDialog.Builder(PickupScanner.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteListPickup(kodeagen,kodeverifikasi,kodesortir);
                finish();
            }});
        adb.show();
    }

    public boolean isTTK(String ttk){

        return ttk.matches("^([A-Z][A-Z][A-Z])([0-9][0-9][0-9][0-9][0-9])$");
    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public void timeAlert(final AlertDialog alert){
        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 1500);
    }
}