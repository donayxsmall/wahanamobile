package com.wahana.wahanamobile.Ops.Pickup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterListPickup;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.wahana.wahanamobile.R;

public class PickupScannerHardware extends AppCompatActivity {

    ListView listTTK;
    DatabaseHandler db;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    String barcode = "";
    TextView jumlah;
    String ttkManual,employeeCode,kodekotatujuanMS,kodeagen,kodeverifikasi,kodesortir;
    int urut,totalpickupsumber;
    List<ListTtkPickup> ttkList;
    List<ListTtkPickup> ttkListAll;
    AdapterListPickup adapter;
    Button button1,submit;
    EditText inputnik;
    String ttkbar;
    String hg="";
    JSONObject jsonlokasi;
    UserAPIService mApiInterface;
//    char pressedKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_scanner_hardware);
        progressDialog = new ProgressDialog(PickupScannerHardware.this, R.style.AppTheme_Dark_Dialog);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(PickupScannerHardware.this));

        Intent intent = getIntent();
        kodekotatujuanMS = intent.getStringExtra("kodekotatujuanMS");

        kodeagen=intent.getStringExtra("kodeagen");
        kodeverifikasi=intent.getStringExtra("kodeverifikasi");
        kodesortir=intent.getStringExtra("kodesortir");

        mApiInterface = ApiClient.getClient().create(UserAPIService.class);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PickupScannerHardware.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        jumlah=(TextView)findViewById(R.id.jumlah);
        inputnik=(EditText)findViewById(R.id.input_nik);
        button1=(Button)findViewById(R.id.input_button1);
        submit=(Button)findViewById(R.id.input_button);

        db = new DatabaseHandler(PickupScannerHardware.this);
        session = new SessionManager(PickupScannerHardware.this);
        employeeCode = session.getID();


        String as="'APM05770'";

//        hg+=as;

        String gg = as.replaceAll("'","");

        String dw=gg.trim();

        Log.d("panjang",""+as.length()+" "+dw.length());

        button1.setVisibility(View.GONE);



        inputnik.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(PickupScannerHardware.this, inputnik.getText().toString(), Toast.LENGTH_SHORT).show();
                    ttkManual=inputnik.getText().toString().toUpperCase();

                    int ttksumber=db.checkListPickupSumberAccount(ttkManual);
                    int ttk = db.checkListPickup(ttkManual,kodeagen,kodeverifikasi,kodesortir);

                    if(ttksumber!=0) {

                        if (ttk == 0) {

                            try {
                                mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.success);
                                ListTtkPickup sj = new ListTtkPickup();
                                sj.setTtk(ttkManual);
                                sj.setKodeagen(kodeagen);
                                sj.setKodeverifikasi(kodeverifikasi);
                                sj.setTgl(getDateTime());
                                sj.setKodesortir(kodesortir);
                                //sj.setNoref(sj.getNoref());
                                db.addTTKPickup(sj);
                                populate();

                            } catch (RuntimeException e){
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.error);
                            Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.error);
                        Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber", Toast.LENGTH_SHORT).show();
                    }

                    inputnik.setText("");

                    mySong.start();

                    return true;
                }
                return false;
            }
        });




        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateKode()) {
                    return;
                }

                Log.d("lokasi",""+jsonlokasi);

                AlertDialog.Builder adb=new AlertDialog.Builder(PickupScannerHardware.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin TTK yang sudah di scan sudah sesuai?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        testjson();
                    }});

                adb.show();

            }


        });


        listTTK.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                final TextView nottk = (TextView)v.findViewById(R.id.no_ttk_isi);
                AlertDialog.Builder adb=new AlertDialog.Builder(PickupScannerHardware.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkListAll.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKListPickup(nottk.getText().toString());
                        populate();
                    }});
                adb.show();
            }
        });




//        inputnik.setEnabled(false);
//        inputnik.setFocusable(true);
////
////
//
//        inputnik.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                // TODO Auto-generated method stub
//
//
//                if (s.length() > 0) {
//
//                    char lastCharacter = s.charAt(s.length() - 1);
//
//                    if (lastCharacter == '\n') {
//                        String barcode = s.subSequence(0, s.length() - 1).toString();
//                        inputnik.setText("");
//                        searchBarcode(barcode);
//                    }
//                }
//
//            }
//        });

















