package com.wahana.wahanamobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Base64;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.camerates.BitmapTools;
import com.wahana.wahanamobile.camerates.Camerates;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.ImageLoadingUtils;
import com.wahana.wahanamobile.utils.ImageUriDatabases;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


import fr.ganfra.materialspinner.MaterialSpinner;

public class Absensi extends DrawerHelper implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private static final String TAG = "Absensi";
    String latEiffelTower;
    String lngEiffelTower;
    ProgressDialog progressDialog;

    private Button btnClickImage, btnFromGallery, btnCancel, btnSend, btnInput;
    private static final String[] ITEMS = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6"};
    Spinner spinnerAbsensi;
    private ListView listView;

    private ImageView imgView;
    private ImageUriDatabases database;
    String tipe = "absen";
    Handler mHandler;
//    Uri imageUri;
    public ArrayAdapter<CharSequence> adapter;
    byte[] bArray = null;

    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    SessionManager session;
    String username, user_id;

    private ImageLoadingUtils utils;
    private ImageListAdapter adapterImage;

    private Cursor cursor;
    private LruCache<String, Bitmap> memoryCache;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;

    Double lat = 0.0, longi = 0.0;

    protected GoogleApiClient mGoogleApiClient;
    protected static Location mLastLocation;


    TextView pengisi, tgl, calendar, tipeAbsen, fotoDiri, nama, jabatan;

    TableLayout tabelFoto;
    Button btnSubmitFoto;
    ImageView imageView, foto, iv;
    DatabaseHandler db;
    String attendanceType;
    private LocationRequest mLocationRequest;
    int fakegps = 0;
    Uri selectedImageUri = null;
    Calendar c;
    LocationManager lm;

    @SuppressWarnings("ResourceType")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);
        super.onCreateDrawer(this);
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
                Intent intent = new Intent(Absensi.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        progressDialog = new ProgressDialog(Absensi.this, R.style.AppTheme_Dark_Dialog);
        db = new DatabaseHandler(Absensi.this);

        spinnerAbsensi = (Spinner) findViewById(R.id.spinner_absensi);
        this.setTitle("");

        session = new SessionManager(Absensi.this);
        if (session.isLogin()) {

        } else {
            Intent inten = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(inten);
            this.finish();
        }

        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

//        if (isMockLocationOn(Absensi.this)){
//            fakegps=1;
////            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
////            alertDialog.setCancelable(false);
////            alertDialog.setTitle("Lokasi palsu");
////            alertDialog.setMessage("Lokasi palsu anda aktif. Mohon nonaktifkan lokasi palsu");
////            alertDialog.setPositiveButton("Settings",
////                    new DialogInterface.OnClickListener() {
////                        public void onClick(DialogInterface dialog, int which) {
////                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
////                            Absensi.this.startActivity(intent);
////                            finish();
////                        }
////                    });
////
////            alertDialog.setNegativeButton("Cancel",
////                    new DialogInterface.OnClickListener() {
////                        public void onClick(DialogInterface dialog, int which) {
////                            finish();
////                        }
////                    });
////            alertDialog.show();
//        }

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");
            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            // On pressing Settings button
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            Absensi.this.startActivity(intent);
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
        buildGoogleApiClient();


        adapter = ArrayAdapter.createFromResource(this, R.array.absensi, R.layout.simple_spinner_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAbsensi.setAdapter(adapter);
        database = new ImageUriDatabases(this);
        database.destroy();
        utils = new ImageLoadingUtils(this);
        int cachesize = 60 * 1024 * 1024;

        memoryCache = new LruCache<String, Bitmap>(cachesize) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (Build.VERSION.SDK_INT >= 12) {
                    return value.getByteCount();
                } else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };


        tabelFoto = (TableLayout) findViewById(R.id.tabel_foto);
        tipeAbsen = (TextView) findViewById(R.id.tipe_absen_label);
        fotoDiri = (TextView) findViewById(R.id.foto_diri_label);

        imageView = (ImageView) findViewById(R.id.imageView);
        iv = (ImageView) findViewById(R.id.mapView);

        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        btnInput = (Button) findViewById(R.id.input_button);
        // btnFromGallery = (Button) findViewById(R.id.btn_tambah_foto);
        listView = (ListView) findViewById(R.id.listView);
        //database.create();

        // cursor = database.getallUri();
        adapterImage = new ImageListAdapter(this, cursor, true);
        listView.setAdapter(adapterImage);


        btnClickImage.setOnClickListener(this);

        spinnerAbsensi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 1) {
                    tabelFoto.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    btnClickImage.setVisibility(View.VISIBLE);
                } else {
                    tabelFoto.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    btnClickImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        username = session.getUsername();
        user_id = session.getID();

        //btnInput = (Button) findViewById(R.id.input_button);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);


        c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);

        pengisi.setText(username);


        Typeface type = Typeface.createFromAsset(getAssets(), "font/OpenSansLight.ttf");
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        fotoDiri.setTypeface(type);
        tipeAbsen.setTypeface(type);
    }

    private void submitForm() {
        if (!validate()) {
            return;
        }

        String datenow,datefoto;

        if (attendanceType.equals("1")) {
            File file = new File(selectedImageUri.toString());
            Date lastModDate = new Date(file.lastModified());
            SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
            datenow = cf.format(c.getTime());
            datefoto = cf.format(lastModDate);
        }else {
            File file = new File(selectedImageUri.toString());
            Date lastModDate = new Date(file.lastModified());
            SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
            datenow = cf.format(c.getTime());
            datefoto = cf.format(lastModDate);
        }

        Random rand;
        rand = new Random();
        int request_id=rand.nextInt(10000);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z");
        String formattedDate = df.format(c.getTime());

        if (!datefoto.equals(datenow) || fakegps==1){
            JSONObject json = new JSONObject();
            try {
                json.put("service", "setAbsenPalsu");
                json.put("action", "absen");
                json.put("fakegps", fakegps);
                json.put("employeeCode", user_id);
                json.put("latitude", lat);
                json.put("longitude", longi);
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
                                Toast.makeText(Absensi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                                        if (fakegps==1){
                                            mLastLocation.reset();
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Absensi.this);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setTitle("Absen Gagal");
                                            alertDialog.setMessage("Anda menggunakan lokasi palsu. Mohon nonaktifkan lokasi palsu");
                                            alertDialog.setPositiveButton("Settings",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                                                            Absensi.this.startActivity(intent);
                                                            finish();
                                                        }
                                                    });

                                            alertDialog.setNegativeButton("Cancel",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    });
                                            alertDialog.show();
                                        }else{
                                            Intent pindah = new Intent(Absensi.this, HasilError.class);
                                            pindah.putExtra("proses","absenPalsu");
                                            JSONArray msd = data.getJSONArray(1);
                                            String ma = msd.getString(0);
                                            pindah.putExtra("no",ma);
                                            startActivity(pindah);
                                            finish();
                                        }
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(Absensi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(Absensi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                progressDialog.dismiss();
                                Intent pindah = new Intent(Absensi.this, HasilError.class);
                                pindah.putExtra("proses","absenPalsu");
                                pindah.putExtra("no", text);
                                startActivity(pindah);
                            }
                        }catch (Exception e){
                            Log.d("hasil error", ""+e);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Absensi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                }

            }.execute(parameter);
        }else{
            ArrayList<String> parameter = new ArrayList<String>();
            //convert image to base64
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String encodedImage = null;
            try{
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                byte[] raw = out.toByteArray();
                encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
            }catch (Exception e){
//                Toast.makeText(Absensi.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            }
            parameter.add("attendanceSet");
            parameter.add(session.getSessionID());
            parameter.add(Integer.toString(request_id));
//            parameter.add(session.getUsername());
            parameter.add(session.getUsername()+"@"+getString(R.string.versinew));
            parameter.add(attendanceType);
            parameter.add(formattedDate);
            parameter.add(String.valueOf(encodedImage));
            parameter.add(String.valueOf(longi));
            parameter.add(String.valueOf(lat));
            progressDialog.setCancelable(false);
            new SoapClient(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Log.d("submit data", "add");
                    Log.i(TAG, "onPreExecute");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Submit Data...");
                    progressDialog.show();
                }
                @Override
                protected void onPostExecute(SoapObject result) {
                    super.onPostExecute(result);
                    Log.d("hasil soap", "" + result);
                    if(result==null){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Absensi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }else {
                        try {
                            progressDialog.dismiss();
                            final String text = result.getProperty(1).toString();
                            if (text.equals("OK")) {
                                db.deleteOfflineTTK();
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat cf = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedCal = cf.format(c.getTime());

                                String statusabsen;
                                if(attendanceType.equals("1")){
                                    statusabsen="Datang";
                                }else{
                                    statusabsen="Pulang";
                                }

//                            Toast.makeText(Absensi.this, "Data berhasil ditambahkan",Toast.LENGTH_LONG).show();
                                db.updateLastAbsen(session.getUsername(), formattedCal);
                                Intent pindah = new Intent(Absensi.this, HasilActivity.class);
                                pindah.putExtra("proses","absen");
                                pindah.putExtra("no", "Data absensi "+statusabsen+" tanggal "+formattedCal+" berhasil ditambahkan.");
                                startActivity(pindah);
                                finish();
                            }else{
                                Intent pindah = new Intent(Absensi.this, HasilError.class);
                                pindah.putExtra("proses","absen");
                                pindah.putExtra("no", ""+text);
                                startActivity(pindah);
                            }
                        }catch (Exception e){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Absensi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                }
            }.execute(parameter);
        }
    }


//    private void initSpinnerIsi() {
//        spinnerIsi = (MaterialSpinner) findViewById(R.id.spinner_absensi);
//        spinnerIsi.setAdapter(adapter);
//        spinnerIsi.setHint("");
//    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_submit_foto:
            {
                //define the file-name to save photo taken by Camera activity
                String fileName = System.currentTimeMillis()+".jpg";
                //create parameters for Intent with filename
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION,"Image captured by camera");
                //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
//                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                Intent intent = new Intent(Absensi.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
            }
            break;
            case R.id.button_manifest:
            {
                if(spinnerAbsensi.getSelectedItem().toString().equals("Datang")){
                    attendanceType="1";
                }else{
                    attendanceType="2";
                }
                submitForm();
            }
            break;
