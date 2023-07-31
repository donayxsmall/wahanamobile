package com.wahana.wahanamobile;

import android.Manifest;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.wahana.wahanamobile.Data.OfflineTTK;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;

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
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.ImageLoadingUtils;
import com.wahana.wahanamobile.utils.ImageUriDatabases;
import com.wahana.wahanamobile.webserviceClient.SoapClient;

import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.serialization.SoapObject;

import static com.wahana.wahanamobile.Absensi.isMockLocationOn;


public class DeliveredAirways extends DrawerHelper implements ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener, LocationListener {
    private Button btnClickImage, btnFromGallery, btnCancel, btnSend, btnInput, offflineButton;
    private ListView listView;
    public static boolean isWindowFocused = false;
    public static boolean isCameraFocused = false;
    public static boolean isAppWentToBg = false;
    public static boolean isBackPressed = false;
    Spinner statusPenerimaSpinner;
    public ArrayAdapter<CharSequence> adapter;
    //private ImageView imageView;
    private ImageView imgView;
    private ImageUriDatabases database;
    Uri imageUri;
    ImageView imageView;
    byte[] bArray;
    String tipe = "ttk terkirim";
    private ImageLoadingUtils utils;
    private ImageListAdapter adapterImage;
    private Cursor cursor;
    private LruCache<String, Bitmap> memoryCache;
    private final int REQUEST_CODE_QRCODE = 3;
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    private static final String TAG = "DeliveredAirways";

    String SOAP_ACTION, NAMESPACE, URL;
    String METHOD_NAME = "deliveredAirway";

    ProgressDialog progressDialog;

    SessionManager session;
    int request_id;
    String username, user_id, session_id;
    TextView pengisi, tgl, status, penerima, keterangan, ttk, calendar;
    Button scan, gambar, tambah, submit;
    Double lat = 0.0, longi = 0.0;
    Float Latitude, Longitude;

    String formattedDate, encodedImage = null, encodedImage2 = null, encodedImage3 = null, encodedImage4 = null;
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
    TextView nama, jabatan, kode_ttk_label, penerima_label, status_penerima_label, keterangan_label, foto_diri_label;
    ImageView foto, iv;
    Bitmap[] arrayOfImages;
    int imageCount;
    DatabaseHandler db;
    String nomorTTK;
    private LocationRequest mLocationRequest;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered_airways);
        super.onCreateDrawer(this);
        db = new DatabaseHandler(DeliveredAirways.this);
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
                Intent intent = new Intent(DeliveredAirways.this, MainActivity.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        statusPenerimaSpinner = (Spinner) findViewById(R.id.status_penerima_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.status_penerima, R.layout.simple_spinner_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusPenerimaSpinner.setAdapter(adapter);
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        int cachesize = 60 * 1024 * 1024;

        memoryCache = new LruCache<String, Bitmap>(cachesize) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (android.os.Build.VERSION.SDK_INT >= 12) {
                    return value.getByteCount();
                } else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };

//        if (isMockLocationOn(DeliveredAirways.this)){
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//            alertDialog.setCancelable(false);
//            alertDialog.setTitle("Lokasi palsu");
//            alertDialog.setMessage("Lokasi palsu anda aktif. Mohon nonaktifkan lokasi palsu");
//            alertDialog.setPositiveButton("Settings",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
//                            DeliveredAirways.this.startActivity(intent);
//                            finish();
//                        }
//                    });
//
//            alertDialog.setNegativeButton("Cancel",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });
//            alertDialog.show();
//        }

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
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
                            Intent pindah = new Intent(DeliveredAirways.this, MainActivity.class);
                            startActivity(pindah);
                            finish();
                        }
                    });
        }

        if (!gps_enabled && !network_enabled) {
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
                            DeliveredAirways.this.startActivity(intent);
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

        progressDialog = new ProgressDialog(DeliveredAirways.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(DeliveredAirways.this);
        username = session.getUsername();
        user_id = session.getID();
        session_id = session.getSessionID();
        Random rand;
        rand = new Random();
        request_id = rand.nextInt(10000);

        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        ttk = (TextView) findViewById(R.id.input_ttk);
        penerima = (TextView) findViewById(R.id.input_penerima);
        keterangan = (TextView) findViewById(R.id.input_keterangan);
        kode_ttk_label = (TextView) findViewById(R.id.kode_ttk_label);
        penerima_label = (TextView) findViewById(R.id.penerima_label);
        status_penerima_label = (TextView) findViewById(R.id.status_penerima_label);
        keterangan_label = (TextView) findViewById(R.id.keterangan_label);
        foto_diri_label = (TextView) findViewById(R.id.foto_diri_label);
        scan = (Button) findViewById(R.id.scan_button);
        gambar = (Button) findViewById(R.id.btn_submit_foto);
        submit = (Button) findViewById(R.id.input_button);
        calendar = (TextView) findViewById(R.id.calendar_isi);
        offflineButton = (Button) findViewById(R.id.offline_button);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        pengisi.setText(username);

        Typeface type = Typeface.createFromAsset(getAssets(), "font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        ttk.setTypeface(type);
        penerima.setTypeface(type);
        keterangan.setTypeface(type);
        kode_ttk_label.setTypeface(type);
        penerima_label.setTypeface(type);
        status_penerima_label.setTypeface(type);
        keterangan_label.setTypeface(type);
        foto_diri_label.setTypeface(type);
        formattedDate = dateTime.format(c.getTime());
        buildGoogleApiClient();

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(BarcodeScannerActivity.class);
            }
        });

        database = new ImageUriDatabases(this);
        utils = new ImageLoadingUtils(this);
        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        listView = (ListView) findViewById(R.id.listView);
        iv = (ImageView) findViewById(R.id.mapView);
        imageView = (ImageView) findViewById(R.id.imageView);
        adapterImage = new ImageListAdapter(this, cursor, true);
        listView.setAdapter(adapterImage);
        btnClickImage.setOnClickListener(this);

        statusPenerimaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                AlertDialog.Builder adb = new AlertDialog.Builder(DeliveredAirways.this);
                adb.setTitle("Update Status TTK");
                adb.setMessage("Apakah anda yakin ? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submitForm();
                    }
                });
                adb.show();
            }
        });

        offflineButton.setVisibility(View.GONE);
        submit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

