package com.wahana.wahanamobile.Ops.stpu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wahana.wahanamobile.Data.ListTtkPickup;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.DownloadTask;
import com.wahana.wahanamobile.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StpuVerifikasi extends DrawerHelper {
    private static final String TAG = "StpuVerifikasi";

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
    DatabaseHandler db;
    Activity context;
    String kodeagen,niksupir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stpu_verifikasi);

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

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        Intent myIntent = getIntent();
        niksupir=myIntent.getStringExtra("niksupir");

        progressDialog = new ProgressDialog(StpuVerifikasi.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(StpuVerifikasi.this);
        user_id = session.getID();
        db = new DatabaseHandler(StpuVerifikasi.this);

        btnInput = (Button) findViewById(R.id.input_button);

        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.kode_kurir);

        inputVerifikasi = (EditText) findViewById(R.id.input_verifikasi);

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
        inputVerifikasi.setTypeface(type);


        db.deleteListSumberSTPU();

//        new DownloadTask(StpuVerifikasi.this,btnInput, Utils.downloadcsvUrl,"stpu");



        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                submitForm();
            }
        });





    }

//    private void submitForm() {
//        if (!validateKode()) {
//            return;
//        }
//
//
//        int histPickup=db.checkListSTPUHistory(niksupir);
//
//
//
//        if(histPickup==1){
//
//            ListTtkPickup ttk = db.getTTKhistoryMaxSTPU(niksupir);
//
//            AlertDialog.Builder adb=new AlertDialog.Builder(StpuVerifikasi.this);
//            adb.setTitle("Info");
//            adb.setMessage("Data ttk sudah pernah di input , Apakah mau melanjutkan STPU dengan ttk terakhir "+ttk.getTtk()+"?");
//            adb.setNegativeButton("Tidak", new AlertDialog.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    db.deleteListSTPU(niksupir);
//
//                    Intent intent = new Intent(StpuVerifikasi.this, StpuScanner.class);
//                    intent.putExtra("niksupir",niksupir);
//                    intent.putExtra("kodeverifikasi",inputVerifikasi.getText().toString());
//                    startActivity(intent);
//                    finish();
//
//                }});
//            adb.setPositiveButton("Iya", new AlertDialog.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    //db.deleteListPickup();
//                    Intent intent = new Intent(StpuVerifikasi.this, StpuScanner.class);
//                    intent.putExtra("niksupir",niksupir);
//                    intent.putExtra("kodeverifikasi",inputVerifikasi.getText().toString());
//                    startActivity(intent);
//                    finish();
//                }});
//
//            adb.show();
//        }else{
//
//            Intent intent = new Intent(StpuVerifikasi.this, StpuScanner.class);
//            intent.putExtra("niksupir",niksupir);
//            intent.putExtra("kodeverifikasi",inputVerifikasi.getText().toString());
//            startActivity(intent);
//            finish();
//
//        }
//
//
//
//        Log.d("histpickup",""+histPickup);
//
////        Intent intent = new Intent(StpuVerifikasi.this, StpuScanner.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        startActivity(intent);
////        finish();
//
//    }


    private boolean validateKode() {
        if (inputVerifikasi.getText().toString().trim().isEmpty()) {
            inputVerifikasi.setError("Masukkan Kode Verifikasi");

            return false;
        } else {
            inputVerifikasi.setError(null);
        }

        return true;
    }


}