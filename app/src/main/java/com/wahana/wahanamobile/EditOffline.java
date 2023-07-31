package com.wahana.wahanamobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AndroidRuntimeException;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.wahana.wahanamobile.Data.OfflineTTK;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.ImageLoadingUtils;
import com.wahana.wahanamobile.utils.ImageUriDatabases;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditOffline extends DrawerHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener  {

    private Button btnClickImage;
    private ListView listView;
    public static boolean isWindowFocused = false;
    public static boolean isCameraFocused = false;
    public static boolean isAppWentToBg = false;
    public static boolean isBackPressed = false;
    Spinner statusPenerimaSpinner,alasanSpinner;
    public ArrayAdapter<CharSequence> adapter;
    private ImageUriDatabases database;
    Uri imageUri;
    ImageView imageView;
    byte[] bArray;
    private ImageLoadingUtils utils;
    private ImageListAdapter adapterImage;
    private Cursor cursor;
    private LruCache<String, Bitmap> memoryCache;
    private final int REQUEST_CODE_QRCODE = 3;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    private static final String TAG = "DeliveredAirways";

    String SOAP_ACTION, NAMESPACE, URL;
    String METHOD_NAME = "deliveredAirway";

    ProgressDialog progressDialog;

    SessionManager session;
    int request_id;
    String username, user_id,session_id;
    Button scan, gambar, tambah, submit;
    Double lat = 0.0, longi = 0.0;

    String formattedDate,encodedImage = null,encodedImage2 = null,encodedImage3 = null,encodedImage4 = null;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;

    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    TextView nama,jabatan, kode_ttk_label, penerima_label, status_penerima_label, keterangan_label, foto_diri_label;
    ImageView foto,iv;
    TextView pengisi, tgl, status, penerima, keterangan, ttk, ttkHidden, calendar, judul;
    Bitmap[] arrayOfImages;
    int imageCount;
    String noTTK;
    DatabaseHandler db;
    Button offflineButton;
    OfflineTTK offlineTTK;
    @SuppressWarnings("ResourceType")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(EditOffline.this);
        super.onCreateDrawer(this);
        Bundle extras = getIntent().getExtras();
        noTTK = extras.getString("ttk");
        offlineTTK = db.getOfflineEditTTK(noTTK);
        if (offlineTTK.getPackagestatus().equals("1")){
            setContentView(R.layout.activity_delivered_airways);
        }else if (offlineTTK.getPackagestatus().equals("2")){
            setContentView(R.layout.activity_undelivered_airways);
        }
        progressDialog = new ProgressDialog(EditOffline.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(EditOffline.this);
        username = session.getUsername();
        user_id = session.getID();
        session_id=session.getSessionID();
        iv = (ImageView) findViewById(R.id.mapView);
        offflineButton = (Button) findViewById(R.id.offline_button);
        submit = (Button) findViewById(R.id.input_button);
        offflineButton.setVisibility(View.GONE);
        submit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        ttk = (TextView) findViewById(R.id.input_ttk);
        ttkHidden = (TextView) findViewById(R.id.input_ttk_hidden);
        keterangan = (TextView) findViewById(R.id.input_keterangan);
        kode_ttk_label = (TextView) findViewById(R.id.kode_ttk_label);
        keterangan_label = (TextView) findViewById(R.id.keterangan_label);
        foto_diri_label = (TextView) findViewById(R.id.foto_diri_label);
        scan = (Button) findViewById(R.id.scan_button);
        gambar = (Button) findViewById(R.id.btn_submit_foto);
        calendar = (TextView) findViewById(R.id.calendar_isi);

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
                Intent intent = new Intent(EditOffline.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");

        if (offlineTTK.getPackagestatus().equals("1")){
            judul = (TextView)findViewById(R.id.judul_status_terkirim_isi);
            judul.setText("Edit Status TTK Terkirim");
            statusPenerimaSpinner = (Spinner) findViewById(R.id.status_penerima_spinner);
            adapter = ArrayAdapter.createFromResource(this, R.array.status_penerima, R.layout.simple_spinner_list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusPenerimaSpinner.setAdapter(adapter);
            penerima = (TextView) findViewById(R.id.input_penerima);
            penerima_label = (TextView) findViewById(R.id.penerima_label);
            status_penerima_label = (TextView) findViewById(R.id.status_penerima_label);
            penerima.setTypeface(type);
            penerima_label.setTypeface(type);
            status_penerima_label.setTypeface(type);
            statusPenerimaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
            int pos = 0;
            if (offlineTTK.getReceivertype().equals("Penerima Langsung")){
                pos = 1;
            }else if (offlineTTK.getReceivertype().equals("Diwakilkan")){
                pos =2;
            }
            penerima.setText(offlineTTK.getReceivername());
            statusPenerimaSpinner.setSelection(pos-1);
        }else if (offlineTTK.getPackagestatus().equals("2")){
            judul = (TextView)findViewById(R.id.judul_status_belum_terkirim_isi);
            judul.setText("Edit Status TTK Tidak Terkirim");
            alasanSpinner = (Spinner) findViewById(R.id.alasan_spinner);
            adapter = ArrayAdapter.createFromResource(this,R.array.alasan_belum_terkirim, R.layout.simple_spinner_list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            alasanSpinner.setAdapter(adapter);
            alasanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {}
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {// your code here
                }
            });
            int pos = 0;
            if (offlineTTK.getReason().equals("Pindah Alamat")){
                pos = 1;
            }else if (offlineTTK.getReason().equals("Rumah Kosong")){
                pos = 2;
            }else if (offlineTTK.getReason().equals("Salah Alamat")){
                pos = 3;
            }else if (offlineTTK.getReason().equals("Salah Tempel TTK")){
                pos = 4;
            }else if (offlineTTK.getReason().equals("Salah Wilayah")){
                pos = 5;
            }else if (offlineTTK.getReason().equals("Tidak Cukup Waktu")){
                pos = 6;
            }
            alasanSpinner.setSelection(pos-1);
        }

        ttk.setText(noTTK);
        ttkHidden.setText(noTTK);
        keterangan.setText(offlineTTK.getComment());

        database = new ImageUriDatabases(this);
        database.destroy();
        //database.create();
        utils = new ImageLoadingUtils(this);

        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        //btnFromGallery = (Button) findViewById(R.id.btn_tambah_foto);
        listView = (ListView) findViewById(R.id.listView);
        cursor = db.getallUri(noTTK);
        imageView = (ImageView) findViewById(R.id.imageView);
        adapterImage = new ImageListAdapter(this, cursor, true);
        listView.setAdapter(adapterImage);
        btnClickImage.setOnClickListener(this);

        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        int cachesize = 60*1024*1024;

        memoryCache	 = new LruCache<String, Bitmap>(cachesize){
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if(android.os.Build.VERSION.SDK_INT>=12){
                    return value.getByteCount();
                }
                else{
                    return value.getRowBytes()*value.getHeight();
                }
            }
        };

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
                            Intent pindah = new Intent(EditOffline.this, MainActivity.class);
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
                            EditOffline.this.startActivity(intent);
                            finish();
                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent pindah = new Intent(EditOffline.this, MainActivity.class);
                            startActivity(pindah);
                            finish();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        pengisi.setText(username);

        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        ttk.setTypeface(type);
        keterangan.setTypeface(type);
        kode_ttk_label.setTypeface(type);
        keterangan_label.setTypeface(type);
        foto_diri_label.setTypeface(type);
        formattedDate=dateTime.format(c.getTime());
        buildGoogleApiClient();

        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchActivity(BarcodeScannerActivity.class);
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
        if (!validate()) {
            return;
        }

        View parentView = null;
        for (int i = 0; i < listView.getCount(); i++) {
            parentView = getViewByPosition(i, listView);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BitmapDrawable drawable = (BitmapDrawable) ((ImageView) parentView.findViewById(R.id.imgView)).getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            byte[] raw = out.toByteArray();
            if (i == 0){
                encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
            }else if (i==1){
                encodedImage2 = Base64.encodeToString(raw, Base64.DEFAULT);
            }else if (i==2){
                encodedImage3 = Base64.encodeToString(raw, Base64.DEFAULT);
            }else if (i==3){
                encodedImage4 = Base64.encodeToString(raw, Base64.DEFAULT);
            }
            Log.d("gambar", ""+encodedImage+"  "+encodedImage2+"  "+encodedImage3+"  "+encodedImage4);
        }

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("setPackageDeliveryStatus");
        parameter.add(offlineTTK.getSessionid());
        parameter.add(offlineTTK.getRequestid());
        parameter.add(offlineTTK.getEmployeecode());
        parameter.add(ttk.getText().toString());
        parameter.add(offlineTTK.getStatusdate());
        parameter.add(offlineTTK.getPackagestatus());
        if (offlineTTK.getPackagestatus().equals("1")){
            parameter.add("");
            parameter.add(penerima.getText().toString());
            parameter.add(statusPenerimaSpinner.getSelectedItem().toString());
            parameter.add(keterangan.getText().toString());
        }else if (offlineTTK.getPackagestatus().equals("2")){
            parameter.add(alasanSpinner.getSelectedItem().toString());
            parameter.add("");
            parameter.add("");
            parameter.add(keterangan.getText().toString());
        }
        parameter.add(encodedImage);
        parameter.add(encodedImage2);
        parameter.add(encodedImage3);
        parameter.add(encodedImage4);
        parameter.add(offlineTTK.getLongitude());
        parameter.add(offlineTTK.getLatitude());

        new SoapClient(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d("adding data", "add");
                Log.i(TAG, "onPreExecute");
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Adding Data...");
                progressDialog.show();
            }
            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("Hasil", result+"");
                final String response = result.getProperty(0).toString();
                if (result == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(EditOffline.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            if (noTTK.equals(ttk.getText().toString())){
                                db.updateOfflineMessage(noTTK, text, "Terkirim");
                            }else {
                                if (offlineTTK.getPackagestatus().equals("1")){
                                    List<String> uris = db.getAllTTKURI(noTTK);
                                    for (int i = 0; i < uris.size(); i++){
                                        Log.d("hasil gambar", uris.get(i));
                                        db.insertTTKUri(uris.get(i), "1", ttk.getText().toString());
                                    }
                                    db.addOfflineTTK(new OfflineTTK(ttk.getText().toString(), session_id, Integer.toString(request_id), user_id, formattedDate, "1", "",penerima.getText().toString(),
                                            statusPenerimaSpinner.getSelectedItem().toString(), keterangan.getText().toString(), lat.toString(), longi.toString(),
                                            text, "Terkirim"));
                                    db.deleteTTK(noTTK);
                                }else if (offlineTTK.getPackagestatus().equals("2")){
                                    List<String> uris = db.getAllTTKURI(noTTK);
                                    for (int i = 0; i < uris.size(); i++){
                                        Log.d("hasil gambar", uris.get(i));
                                        db.insertTTKUri(uris.get(i), "2", ttk.getText().toString());
                                    }
                                    db.addOfflineTTK(new OfflineTTK(ttk.getText().toString(), session_id, Integer.toString(request_id), user_id, formattedDate, "2",
                                            alasanSpinner.getSelectedItem().toString(), "", "", keterangan.getText().toString(), lat.toString(), longi.toString(),
                                            text, "Terkirim"));
                                    db.deleteTTK(noTTK);
                                }
                            }
                            Toast.makeText(EditOffline.this, getString(R.string.update_airway_message), Toast.LENGTH_LONG).show();
                            Intent pindah = new Intent(EditOffline.this, OfflineDeliveryNote.class);
                            startActivity(pindah);
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(EditOffline.this, text, Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                            if (noTTK.equals(ttk.getText().toString())){
                                db.updateOfflineMessage(noTTK, text, "Gagal");
                            }else {
                                if (offlineTTK.getPackagestatus().equals("1")){
                                    List<String> uris = db.getAllTTKURI(noTTK);
                                    for (int i = 0; i < uris.size(); i++){
                                        Log.d("hasil gambar", uris.get(i));
                                        db.insertTTKUri(uris.get(i), "1", ttk.getText().toString());
                                    }
                                    db.addOfflineTTK(new OfflineTTK(ttk.getText().toString(), session_id, Integer.toString(request_id), user_id, formattedDate, "1", "",penerima.getText().toString(),
                                            statusPenerimaSpinner.getSelectedItem().toString(), keterangan.getText().toString(), lat.toString(), longi.toString(),
                                            text, "Gagal"));
                                    db.deleteTTK(noTTK);
                                }else if (offlineTTK.getPackagestatus().equals("2")){
                                    List<String> uris = db.getAllTTKURI(noTTK);
                                    for (int i = 0; i < uris.size(); i++){
                                        Log.d("hasil gambar", uris.get(i));
                                        db.insertTTKUri(uris.get(i), "2", ttk.getText().toString());
                                    }
                                    db.addOfflineTTK(new OfflineTTK(ttk.getText().toString(), session_id, Integer.toString(request_id), user_id, formattedDate, "2",
                                            alasanSpinner.getSelectedItem().toString(), "", "", keterangan.getText().toString(), lat.toString(), longi.toString(),
                                            text, "Gagal"));
                                    db.deleteTTK(noTTK);
                                }
                            }
                            Intent pindah = new Intent(EditOffline.this, OfflineDeliveryNote.class);
                            startActivity(pindah);
                            finish();
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(EditOffline.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                        if (noTTK.equals(ttk.getText().toString())){
                            db.updateOfflineMessage(noTTK, "System Error", "Gagal");
                        }else {
                            if (offlineTTK.getPackagestatus().equals("1")){
                                List<String> uris = db.getAllTTKURI(noTTK);
                                for (int i = 0; i < uris.size(); i++){
                                    Log.d("hasil gambar", uris.get(i));
                                    db.insertTTKUri(uris.get(i), "1", ttk.getText().toString());
                                }
                                db.addOfflineTTK(new OfflineTTK(ttk.getText().toString(), session_id, Integer.toString(request_id), user_id, formattedDate, "1", "",penerima.getText().toString(),
                                        statusPenerimaSpinner.getSelectedItem().toString(), keterangan.getText().toString(), lat.toString(), longi.toString(),
                                        "System Error", "Gagal"));
                                db.deleteTTK(noTTK);
                            }else if (offlineTTK.getPackagestatus().equals("2")){
                                List<String> uris = db.getAllTTKURI(noTTK);
                                for (int i = 0; i < uris.size(); i++){
                                    Log.d("hasil gambar", uris.get(i));
                                    db.insertTTKUri(uris.get(i), "2", ttk.getText().toString());
                                }
                                db.addOfflineTTK(new OfflineTTK(ttk.getText().toString(), session_id, Integer.toString(request_id), user_id, formattedDate, "2",
                                        alasanSpinner.getSelectedItem().toString(), "", "", keterangan.getText().toString(), lat.toString(), longi.toString(),
                                        "System Error", "Gagal"));
                                db.deleteTTK(noTTK);
                            }
                        }
                    }
                }
            }
        }.execute(parameter);

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_submit_foto:
            {
                try {
                    isCameraFocused = true;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //define the file-name to save photo taken by Camera activity
                    String fileName = System.currentTimeMillis()+".jpg";
                    //create parameters for Intent with filename
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION,"Image captured by camera");
                    //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)

                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
                }catch(AndroidRuntimeException e){
                    Log.d("error",e.getMessage());
                    Integer res;
                    res=0;
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, res);
                    Log.d("error res",res.toString());

                }catch(Exception e){
                    Log.d("error",e.getMessage());
                    Integer res;
                    res=0;
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, res);
                    Log.d("error res",res.toString());

                }
            }
            break;
        }

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
    protected void onRestart() {
        isCameraFocused = false;
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onBackPressed() {

        if (this instanceof EditOffline) {

        } else {
            isBackPressed = true;
        }

        Log.d(TAG,
                "onBackPressed " + isBackPressed + ""
                        + this.getLocalClassName());
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        isWindowFocused = hasFocus;

        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }

        super.onWindowFocusChanged(hasFocus);
    }
    @Override
    protected void onStop() {
        applicationdidenterbackground();
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    public void applicationdidenterbackground() {
        if (!isWindowFocused && !isCameraFocused) {
            memoryCache.evictAll();
            database.destroy();
            isAppWentToBg = true;
//            Toast.makeText(getApplicationContext(),
//                    "App is Going to Background", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        memoryCache.evictAll();
        database.destroy();
        super.onDestroy();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = null;
        String filePath = null;

        if(resultCode == RESULT_OK){

            switch(requestCode){
//                case REQUEST_CODE_FROM_GALLERY:
//                    new ImageCompressionAsyncTask(true).execute(data.getDataString());
//
//                    break;
                case REQUEST_CODE_CLICK_IMAGE:
                    selectedImageUri = imageUri;
                    break;

                case REQUEST_CODE_QRCODE:
                    String nilai=data.getStringExtra("ttk");
                    ttk.setText(nilai);
                    break;
            }
        }
        if(selectedImageUri != null){
            try {
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePath = filemanagerstring;
                } else {
                    Toast.makeText(getApplicationContext(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

                if (filePath != null) {
                    filePath = compressImage(filePath);
                    db.insertTTKUri(filePath,offlineTTK.getPackagestatus(),noTTK);
                    cursor = db.getallUri(noTTK);
                    adapterImage.changeCursor(cursor);

                } else {
                    //bitmap = null;
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), ""+e,
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public String compressImage(String imageUri) {

        String ImageUri = imageUri;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(ImageUri,options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16*1024];

        try{
            bmp = BitmapFactory.decodeFile(ImageUri,options);
        }
        catch(OutOfMemoryError exception){
            exception.printStackTrace();

        }
        try{
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);


        }
        catch(OutOfMemoryError exception){
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float)options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


        ExifInterface exif;
        try {
            exif = new ExifInterface(ImageUri);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            Log.d("lala",""+bArray);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/TerkirimImages");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"+ System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZBAR_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


    class ImageListAdapter extends CursorAdapter {
        private ImageLoadingUtils utils;

        public ImageListAdapter(Context context, Cursor cursor, boolean autoRequery) {
            super(context, cursor, autoRequery);
            utils = new ImageLoadingUtils(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            loadBitmap(cursor.getString(cursor.getColumnIndex(ImageUriDatabases.PATH_NAME)), holder.imageView, context);
            final Object obj = cursor.getString(cursor.getColumnIndex(ImageUriDatabases.ENTITY_ID));
            holder.imageView.setTag(obj);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete image "+obj+"?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(mContext,"berhasil dihapus", Toast.LENGTH_LONG).show();
                                        database.deleteUri(obj.toString());
                                        holder.imageView.setVisibility(View.GONE);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(mContext,"gagal dihapus", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                }
            });


        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_item_layout, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.imgView);
            view.setTag(holder);
            return view;
        }

        public void loadBitmap(String filePath, ImageView imageView, Context context) {
            if (cancelPotentialWork(filePath, imageView)) {
                final Bitmap bitmap = getBitmapFromMemCache(filePath);
                if(bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }
                else{
                    final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                    final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), utils.icon, task);
                    imageView.setImageDrawable(asyncDrawable);
                    task.execute(filePath);
                }
            }
        }

        class ViewHolder{
            ImageView imageView;
        }

        class AsyncDrawable extends BitmapDrawable {

            private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

            public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
                super(res, bitmap);
                bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
            }

            public BitmapWorkerTask getBitmapWorkerTask() {
                return bitmapWorkerTaskReference.get();
            }
        }

        public boolean cancelPotentialWork(String filePath, ImageView imageView) {

            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (bitmapWorkerTask != null) {
                final String bitmapFilePath = bitmapWorkerTask.filePath;
                if (bitmapFilePath != null && !bitmapFilePath.equalsIgnoreCase(filePath)) {
                    bitmapWorkerTask.cancel(true);
                } else {
                    return false;
                }
            }
            return true;
        }

        private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
            if (imageView != null) {
                final Drawable drawable = imageView.getDrawable();
                if (drawable instanceof AsyncDrawable) {
                    final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                    return asyncDrawable.getBitmapWorkerTask();
                }
            }
            return null;
        }

        class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>{
            private final WeakReference<ImageView> imageViewReference;
            public String filePath;

            public BitmapWorkerTask(ImageView imageView){
                imageViewReference = new WeakReference<ImageView>(imageView);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                filePath = params[0];
                Bitmap bitmap = utils.decodeBitmapFromPath(filePath);
                if(bitmap!=null) {
                    addBitmapToMemoryCache(filePath, bitmap);
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (isCancelled()) {
                    bitmap = null;
                }
                if(imageViewReference != null && bitmap != null){
                    final ImageView imageView = imageViewReference.get();
                    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                    if (this == bitmapWorkerTask && imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }

    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public boolean validate() {
        boolean valid = true;

        String email = ttk.getText().toString();
        String password = keterangan.getText().toString();

        // if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        if(email.isEmpty())
        {
            ttk.setError("masukkan username anda");
            valid = false;
        } else {
            ttk.setError(null);
        }

        if (password.isEmpty()) {
            keterangan.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            keterangan.setError(null);
        }

        if (lat == 0.0 || longi == 0.0){
            Toast.makeText(EditOffline.this, "Lokasi Belum Terdeteksi, Mohon Tunggu Beberapa Saat", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}
