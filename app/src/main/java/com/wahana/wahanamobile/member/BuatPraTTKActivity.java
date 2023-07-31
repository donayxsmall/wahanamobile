package com.wahana.wahanamobile.member;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.util.LruCache;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.ImageLoadingUtils;
import com.wahana.wahanamobile.utils.ImageUriDatabases;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class BuatPraTTKActivity  extends DrawerHelper {
    Spinner provinsiSpinner,kelurahanSpinner,kecamatanSpinner,kabkotSpinner,mprovinsiSpinner,mkelurahanSpinner,mkecamatanSpinner,mkabkotSpinner;
    Spinner spinnerLayanan;
    public ArrayAdapter<CharSequence> adapter;
    //private ImageView imageView;
    private ImageView imgView;
    private ImageUriDatabases database;
    Uri imageUri;
    ImageView imageView;
    byte[] bArray;
    String tipe="ttk terkirim";
    private ImageLoadingUtils utils;

    private Cursor cursor;
    private LruCache<String, Bitmap> memoryCache;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    private static final String TAG = "BuatPraTTKActivity";

    ProgressDialog progressDialog;

    SessionManager session;
    int request_id;
    String username, user_id,session_id;
    TextView pengisi, tgl, ttk, calendar;
    Button next,submit;
    Double lat = 0.0, longi = 0.0;

    String formattedDate;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    LinearLayout main, result, pengirim_sendiri, pengirim_lain, penerima_member, penerima_nonmember;
    EditText nama_agen,no_ktp, no_handphone ,email_agen ,penanggung_jawab, alamat_agen;
    TextView nama,jabatan, nama_agen_label, no_ktp_label,no_handphone_label,email_agen_label, penanggung_jawab_label, alamat_agen_label, member_isi;
    ImageView foto;
    DatabaseHandler db;
    String nomorTTK;
    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_pra_ttk);
        super.onCreateDrawer(this);

        ////////////////////////////////////////////////
        //        TOOLBAR SECTION
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setIcon(R.drawable.wahana_logo);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_extend_layout);
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
                Intent intent = new Intent(BuatPraTTKActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;


        ////////////////////////////////////////////////
        //        GPS SECTION
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            // Setting Dialog Title
            alertDialog.setTitle(getString(R.string.error_message_title));
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.error_message));
            // On pressing Settings button
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent pindah = new Intent(BuatPraTTKActivity.this, MainActivity.class);
                            startActivity(pindah);
                            finish();
                        }
                    });
        }

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            // Setting Dialog Title
            alertDialog.setTitle(getString(R.string.gps_violation_title));
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.gps_violation));
            // On pressing Settings button
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            BuatPraTTKActivity.this.startActivity(intent);
                            finish();
                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }

        spinnerLayanan = (Spinner) findViewById(R.id.spinner_layanan);
        main=(LinearLayout)findViewById(R.id.main_input);
        result=(LinearLayout)findViewById(R.id.main_result);
        pengirim_sendiri =(LinearLayout)findViewById(R.id.pengirim_sendiri);
        pengirim_lain=(LinearLayout)findViewById(R.id.pengirim);
        penerima_member =(LinearLayout)findViewById(R.id.penerima_member);
        penerima_nonmember=(LinearLayout)findViewById(R.id.penerima_bukanmember);
        adapter = ArrayAdapter.createFromResource(this, R.array.layanan, R.layout.simple_spinner_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLayanan.setAdapter(adapter);

        progressDialog = new ProgressDialog(BuatPraTTKActivity.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(BuatPraTTKActivity.this);
        username = session.getUsername();
        user_id = session.getID();
        session_id=session.getSessionID();
        Random rand;
        rand = new Random();
        request_id=rand.nextInt(10000);
        database = new ImageUriDatabases(this);
        utils = new ImageLoadingUtils(this);

        ////////////////////////////////////////////////
        //        FORM SECTION
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);

        nama_agen = (EditText) findViewById(R.id.input_nama_agen);
        no_ktp = (EditText) findViewById(R.id.input_ktp);
        no_handphone = (EditText) findViewById(R.id.input_handphone_agen);
        email_agen = (EditText) findViewById(R.id.input_email);
        penanggung_jawab = (EditText) findViewById(R.id.input_penanggung_jawab);

        nama_agen_label = (TextView) findViewById(R.id.nama_agen_label);
        no_ktp_label = (TextView) findViewById(R.id.ktp_label);
        no_handphone_label = (TextView) findViewById(R.id.handphone_agen_label);
        email_agen_label = (TextView) findViewById(R.id.email_agen_label);
        penanggung_jawab_label = (TextView) findViewById(R.id.penanggung_jawab_label);
        member_isi = (TextView) findViewById(R.id.member_isi);

        provinsiSpinner = (Spinner) findViewById(R.id.provinsi_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.brand_logistik, R.layout.simple_spinner_list_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinsiSpinner.setAdapter(adapter);
        kabkotSpinner = (Spinner) findViewById(R.id.kabkot_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.brand_logistik, R.layout.simple_spinner_list_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kabkotSpinner.setAdapter(adapter);
        kelurahanSpinner = (Spinner) findViewById(R.id.kelurahan_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.brand_logistik, R.layout.simple_spinner_list_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kelurahanSpinner.setAdapter(adapter);
        kecamatanSpinner = (Spinner) findViewById(R.id.kecamatan_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.brand_logistik, R.layout.simple_spinner_list_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kecamatanSpinner.setAdapter(adapter);

        mprovinsiSpinner = (Spinner) findViewById(R.id.provinsi_spinner_member);
        adapter = ArrayAdapter.createFromResource(this, R.array.brand_logistik, R.layout.simple_spinner_list_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mprovinsiSpinner.setAdapter(adapter);
        mkabkotSpinner = (Spinner) findViewById(R.id.kabkot_spinner_member);
        adapter = ArrayAdapter.createFromResource(this, R.array.brand_logistik, R.layout.simple_spinner_list_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mkabkotSpinner.setAdapter(adapter);
        mkelurahanSpinner = (Spinner) findViewById(R.id.kelurahan_spinner_member);
        adapter = ArrayAdapter.createFromResource(this, R.array.brand_logistik, R.layout.simple_spinner_list_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mkelurahanSpinner.setAdapter(adapter);
        mkecamatanSpinner = (Spinner) findViewById(R.id.kecamatan_spinner_member);
        adapter = ArrayAdapter.createFromResource(this, R.array.brand_logistik, R.layout.simple_spinner_list_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mkecamatanSpinner.setAdapter(adapter);

        next = (Button) findViewById(R.id.input_button);
        submit = (Button) findViewById(R.id.submit_button);
        calendar = (TextView) findViewById(R.id.calendar_isi);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        calendar.setText(formattedCal);
        member_isi.setText(username);


        ////////////////////////////////////////////////
        //        TYPEFACE SECTION
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        calendar.setTypeface(type);
        member_isi.setTypeface(type);


        formattedDate=dateTime.format(c.getTime());
//        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
//        btnClickImage.setOnClickListener(this);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextForm();
            }
        });

    }

    public void onPengirimSendiri(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_pengirim:
                if (checked)
                {
                    Toast.makeText(this, "pengirim diri sendiri", Toast.LENGTH_LONG).show();
                    pengirim_sendiri.setVisibility(View.VISIBLE);
                    pengirim_lain.setVisibility(View.GONE);
                }
                else
                {
                    Toast.makeText(this, "pengirim bukan diri sendiri", Toast.LENGTH_LONG).show();
                    pengirim_lain.setVisibility(View.VISIBLE);
                    pengirim_sendiri.setVisibility(View.GONE);
                }
                break;
            // TODO: Veggie sandwich
        }
    }

    public void onPenerimaMember(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_penerima:
                if (checked)
                {
                    penerima_member.setVisibility(View.VISIBLE);
                    penerima_nonmember.setVisibility(View.GONE);
                }
                else
                {
                    Toast.makeText(this, "penerima bukan member", Toast.LENGTH_LONG).show();
                    penerima_nonmember.setVisibility(View.VISIBLE);
                    penerima_member.setVisibility(View.GONE);
                }
                break;
            // TODO: Veggie sandwich
        }
    }

    private void nextForm() {
//        startActivity(new Intent(BuatPraTTKActivity.this, PendaftaranCalonAgen2.class));
        main.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        result.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    public boolean isInternetAvailable(Context c) {

        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }
}

