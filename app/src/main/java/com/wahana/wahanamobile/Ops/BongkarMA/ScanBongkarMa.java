package com.wahana.wahanamobile.Ops.BongkarMA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Handler;
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

import com.wahana.wahanamobile.Data.ListTtkBongkarMA;
import com.wahana.wahanamobile.Data.ListTtkSTPUMA;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.mkSTMASingle;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2GetTTKMA;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2MkBongkarMA;
import com.wahana.wahanamobile.ModelApiOPS.BongkarMA.opv2MkBongkarMAFinal;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.Ops.STPUdirectMA.StpuScannerdirectMa;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterListBongkarMA;
import com.wahana.wahanamobile.adapter.AdapterListSTPUMA;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Call;
import retrofit2.Callback;

public class ScanBongkarMa extends Activity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    private Button flashButton, flashoff, addManual;
    private boolean mFlash;
    private static final String FLASH_STATE = "FLASH_STATE";
    ListView listTTK;
    DatabaseHandler db;
    List<ListTtkBongkarMA> ttkList;
    List<ListTtkBongkarMA> ttkListAll;
    AdapterListBongkarMA adapter;
    TextView jumlahTTK, judul;
    Button previewTTK, submit;
    String ttkManual,employeeCode,niksupirma, niksupir, kodeverifikasi, kodesortir;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    int koliMatang, totalpickupsumber;
    RequestApiWahanaOps mApiInterface;
    JSONObject jsonparam;
    JSONObject jsonlokasi;
    String nomorMA,sessionId,jenis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bongkar_ma);

        Intent intent = getIntent();
        nomorMA=intent.getStringExtra("nomorMA");
        sessionId=intent.getStringExtra("sessionId");
        jenis=intent.getStringExtra("jenis");

        session = new SessionManager(ScanBongkarMa.this);
        employeeCode = session.getID();
        jumlahTTK = (TextView) findViewById(R.id.jumlah_label);

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(ScanBongkarMa.this);

//        db.deleteListBONGKARMA();

        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);

        progressDialog = new ProgressDialog(ScanBongkarMa.this, R.style.AppTheme_Dark_Dialog);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);

//        populate();

        /// GET LOKASI
        FusedLocation fusedLocation = new FusedLocation(ScanBongkarMa.this, new FusedLocation.Callback() {
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

//                getlokasi(latitude,longitude);


            }
        });

        if (!fusedLocation.isGPSEnabled()) {
            fusedLocation.showSettingsAlert();
        } else {
            fusedLocation.getCurrentLocation(1);
        }

        //--END----///


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


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScanBongkarMa.this);
                alertDialog.setTitle("Input Manual TTK");
                alertDialog.setMessage("Masukkan no. TTK");

                final EditText input = new EditText(ScanBongkarMa.this);
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
                                ttkList = db.getAllDataBONGKARMA(session.getID(),nomorMA);
                                int ttk = db.checkListBONGKARMA(ttkManual,session.getID(),nomorMA);


                                int ttksumber = db.checkListBONGKARMASumber(ttkManual);


                                if (ttksumber != 0) {

                                    if (ttk == 0) {


                                        submitTtk(ttkManual);

//                                        try {
//
//
//
//                                            mySong = MediaPlayer.create(ScanBongkarMa.this, R.raw.success);
//                                            ListTtkBongkarMA sj = new ListTtkBongkarMA();
//                                            sj.setTtk(ttkManual);
//                                            sj.setNikSTMA(session.getID());
//                                            sj.setNoMa(nomorMA);
//                                            sj.setTgl(getDateTime());
//                                            db.addTTKBONGKARMA(sj);
//                                            populate();
//
//                                            final AlertDialog.Builder adb = new AlertDialog.Builder(ScanBongkarMa.this);
//                                            adb.setTitle("Info");
//                                            adb.setMessage(ttkManual);
//
//
//                                            adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//
//                                                }
//                                            });
//
//                                            final AlertDialog alert = adb.create();
//                                            alert.getWindow().setBackgroundDrawableResource(R.color.primary);
//
//                                            alert.show();
//
//                                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//                                            nbutton.setTextColor(getResources().getColor(R.color.white));
//
//                                            timeAlert(alert);
//
//                                        } catch (RuntimeException e) {
//                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        } catch (Exception e) {
//                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }


                                    } else {
                                        mySong = MediaPlayer.create(ScanBongkarMa.this, R.raw.error);
                                        Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                                        mySong.start();
                                    }

                                } else {
                                    mySong = MediaPlayer.create(ScanBongkarMa.this, R.raw.error);
                                    Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber", Toast.LENGTH_SHORT).show();
                                    mySong.start();
                                }

