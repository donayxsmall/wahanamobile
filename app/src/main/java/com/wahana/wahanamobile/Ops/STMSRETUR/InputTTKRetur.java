package com.wahana.wahanamobile.Ops.STMSRETUR;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.aoAuthUser;
import com.wahana.wahanamobile.ModelApiOPS.aoAuthUser_Result;
import com.wahana.wahanamobile.ModelApiOPS.aoCekAlasanRetur;
import com.wahana.wahanamobile.ModelApiOPS.aoCekAlasanRetur_Result;
import com.wahana.wahanamobile.ModelApiOPS.aoCheckSTMRetur;
import com.wahana.wahanamobile.ModelApiOPS.aoGetJenisKeranjang;
import com.wahana.wahanamobile.ModelApiOPS.aoGetJenisKeranjang_Result;
import com.wahana.wahanamobile.Ops.KeranjangRetur.InputKeranjang;
import com.wahana.wahanamobile.Ops.penerimaanRetur.inputTTKReturActivity;
import com.wahana.wahanamobile.Ops.penerimaanRetur.penerimaanRetur;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputTTKRetur extends AppCompatActivity {

    private static final String TAG = "inputTTKRetur";

    ProgressDialog progressDialog;
    public EditText inputVerifikasi;
    private Button btnInput;
    TextView pengisi, tgl, calendar;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String user_id,alasanIsi;
    Button scan;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    Spinner alasan;
    private ArrayList<Origin> returList = new ArrayList<Origin>();
    EditText inputkeranjang,alasanreturtext,idalasanreturtext,alasanreturtextcs;
    Button scankeranjang,cekalasanretur;

    Spinner jeniskeranjang;
    private ArrayList<Origin> jenisstatuslist = new ArrayList<Origin>();
    String spinner_value,alasanfix;
    RelativeLayout content;

    private final int REQUEST_FOR_SEARCHKERANJANG = 1;
    private final int REQUEST_FOR_SCANKERANJANG = 2;
    private final int REQUEST_FOR_SCANTTK = 3;
    private final int REQUEST_FOR_SEARCHALASANRETURCS = 4;

    String idalasanreturcs;

    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_ttkretur);

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
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(InputTTKRetur.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputTTKRetur.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        scan = (Button) findViewById(R.id.scan_button);
        alasan = (Spinner)findViewById(R.id.alasan_retur);

        inputkeranjang=(EditText)findViewById(R.id.input_keranjang);
        scankeranjang=(Button)findViewById(R.id.scan_buttonkeranjang);

        jeniskeranjang=(Spinner)findViewById(R.id.jeniskeranjang);
        cekalasanretur=(Button)findViewById(R.id.cekalasanretur);

        content=(RelativeLayout)findViewById(R.id.content);
        alasanreturtext=(EditText)findViewById(R.id.alasan_returtext);
        idalasanreturtext=(EditText)findViewById(R.id.id_alasanreturtext);


        alasanreturtextcs = (EditText) findViewById(R.id.alasan_returtextcs);


        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);





        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);

        pengisi.setText(user_id);

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);

        inputVerifikasi = (EditText) findViewById(R.id.input_ttk);
        this.setTitle("");

        inputVerifikasi.setTypeface(type);




        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateKode()) {
                    return;
                }

//                checkkeranjang();

                submitFormv2();



            }
        });



        cekalasanretur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                cekAlasanRetur();

                cekAlasanReturv2();


//                tesret();

//                Log.d("tesr",""+session.getKeyTokenJWT());

            }
        });

        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                launchActivity(BarcodeScannerActivity.class);

                startActivityForResult(new Intent(InputTTKRetur.this,BarcodeScannerActivity.class),REQUEST_FOR_SCANTTK);
            }
        });


        scankeranjang.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                launchActivityKeranjang(BarcodeScannerActivity.class,"keranjang");

                startActivityForResult(new Intent(InputTTKRetur.this,BarcodeScannerActivity.class),REQUEST_FOR_SCANKERANJANG);
            }
        });


        inputkeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(InputTTKRetur.this,SearchKeranjang.class);
