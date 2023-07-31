package com.wahana.wahanamobile.Ops.penerimaanRetur;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.wahana.wahanamobile.Data.Role;
import com.wahana.wahanamobile.MainActivity;
import com.wahana.wahanamobile.Ops.HasilError;
import com.wahana.wahanamobile.Ops.HasilProses;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.camerates.BitmapTools;
import com.wahana.wahanamobile.camerates.Camerates;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.helper.UnCaughtException;
import com.wahana.wahanamobile.model.NavDrawerItem;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class penerimaanRetur extends DrawerHelper {
    private Button btnClickImage,btnClickImage2,btnClickImage3;
    ImageView imageView,imageView2,imageView3;
    private final int REQUEST_CODE_CLICK_IMAGE = 01;
    private final int REQUEST_CODE_CLICK_IMAGE2 = 02;
    private final int REQUEST_CODE_CLICK_IMAGE3 = 03;
    private static final String TAG = "penerimaanRetur";

    ProgressDialog progressDialog;

    SessionManager session;
    int request_id;
    String username, user_id,session_id,ttk;
    TextView pengisi, tgl, calendar;
    Button submit;

    String alasan,formattedDate,encodedImage = null,encodedImage2 = null,encodedImage3 = null,encodedImage4 = null;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    public NavDrawerListAdapter drawerAdapter;
    TextView keterangan_label, foto_diri_label;
    ImageView iv;
    DatabaseHandler db;
    Uri selectedImageUri = null;


    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penerimaan_retur);


        //get force close
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(penerimaanRetur.this));



        db = new DatabaseHandler(penerimaanRetur.this);
        Intent intent = getIntent();
        ttk = intent.getStringExtra("ttk");
        alasan = intent.getStringExtra("alasan");
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

        progressDialog = new ProgressDialog(penerimaanRetur.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(penerimaanRetur.this);
        username = session.getUsername();
        user_id = session.getID();
        session_id=session.getSessionID();
        Random rand;
        rand = new Random();
        request_id=rand.nextInt(10000);

        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        keterangan_label = (TextView) findViewById(R.id.keterangan_label);
        foto_diri_label = (TextView) findViewById(R.id.foto_diri_label);
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

        Typeface type = Typeface.createFromAsset(getAssets(),"font/OpenSansLight.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        formattedDate=dateTime.format(c.getTime());

        btnClickImage = (Button) findViewById(R.id.btn_submit_foto);
        btnClickImage2 = (Button) findViewById(R.id.btn_submit_foto2);
        btnClickImage3 = (Button) findViewById(R.id.btn_submit_foto3);
        imageView = (ImageView) findViewById(R.id.listView);
        imageView2 = (ImageView) findViewById(R.id.listView2);
        imageView3 = (ImageView) findViewById(R.id.listView3);
        iv = (ImageView) findViewById(R.id.mapView);
        btnClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent(penerimaanRetur.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
            }
        });
        btnClickImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(penerimaanRetur.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE2);
            }
        });
        btnClickImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(penerimaanRetur.this, Camerates.class);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE3);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb=new AlertDialog.Builder(penerimaanRetur.this);
                adb.setTitle("Penerimaan TTK retur");
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

    }

    private void submitForm() {
        if (!validate()) {
            return;
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        JSONObject json = new JSONObject();
        try {
            json.put("service", "setDataRetur");
            json.put("employeeCode", session.getID());
            json.put("tgl", formattedDate);
            json.put("ttk", ttk);
            json.put("alasan", alasan);
            if (imageView.getDrawable() != null){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                byte[] raw = out.toByteArray();
                encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
                json.put("foto", String.valueOf(encodedImage));
                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                Log.e("hasil Original", bitmap.getWidth()+" "+bitmap.getHeight());
                Log.e("hasil Compressed", decoded.getWidth()+" "+decoded.getHeight());
            }
            if (imageView2.getDrawable() != null){
                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                BitmapDrawable drawable2 = (BitmapDrawable) imageView2.getDrawable();
                Bitmap bitmap2 = drawable2.getBitmap();
                bitmap2.compress(Bitmap.CompressFormat.JPEG,100,out2);
                byte[] raw2 = out2.toByteArray();
                encodedImage2 = Base64.encodeToString(raw2, Base64.DEFAULT);
                json.put("foto2", String.valueOf(encodedImage2));
            }
            if(imageView3.getDrawable() != null){
                ByteArrayOutputStream out3 = new ByteArrayOutputStream();
                BitmapDrawable drawable3 = (BitmapDrawable) imageView3.getDrawable();
                Bitmap bitmap3 = drawable3.getBitmap();
                bitmap3.compress(Bitmap.CompressFormat.JPEG,100,out3);
                byte[] raw3 = out3.toByteArray();
                encodedImage3 = Base64.encodeToString(raw3, Base64.DEFAULT);
                json.put("foto3", String.valueOf(encodedImage3));
            }
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
                                Toast.makeText(penerimaanRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    try{
                        final String code = result.getProperty(0).toString();
                        final String text = result.getProperty(1).toString();
                        if (code.equals("1")) {
                            progressDialog.dismiss();
                            String so = result.getProperty(2).toString();
                            JSONObject jsonObj = new JSONObject(so);
                            JSONArray data = jsonObj.getJSONArray("data");
                            Log.d("hasil soap data", ""+data);
                            if (data.length()>1){
                                final JSONArray d = data.getJSONArray(2);
//                                Log.d("hasil soap data", ""+d.getJSONObject(1).getString("status"));
                                if (d.getJSONObject(1).getString("status").equals("1")){
                                    Intent pindah = new Intent(penerimaanRetur.this, HasilProses.class);
                                    pindah.putExtra("proses","retur");
                                    JSONArray msd = data.getJSONArray(1);
                                    String ms = msd.getString(0);
                                    pindah.putExtra("no", ms);
                                    startActivity(pindah);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(penerimaanRetur.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(penerimaanRetur.this, "Mohon Cek Kembali TTK Anda",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            progressDialog.dismiss();
                            Intent pindah = new Intent(penerimaanRetur.this, HasilError.class);
                            pindah.putExtra("proses","retur");
                            pindah.putExtra("no", text);
                            startActivity(pindah);
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(penerimaanRetur.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        }.execute(parameter);

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
  
        if(resultCode == RESULT_OK){

            switch(requestCode){
                case REQUEST_CODE_CLICK_IMAGE:
//                    byte[] byteArray = data.getByteArrayExtra("foto");
//                    Bitmap photo = BitmapTools.toBitmap(byteArray);
//                    photo = BitmapTools.rotate(photo, data.getIntExtra("rotation", 0), data.getIntExtra("camera", 0));
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView.setImageBitmap(bmp);
                    imageView.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_CODE_CLICK_IMAGE2:
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp2 = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView2.setImageBitmap(bmp2);
                    imageView2.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_CODE_CLICK_IMAGE3:
                    selectedImageUri = Uri.parse(data.getStringExtra("foto"));
                    Bitmap bmp3 = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    imageView3.setImageBitmap(bmp3);
                    imageView3.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        if (imageView.getDrawable() == null){
            Toast.makeText(penerimaanRetur.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
            valid = false;
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
