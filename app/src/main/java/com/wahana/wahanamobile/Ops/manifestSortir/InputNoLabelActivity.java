package com.wahana.wahanamobile.Ops.manifestSortir;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by sadewa on 09/06/17.
 */

public class InputNoLabelActivity extends DrawerHelper {
    private static final String TAG = "InputNoLabelActivity";
    ProgressDialog progressDialog;

    String proses, no;
    TextView pengisi, tgl, calendar, manifest_label, manifest_isi, hasil_manifest;
    Button buttonOK;
    SessionManager session;
    String username, employeeCode;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    Button scan;
    EditText label;
    DatabaseHandler db;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("create","manifest");
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        no = myIntent.getStringExtra("no");
        setContentView(R.layout.activity_scan_label_ms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_extend_layout);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);


        db = new DatabaseHandler(InputNoLabelActivity.this);
        progressDialog = new ProgressDialog(InputNoLabelActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputNoLabelActivity.this);
        username = session.getUsername();
        employeeCode = session.getID();
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        buttonOK = (Button) findViewById(R.id.input_button);
        manifest_label = (TextView) findViewById(R.id.manifest_label);
        manifest_isi = (TextView) findViewById(R.id.manifest_isi);
        hasil_manifest = (TextView) findViewById(R.id.hasil_manifest);
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
        manifest_label.setTypeface(type);
        manifest_isi.setTypeface(type,Typeface.BOLD);
        hasil_manifest.setTypeface(type);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        pengisi.setText(employeeCode);
        manifest_isi.setText(no);
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

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        JSONObject json = new JSONObject();
        try {
            json.put("service", "setLabelMS");
            json.put("ms", no);
            json.put("employeeCode", employeeCode);
            json.put("tgl", formattedDate);
            json.put("label", label.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("hasil json", ""+json);

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add(""+json);
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
                            Toast.makeText(InputNoLabelActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(2);
//                                Log.d("hasil soap data", ""+d.getJSONObject(1).getString("status"));
                                if (d.getJSONObject(1).getString("status").equals("1")){
                                    Intent pindah = new Intent(InputNoLabelActivity.this, HasilProses.class);
                                    pindah.putExtra("proses", "ms");
                                    pindah.putExtra("no", no);
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(InputNoLabelActivity.this, "Mohon Cek Kembali No Label Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(InputNoLabelActivity.this, "Mohon Cek Kembali No Label Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(InputNoLabelActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(InputNoLabelActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    private boolean validateKode() {
        if (label.getText().toString().trim().isEmpty()) {
            label.setError("Masukkan No Label");

            return false;
        } else {
            label.setError(null);
        }

        return true;
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

    @Override
    public void onBackPressed() {
        // do nothing.
    }

}
