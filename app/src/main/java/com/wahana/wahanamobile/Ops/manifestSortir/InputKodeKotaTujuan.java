package com.wahana.wahanamobile.Ops.manifestSortir;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.Ops.stms.SerahTerimaMSActivity;
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
 * Created by sadewa on 09/06/17.
 */

public class InputKodeKotaTujuan extends DrawerHelper {
    private static final String TAG = "InputKodeTujuan";

    ProgressDialog progressDialog;

//    public EditText inputVerifikasi;
    private Button btnInput;
    TextView pengisi, tgl, calendar;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id;
    AutoCompleteTextView inputKodeTujuan;
    private final int REQUEST_FOR_KODETUJUAN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_kota_tujuan_manifest);
        super.onCreateDrawer(this);
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
                Intent intent = new Intent(InputKodeKotaTujuan.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(InputKodeKotaTujuan.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputKodeKotaTujuan.this);
        user_id = session.getID();

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_pelaksana);

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

        inputKodeTujuan = (AutoCompleteTextView) findViewById(R.id.input_kode_tujuan);
        this.setTitle("");

        inputKodeTujuan.setTypeface(type);

        inputKodeTujuan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(InputKodeKotaTujuan.this,SearchKodeTujuanMS.class),REQUEST_FOR_KODETUJUAN);
            }

        });




//        inputKodeTujuan.addTextChangedListener(new TextWatcher() {
//            int keyDel;
//            int len=0;
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                inputKodeTujuan.setOnKeyListener(new View.OnKeyListener() {
//                    @Override
//                    public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                        if (keyCode == KeyEvent.KEYCODE_DEL)
//                            keyDel = 1;
//                        return false;
//                    }
//                });
//
//                if (keyDel == 0) {
//                    int len = inputKodeTujuan.getText().length();
////                    if(len == 3 || len == 7 || len == 9) {
////                    if(len == 2 || len == 6) {
//                    if (((len + 1) % 4) == 0) {
//                        inputKodeTujuan.setText(inputKodeTujuan.getText() + "-");
//                        inputKodeTujuan.setSelection(inputKodeTujuan.getText().length());
//                    }
//
//                } else {
//                    keyDel = 0;
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable arg0) {
//                // TODO Auto-generated method stub
//
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//                // TODO Auto-generated method stub
//
//            }
//        });

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

        Intent pindah = new Intent(this, ManifestSortirActivity.class);
//        Intent pindah = new Intent(this, HasilProses.class);
        pindah.putExtra("kodekotatujuanMS",inputKodeTujuan.getText().toString());
        startActivity(pindah);
        finish();

    }

    private boolean validateKode() {
        if (inputKodeTujuan.getText().toString().trim().isEmpty()) {
            inputKodeTujuan.setError("Masukkan Kode Kota Tujuan");

            return false;
        } else {
            inputKodeTujuan.setError(null);
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

            String nopol = data.getStringExtra("nopol");
            String sealId = data.getStringExtra("seal");
            inputKodeTujuan.setText(nopol);
//            seal.setText(sealId);
        }
    }
}