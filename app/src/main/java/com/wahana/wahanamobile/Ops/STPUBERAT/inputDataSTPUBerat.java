package com.wahana.wahanamobile.Ops.STPUBERAT;

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

import com.wahana.wahanamobile.Data.ListTtkSTPUBERAT;
import com.wahana.wahanamobile.ModelApiOPS.StpuBerat.opv2GetTTKSTPUv2;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaNew;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class inputDataSTPUBerat extends AppCompatActivity {

    private static final String TAG = "InputNikSupir";

    ProgressDialog progressDialog;
    private TextView inputLayoutKode;
    public EditText niksupir, kodesortir, niksupirma;
    private Button btnInput;

    TextView pengisi, tgl, calendar;
    RelativeLayout infoLayout;

    SessionManager session;
    String username, user_id;
    TextView nama, jabatan;
    ImageView foto;
    DatabaseHandler db;
    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data_stpuberat);
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

        db = new DatabaseHandler(inputDataSTPUBerat.this);

        progressDialog = new ProgressDialog(inputDataSTPUBerat.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(inputDataSTPUBerat.this);
        mApiInterface = RestApiWahanaNew.getClient().create(RequestApiWahanaOps.class);
        username = session.getUsername();
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);
        niksupir = (EditText) findViewById(R.id.input_nik_supir);
        kodesortir = (EditText) findViewById(R.id.input_kode_sortir);
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

            }
        });


    }


    private void submitForm(){


        Call<opv2GetTTKSTPUv2> result = mApiInterface.opv2GetTTKSTPUv2(
                "opv2GetTTKSTPUv2",
                getString(R.string.partnerid),
                session.getID(),
                "0",
                niksupir.getText().toString(),
                kodesortir.getText().toString(),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        result.enqueue(new Callback<opv2GetTTKSTPUv2>() {

            @Override
            public void onResponse(Call<opv2GetTTKSTPUv2> call, retrofit2.Response<opv2GetTTKSTPUv2> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    try {

                        String code = response.body().getCode();

                        if (code.equals("0")) {

                            String filename = response.body().getData().getFilename();
                            String fileurl = response.body().getData().getFileurl();

                            db.deleteListSumberSTPUBerat();

                            new DownloadTask(inputDataSTPUBerat.this, btnInput, fileurl, "stpuberat", filename);

                            int histPickup = db.checkListSTPUBERATHistory(niksupir.getText().toString(), kodesortir.getText().toString().toUpperCase());
                            int jumlahscan = db.countSTPUBERATlistScan(niksupir.getText().toString(), kodesortir.getText().toString().toUpperCase());

                            if (histPickup == 1) {

                                ListTtkSTPUBERAT ttk = db.getTTKhistoryMaxSTPUBERAT(niksupir.getText().toString(), kodesortir.getText().toString().toUpperCase());

                                AlertDialog.Builder adb = new AlertDialog.Builder(inputDataSTPUBerat.this);
                                adb.setTitle("Info");
                                adb.setMessage("Proses STPU terakhir belum selesai , Sudah di scan " + jumlahscan + " ttk dengan nomor ttk terakhir " + ttk.getTtk() + ", Apakah anda ingin melanjutkan?");
                                adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteListSTPUBERAT(niksupir.getText().toString(), kodesortir.getText().toString().toUpperCase());

                                        Intent intent = new Intent(inputDataSTPUBerat.this, StpuBeratScanner.class);
                                        intent.putExtra("niksupir", niksupir.getText().toString());
                                        intent.putExtra("kodesortir", kodesortir.getText().toString().toUpperCase());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                                adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //db.deleteListPickup();

                                        Intent intent = new Intent(inputDataSTPUBerat.this, StpuBeratScanner.class);
                                        intent.putExtra("niksupir", niksupir.getText().toString());
                                        intent.putExtra("kodesortir", kodesortir.getText().toString().toUpperCase());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    }
                                });

                                adb.show();


                            } else {

                                Intent intent = new Intent(inputDataSTPUBerat.this, StpuBeratScanner.class);
                                intent.putExtra("niksupir", niksupir.getText().toString());
                                intent.putExtra("kodesortir", kodesortir.getText().toString().toUpperCase());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            }


                        } else {

                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(inputDataSTPUBerat.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        new SweetAlertDialog(inputDataSTPUBerat.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                } else {

                    Log.d("error1", "" + response.code());

                    new SweetAlertDialog(inputDataSTPUBerat.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();

                }


            }

            @Override
            public void onFailure(Call<opv2GetTTKSTPUv2> call, Throwable t) {
                t.printStackTrace();

                progressDialog.dismiss();

                new SweetAlertDialog(inputDataSTPUBerat.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

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



        return valid;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
