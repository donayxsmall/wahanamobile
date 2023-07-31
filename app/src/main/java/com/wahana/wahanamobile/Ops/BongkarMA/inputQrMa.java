package com.wahana.wahanamobile.Ops.BongkarMA;

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

import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.Data.ListTtkBongkarMA;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2GetBongkarMAProcessList;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2GetTTKMA;
import com.wahana.wahanamobile.ModelApiOPS.StpuMa.opv2GetTTKSTPU;
import com.wahana.wahanamobile.Ops.STMSRETUR.InputTTKRetur;
import com.wahana.wahanamobile.Ops.STPUdirectMA.StpuScannerdirectMa;
import com.wahana.wahanamobile.Ops.STPUdirectMA.inputDataSTPUdirectMa;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class inputQrMa extends AppCompatActivity {
    EditText inputma;
    Button scanbutton,btninput;
    TextView pengisi, tgl, calendar;
    SessionManager session;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private final int REQUEST_FOR_SCANTTK = 1;
    RequestApiWahanaOps mApiInterface;
    String user_id,sessionId;

    private static final String TAG = "inputTTKRetur";

    ProgressDialog progressDialog;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_qr_ma);


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

        db = new DatabaseHandler(inputQrMa.this);
        progressDialog = new ProgressDialog(inputQrMa.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(inputQrMa.this);
        user_id = session.getID();
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);



        inputma = (EditText)findViewById(R.id.input_ma);
        scanbutton = (Button) findViewById(R.id.scan_button);
        btninput = (Button) findViewById(R.id.input_button);


        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);

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

        scanbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                launchActivity(BarcodeScannerActivity.class);

                startActivityForResult(new Intent(inputQrMa.this,BarcodeScannerActivity.class),REQUEST_FOR_SCANTTK);
            }
        });

        btninput.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (!validateKode()) {
                    return;
                }

//                Intent intent = new Intent(inputQrMa.this, ScanBongkarMa.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("nomorMA", "MA12345");
//                intent.putExtra("sessionId", "213213");
//                startActivity(intent);
//                finish();

                submitform();

            }
        });



    }



    private void submitform(){

        Call<opv2GetTTKMA> result = mApiInterface.opv2GetTTKMA(
                "opv2GetTTKMA",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                inputma.getText().toString().toUpperCase(),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2GetTTKMA>() {

            @Override
            public void onResponse(Call<opv2GetTTKMA> call, retrofit2.Response<opv2GetTTKMA> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if(code.equals("0")){



                            String fileurl = response.body().getData().getUrlMA();
                            String downloadFileNamefull = fileurl.substring(fileurl.lastIndexOf('/'), fileurl.length());
                            String filename = downloadFileNamefull.replaceAll("/", "");

                            sessionId = response.body().getData().getSessionId();

                            db.deleteListSumberBONGKARMA();

                            new DownloadTask(inputQrMa.this, btninput, fileurl, "bongkarma",filename);

                            int histPickup = db.checkListBONGKARMAHistory(session.getID(),inputma.getText().toString().toUpperCase());
                            int jumlahscan = db.countBONGKARMAlistScan(session.getID(),inputma.getText().toString().toUpperCase());

                            if (histPickup == 1) {
                                ListTtkBongkarMA ttk = db.getTTKhistoryMaxBONGKARMA(session.getID(),inputma.getText().toString().toUpperCase());

                                AlertDialog.Builder adb = new AlertDialog.Builder(inputQrMa.this);
                                adb.setTitle("Info");
                                adb.setMessage("Proses STMA terakhir belum selesai , Sudah di scan " + jumlahscan + " ttk dengan nomor ttk terakhir " + ttk.getTtk() + ", Apakah anda ingin melanjutkan?");
                                adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteListBONGKARMA(session.getID(),inputma.getText().toString().toUpperCase());

                                        Intent intent = new Intent(inputQrMa.this, ScanBongkarMa.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("nomorMA", inputma.getText().toString().toUpperCase());
                                        intent.putExtra("sessionId", sessionId);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                                adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //db.deleteListPickup();

                                        Intent intent = new Intent(inputQrMa.this, ScanBongkarMa.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("nomorMA", inputma.getText().toString().toUpperCase());
                                        intent.putExtra("sessionId", sessionId);
                                        startActivity(intent);
                                        finish();

                                    }
                                });

                                adb.show();

                            }else{
                                Intent intent = new Intent(inputQrMa.this, ScanBongkarMa.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("nomorMA", inputma.getText().toString().toUpperCase());
                                intent.putExtra("sessionId", sessionId);
                                startActivity(intent);
                                finish();
                            }








                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(inputQrMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(inputQrMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(inputQrMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2GetTTKMA> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(inputQrMa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }

    private boolean validateKode() {
        if (inputma.getText().toString().trim().isEmpty()) {
            inputma.setError("Masukkan Nomor MA");

            return false;
        } else {
            inputma.setError(null);
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
//            String nilai=data.getStringExtra("ttk");
//            String noker=data.getStringExtra("keranjang");

            switch (requestCode){
                case REQUEST_FOR_SCANTTK:
                    String nilai=data.getStringExtra("ttk");
                    inputma.setText(nilai);
                    break;


            }

        }
    }





}
