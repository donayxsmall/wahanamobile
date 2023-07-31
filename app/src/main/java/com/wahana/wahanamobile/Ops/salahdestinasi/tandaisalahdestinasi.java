package com.wahana.wahanamobile.Ops.salahdestinasi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.wahana.wahanamobile.R;
import com.wahana.wahanamobile.adapter.NavDrawerListAdapter;
import com.wahana.wahanamobile.helper.DatabaseHandler;
import com.wahana.wahanamobile.helper.DrawerHelper;
import com.wahana.wahanamobile.helper.SessionManager;
import com.wahana.wahanamobile.webserviceClient.SoapClientMobile;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class tandaisalahdestinasi extends DrawerHelper {
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
    TextView pengisi, tgl, calendar,judul_status_terkirim_isi;
    Button submit;

    String formattedDate,encodedImage = null,encodedImage2 = null,encodedImage3 = null,encodedImage4 = null;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    public NavDrawerListAdapter drawerAdapter;
    TextView keterangan_label, foto_diri_label;
    ImageView iv;
    DatabaseHandler db;
    Spinner alasan;
    EditText keterangan,input_tujuan;
    RelativeLayout layouttujuan1;

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tandairejectxray);
        db = new DatabaseHandler(tandaisalahdestinasi.this);
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

        progressDialog = new ProgressDialog(tandaisalahdestinasi.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(tandaisalahdestinasi.this);
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
        alasan = (Spinner)findViewById(R.id.alasan_retur);
        layouttujuan1=(RelativeLayout)findViewById(R.id.layouttujuan1);

        layouttujuan1.setVisibility(View.VISIBLE);

        keterangan = (EditText) findViewById(R.id.input_keterangan);
        judul_status_terkirim_isi=(TextView)findViewById(R.id.judul_status_terkirim_isi);

        judul_status_terkirim_isi.setText("Tandai Salah Destinasi");

        input_tujuan=(EditText)findViewById(R.id.input_tujuan);

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
//        btnClickImage2 = (Button) findViewById(R.id.btn_submit_foto2);
//        btnClickImage3 = (Button) findViewById(R.id.btn_submit_foto3);
        imageView = (ImageView) findViewById(R.id.listView);
//        imageView2 = (ImageView) findViewById(R.id.listView2);
//        imageView3 = (ImageView) findViewById(R.id.listView3);
        iv = (ImageView) findViewById(R.id.mapView);
        btnClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_CLICK_IMAGE);
            }
        });
//        btnClickImage2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, REQUEST_CODE_CLICK_IMAGE2);
//            }
//        });
//        btnClickImage3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, REQUEST_CODE_CLICK_IMAGE3);
//            }
//        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb=new AlertDialog.Builder(tandaisalahdestinasi.this);
                adb.setTitle("Tandai TTK Salah Destinasi");
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
            json.put("service", "settandaisalahdestinasi");
            json.put("employeeCode", session.getID());
            json.put("tgl", formattedDate);
            json.put("ttk", ttk);
            json.put("keterangan", keterangan.getText().toString());
            json.put("tujuanasli", input_tujuan.getText().toString());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            byte[] raw = out.toByteArray();
            encodedImage = Base64.encodeToString(raw, Base64.DEFAULT);
            json.put("image", String.valueOf(encodedImage));
//            json.put("lat", lat);
//            json.put("longi", longi);
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
                                Toast.makeText(tandaisalahdestinasi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    try{
                        final String text = result.getProperty(1).toString();
                        if (text.equals("OK")) {
                            progressDialog.dismiss();
                            Toast.makeText(tandaisalahdestinasi.this, getString(R.string.update_airway_message), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(tandaisalahdestinasi.this, text, Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(tandaisalahdestinasi.this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
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
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                    imageView.setVisibility(View.VISIBLE);
                    break;

//                case REQUEST_CODE_CLICK_IMAGE2:
//                    Bitmap photo2 = (Bitmap) data.getExtras().get("data");
//                    imageView2.setImageBitmap(photo2);
//                    imageView2.setVisibility(View.VISIBLE);
//                    break;
//
//                case REQUEST_CODE_CLICK_IMAGE3:
//                    Bitmap photo3 = (Bitmap) data.getExtras().get("data");
//                    imageView3.setImageBitmap(photo3);
//                    imageView3.setVisibility(View.VISIBLE);
//                    break;
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        String password = keterangan.getText().toString();
        String inputtujuan1=input_tujuan.getText().toString();


        if (password.isEmpty()) {
            keterangan.setError("Mohon masukkan keterangan");
            valid = false;
        } else {
            keterangan.setError(null);
        }

        if (inputtujuan1.isEmpty()) {
            input_tujuan.setError("Mohon masukkan Tujuan");
            valid = false;
        } else {
            input_tujuan.setError(null);
        }



        if (imageView.getDrawable() == null){
            Toast.makeText(tandaisalahdestinasi.this, "Mohon Masukkan Foto", Toast.LENGTH_SHORT).show();
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