//
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ttkManual=inputnik.getText().toString().toUpperCase();
//                int ttksumber=db.checkListPickupSumberAccount(ttkManual);
//                int ttk = db.checkListPickup(ttkManual,kodeagen,kodeverifikasi,kodesortir);
//
//                if(ttksumber!=0) {
//
//                    if (ttk == 0) {
//                        mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.success);
//                        ListTtkPickup sj = new ListTtkPickup();
//                        sj.setTtk(ttkManual);
//                        sj.setKodeagen(kodeagen);
//                        sj.setKodeverifikasi(kodeverifikasi);
//                        sj.setTgl(getDateTime());
//                        sj.setKodesortir(kodesortir);
//                        //sj.setNoref(sj.getNoref());
//                        db.addTTKPickup(sj);
//
//                        populate();
//                    }else{
//                        mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.error);
//                        Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
//                    }
//
//                }else{
//                    mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.error);
//                    Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber", Toast.LENGTH_SHORT).show();
//                }
//
//                mySong.start();
//
//
//            }
//        });







    }



//    public void searchBarcode(String bart){
//
//
//        int ttk = db.checkListPickup(bart,kodeagen,kodeverifikasi,kodesortir);
//        int ttksumber=db.checkListPickupSumber(bart);
//
//        int sw=db.countPickupSumber();
//
//        int gt=db.countPickupSumberTTK("APM05770");
//
//        Toast.makeText(getApplicationContext(), "Bartext"+bart+" "+sw+" "+bart.length()+" "+gt, Toast.LENGTH_SHORT).show();
//
////        if (ttksumber!=0) {
////
////            if (ttk == 0) {
////                mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.success);
////                ListTtkPickup sj = new ListTtkPickup();
////                sj.setTtk(bart);
////                sj.setKodeagen(kodeagen);
////                sj.setKodeverifikasi(kodeverifikasi);
////                sj.setTgl(getDateTime());
////                sj.setKodesortir(kodesortir);
////                //sj.setNoref(sj.getNoref());
////                db.addTTKPickup(sj);
////
////                populate();
////            } else {
////                mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.error);
////                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
////            }
////
////        }else{
////            mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.error);
////            Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber9999", Toast.LENGTH_SHORT).show();
////        }
////
////        mySong.start();
//    }




//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent e) {
//
//        if(e.getAction()==KeyEvent.ACTION_DOWN ){
//            Log.i(TAG,"dispatchKeyEvent: "+e.toString());
//            char pressedKey = (char) e.getUnicodeChar();
////            barcode += pressedKey;
//
//            Toast.makeText(getApplicationContext(), "Char"+Character.toString(pressedKey)+" "+Character.toString(pressedKey).length(), Toast.LENGTH_SHORT).show();
////            barcode="";
//        }
//
//        if (e.getAction()==KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//
////            barcode = barcode.replaceAll("\n","");
//
////            barcode = barcode.replaceAll("\n","");
//////
////////            ttkbar=barcode.replaceAll("","");
//////
////            inputnik.setText(barcode);
////
////            ttkManual=inputnik.getText().toString().toUpperCase();
////
////
////
////
//
////            Toast.makeText(getApplicationContext(), "TTK barcode "+barcode, Toast.LENGTH_SHORT).show();
//
////                yu(barcode);
//
////               inputnik.setText(String.valueOf(pressedKey));
//
//                barcode="";
//
////            inputnik.setText("");
//
//
//        }
//
//        return super.dispatchKeyEvent(e);
////        return false;
//    }
//
//    public void yu(String bar){
//
////        String rt = bar.replaceAll("\\s+", " ");
//
//
////        String dw=bar.trim();
////
////        Toast.makeText(getApplicationContext(), ""+bar+" "+bar.length()+" "+dw.length()+" "+inputnik.getText().toString().length(), Toast.LENGTH_SHORT).show();
////        ttkManual=String.valueOf(bar);
//
//
////        String ttkbar=bar.replaceAll("\n","");
//
////
//
////        int ttk = db.checkListPickup(inputnik.getText().toString(),kodeagen,kodeverifikasi,kodesortir);
//////
////        int ttksumber=db.checkListPickupSumber(inputnik.getText().toString());
//
////        int ttksumber=db.checkjumlahSumberPickup(ttkbar);
////
//////        Toast.makeText(getApplicationContext(), "ttksumber "+ttksumber, Toast.LENGTH_SHORT).show();
////
////        Toast.makeText(getApplicationContext(), "TTK1 "+bar+""+ttksumber, Toast.LENGTH_SHORT).show();
//
////        if(ttksumber == 0){
////            Toast.makeText(getApplicationContext(), "TTK Belum ada dalam daftar" +bar+" "+kodesortir, Toast.LENGTH_SHORT).show();
////        }else{
////            Toast.makeText(getApplicationContext(), "TTK Ada dalam daftar" +bar+" "+kodesortir, Toast.LENGTH_SHORT).show();
////        }
////
////        if(ttksumber != 0) {
//////
////            if (ttk == 0) {
////                mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.success);
////                ListTtkPickup sj = new ListTtkPickup();
////                sj.setTtk(inputnik.getText().toString());
////                sj.setKodeagen(kodeagen);
////                sj.setKodeverifikasi(kodeverifikasi);
////                sj.setTgl(getDateTime());
////                sj.setKodesortir(kodesortir);
////                //sj.setNoref(sj.getNoref());
////                db.addTTKPickup(sj);
////
////                populate();
////            }else{
////                mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.error);
////                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
////            }
//////
////        }else{
////            mySong = MediaPlayer.create(PickupScannerHardware.this, R.raw.error);
////            Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber 2"+bar+""+ttksumber, Toast.LENGTH_SHORT).show();
////        }
////
////        mySong.start();
//
//    }



