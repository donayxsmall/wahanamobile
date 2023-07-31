package com.wahana.wahanamobile.Ops.KeranjangRetur;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
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
import android.widget.Toast;

import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class BuatKeranjangBaru extends AppCompatActivity {

    private static final String TAG = "inputKeranjang";

    ProgressDialog progressDialog;
    SessionManager session;
    String user_id;
    Button input_button,scankeranjang;
    EditText input_keranjang,input_namakeranjang;
    TextView pengisi, tgl, calendar;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    String ttkManual,employeeCode,via,nokeranjang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_keranjang_baru);

        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(BuatKeranjangBaru.this));

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

        progressDialog = new ProgressDialog(BuatKeranjangBaru.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(BuatKeranjangBaru.this);
        user_id = session.getID();
        employeeCode = session.getID();

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        input_keranjang=(EditText)findViewById(R.id.input_nomorkeranjang);
        input_namakeranjang=(EditText)findViewById(R.id.input_namakeranjang);
        input_button=(Button)findViewById(R.id.input_button);


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


        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateKode()) {
                    return;
                }

                checkkeranjang();

            }
        });



    }


    private void checkkeranjang() {
        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("cekkeranjang");
        parameter.add("0");
        parameter.add("0");
        parameter.add("noker");
        parameter.add(input_keranjang.getText().toString().toUpperCase());
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
                            Toast.makeText(BuatKeranjangBaru.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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

                                    Toast.makeText(BuatKeranjangBaru.this, "No Keranjang sudah ada disistem",Toast.LENGTH_LONG).show();

//                                Intent pindah = new Intent(BuatKeranjangBaru.this, KeranjangScanner.class);
//                                pindah.putExtra("nokeranjang",input_keranjang.getText().toString().toUpperCase());
//                                startActivity(pindah);
//                                finish();

                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(BuatKeranjangBaru.this, "No Keranjang bisa ditambahkan",Toast.LENGTH_LONG).show();

                                        insertKeranjangReturBaru();

                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(BuatKeranjangBaru.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(BuatKeranjangBaru.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }



    private void insertKeranjangReturBaru(){
        JSONObject json = new JSONObject();
        try {
            json.put("service", "setKeranjangReturBaru");
            json.put("employeeCode", employeeCode);
            json.put("nokeranjang", input_keranjang.getText().toString().toUpperCase());
            json.put("namakeranjang",input_namakeranjang.getText().toString());
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
                            Toast.makeText(BuatKeranjangBaru.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
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

                                    JSONArray msd = data.getJSONArray(1);
                                    String noker = msd.getString(1);

                                    Intent pindah = new Intent(BuatKeranjangBaru.this, HasilProses.class);
                                    pindah.putExtra("proses","ttkkeranjangbaru");
                                    pindah.putExtra("no", noker);
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(BuatKeranjangBaru.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(BuatKeranjangBaru.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(BuatKeranjangBaru.this, HasilError.class);
                            pindah.putExtra("proses","ttkkeranjangbaru");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(BuatKeranjangBaru.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }





    private boolean validateKode() {
        if (input_keranjang.getText().toString().trim().isEmpty()) {
            input_keranjang.setError("Masukkan No Keranjang");

            return false;
        } else {
            input_keranjang.setError(null);
        }

        if (input_namakeranjang.getText().toString().trim().isEmpty()) {
            input_namakeranjang.setError("Masukkan Nama Keranjang");

            return false;
        } else {
            input_namakeranjang.setError(null);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
