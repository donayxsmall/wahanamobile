package com.wahana.wahanamobile.CustomerRelation;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.wahana.wahanamobile.AttendanceReport;
import com.wahana.wahanamobile.ChangePasswordActivity;
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

public class InputNomorAgenDailyReport extends DrawerHelper implements View.OnClickListener  {
    private static final String TAG = "InputNomorAgenDailyReport";
    ProgressDialog progressDialog;
    SessionManager session;
    int request_id;
    String username, user_id,session_id;
    TextView pengisi, tgl, ttk, calendar;
    Button submit;
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
    EditText id_agen;
    TextView nama,jabatan, id_agen_label, no_ktp_label,no_handphone_label,email_agen_label, penanggung_jawab_label, alamat_agen_label;
    ImageView foto;
    DatabaseHandler db;
    String nomorTTK;
    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_nomor_agen_daily_report);
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
                Intent intent = new Intent(InputNomorAgenDailyReport.this, MainActivity.class);
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
                            Intent pindah = new Intent(InputNomorAgenDailyReport.this, MainActivity.class);
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
                            InputNomorAgenDailyReport.this.startActivity(intent);
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

        progressDialog = new ProgressDialog(InputNomorAgenDailyReport.this, R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(InputNomorAgenDailyReport.this);
        username = session.getUsername();
        user_id = session.getID();
        session_id=session.getSessionID();
        Random rand;
        rand = new Random();
        request_id=rand.nextInt(10000);


        ////////////////////////////////////////////////
        //        FORM SECTION

        tgl = (TextView) findViewById(R.id.tanggal_jam_isi);
        pengisi = (TextView) findViewById(R.id.pengisi_isi);
        id_agen = (EditText) findViewById(R.id.input_id_agen);
        id_agen_label = (TextView) findViewById(R.id.judul_input_agen);
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
        //Typeface typeNama = Typeface.createFromAsset(getAssets(),"font/OpenSansSemibold.ttf");
        calendar.setTypeface(type);
        tgl.setTypeface(type);
        pengisi.setTypeface(type);
        id_agen_label.setTypeface(type);
        formattedDate=dateTime.format(c.getTime());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        startActivity(new Intent(InputNomorAgenDailyReport.this, DailyReportActivity.class));
    }

    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.btn_submit_foto:
//            {
//                nomorTTK = ttk.getText().toString();
//                if (nomorTTK.equals(null)||nomorTTK.equals(""))
//                {
//                    Toast.makeText(BuatAgenActivity.this,"mohon maaf ttk harus diisi "+nomorTTK,Toast.LENGTH_LONG).show();
//                    break;
//                }
//                try {
//                    isCameraFocused = true;
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    //define the file-name to save photo taken by Camera activity
//                    //edit nama foto untuk ttk
//                    String fileName = System.currentTimeMillis()+".jpg";
//                    //create parameters for Intent with filename
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, fileName);
//                    values.put(MediaStore.Images.Media.DESCRIPTION,"Image captured by camera");
//                    //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
//
//                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
////                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                    startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
//                }catch(AndroidRuntimeException e){
//                    Log.d("error",e.getMessage());
//                    Integer res;
//                    res=0;
//                    ActivityCompat.requestPermissions(this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, res);
//                    Log.d("error res",res.toString());
//
//                }catch(Exception e){
//                    Log.d("error",e.getMessage());
//                    Integer res;
//                    res=0;
//                    ActivityCompat.requestPermissions(this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, res);
//                    Log.d("error res",res.toString());
//
//                }
//            }
//            break;
////            case R.id.btn_tambah_foto :
////            {
////                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////                intent.setType("image/*");
////                startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
////            }
////            break;
//        }

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
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }




}
