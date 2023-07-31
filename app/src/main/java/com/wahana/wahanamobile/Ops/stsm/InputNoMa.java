package com.wahana.wahanamobile.Ops.stsm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.wahana.wahanamobile.Data.ListMsSTSM;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.STSM.getMsFromMa;
import com.wahana.wahanamobile.ModelApiOPS.STSM.getMsFromMa_Result;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputNoMa extends AppCompatActivity {
    private static final String TAG = "InputNoLabelActivity";
    ProgressDialog progressDialog;

    String proses, no;
    TextView pengisi, tgl, calendar, manifest_label, manifest_isi, hasil_manifest;
    Button buttonOK;
    SessionManager session;
    DatabaseHandler db;
    String username, user_id;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    Button scan;
    EditText label;
    RequestApiWahanaOps mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_no_ma);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_extend_layout);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        mApiInterface = RestApiWahanaOps2019.getClient().create(RequestApiWahanaOps.class);
        db = new DatabaseHandler(InputNoMa.this);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        progressDialog = new ProgressDialog(InputNoMa.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputNoMa.this);
        username = session.getUsername();
        user_id = session.getID();
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        buttonOK = (Button) findViewById(R.id.input_button);
        scan = (Button) findViewById(R.id.scan_button);
        label = (EditText) findViewById(R.id.input_ma);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateKode()) {
                    return;
                }
                submitForm();
            }
        });
        pengisi.setText(user_id);
        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchActivity(BarcodeScannerActivity.class);
            }
        });




    }


    private void submitForm() {

        if (!validateKode()) {
            return;
        }


        Call<getMsFromMa> result = mApiInterface.getMsFromMa(
                "getMsFromMa",
                getString(R.string.partnerid),
                session.getID(),
                label.getText().toString().toUpperCase(),
                session.getKeyTokenJWT()
        );

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();


        result.enqueue(new Callback<getMsFromMa>() {

            @Override
            public void onResponse(Call<getMsFromMa> call, Response<getMsFromMa> response) {

                Log.d("error",""+response.body().getText());

                progressDialog.dismiss();

                if(response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();



                        if (vrcode.equals("0")) {
//
                            getMsFromMa data = response.body();
                            List<getMsFromMa_Result> dataList = new ArrayList<>();
                            dataList = data.getData();

                            String vurl = dataList.get(0).getUrlfile();
                            String vFile = dataList.get(0).getFilename();

                            db.deleteListSumberSTSM();

                            Log.d("STSM",""+vurl);

                            new DownloadTask(InputNoMa.this, buttonOK, vurl, "STSM",vFile);

                            int histSTSM = db.checkListSTSMHistory(label.getText().toString().toUpperCase());

                            if (histSTSM == 1) {

                                ListMsSTSM ttk = db.getMshistoryMax(label.getText().toString().toUpperCase());

                                int jumlahscan = db.countSTSMlistScan(label.getText().toString().toUpperCase());

                                final AlertDialog.Builder adb = new AlertDialog.Builder(InputNoMa.this);
                                adb.setTitle("Info");
                                adb.setMessage("Proses STSM terakhir belum selesai , Sudah di scan " + jumlahscan + " MS dengan nomor MS terakhir " + ttk.getNoMs() + ", Apakah anda ingin melanjutkan?");
                                adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteListSTSM(label.getText().toString().toUpperCase());

                                        Intent pindah = new Intent(InputNoMa.this, SerahTerimaSMv2Activity.class);
                                        pindah.putExtra("ma",label.getText().toString().toUpperCase());
                                        startActivity(pindah);
                                        finish();

                                    }
                                });
                                adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent pindah = new Intent(InputNoMa.this, SerahTerimaSMv2Activity.class);
                                        pindah.putExtra("ma",label.getText().toString().toUpperCase());
                                        startActivity(pindah);
                                        finish();

                                    }
                                });


                                final AlertDialog alert = adb.create();
                                alert.show();


                            } else {

                                Intent pindah = new Intent(InputNoMa.this, SerahTerimaSMv2Activity.class);
                                pindah.putExtra("ma",label.getText().toString().toUpperCase());
                                startActivity(pindah);
                                finish();

                            }







                        } else {


                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(InputNoMa.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(InputNoMa.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(InputNoMa.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<getMsFromMa> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(InputNoMa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });





//        Intent pindah = new Intent(inputMSActivity.this, SerahTerimaMSActivity.class);
//        pindah.putExtra("ms",inputVerifikasi.getText().toString().toUpperCase());
//        startActivity(pindah);
//        finish();
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
            label.setText(nilai);
        }
    }

    private boolean validateKode() {
        if (label.getText().toString().trim().isEmpty()) {
            label.setError("Masukkan Nomor MA");

            return false;
        } else {
            label.setError(null);
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