//                startActivity(i);
                startActivityForResult(new Intent(InputTTKRetur.this,SearchKeranjang.class),REQUEST_FOR_SEARCHKERANJANG);
            }
        });


        alasanreturtextcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(InputTTKRetur.this,SearchKeranjang.class);
//                startActivity(i);
                startActivityForResult(new Intent(InputTTKRetur.this,SearchAlasanReturCS.class),REQUEST_FOR_SEARCHALASANRETURCS);
            }
        });

//        getJenisKeranjang();
        getJenisKeranjangv2();




        jeniskeranjang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                spinner_value = jenisstatuslist.get(position).getId().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });







        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getAlasanRetur");
        parameter.add("0");
        parameter.add("0");
        parameter.add("nik");
        parameter.add("");
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(InputTTKRetur.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resMessage").toString();
                        if (code.equals("1")) {
                            String resData = result.getProperty("resData").toString();

                            JSONObject jsonObj = new JSONObject(resData);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String kode = isi.getString(0);
                                            String nama = isi.getString(1);
                                            Origin prov = new Origin();
                                            prov.setProvince(kode);
                                            prov.setCity(nama);
                                            prov.setId(""+i);
                                            returList.add(prov);
                                        }else{
                                            Origin prov = new Origin();
                                            prov.setProvince("0");
                                            prov.setCity("--Pilih--");
                                            prov.setId(""+i);
                                            returList.add(prov);
                                        }
                                    }
                                    populateSpinner();
                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(InputTTKRetur.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(InputTTKRetur.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(InputTTKRetur.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil soap data", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(InputTTKRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);

    }


    private void tesret(){

        Call<aoAuthUser> result = mApiInterface.aoAuthUser("aoAuthUser","androidoops","11181155","andro o");

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        result.enqueue(new Callback<aoAuthUser>() {

            @Override
            public void onResponse(Call<aoAuthUser> call, Response<aoAuthUser> response) {

                progressDialog.dismiss();



//                aoAuthUser data = response.body();
//                List<aoAuthUser_Result> datalist = new ArrayList<>();
//
//                datalist = data.getData().getJwt();






                if(response.isSuccessful()) {

//                    btnInput.setEnabled(true);

                    try {

                        progressDialog.dismiss();

                        Log.d("retro",""+response.body().getData().getBenm());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{

                    progressDialog.dismiss();

                    Log.d("error1",""+response.code());

                    Toast.makeText(InputTTKRetur.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();


                }



            }

            @Override
            public void onFailure(Call<aoAuthUser> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                Log.d("error2",""+t.toString());

                Toast.makeText(InputTTKRetur.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();

            }
        });



    }


    private void cekAlasanReturv2(){

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking...");
        progressDialog.show();

        Call<aoCekAlasanRetur> result = mApiInterface.aoCekAlasanRetur("aoCekAlasanRetur",getString(R.string.partnerid),inputVerifikasi.getText().toString().toUpperCase(),session.getKeyTokenJWT());

        result.enqueue(new Callback<aoCekAlasanRetur>() {

            @Override
            public void onResponse(Call<aoCekAlasanRetur> call, Response<aoCekAlasanRetur> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        Log.d("retro",""+response.body().getCode());

                        if (response.body().getCode().equals("0")) {

                            aoCekAlasanRetur data = response.body();
                            List<aoCekAlasanRetur_Result> dataList = new ArrayList<>();
                            dataList = data.getData();

                            Log.d("retro", "" + dataList.get(0).getIdketeranganretur());

                            cekalasanretur.setVisibility(View.GONE);
                            content.setVisibility(View.VISIBLE);

                            alasan.setVisibility(View.GONE);
                            alasanreturtext.setVisibility(View.VISIBLE);

                            alasanreturtext.setText(dataList.get(0).getKeteranganretur());
                            idalasanreturtext.setText(dataList.get(0).getIdketeranganretur());

//                        Toast.makeText(InputTTKRetur.this, ""+dataList.get(0).getKeteranganretur()+"  "+dataList.get(0).getIdketeranganretur(), Toast.LENGTH_SHORT).show();


                        }else{

//                            Toast.makeText(InputTTKRetur.this, ""+response.body().getAlert(), Toast.LENGTH_SHORT).show();

                            new SweetAlertDialog(InputTTKRetur.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(response.body().getAlert())
                                    .show();


                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{
//
//
                    Log.d("error1",""+response.code());

//                    Toast.makeText(InputTTKRetur.this, "Error , Data tidak ditemukan", Toast.LENGTH_SHORT).show();

                    new SweetAlertDialog(InputTTKRetur.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<aoCekAlasanRetur> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                Log.d("error2",""+t.toString());

//                Toast.makeText(InputTTKRetur.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();

//                Toast.makeText(InputTTKRetur.this, "Error , Data tidak ditemukan", Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(InputTTKRetur.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }


    private void cekAlasanRetur(){
//        cekalasanretur.setVisibility(View.GONE);
//        content.setVisibility(View.VISIBLE);
//
//        if(inputVerifikasi.getText().toString().equals("ABC12345")){
//            alasan.setVisibility(View.VISIBLE);
//            alasanreturtext.setVisibility(View.GONE);
//        }else{
//            alasan.setVisibility(View.GONE);
//            alasanreturtext.setVisibility(View.VISIBLE);
//        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("cekAlasanReturdariOps");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBTNO");
        parameter.add(inputVerifikasi.getText().toString().toUpperCase());
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
                            Toast.makeText(InputTTKRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                JSONArray msd = data.getJSONArray(1);
                                String ttklama=msd.getString(1);
                                String keteranganretur = msd.getString(2);
                                String idketeranganretur = msd.getString(3);

//                                Log.d("jenis",""+keteranganretur+" "+idketeranganretur);

                                if (ttklama == "null"){
                                    Toast.makeText(InputTTKRetur.this, "TTK Tidak Ditemukan",Toast.LENGTH_LONG).show();
                                }else{

                                    cekalasanretur.setVisibility(View.GONE);
                                    content.setVisibility(View.VISIBLE);

                                    if (idketeranganretur == "null" || idketeranganretur.equals("")){

                                        alasan.setVisibility(View.VISIBLE);
                                        alasanreturtext.setVisibility(View.GONE);


                                    }else{

                                        alasan.setVisibility(View.GONE);
                                        alasanreturtext.setVisibility(View.VISIBLE);

                                        alasanreturtext.setText(keteranganretur);
                                        idalasanreturtext.setText(idketeranganretur);

//                                        Log.d("hasilretur",""+alasanreturtext.getText().toString()+" "+idalasanreturtext.getText().toString());
                                    }
                                }

                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(InputTTKRetur.this, "TTK tidak ditemukan",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(InputTTKRetur.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(InputTTKRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);



    }


    private void getJenisKeranjangv2(){

        Call<aoGetJenisKeranjang> result = mApiInterface.aoGetJenisKeranjang("aoGetJenisKeranjang",getString(R.string.partnerid),session.getKeyTokenJWT());

        result.enqueue(new Callback<aoGetJenisKeranjang>() {

            @Override
            public void onResponse(Call<aoGetJenisKeranjang> call, Response<aoGetJenisKeranjang> response) {

                progressDialog.dismiss();


                if(response.isSuccessful()) {

                    try {


                        Log.d("retro",""+response.body().getCode());

                        aoGetJenisKeranjang data = response.body();
                        List<aoGetJenisKeranjang_Result> dataList = new ArrayList<>();
                        dataList = data.getData();


                        if(dataList.size() > 0) {

                            Origin prov1 = new Origin();
                            prov1.setId("0");
                            prov1.setCity("--Pilih--");
                            jenisstatuslist.add(prov1);


                            for (int i = 0; i < dataList.size(); i++) {
                                Log.d("fit1", "" + dataList.get(i).getATRLVNM());

                                    Origin prov = new Origin();
                                    prov.setId(dataList.get(i).getATRLVCD());
                                    prov.setCity(dataList.get(i).getATRLVNM());
                                    jenisstatuslist.add(prov);

                            }

                        }else{

                            Origin prov = new Origin();
                            prov.setId("0");
                            prov.setCity("--Pilih--");
                            jenisstatuslist.add(prov);
                        }

                        populateSpinnerJenisKeranjang();




                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{

                    progressDialog.dismiss();

                    Log.d("error1",""+response.code());

                    Toast.makeText(InputTTKRetur.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();


                }



            }

            @Override
            public void onFailure(Call<aoGetJenisKeranjang> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                Log.d("error2",""+t.toString());

                Toast.makeText(InputTTKRetur.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();

            }
        });
    }




    private void getJenisKeranjang(){
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getJenisKeranjang");
        parameter.add("0");
        parameter.add("50");
        parameter.add("0");
        parameter.add("0");
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(InputTTKRetur.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resMessage").toString();
                        if (code.equals("1")) {
                            String resData = result.getProperty("resData").toString();

                            JSONObject jsonObj = new JSONObject(resData);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String id = isi.getString(0);
                                            String nama = isi.getString(1);
//                                            String harga = isi.getString(2);
                                            Origin prov = new Origin();
                                            prov.setId(id);
                                            prov.setCity(nama);
//                                            prov.setProvince(harga);
//                                            prov.setId(""+i);
                                            jenisstatuslist.add(prov);
                                        }else{
                                            Origin prov = new Origin();
                                            prov.setId("0");
                                            prov.setCity("--Pilih--");
//                                            prov.setProvince("0");
                                            jenisstatuslist.add(prov);
                                        }
                                    }
                                    populateSpinnerJenisKeranjang();
                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(InputTTKRetur.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(InputTTKRetur.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(InputTTKRetur.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil soap data", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(InputTTKRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }





    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < returList.size(); i++) {
            if (i==0){
                lables.add(returList.get(i).getCity());
            }else{
                lables.add(returList.get(i).getId()+". "+returList.get(i).getCity());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_list, lables);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        alasan.setAdapter(spinnerAdapter);
    }



    private void populateSpinnerJenisKeranjang() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < jenisstatuslist.size(); i++) {
            if (i==0){
                lables.add(jenisstatuslist.get(i).getCity());
            }else{
                lables.add(jenisstatuslist.get(i).getCity());
            }

            Log.d("fit11",""+jenisstatuslist.get(i).getCity());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_list, lables);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        jeniskeranjang.setAdapter(spinnerAdapter);
    }



    private void submitFormv2(){

        Call<aoCheckSTMRetur> result = mApiInterface.aoCheckSTMRetur("aoCheckSTMRetur","androidoops",inputVerifikasi.getText().toString().toUpperCase(),session.getKeyTokenJWT());

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        result.enqueue(new Callback<aoCheckSTMRetur>() {

            @Override
            public void onResponse(Call<aoCheckSTMRetur> call, Response<aoCheckSTMRetur> response) {

                progressDialog.dismiss();





                if(response.isSuccessful()) {


                    try {


                        if (response.body().getCode().equals("0")) {


                            Intent pindah = new Intent(InputTTKRetur.this, PenerimaanReturV2.class);
                            pindah.putExtra("ttk", inputVerifikasi.getText().toString().toUpperCase());
                            pindah.putExtra("alasan", idalasanreturtext.getText().toString());
                            pindah.putExtra("nokeranjang", inputkeranjang.getText().toString().toUpperCase());
                            pindah.putExtra("jeniskeranjang", spinner_value);
                            pindah.putExtra("alasanreturcs", idalasanreturcs);
                            startActivity(pindah);
                            finish();

                        }else{

                            new SweetAlertDialog(InputTTKRetur.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(response.body().getAlert())
                                    .show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{

                    progressDialog.dismiss();

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(InputTTKRetur.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<aoCheckSTMRetur> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                Log.d("error2",""+t.toString());

//                Toast.makeText(InputTTKRetur.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(InputTTKRetur.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });


    }


    private void submitForm() {

        Log.d("jenis",""+spinner_value);

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("checkSTMReturv2");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBTNO");
        parameter.add(inputVerifikasi.getText().toString().toUpperCase());
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
                            Toast.makeText(InputTTKRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                JSONArray msd = data.getJSONArray(1);
                                String internal = msd.getString(0);
                                String retur = msd.getString(1);
                                if (retur == "null"){
                                    if (internal == "null"){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(InputTTKRetur.this, "TTK belum dibuatkan TTK internal",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else{

                                          if (idalasanreturtext.getText().toString().equals("")){
                                              alasanfix=alasanIsi;
                                          }else{
                                              alasanfix=idalasanreturtext.getText().toString();
                                          }

                                            Intent pindah = new Intent(InputTTKRetur.this, PenerimaanReturV2.class);
                                            pindah.putExtra("ttk",inputVerifikasi.getText().toString().toUpperCase());
                                            pindah.putExtra("alasan",alasanfix);
                                            pindah.putExtra("nokeranjang",inputkeranjang.getText().toString().toUpperCase());
                                            pindah.putExtra("jeniskeranjang",spinner_value);
                                            startActivity(pindah);
                                            finish();

                                    }
                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(InputTTKRetur.this, "TTK sudah diproses retur",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(InputTTKRetur.this, "TTK tidak ditemukan",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(InputTTKRetur.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(InputTTKRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }



    private void checkkeranjang() {

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("cekkeranjang");
        parameter.add("0");
        parameter.add("0");
        parameter.add("noker");
        parameter.add(inputkeranjang.getText().toString().toUpperCase());
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
                            Toast.makeText(InputTTKRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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


//                                Toast.makeText(InputTTKRetur.this, "No Keranjang ditemukan",Toast.LENGTH_LONG).show();

                                alasanIsi = returList.get(alasan.getSelectedItemPosition()).getProvince().toString();

//
//                                if(alasanIsi.equals("R04")){
//                                    Intent pindah = new Intent(InputTTKRetur.this, PenerimaanReturV2.class);
//                                    pindah.putExtra("ttk",inputVerifikasi.getText().toString().toUpperCase());
//                                    pindah.putExtra("alasan",alasanIsi);
//                                    pindah.putExtra("nokeranjang",inputkeranjang.getText().toString().toUpperCase());
//                                    startActivity(pindah);
//                                    finish();
//                                }else{
//                                    submitForm();
//                                }

                                submitForm();




//                                JSONArray msd = data.getJSONArray(1);
//                                String internal = msd.getString(0);
//                                String retur = msd.getString(1);
//                                if (retur == "null"){
//                                    if (internal == "null"){
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                Toast.makeText(InputTTKRetur.this, "TTK belum dibuatkan TTK internal",Toast.LENGTH_LONG).show();
//                                            }
//                                        });
//                                    }else{
//                                        Intent pindah = new Intent(InputTTKRetur.this, PenerimaanReturV2.class);
//                                        pindah.putExtra("ttk",inputVerifikasi.getText().toString().toUpperCase());
//                                        pindah.putExtra("alasan",alasanIsi);
//                                        startActivity(pindah);
//                                        finish();
//
//                                    }
//                                }else {
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            Toast.makeText(InputTTKRetur.this, "TTK sudah diproses retur",Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }

                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(InputTTKRetur.this, "No Keranjang tidak ditemukan",Toast.LENGTH_LONG).show();
                                    }
                                });

//                                alasanIsi = returList.get(alasan.getSelectedItemPosition()).getProvince().toString();
//
//                                if(alasanIsi.equals("R04")){
//                                    Intent pindah = new Intent(InputTTKRetur.this, PenerimaanReturV2.class);
//                                    pindah.putExtra("ttk",inputVerifikasi.getText().toString().toUpperCase());
//                                    pindah.putExtra("alasan",alasanIsi);
//                                    pindah.putExtra("nokeranjang",inputkeranjang.getText().toString().toUpperCase());
//                                    startActivity(pindah);
//                                    finish();
//                                }else{
//                                    submitForm();
//                                }


                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(InputTTKRetur.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(InputTTKRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }





    private boolean validateKode() {


        if (idalasanreturtext.getText().toString().equals("")){


            if (inputVerifikasi.getText().toString().trim().isEmpty()) {
                inputVerifikasi.setError("Masukkan No. TTK");

                return false;
            } else {
                inputVerifikasi.setError(null);
            }

            if (inputkeranjang.getText().toString().trim().isEmpty()) {
                inputkeranjang.setError("Masukkan Nomor Keranjang");

                return false;
            } else {
                inputkeranjang.setError(null);
            }

            if(returList.get(alasan.getSelectedItemPosition()).getProvince().toString().equals("0")){
                Toast.makeText(InputTTKRetur.this, "Pilih alasan retur",Toast.LENGTH_SHORT).show();

                return false;
            }


            if(jenisstatuslist.get(jeniskeranjang.getSelectedItemPosition()).getId().toString().equals("0")){
                Toast.makeText(InputTTKRetur.this, "Pilih Jenis",Toast.LENGTH_SHORT).show();

                return false;
            }

            return true;


        }else{

            if (inputVerifikasi.getText().toString().trim().isEmpty()) {
                inputVerifikasi.setError("Masukkan No. TTK");

                return false;
            } else {
                inputVerifikasi.setError(null);
            }

            if (inputkeranjang.getText().toString().trim().isEmpty()) {
                inputkeranjang.setError("Masukkan Nomor Keranjang");

                return false;
            } else {
                inputkeranjang.setError(null);
            }

            if (alasanreturtext.getText().toString().trim().isEmpty()) {
                alasanreturtext.setError("Masukkan Alasan Retur");

                return false;
            } else {
                alasanreturtext.setError(null);
            }


            if (alasanreturtextcs.getText().toString().trim().isEmpty()) {
                alasanreturtextcs.setError("Masukkan Alasan Retur CS");

                return false;
            } else {
                alasanreturtextcs.setError(null);
            }





            if(jenisstatuslist.get(jeniskeranjang.getSelectedItemPosition()).getId().toString().equals("0")){
                Toast.makeText(InputTTKRetur.this, "Pilih Jenis",Toast.LENGTH_SHORT).show();

                return false;
            }

            return true;

        }




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, 3);
        }
    }

    public void launchActivityKeranjang(Class<?> clss,String keranjang) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            intent.putExtra("keranjang",keranjang);
            startActivityForResult(intent, 3);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
//            String nilai=data.getStringExtra("ttk");
//            String noker=data.getStringExtra("keranjang");

            switch (requestCode){
                case REQUEST_FOR_SCANTTK:
                    String nilai=data.getStringExtra("ttk");
                    inputVerifikasi.setText(nilai);
                    break;

                case REQUEST_FOR_SCANKERANJANG:
                    String keranjang=data.getStringExtra("ttk");
                    inputkeranjang.setText(keranjang);
                    break;

                case REQUEST_FOR_SEARCHKERANJANG:
                    String textkeranjang=data.getStringExtra("textkeranjang");
                    inputkeranjang.setText(textkeranjang);
                    break;

                case REQUEST_FOR_SEARCHALASANRETURCS:
                    String textalasanreturcs=data.getStringExtra("textalasanreturcs");

                    idalasanreturcs = data.getStringExtra("idalasanretur");
                    alasanreturtextcs.setText(textalasanreturcs);
                    break;


            }

//            if(noker != null) {
//                inputkeranjang.setText(nilai);
//            }else {
//                inputVerifikasi.setText(nilai);
//            }
        }
    }

    public boolean isTTK(String ttk){

        return ttk.matches("^([A-Z][A-Z][A-Z])([0-9][0-9][0-9][0-9][0-9])$");
    }


//    public static void hideSoftKeyboard(Activity activity) {
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) activity.getSystemService(
//                        Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(
//                activity.getCurrentFocus().getWindowToken(), 0);
//    }





}
