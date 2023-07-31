package com.wahana.wahanamobile.Ops.stpuaccount;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.Pickup.PickupScannerHardware;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterListPickup;
import com.wahana.wahanamobile.adapter.AdapterListSTPU;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.json.JSONArray;
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

public class StpuScannerHardwareAccount extends AppCompatActivity {

    ListView listTTK;
    DatabaseHandler db;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    String barcode = "";
    TextView jumlah;
    String ttkManual,employeeCode,kodekotatujuanMS,kodeagen,kodeverifikasi,kodesortir,niksupir;
    int urut,totalpickupsumber;
    List<ListTtkPickup> ttkList;
    List<ListTtkPickup> ttkListAll;
    AdapterListSTPU adapter;
    Button button1,submit;
    EditText inputnik;
    String ttkbar;
    String hg="";
    JSONObject jsonlokasi;
    UserAPIService mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stpu_scanner_hardware_account);
        progressDialog = new ProgressDialog(StpuScannerHardwareAccount.this, R.style.AppTheme_Dark_Dialog);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(StpuScannerHardwareAccount.this));

        Intent intent = getIntent();
        kodekotatujuanMS = intent.getStringExtra("kodekotatujuanMS");

        niksupir=intent.getStringExtra("niksupir");
        kodeverifikasi=intent.getStringExtra("kodeverifikasi");
        kodesortir=intent.getStringExtra("kodesortir");


        session = new SessionManager(StpuScannerHardwareAccount.this);
        employeeCode = session.getID();

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        jumlah=(TextView)findViewById(R.id.jumlah);
        inputnik=(EditText)findViewById(R.id.input_nik);
        submit=(Button)findViewById(R.id.input_button);
        button1=(Button)findViewById(R.id.input_button1);
        db = new DatabaseHandler(StpuScannerHardwareAccount.this);

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
                Intent intent = new Intent(StpuScannerHardwareAccount.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);


//        totalpickupsumber=db.countSTPUSumber();

        populate();

        button1.setVisibility(View.GONE);