//

//                                mySong.start();
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
                final TextView nottk = (TextView) v.findViewById(R.id.no_ttk_isi);
                AlertDialog.Builder adb = new AlertDialog.Builder(ScanBongkarMa.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKListBONGKARMA(nottk.getText().toString());
                        populate();
                    }
                });
                adb.show();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateKode()) {
                    return;
                }



                AlertDialog.Builder adb = new AlertDialog.Builder(ScanBongkarMa.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin TTK yang sudah di scan sudah sesuai? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        testjsonstpu();

//                        if(jenis != null){
//
//                            Intent pindah = new Intent(ScanBongkarMa.this, HasilProses.class);
//                            pindah.putExtra("proses", "bongkarma");
//                            pindah.putExtra("jenis", jenis);
//                            pindah.putExtra("nomorMA", nomorMA);
//                            startActivity(pindah);
//                            finish();
//
//                        }else{
//                            Intent pindah = new Intent(ScanBongkarMa.this, HasilProses.class);
//                            pindah.putExtra("proses", "bongkarma");
//                            startActivity(pindah);
//                            finish();
//
//                        }


                    }
                });

                adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent pindah = new Intent(ScanBongkarMa.this, PreviewResultScanner.class);
                        pindah.putExtra("asal", "bongkarma");
                        pindah.putExtra("nomorMA", nomorMA);
                        startActivity(pindah);

                    }
                });


                adb.show();

            }


        });




    }




    private void submitTtk(final String ttk){

        Call<mkSTMASingle> result = mApiInterface.mkSTMASingle(
                "mkSTMASingle",
                getString(R.string.partnerid),
                session.getID(),
                sessionId,
                ttk,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<mkSTMASingle>() {

            @Override
            public void onResponse(Call<mkSTMASingle> call, retrofit2.Response<mkSTMASingle> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();


                        if(code.equals("0")){

                            try {


                                mySong = MediaPlayer.create(ScanBongkarMa.this, R.raw.success);
                                ListTtkBongkarMA sj = new ListTtkBongkarMA();
                                sj.setTtk(ttk);
                                sj.setNikSTMA(session.getID());
                                sj.setNoMa(nomorMA);
                                sj.setTgl(getDateTime());
                                db.addTTKBONGKARMA(sj);
                                populate();

                                final AlertDialog.Builder adb = new AlertDialog.Builder(ScanBongkarMa.this);
                                adb.setTitle("Info");
                                adb.setMessage(ttkManual);


                                adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                });

                                final AlertDialog alert = adb.create();
                                alert.getWindow().setBackgroundDrawableResource(R.color.primary);

                                alert.show();

                                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                nbutton.setTextColor(getResources().getColor(R.color.white));

                                timeAlert(alert);

                            } catch (RuntimeException e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            mySong.start();


                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(ScanBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(ScanBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(ScanBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<mkSTMASingle> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(ScanBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }



    private void testjsonstpu(){
        ArrayList<String> ttks = db.getAllTTKBONGKARMAfix();

        JSONArray jsArray = new JSONArray(ttks);

        Log.d("json", "" + jsArray);


        Call<opv2MkBongkarMA> result = mApiInterface.opv2MkBongkarMA(
                "opv2MkBongkarMA",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                nomorMA,
                sessionId,
                jsArray,
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2MkBongkarMA>() {

            @Override
            public void onResponse(Call<opv2MkBongkarMA> call, retrofit2.Response<opv2MkBongkarMA> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        Log.d("hasil",""+code);

                        if(code.equals("0")){

//                            String nomorSTMA = response.body().getData().getNomorSTMA();
//                            String urlSTMA = response.body().getData().getUrlSTMA();

                            db.deleteListBONGKARMA(session.getID(),nomorMA);

                            if(jenis != null){

                                Intent pindah = new Intent(ScanBongkarMa.this, HasilProses.class);
                                pindah.putExtra("proses", "bongkarma");
                                pindah.putExtra("jenis", jenis);
                                pindah.putExtra("nomorMA", nomorMA);
                                startActivity(pindah);
                                finish();

                            }else{
                                Intent pindah = new Intent(ScanBongkarMa.this, HasilProses.class);
                                pindah.putExtra("proses", "bongkarma");
                                startActivity(pindah);
                                finish();

                            }




                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(ScanBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(ScanBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(ScanBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2MkBongkarMA> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(ScanBongkarMa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }



    private boolean validateKode() {
        List<ListTtkBongkarMA> ttks = db.getAllDataBONGKARMAPreview(session.getID(),nomorMA);
        if (ttks.size() < 1) {
            Toast.makeText(this, "Mohon Masukkan TTK nya", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }




    private void populate() {

        totalpickupsumber = db.countBONGKARMASumber();

        Log.d("jumlahsumber", "" + totalpickupsumber);

        urut = urut + 1;
//        ttkList.clear();
        ttkList = db.getAllDataBONGKARMA(session.getID(),nomorMA);
        ttkListAll = db.getAllDataBONGKARMAPreview(session.getID(),nomorMA);


        String textJumlah = Integer.toString(ttkListAll.size());
        jumlahTTK.setText(textJumlah + "/" + totalpickupsumber);

//        int sisasumber=totalpickupsumber-Integer.parseInt(textJumlah);
//
//        judul.setText("Scan TTK /"+sisasumber);

        Log.d("Hasil", "" + ttkList);
        adapter = new AdapterListBongkarMA(ScanBongkarMa.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "bongkarma");
        pindah.putExtra("nomorMA", nomorMA);
        startActivity(pindah);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
//                progressDialog.cancel();
                populate();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 300);
//        populate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void timeAlert(final AlertDialog alert) {
        // Hide after some seconds
        final Handler handler = new Handler();
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

        handler.postDelayed(runnable, 1000);
    }



    @Override
    public void handleResult(Result rawResult) {

        int ttk = db.checkListBONGKARMA(rawResult.getContents().toUpperCase(),session.getID(),nomorMA);
        Log.d("Hasil", "" + ttk);
//        ttkList = db.getAllDataPickup();

        int ttksumber = db.checkListBONGKARMASumber(rawResult.getContents().toUpperCase());


        Log.d("ttksumber", "" + ttksumber + ":" + ttk);

        if (ttksumber != 0) {

            if (ttk == 0) {

                submitTtk(rawResult.getContents().toUpperCase());

//                try {
//
//
//                    mySong = MediaPlayer.create(ScanBongkarMa.this, R.raw.success);
//                    ListTtkBongkarMA sj = new ListTtkBongkarMA();
//                    sj.setTtk(rawResult.getContents().toUpperCase());
//                    sj.setNikSTMA(session.getID());
//                    sj.setNoMa(nomorMA);
//                    sj.setTgl(getDateTime());
//                    db.addTTKBONGKARMA(sj);
//                    populate();
//
//
//                    final AlertDialog.Builder adb = new AlertDialog.Builder(ScanBongkarMa.this);
//                    adb.setTitle("Info");
//                    adb.setMessage(rawResult.getContents());
//
//
//                    adb.setNegativeButton("Close", new AlertDialog.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//                        }
//                    });
//
//                    final AlertDialog alert = adb.create();
//                    alert.getWindow().setBackgroundDrawableResource(R.color.primary);
//
//                    alert.show();
//
//                    Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//                    nbutton.setTextColor(getResources().getColor(R.color.white));
//
//                    timeAlert(alert);
//
//                } catch (RuntimeException e) {
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }


            } else {
                mySong = MediaPlayer.create(ScanBongkarMa.this, R.raw.error);
                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                mySong.start();
            }

        } else {

            mySong = MediaPlayer.create(ScanBongkarMa.this, R.raw.error);
            Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber", Toast.LENGTH_SHORT).show();
            mySong.start();
        }
//        mySong.start();
//        }
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScanBongkarMa.this);
            }
        }, 2000);

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(ScanBongkarMa.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteListBONGKARMA(session.getID(),nomorMA);

                if(jenis != null){
                    Intent pindah = new Intent(ScanBongkarMa.this, QRcodeMa.class);
                    pindah.putExtra("nomorMA", nomorMA);
                    startActivity(pindah);
                    finish();
                }else{
                    finish();
                }



            }
        });
        adb.show();
    }









}