//            case R.id.btn_tambah_foto :
//            {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
//            }
//            break;
        }

    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout);
        imgView = (ImageView) dialog.findViewById(R.id.dlgImageView);
        btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnSend = (Button) dialog.findViewById(R.id.btnSend);
        return dialog;
    }

    @Override
    @Deprecated
    protected void onPrepareDialog(int id, final Dialog dialog, Bundle bundle) {
        switch (id){
            case 1:
                if(bundle != null){
                    final String filePath = bundle.getString("FILE_PATH");
                    imgView.setImageBitmap(utils.decodeBitmapFromPath(filePath));

                    btnCancel.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    btnSend.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            database.insertUri(filePath,tipe);
                            cursor = database.getallUri();
                            adapterImage.changeCursor(cursor);
                            dialog.dismiss();
                        }
                    });
                }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = null;
        Log.d("data :",""+data);
        if(resultCode == RESULT_OK){
            switch(requestCode){
//                case REQUEST_CODE_FROM_GALLERY:
//                    new ImageCompressionAsyncTask(true).execute(data.getDataString());
//
//                    break;
                case REQUEST_CODE_CLICK_IMAGE:
//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    imageView.setImageBitmap(photo);
//                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//                    Uri tempUri = getImageUri(getApplicationContext(), photo);
//
//                    // CALL THIS METHOD TO GET THE ACTUAL PATH
//                    File finalFile = new File(getRealPathFromURI(tempUri));
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
//                    byte[] byteArray = data.getByteArrayExtra("foto");
//                    Uri myUri = Uri.parse(data.getStringExtra("foto"));
////                    selectedImageUri = myUri;
//                    Bitmap bitmap2 = BitmapTools.toBitmap(byteArray);
//                    bitmap2 = BitmapTools.rotate(bitmap2, data.getIntExtra("rotation", 0), data.getIntExtra("camera", 0));
////                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                    imageView.setImageBitmap(bitmap2);

//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    String encodedImage = null;
//                        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//                        Bitmap bitmap = drawable.getBitmap();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
//                        byte[] raw = out.toByteArray();
//                        encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
//                    Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//                    Log.e("hasil Original", bitmap2.getWidth()+" "+bitmap2.getHeight());
//                    Log.e("hasil Compressed", decoded.getWidth()+" "+decoded.getHeight());
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
                    database.insertUri(filePath, tipe);
                    cursor = database.getallUri();
                    adapterImage.changeCursor(cursor);

                } else {
                    //bitmap = null;
                }

            } catch (Exception e) {

               // Toast.makeText(getApplicationContext(), "Internal error",
                 //       Toast.LENGTH_LONG).show();
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

//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(photo);
//            Uri tempUri = getImageUri(getApplicationContext(), photo);

//        String filePath = getRealPathFromURI(imageUri);
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
            Log.d("lala","aaa");
            e.printStackTrace();
        }
