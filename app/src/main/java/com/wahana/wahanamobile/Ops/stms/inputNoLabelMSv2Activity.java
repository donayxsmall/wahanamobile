package com.wahana.wahanamobile.Ops.stms;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.Data.ListTtkSTMS;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.ModelApiOPS.STMS.getTtkFromMs;
import com.wahana.wahanamobile.ModelApiOPS.STMS.getTtkFromMs_Result;
import com.wahana.wahanamobile.Ops.ListTTKbyMS;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.Ops.stms.inputNoLabelMSActivity;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.webserviceClient.RequestApiWahanaOps;
import com.wahana.wahanamobile.webserviceClient.RestApiWahanaOps2019;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sadewa on 09/06/17.
 */

public class inputNoLabelMSv2Activity extends DrawerHelper {
    private static final String TAG = "InputNoLabelActivity";
    ProgressDialog progressDialog;

    String proses, no;
    TextView pengisi, tgl, calendar, manifest_label, manifest_isi, hasil_manifest;
    Button buttonOK;
    SessionManager session;
    String username, user_id;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    Button scan;
    EditText label;
    RequestApiWahanaOps mApiInterface;
    DatabaseHandler db;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("create","manifest");
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        no = myIntent.getStringExtra("no");
        setContentView(R.layout.activity_scan_label_stms);
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
        db = new DatabaseHandler(inputNoLabelMSv2Activity.this);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        progressDialog = new ProgressDialog(inputNoLabelMSv2Activity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(inputNoLabelMSv2Activity.this);
        username = session.getUsername();
        user_id = session.getID();
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        buttonOK = (Button) findViewById(R.id.input_button);
        scan = (Button) findViewById(R.id.scan_button);
        label = (EditText) findViewById(R.id.input_ttk);
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
//                submitForm();

                submitFormV2();
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

    private void submitFormV2(){

        Call<getTtkFromMs> result = mApiInterface.getTtkFromMs(
                "getTtkFromMs",
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

        result.enqueue(new Callback<getTtkFromMs>() {

            @Override
            public void onResponse(Call<getTtkFromMs> call, Response<getTtkFromMs> response) {

                Log.d("error",""+response.body().getText());

                progressDialog.dismiss();

                if(response.isSuccessful()) {


                    try {

                        String vrcode = response.body().getCode();



                        if (vrcode.equals("0")) {
//
                            getTtkFromMs data = response.body();
                            List<getTtkFromMs_Result> dataList = new ArrayList<>();
                            dataList = data.getData();

                            String vurl = dataList.get(0).getUrlfile();
                            String vFile = dataList.get(0).getFilename();

                            db.deleteListSumberSTMS();

                            Log.d("STMS",""+vurl);

                            new DownloadTask(inputNoLabelMSv2Activity.this, buttonOK, vurl, "STMS",vFile);

                            int histSTMS = db.checkListSTMSHistory(label.getText().toString().toUpperCase());

                            if (histSTMS == 1) {

                                ListTtkSTMS ttk = db.getTtkSTMShistoryMax(label.getText().toString().toUpperCase());

                                int jumlahscan = db.countSTMSlistScan(label.getText().toString().toUpperCase());

                                final AlertDialog.Builder adb = new AlertDialog.Builder(inputNoLabelMSv2Activity.this);
                                adb.setTitle("Info");
                                adb.setMessage("Proses STMS terakhir belum selesai , Sudah di scan " + jumlahscan + " TTK dengan nomor TTK terakhir " + ttk.getNoTtk() + ", Apakah anda ingin melanjutkan?");
                                adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteListSTMS(label.getText().toString().toUpperCase());

                                        Intent pindah = new Intent(inputNoLabelMSv2Activity.this, SerahTerimaMSv2Activity.class);
                                        pindah.putExtra("ms",label.getText().toString().toUpperCase());
                                        startActivity(pindah);
                                        finish();

                                    }
                                });
                                adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent pindah = new Intent(inputNoLabelMSv2Activity.this, SerahTerimaMSv2Activity.class);
                                        pindah.putExtra("ms",label.getText().toString().toUpperCase());
                                        startActivity(pindah);
                                        finish();

                                    }
                                });


                                final AlertDialog alert = adb.create();
                                alert.show();


                            } else {

                                Intent pindah = new Intent(inputNoLabelMSv2Activity.this, SerahTerimaMSv2Activity.class);
                                pindah.putExtra("ms",label.getText().toString().toUpperCase());
                                startActivity(pindah);
                                finish();

                            }







                        } else {


                            String error = response.body().getAlert().equals("") ? response.body().getText() : response.body().getAlert();

                            new SweetAlertDialog(inputNoLabelMSv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error...")
                                    .setContentText(error)
                                    .show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                        new SweetAlertDialog(inputNoLabelMSv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error...")
                                .setContentText(response.body().getText())
                                .show();
                    }


                }else{

                    new SweetAlertDialog(inputNoLabelMSv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error...")
                            .setContentText("Cek Koneksi Anda")
                            .show();


                }



            }

            @Override
            public void onFailure(Call<getTtkFromMs> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();

                new SweetAlertDialog(inputNoLabelMSv2Activity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText("Cek Koneksi Anda")
                        .show();

            }
        });

    }


    private void submitForm() {
//        if (!validateKode()) {
//            return;
//        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("checkMsNik");
        parameter.add("50");
        parameter.add("0");
        parameter.add("nik");
        parameter.add(username);
        parameter.add("ms");
        parameter.add(label.getText().toString().toUpperCase());
        progressDialog.setCancelable(false);
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(inputNoLabelMSv2Activity.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                Intent pindah = new Intent(inputNoLabelMSv2Activity.this, SerahTerimaMSActivity.class);
                                pindah.putExtra("ms",label.getText().toString().toUpperCase());
                                startActivity(pindah);
                                finish();
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(inputNoLabelMSv2Activity.this, "Manifest kota tujuan tidak sesuai",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(inputNoLabelMSv2Activity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(inputNoLabelMSv2Activity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
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

//    @Override
//    public void onBackPressed() {
//        // do nothing.
//    }


    private boolean validateKode() {
        if (label.getText().toString().trim().isEmpty()) {
            label.setError("Masukkan Label");

            return false;
        } else {
            label.setError(null);
        }

        return true;
    }

}
