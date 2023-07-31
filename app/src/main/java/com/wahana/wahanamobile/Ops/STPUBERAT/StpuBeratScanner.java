package com.wahana.wahanamobile.Ops.STPUBERAT;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.wahana.wahanamobile.Data.ListTtkSTPUBERAT;
import com.wahana.wahanamobile.ModelApiOPS.StpuBerat.opv2CekDimensiSTPU;
import com.wahana.wahanamobile.ModelApiOPS.StpuBerat.opv2STPUv2;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.Ops.stpu.ListTTKnotFound;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterListSTPUBERAT;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Call;
import retrofit2.Callback;

public class StpuBeratScanner extends Activity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    private Button flashButton, flashoff, addManual;
    private boolean mFlash;
    private static final String FLASH_STATE = "FLASH_STATE";
    ListView listTTK;
    DatabaseHandler db;
    List<ListTtkSTPUBERAT> ttkList;
    List<ListTtkSTPUBERAT> ttkListAll;
    AdapterListSTPUBERAT adapter;
    TextView jumlahTTK, judul;
    Button previewTTK, submit;
    String ttkManual, employeeCode, niksupirma, niksupir, kodeverifikasi, kodesortir;
    int urut;
    SessionManager session;
    ProgressDialog progressDialog;
    MediaPlayer mySong;
    int koliMatang, totalpickupsumber;
    RequestApiWahanaOps mApiInterface;
    JSONObject jsonparam;
    JSONObject jsonlokasi;
    EditText berat, panjang, lebar, tinggi;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stpu_berat_scanner);
        Intent intent = getIntent();
        niksupir = intent.getStringExtra("niksupir");
        kodesortir = intent.getStringExtra("kodesortir");

        session = new SessionManager(StpuBeratScanner.this);
        employeeCode = session.getID();
        jumlahTTK = (TextView) findViewById(R.id.jumlah_label);

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(StpuBeratScanner.this);
        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);

        progressDialog = new ProgressDialog(StpuBeratScanner.this, R.style.AppTheme_Dark_Dialog);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);

        /// GET LOKASI
        FusedLocation fusedLocation = new FusedLocation(StpuBeratScanner.this, new FusedLocation.Callback() {
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


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(StpuBeratScanner.this);
                alertDialog.setTitle("Input Manual TTK");
                alertDialog.setMessage("Masukkan no. TTK");

                final EditText input = new EditText(StpuBeratScanner.this);
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
                                ttkList = db.getAllDataSTPUBERAT(niksupir, kodesortir);
                                int ttk = db.checkListSTPUBERAT(ttkManual, niksupir, kodesortir);


                                int ttksumber = db.checkListSTPUBERATSumber(ttkManual);


                                if (ttksumber != 0) {

                                    if (ttk == 0) {


                                        inputberat(ttkManual);

//                                        try {
//
//
//                                            mySong = MediaPlayer.create(StpuBeratScanner.this, R.raw.success);
//                                            ListTtkSTPUBERAT sj = new ListTtkSTPUBERAT();
//                                            sj.setTtk(ttkManual);
//                                            sj.setNikpickup(niksupir);
//                                            sj.setKodesortir(kodesortir);
//                                            sj.setBerat("2");
//                                            sj.setPanjang("2");
//                                            sj.setLebar("2");
//                                            sj.setTinggi("2");
//                                            sj.setTgl(getDateTime());
//                                            db.addTTKSTPUBERAT(sj);
//                                            populate();
//
//                                            final AlertDialog.Builder adb = new AlertDialog.Builder(StpuBeratScanner.this);
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
                                        mySong = MediaPlayer.create(StpuBeratScanner.this, R.raw.error);
                                        Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                                        mySong.start();
                                    }

                                } else {
                                    mySong = MediaPlayer.create(StpuBeratScanner.this, R.raw.error);
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
                AlertDialog.Builder adb = new AlertDialog.Builder(StpuBeratScanner.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKListSTPUBERAT(nottk.getText().toString());
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


                AlertDialog.Builder adb = new AlertDialog.Builder(StpuBeratScanner.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin TTK yang sudah di scan sudah sesuai? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {



                        int jumlahnotfound = db.countListSTPUBERATnotFound(niksupir,kodesortir);

                        if (jumlahnotfound > 0) {
                            alertttknotfound();
                        } else {

                            submitform();
                        }

                    }
                });

                adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent pindah = new Intent(StpuBeratScanner.this, PreviewResultScanner.class);
                        pindah.putExtra("asal", "stpuberat");
                        pindah.putExtra("niksupir", niksupir);
                        pindah.putExtra("kodesortir", kodesortir);
                        startActivity(pindah);

                    }
                });


                adb.show();

            }


        });


    }


    private void alertttknotfound() {

        int jumlahnotfound1 = db.countListSTPUBERATnotFound(niksupir,kodesortir);


        AlertDialog.Builder adb = new AlertDialog.Builder(StpuBeratScanner.this);
        adb.setTitle("Info");
        adb.setMessage("Ada " + jumlahnotfound1 + " TTK yang belum di Scan ");
//        adb.setNegativeButton("Cancel", null);
        adb.setNegativeButton("Lihat", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent pindah = new Intent(StpuBeratScanner.this, ListTTKnotFound.class);
                pindah.putExtra("asal", "stpuberat");
                pindah.putExtra("niksupir", niksupir);
                pindah.putExtra("kodesortir", kodesortir);
                startActivity(pindah);
            }
        });

        adb.setPositiveButton("Lanjutkan", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                submitform();

            }
        });


        adb.show();
    }



    private void inputberat(final String ttk) {

        final AlertDialog.Builder alertDialogBerat = new AlertDialog.Builder(StpuBeratScanner.this, R.style.MyDialog);
        alertDialogBerat.setTitle("Input Dimensi TTK");
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.input_dimensi_berat, null);

        Button submit = (Button) row.findViewById(R.id.input_button);
        Button cancel = (Button) row.findViewById(R.id.input_buttoncancel);
        berat = (EditText) row.findViewById(R.id.berat);
        panjang = (EditText) row.findViewById(R.id.panjang);
        lebar = (EditText) row.findViewById(R.id.lebar);
        tinggi = (EditText) row.findViewById(R.id.tinggi);

        final AlertDialog optionDialog = alertDialogBerat.create();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateDimensi()) {
                    return;
                }


                Call<opv2CekDimensiSTPU> result = mApiInterface.opv2CekDimensiSTPU(
                        "opv2CekDimensiSTPU",
                        getString(R.string.partnerid),
                        session.getID(),
                        "0",
                        ttk,
                        berat.getText().toString(),
                        panjang.getText().toString(),
                        lebar.getText().toString(),
                        tinggi.getText().toString(),
                        session.getKeyTokenJWT()
                );

                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Processing...");
                progressDialog.show();

                result.enqueue(new Callback<opv2CekDimensiSTPU>() {

                    @Override
                    public void onResponse(Call<opv2CekDimensiSTPU> call, retrofit2.Response<opv2CekDimensiSTPU> response) {

                        progressDialog.dismiss();

                        if (response.isSuccessful()) {

                            try {

                                String code = response.body().getCode();

                                if (code.equals("0")) {


                                    try {
//
//
                                        mySong = MediaPlayer.create(StpuBeratScanner.this, R.raw.success);
                                        ListTtkSTPUBERAT sj = new ListTtkSTPUBERAT();
                                        sj.setTtk(ttk);
                                        sj.setNikpickup(niksupir);
                                        sj.setKodesortir(kodesortir);
                                        sj.setBerat(berat.getText().toString());
                                        sj.setPanjang(panjang.getText().toString());
                                        sj.setLebar(lebar.getText().toString());
                                        sj.setTinggi(tinggi.getText().toString());
                                        sj.setTgl(getDateTime());
                                        db.addTTKSTPUBERAT(sj);
                                        populate();

                                        final AlertDialog.Builder adb = new AlertDialog.Builder(StpuBeratScanner.this);
                                        adb.setTitle("Info");
                                        adb.setMessage(ttk);


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
                                        mySong.start();

                                    } catch (RuntimeException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    optionDialog.dismiss();

                                } else {

                                    String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();
                                    new SweetAlertDialog(StpuBeratScanner.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Error...")
                                            .setContentText(error)
                                            .show();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                new SweetAlertDialog(StpuBeratScanner.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error...")
                                        .setContentText(response.body().getText())
                                        .show();
                            }


                        } else {

                            Log.d("error1", "" + response.code());

                            new SweetAlertDialog(StpuBeratScanner.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText("Cek Koneksi Anda")
                                    .show();

                        }


                    }

                    @Override
                    public void onFailure(Call<opv2CekDimensiSTPU> call, Throwable t) {
                        t.printStackTrace();

                        progressDialog.dismiss();

                        new SweetAlertDialog(StpuBeratScanner.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText("Cek Koneksi Anda")
                                .show();

                    }
                });


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.dismiss();
            }
        });

        optionDialog.setView(row);


        optionDialog.show();
    }


    private void submitform() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());


        ArrayList<ListTtkSTPUBERAT> ttks = (ArrayList<ListTtkSTPUBERAT>) db.getAllDataSTPUBERATfixwithDimensi(niksupir,kodesortir);

        ArrayList<String> ttksnotfound = db.getAllTtkSTPUBERATnotFoundFix(niksupir,kodesortir);



        jsonArray = new JSONArray();

        for (int i = 0; i < ttks.size(); i++) {
            JSONObject jsonObjectRoot = new JSONObject();
            Log.d("fit1", "" + ttks.get(i).getTtk()+" "+ttks.get(i).getBerat()+" "+ttks.get(i).getPanjang()+" "+ttks.get(i).getLebar()+" "+ttks.get(i).getTinggi());
            try {

                jsonObjectRoot.put("ttk",ttks.get(i).getTtk());
                jsonObjectRoot.put("berat",ttks.get(i).getBerat());
                jsonObjectRoot.put("panjang",ttks.get(i).getPanjang());
                jsonObjectRoot.put("lebar",ttks.get(i).getLebar());
                jsonObjectRoot.put("tinggi",ttks.get(i).getTinggi());


                jsonArray.put(jsonObjectRoot);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        JSONObject okObj = new JSONObject();
        JSONArray jsArraynotfound = new JSONArray(ttksnotfound);
        try {
            okObj.put("ok", jsonArray);
            okObj.put("nok", jsArraynotfound);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("json",""+okObj);


        Call<opv2STPUv2> result = mApiInterface.opv2STPUv2(
                "opv2STPUv2",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                niksupir,
                kodesortir,
                okObj,
                jsonlokasi,
                formattedDate,
                session.getKeyTokenJWT()
        );


        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2STPUv2>() {

            @Override
            public void onResponse(Call<opv2STPUv2> call, retrofit2.Response<opv2STPUv2> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if(code.equals("0")){

                            String nomorstpu = response.body().getNomorSTPU();
                            String urlSTPU = response.body().getUrlSTPU();

//                            String downloadFileNamefull = urlSTPU.substring(urlSTPU.lastIndexOf('/'), urlSTPU.length());
//                            String downloadFileName = downloadFileNamefull.replaceAll("/", "");

                            db.deleteListSTPUBERAT(niksupir, kodesortir);

                            Intent pindah = new Intent(StpuBeratScanner.this, HasilProses.class);
                            pindah.putExtra("proses", "stpuberat");
                            pindah.putExtra("no", nomorstpu);
                            pindah.putExtra("urlfile", urlSTPU);
                            startActivity(pindah);
                            finish();

                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(StpuBeratScanner.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(StpuBeratScanner.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(StpuBeratScanner.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2STPUv2> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(StpuBeratScanner.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });







    }


    public boolean validateDimensi() {
        boolean valid = true;


        if (berat.getText().toString().isEmpty()) {
            berat.setError("Masukkan Berat");
            valid = false;
        } else {
            berat.setError(null);
        }

        if (panjang.getText().toString().isEmpty()) {
            panjang.setError("Masukkan Panjang");
            valid = false;
        } else {
            panjang.setError(null);
        }

        if (lebar.getText().toString().isEmpty()) {
            lebar.setError("Masukkan Lebar");
            valid = false;
        } else {
            lebar.setError(null);
        }

        if (tinggi.getText().toString().isEmpty()) {
            tinggi.setError("Masukkan TInggi");
            valid = false;
        } else {
            tinggi.setError(null);
        }


        return valid;
    }


    private void populate() {
        totalpickupsumber = db.countSTPUBERATSumber();
        Log.d("jumlahsumber", "" + totalpickupsumber);

        urut = urut + 1;
//        ttkList.clear();
        ttkList = db.getAllDataSTPUBERAT(niksupir, kodesortir);
        ttkListAll = db.getAllDataSTPUBERATPreview(niksupir, kodesortir);


        String textJumlah = Integer.toString(ttkListAll.size());
        jumlahTTK.setText(textJumlah + "/" + totalpickupsumber);

//        int sisasumber=totalpickupsumber-Integer.parseInt(textJumlah);
//
//        judul.setText("Scan TTK /"+sisasumber);

        Log.d("Hasil", "" + ttkList);
        adapter = new AdapterListSTPUBERAT(StpuBeratScanner.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "stpuberat");
        pindah.putExtra("niksupir", niksupir);
        pindah.putExtra("kodesortir", kodesortir);
        startActivity(pindah);
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
//        populate();

        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
//                progressDialog.cancel();
                populate();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 300);
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


    private boolean validateKode() {
        List<ListTtkSTPUBERAT> ttks = db.getAllDataSTPUBERATPreview(niksupir, kodesortir);
        if (ttks.size() < 1) {
            Toast.makeText(this, "Mohon Masukkan TTK nya", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }


    @Override
    public void handleResult(Result rawResult) {

        int ttk = db.checkListSTPUBERAT(rawResult.getContents(), niksupir, kodesortir);
        Log.d("Hasil", "" + ttk);
//        ttkList = db.getAllDataPickup();

        int ttksumber = db.checkListSTPUBERATSumber(rawResult.getContents());


        Log.d("ttksumber", "" + ttksumber + ":" + ttk);

        if (ttksumber != 0) {

            if (ttk == 0) {

                inputberat(rawResult.getContents());

//                try {
//
//
//                    mySong = MediaPlayer.create(StpuBeratScanner.this, R.raw.success);
//                    ListTtkSTPUBERAT sj = new ListTtkSTPUBERAT();
//                    sj.setTtk(rawResult.getContents().toUpperCase());
//                    sj.setNikpickup(niksupir);
//                    sj.setKodesortir(kodesortir);
//                    sj.setBerat("2");
//                    sj.setPanjang("2");
//                    sj.setLebar("2");
//                    sj.setTinggi("2");
//                    sj.setTgl(getDateTime());
//                    db.addTTKSTPUBERAT(sj);
//                    populate();
//
//
//                    final AlertDialog.Builder adb = new AlertDialog.Builder(StpuBeratScanner.this);
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
                mySong = MediaPlayer.create(StpuBeratScanner.this, R.raw.error);
                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                mySong.start();
            }

        } else {

            mySong = MediaPlayer.create(StpuBeratScanner.this, R.raw.error);
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
                mScannerView.resumeCameraPreview(StpuBeratScanner.this);
            }
        }, 2000);

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(StpuBeratScanner.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteListSTPUBERAT(niksupir,kodesortir);
                finish();
            }
        });
        adb.show();
    }

}
