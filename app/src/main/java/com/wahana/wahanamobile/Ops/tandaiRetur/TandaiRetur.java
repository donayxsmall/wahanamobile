package com.wahana.wahanamobile.Ops.tandaiRetur;

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
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wahana.wahanamobile.BarcodeScannerActivity;
import com.wahana.wahanamobile.Data.OfflineTTK;
import com.wahana.wahanamobile.Data.Origin;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.ReportPageActivity;
import com.wahana.wahanamobile.Ops.STMSRETUR.InputTTKRetur;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.utils.ImageLoadingUtils;
import com.wahana.wahanamobile.utils.ImageUriDatabases;
import com.wahana.wahanamobile.webserviceClient.SoapClient;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.Random;


public class TandaiRetur extends DrawerHelper implements ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener, LocationListener {
    private Button btnClickImage;
    ImageView imageView;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    private static final String TAG = "tandaiRetur";

    ProgressDialog progressDialog;

    SessionManager session;
    int request_id;
    String username, user_id,session_id,ttk;
    TextView pengisi, tgl, calendar;
    Button gambar, submit;
    Double lat = 0.0, longi = 0.0;
    EditText keterangan;

    String formattedDate,encodedImage = null,encodedImage2 = null,encodedImage3 = null,encodedImage4 = null;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    public ArrayList<NavDrawerItem> navDrawerItems;
    public NavDrawerListAdapter drawerAdapter;
    TextView keterangan_label, foto_diri_label;
    ImageView iv;
    DatabaseHandler db;
    private LocationRequest mLocationRequest;
    Spinner alasan;
    String alasanisi,textalasanisi;

