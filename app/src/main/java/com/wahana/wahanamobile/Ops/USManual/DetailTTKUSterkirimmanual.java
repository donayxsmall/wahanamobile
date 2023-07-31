package com.wahana.wahanamobile.Ops.USManual;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.ApprovalSJ.ListApprovalSJ;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

public class DetailTTKUSterkirimmanual extends AppCompatActivity {

    private static final String TAG = "sj";
    SessionManager session;
    TextView nottk,kodtuj,namtuj,kodestatus,namastatus,kodeops,tglttk,layanan;
    Button input_button;
    ProgressDialog progressDialog;
    String ttk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ttkusterkirimmanual);

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
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailTTKUSterkirimmanual.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(DetailTTKUSterkirimmanual.this, R.style.AppTheme_Dark_Dialog);

        session = new SessionManager(DetailTTKUSterkirimmanual.this);

        nottk=(TextView)findViewById(R.id.judul);
        input_button=(Button)findViewById(R.id.input_button);

        kodtuj=(TextView)findViewById(R.id.kotuj);
        namtuj=(TextView)findViewById(R.id.namtuj);
        kodestatus=(TextView)findViewById(R.id.kodestatus);
        namastatus=(TextView)findViewById(R.id.namastatus);
        kodeops=(TextView)findViewById(R.id.kodeops);
        tglttk=(TextView)findViewById(R.id.tglttk);
        layanan=(TextView)findViewById(R.id.layanan);

        Intent intent = getIntent();
        ttk=intent.getStringExtra("ttk");


        nottk.setText(ttk);


        setdata();


        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb=new AlertDialog.Builder(DetailTTKUSterkirimmanual.this);
                adb.setTitle("Info");
                adb.setMessage("Apakah anda yakin ingin Update TTK ini ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updatedata();
                    }});
                adb.show();
            }
        });


    }


    private void setdata(){

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add("12345");
        parameter.add("getDetailTTKAktif");
        parameter.add("0");
        parameter.add("0");
        parameter.add("ATRBTNO");
        parameter.add(ttk);

        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Mengambil data TTK...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                progressDialog.dismiss();
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DetailTTKUSterkirimmanual.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String text = result.getProperty(1).toString();
                        Log.d("hasil soap data", ""+text);
                        if (text.equals("OK")) {
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);

                                            kodtuj.setText(isi.getString(0));
                                            namtuj.setText(isi.getString(1));
                                            kodestatus.setText(isi.getString(6));
                                            namastatus.setText(isi.getString(7));
                                            kodeops.setText(isi.getString(8));
                                            tglttk.setText(isi.getString(9));
                                            layanan.setText(isi.getString(10));

                                        }

                                    }

//                                    adapter = new ListViewAdapterSearchKotaAsal(SearchKotaAsal.this, R.layout.listviewsearchkotaasal, originList);
//                                    listView.setAdapter(adapter);

//                                    adapter= new AdapterSJApproval(sjList,getApplicationContext());
//
//                                    listView.setAdapter(adapter);


                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(DetailTTKUSterkirimmanual.this, text,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(DetailTTKUSterkirimmanual.this, "TTK tidak terdaftar",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(DetailTTKUSterkirimmanual.this, text,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DetailTTKUSterkirimmanual.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);
    }




    private void updatedata(){

        JSONObject json = new JSONObject();
        try {
            json.put("service", "setUpdateTTKterkirimManual");
            json.put("employeecode", session.getID());
            json.put("ttk", ttk);
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
                            Toast.makeText(DetailTTKUSterkirimmanual.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty(0).toString();
                        final String text = result.getProperty(1).toString();
                        if (code.equals("1")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();

                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(2);
//                                Log.d("hasil soap data", ""+d.getJSONObject(1).getString("status"));
                                if (d.getJSONObject(1).getString("status").equals("1")){
                                    Intent pindah = new Intent(DetailTTKUSterkirimmanual.this, HasilProses.class);
                                    pindah.putExtra("proses","USmanual");
                                    pindah.putExtra("no", ttk);
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(DetailTTKUSterkirimmanual.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(DetailTTKUSterkirimmanual.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(DetailTTKUSterkirimmanual.this, HasilError.class);
                            pindah.putExtra("proses","USmanual");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DetailTTKUSterkirimmanual.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, UpdateTerkirimManual.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
