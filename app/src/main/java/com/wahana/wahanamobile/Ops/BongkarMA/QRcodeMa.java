package com.wahana.wahanamobile.Ops.BongkarMA;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.wahana.wahanamobile.Data.ListTtkBongkarMA;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2GetTTKMA;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2MkBongkarMAFinal;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class QRcodeMa extends AppCompatActivity {

    ImageView qrcode;
    Button lihatproses,finish,stma;
    String nomorMA,sessionId,nikLintas;
    TextView textma;
    ProgressDialog progressDialog;
    SessionManager session;
    DatabaseHandler db;
    RequestApiWahanaOps mApiInterface;
    JSONObject jsonlokasi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_ma);

        Intent intent = getIntent();
        nomorMA=intent.getStringExtra("nomorMA");
        nikLintas = intent.getStringExtra("nikLintas");

        qrcode = (ImageView)findViewById(R.id.qrcode);
        lihatproses = (Button)findViewById(R.id.lihatproses);
        finish = (Button)findViewById(R.id.finish);
        stma = (Button)findViewById(R.id.stma);
        textma = (TextView)findViewById(R.id.textma);

        textma.setText(nomorMA);

        db = new DatabaseHandler(QRcodeMa.this);

        progressDialog = new ProgressDialog(QRcodeMa.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(QRcodeMa.this);

        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);

        /// GET LOKASI
        FusedLocation fusedLocation = new FusedLocation(QRcodeMa.this, new FusedLocation.Callback() {
            @Override
            public void onLocationResult(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                jsonlokasi = new JSONObject();
                try {
                    jsonlokasi.put("latitude", location.getLatitude());
                    jsonlokasi.put("longitude", location.getLongitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        if (!fusedLocation.isGPSEnabled()) {
            fusedLocation.showSettingsAlert();
        } else {
            fusedLocation.getCurrentLocation(1);
        }

        //--END----///


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(nomorMA, BarcodeFormat.QR_CODE, 200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            qrcode.setImageBitmap(bitmap);

        }catch (WriterException e){
            e.printStackTrace();
        }


        lihatproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QRcodeMa.this, LihatProsesBongkar.class);
                intent.putExtra("nomorMA",nomorMA);
                startActivity(intent);
            }
        });

        stma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambildatastma();

//                Intent intent = new Intent(QRcodeMa.this, ScanBongkarMa.class);
//                intent.putExtra("nomorMA", "MA12345");
//                intent.putExtra("sessionId", "12323");
//                intent.putExtra("jenis", "bongkarmaSPV");
//                startActivity(intent);
//                finish();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder adb=new AlertDialog.Builder(QRcodeMa.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah Data yang di Proses Sudah Benar ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submitfinal();
                    }});
                adb.show();


            }
        });



    }


    private void ambildatastma(){

        Call<opv2GetTTKMA> result = mApiInterface.opv2GetTTKMA(
                "opv2GetTTKMA",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                nomorMA,
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

                            new DownloadTask(QRcodeMa.this, stma, fileurl, "bongkarma",filename);

                            int histPickup = db.checkListBONGKARMAHistory(session.getID(),nomorMA);
                            int jumlahscan = db.countBONGKARMAlistScan(session.getID(),nomorMA);

                            if (histPickup == 1) {
                                ListTtkBongkarMA ttk = db.getTTKhistoryMaxBONGKARMA(session.getID(),nomorMA);

                                android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(QRcodeMa.this);
                                adb.setTitle("Info");
                                adb.setMessage("Proses STMA terakhir belum selesai , Sudah di scan " + jumlahscan + " ttk dengan nomor ttk terakhir " + ttk.getTtk() + ", Apakah anda ingin melanjutkan?");
                                adb.setNegativeButton("Tidak", new android.support.v7.app.AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteListBONGKARMA(session.getID(),nomorMA);

                                        Intent intent = new Intent(QRcodeMa.this, ScanBongkarMa.class);
                                        intent.putExtra("nomorMA", nomorMA);
                                        intent.putExtra("sessionId", sessionId);
                                        intent.putExtra("jenis", "bongkarmaSPV");
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                                adb.setPositiveButton("Iya", new android.support.v7.app.AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //db.deleteListPickup();

                                        Intent intent = new Intent(QRcodeMa.this, ScanBongkarMa.class);
                                        intent.putExtra("nomorMA", nomorMA);
                                        intent.putExtra("sessionId", sessionId);
                                        intent.putExtra("jenis", "bongkarmaSPV");
                                        startActivity(intent);
                                        finish();

                                    }
                                });

                                adb.show();

                            }else{

                                Intent intent = new Intent(QRcodeMa.this, ScanBongkarMa.class);
                                intent.putExtra("nomorMA", nomorMA);
                                intent.putExtra("sessionId", sessionId);
                                intent.putExtra("jenis", "bongkarmaSPV");
                                startActivity(intent);
                                finish();

                            }






                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(QRcodeMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(QRcodeMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(QRcodeMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2GetTTKMA> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(QRcodeMa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });
    }


    private void submitfinal(){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        Call<opv2MkBongkarMAFinal> result = mApiInterface.opv2MkBongkarMAFinal(
                "opv2MkBongkarMAFinal",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                nomorMA,
                jsonlokasi,
                formattedDate,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2MkBongkarMAFinal>() {

            @Override
            public void onResponse(Call<opv2MkBongkarMAFinal> call, retrofit2.Response<opv2MkBongkarMAFinal> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if(code.equals("0")){

//                            String nomorSTMA = response.body().getData().getNomorSTMA().getStsm().getNo();
//                            String urlSTMA = response.body().getData().getUrlSTMA();

                            String nomorSTMA = response.body().getData().getNomorSTMA();
                            String urlSTMA = response.body().getData().getUrlSTMA();


                            Intent pindah = new Intent(QRcodeMa.this, HasilProses.class);
                            pindah.putExtra("proses", "bongkarmafinal");
                            pindah.putExtra("no", nomorSTMA);
                            pindah.putExtra("urlfile", urlSTMA);
                            startActivity(pindah);
                            finish();


                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(QRcodeMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(QRcodeMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(QRcodeMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2MkBongkarMAFinal> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(QRcodeMa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(QRcodeMa.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        adb.show();
    }




}
