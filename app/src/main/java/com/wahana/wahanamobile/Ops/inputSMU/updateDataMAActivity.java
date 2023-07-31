package com.wahana.wahanamobile.Ops.inputSMU;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.SJP.SearchPenerusActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
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

/**
 * Created by sadewa on 10/06/17.
 */

public class updateDataMAActivity extends DrawerHelper {
    private static final String TAG = "AddDataMAActivity";

    ProgressDialog progressDialog;

    private Button btnInput;
    TextView pengisi, tgl, calendar;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id, ma, kolimatang, kgmatang;
    private static final int CAMERA_REQUEST = 1888;
    EditText mapenerus, berat, koli, idPenerus, noSMU;
    AutoCompleteTextView penerus;
    private final int REQUEST_FOR_NOMOBIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ma);
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
                Intent intent = new Intent(updateDataMAActivity.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        Intent intent = getIntent();
        ma = intent.getStringExtra("ma");
        kgmatang = intent.getStringExtra("berat");
        kolimatang = intent.getStringExtra("koli");

        progressDialog = new ProgressDialog(updateDataMAActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(updateDataMAActivity.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        mapenerus = (EditText) findViewById(R.id.input_ma_penerus);
        noSMU = (EditText) findViewById(R.id.input_smu);
        berat = (EditText) findViewById(R.id.input_berat);
        koli = (EditText) findViewById(R.id.input_koli_matang);
        idPenerus = (EditText) findViewById(R.id.penerus_id);
        penerus=(AutoCompleteTextView)findViewById(R.id.input_penerus);

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

        this.setTitle("");

        penerus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(updateDataMAActivity.this,SearchAirlinesActivity.class),REQUEST_FOR_NOMOBIL);
            }
        });



        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateKode()) {
                    return;
                }

                if (Integer.parseInt(berat.getText().toString()) > Integer.parseInt(kgmatang)){
                    AlertDialog.Builder adb=new AlertDialog.Builder(updateDataMAActivity.this);
                    adb.setTitle("Input SMU");
                    adb.setMessage("Berat melebihi berat sistem. Apakah anda yakin ? ");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            submitForm();
                        }});
                    adb.show();
                }else {
                    AlertDialog.Builder adb=new AlertDialog.Builder(updateDataMAActivity.this);
                    adb.setTitle("Input SMU");
                    adb.setMessage("Apakah anda yakin ? ");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            submitForm();
                        }});
                    adb.show();
                }
            }
        });

    }

    private void submitForm() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        JSONObject json = new JSONObject();
        try {
            json.put("service", "setDataSMU");
            json.put("ma", ma);
            json.put("noAirlines", mapenerus.getText().toString());
            json.put("maPenerus", noSMU.getText().toString());
            json.put("penerus", idPenerus.getText().toString());
            json.put("tgl", formattedDate);
            json.put("berat", berat.getText().toString());
            json.put("koli", koli.getText().toString());
            json.put("employeeCode", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("hasil error", ""+e);
        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add(""+json);
        Log.d("hasil json", ""+json);
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
                            Toast.makeText(updateDataMAActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                final JSONArray d = data.getJSONArray(2);
                                if (d.getJSONObject(1).getString("status").equals("1")){
                                    Intent pindah = new Intent(updateDataMAActivity.this, HasilProses.class);
                                    pindah.putExtra("proses","updateMA");
                                    JSONArray msd = data.getJSONArray(1);
                                    String ma = msd.getString(0);
                                    pindah.putExtra("no",ma);
//                                    pindah.putExtra("no","MA-123-123");
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(updateDataMAActivity.this, "Mohon Cek Kembali No MA Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(updateDataMAActivity.this, "Mohon Cek Kembali No MA Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(updateDataMAActivity.this, HasilError.class);
                            pindah.putExtra("proses","updateMA");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        Log.d("hasil error", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(updateDataMAActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }

        }.execute(parameter);
    }

    private boolean validateKode() {
            if(penerus.getText().toString().trim().isEmpty()) {
                penerus.setError("Masukkan Airlines");

                return false;
            }else if(mapenerus.getText().toString().trim().isEmpty()){
                mapenerus.setError("Masukkan No Penerbangan");

                return false;
            }else if(noSMU.getText().toString().trim().isEmpty()){
                noSMU.setError("Masukkan No SMU");

                return false;
            }else if(berat.getText().toString().trim().isEmpty()){
                berat.setError("Masukkan Berat SMU");

                return false;
            }else if(koli.getText().toString().trim().isEmpty()){
                koli.setError("Masukkan Koli SMU");

                return false;
            }else if(Integer.parseInt(koli.getText().toString()) > Integer.parseInt(kolimatang)){
                koli.setError("Jumlah koli tidak boleh melebihi koli sistem");

                return false;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case REQUEST_FOR_NOMOBIL:
                    String penerusTxt = data.getStringExtra("penerus");
                    String id = data.getStringExtra("id");
                    penerus.setText(penerusTxt);
                    idPenerus.setText(id);
                    break;
            }

        }
    }
}
