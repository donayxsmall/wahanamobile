package com.wahana.wahanamobile.Ops.modaAngkutan;

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
import android.telephony.PhoneNumberFormattingTextWatcher;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by sadewa on 09/06/17.
 */

public class InputKodeVia extends DrawerHelper {
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
    private final int REQUEST_FOR_KODEVIA = 1;
    EditText input_kode_kantortxt,input_kode_tujuan2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_kodevia_ma);
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
                Intent intent = new Intent(InputKodeVia.this, OpsMainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(InputKodeVia.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputKodeVia.this);
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
        input_kode_kantortxt = (EditText)findViewById(R.id.input_kode_kantor);
        input_kode_tujuan2 = (EditText)findViewById(R.id.input_kode_tujuan2);
        this.setTitle("");

        inputKodeTujuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_kode_kantortxt.getText().toString().equals("")){
                    Toast.makeText(InputKodeVia.this, "Mohon isi kode kantor", Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(InputKodeVia.this,SearchViaMaActivity.class);
                    i.putExtra("kdkantor",input_kode_kantortxt.getText().toString());
                    startActivityForResult(i,REQUEST_FOR_KODEVIA);
                }
            }
        });

        inputKodeTujuan.setTypeface(type);

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

        Intent pindah = new Intent(this, ScanMAActivity.class);
//        Intent pindah = new Intent(this, AddDataMAActivity.class);
        pindah.putExtra("kdkantor",input_kode_kantortxt.getText().toString());
        pindah.putExtra("tujuan",inputKodeTujuan.getText().toString());
        pindah.putExtra("kdtujuan2",input_kode_tujuan2.getText().toString());
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
            input_kode_kantortxt.setFocusable(false);
//            seal.setText(sealId);
        }
    }


//    private TextWatcher onTextChangedListener() {
//        return new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                inputKodeTujuan.removeTextChangedListener(this);
//
//                try {
//                    String originalString = s.toString();
//
//                    Long longval;
//                    if (originalString.contains(",")) {
//                        originalString = originalString.replaceAll(",", "");
//                    }
//                    longval = Long.parseLong(originalString);
//
//                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//                    formatter.applyPattern("#,###,###,###");
//                    String formattedString = formatter.format(longval);
//
//                    //setting text after format to EditText
//                    inputKodeTujuan.setText(formattedString);
//                    inputKodeTujuan.setSelection(inputKodeTujuan.getText().length());
//                } catch (NumberFormatException nfe) {
//                    nfe.printStackTrace();
//                }
//
//                inputKodeTujuan.addTextChangedListener(this);
//            }
//        };
//    }




}