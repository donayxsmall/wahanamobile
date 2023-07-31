package com.wahana.wahanamobile.member;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.LruCache;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.wahana.wahanamobile.CustomerRelation.BuatAgen3Activity;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.ImageLoadingUtils;
import com.wahana.wahanamobile.utils.ImageUriDatabases;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class PendaftaranCalonAgen2 extends DrawerHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Spinner provinsiSpinner,kelurahanSpinner,kecamatanSpinner,kabkotSpinner;
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
    private static final String TAG = "BuatAgen2Activity";

    ProgressDialog progressDialog;

    SessionManager session;
    int request_id;
    String username, user_id,session_id;
    TextView pengisi, tgl, ttk, calendar;
    Button submit, submitMap;
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
    EditText alamat_agen, latitude_agen, longitude_agen;
    TextView nama,jabatan, latitude_label, longitude_label, provinsi_label, kabkot_label, kelurahan_label, kecamatan_label, alamat_agen_label;
    ImageView foto, iv;
    DatabaseHandler db;
    String nomorTTK;
    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendaftaran_calon_agen2);
        db = new DatabaseHandler(PendaftaranCalonAgen2.this);
        super.onCreateDrawer(this);
        ////////////////////////////////////////////////
        //        TOOLBAR SECTION
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
                Intent intent = new Intent(PendaftaranCalonAgen2.this, MainActivity.class);
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
                            Intent pindah = new Intent(PendaftaranCalonAgen2.this, MainActivity.class);
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
                            PendaftaranCalonAgen2.this.startActivity(intent);
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

        progressDialog = new ProgressDialog(PendaftaranCalonAgen2.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(PendaftaranCalonAgen2.this);
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
        iv = (ImageView) findViewById(R.id.mapView);
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
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);



        alamat_agen = (EditText) findViewById(R.id.input_alamat_agen);
        latitude_agen = (EditText) findViewById(R.id.input_latitude);
        longitude_agen = (EditText) findViewById(R.id.input_longitude);
        alamat_agen_label = (TextView) findViewById(R.id.alamat_agen_label);
        provinsi_label = (TextView) findViewById(R.id.provinsi_label);
        kabkot_label = (TextView) findViewById(R.id.kabkot_label);
        kecamatan_label = (TextView) findViewById(R.id.kecamatan_label);
        kelurahan_label = (TextView) findViewById(R.id.kelurahan_label);
        latitude_label = (TextView) findViewById(R.id.latitude_label);
        longitude_label = (TextView) findViewById(R.id.longitude_label);

        submitMap = (Button) findViewById(R.id.input_lokasi);
        submit = (Button) findViewById(R.id.input_button);
        calendar = (TextView) findViewById(R.id.calendar_isi);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        pengisi.setText(username);

        ////////////////////////////////////////////////
        //        TYPEFACE SECTION
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        alamat_agen.setTypeface(type);
        latitude_agen.setTypeface(type);
        longitude_agen.setTypeface(type);
        alamat_agen_label.setTypeface(type);
        provinsi_label.setTypeface(type);
        kabkot_label.setTypeface(type);
        kecamatan_label.setTypeface(type);
        kelurahan_label.setTypeface(type);
        latitude_label.setTypeface(type);
        longitude_label.setTypeface(type);
        formattedDate=dateTime.format(c.getTime());
        buildGoogleApiClient();

        provinsiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        kabkotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        kelurahanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        kecamatanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

    }

    private void submitForm() {
        startActivity(new Intent(PendaftaranCalonAgen2.this, MainActivity.class));
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }



    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            longi = mLastLocation.getLongitude();
//            Toast.makeText(this, "Lokasi : "+lat+", "+longi, Toast.LENGTH_LONG).show();
            try {
                final String url = "https://maps.googleapis.com/maps/api/staticmap?center="+ lat +","+ longi +"&zoom=18&size=600x800&sensor=false&markers=icon:http://sadewa.asia/icon_kurir.png%7C"+ lat +","+ longi+"&key=AIzaSyAZwqU0H1Fxb8-hGj6t1fKKW984Qam9UY0";
                final String encodedurl = URLEncoder.encode(url,"UTF-8");
                Log.d("STATICMAPS", encodedurl);

                AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>(){
                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        Bitmap bmp = null;
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpGet request = new HttpGet(url);

                        InputStream in = null;
                        try {
                            HttpResponse response = httpclient.execute(request);
                            in = response.getEntity().getContent();
                            bmp = BitmapFactory.decodeStream(in);
                            in.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return bmp;
                    }
                    protected void onPostExecute(Bitmap bmp) {
                        if (bmp!=null) {
                            latitude_agen.setText(lat.toString());
                            longitude_agen.setText(longi.toString());
                            iv.setImageBitmap(bmp);

                        }

                    }
                };

                setImageFromUrl.execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "No Location Detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

//        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public boolean isInternetAvailable(Context c) {

        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }
}