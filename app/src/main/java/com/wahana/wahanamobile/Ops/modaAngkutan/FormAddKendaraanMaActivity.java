package com.wahana.wahanamobile.Ops.modaAngkutan;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wahana.wahanamobile.Data.BuatSJ;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.manifestSortir.ManifestSortirActivity;
import com.wahana.wahanamobile.OpsMainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.BuatSJAdapter;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sadewa on 10/06/17.
 */

public class FormAddKendaraanMaActivity extends DrawerHelper {
    private static final String TAG = "FormAddKendaraanMaActivity";

    private Button btnInput;
    TextView pengisi, tgl, calendar,id_mobil,id_kodetujuan,seal;
    Button submit;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id;
    DatabaseHandler db;
    List<BuatSJ> kendaraanList;
    ListView lv;
    AutoCompleteTextView pilihmobil,kodevia;
    BuatSJAdapter adapter;
    private final int REQUEST_FOR_VEHICLE = 1;
    private final int REQUEST_FOR_KODEVIA = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formaddkendaraan_ma);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.abs_extend_layout, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        db = new DatabaseHandler(FormAddKendaraanMaActivity.this);
        session = new SessionManager(FormAddKendaraanMaActivity.this);

        pilihmobil=(AutoCompleteTextView) findViewById(R.id.pilihmobil);
        id_mobil=(TextView) findViewById(R.id.id_mobil);
        id_kodetujuan=(TextView) findViewById(R.id.id_kodetujuan);
        seal=(TextView) findViewById(R.id.seal);
        kodevia=(AutoCompleteTextView) findViewById(R.id.kodetujuan);

        submit=(Button) findViewById(R.id.input_button);

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");

        pilihmobil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(FormAddKendaraanMaActivity.this,SearchMobilActivity.class),REQUEST_FOR_VEHICLE);
            }

        });

        kodevia.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(FormAddKendaraanMaActivity.this,SearchViaMaActivity.class),REQUEST_FOR_KODEVIA);
            }

        });

        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                submitform();
            }

        });

    }

    public void submitform(){
        if (!validate()) {
            return;
        }

        String no = pilihmobil.getText().toString().toUpperCase();
        String tujuan = kodevia.getText().toString();
        String sealId;
        int ttk = db.checkKendaraan(no,tujuan);
//        Log.d("Hasil kendaraan", ""+no+ttk);
        if (ttk==0){
            if (seal.getText().toString().equals("NO SEAL")){
                sealId = "0";
            }else {
                sealId = "1";
            }
            BuatSJ sj = new BuatSJ();
            sj.setTtk(no);
            sj.setSeal(sealId);
            sj.setTujuan(tujuan);
            db.addKendaraan(sj);
            startActivity(new Intent(FormAddKendaraanMaActivity.this,AddKendaraanActivity.class));
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Nomor kendaraan dengan tujuan "+tujuan+" sudah ada dalam daftar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, AddKendaraanActivity.class);
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
                case REQUEST_FOR_VEHICLE:
                    String nopol = data.getStringExtra("nopol");
                    String sealId = data.getStringExtra("seal");
                    pilihmobil.setText(nopol);
                    seal.setText(sealId);
                    break;

                case REQUEST_FOR_KODEVIA:
                    String nopol1 = data.getStringExtra("nopol");
                    kodevia.setText(nopol1);
                    break;
            }

        }
    }



    private boolean validate() {
        if (pilihmobil.getText().toString().trim().isEmpty()) {
            pilihmobil.setError("Masukkan Mobil");

            return false;
        } else if(kodevia.getText().toString().trim().isEmpty()) {
            kodevia.setError("Masukkan kode Tujuan");

            return false;
        }else{
            pilihmobil.setError(null);
            kodevia.setError(null);
        }

        return true;
    }

}