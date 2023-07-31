package com.wahana.wahanamobile.Ops.STPUdirectMA;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
//import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.Data.ListTtkSTPUMA;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.StpuMa.opv2GetTTKSTPU;
import com.wahana.wahanamobile.Ops.PickupRetail.PickupAgenRetail;
import com.wahana.wahanamobile.Ops.stpu.InputNikSupir;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.utils.Utils;
import com.wahana.wahanamobile.webserviceClient.ApiClient;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;
import com.wahana.wahanamobile.webserviceClient.UserAPIService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class inputDataSTPUdirectMa extends AppCompatActivity {

    private static final String TAG = "InputNikSupir";

    ProgressDialog progressDialog;
    private TextView inputLayoutKode;
    public EditText niksupir,kodesortir,niksupirma;
    private Button btnInput;

    TextView pengisi, tgl, calendar;
    RelativeLayout infoLayout;

    SessionManager session;
    String username, user_id;
    TextView nama,jabatan;
    ImageView foto;
    DatabaseHandler db;
    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data_stpudirect_ma);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setIcon(R.drawable.wahana_logo);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_extend_layout);
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

        db = new DatabaseHandler(inputDataSTPUdirectMa.this);

        progressDialog = new ProgressDialog(inputDataSTPUdirectMa.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(inputDataSTPUdirectMa.this);
        username = session.getUsername();
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);
        niksupir = (EditText) findViewById(R.id.input_nik_supir);
        kodesortir = (EditText) findViewById(R.id.input_kode_sortir);
        niksupirma = (EditText) findViewById(R.id.input_nik_supirma);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);

        pengisi.setText(username);

        Typeface type = Typeface.createFromAsset(getAssets(), "font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                }
                submitForm();
//                submitRetro();

            }
        });

        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);


    }


    private void submitForm() {

        Call<opv2GetTTKSTPU> result = mApiInterface.opv2GetTTKSTPU(
                "opv2GetTTKSTPU",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                niksupir.getText().toString(),
                niksupirma.getText().toString(),
                kodesortir.getText().toString(),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2GetTTKSTPU>() {

            @Override
            public void onResponse(Call<opv2GetTTKSTPU> call, retrofit2.Response<opv2GetTTKSTPU> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if (code.equals("0")) {

                            String filename = response.body().getData().getFilename();
                            String fileurl = response.body().getData().getFileurl();

                            db.deleteListSumberSTPUMA();

                            new DownloadTask(inputDataSTPUdirectMa.this, btnInput, fileurl, "stpuma", filename);

                            int histPickup = db.checkListSTPUMAHistory(niksupir.getText().toString(), kodesortir.getText().toString().toUpperCase());
                            int jumlahscan = db.countSTPUMAlistScan(niksupir.getText().toString(), kodesortir.getText().toString().toUpperCase());

                            if (histPickup == 1) {

                                ListTtkPickup ttk = db.getTTKhistoryMaxSTPUMA(niksupir.getText().toString(), kodesortir.getText().toString().toUpperCase());

                                AlertDialog.Builder adb = new AlertDialog.Builder(inputDataSTPUdirectMa.this);
                                adb.setTitle("Info");
                                adb.setMessage("Proses STPU terakhir belum selesai , Sudah di scan " + jumlahscan + " ttk dengan nomor ttk terakhir " + ttk.getTtk() + ", Apakah anda ingin melanjutkan?");
                                adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteListSTPUMA(niksupir.getText().toString(), kodesortir.getText().toString().toUpperCase());

                                        Intent intent = new Intent(inputDataSTPUdirectMa.this, StpuScannerdirectMa.class);
                                        intent.putExtra("niksupir", niksupir.getText().toString());
                                        intent.putExtra("kodesortir", kodesortir.getText().toString().toUpperCase());
                                        intent.putExtra("niksupirma", niksupirma.getText().toString());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                                adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //db.deleteListPickup();

                                        Intent intent = new Intent(inputDataSTPUdirectMa.this, StpuScannerdirectMa.class);
                                        intent.putExtra("niksupir", niksupir.getText().toString());
                                        intent.putExtra("kodesortir", kodesortir.getText().toString().toUpperCase());
                                        intent.putExtra("niksupirma", niksupirma.getText().toString());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    }
                                });

                                adb.show();


                            } else {

                                Intent intent = new Intent(inputDataSTPUdirectMa.this, StpuScannerdirectMa.class);
                                intent.putExtra("niksupir", niksupir.getText().toString());
                                intent.putExtra("kodesortir", kodesortir.getText().toString().toUpperCase());
                                intent.putExtra("niksupirma", niksupirma.getText().toString());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            }


                        } else {

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(inputDataSTPUdirectMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(inputDataSTPUdirectMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                } else {

                    Log.d("error1", "" + response.code());

                    new SweetAlertDialog(inputDataSTPUdirectMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }


            }

            @Override
            public void onFailure(Call<opv2GetTTKSTPU> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(inputDataSTPUdirectMa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });


    }


//    private void submitForm() {
//
////        Intent intent = new Intent(inputDataSTPUdirectMa.this, StpuScannerdirectMa.class);
////        intent.putExtra("niksupir",niksupir.getText().toString());
////        intent.putExtra("kodesortir",kodesortir.getText().toString());
////        intent.putExtra("niksupirma",niksupirma.getText().toString());
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        startActivity(intent);
////        finish();
//
//        getTTKStpu();
//
//    }


    private void getTTKStpu() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Utils.urlRest;

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.d("Response", response);

                        progressDialog.dismiss();
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject datattk = json.getJSONObject("data");

                            String code = json.getString("code");
                            String text = json.getString("text");
                            String filename = datattk.getString("filename");
                            String fileurl = datattk.getString("fileurl");

                            if (code.equals("0")) {

                                db.deleteListSumberSTPUMA();

                                new DownloadTask(inputDataSTPUdirectMa.this, btnInput, fileurl, "stpuma", filename);

//                                JSONObject datattk = json.getJSONObject("data");
//                                JSONArray itemArrayTtk=datattk.getJSONArray("ttk");
//                                for (int i = 0; i < itemArrayTtk.length(); i++) {
//                                    String value=itemArrayTtk.getString(i);
//                                    Log.d("Response", value);
//                                    ListTtkSTPUMA ttk = new ListTtkSTPUMA();
//                                    ttk.setTtk(value.toUpperCase());
//                                    ttk.setKodesortir(kodesortir.getText().toString().toUpperCase());
//                                    db.addTTKSTPUMAsumber(ttk);
//                                }

                                Intent intent = new Intent(inputDataSTPUdirectMa.this, StpuScannerdirectMa.class);
                                intent.putExtra("niksupir", niksupir.getText().toString());
                                intent.putExtra("kodesortir", kodesortir.getText().toString().toUpperCase());
                                intent.putExtra("niksupirma", niksupirma.getText().toString());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();


                            } else {
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();
                params.put("_r", "opv2GetTTKSTPU");
                params.put("partnerId", getString(R.string.partnerid));
                params.put("authCode", session.getKeyTokenJWT());
                params.put("specification", "0");
                params.put("nikPU", niksupir.getText().toString());
                params.put("nikMA", niksupirma.getText().toString());
                params.put("sortingCode", kodesortir.getText().toString());
                params.put("employeeCode", session.getID());
                return params;
            }
        };
        queue.add(postRequest);

    }

    public boolean validate() {
        boolean valid = true;


        if (niksupir.getText().toString().isEmpty()) {
            niksupir.setError("Masukkan Nik Pickup");
            valid = false;
        } else {
            niksupir.setError(null);
        }

        if (kodesortir.getText().toString().isEmpty()) {
            kodesortir.setError("Masukkan Kode Sortir");
            valid = false;
        } else {
            kodesortir.setError(null);
        }

        if (niksupirma.getText().toString().isEmpty()) {
            niksupirma.setError("Masukkan Nik Supir");
            valid = false;
        } else {
            niksupirma.setError(null);
        }


        return valid;
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
