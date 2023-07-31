package com.wahana.wahanamobile.Ops.STPUdirectMA;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.Data.ListTtkSTPUMA;
import com.wahana.wahanamobile.ModelApiOPS.StpuMa.opv2CreateSTPU2MA;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.PreviewResultScanner;
import com.wahana.wahanamobile.Ops.stpu.ListTTKnotFound;
import com.wahana.wahanamobile.Ops.stpu.StpuScanner;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.AdapterListSTPU;
import com.wahana.wahanamobile.adapter.AdapterListSTPUMA;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.FusedLocation;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.Utils;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Call;
import retrofit2.Callback;

public class StpuScannerdirectMa extends Activity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    private Button flashButton, flashoff, addManual;
    private boolean mFlash;
    private static final String FLASH_STATE = "FLASH_STATE";
    ListView listTTK;
    DatabaseHandler db;
    List<ListTtkSTPUMA> ttkList;
    List<ListTtkSTPUMA> ttkListAll;
    AdapterListSTPUMA adapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stpu_scannerdirect_ma);
        Intent intent = getIntent();

        niksupir = intent.getStringExtra("niksupir");
        niksupirma = intent.getStringExtra("niksupirma");
        kodesortir = intent.getStringExtra("kodesortir");

        session = new SessionManager(StpuScannerdirectMa.this);
        employeeCode = session.getID();
        jumlahTTK = (TextView) findViewById(R.id.jumlah_label);

        listTTK = (ListView) findViewById(R.id.list_ttk_daftar);
        db = new DatabaseHandler(StpuScannerdirectMa.this);

//        db.deleteListSTPUMA();

        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);

        progressDialog = new ProgressDialog(StpuScannerdirectMa.this, R.style.AppTheme_Dark_Dialog);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);

