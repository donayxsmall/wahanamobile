package com.wahana.wahanamobile.Ops.PickupRetail;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoCreateMSPUOpsRetail;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoCreateMSPUOpsRetail_Result;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoPickupBeginOpsRetail;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoPickupKodeVerTTKBatal;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.STMSRETUR.InputTTKRetur;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KodeVerifikasiTTKBatal extends AppCompatActivity {

    private static final String TAG = "inputTTKRetur";

    ProgressDialog progressDialog;
    public EditText inputVerifikasi;
    private Button btnInput;
    TextView pengisi, tgl, calendar,kode_agent;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String user_id,alasanIsi;
    Button scan;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    Spinner alasan;
    private ArrayList<Origin> returList = new ArrayList<Origin>();
    EditText kodeverifikasi,alasanreturtext,idalasanreturtext;
    Button scankeranjang,cekalasanretur;

    Spinner jeniskeranjang;
    private ArrayList<Origin> jenisstatuslist = new ArrayList<Origin>();
    String spinner_value,alasanfix;
    RelativeLayout content;
    String hasilttk,agentcode,kodeverifikasipickup,kodesortir,flagsortir;
    JSONObject jsonlokasi;
    RequestApiWahanaOps mApiInterface;

    DatabaseHandler db;

    JSONObject ttklist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kode_verifikasi_ttkbatal);

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

        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);

        Intent intent = getIntent();
        hasilttk = intent.getStringExtra("hasilttk");
        kodeverifikasipickup = intent.getStringExtra("kodeverifikasi");
        kodesortir = intent.getStringExtra("kodesortir");
        flagsortir = intent.getStringExtra("flagsortir");


        try {
            ttklist = new JSONObject(hasilttk);
            Log.d("ttkl3",""+ttklist+" "+jsonlokasi);
        } catch (JSONException e) {
            e.printStackTrace();
        }




        progressDialog = new ProgressDialog(KodeVerifikasiTTKBatal.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(KodeVerifikasiTTKBatal.this);
        user_id = session.getID();
        db = new DatabaseHandler(KodeVerifikasiTTKBatal.this);

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        scan = (Button) findViewById(R.id.scan_button);
        kodeverifikasi = (EditText) findViewById(R.id.kodeverifikasi_ttkbatal);
        AgentCode kode = db.getKodeagent();
        agentcode = kode.getAgentCode();

        kode_agent = (TextView) findViewById(R.id.kode_agen);
        kode_agent.setText(agentcode);

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

        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchActivity(BarcodeScannerActivity.class);
            }
        });

        btnInput.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

//                Intent pindah = new Intent(KodeVerifikasiTTKBatal.this, HasilProses.class);
//                pindah.putExtra("proses","pu");
//                pindah.putExtra("no", "PU-1212-12121");
//                startActivity(pindah);
//                finish();
                if (!validateKode()) {
                    return;
                }

                submitform();

//                Log.d("ttkl4",""+jsonlokasi);

            }
        });


    }


    private void submitform(){

        Call<aoPickupKodeVerTTKBatal> result = mApiInterface.aoPickupKodeVerTTKBatal(
                "aoPickupKodeVerTTKBatal",
                getString(R.string.partnerid),
                session.getID(),
                agentcode,
                kodeverifikasi.getText().toString(),
                session.getSessionID(),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        btnInput.setEnabled(false);

        result.enqueue(new Callback<aoPickupKodeVerTTKBatal>() {

            @Override
            public void onResponse(Call<aoPickupKodeVerTTKBatal> call, Response<aoPickupKodeVerTTKBatal> response) {

                Log.d("error",""+response.body().getText());

                progressDialog.dismiss();
                btnInput.setEnabled(true);

                if(response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();

                        if (vrcode.equals("0")) {


                            AlertDialog.Builder adb=new AlertDialog.Builder(KodeVerifikasiTTKBatal.this);
                            adb.setTitle("Konfirmasi");
                            adb.setMessage("Apakah anda yakin Data yang diinput untuk dibuat MSPU sudah sesuai ");

                            adb.setNegativeButton("Cancel", null);
                            adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    submitpu();

                                }});
                            adb.show();


                        } else {

                            Log.d("error3",""+response.body().getText());

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(KodeVerifikasiTTKBatal.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();

                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(KodeVerifikasiTTKBatal.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(KodeVerifikasiTTKBatal.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<aoPickupKodeVerTTKBatal> call, Throwable t) {
                progressDialog.dismiss();
                btnInput.setEnabled(true);
                t.printStackTrace();

                new SweetAlertDialog(KodeVerifikasiTTKBatal.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }


    public void submitpu(){

        Call<aoCreateMSPUOpsRetail> result = mApiInterface.aoCreateMSPUOpsRetail(
                "aoCreateMSPUOpsRetail",
                getString(R.string.partnerid),
                session.getID(),
//                "11181155",
                agentcode,
                ttklist,
                jsonlokasi,
                getString(R.string.versionpu),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        btnInput.setEnabled(false);

        result.enqueue(new Callback<aoCreateMSPUOpsRetail>() {

            @Override
            public void onResponse(Call<aoCreateMSPUOpsRetail> call, Response<aoCreateMSPUOpsRetail> response) {

                Log.d("error",""+response.body().getText());

                progressDialog.dismiss();
                btnInput.setEnabled(true);

                if(response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();

                        if (vrcode.equals("0")) {

                            aoCreateMSPUOpsRetail data = response.body();
                            List<aoCreateMSPUOpsRetail_Result> dataList = new ArrayList<>();
                            dataList = data.getData();

                            String nomorpu = dataList.get(0).getNomorpu();
                            String urlfilepu = dataList.get(0).getUrlfilepu();
                            String flagstat = dataList.get(0).getFlagstat();

                            Intent pindah = new Intent(KodeVerifikasiTTKBatal.this, HasilProses.class);
                            pindah.putExtra("proses","pu-scan");
                            pindah.putExtra("no", nomorpu);
                            pindah.putExtra("urlfilepu", urlfilepu);
                            pindah.putExtra("kodeagen", agentcode);
                            pindah.putExtra("kodesortir", kodesortir);
                            pindah.putExtra("kodeverifikasi", kodeverifikasipickup);
                            pindah.putExtra("flagsortir", flagsortir);
                            pindah.putExtra("flagstat", flagstat);
                            startActivity(pindah);
                            finish();


                        } else {

                            Log.d("error3",""+response.body().getText());

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(KodeVerifikasiTTKBatal.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();

                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(KodeVerifikasiTTKBatal.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(KodeVerifikasiTTKBatal.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<aoCreateMSPUOpsRetail> call, Throwable t) {
                progressDialog.dismiss();
                btnInput.setEnabled(true);
                t.printStackTrace();

                new SweetAlertDialog(KodeVerifikasiTTKBatal.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });


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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String nilai=data.getStringExtra("ttk");
                kodeverifikasi.setText(nilai);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        FusedLocation fusedLocation = new FusedLocation(KodeVerifikasiTTKBatal.this, new FusedLocation.Callback(){
            @Override
            public void onLocationResult(Location location){
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

    private boolean validateKode() {
        if (kodeverifikasi.getText().toString().trim().isEmpty()) {
            kodeverifikasi.setError("Masukkan Kode Verifikasi");

            return false;
        } else {
            kodeverifikasi.setError(null);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
