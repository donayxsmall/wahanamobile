package com.wahana.wahanamobile.Ops.tandaiRetur;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import com.wahana.wahanamobile.Ops.ReportPageActivity;
import com.wahana.wahanamobile.Ops.stms.SerahTerimaMSActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sadewa on 20/10/17.
 */

public class inputTTKActivity extends DrawerHelper {
    private static final String TAG = "inputTTKActivity";

    ProgressDialog progressDialog;
    public EditText inputVerifikasi;
    private Button btnInput;
    TextView pengisi, tgl, calendar;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String user_id;
    Button scan;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_ttk);
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

        progressDialog = new ProgressDialog(inputTTKActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(inputTTKActivity.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        scan = (Button) findViewById(R.id.scan_button);

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

        inputVerifikasi = (EditText) findViewById(R.id.input_ttk);
        this.setTitle("");

        inputVerifikasi.setTypeface(type);

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

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

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getTTKstbtByNik");
        parameter.add("50");
        parameter.add("0");
        parameter.add("ttk");
        parameter.add(inputVerifikasi.getText().toString().toUpperCase());
        parameter.add("nik");
        parameter.add(user_id);
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
                            Toast.makeText(inputTTKActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                            if (data.length()>1){
                                JSONArray msd = data.getJSONArray(1);
                                String btss = msd.getString(2);
                                String btbs = msd.getString(3);
                                final String ttkretur=msd.getString(4);
                                if (btss.equals("2")){
                                    if (btbs.equals("17")){
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                if (ttkretur == "null") {

                                                    Toast.makeText(inputTTKActivity.this, "TTK sudah ditandai Retur", Toast.LENGTH_LONG).show();

                                                    Intent picture_intent = new Intent(inputTTKActivity.this, ReportPageActivity.class);
                                                    picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=mkTTKRetur;iswv=1;asis=1&user=" + session.getUsername() + "&ttk=" + inputVerifikasi.getText().toString().toUpperCase());
                                                    //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
                                                    startActivity(picture_intent);
                                                }else{
                                                    Toast.makeText(inputTTKActivity.this, "TTK sudah ditandai Retur", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(inputTTKActivity.this, "Status TTK tidak bisa dirubah. Hubungi pengawas anda.",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }else if(btss.equals("3")){
                                    if (btbs.equals("14")){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(inputTTKActivity.this, "TTK sudah ditandai terkirim",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else if (btbs.equals("15")){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(inputTTKActivity.this, "TTK sudah ditandai gagal kirim",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else if(btbs.equals("16")){
                                        Intent pindah = new Intent(inputTTKActivity.this, TandaiRetur.class);
                                        pindah.putExtra("ttk",inputVerifikasi.getText().toString().toUpperCase());
                                        startActivity(pindah);
                                        finish();
                                    }else if (btbs.equals("13")){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(inputTTKActivity.this, "Status TTK tidak bisa dirubah.",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else if (btbs.equals("18")){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(inputTTKActivity.this, "Status TTK tidak bisa dirubah.",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(inputTTKActivity.this, "Status TTK tidak bisa dirubah. Hubungi pengawas anda.",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }else if (btss.equals("4")){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(inputTTKActivity.this, "Status TTK sudah terkirim",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(inputTTKActivity.this, "TTK tidak ditemukan",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(inputTTKActivity.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(inputTTKActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }

    private boolean validateKode() {
        if (inputVerifikasi.getText().toString().trim().isEmpty()) {
            inputVerifikasi.setError("Masukkan No. SM");

            return false;
        } else {
            inputVerifikasi.setError(null);
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
            inputVerifikasi.setText(nilai);
        }
    }
}