//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ttkManual=inputnik.getText().toString().toUpperCase();
//                int ttk = db.checkListSTPU(ttkManual,niksupir,kodesortir);
//                int ttksumber=db.checkListSTPUSumber(ttkManual);
//
//                if(ttksumber!=0) {
//
//                    if (ttk == 0) {
//
//                        try {
//
//                            ListTtkPickup ttksumber1 = db.getDataSumber(ttkManual);
//
//                            Log.d("ttkg",""+ttksumber1.getKodeopsttk()+ " | "+ttksumber1.getKodesortir());
//
//                            mySong = MediaPlayer.create(StpuScannerHardwareAccount.this, R.raw.success);
//                            ListTtkPickup sj = new ListTtkPickup();
//                            sj.setTtk(ttkManual);
//                            sj.setNiksupir(niksupir);
//                            sj.setKodeopsttk(ttksumber1.getKodeopsttk());
//                            sj.setKodesortir(ttksumber1.getKodesortir());
//                            sj.setTgl(getDateTime());
//                            sj.setKodesortirstpu(kodesortir);
//                            //sj.setNoref(sj.getNoref());
//                            db.addTTKSTPU(sj);
//                            populate();
//
//                        } catch (RuntimeException e){
//                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }catch (Exception e){
//                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//
//                    } else {
//                        mySong = MediaPlayer.create(StpuScannerHardwareAccount.this, R.raw.error);
//                        Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
//                    }
//
//                }else{
//                    mySong = MediaPlayer.create(StpuScannerHardwareAccount.this, R.raw.error);
//                    Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber", Toast.LENGTH_SHORT).show();
//                }
//
//                mySong.start();
//
//
//            }
//        });





        inputnik.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(StpuScannerHardwareAccount.this, inputnik.getText().toString(), Toast.LENGTH_SHORT).show();
                    ttkManual=inputnik.getText().toString().toUpperCase();

                    int ttk = db.checkListSTPU(ttkManual,niksupir,kodesortir);
                    int ttksumber=db.checkListSTPUSumber(ttkManual);

                    if(ttksumber!=0) {

                        if (ttk == 0) {

                            try {

                                ListTtkPickup ttksumber1 = db.getDataSumber(ttkManual);

                                Log.d("ttkg",""+ttksumber1.getKodeopsttk()+ " | "+ttksumber1.getKodesortir());

                                mySong = MediaPlayer.create(StpuScannerHardwareAccount.this, R.raw.success);
                                ListTtkPickup sj = new ListTtkPickup();
                                sj.setTtk(ttkManual);
                                sj.setNiksupir(niksupir);
                                sj.setKodeopsttk(ttksumber1.getKodeopsttk());
                                sj.setKodesortir(ttksumber1.getKodesortir());
                                sj.setTgl(getDateTime());
                                sj.setKodesortirstpu(kodesortir);
                                //sj.setNoref(sj.getNoref());
                                db.addTTKSTPU(sj);
                                populate();

                            } catch (RuntimeException e){
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            mySong = MediaPlayer.create(StpuScannerHardwareAccount.this, R.raw.error);
                            Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        mySong = MediaPlayer.create(StpuScannerHardwareAccount.this, R.raw.error);
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
            public void onClick(View v) {
                if (!validateKode()) {
                    return;
                }



                AlertDialog.Builder adb=new AlertDialog.Builder(StpuScannerHardwareAccount.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin TTK yang sudah di scan sudah sesuai? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        int jumlahnotfound=db.countListSTPUnotFound();

                        if(jumlahnotfound>0){
                            alertttknotfound();
                        }else{
                            testjsonstpu();
                        }


                    }});

                adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent pindah = new Intent(StpuScannerHardwareAccount.this, PreviewResultScanner.class);
                        pindah.putExtra("asal", "stpu");
                        pindah.putExtra("niksupir", niksupir);
                        pindah.putExtra("kodesortir", kodesortir);
                        startActivity(pindah);

                    }});


                adb.show();

            }

        });



        listTTK.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                final TextView nottk = (TextView)v.findViewById(R.id.no_ttk_isi);
                AlertDialog.Builder adb=new AlertDialog.Builder(StpuScannerHardwareAccount.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkListAll.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKListSTPU(nottk.getText().toString());
                        populate();
                    }});
                adb.show();
            }
        });



    }


    private void alertttknotfound(){

        int jumlahnotfound1=db.countListSTPUnotFound();


        AlertDialog.Builder adb=new AlertDialog.Builder(StpuScannerHardwareAccount.this);
        adb.setTitle("Info");
        adb.setMessage("Ada "+jumlahnotfound1+" TTK yang belum di Scan ");
        adb.setNegativeButton("Lihat", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent pindah = new Intent(StpuScannerHardwareAccount.this, ListTTKnotFoundAccount.class);
                startActivity(pindah);
            }});

        adb.setPositiveButton("Lanjutkan", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                testjsonstpu();
            }});


        adb.show();
    }


    private void testjsonstpu(){
        ArrayList<String> ttks = db.getAllTTKSTPUfix(niksupir,kodesortir);

        ArrayList<String> ttksnotfound = db.getAllTtkSTPUnotFoundFix();

        JSONObject json = new JSONObject();

        try{
            JSONArray jsArray = new JSONArray(ttks);
            JSONArray jsArray1 = new JSONArray(ttksnotfound);

            json.put("ttk", jsArray);
            json.put("ttknotfound", jsArray1);

        }catch (Exception e){
            e.printStackTrace();
        }


        Log.d("stpu",""+json);


        Random r = new Random();
        int requestCode = r.nextInt(9999);
        String rc = String.valueOf(requestCode);


        Call<ApiPickup> result = mApiInterface.createSTMAccount(session.getID(),niksupir,json);

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
                    String nostm=response.body().getNostm();

                    if(vrcode.equals("1")){

                        db.deleteListSTPU(niksupir,kodesortir);
                        Intent pindah = new Intent(StpuScannerHardwareAccount.this, HasilProses.class);
                        pindah.putExtra("proses","stpu");
                        pindah.putExtra("no",nostm );
                        startActivity(pindah);
                        finish();

                        Log.d("retro",""+nostm);

                        Log.d("retro",""+vrcode);

                    }else{
                        Toast.makeText(StpuScannerHardwareAccount.this,vrmsg,Toast.LENGTH_SHORT).show();
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



    private void populate(){

        totalpickupsumber=db.countSTPUSumber();

        Log.d("jumlahsumber",""+totalpickupsumber);

        urut = urut +1;
        ttkList = db.getAllDataSTPU(niksupir,kodesortir);
        ttkListAll=db.getAllDataSTPUPreview(niksupir,kodesortir);


        String textJumlah = Integer.toString(ttkListAll.size());
        jumlah.setText(textJumlah+"/"+totalpickupsumber);

        Log.d("Hasil", ""+ttkList);
        adapter = new AdapterListSTPU(StpuScannerHardwareAccount.this, ttkListAll, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    private boolean validateKode() {
        List<ListTtkPickup> ttks = db.getAllDataSTPUPreview(niksupir,kodesortir);
        if (ttks.size()<1) {
            Toast.makeText(this, "Mohon Masukkan TTK nya", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        populate();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb=new AlertDialog.Builder(StpuScannerHardwareAccount.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteListSTPU(niksupir,kodesortir);
                finish();
            }});
        adb.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);

                AlertDialog.Builder adb=new AlertDialog.Builder(StpuScannerHardwareAccount.this);
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


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {

            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showInputMethodPicker();
            Toast.makeText(this, "Barcode Scanner detected. Please turn OFF Hardware/Physical keyboard to enable softkeyboard to function.", Toast.LENGTH_LONG).show();
        }
    }







}
