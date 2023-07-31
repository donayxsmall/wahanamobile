package com.wahana.wahanamobile.Ops.stsm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sadewa on 14/06/17.
 */

public class inputSMActivity extends DrawerHelper {
    private static final String TAG = "inputSMActivity";

    ProgressDialog progressDialog;

    private TextInputLayout inputLayoutVerifikasi;
    public EditText inputVerifikasi;
    private Button btnInput;
    String agentcode;
    TextView kode_kurir, kode_agent;
    TextView pengisi, tgl, calendar;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    String username, user_id;
    TextView nama,jabatan;
    ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_sm);
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
                Intent intent = new Intent(inputSMActivity.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(inputSMActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(inputSMActivity.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);

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

        inputVerifikasi = (EditText) findViewById(R.id.input_verifikasi);
        this.setTitle("");

        inputVerifikasi.setTypeface(type);

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

    }

    private void submitForm() {
        if (!validateKode()) {
            return;
        }

        Intent pindah = new Intent(inputSMActivity.this, SerahTerimaSMActivity.class);
        pindah.putExtra("sm",inputVerifikasi.getText().toString());
        startActivity(pindah);
        finish();

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("whnGetBAKurirByNo");
        parameter.add("5");
        parameter.add("0");
        parameter.add("ATRBACD");
        parameter.add(inputVerifikasi.getText().toString());
//        new SoapClientMobile(){
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                Log.i(TAG, "onPreExecute");
//                progressDialog.setIndeterminate(true);
//                progressDialog.setMessage("Authenticating...");
//                progressDialog.show();
//            }
//
//            @Override
//            protected void onPostExecute(SoapObject result) {
//                super.onPostExecute(result);
//                Log.d("hasil soap", ""+result);
//                if(result==null){
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(inputSMActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
//                        }
//                    });
//                }else {
//                    try{
//                        final String text = result.getProperty(1).toString();
//                        Log.d("hasil soap data", ""+text);
//                        if (text.equals("OK")) {
//                            progressDialog.dismiss();
//                            String so = result.getProperty(2).toString();
//
//                            JSONObject jsonObj = new JSONObject(so);
//                            JSONArray data = jsonObj.getJSONArray("data");
//                            if (data.length()>1){
//                                final JSONArray d = data.getJSONArray(1);
//                                if (d.getInt(1) != 2){
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            Toast.makeText(inputSMActivity.this, "Kurir sudah tidak Aktif",Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }else if (d.getInt(2)>0 || d.getInt(3)>0){
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            try {
//                                                Toast.makeText(inputSMActivity.this, "Kurir ini memiliki TTK yang belum terupdate statusnya: "+d.getInt(2)+" dalam proses pengiriman dan "+d.getInt(3)+" belum serah terima BT",Toast.LENGTH_LONG).show();
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                }else{
//                                    Intent pindah = new Intent(inputSMActivity.this, InputViaKurir1.class);
//                                    pindah.putExtra("kurir",inputVerifikasi.getText().toString());
//                                    startActivity(pindah);
//                                    finish();
//                                }
//                            }else{
//                                runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(inputSMActivity.this, "Data kurir tidak ditemukan",Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//                        }else{
//                            progressDialog.dismiss();
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(inputSMActivity.this, text,Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                    }catch (Exception e){
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(inputSMActivity.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
//                                progressDialog.dismiss();
//                            }
//                        });
//                    }
//                }
//            }
//        }.execute(parameter);
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
}