//        offflineButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeliveredAirways.this);
//                alertDialog.setCancelable(false);
//                // Setting Dialog Title
//                alertDialog.setTitle("Update Status TTK");
//                // Setting Dialog Message
//                alertDialog.setMessage("Apakah Anda Yakin ?");
//                // On pressing Settings button
//                alertDialog.setPositiveButton("Ya",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (!validate()) {
//                                    return;
//                                }
//                                OfflineTTK off = db.checkTTK(ttk.getText().toString());
//                                String check = off.getTTK();
//                                Log.d("hasil", check+"<<");
//                                if (check != null){
//                                    Toast.makeText(DeliveredAirways.this, "TTK Sudah Terdapat Pada TTK Offline", Toast.LENGTH_LONG).show();
//                                }else {
//                                    SubmitOffline();
//                                }
//                            }
//                        });
//                // on pressing cancel button
//                alertDialog.setNegativeButton("Tidak",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                // Showing Alert Message
//                alertDialog.show();
//            }
//        });
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] raw = out.toByteArray();
            if (i == 0) {
                encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
            } else if (i == 1) {
                encodedImage2 = Base64.encodeToString(raw, Base64.DEFAULT);
            } else if (i == 2) {
                encodedImage3 = Base64.encodeToString(raw, Base64.DEFAULT);
            } else if (i == 3) {
                encodedImage4 = Base64.encodeToString(raw, Base64.DEFAULT);
            }
            Log.d("hasil gambar", "" + encodedImage + "  " + encodedImage2 + "  " + encodedImage3 + "  " + encodedImage4);
        }
        Log.d("hasil", "a -> " + listView.getCount());

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("setPackageDeliveryStatus");
        parameter.add(session_id);
        parameter.add(Integer.toString(this.request_id));
        parameter.add(this.user_id);
        parameter.add(this.ttk.getText().toString());
        parameter.add(formattedDate);
        parameter.add("1");
        parameter.add("");
        parameter.add(penerima.getText().toString());
        parameter.add(statusPenerimaSpinner.getSelectedItem().toString());
        parameter.add(keterangan.getText().toString());
        parameter.add(encodedImage);
        parameter.add(encodedImage2);
        parameter.add(encodedImage3);
        parameter.add(encodedImage4);
        parameter.add(longi.toString());
        parameter.add(lat.toString());
        progressDialog.setCancelable(false);

        new SoapClient() {
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
                Log.d("Hasil", result + "");
                if (result == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
//                            OfflineTTK off = db.checkTTK(ttk.getText().toString());
//                            String check = off.getTTK();
//                            Log.d("hasil", check + "<<");
//                            if (check != null) {
//                                Toast.makeText(DeliveredAirways.this, "TTK Sudah Terdapat Pada TTK Offline", Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(DeliveredAirways.this, "Anda Tidak Memiliki Koneksi, TTK Anda Tersimpan Secara Offline", Toast.LENGTH_LONG).show();
//                                SubmitOffline();
//                            }

                            Toast.makeText(DeliveredAirways.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();

                        }
                    });
                } else {
                    try {
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            Toast.makeText(DeliveredAirways.this, getString(R.string.update_airway_message), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(DeliveredAirways.this, text, Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DeliveredAirways.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_foto: {
                nomorTTK = ttk.getText().toString();
                if (nomorTTK.equals(null) || nomorTTK.equals("")) {
                    Toast.makeText(DeliveredAirways.this, "mohon maaf ttk harus diisi " + nomorTTK, Toast.LENGTH_LONG).show();
                    break;
                }
                try {
                    isCameraFocused = true;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //define the file-name to save photo taken by Camera activity
                    //edit nama foto untuk ttk
                    String fileName = System.currentTimeMillis() + ".jpg";
                    //create parameters for Intent with filename
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image captured by camera");
                    //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)

                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
                } catch (AndroidRuntimeException e) {
                    Log.d("error", e.getMessage());
                    Integer res;
                    res = 0;
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, res);
                    Log.d("error res", res.toString());

                } catch (Exception e) {
                    Log.d("error", e.getMessage());
                    Integer res;
                    res = 0;
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, res);
                    Log.d("error res", res.toString());

                }
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

        if (this instanceof DeliveredAirways) {

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
                final String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + lat + "," + longi + "&zoom=18&size=600x800&sensor=false&markers=icon:http://sadewa.asia/icon_kurir.png%7C" + lat + "," + longi + "&key=AIzaSyAZwqU0H1Fxb8-hGj6t1fKKW984Qam9UY0";
                final String encodedurl = URLEncoder.encode(url, "UTF-8");
                Log.d("STATICMAPS", encodedurl);

                AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>() {
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
                        if (bmp != null) {

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

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
//                case REQUEST_CODE_FROM_GALLERY:
//                    new ImageCompressionAsyncTask(true).execute(data.getDataString());
//
//                    break;
                case REQUEST_CODE_CLICK_IMAGE:
                    selectedImageUri = imageUri;
                    break;

                case REQUEST_CODE_QRCODE:
                    String nilai = data.getStringExtra("ttk");
                    ttk.setText(nilai);
                    break;
            }
        }
        if (selectedImageUri != null) {
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

                    //CEK META DATA FOTO
//                    ExifInterface exif;
//                    try {
//                        exif = new ExifInterface(filePath);
//                        String LATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
//                        String LATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
//                        String LONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
//                        String LONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
//                        String TANGGAL = exif.getAttribute(ExifInterface.TAG_DATETIME);
//
//                        double lattuj = lat;
//                        double longtuj = longi;
//
//                        Log.d("EXIFrat", "" + LATITUDE + " " + LONGITUDE + " " + LATITUDE_REF + " " + LONGITUDE_REF + " " + TANGGAL);
//
//                        Log.d("EXIFloc", "" + String.valueOf(lattuj) + " | " + String.valueOf(longtuj));
//
//                        Log.d("exif", "" + "coba");
//
//
//                        if ((LATITUDE != null)
//                                && (LATITUDE_REF != null)
//                                && (LONGITUDE != null)
//                                && (LONGITUDE_REF != null) && (TANGGAL != null)) {
//
//                            if (LATITUDE_REF.equals("N")) {
//                                Latitude = convertToDegree(LATITUDE);
//                            } else {
//                                Latitude = 0 - convertToDegree(LATITUDE);
//                            }
//
//                            if (LONGITUDE_REF.equals("E")) {
//                                Longitude = convertToDegree(LONGITUDE);
//                            } else {
//                                Longitude = 0 - convertToDegree(LONGITUDE);
//                            }
//
//
//                            Location locationCurrent = new Location("Lokasi Sekarang");
//                            locationCurrent.setLatitude(lattuj);
//                            locationCurrent.setLongitude(longtuj);
//
//                            Location locationB = new Location("Lokasi Tujuan");
//                            locationB.setLatitude(Latitude);
//                            locationB.setLongitude(Longitude);
//
//                            double distance = locationCurrent.distanceTo(locationB) / 1000;
//                            distance = (double) (Math.round(distance * 100)) / 100;
//
//                            Log.d("exiflat", "" + distance);
//
//                            Calendar c = Calendar.getInstance();
//                            SimpleDateFormat dateTime = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
//                            SimpleDateFormat date = new SimpleDateFormat("yyyy:MM:dd");
//
//                            String formatteddate = date.format(c.getTime());
//
//                            Date today = new Date();
//
//                            Date d2 = null;
//
//                            d2 = dateTime.parse(TANGGAL);
//
//                            long diff = today.getTime() - d2.getTime();
//
//                            long diffSeconds = diff / 1000 % 60;
//                            long diffMinutes = diff / (60 * 1000) % 60;
//                            long diffHours = diff / (60 * 60 * 1000) % 24;
//                            long diffDays = diff / (24 * 60 * 60 * 1000);
//
//                            //sellisih hirungan menit
//                            long totalselisihdetik = ((3600 * 24 * diffDays) + (3600 * diffHours) + (60 * diffMinutes) + diffSeconds) / 60;
//
////                            String selisihwaktu = totalselisihdetik > 10 ? "gagal" : "bisa";
//
//                            String[] part = TANGGAL.split(" ");
//                            String tglimage = part[0];
//
//
////                            double jarak = getjarak(Latitude, Longitude, lattuj, longtuj);
////
////                            Log.d("EXIFjarak", "" + Math.round(jarak) + " " + jarak);
//
//                            if(distance > 1){
//                                Log.d("EXIFstat", "" +"gagal");
//                            }else{
//                                Log.d("EXIFstat", "" +"berhasil");
//                            }
//
//
//                            Log.d("EXIFreal", "" + Latitude + " | " + Longitude);
//
//                            Log.d("EXIFsel", "" + totalselisihdetik + "|" + formatteddate + "|" + tglimage);
//
//
//                            if (!formatteddate.equals(tglimage)) {
//
//                                Toast.makeText(getApplicationContext(), "Tanggal dan Waktu Foto Tidak sesuai",
//                                        Toast.LENGTH_LONG).show();
//                                imageView.setImageBitmap(null);
//                                imageView.setImageDrawable(null);
//
//                                adapterImage.notifyDataSetChanged();
//                                listView.setAdapter(null);
//
//                            } else if (totalselisihdetik > 10) {
//                                Toast.makeText(getApplicationContext(), "Foto di ambil sudah melebihi 10 menit",
//                                        Toast.LENGTH_LONG).show();
//                                imageView.setImageBitmap(null);
//                                imageView.setImageDrawable(null);
//
//                                adapterImage.notifyDataSetChanged();
//                                listView.setAdapter(null);
//                            } else if (distance > 1) {
//                                Toast.makeText(getApplicationContext(), "Location Foto melebihi 1 km",
//                                        Toast.LENGTH_LONG).show();
//                                imageView.setImageBitmap(null);
//                                imageView.setImageDrawable(null);
//
//                                adapterImage.notifyDataSetChanged();
//                                listView.setAdapter(null);
//
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Location:" + String.valueOf(lat) + " | " + String.valueOf(longi),
//                                        Toast.LENGTH_LONG).show();
//
//                                filePath = compressImage(filePath);
//                                database.insertTTKUri(filePath, tipe, nomorTTK);
//                                cursor = database.getallUriTTK(nomorTTK);
//                                adapterImage.changeCursor(cursor);
//                                listView.setAdapter(adapterImage);
//                            }
//
//
//                        } else {
//
//                            Toast.makeText(getApplicationContext(), "Teridentifikasi Foto Palsu",
//                                    Toast.LENGTH_LONG).show();
////                            Log.e("Bitmap", "Unknown path");
//                            imageView.setImageBitmap(null);
//                            imageView.setImageDrawable(null);
//
//                            adapterImage.notifyDataSetChanged();
//                            listView.setAdapter(null);
//
//                        }
//
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                    filePath = compressImage(filePath);
                    database.insertTTKUri(filePath, tipe, nomorTTK);
                    cursor = database.getallUriTTK(nomorTTK);
                    adapterImage.changeCursor(cursor);
                    listView.setAdapter(adapterImage);


                } else {
                    //bitmap = null;
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "" + e,
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
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
        Bitmap bmp = BitmapFactory.decodeFile(ImageUri, options);

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
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(ImageUri, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);


        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


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
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            //imageView.setImageBitmap(scaledBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            //ByteArrayOutputStream bos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            //  bArray = bos.toByteArray();
            Log.d("lala", "" + bArray);


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
        //edit nama foto saat ambil ttk
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZBAR_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
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
                                .setMessage("Are you sure you want to delete image " + obj + "?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(mContext, "berhasil dihapus", Toast.LENGTH_LONG).show();
                                        database.deleteUri(obj.toString());
                                        holder.imageView.setVisibility(View.GONE);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(mContext, "gagal dihapus", Toast.LENGTH_LONG).show();
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

            //ambil bitmap dari memcache, kalo gak ada berarti yang keluar logo wahana
            if (cancelPotentialWork(filePath, imageView)) {
                final Bitmap bitmap = getBitmapFromMemCache(filePath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                    final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), utils.icon, task);
                    imageView.setImageDrawable(asyncDrawable);
                    task.execute(filePath);
                }
            }
        }

        class ViewHolder {
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

        class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
            private final WeakReference<ImageView> imageViewReference;
            public String filePath;

            public BitmapWorkerTask(ImageView imageView) {
                imageViewReference = new WeakReference<ImageView>(imageView);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                filePath = params[0];
                Bitmap bitmap = utils.decodeBitmapFromPath(filePath);
                if (bitmap != null) {
                    addBitmapToMemoryCache(filePath, bitmap);
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (isCancelled()) {
                    bitmap = null;
                }
                if (imageViewReference != null && bitmap != null) {
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

        String ttk1 = ttk.getText().toString();
        String keterangan1 = keterangan.getText().toString();
        String penerima1= penerima.getText().toString();


        // if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        if (ttk1.isEmpty()) {
            ttk.setError("Mohon masukkan Nomor TTK");
            valid = false;
        } else {
            ttk.setError(null);
        }

        if (keterangan1.isEmpty()) {
            keterangan.setError("Mohon Masukkan keterangan");
            valid = false;
        } else {
            keterangan.setError(null);
        }

        if (penerima1.isEmpty()) {
            penerima.setError("Mohon Masukkan Penerima");
            valid = false;
        } else {
            penerima.setError(null);
        }

        if (listView.getCount() == 0) {
            Toast.makeText(DeliveredAirways.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (isInternetAvailable(getApplicationContext())) {
            if (lat == 0.0 || longi == 0.0) {
                Toast.makeText(DeliveredAirways.this, "Lokasi Belum Terdeteksi, Mohon Tunggu Beberapa Saat", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        return valid;
    }

    private void SubmitOffline() {
        try {
            List<String> uris = database.getAllTTKBT(ttk.getText().toString());
            for (int i = 0; i < uris.size(); i++) {
                Log.d("hasil gambar", uris.get(i));
                db.insertTTKUri(uris.get(i), "1", ttk.getText().toString());
            }
        } catch (Exception e) {
            Log.d("Exception", e + "");
        }
        db.addOfflineTTK(new OfflineTTK(ttk.getText().toString(), session_id, Integer.toString(request_id), user_id, formattedDate, "1", "", penerima.getText().toString(),
                statusPenerimaSpinner.getSelectedItem().toString(), keterangan.getText().toString(), lat.toString(), longi.toString(),
                "belum diproses", ""));
        finish();
    }

    public boolean isInternetAvailable(Context c) {

        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

//        Log.d("EXIF",""+result);

        return result;

    }

    ;


    public static double getjarak(double lat1, double lon1, double lat2, double lon2) {

        final int R = 6371; // Radious of the earth

        Double latDistance = toRad(lat2 - lat1);

        Double lonDistance = toRad(lon2 - lon1);

        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +

                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *

                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        Double distance = R * c;

        double distancex = distance;

        return distancex;
    }

    private static Double toRad(Double value) {

        return value * Math.PI / 180;

    }


}