//        FileOutputStream out = null;
//        String filename = getFilename();
//        try {
//            out = new FileOutputStream(filename);
//            //ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//            //  bArray = bos.toByteArray();
//            Log.d("lala",""+bArray);
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        imageView.setImageBitmap(scaledBitmap);
        return imageUri;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "WahanaFolder/AbsenImages");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"+ System.currentTimeMillis() + ".jpg");
        return uriSting;

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
    public void onLocationChanged(Location location) {

    }

    class ImageListAdapter extends CursorAdapter {
        private ImageLoadingUtils utils;

        public ImageListAdapter(Context context, Cursor cursor, boolean autoRequery) {
            super(context, cursor, autoRequery);
            utils = new ImageLoadingUtils(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            //loadBitmap(cursor.getString(cursor.getColumnIndex(ImageUriDatabases.PATH_NAME)), holder.imageView, context);

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
                addBitmapToMemoryCache(filePath, bitmap);
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

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
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
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
            if (isMockLocationOn(Absensi.this, mLastLocation)){
                fakegps=1;
            }
            lat = mLastLocation.getLatitude();
            longi = mLastLocation.getLongitude();
            String icon = "";
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
            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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

    public boolean validate() {
        boolean valid = true;

        if (lat == 0.0 || longi == 0.0){
            Toast.makeText(Absensi.this, "Lokasi Belum Terdeteksi, Mohon Tunggu Beberapa Saat", Toast.LENGTH_SHORT).show();
            valid = false;
        }

//        if(attendanceType.equals("1")){
            if (imageView.getDrawable() == null){
                Toast.makeText(Absensi.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
                valid = false;
            }
//        }

        return valid;
    }

    public static boolean isMockLocationOn(Context context, Location mLastLocation) {

        if (android.os.Build.VERSION.SDK_INT >= 18){
            return mLastLocation.isFromMockProvider();
        }else{
            if (Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
                return false;
            else
                return true;
        }
    }
}