//    @Override
//    public boolean dispatchKeyEvent(KeyEvent e) {
//
//        if(e.getAction()==KeyEvent.ACTION_DOWN){
//            Log.i(TAG,"dispatchKeyEvent: "+e.toString());
//            char pressedKey = (char) e.getUnicodeChar();
//            barcode += pressedKey;
//        }
//        if (e.getAction()==KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//            Toast.makeText(getApplicationContext(),
//                    "barcode--->>>" + barcode + " "+barcode.length(), Toast.LENGTH_LONG)
//                    .show();
//
//            barcode=barcode.replaceAll("\n","");
//
//            bas(barcode);
//
//            barcode="";
//
//
//        }
//
//        return super.dispatchKeyEvent(e);
////        return false;
//    }
//
//
//    public void bas(String bar){
//        String gg = bar.replaceAll("'","");
//        String ws=gg.trim();
//
//        int gt=db.countPickupSumberTTK(ws);
//        Toast.makeText(getApplicationContext(),
//                "barcode121212--->>>" + ws + " "+gt, Toast.LENGTH_LONG)
//                .show();
//    }
















    private void populate(){
        urut = urut +1;
        //ttkList.clear();

        totalpickupsumber=db.countPickupSumber();

        Log.d("jumlahsumber",""+totalpickupsumber);

//        Log.d("kodesortir",""+kodesortir);

        ttkList = db.getAllDataPickup(kodeagen,kodeverifikasi,kodesortir);

        ttkListAll=db.getAllDataPickupPreview(kodeagen,kodeverifikasi,kodesortir);
        String textJumlah = Integer.toString(ttkListAll.size());

        jumlah.setText(textJumlah+"/"+totalpickupsumber);

        int sisasumber=totalpickupsumber-Integer.parseInt(textJumlah);

//        judul.setText("Scan TTK /"+sisasumber);

        Log.d("Hasil", ""+ttkList);
        adapter = new AdapterListPickup(PickupScannerHardware.this, ttkListAll, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }





    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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


        Log.d("pickup",""+json);


        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);

        Call<ApiPickup> result = mApiInterface.CreatePUAccount(session.getID(),kodeagen,json,session.getSessionID(),jsonlokasi);

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
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

                        Intent pindah = new Intent(PickupScannerHardware.this, HasilProses.class);
                        pindah.putExtra("proses","pu");
                        pindah.putExtra("no", vrmsg);
                        pindah.putExtra("ATRBTID", ATRBTID);
                        pindah.putExtra("kodeagen", kodeagen);
                        pindah.putExtra("kodesortir", kodesortir);
                        startActivity(pindah);
                        finish();

                    }else{
                        Toast.makeText(PickupScannerHardware.this,vrmsg,Toast.LENGTH_SHORT).show();
                    }



                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(PickupScannerHardware.this,"Cek Koneksi Anda",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ApiPickup> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Log.d("error",""+t.toString());
                Toast.makeText(PickupScannerHardware.this,"Cek Koneksi Anda",Toast.LENGTH_SHORT).show();
            }
        });


        Log.d("scan",""+json);


    }





















    @Override
    public void onResume() {
        super.onResume();
        populate();

        FusedLocation fusedLocation = new FusedLocation(PickupScannerHardware.this, new FusedLocation.Callback(){
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
    public void onBackPressed() {
        AlertDialog.Builder adb=new AlertDialog.Builder(PickupScannerHardware.this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);

                AlertDialog.Builder adb=new AlertDialog.Builder(PickupScannerHardware.this);
                adb.setTitle("Keluar Halaman");
                adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteListPickup(kodeagen,kodeverifikasi,kodesortir);
                        finish();
                    }});
                adb.show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}