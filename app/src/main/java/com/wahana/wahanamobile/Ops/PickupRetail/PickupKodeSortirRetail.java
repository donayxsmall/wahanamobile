package com.wahana.wahanamobile.Ops.PickupRetail;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.AgentCode;
import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoGenTTKPickupOpsRetail;
import com.wahana.wahanamobile.ModelApiOPS.PickupScan.aoGenTTKPickupOpsRetail_Result;
import com.wahana.wahanamobile.Ops.Pickup.PickupKodeSortir;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.ApiPickup;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickupKodeSortirRetail extends AppCompatActivity {

    private static final String TAG = "PickupKodeSortirRetail";
    ProgressDialog progressDialog;
    SessionManager session;
    DatabaseHandler db;
    public EditText inputkodesortir;
    private Button btnInput;
    TextView kode_kurir, kode_agent;
    TextView pengisi, tgl, calendar;
    String username, user_id,agentcode;
//    UserAPIService mApiInterface;
    String kodeverifikasi,flagsortir,kodesortir;

    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_kode_sortir_retail);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(PickupKodeSortirRetail.this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        Intent intent = getIntent();
        kodeverifikasi=intent.getStringExtra("kodeverifikasi");
        flagsortir=intent.getStringExtra("flagsortir");


//        mApiInterface = ApiClient.getClient().create(UserAPIService.class);
        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(PickupKodeSortirRetail.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(PickupKodeSortirRetail.this);
        user_id = session.getID();
        db = new DatabaseHandler(PickupKodeSortirRetail.this);
        AgentCode kode = db.getKodeagent();
        agentcode = kode.getAgentCode();

        btnInput = (Button) findViewById(R.id.input_button);

        kode_agent = (TextView) findViewById(R.id.kode_agen);
        kode_agent.setText(agentcode);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);

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
        kode_agent.setTypeface(type);

        inputkodesortir = (EditText) findViewById(R.id.input_kodesortir);
        inputkodesortir.setTypeface(type);



        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateKode()) {
                    return;
                }

//                submitForm();

                submitFormv2();

