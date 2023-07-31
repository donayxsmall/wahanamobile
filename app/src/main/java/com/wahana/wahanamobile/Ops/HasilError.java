package com.wahana.wahanamobile.Ops;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
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
 * Created by sadewa on 20/07/17.
 */

public class HasilError extends DrawerHelper {
    private static final String TAG = "HasilError";
    ProgressDialog progressDialog;

    String proses, no;
    TextView kode_kurir;
    TextView pengisi, tgl, calendar, manifest_label, manifest_isi, hasil_manifest;
    Button buttonOK;
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
    TableRow label;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("create","manifest");
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent(); // gets the previously created intent
        proses = myIntent.getStringExtra("proses");
        no = myIntent.getStringExtra("no");
        setContentView(R.layout.activity_hasil_proses);
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
        ImageView imageButton = (ImageView) mCustomView
                .findViewById(R.id.wahana_logonew2018);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HasilError.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        progressDialog = new ProgressDialog(HasilError.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(HasilError.this);
        username = session.getUsername();
        user_id = session.getID();
        calendar = (TextView) findViewById(R.id.calendar_isi);
        label = (TableRow) findViewById(R.id.label);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);
        buttonOK = (Button) findViewById(R.id.input_button);
        manifest_label = (TextView) findViewById(R.id.manifest_label);
        manifest_isi = (TextView) findViewById(R.id.manifest_isi);
        hasil_manifest = (TextView) findViewById(R.id.hasil_manifest);
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
        pengisi.setText(user_id);
        if (proses.equals("sj")){
            manifest_isi.setText(no);
            manifest_label.setText("PEMBUATAN SURAT JALAN GAGAL");
            hasil_manifest.setText("SURAT JALAN GAGAL DIBUAT");
        }else if(proses.equals("sjp")){
            manifest_isi.setText(no);
            manifest_label.setText("PEMBUATAN SURAT JALAN PENERUS GAGAL");
            hasil_manifest.setText("SURAT JALAN PENERUS GAGAL DIBUAT");
        }else if(proses.equals("stbt")) {
            manifest_isi.setText(no);
            manifest_label.setText("STBT GAGAL");
            hasil_manifest.setText("STBT GAGAL");
        }else if(proses.equals("stms")) {
            manifest_isi.setText(no);
            manifest_label.setText("STMS GAGAL");
            hasil_manifest.setText("STMS DIBUAT");
        }else if(proses.equals("stsm")) {
            manifest_isi.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            hasil_manifest.setText("BERHASIL STSM");
        }else if(proses.equals("ma")) {
            manifest_isi.setText(no);
            manifest_label.setText("PEMBUATAN MODA ANGKUTAN GAGAL");
            hasil_manifest.setText("MODA ANGKUTAN GAGAL DIBUAT");
        }else if(proses.equals("ms")) {
            manifest_isi.setText(no);
            manifest_label.setText("PEMBUATAN MANIFEST SORTIR GAGAL");
            hasil_manifest.setText("MANIFEST SORTIR GAGAL DIBUAT");
        }else if(proses.equals("sm")) {
            manifest_isi.setText(no);
            manifest_label.setText("NOMOR SURAT MUATAN");
            hasil_manifest.setText("SURAT MUATAN BERHASIL DIBUAT");
        }else if(proses.equals("stk")) {
            manifest_isi.setText(no);
            manifest_label.setText("STK GAGAL");
            hasil_manifest.setText("STK DIBUAT");
        }else if(proses.equals("ubah")) {
            manifest_isi.setText(no);
            manifest_label.setText("Ubah Password Gagal");
            hasil_manifest.setText("Ubah Password Gagal");
        }else if(proses.equals("absen")) {
            manifest_isi.setText(no);
            manifest_label.setText("Absen Gagal");
            hasil_manifest.setText("Absen Gagal");
        }else if(proses.equals("updateMA")) {
            manifest_isi.setText(no);
            manifest_label.setText("Update MA Gagal");
            hasil_manifest.setText("Update MA Gagal");
        }else if(proses.equals("retur")) {
            manifest_isi.setText(no);
            manifest_label.setText("Penerimaan Retur Gagal");
            hasil_manifest.setText("Penerimaan Retur Gagal");
        }else if(proses.equals("selisih")) {
            manifest_isi.setText(no);
            manifest_label.setText("Selisih Berat Gagal");
            hasil_manifest.setText("Selisih Berat Gagal");
        }else if(proses.equals("inputbbm")) {
            manifest_isi.setText(no);
            manifest_label.setText("Input BBM Gagal");
            hasil_manifest.setText("Input BBM Gagal");
        }else if(proses.equals("absenPalsu")) {
            manifest_isi.setText(no);
            manifest_label.setText("Absen Gagal");
            hasil_manifest.setText("Absen Gagal");
        }else if(proses.equals("updateSJP")) {
            manifest_isi.setText(no);
            manifest_label.setText("Update SJP Gagal");
            hasil_manifest.setText("Update SJP Gagal");
        }else if(proses.equals("USmanual")) {
            manifest_isi.setText(no);
            manifest_label.setText("Update TTK Terkirim Manual Gagal");
            hasil_manifest.setText("Update TTK Terkirim Manual Gagal");
        }else if(proses.equals("ttkbermasalah")) {
            manifest_isi.setText(no);
            manifest_label.setText("Update TTK Bermasalah Gagal");
            hasil_manifest.setText("Update TTK Bermasalah Manual Gagal");
        }else if(proses.equals("ttkterkirimpanelMW")) {
            manifest_isi.setText(no);
            manifest_label.setText("Update TTK Terkirim panel MW Gagal");
            hasil_manifest.setText("Update TTK Terkirim panel MW Gagal");
        }else if(proses.equals("ttkkeranjang")) {
            manifest_isi.setText(no);
            manifest_label.setText("Input Keranjang Retur Gagal");
            hasil_manifest.setText("Input Keranjang Retur Gagal");
        }else if(proses.equals("ttkdestroy")) {
            manifest_isi.setText(no);
            manifest_label.setText("Tandai Destroy Gagal");
            hasil_manifest.setText("Tandai Destroy Gagal");
        }else if(proses.equals("retursaltem")) {
            manifest_isi.setText(no);
            manifest_label.setText("Penerimaan Retur Gagal");
            hasil_manifest.setText("Penerimaan Retur Gagal");
        }else if(proses.equals("stksw")) {
            manifest_isi.setText(no);
            manifest_label.setText("STK SW GAGAL");
            hasil_manifest.setText("STK SW DIBUAT");
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, OpsMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitForm() {
//        if (!validateKode()) {
//            return;
//        }
//

//        Intent intent = new Intent(this, OpsMainActivity.class);
//        startActivity(intent);

        finish();
    }


    @Override
    public void onBackPressed() {
        // do nothing.
    }

}