    private ArrayList<Origin> returList = new ArrayList<Origin>();
    String spinner_value;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tandai_retur);
        super.onCreateDrawer(this);
        db = new DatabaseHandler(TandaiRetur.this);
        Intent intent = getIntent();
        ttk = intent.getStringExtra("ttk");
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
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

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
                            Intent pindah = new Intent(TandaiRetur.this, MainActivity.class);
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
                            TandaiRetur.this.startActivity(intent);
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

        progressDialog = new ProgressDialog(TandaiRetur.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(TandaiRetur.this);
        username = session.getUsername();
        user_id = session.getID();
        session_id=session.getSessionID();
        Random rand;
        rand = new Random();
        request_id=rand.nextInt(10000);

        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        keterangan = (EditText) findViewById(R.id.input_keterangan);
//        keterangan_label = (TextView) findViewById(R.id.keterangan_label);
        foto_diri_label = (TextView) findViewById(R.id.foto_diri_label);
        gambar = (Button) findViewById(R.id.btn_submit_foto);
        submit = (Button) findViewById(R.id.input_button);
        calendar = (TextView) findViewById(R.id.calendar_isi);

        alasan = (Spinner)findViewById(R.id.alasan_retur);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        formattedDate = df.format(c.getTime());
        String formattedCal = cf.format(c.getTime());
        tgl.setText(formattedDate);
        calendar.setText(formattedCal);
        pengisi.setText(username);

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
//        keterangan.setTypeface(type);
//        keterangan_label.setTypeface(type);
        foto_diri_label.setTypeface(type);
        formattedDate=dateTime.format(c.getTime());
        buildGoogleApiClient();

        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        imageView = (ImageView) findViewById(R.id.listView);
        iv = (ImageView) findViewById(R.id.mapView);
        btnClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_CLICK_IMAGE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb=new AlertDialog.Builder(TandaiRetur.this);
                adb.setTitle("Tandai TTK retur");
                adb.setMessage("Apakah anda yakin ? ");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submitForm();
                    }});
                adb.show();
            }
        });

        submit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));


        getalasanretur();

    }




    private void getalasanretur(){

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("getAlasanRetur");
        parameter.add("0");
        parameter.add("0");
        parameter.add("nik");
        parameter.add("");
        new SoapClientMobile(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
            }

            @Override
            protected void onPostExecute(SoapObject result) {
                super.onPostExecute(result);
                Log.d("hasil soap", ""+result);
                if(result==null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(TandaiRetur.this, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    try{
                        final String code = result.getProperty("resCode").toString();
                        final String text = result.getProperty("resMessage").toString();
                        if (code.equals("1")) {
                            String resData = result.getProperty("resData").toString();

                            JSONObject jsonObj = new JSONObject(resData);
                            JSONArray data = jsonObj.getJSONArray("data");
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(1);
                                if (d.getString(0) != null){
                                    for (int i=0; i<data.length(); i++){
                                        if(i!=0){
                                            JSONArray isi = data.getJSONArray(i);
                                            String kode = isi.getString(0);
                                            String nama = isi.getString(1);
                                            Origin prov = new Origin();
                                            prov.setProvince(kode);
                                            prov.setCity(nama);
                                            prov.setId(""+i);
                                            returList.add(prov);
                                        }else{
                                            Origin prov = new Origin();
                                            prov.setProvince("0");
                                            prov.setCity("--Pilih--");
                                            prov.setId(""+i);
                                            returList.add(prov);
                                        }
                                    }
                                    populateSpinner();
                                }else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(TandaiRetur.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(TandaiRetur.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(TandaiRetur.this, "Mohon periksa kembali data Anda",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.d("hasil soap data", ""+e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(TandaiRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);

    }



    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < returList.size(); i++) {
            if (i==0){
                lables.add(returList.get(i).getCity());
            }else{
                lables.add(returList.get(i).getId()+". "+returList.get(i).getCity());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_list, lables);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        alasan.setAdapter(spinnerAdapter);
    }

    private void submitForm() {
        if (!validate()) {
            return;
        }


        alasanisi = returList.get(alasan.getSelectedItemPosition()).getProvince().toString();

        textalasanisi = returList.get(alasan.getSelectedItemPosition()).getCity().toString();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        JSONObject json = new JSONObject();
        try {
            json.put("service", "setTandaiRetur");
            json.put("employeeCode", session.getID());
            json.put("tgl", formattedDate);
            json.put("ttk", ttk);
            json.put("keterangan", keterangan.getText().toString());
            json.put("idketerangan", alasanisi);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            byte[] raw = out.toByteArray();
            encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
            json.put("foto", String.valueOf(encodedImage));
            json.put("lat", lat);
            json.put("longi", longi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("hasil json", ""+json);

        ArrayList<String> parameter = new ArrayList<String>();
        parameter.add("doSSQL");
        parameter.add(session.getSessionID());
        parameter.add("apiGeneric");
        parameter.add("20");
        parameter.add("0");
        parameter.add("jsonp");
        parameter.add(""+json);
        progressDialog.setCancelable(false);

        new SoapClientMobile(){
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
                if (result == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {progressDialog.dismiss();
                                Toast.makeText(TandaiRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            Toast.makeText(TandaiRetur.this, getString(R.string.update_airway_message), Toast.LENGTH_LONG).show();
                            finish();

                            Intent picture_intent = new Intent(TandaiRetur.this, ReportPageActivity.class);
                            picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=mkTTKRetur;iswv=1;asis=1&user=" + session.getUsername()+"&ttk="+ttk);
                            //picture_intent.putExtra("url", "http://mobile.wahana.com/apps/wahana/cgi-bin/dw.cgi?b=form_crud/fnm.50180000ENTBT_m_TTKP_CS&noframe=1&iswv=1");
                            startActivity(picture_intent);

                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(TandaiRetur.this, text, Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(TandaiRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
        switch(v.getId()){

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
        super.onRestart();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
  
        if(resultCode == RESULT_OK){

            switch(requestCode){
                case REQUEST_CODE_CLICK_IMAGE:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                    imageView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public boolean validate() {
        boolean valid = true;


        if(returList.get(alasan.getSelectedItemPosition()).getProvince().toString().equals("0")){
            Toast.makeText(TandaiRetur.this, "Pilih alasan retur",Toast.LENGTH_SHORT).show();

            return false;
        }

        if (imageView.getDrawable() == null){
            Toast.makeText(TandaiRetur.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (isInternetAvailable(getApplicationContext())){
            if (lat == 0.0 || longi == 0.0){
                Toast.makeText(TandaiRetur.this, "Lokasi Belum Terdeteksi, Mohon Tunggu Beberapa Saat", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        if (keterangan.getText().toString().trim().isEmpty()) {
            keterangan.setError("Masukkan Keterangan");

            return false;
        } else {
            keterangan.setError(null);
        }

        return valid;
    }

    public boolean isInternetAvailable(Context c) {

        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }
}