//                Intent intent = new Intent(PickupKodeSortirRetail.this, PickupScannerRetail.class);
//                intent.putExtra("kodeagen", agentcode);
//                intent.putExtra("kodeverifikasi", kodeverifikasi);
//                intent.putExtra("kodesortir", inputkodesortir.getText().toString());
//                startActivity(intent);
//                finish();

            }
        });

    }


    public void submitFormv2(){

        kodesortir = inputkodesortir.getText().toString().toUpperCase();

        Call<aoGenTTKPickupOpsRetail> result = mApiInterface.aoGenTTKPickupOpsRetail(
                "aoGenTTKPickupOpsRetail",
                getString(R.string.partnerid),
                session.getID(),
                agentcode,
                kodesortir,
                session.getSessionID(),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        result.enqueue(new Callback<aoGenTTKPickupOpsRetail>() {

            @Override
            public void onResponse(Call<aoGenTTKPickupOpsRetail> call, Response<aoGenTTKPickupOpsRetail> response) {

                Log.d("error",""+response.body().getText());

                progressDialog.dismiss();

                if(response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();


                        if (vrcode.equals("0")) {
//
                            aoGenTTKPickupOpsRetail data = response.body();
                            List<aoGenTTKPickupOpsRetail_Result> dataList = new ArrayList<>();
                            dataList = data.getData();

                            String vurl = dataList.get(0).getUrlfile();
                            String vFile = dataList.get(0).getFilename();

                            db.deleteListSumberPickup();

                            new DownloadTask(PickupKodeSortirRetail.this, btnInput, vurl, "pu",vFile);

                            int histPickup = db.checkListPickupHistory(agentcode, kodeverifikasi, kodesortir);

                            if (histPickup == 1) {

                                ListTtkPickup ttk = db.getTTKhistoryMax(agentcode, kodeverifikasi, kodesortir);

                                int jumlahscan = db.countPUlistScan(agentcode, kodeverifikasi, kodesortir);

                                final AlertDialog.Builder adb = new AlertDialog.Builder(PickupKodeSortirRetail.this);
                                adb.setTitle("Info");
                                adb.setMessage("Proses Pickup terakhir belum selesai , Sudah di scan " + jumlahscan + " ttk dengan nomor ttk terakhir " + ttk.getTtk() + ", Apakah anda ingin melanjutkan?");
                                adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteListPickup(agentcode, kodeverifikasi, kodesortir);

                                        Intent intent = new Intent(PickupKodeSortirRetail.this, PickupScannerRetail.class);
                                        intent.putExtra("kodeagen", agentcode);
                                        intent.putExtra("kodeverifikasi", kodeverifikasi);
                                        intent.putExtra("kodesortir", kodesortir);
                                        intent.putExtra("flagsortir", flagsortir);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                                adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //db.deleteListPickup();
                                        Intent intent = new Intent(PickupKodeSortirRetail.this, PickupScannerRetail.class);
                                        intent.putExtra("kodeagen", agentcode);
                                        intent.putExtra("kodeverifikasi", kodeverifikasi);
                                        intent.putExtra("kodesortir", kodesortir);
                                        intent.putExtra("flagsortir", flagsortir);
                                        startActivity(intent);
                                        finish();
                                    }
                                });


                                adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //db.deleteListPickup();
//                                        Toast.makeText(getApplicationContext(), "neutralize", Toast.LENGTH_SHORT).show();
                                        Intent pindah = new Intent(PickupKodeSortirRetail.this, PreviewResultScanner.class);
                                        pindah.putExtra("asal", "pu");
                                        pindah.putExtra("kodeagen", agentcode);
                                        pindah.putExtra("kodeverifikasi", kodeverifikasi);
                                        pindah.putExtra("kodesortir", kodesortir);
                                        pindah.putExtra("flagsortir", flagsortir);
                                        startActivity(pindah);

                                    }
                                });

                                final AlertDialog alert = adb.create();
                                alert.show();


                            } else {

                                Intent intent = new Intent(PickupKodeSortirRetail.this, PickupScannerRetail.class);
                                intent.putExtra("kodeagen", agentcode);
                                intent.putExtra("kodeverifikasi", kodeverifikasi);
                                intent.putExtra("kodesortir", kodesortir);
                                intent.putExtra("flagsortir", flagsortir);
                                startActivity(intent);
                                finish();

                            }







                        } else {


                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(PickupKodeSortirRetail.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(PickupKodeSortirRetail.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(PickupKodeSortirRetail.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<aoGenTTKPickupOpsRetail> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(PickupKodeSortirRetail.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });


    }


//    public void submitForm(){
//
//        Log.d("request",""+agentcode+" "+inputkodesortir.getText().toString());
//
//        Call<ApiPickup> result = mApiInterface.createttkPickup(agentcode,inputkodesortir.getText().toString());
//
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();
//
//        result.enqueue(new Callback<ApiPickup>() {
//
//            @Override
//            public void onResponse(Call<ApiPickup> call, Response<ApiPickup> response) {
//
//                if(response.isSuccessful()) {
//
//                    try {
//
//                        progressDialog.dismiss();
//
//                        String vrcode = response.body().getVrcode();
//                        String vrmsg = response.body().getVrmesg();
//                        String vurl = response.body().getvUrl();
//                        String vFile = response.body().getvFile();
//
//                        Log.d("retro", "" + vurl +"|"+vFile);
//
//                        if (vrcode.equals("1")) {
////
//                            db.deleteListSumberPickup();
//
//                            new DownloadTask(PickupKodeSortirRetail.this, btnInput, vurl, "pu",vFile);
//
//                            Log.d("retro", "" + vrcode + "|" + vrmsg + "" + vurl);
//
//                            int histPickup = db.checkListPickupHistory(agentcode, kodeverifikasi, inputkodesortir.getText().toString());
//
//                            if (histPickup == 1) {
//
//                                ListTtkPickup ttk = db.getTTKhistoryMax(agentcode, kodeverifikasi, inputkodesortir.getText().toString());
//
//                                int jumlahscan = db.countPUlistScan(agentcode, kodeverifikasi, inputkodesortir.getText().toString());
//
//                                final AlertDialog.Builder adb = new AlertDialog.Builder(PickupKodeSortirRetail.this);
//                                adb.setTitle("Info");
//                                adb.setMessage("Proses Pickup terakhir belum selesai , Sudah di scan " + jumlahscan + " ttk dengan nomor ttk terakhir " + ttk.getTtk() + ", Apakah anda ingin melanjutkan?");
//                                adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        db.deleteListPickup(agentcode, kodeverifikasi, inputkodesortir.getText().toString());
//
//                                        Intent intent = new Intent(PickupKodeSortirRetail.this, PickupScannerRetail.class);
//                                        intent.putExtra("kodeagen", agentcode);
//                                        intent.putExtra("kodeverifikasi", kodeverifikasi);
//                                        intent.putExtra("kodesortir", inputkodesortir.getText().toString());
//                                        startActivity(intent);
//                                        finish();
//
//                                    }
//                                });
//                                adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        //db.deleteListPickup();
//                                        Intent intent = new Intent(PickupKodeSortirRetail.this, PickupScannerRetail.class);
//                                        intent.putExtra("kodeagen", agentcode);
//                                        intent.putExtra("kodeverifikasi", kodeverifikasi);
//                                        intent.putExtra("kodesortir", inputkodesortir.getText().toString());
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                });
//
//
//                                adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        //db.deleteListPickup();
////                                        Toast.makeText(getApplicationContext(), "neutralize", Toast.LENGTH_SHORT).show();
//                                        Intent pindah = new Intent(PickupKodeSortirRetail.this, PreviewResultScanner.class);
//                                        pindah.putExtra("asal", "pu");
//                                        pindah.putExtra("kodeagen", agentcode);
//                                        pindah.putExtra("kodeverifikasi", kodeverifikasi);
//                                        pindah.putExtra("kodesortir", inputkodesortir.getText().toString());
//                                        startActivity(pindah);
//
//                                    }
//                                });
//
//                                final AlertDialog alert = adb.create();
//                                alert.show();
//
//
//                            } else {
//
//                                Intent intent = new Intent(PickupKodeSortirRetail.this, PickupScannerRetail.class);
//                                intent.putExtra("kodeagen", agentcode);
//                                intent.putExtra("kodeverifikasi", kodeverifikasi);
//                                intent.putExtra("kodesortir", inputkodesortir.getText().toString());
//                                startActivity(intent);
//                                finish();
//
//                            }
//
//
//                        } else {
//                            Toast.makeText(PickupKodeSortirRetail.this, vrmsg, Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }else{
//
//                    progressDialog.dismiss();
//
//                    try {
//                        Toast.makeText(PickupKodeSortirRetail.this, " error " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        Toast.makeText(PickupKodeSortirRetail.this, " unknown error", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ApiPickup> call, Throwable t) {
//                progressDialog.dismiss();
//                t.printStackTrace();
//                Log.d("error",""+t.toString());
//            }
//        });
//
//    }

    private boolean validateKode() {
        if (inputkodesortir.getText().toString().trim().isEmpty()) {
            inputkodesortir.setError("Masukkan Kode Sortir");

            return false;
        } else {
            inputkodesortir.setError(null);
        }

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}