//        populate();


        /// GET LOKASI
        FusedLocation fusedLocation = new FusedLocation(StpuScannerdirectMa.this, new FusedLocation.Callback() {
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


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(StpuScannerdirectMa.this);
                alertDialog.setTitle("Input Manual TTK");
                alertDialog.setMessage("Masukkan no. TTK");

                final EditText input = new EditText(StpuScannerdirectMa.this);
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
                                ttkList = db.getAllDataSTPUMA(niksupir, kodesortir);
                                int ttk = db.checkListSTPUMA(ttkManual, niksupir, kodesortir);


                                int ttksumber = db.checkListSTPUMASumber(ttkManual);


                                if (ttksumber != 0) {

                                    if (ttk == 0) {

                                        try {

                                            ListTtkSTPUMA ttksumber1 = db.getDataSumberSTPUMA(ttkManual);


                                            mySong = MediaPlayer.create(StpuScannerdirectMa.this, R.raw.success);
                                            ListTtkSTPUMA sj = new ListTtkSTPUMA();
                                            sj.setTtk(ttkManual);
                                            sj.setNiksupir(niksupir);
                                            sj.setKodesortir(kodesortir);
                                            sj.setTgl(getDateTime());
                                            db.addTTKSTPUMA(sj);
                                            populate();

                                            final AlertDialog.Builder adb = new AlertDialog.Builder(StpuScannerdirectMa.this);
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


                                    } else {
                                        mySong = MediaPlayer.create(StpuScannerdirectMa.this, R.raw.error);
                                        Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    mySong = MediaPlayer.create(StpuScannerdirectMa.this, R.raw.error);
                                    Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber", Toast.LENGTH_SHORT).show();
                                }

//

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
                final TextView nottk = (TextView) v.findViewById(R.id.no_ttk_isi);
                AlertDialog.Builder adb = new AlertDialog.Builder(StpuScannerdirectMa.this);
                adb.setTitle("Hapus TTK ?");
                adb.setMessage("Apakah anda yakin ingin mendelete ttk " + nottk.getText().toString());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ttkList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        db.deleteTTKListSTPUMA(nottk.getText().toString());
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



                AlertDialog.Builder adb = new AlertDialog.Builder(StpuScannerdirectMa.this);
                adb.setTitle("Confirmation ?");
                adb.setMessage("Apakah anda yakin TTK yang sudah di scan sudah sesuai? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        int jumlahnotfound = db.countListSTPUMAnotFound(niksupir,kodesortir);

                        if (jumlahnotfound > 0) {
                            alertttknotfound();
                        } else {

                            testjsonstpu();
                        }

//                        Intent pindah = new Intent(StpuScannerdirectMa.this, HasilProses.class);
//                        pindah.putExtra("proses", "stpuma");
//                        pindah.putExtra("no", "213213");
//                        startActivity(pindah);
//                        finish();


                    }
                });

                adb.setNeutralButton("Lihat", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent pindah = new Intent(StpuScannerdirectMa.this, PreviewResultScanner.class);
                        pindah.putExtra("asal", "stpuma");
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

        int jumlahnotfound1 = db.countListSTPUMAnotFound(niksupir,kodesortir);


        AlertDialog.Builder adb = new AlertDialog.Builder(StpuScannerdirectMa.this);
        adb.setTitle("Info");
        adb.setMessage("Ada " + jumlahnotfound1 + " TTK yang belum di Scan ");
//        adb.setNegativeButton("Cancel", null);
        adb.setNegativeButton("Lihat", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent pindah = new Intent(StpuScannerdirectMa.this, ListTTKnotFound.class);
                pindah.putExtra("asal", "stpuma");
                pindah.putExtra("niksupir", niksupir);
                pindah.putExtra("kodesortir", kodesortir);
                startActivity(pindah);
            }
        });

        adb.setPositiveButton("Lanjutkan", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //db.deleteListPickup();
//                                        Toast.makeText(getApplicationContext(), "neutralize", Toast.LENGTH_SHORT).show();
                testjsonstpu();

//                ArrayList<String> ttksnotfound = db.getAllTtkSTPUnotFoundFix();
//
//                JSONObject jsonttknotfound = new JSONObject();
//
//                try{
//                    JSONArray jsArray = new JSONArray(ttksnotfound);
//                    jsonttknotfound.put("ttk", jsArray);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                Log.d("not found",""+jsonttknotfound);

            }
        });


        adb.show();
    }


    private void testjsonstpu(){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        ArrayList<String> ttks = db.getAllTTKSTPUMAfix(niksupir,kodesortir);

        ArrayList<String> ttksnotfound = db.getAllTtkSTPUMAnotFoundFix(niksupir,kodesortir);
        jsonparam = new JSONObject();

        try {
            JSONArray jsArray = new JSONArray(ttks);
            JSONArray jsArray1 = new JSONArray(ttksnotfound);

            jsonparam.put("ok", jsArray);
            jsonparam.put("nok", jsArray1);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Call<opv2CreateSTPU2MA> result = mApiInterface.opv2CreateSTPU2MA(
                "opv2CreateSTPU2MA",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                niksupir,
                niksupirma,
                kodesortir,
                jsonparam,
                jsonlokasi,
                formattedDate,
                session.getKeyTokenJWT()
        );


        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2CreateSTPU2MA>() {

            @Override
            public void onResponse(Call<opv2CreateSTPU2MA> call, retrofit2.Response<opv2CreateSTPU2MA> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if(code.equals("0")){

                            String nomorstpu = response.body().getData().getNomorSTPU();
                            String urlSTPU = response.body().getData().getUrlSTPU();

//                            String downloadFileNamefull = urlSTPU.substring(urlSTPU.lastIndexOf('/'), urlSTPU.length());
//                            String downloadFileName = downloadFileNamefull.replaceAll("/", "");

                            db.deleteListSTPUMA(niksupir, kodesortir);

                            Intent pindah = new Intent(StpuScannerdirectMa.this, HasilProses.class);
                            pindah.putExtra("proses", "stpuma");
                            pindah.putExtra("no", nomorstpu);
                            pindah.putExtra("urlfile", urlSTPU);
                            startActivity(pindah);
                            finish();

                        }else{

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(StpuScannerdirectMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(StpuScannerdirectMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    Log.d("error1",""+response.code());

                    new SweetAlertDialog(StpuScannerdirectMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }



            }

            @Override
            public void onFailure(Call<opv2CreateSTPU2MA> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(StpuScannerdirectMa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });





    }



    private void testjsonstpuv2(){
        ArrayList<String> ttks = db.getAllTTKSTPUMAfix(niksupir,kodesortir);

        ArrayList<String> ttksnotfound = db.getAllTtkSTPUMAnotFoundFix(niksupir,kodesortir);
        jsonparam = new JSONObject();

        try {
            JSONArray jsArray = new JSONArray(ttks);
            JSONArray jsArray1 = new JSONArray(ttksnotfound);

            jsonparam.put("ok", jsArray);
            jsonparam.put("nok", jsArray1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("json", "" + jsonparam);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Utils.urlRest;

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.d("Response", response);

                        progressDialog.dismiss();
                        try {
                            JSONObject json = new JSONObject(response);

                            String code = json.getString("code");
                            String text = json.getString("text");

                            if(code.equals("0")){

                                JSONObject datattk = json.getJSONObject("data");

                                String nomorstpu = datattk.getString("nomorSTPU");
                                String urlSTPU = datattk.getString("urlSTPU");

                                String downloadFileNamefull = urlSTPU.substring(urlSTPU.lastIndexOf('/'), urlSTPU.length());
                                String downloadFileName = downloadFileNamefull.replaceAll("/", "");

                                Intent pindah = new Intent(StpuScannerdirectMa.this, HasilProses.class);
                                pindah.putExtra("proses", "stpuma");
                                pindah.putExtra("no", nomorstpu);
                                pindah.putExtra("urlfile", urlSTPU);
                                startActivity(pindah);
                                finish();


                                Log.d("Response", downloadFileName);




                            }else{
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("_r", "opv2CreateSTPU2MA");
                params.put("partnerId", getString(R.string.partnerid));
                params.put("authCode", session.getKeyTokenJWT());
                params.put("specification", "0");
                params.put("nikPU", niksupir);
                params.put("nikMA", niksupirma);
                params.put("sortingCode", kodesortir);
                params.put("employeeCode", session.getID());
                params.put("ttk", String.valueOf(jsonparam));
                return params;
            }
        };
        queue.add(postRequest);


    }




    private void populate() {

        totalpickupsumber = db.countSTPUMASumber();

        Log.d("jumlahsumber", "" + totalpickupsumber);

        urut = urut + 1;
//        ttkList.clear();
        ttkList = db.getAllDataSTPUMA(niksupir, kodesortir);
        ttkListAll = db.getAllDataSTPUMAPreview(niksupir, kodesortir);


        String textJumlah = Integer.toString(ttkListAll.size());
        jumlahTTK.setText(textJumlah + "/" + totalpickupsumber);

//        int sisasumber=totalpickupsumber-Integer.parseInt(textJumlah);
//
//        judul.setText("Scan TTK /"+sisasumber);

        Log.d("Hasil", "" + ttkList);
        adapter = new AdapterListSTPUMA(StpuScannerdirectMa.this, ttkList, urut);
        listTTK.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void preview() {
        Intent pindah = new Intent(this, PreviewResultScanner.class);
        pindah.putExtra("asal", "stpuma");
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

    @Override
    public void handleResult(Result rawResult) {

        int ttk = db.checkListSTPUMA(rawResult.getContents(), niksupir, kodesortir);
        Log.d("Hasil", "" + ttk);
//        ttkList = db.getAllDataPickup();

        int ttksumber = db.checkListSTPUMASumber(rawResult.getContents());


        Log.d("ttksumber", "" + ttksumber + ":" + ttk);

        if (ttksumber != 0) {

            if (ttk == 0) {

                try {

                    ListTtkSTPUMA ttksumber1 = db.getDataSumberSTPUMA(rawResult.getContents());


                    mySong = MediaPlayer.create(StpuScannerdirectMa.this, R.raw.success);
                    ListTtkSTPUMA sj = new ListTtkSTPUMA();
                    sj.setTtk(rawResult.getContents().toUpperCase());
                    sj.setNiksupir(niksupir);
                    sj.setKodesortir(kodesortir);
                    sj.setTgl(getDateTime());
                    db.addTTKSTPUMA(sj);
                    populate();


                    final AlertDialog.Builder adb = new AlertDialog.Builder(StpuScannerdirectMa.this);
                    adb.setTitle("Info");
                    adb.setMessage(rawResult.getContents());


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


            } else {
                mySong = MediaPlayer.create(StpuScannerdirectMa.this, R.raw.error);
                Toast.makeText(getApplicationContext(), "TTK Sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
            }

        } else {

            mySong = MediaPlayer.create(StpuScannerdirectMa.this, R.raw.error);
            Toast.makeText(getApplicationContext(), "TTK Tidak terdaftar di sumber", Toast.LENGTH_SHORT).show();
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
                mScannerView.resumeCameraPreview(StpuScannerdirectMa.this);
            }
        }, 2000);

    }

    private boolean validateKode() {
        List<ListTtkSTPUMA> ttks = db.getAllDataSTPUMAPreview(niksupir, kodesortir);
        if (ttks.size() < 1) {
            Toast.makeText(this, "Mohon Masukkan TTK nya", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(StpuScannerdirectMa.this);
        adb.setTitle("Keluar Halaman");
        adb.setMessage("Apakah anda yakin ? Jika keluar dari halaman ini data anda akan kembali ke awal");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteListSTPUMA(niksupir,kodesortir);
                finish();
            }
        });
        adb.show();
    }


}
